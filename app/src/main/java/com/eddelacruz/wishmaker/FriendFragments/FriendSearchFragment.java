package com.eddelacruz.wishmaker.FriendFragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eddelacruz.wishmaker.Adapters.FriendsAdapter;
import com.eddelacruz.wishmaker.Adapters.SearchUsers;
import com.eddelacruz.wishmaker.LoadingFragments.LoadingFragment;
import com.eddelacruz.wishmaker.MainActivity;
import com.eddelacruz.wishmaker.Managers.DataManager;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.Models.Friends;
import com.eddelacruz.wishmaker.Models.Lists;
import com.eddelacruz.wishmaker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

public class FriendSearchFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "FRIENDS SEARCH FRAGMENT";

    private ImageView backArrow;
    private EditText friendSearch;
    private TextView searchIcon, requests;
    private String myUid = DataManager.getInstance().getUserId();
    private Query query;
    private Friends friendRow;
    private ArrayList<Friends> friendList = new ArrayList<>();
    private SearchUsers friendsAdapter;
    private RecyclerView recyclerView;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    boolean noMorePulls = false;
    private String gSearchName = "";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.friends_search_fragment, container, false);
        setUpListeners(root);
        return root;
    }

    private void setUpListeners(View rootView) {
        backArrow = rootView.findViewById(R.id.backArrow);
        searchIcon = rootView.findViewById(R.id.searchIcon);
        friendSearch = rootView.findViewById(R.id.friendSearch);
        requests = rootView.findViewById(R.id.Requests);
        recyclerView = rootView.findViewById(R.id.searchRecyclerView);

        backArrow.setOnClickListener(this);
        searchIcon.setOnClickListener(this);
        requests.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                try {
                    getFragmentManager().popBackStack(TransactionNameAndFragmentTag.FriendsSearchFragment,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.searchIcon:
                if(friendsAdapter != null) {
                    friendList.clear();
                    friendsAdapter.notifyDataSetChanged();
                }
                final String searchName = friendSearch.getText().toString().toLowerCase();
                gSearchName = searchName;
                setUpLoading();
                friendSearch(searchName);
                break;
            case R.id.Requests:
                FriendRequestFragment friendRequestFragment = new FriendRequestFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.add(R.id.root_frame, friendRequestFragment, TransactionNameAndFragmentTag.FriendRequestFragment);
                fragmentTransaction.addToBackStack(TransactionNameAndFragmentTag.FriendRequestFragment);
                fragmentTransaction.commitAllowingStateLoss();
                break;
            default:
                break;
        }
    }

    private void friendSearch(String searchName) {

        query = FirebaseDatabase.getInstance().getReference()
                .child("users").orderByChild("name").equalTo(searchName).limitToFirst(7);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    friendRow = (new Friends(userSnapshot.child("image").getValue(String.class), userSnapshot.child("name").getValue(String.class),  userSnapshot.child("uid").getValue(String.class)));
                    friendList.add(friendRow);
                }
                setupAdapter(friendList);
                removeLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }


    private void anotherPull(String searchName){
        if(noMorePulls) {

        } else {
            try {
                final int listsOldSize = friendList.size() - 1;
                Query AnotherQuery = FirebaseDatabase.getInstance().getReference()
                        .child("users").orderByChild("name").equalTo(searchName).startAt(friendList.get(listsOldSize).getName()).limitToFirst(7);

                AnotherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            friendRow = (new Friends(userSnapshot.child("image").getValue(String.class), userSnapshot.child("name").getValue(String.class),  userSnapshot.child("uid").getValue(String.class)));
                            friendList.add(friendRow);
                        }
                        if (friendList.size() % 7 != 0 || dataSnapshot.getChildrenCount() == 0L) {
                            noMorePulls = true;
                        }

                        if(dataSnapshot.getChildrenCount() != 0L) {
                            friendList.remove(listsOldSize);
                        }

                        Log.e(TAG, "Log this itram range inserted " + String.valueOf(listsOldSize) + " " + String.valueOf(friendList.size() - 1));
                        friendsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            } catch (com.google.firebase.database.DatabaseException e) {
                e.printStackTrace();
                //removeLoading();
            }
        }
    }

    public void setupAdapter(ArrayList<Friends> friends) {
        friendsAdapter = new SearchUsers(this.getContext(), friends);
        recyclerView.setLayoutManager((RecyclerView.LayoutManager) new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        friendsAdapter.setOnItemClickListener(new FriendsAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position, String uid, String name, String url) {
                try {
                    setUpLoading();
                    sendRequest(uid, name, url);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    anotherPull(gSearchName);
                    Log.d("-----","end");

                }
            }
        });
        recyclerView.setAdapter(friendsAdapter);
    }

    private void sendRequest(String uid, String name, String url) {

        final DatabaseReference requests_ref = database.getReference("requests/" + uid);

        requests_ref.push().setValue(new Friends(url, name, myUid)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    try {
                        removeLoading();
                        FancyToast.makeText(getActivity(), "REQUEST SENT !", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        removeLoading();
                        FancyToast.makeText(getActivity(), "ERROR SENDING REQUEST !", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void setUpLoading() {
        try {
            ((MainActivity) getActivity()).hideKeyboard(getActivity());
            LoadingFragment loadingFragment = new LoadingFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.add(R.id.root_frame_second, loadingFragment, TransactionNameAndFragmentTag.LoadingFragment);
            fragmentTransaction.addToBackStack(TransactionNameAndFragmentTag.LoadingFragment);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void removeLoading() {
        try {
            getFragmentManager().popBackStack(TransactionNameAndFragmentTag.LoadingFragment,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}

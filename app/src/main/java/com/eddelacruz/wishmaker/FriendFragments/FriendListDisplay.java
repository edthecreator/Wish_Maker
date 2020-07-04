package com.eddelacruz.wishmaker.FriendFragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eddelacruz.wishmaker.Adapters.ListsAdapter;
import com.eddelacruz.wishmaker.LoadingFragments.LoadingFragment;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.Models.Lists;
import com.eddelacruz.wishmaker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListDisplay extends Fragment implements View.OnClickListener {
    private static String TAG = "FRIENDS LIST DISPLAY";

    private TextView friendName;
    private CircleImageView friend_profile;
    private ImageView backArrow;
    private ListsAdapter listsAdapter;
    private RecyclerView recyclerView;
    private String uid = "";
    private String url = "";
    private String name = "";
    private Query query;
    private Lists lists;
    boolean noMorePulls = false;
    private ArrayList<Lists> listsArrayList = new ArrayList<>();

    public FriendListDisplay() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.friends_list_display_fragment, container, false);
        try {
            Bundle extras = this.getArguments();
            uid = extras.getString("uid", "");
            name = extras.getString("name", "");
            url = extras.getString("url", "");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setUpLoading();
        setUpListeners(root);
        return root;
    }

    private void setUpListeners(View rootview) {
        recyclerView = rootview.findViewById(R.id.listsRecyclerView);
        friend_profile = rootview.findViewById(R.id.friendImage);
        friendName = rootview.findViewById(R.id.friendTextView);
        backArrow = rootview.findViewById(R.id.backArrow);

        backArrow.setOnClickListener(this);

        initializeData();
        firebasecall();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                try {
                    getFragmentManager().popBackStack(TransactionNameAndFragmentTag.FriendsListDisplay,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void initializeData() {
        try {
            friendName.setText(name);
            if( URLUtil.isValidUrl(url)) {
                Glide.with(friend_profile.getContext()).load(Uri.parse(url)).centerCrop().into(friend_profile);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void firebasecall() {

        try {
            query = FirebaseDatabase.getInstance().getReference().child("wishlist")
                    .child(uid).orderByChild("name").limitToFirst(7);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        lists = (new Lists(userSnapshot.child("image").getValue(String.class), userSnapshot.child("name").getValue(String.class), userSnapshot.child("created_at").getValue(Long.class)));
                        listsArrayList.add(lists);
                    }
                    if(listsArrayList.size() < 7) {
                        noMorePulls = true;
                    }
                    setupAdapter(listsArrayList);
                    removeLoading();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } catch (com.google.firebase.database.DatabaseException e) {
            e.printStackTrace();
            removeLoading();
        }

    }

    public void setupAdapter(ArrayList<Lists> list) {
        listsAdapter = new ListsAdapter(this.getContext(), list);
        recyclerView.setLayoutManager((RecyclerView.LayoutManager) new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        listsAdapter.setOnItemClickListener(new ListsAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position, String list_name) {
                try {
                    Bundle data = new Bundle();
                    data.putString("uid", uid);
                    data.putString("name", list_name);
                    data.putString("url", url);
                    GiftFriendsListDisplay giftFriendsListDisplay = new GiftFriendsListDisplay();
                    giftFriendsListDisplay.setArguments(data);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.root_frame, giftFriendsListDisplay, TransactionNameAndFragmentTag.GiftsFriendsListDisplay);
                    fragmentTransaction.addToBackStack(TransactionNameAndFragmentTag.GiftsFriendsListDisplay);
                    fragmentTransaction.commitAllowingStateLoss();
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
                    anotherPull();
                    Log.d("-----","end");

                }
            }
        });
        recyclerView.setAdapter(listsAdapter);
    }

    private void anotherPull(){
        try {
            final int listsOldSize = listsArrayList.size() - 1;
            Query AnotherQuery = FirebaseDatabase.getInstance().getReference().child("wishlist")
                    .child(uid).orderByChild("name").startAt(listsArrayList.get(listsArrayList.size() - 1).getName()).limitToFirst(7);

            AnotherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        lists = (new Lists(userSnapshot.child("image").getValue(String.class), userSnapshot.child("name").getValue(String.class), userSnapshot.child("created_at").getValue(Long.class)));
                        listsArrayList.add(lists);
                    }
                    if(listsArrayList.size() % 7 != 0 || dataSnapshot.getChildrenCount() == 0L) {
                        noMorePulls = true;
                    }

                    if(dataSnapshot.getChildrenCount() != 0L) {
                        listsArrayList.remove(listsOldSize);
                    }

                    listsAdapter.notifyDataSetChanged();
                    //listsAdapter.notifyItemRangeInserted(listsOldSize, listsArrayList.size());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } catch (com.google.firebase.database.DatabaseException e) {
            e.printStackTrace();
            removeLoading();
        }
    }

    private void setUpLoading() {
        try {
            LoadingFragment loadingFragment = new LoadingFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.add(R.id.root_frame, loadingFragment, TransactionNameAndFragmentTag.LoadingFragment);
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

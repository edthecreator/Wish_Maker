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


import com.eddelacruz.wishmaker.Adapters.FriendRequestAdapter;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestFragment extends Fragment implements View.OnClickListener {

    private static String TAG = "FRIENDS REQUEST FRAGMENT";
    private String myuid = "";
    private String url = "";
    private String name = "";
    private ImageView backArrow;
    private RecyclerView recyclerView;
    private Query query;
    private Friends friend;
    private ArrayList<Friends> friendList = new ArrayList<>();
    private FriendRequestAdapter friendRequestAdapter;
    private boolean noMorePulls = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.friend_request_fragment, container, false);
        //setUpLoading();
        myuid = DataManager.getInstance().getUserId();
        Bundle extras = this.getArguments();
        setUpListeners(root);
        return root;
    }

    private void setUpListeners(View rootView) {
       recyclerView = rootView.findViewById(R.id.searchRecyclerView);
        backArrow = rootView.findViewById(R.id.backArrow);

        recyclerView.setOnClickListener(this);
        backArrow.setOnClickListener(this);

        setUpLoading();
        firebaseCall();
    }

    private void firebaseCall() {
        query = FirebaseDatabase.getInstance().getReference()
                .child("requests").child(myuid).orderByChild("name").limitToFirst(7);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    friend = (new Friends(userSnapshot.child("image").getValue(String.class), userSnapshot.child("name").getValue(String.class),  userSnapshot.child("uid").getValue(String.class)));
                    friendList.add(friend);
                }

                if(friendList.size() < 7) {
                    noMorePulls = true;
                }
                setupAdapter(friendList);
                removeLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void setupAdapter(ArrayList<Friends> friends) {
        friendRequestAdapter = new FriendRequestAdapter(this, this.getContext(), friends);
        recyclerView.setLayoutManager((RecyclerView.LayoutManager) new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        friendRequestAdapter.setOnItemClickListener(new FriendRequestAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position, String uid, String name, String url) {
                try {
                    switch (v.getId()) {
                        case R.id.accept:
                            friendRequestAdapter.removeRequest(position);
                            removeRequest(true, uid, name, url);
                            break;
                        case R.id.reject:
                            friendRequestAdapter.removeRequest(position);
                            removeRequest(false, null, null, null);
                            break;
                    }
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
        recyclerView.setAdapter(friendRequestAdapter);
    }

    private void anotherPull(){
        if(noMorePulls) {

        } else {
            try {
                final int listsOldSize = friendList.size() - 1;
                Query AnotherQuery = FirebaseDatabase.getInstance().getReference()
                        .child("requests").child(myuid).orderByChild("name").startAt(friendList.get(listsOldSize).getName()).limitToFirst(7);

                AnotherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            friend = (new Friends(userSnapshot.child("image").getValue(String.class), userSnapshot.child("name").getValue(String.class),  userSnapshot.child("uid").getValue(String.class)));
                            Log.d(TAG, "NEW LISTS" + friend.getName());
                            friendList.add(friend);
                        }
                        if (friendList.size() % 7 != 0 || dataSnapshot.getChildrenCount() == 0L) {
                            noMorePulls = true;
                        }

                        if(dataSnapshot.getChildrenCount() != 0L) {
                            friendList.remove(listsOldSize);
                        }

                        friendRequestAdapter.notifyDataSetChanged();
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

    private void removeRequest(boolean accept, String uid, String name, String url) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference remove_requests = database.getReference("requests/" + myuid);



        remove_requests.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postsnapshot : dataSnapshot.getChildren()) {
                    String key = postsnapshot.getKey();
                    dataSnapshot.getRef().removeValue();

                    FancyToast.makeText(getActivity(), "REQUEST ACCEPTED!", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.getCode();

                FancyToast.makeText(getActivity(), "ERROR !", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
            }
        });

        if(accept) {

            final DatabaseReference requests_ref = database.getReference("friends/" + myuid);
            requests_ref.push().setValue(new Friends(uid, name, url)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        try {
                            FancyToast.makeText(getActivity(), "REQUEST ACCEPTED !", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            FancyToast.makeText(getActivity(), "ERROR ACCEPTING REQUEST !", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        } else {
            FancyToast.makeText(getActivity(), "REQUEST REJECTED !", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                try {
                    getFragmentManager().popBackStack(TransactionNameAndFragmentTag.FriendRequestFragment,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
        }

    }



    private void setUpLoading() {
        try {
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

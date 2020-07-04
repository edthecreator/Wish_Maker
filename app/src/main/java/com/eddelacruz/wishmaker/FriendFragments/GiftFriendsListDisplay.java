package com.eddelacruz.wishmaker.FriendFragments;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eddelacruz.wishmaker.Adapters.WishAdapter;
import com.eddelacruz.wishmaker.LoadingFragments.LoadingFragment;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.Models.Lists;
import com.eddelacruz.wishmaker.Models.Wishes;
import com.eddelacruz.wishmaker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GiftFriendsListDisplay extends Fragment implements View.OnClickListener {
        private static String TAG = "Gifts Friends List Fragment";

        Query query;
        RecyclerView recyclerView;
        ImageView backArrow;
        TextView friendName;
        CircleImageView friendProfile;
        Wishes wishes;
        ArrayList<Wishes> wishesArrayList = new ArrayList<>();
        private WishAdapter wishAdapter;
        private String name = "";
        private String uid = "";
        private String url = "";
        private boolean noMorePulls = false;


        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.gifts_friends_list_display, container, false);
            setUpLoading();
            try {
                Bundle extras = this.getArguments();
                uid = extras.getString("uid", "");
                name = extras.getString("name", "");
                url = extras.getString("url", "");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            setUpListeners(root);
            return root;
        }


        private void setUpListeners(View rootView) {
            recyclerView = rootView.findViewById(R.id.giftsRecyclerView);
            backArrow = rootView.findViewById(R.id.backArrow);
            friendName = rootView.findViewById(R.id.giftfriendTextView);
            friendProfile = rootView.findViewById(R.id.giftfriendImage);

            backArrow.setOnClickListener(this);
            initializeData();
            firebasecall();
        }

        private void initializeData() {
        try {
            friendName.setText(name);
            if( URLUtil.isValidUrl(url)) {
                Glide.with(friendProfile.getContext()).load(Uri.parse(url)).centerCrop().into(friendProfile);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

        private void firebasecall() {
            Log.d(TAG, "MY FRIENDS WISHES QUERY DATA : " + uid + "   " + name);
            query = FirebaseDatabase.getInstance().getReference().child("gifts")
                    .child(uid).orderByChild("created_at").limitToFirst(7);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        wishes = (new Wishes(userSnapshot.child("image").getValue(String.class), userSnapshot.child("name").getValue(String.class), userSnapshot.child("price").getValue(Double.class), userSnapshot.child("link").getValue(String.class), userSnapshot.child("created_at").getValue(Long.class)));
                        wishesArrayList.add(wishes);
                        Log.d(TAG, "MY FRIENDS WISHES : " + wishes);
                    }

                    if(wishesArrayList.size() < 7) {
                        noMorePulls = true;
                    }

                    setupAdapter(wishesArrayList);
                    removeLoading();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.backArrow:
                    try {
                        getFragmentManager().popBackStack(TransactionNameAndFragmentTag.GiftsFriendsListDisplay,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }

        public void setupAdapter(ArrayList<Wishes> wishes) {
            wishAdapter = new WishAdapter(this.getContext(), wishes);
            recyclerView.setLayoutManager((RecyclerView.LayoutManager) new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
            wishAdapter.setOnItemClickListener(new WishAdapter.onRecyclerViewItemClickListener() {
                @Override
                public void onItemClickListener(View v, String name, int position, String link) {
                    Log.d(TAG, "onItemClick position: " + position);
                    if( URLUtil.isValidUrl(link)) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        startActivity(browserIntent);
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
            recyclerView.setAdapter(wishAdapter);
        }

    private void anotherPull(){
        if(noMorePulls) {

        } else {
            try {
                final int listsOldSize = wishesArrayList.size() - 1;
                Query AnotherQuery = FirebaseDatabase.getInstance().getReference().child("wishlist")
                        .child(uid).orderByChild("created_at").startAt(wishesArrayList.get(wishesArrayList.size() - 1).getCreated_At()).limitToFirst(7);

                AnotherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            wishes = (new Wishes(userSnapshot.child("image").getValue(String.class), userSnapshot.child("name").getValue(String.class), userSnapshot.child("price").getValue(Double.class), userSnapshot.child("link").getValue(String.class), userSnapshot.child("created_at").getValue(Long.class)));
                            wishesArrayList.add(wishes);
                        }
                        if (wishesArrayList.size() % 7 != 0 || dataSnapshot.getChildrenCount() == 0L) {
                            noMorePulls = true;
                        }

                        if(dataSnapshot.getChildrenCount() != 0L) {
                            wishesArrayList.remove(listsOldSize);
                        }

                        Log.e(TAG, "Log this itram range inserted " + String.valueOf(listsOldSize) + " " + String.valueOf(wishesArrayList.size() - 1));
                        wishAdapter.notifyDataSetChanged();
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

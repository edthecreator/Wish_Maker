package com.eddelacruz.wishmaker.FriendFragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eddelacruz.wishmaker.Adapters.FriendsAdapter;
import com.eddelacruz.wishmaker.LoadingFragments.LoadingFragment;
import com.eddelacruz.wishmaker.Managers.DataManager;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.Models.Friends;
import com.eddelacruz.wishmaker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "FRIENDS FRAGMENT";

    private Query query;
    private Friends friendRow;
    private ArrayList<Friends> friendList = new ArrayList<>();
    private FriendsAdapter friendsAdapter;
    private RecyclerView recyclerView;
    private ImageView backArrow, addImage;
    private String uid = DataManager.getInstance().getUserId();
   // private DatabaseHelper dbHelper;

    public FriendsFragment() { }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        setUpLoading();
        setUpListeners(root);
        return root;
    }

    private void setUpListeners(View rootView) {
       // dbHelper = new DatabaseHelper(getActivity());
        //final SQLiteDatabase db = dbHelper.getReadableDatabase();

        recyclerView = rootView.findViewById(R.id.friendsRecyclerView);
        backArrow = rootView.findViewById(R.id.backArrow);
        addImage = rootView.findViewById(R.id.addImage);

        addImage.setOnClickListener(this);
        backArrow.setOnClickListener(this);

      //  pullFromSQLite(db);
        firebasecall();
    }

    private void firebasecall() {
        query = FirebaseDatabase.getInstance().getReference()
                .child("friends").child(uid).limitToFirst(7);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    friendRow = (new Friends(userSnapshot.child("image").getValue(String.class), userSnapshot.child("name").getValue(String.class),  userSnapshot.child("uid").getValue(String.class)));

                    /*Glide.with(getContext())
                            .asBitmap()
                            .load(friendRow)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    dbHelper.addUserEntry(friendRow.getName(), friendRow.getUid(), DbBitmapUtility.getBytes(resource));

                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            }); */

                    friendList.add(friendRow);
                }
                try {
                  //  ((MainActivity) getActivity()).setFriends(false);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                setupAdapter(friendList, false);
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
                    getFragmentManager().popBackStack(TransactionNameAndFragmentTag.FriendsFragment,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.addImage:
                try {
                    FriendSearchFragment friendSearchFragment = new FriendSearchFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.root_frame, friendSearchFragment, TransactionNameAndFragmentTag.FriendsSearchFragment);
                    fragmentTransaction.addToBackStack(TransactionNameAndFragmentTag.FriendsSearchFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /*
    private void pullFromSQLite(SQLiteDatabase db) {
        ArrayList<Friends> userList = new ArrayList<>();

        if(((MainActivity) getActivity()).getTracking().getReload_friends().equals("Y")) {
            firebasecall();
        } else {
            String query = "SELECT * FROM user_friends";
            Cursor cursor = db.rawQuery(query, null);

            if(cursor.getCount() != 0) {
                if (cursor.moveToFirst()) {
                    while (cursor.isAfterLast() != true) {

                        String name = cursor.getString(cursor.getColumnIndex("friend_name"));
                        String uid = cursor.getString(cursor.getColumnIndex("friend_uid"));
                        Bitmap bitmap_retrieved = DbBitmapUtility.getImage(cursor.getBlob(cursor.getColumnIndex("friend_data")));

                        Friends user = new Friends();
                        user.setName(name);
                        user.setUid(uid);
                        user.setBitmap_profile(bitmap_retrieved);

                        userList.add(user);
                    }
                }
                setupAdapter(userList, true);
            } else {
                firebasecall();
            }
        }
    }
*/
    public void setupAdapter(ArrayList<Friends> friends, boolean dbPull) {
        friendsAdapter = new FriendsAdapter(this.getContext(), friends);
        recyclerView.setLayoutManager((RecyclerView.LayoutManager) new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        friendsAdapter.setOnItemClickListener(new FriendsAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position, String uid, String name, String url) {
                try {
                    Bundle data = new Bundle();
                    data.putString("uid", uid);
                    data.putString("name", name);
                    data.putString("url", url);
                    Log.d(TAG, "PASSING IN FRIENDS UID:" + uid);
                    FriendsClickedFragment friendsClickedFragment = new FriendsClickedFragment();
                    friendsClickedFragment.setArguments(data);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.root_frame, friendsClickedFragment, TransactionNameAndFragmentTag.FriendsClickedFragment);
                    fragmentTransaction.addToBackStack(TransactionNameAndFragmentTag.FriendsClickedFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        recyclerView.setAdapter(friendsAdapter);
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

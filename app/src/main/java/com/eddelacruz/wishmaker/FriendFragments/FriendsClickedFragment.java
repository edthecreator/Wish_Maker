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

import com.bumptech.glide.Glide;
import com.eddelacruz.wishmaker.LoadingFragments.LoadingFragment;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.Models.User;
import com.eddelacruz.wishmaker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsClickedFragment extends Fragment implements View.OnClickListener  {
    private static String TAG = "FRIENDS CLICKED FRAGMENT";
    private String uid = "";
    private String url = "";
    private String name = "";
    private CircleImageView friendProfile;
    private ImageView backArrow, wishStar;
    private TextView friendName;
    private Query query;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends_clicked, container, false);
        //setUpLoading();
        Bundle extras = this.getArguments();
        uid = extras.getString("uid", "");
        name = extras.getString("name", "");
        url = extras.getString("url", "");
        setUpListeners(root);
        return root;
    }

    private void setUpListeners(View rootView) {
        friendName = rootView.findViewById(R.id.name);
        friendProfile = rootView.findViewById(R.id.profile_image);
        wishStar = rootView.findViewById(R.id.wishStar);
        backArrow = rootView.findViewById(R.id.backArrow);

        friendProfile.setOnClickListener(this);
        friendName.setOnClickListener(this);
        wishStar.setOnClickListener(this);
        backArrow.setOnClickListener(this);

        initializeData();
        //firebaseCall();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                try {
                    getFragmentManager().popBackStack(TransactionNameAndFragmentTag.FriendsClickedFragment,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.wishStar:
                try {
                    Bundle data = new Bundle();
                    data.putString("uid", uid);
                    data.putString("name", name);
                    data.putString("url", url);
                    Log.d(TAG, "PASSING IN FRIENDS UID:" + uid);
                   FriendListDisplay friendListDisplay = new FriendListDisplay();
                   friendListDisplay.setArguments(data);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.root_frame, friendListDisplay, TransactionNameAndFragmentTag.FriendsListDisplay);
                    fragmentTransaction.addToBackStack(TransactionNameAndFragmentTag.FriendsListDisplay);
                    fragmentTransaction.commitAllowingStateLoss();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
        }

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



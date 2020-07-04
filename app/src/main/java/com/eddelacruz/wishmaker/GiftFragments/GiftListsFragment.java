package com.eddelacruz.wishmaker.GiftFragments;

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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eddelacruz.wishmaker.Adapters.WishAdapter;
import com.eddelacruz.wishmaker.AddScreens.AddGifts;
import com.eddelacruz.wishmaker.LoadingFragments.LoadingFragment;
import com.eddelacruz.wishmaker.Managers.DataManager;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.Models.Lists;
import com.eddelacruz.wishmaker.Models.Wishes;
import com.eddelacruz.wishmaker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Locale;

public class GiftListsFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "Gifts List Fragment";

    Query query;
    RecyclerView recyclerView;
    ImageView backArrow, addImage, deleteImage;
    TextView wishesTV, topBar;
    String uid = DataManager.getInstance().getUserId();
    Wishes wishes;
    ArrayList<Wishes> wishesArrayList = new ArrayList<>();
    WishAdapter wishAdapter;
    String name = "";
    boolean regularmode = true;
    boolean noMorePulls = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.gifts_fragment, container, false);
        setUpLoading();
        Bundle extras = this.getArguments();
        name = extras.getString("name", "");
        setUpListeners(root);
        return root;
    }


    private void setUpListeners(View rootView) {
        recyclerView = rootView.findViewById(R.id.giftsRecyclerView);
        backArrow = rootView.findViewById(R.id.backArrow);
        addImage = rootView.findViewById(R.id.addImage);
        deleteImage = rootView.findViewById(R.id.deleteImage);
        topBar = rootView.findViewById(R.id.topBar);
        wishesTV = rootView.findViewById(R.id.wishesTextView);

        addImage.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        deleteImage.setOnClickListener(this);
        firebasecall();
    }
    private void firebasecall() {
        query = FirebaseDatabase.getInstance().getReference().child("gifts")
                .child(uid).child(name).orderByChild("created_at").limitToFirst(7);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    wishes = (new Wishes(userSnapshot.child("image").getValue(String.class), userSnapshot.child("name").getValue(String.class),userSnapshot.child("price").getValue(Double.class), userSnapshot.child("link").getValue(String.class), userSnapshot.child("created_at").getValue(Long.class)));
                    wishesArrayList.add(wishes);
                }

                setupAdapter();
                removeLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    public void addItemtoAdapter(Wishes wishes) {
        wishesArrayList.add(wishes);
        wishAdapter.notifyItemInserted(wishesArrayList.size() - 1);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                if(!regularmode) {
                    regularMode();
                }
                try {
                    getActivity().getSupportFragmentManager().popBackStack(TransactionNameAndFragmentTag.GiftsListFragment,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.addImage:
                if(!regularmode) {
                    regularMode();
                }
                try {
                    Bundle data = new Bundle();
                    data.putString("name", name);
                    AddGifts addGifts = new AddGifts(this);
                    addGifts.setArguments(data);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.root_frame_second, addGifts, TransactionNameAndFragmentTag.AddGiftsFragment);
                    fragmentTransaction.addToBackStack(TransactionNameAndFragmentTag.AddGiftsFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.deleteImage:
                if(regularmode) {
                    deleteMode();
                } else  {
                    regularMode();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!regularmode) {
            regularMode();
        }
    }

    private void deleteMode() {
        //Log.e(TAG, "MODE : DELETE");
        regularmode = false;
        deleteImage.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.delete_white));
        topBar.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
        wishesTV.setText("Delete Mode");
        wishesTV.setTextColor(getActivity().getResources().getColor(R.color.offWhite));
        backArrow.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.back_arrow_white));
        addImage.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_add_white));

    }

    private void regularMode() {
        //Log.e(TAG, "MODE : REGULAR");
        regularmode = true;
        backArrow.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_arrow_back_ios_24px));
        deleteImage.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_delete_24px));
        topBar.setBackgroundColor(getActivity().getResources().getColor(R.color.dark));
        wishesTV.setText("Lists");
        wishesTV.setTextColor(getActivity().getResources().getColor(R.color.blue));
        addImage.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_add_24px));
    }

    public void setupAdapter() {
        wishAdapter = new WishAdapter(this.getContext(), wishesArrayList);
        recyclerView.setLayoutManager((RecyclerView.LayoutManager) new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        wishAdapter.setOnItemClickListener(new WishAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View v, String name, int position, String link) {
                Log.d(TAG, "onItemClick position: " + position);
                if(regularmode) {
                    if (URLUtil.isValidUrl(link)) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        startActivity(browserIntent);
                    }
                } else {
                    wishesArrayList.remove(position);
                    wishAdapter.notifyItemRemoved(position);
                    deleteItemNode(name);
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
                        .child(uid).child(name).orderByChild("created_at").startAt(wishesArrayList.get(wishesArrayList.size() - 1).getCreated_At()).limitToFirst(7);

                AnotherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            wishes = (new Wishes(userSnapshot.child("image").getValue(String.class), userSnapshot.child("name").getValue(String.class),userSnapshot.child("price").getValue(Double.class), userSnapshot.child("link").getValue(String.class), userSnapshot.child("created_at").getValue(Long.class)));
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

    private void deleteItemNode(String giftName) {
        Log.e(TAG, "print name " + name);
        query = FirebaseDatabase.getInstance().getReference().child("gifts")
                .child(uid).child(name).orderByChild("name").equalTo(giftName).limitToFirst(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String stringUrl = userSnapshot.child("image").getValue(String.class);
                    FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
                    StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(stringUrl);
                    photoRef.delete();
                    userSnapshot.getRef().removeValue();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}


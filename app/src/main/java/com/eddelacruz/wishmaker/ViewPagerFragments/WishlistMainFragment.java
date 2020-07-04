package com.eddelacruz.wishmaker.ViewPagerFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.eddelacruz.wishmaker.Adapters.ListsAdapter;
import com.eddelacruz.wishmaker.AddScreens.AddList;
import com.eddelacruz.wishmaker.GiftFragments.GiftListsFragment;
import com.eddelacruz.wishmaker.Managers.DataManager;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.Models.Lists;
import com.eddelacruz.wishmaker.Models.Wishes;
import com.eddelacruz.wishmaker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonSerializationContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag.GiftsListFragment;

public class WishlistMainFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "Wishlist Main Fragment";

    Query query;
    RecyclerView recyclerView;
    ImageView addImage, deleteImage;
    TextView listText, topBar;
    String uid = DataManager.getInstance().getUserId();
    Lists lists;
    ArrayList<Lists> listsArrayList = new ArrayList<>();
    ListsAdapter listsAdapter;
    boolean regularmode = true;
    boolean noMorePulls = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.lists_fragment, container, false);
        Log.d(TAG, "WISH LIST FRAGMENT SET UP");
        setUpListeners(root);
        return root;
    }

    private void setUpListeners(View rootView) {
        recyclerView = rootView.findViewById(R.id.listsRecyclerView);
        addImage = rootView.findViewById(R.id.addImage);
        deleteImage = rootView.findViewById(R.id.deleteImage);
        topBar = rootView.findViewById(R.id.topBar);
        listText = rootView.findViewById(R.id.listsTextView);

        deleteImage.setOnClickListener(this);
        addImage.setOnClickListener(this);
        firebasecall();
    }

    private void firebasecall() {
        query = FirebaseDatabase.getInstance().getReference().child("wishlist")
                .child(uid).orderByChild("created_at").limitToFirst(7);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    lists = (new Lists(userSnapshot.child("image").getValue(String.class), userSnapshot.child("name").getValue(String.class), userSnapshot.child("created_at").getValue(Long.class)));
                    listsArrayList.add(lists);
                }

                if(listsArrayList.size() < 7) {
                    Log.d(TAG, "NO MORE PULLS CALLED" + listsArrayList.size());
                    noMorePulls = true;
                }

                setupAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }


    private void anotherPull(){
        if(noMorePulls) {

        } else {
            try {
                final int listsOldSize = listsArrayList.size() - 1;
                Query AnotherQuery = FirebaseDatabase.getInstance().getReference().child("wishlist")
                        .child(uid).orderByChild("created_at").startAt(listsArrayList.get(listsArrayList.size() - 1).getCreated_at()).limitToFirst(7);

                AnotherQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                lists = (new Lists(userSnapshot.child("image").getValue(String.class), userSnapshot.child("name").getValue(String.class), userSnapshot.child("created_at").getValue(Long.class)));
                                Log.d(TAG, "NEW LISTS" + lists.getName());
                                listsArrayList.add(lists);
                        }
                        if (listsArrayList.size() % 7 != 0 || dataSnapshot.getChildrenCount() == 0L) {
                            noMorePulls = true;
                        }

                        if(dataSnapshot.getChildrenCount() != 0L) {
                            listsArrayList.remove(listsOldSize);
                        }

                        Log.e(TAG, "Log this itram range inserted " + String.valueOf(listsOldSize) + " " + String.valueOf(listsArrayList.size() - 1));
                        listsAdapter.notifyDataSetChanged();
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addImage:
                if(!regularmode) {
                    regularMode();
                }
                try {
                    AddList addList = new AddList(this);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.root_frame_second, addList, TransactionNameAndFragmentTag.AddListFragment);
                    fragmentTransaction.addToBackStack(TransactionNameAndFragmentTag.AddListFragment);
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

    private void deleteMode() {
       //Log.e(TAG, "MODE : DELETE");
        regularmode = false;
        deleteImage.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.delete_white));
        topBar.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
        listText.setText("Delete Mode");
        listText.setTextColor(getActivity().getResources().getColor(R.color.offWhite));
        addImage.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_add_white));

    }

    private void regularMode() {
        //Log.e(TAG, "MODE : REGULAR");
        regularmode = true;
        deleteImage.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_delete_24px));
        topBar.setBackgroundColor(getActivity().getResources().getColor(R.color.dark));
        listText.setText("Lists");
        listText.setTextColor(getActivity().getResources().getColor(R.color.blue));
        addImage.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_add_24px));
    }

    public void setupAdapter() {
        listsAdapter = new ListsAdapter(this.getContext(), listsArrayList);
        recyclerView.setLayoutManager((RecyclerView.LayoutManager) new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        listsAdapter.setOnItemClickListener(new ListsAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position, String list_name) {
                try {
                    if(regularmode) {
                        Bundle data = new Bundle();
                        data.putString("name", list_name);
                        GiftListsFragment giftsListFragment = new GiftListsFragment();
                        giftsListFragment.setArguments(data);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.add(R.id.root_frame_second, giftsListFragment, GiftsListFragment);
                        fragmentTransaction.addToBackStack(GiftsListFragment);
                        fragmentTransaction.commitAllowingStateLoss();
                    } else {
                        listsArrayList.remove(position);
                        listsAdapter.notifyItemRemoved(position);
                        deleteItemNode(list_name);
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
        recyclerView.setAdapter(listsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!regularmode) {
            regularMode();
        }
    }

    public void addItemtoAdapter(Lists newlist) {
        listsArrayList.add(newlist);
        listsAdapter.notifyItemInserted(listsArrayList.size() - 1);
    }


    private void deleteItemNode(String name) {
        Query queryList = FirebaseDatabase.getInstance().getReference().child("wishlist")
                .child(uid).orderByChild("created_at").equalTo(name).limitToFirst(1);

        Query queryGift = FirebaseDatabase.getInstance().getReference().child("gifts")
                .child(uid).child(name);

        queryGift.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                     userSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        queryList.addListenerForSingleValueEvent(new ValueEventListener() {
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

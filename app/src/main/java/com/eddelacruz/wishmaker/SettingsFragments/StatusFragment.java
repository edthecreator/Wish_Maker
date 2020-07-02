package com.eddelacruz.wishmaker.SettingsFragments;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eddelacruz.wishmaker.Adapters.CurrencyAdapter;
import com.eddelacruz.wishmaker.Adapters.FriendsAdapter;
import com.eddelacruz.wishmaker.FriendFragments.FriendsClickedFragment;
import com.eddelacruz.wishmaker.Managers.DataManager;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.Models.Currency;
import com.eddelacruz.wishmaker.Models.User;
import com.eddelacruz.wishmaker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StatusFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "Currency Fragment";

    private ImageView backArrow;
    //private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private ArrayList<Currency> currencyArrayList = new ArrayList<>();
    private CurrencyAdapter currencyAdapter;
    private String uid = DataManager.getInstance().getUserId();
    private Query query;

    public StatusFragment(){ }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.currency_list_fragment, container, false);
        setUpListeners(root);
        return root;
    }

    private void setUpListeners(View rootView) {
        //dbHelper = new DatabaseHelper(this.getContext());
        //db = dbHelper.getReadableDatabase();
        //pullFromSQLite(db);

        backArrow = rootView.findViewById(R.id.backArrow);
        recyclerView = rootView.findViewById(R.id.recyclerViewCurrency);

        currencyArrayList.add(new Currency(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_emoji_emotions_24px), "Excited"));
        currencyArrayList.add(new Currency(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_sentiment_dissatisfied_24px), "Sad"));
        currencyArrayList.add(new Currency(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_sentiment_satisfied_alt_24px), "Happy"));
        setUpAdapter(currencyArrayList);


        backArrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                try {
                    getFragmentManager().popBackStack(TransactionNameAndFragmentTag.StatusFragment,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void firebasePush(boolean stSwitch) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference list_ref = database.getReference("settings/" + uid);
        list_ref.removeValue();
        list_ref.push().setValue(stSwitch);
    }

    private void setUpAdapter (ArrayList<Currency> currencyArrayList) {
        currencyAdapter = new CurrencyAdapter(this.getContext(), currencyArrayList);
        recyclerView.setLayoutManager((RecyclerView.LayoutManager) new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        currencyAdapter.setOnItemClickListener(new CurrencyAdapter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position, final Drawable symbol, final String name) {
                try {

                    DataManager.getInstance().getUser().setstatus(name);
                    currencyAdapter.notifyItemChanged(position);
                   //Log.e(TAG, "KEY" + list_ref.child("").getKey().toString());

                    query = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(uid).limitToFirst(1);

                    Log.d(TAG, "User Query : " + uid);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                Map<String, Object> currency = new HashMap<String, Object>();;
                                currency.put("status", name);

                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final DatabaseReference list_ref = database.getReference("users/" + uid).child(userSnapshot.getKey());

                                list_ref.updateChildren(currency);

                            }
                        }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });




                    getFragmentManager().popBackStack(TransactionNameAndFragmentTag.StatusFragment,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        recyclerView.setAdapter(currencyAdapter);
    }

    /*
    private void pullFromSQLite(SQLiteDatabase db) {
        if(((MainActivity) getActivity()).getTracking().getReload_settings().equals("Y")) {
            firebaseCall();
        } else {
            String query = "SELECT * FROM user_settings";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() != true) {

                    String searchable = cursor.getString(cursor.getColumnIndex("searchable"));

                    if(searchable.equals("Y")) {
                        changeSeachable.setChecked(true);
                        stateOfSwitch = true;
                    } else {
                        changeSeachable.setChecked(false);
                        stateOfSwitch = false;
                    }
                }
            }
        }
    } */


}


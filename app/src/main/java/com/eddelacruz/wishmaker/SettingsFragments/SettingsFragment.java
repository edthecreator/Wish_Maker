package com.eddelacruz.wishmaker.SettingsFragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.eddelacruz.wishmaker.MainActivity;
import com.eddelacruz.wishmaker.Managers.DataManager;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.Models.Settings;
import com.eddelacruz.wishmaker.R;
import com.eddelacruz.wishmaker.ViewPagerFragments.WishlistMainFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "Settings Fragment";

    private ImageView backArrow;
    private TextView Save, changeCurrency, logout;
    private Switch changeSeachable;
    private boolean stateOfSwitch = false;
    //private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Query query;
    private String uid = DataManager.getInstance().getUserId();

    public SettingsFragment(){ }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.settings_fragment, container, false);
        setUpListeners(root);
        return root;
    }

    private void setUpListeners(View rootView) {
        //dbHelper = new DatabaseHelper(this.getContext());
        //db = dbHelper.getReadableDatabase();
        //pullFromSQLite(db);

        firebaseCall();

        backArrow = rootView.findViewById(R.id.backArrow);
        Save = rootView.findViewById(R.id.SaveTV);
        changeSeachable = rootView.findViewById(R.id.changeSearchable);
        changeCurrency = rootView.findViewById(R.id.changeCurrency);
        logout = rootView.findViewById(R.id.Logout);

        changeCurrency.setOnClickListener(this);
        logout.setOnClickListener(this);
        changeSeachable.setOnClickListener(this);
        Save.setOnClickListener(this);
        backArrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                try {
                    getFragmentManager().popBackStack(TransactionNameAndFragmentTag.SettingsFragment,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.SaveTV:
                    if(changeSeachable.isChecked() && !stateOfSwitch) {
                       // ((MainActivity) getActivity()).setSettings(true);
                        firebasePush(true);
                    } else if(!changeSeachable.isChecked()  && stateOfSwitch) {
                        //((MainActivity) getActivity()).setSettings(true);
                        firebasePush(false);
                    } else {
                        firebasePush(stateOfSwitch);
                        //((MainActivity) getActivity()).setSettings(false);
                    }

                break;
            case R.id.changeCurrency:
                try {
                    StatusFragment StatusFragment = new StatusFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.root_frame, StatusFragment, TransactionNameAndFragmentTag.StatusFragment);
                    fragmentTransaction.addToBackStack(TransactionNameAndFragmentTag.StatusFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.Logout:
                try {
                    ((MainActivity) getActivity()).signOut();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void firebasePush(final Boolean stSwitch) {
        final String uid = DataManager.getInstance().getUserId();
        query = FirebaseDatabase.getInstance().getReference()
                .child("settings").child(uid).limitToFirst(1);

        Log.d(TAG, "User Query : " + uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> image = new HashMap<String, Object>();;
                    image.put("searchable", stSwitch);

                    Log.d(TAG, "User Query : " + userSnapshot.getKey());

                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference list_ref = database.getReference("settings").child(uid).child(userSnapshot.getKey());

                    list_ref.updateChildren(image);
                try {
                    getFragmentManager().popBackStack(TransactionNameAndFragmentTag.SettingsFragment,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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

    private void firebaseCall() {
        query = FirebaseDatabase.getInstance().getReference()
                .child("settings").child(uid).limitToFirst(1);

        Log.d(TAG, "User Query : " + uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    stateOfSwitch = (userSnapshot.child("searchable").getValue(Boolean.class));
                    if(stateOfSwitch) {
                        changeSeachable.setChecked(true);
                    } else {
                        changeSeachable.setChecked(false);
                    }
                   // dbHelper.addSettingsEntry(searchable);
                    try {
                      //  ((MainActivity) getActivity()).setSettings(false);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}

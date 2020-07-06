package com.eddelacruz.wishmaker.SettingsFragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.eddelacruz.wishmaker.Models.Friends;
import com.eddelacruz.wishmaker.Models.NewUser;
import com.eddelacruz.wishmaker.Models.Settings;
import com.eddelacruz.wishmaker.Models.User;
import com.eddelacruz.wishmaker.R;
import com.eddelacruz.wishmaker.ViewPagerFragments.WishlistMainFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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


        firebaseCall();
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
                try {
                    Long t4Check = System.currentTimeMillis() - DataManager.getInstance().getSettings().getLast_date_changed();
                    if(changeSeachable.isChecked() && !stateOfSwitch) {

                        // ((MainActivity) getActivity()).setSettings(true);
                        if(t4Check > TimeUnit.HOURS.toMillis(24)) {
                            firebasePush(true);
                            stateOfSwitch = true;
                        } else {
                            FancyToast.makeText(this.getContext(), "Searchability changes are allowed once every 24 hours", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                        }
                    } else if(!changeSeachable.isChecked()  && stateOfSwitch) {
                        //((MainActivity) getActivity()).setSettings(true);
                        if(t4Check > TimeUnit.HOURS.toMillis(24)) {
                            firebasePush(false);
                            stateOfSwitch = false;
                        } else {
                            FancyToast.makeText(this.getContext(), "Searchability changes are allowed once every 24 hours", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                        }
                    } else {
                        //firebasePush(stateOfSwitch);
                        //((MainActivity) getActivity()).setSettings(false);
                    }

                } catch (NullPointerException e) {

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
                for (final DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> image = new HashMap<String, Object>();
                    Long currentDate = System.currentTimeMillis();
                    image.put("searchable", stSwitch);
                    image.put("last_date_changed", currentDate);
                    DataManager.getInstance().getSettings().setSearchable(stSwitch);


                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference list_ref = database.getReference("settings").child(uid).child(userSnapshot.getKey());

                    list_ref.updateChildren(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });

                    if(stSwitch) {
                        userSwitch("private_users", "users");
                    }
                    else {
                        userSwitch("users", "private_users");
                    }
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

    private void userSwitch(final String remove, final String add) {
        Query query2 = FirebaseDatabase.getInstance().getReference()
                .child(remove).child(uid).limitToFirst(1);

        Log.d(TAG, "User Query : " + uid);

            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        final NewUser user2 = (new NewUser(userSnapshot.child("email").getValue(String.class), userSnapshot.child("image").getValue(String.class), userSnapshot.child("firstName").getValue(String.class), userSnapshot.child("lastName").getValue(String.class), userSnapshot.child("uid").getValue(String.class), userSnapshot.child("status").getValue(String.class), userSnapshot.child("date").getValue(Long.class), userSnapshot.child("fullName").getValue(String.class)));
                        Log.d(TAG, "User initialized with " + user2.getFirstName() + " imagelink : ");
                        dataSnapshot.getRef().removeValue();



                        final DatabaseReference requests_ref = FirebaseDatabase.getInstance().getReference().child(add).child(uid);
                        requests_ref.push().setValue(user2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

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
        if(DataManager.getInstance().getSettings() != null) {
            stateOfSwitch = DataManager.getInstance().getSettings().getSearchable();
            if (stateOfSwitch) {
                changeSeachable.setChecked(true);
            } else {
                changeSeachable.setChecked(false);
            }
        } else {
            query = FirebaseDatabase.getInstance().getReference()
                    .child("settings").child(uid).limitToFirst(1);

            Log.d(TAG, "User Query : " + uid);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        stateOfSwitch = (userSnapshot.child("searchable").getValue(Boolean.class));
                        if (stateOfSwitch) {
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

}

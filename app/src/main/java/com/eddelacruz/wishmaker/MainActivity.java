package com.eddelacruz.wishmaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.eddelacruz.wishmaker.Adapters.CustomViewPager;
import com.eddelacruz.wishmaker.Adapters.MyFragmentPagerAdapter;
import com.eddelacruz.wishmaker.AuthenticationActivities.Login;
import com.eddelacruz.wishmaker.Managers.DataManager;
import com.eddelacruz.wishmaker.Models.Tracking;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;
import static androidx.viewpager.widget.PagerAdapter.POSITION_UNCHANGED;


public class MainActivity extends AppCompatActivity {
    private static String TAG = "MAIN ACTIVITY";
    public final int GET_FROM_GALLERY = 3;
    public final int UPLOAD_LIST_IMAGE = 6;
    public final int UPLOAD_GIFT_IMAGE = 9;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private String last_date;
    private String reload_friends;
    private String reload_settings;
    private String reload_profile;

    //private DatabaseHelper dbHelper;
    //private Tracking tracking = new Tracking("i8NtwVZJKSW0jRMYMOxLTDvEfzs1", 0L, "Y", "Y", "Y");
    private SQLiteDatabase db;
    private Query query;
    private BottomNavigationView navView;
    private MyFragmentPagerAdapter adapter;
    private Bitmap imagelink;

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference user_ref = database.getReference("users/");
    private String UserId = "i8NtwVZJKSW0jRMYMOxLTDvEfzs1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //navView.setItemIconTintList(ColorStateList.valueOf(Color.BLUE));
        //navView.setItemTextColor(ColorStateList.valueOf(Color.BLUE));



        navView = findViewById(R.id.nav_view);
        viewPager = findViewById(R.id.viewpager);
        viewPagerSetup();
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        DataManager.getInstance().setUserId(auth.getUid());

        //dbHelper = new DatabaseHelper(this);
        //db = dbHelper.getReadableDatabase();
        //pullFromSQLite(db);
        firebaseCall();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }
            }
        };

    }

    private void viewPagerSetup() {

        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());

        viewPager.storeAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

        Log.d(TAG, "VIEW PAGER INITIALIZATION");
        /* Creating ViewPagerAdapter */
        navView.setItemIconTintList(null);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        viewPager.setCurrentItem(0);
                        item.setChecked(true);
                        //adapter.getItem(0);

                        break;
                    case R.id.navigation_wishlist:
                        viewPager.setCurrentItem(1);
                        item.setChecked(true);
                        //adapter.getItem(0);
                        break;

                }

                return true;
            }
        });


        /* Adding new fragments */
        //adapter.add(new FirstTabRootFragment(), TransactionNameAndFragmentTag.RootFragmentHome);
        //adapter.addFragment(new SecondTabRootFragment(), TransactionNameAndFragmentTag.RootFragmentWishlist);
        /*
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setAdapter(adapter);
            }
        }); */

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("TAG", "IN ONACTIVITYRESULT" + " " + GET_FROM_GALLERY + " " + requestCode + " " + resultCode + " " + Activity.RESULT_OK);
        super.onActivityResult(requestCode, resultCode, data);

        /*
        if(requestCode== GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
                try {
                    Log.d(TAG, "SEND IMAGE TO FB");
                    Uri selectedImage = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    imagelink = bitmap;
                    //uploadFile(bitmap);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

         */
        }



    private void addImageNode(final Uri uri) {
        final String uid = DataManager.getInstance().getUserId();
        query = FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid).limitToFirst(1);

        Log.d(TAG, "User Query : " + uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> image = new HashMap<String, Object>();;
                    image.put("image", uri.toString());

                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference list_ref = database.getReference("users/" + uid).child(userSnapshot.getKey());

                    list_ref.updateChildren(image);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof TextInputEditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private void firebaseCall() {
        query = FirebaseDatabase.getInstance().getReference()
                .child("tracking").child(UserId).limitToFirst(1);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "VIEW PAGER SETTING UP DATA PULL");
                    //tracking = (new Tracking(userSnapshot.child("uid").getValue(String.class), userSnapshot.child("last_date").getValue(Long.class), userSnapshot.child("reload_friends").getValue(String.class), userSnapshot.child("reload_settings").getValue(String.class), userSnapshot.child("reload_profile").getValue(String.class)));
                    //dbHelper.addDateEntry(String.valueOf(tracking.getLast_date()), tracking.getReload_friends(), tracking.getReload_settings(), tracking.getReload_profile());

                    Log.d(TAG, "VIEW PAGER CALL QUERY");
                    //viewPagerSetup();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    /*
    private void pullFromSQLite(SQLiteDatabase db) {
        String query = "SELECT * FROM tracking";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                while (cursor.isAfterLast() != true) {

                    last_date = cursor.getString(cursor.getColumnIndex("last_date"));
                    reload_friends = cursor.getString(cursor.getColumnIndex("reload_friends"));
                    reload_settings = cursor.getString(cursor.getColumnIndex("reload_settings"));
                    reload_profile = cursor.getString(cursor.getColumnIndex("reload_profile"));

                    Log.d(TAG, "VIEW PAGER SETTING UP TRACKING " + last_date + " " + reload_friends + " " + reload_settings);
                    tracking = new Tracking(UserId, Long.decode(last_date), reload_friends, reload_settings, reload_profile);
                    cursor.moveToNext();
                }
                Log.d(TAG, "VIEW PAGER SQL LITE");
               // viewPagerSetup();
            }
        } else {
            firebaseCall();
        }
    }
*/

    /*
    public Tracking getTracking() {
        return tracking;
    }

    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
    }

    public void setProfile(boolean doPull) {
        if(doPull) {
            this.tracking.setReload_profile("Y");
            sqlLiteMagic("reload_profile", "Y");
        }
        else {
            this.tracking.setReload_profile("N");
            sqlLiteMagic("reload_profile", "N");
        }
    }

    public void setFriends(boolean doPull) {
        if(doPull) {
            this.tracking.setReload_friends("Y");
            sqlLiteMagic("reload_friends", "Y");
        }
        else {
            this.tracking.setReload_friends("N");
            sqlLiteMagic("reload_friends", "N");
        }
    }

    public void setSettings(boolean doPull) {
        if(doPull) {
             this.tracking.setReload_settings("Y");
            sqlLiteMagic("reload_settings", "Y");
        }
        else {
            this.tracking.setReload_settings("N");
            sqlLiteMagic("reload_settings", "N");
        }
    }

    private void sqlLiteMagic(String ColumnName, String message) {
        dbHelper.alterExisitingColumn(db, ColumnName, message);
    }
 */

    //sign out method
    public void signOut() {
        auth.signOut();
        DataManager.getInstance().setUser(null);
        DataManager.getInstance().setSettings(null);
        DataManager.getInstance().setUserId(null);
    }


    @Override
    protected void onResume() {

        super.onResume();
        /*viewPager.post(new Runnable() {
            @Override
            public void run() {
                if(adapter != null && viewPager != null) {
                    //viewPager.setAdapter(null);
                    viewPager.setAdapter(adapter);
                    viewPager.getAdapter().notifyDataSetChanged();
                }
            }
        }); */

    }
        /*
       viewPager.post(new Runnable() {
            @Override
            public void run() {
                if(adapter != null && viewPager != null) {
                    //viewPager.setAdapter(null);
                    viewPager.setAdapter(adapter);
                    viewPager.getAdapter().notifyDataSetChanged();
                }
            }
        }); */


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onStart() {
        Log.d(TAG, "ON START CALLED");
        super.onStart();
        auth.addAuthStateListener(authListener);
    }



    @Override
    public void onStop() {
        Log.d(TAG, "ON STOP CALLED");
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }



}

package com.eddelacruz.wishmaker.ViewPagerFragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.GLDebugHelper;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eddelacruz.wishmaker.FriendFragments.FriendsFragment;
import com.eddelacruz.wishmaker.MainActivity;
import com.eddelacruz.wishmaker.Managers.DataManager;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.Models.Settings;
import com.eddelacruz.wishmaker.Models.User;
import com.eddelacruz.wishmaker.R;
import com.eddelacruz.wishmaker.RootFragments.FirstTabRootFragment;
import com.eddelacruz.wishmaker.SettingsFragments.SettingsFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "HOME FRAGMENT";

    private final int GET_FROM_GALLERY = 3;
    private ImageView friends, settings, statusIcon;
    private TextView name, logout;
    private CircleImageView profile;
    private Button upload_image;
    private Bitmap bitmap;
    private String uid = DataManager.getInstance().getUserId();
    private Query query;
    private User user;
    private ArrayList<User> listUser = new ArrayList<>();
    //private DatabaseHelper dbHelper;
    //private SQLiteDatabase db;


    public HomeFragment() { }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d(TAG, "HOME FRAGMENT SET UP");
        setUpListeners(root);
        return root;
    }

    private void setUpListeners(View rootView) {
        //dbHelper = new DatabaseHelper(getActivity());
        //db = dbHelper.getReadableDatabase();

        friends = rootView.findViewById(R.id.friends);
        settings = rootView.findViewById(R.id.settings);
        profile = rootView.findViewById(R.id.profile_image);
        upload_image = rootView.findViewById(R.id.upload_image);
        name = rootView.findViewById(R.id.name);
        statusIcon = rootView.findViewById(R.id.statusIcon);
        //logout = rootView.findViewById(R.id.logout);

        //        logout.setOnClickListener(this);
        name.setOnClickListener(this);
        friends.setOnClickListener(this);
        settings.setOnClickListener(this);
        profile.setOnClickListener(this);
        upload_image.setOnClickListener(this);

        //pullFromSQLite(db);
        initialSettingsCall();
        //firebaseCall();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friends:
                try {
                    FriendsFragment friendsFragment = new FriendsFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.root_frame, friendsFragment, TransactionNameAndFragmentTag.FriendsFragment);
                    fragmentTransaction.addToBackStack(TransactionNameAndFragmentTag.FriendsFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.settings:

                    try {
                    SettingsFragment settingsFragment = new SettingsFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.root_frame, settingsFragment, TransactionNameAndFragmentTag.SettingsFragment);
                    fragmentTransaction.addToBackStack(TransactionNameAndFragmentTag.SettingsFragment);
                    fragmentTransaction.commitAllowingStateLoss();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.profile_image:
            case R.id.upload_image:
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                break;
            /*case R.id.logout:
                logout();
                break;*/
        }
    }

    private void logout() {
        try {
            ((MainActivity) getActivity()).signOut();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "CHECK IMAGE LINK 1");
        try {
            //Log.d(TAG, "CHECK IMAGE LINK" + ((MainActivity) getActivity()).getImagelink().toString());
            Uri retrieve = data.getData();
            addImageNode(retrieve);
            //Bitmap selectedImage = ((MainActivity) getActivity()).getImagelink();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), retrieve);
                uploadFile(bitmap);
            Glide.with(profile.getContext()).load(retrieve).centerCrop().into(profile);

            saveProfilePic(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }



    private void saveProfilePic(Bitmap bitmap) {
        /*final byte[] deliveryBoy = DbBitmapUtility.getBytes(bitmap);

        dbHelper.alterExisitingBlob(db, "user_data", "image_data", deliveryBoy);
        try {
            ((MainActivity) getActivity()).setProfile(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } */
    }

    /*
    private void pullFromSQLite(SQLiteDatabase db) {
        if(((MainActivity)getActivity()).getTracking().getReload_profile().equals("Y")) {
            firebaseCall();
        } else {
            String query = "SELECT * FROM user_data";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.getCount() != 0) {
                if (cursor.moveToFirst()) {
                    while (cursor.isAfterLast() != true) {

                        String name = cursor.getString(cursor.getColumnIndex("user_name"));
                        String uid = cursor.getString(cursor.getColumnIndex("user_uid"));
                        Bitmap bitmap_retrieved = DbBitmapUtility.getImage(cursor.getBlob(cursor.getColumnIndex("image_data")));

                        User user = new User();
                        user.setName(name);
                        user.setUid(uid);
                        user.setBitmap_profile(bitmap_retrieved);

                        initializeData(user, true);
                    }
                }
            } else {
                firebaseCall();
            }
        }
    } */

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "ON RESUME HOME");
        if(DataManager.getInstance().getUser() != null)
        setStatus();
    }

    private void setStatus() {
        try {
            String status = DataManager.getInstance().getUser().getstatus();
            if (status.equals("Happy"))
                statusIcon.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_sentiment_satisfied_alt_24px_1));
            else if (status.equals("Excited"))
                statusIcon.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_sentiment_very_satisfied_24px_1));
            else if (status.equals("Sad"))
                statusIcon.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_sentiment_dissatisfied_24px_1));
            else if (status.equals("Lazy"))
                statusIcon.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.ic_sentiment_very_dissatisfied_24px));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void firebaseCall(String que) {

            query = FirebaseDatabase.getInstance().getReference()
                    .child(que).child(uid).limitToFirst(1);

            Log.d(TAG, "User Query : " + uid);

            if(DataManager.getInstance().getUser() != null) {
                initializeData(DataManager.getInstance().getUser(), false);
            }
            else {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            final User user2 = (new User(userSnapshot.child("image").getValue(String.class), userSnapshot.child("firstName").getValue(String.class), userSnapshot.child("lastName").getValue(String.class), userSnapshot.child("uid").getValue(String.class), userSnapshot.child("status").getValue(String.class)));
                            Log.d(TAG, "User initialized with " + user2.getFirstName() + " imagelink : ");

                            DataManager.getInstance().setUser(user2);

                            setStatus();
                            initializeData(user2, false);
                        /*
                        Glide.with(getContext())
                                .asBitmap()
                                .load(user.getImage())
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                       // dbHelper.addUserEntry(user.getName(), user.getUid(), DbBitmapUtility.getBytes(resource));

                                        try {
                                            ((MainActivity) getActivity()).setProfile(false);
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                }); */
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        }

    private void initialSettingsCall() {
        query = FirebaseDatabase.getInstance().getReference()
                .child("settings").child(uid).limitToFirst(1);

        Log.d(TAG, "User Query : " + uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Settings newSettings = new Settings(userSnapshot.child("searchable").getValue(Boolean.class), userSnapshot.child("last_date_changed").getValue(Long.class));
                        DataManager.getInstance().setSettings(newSettings);

                        if(newSettings.getSearchable()) {
                            firebaseCall("users");
                        } else {
                            firebaseCall("private_users");
                        }
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

    private void initializeData(User user, boolean dbPull) {

            try {
                Log.d(TAG, "CHECK IMAGE" + user.getImage());
                String output = user.getFirstName().substring(0, 1).toUpperCase() + user.getFirstName().substring(1);
                name.setText(output);

                if(user.getImage() != null && !user.getImage().equals("")) {
                    if (dbPull) {
                        Glide.with(profile.getContext()).load(user.getBitmap_profile()).centerCrop().into(profile);
                    } else {
                        Log.d(TAG, "CHECK IMAGE" + user.getImage());
                        Glide.with(profile.getContext()).load(user.getImage()).centerCrop().into(profile);
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

    private void uploadFile(Bitmap bitmap) {
        String myFolder = DataManager.getInstance().getUserId();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://wish-maker-b5fe1.appspot.com/");
        final StorageReference profileImagesRef = storageRef.child("profileimages/" + myFolder + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = profileImagesRef.putBytes(data);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "FAILURE AT BEGINNING OF UPLOAD TASK");
                    Log.d(TAG, "SEND IMAGE TO FB FAILURE");
                    FancyToast.makeText(getActivity(),"FAILURE UPLOADING IMAGE !",FancyToast.LENGTH_LONG,FancyToast.ERROR,true);
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return profileImagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "SEND IMAGE TO FB STEP 2");
                    Uri downloadUri = task.getResult();
                    //imagelink = downloadUri;
                    addImageNode(downloadUri);
                    try {
                         FancyToast.makeText(getActivity(),"IMAGE UPLOADED !",FancyToast.LENGTH_LONG, FancyToast.SUCCESS,true);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "FAILURE AT COMPLETING TASK");
                }
            }
        });
    }

    private void addImageNode(final Uri uri) {
        final String uid = DataManager.getInstance().getUserId();

        if(DataManager.getInstance().getSettings().getSearchable()) {
            query = FirebaseDatabase.getInstance().getReference()
                    .child("users").child(uid).limitToFirst(1);
        } else {
            query = FirebaseDatabase.getInstance().getReference()
                    .child("private_users").child(uid).limitToFirst(1);
        }

        Log.d(TAG, "User Query : " + uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> image = new HashMap<String, Object>();;
                    image.put("image", uri.toString());


                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference list_ref;

                    if(DataManager.getInstance().getSettings().getSearchable()) {
                        list_ref = database.getReference("users/" + uid).child(userSnapshot.getKey());
                        list_ref.updateChildren(image);
                    } else {
                        list_ref = database.getReference("private_users/" + uid).child(userSnapshot.getKey());
                        list_ref.updateChildren(image);
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}

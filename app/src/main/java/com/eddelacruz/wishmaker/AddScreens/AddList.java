package com.eddelacruz.wishmaker.AddScreens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.eddelacruz.wishmaker.MainActivity;
import com.eddelacruz.wishmaker.Managers.DataManager;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.Models.Lists;
import com.eddelacruz.wishmaker.R;
import com.eddelacruz.wishmaker.ViewPagerFragments.WishlistMainFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class AddList extends Fragment implements View.OnClickListener {
    private static String TAG = "ADD LISTS SCREEN";

    private Button saveButton, cancelButton;
    private TextInputEditText listname;
    private ImageView cameraIcon, listimage;
    private Bitmap bitmap = null;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String uid = DataManager.getInstance().getUserId();
    private final DatabaseReference list_ref = database.getReference("wishlist/" + uid);
    private WishlistMainFragment wishlistMainFragment;

    public AddList() { }

    public AddList(WishlistMainFragment wishlistMainFragment) {
        this.wishlistMainFragment = wishlistMainFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_list, container, false);
        setUpListeners(root);
        return root;
    }

    private void setUpListeners(View rootView) {
        saveButton = rootView.findViewById(R.id.save);
        cancelButton = rootView.findViewById(R.id.cancel);
        listname = rootView.findViewById(R.id.listname);
        cameraIcon = rootView.findViewById(R.id.cameraIcon);
        listimage = rootView.findViewById(R.id.listimage);

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        listimage.setOnClickListener(this);
        cameraIcon.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listimage:
            case R.id.cameraIcon:
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), ((MainActivity) getActivity()).UPLOAD_LIST_IMAGE);
                break;
            case R.id.save:
                try {
                if(listname.getText() != null && !listname.getText().toString().equals("")) {
                    String uploadName = String.valueOf(listname.getText());
                    uploadFile(bitmap, uploadName);
                } else {
                    try {
                        FancyToast.makeText(getActivity(),"Please Input Name !",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.cancel:
                try {
                    getFragmentManager().popBackStack(TransactionNameAndFragmentTag.AddListFragment,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e(TAG, "IN ONACTIVITYRESULT" + " " + " " + requestCode + " " + resultCode + " " + Activity.RESULT_OK);
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri selectedImage = data.getData();
            cameraIcon.setVisibility(View.INVISIBLE);

            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
            Glide.with(listimage.getContext()).load(bitmap).centerCrop().into(listimage);
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

    private void saveNewItem() {

    }

    private void uploadFile(Bitmap bitmap, final String name) {
        String myFolder = DataManager.getInstance().getUserId();
        if (bitmap != null) {
            try {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://wish-maker-b5fe1.appspot.com/");
                final StorageReference listImageRef = storageRef.child("listimages/" + myFolder + "_" + name + ".jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = listImageRef.putBytes(data);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "FALURE AT BEGINNING OF UPLOAD TASK");
                            FancyToast.makeText(getActivity(),"FAILURE UPLOADING IMAGE !",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                            throw task.getException();
                        }

                        return listImageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            createNewList(downloadUri, name);
                        } else {
                            FancyToast.makeText(getActivity(),"FAILURE COMPLETING TASK !",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                        }
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            createNewList(null, name);
        }
    }

    private void createNewList(Uri list_uri, String list_name) {
        String listUrl = "";
        if(list_uri != null)
            listUrl = String.valueOf(list_uri);

        wishlistMainFragment.addItemtoAdapter(new Lists(String.valueOf(listUrl), list_name));

        list_ref.push().setValue(new Lists(String.valueOf(listUrl), list_name));
        try {
            FancyToast.makeText(getActivity(),"Successfully created new List !",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
            getFragmentManager().popBackStack(TransactionNameAndFragmentTag.AddListFragment,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            hideKeyboard(getActivity());
            getFragmentManager().popBackStack(TransactionNameAndFragmentTag.AddGiftsFragment,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

}

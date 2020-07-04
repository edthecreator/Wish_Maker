package com.eddelacruz.wishmaker.AddScreens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.eddelacruz.wishmaker.GiftFragments.GiftListsFragment;
import com.eddelacruz.wishmaker.LoadingFragments.LoadingFragment;
import com.eddelacruz.wishmaker.MainActivity;
import com.eddelacruz.wishmaker.Managers.DataManager;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.Models.Wishes;
import com.eddelacruz.wishmaker.R;
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

public class AddGifts extends Fragment implements View.OnClickListener {
    private static String TAG = "ADD GIFTS SCREEN";
    private Button saveButton, cancelButton;
    private TextInputEditText giftname, link, price;
    private ImageView cameraIcon, listimage;
    private Bitmap bitmap = null;
    private String name = "";
    private String uid = DataManager.getInstance().getUserId();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference wish_ref = database.getReference("gifts/" + uid);
    private GiftListsFragment giftListsFragment;

    public AddGifts() { }

    public AddGifts(GiftListsFragment giftListsFragment) {
        this.giftListsFragment = giftListsFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_item, container, false);
        Bundle extras = this.getArguments();
        name = extras.getString("name", "");
        setUpListeners(root);
        return root;
    }

    private void setUpListeners(View rootView) {
        saveButton = rootView.findViewById(R.id.save);
        cancelButton = rootView.findViewById(R.id.cancel);
        giftname = rootView.findViewById(R.id.giftname);
        price = rootView.findViewById(R.id.price);
        link = rootView.findViewById(R.id.link);
        cameraIcon = rootView.findViewById(R.id.cameraIcon);
        listimage = rootView.findViewById(R.id.listimage);

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        listimage.setOnClickListener(this);
        cameraIcon.setOnClickListener(this);
        price.setOnClickListener(this);
        link.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listimage:
            case R.id.cameraIcon:
                hideKeyboard(getActivity());
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), ((MainActivity) getActivity()).UPLOAD_GIFT_IMAGE);
                break;
            case R.id.save:

                if (giftname.getText() != null && !giftname.getText().toString().equals("")) {
                    try {
                        Double giftPrice = 0.00;
                        String uploadName = String.valueOf(giftname.getText());
                        if (!price.getText().toString().equals(""))
                            giftPrice = Double.valueOf(String.valueOf(price.getText()));
                        String giftLink = String.valueOf(link.getText());
                        setUpLoading();
                        uploadFile(bitmap, uploadName, giftPrice, giftLink);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        FancyToast.makeText(getActivity(),"Plase Input Name !",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    /* Toast toast = Toast.makeText(getActivity().getBaseContext(), "Please input list name", Toast.LENGTH_LONG);
                    toast.show(); */
                }
                break;
            case R.id.cancel:
                try {
                    getFragmentManager().popBackStack(TransactionNameAndFragmentTag.AddGiftsFragment,
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
        Log.e(TAG, "IN ONACTIVITYRESULT" + " " + requestCode + " " + resultCode + " " + Activity.RESULT_OK);
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri selectedImage = data.getData();
            cameraIcon.setVisibility(View.INVISIBLE);
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
            Glide.with(listimage.getContext()).load(bitmap).centerCrop().into(listimage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void saveNewItem() {

    }

    private void uploadFile(Bitmap bitmap, final String name, final Double giftprice, final String giftlink) {
        String myFolder = DataManager.getInstance().getUserId();
        if (bitmap != null) {
            try {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://wish-maker-b5fe1.appspot.com/");
                final StorageReference wishImageRef = storageRef.child("giftImages/" + myFolder + "_" + name + ".jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = wishImageRef.putBytes(data);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "FAILURE AT BEGINNING OF UPLOAD TASK");
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return wishImageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            createNewList(downloadUri, name, giftprice, giftlink);
                        } else {
                            Log.e("TAG", "FAILURE AT COMPLETION");
                        }
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            createNewList(null, name, giftprice, giftlink);
        }

    }

    private void createNewList(Uri wish_uri, String wish_name, Double wish_price, String wish_link) {
        String wishUrl = "";
        if(wish_uri != null)
            wishUrl = String.valueOf(wish_uri);

        if(giftListsFragment != null)
            giftListsFragment.addItemtoAdapter(new Wishes(wishUrl, wish_name, wish_price, wish_link, System.currentTimeMillis()));

        wish_ref.child(name).push().setValue(new Wishes(wishUrl, wish_name, wish_price, wish_link, System.currentTimeMillis()));
        try {
            removeLoading();
            FancyToast.makeText(getActivity(),"Gift Successfully Added !",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
            getFragmentManager().popBackStack(TransactionNameAndFragmentTag.AddGiftsFragment,
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

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}


package com.eddelacruz.wishmaker.AuthenticationActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.eddelacruz.wishmaker.LoadingFragments.LoadingFragment;
import com.eddelacruz.wishmaker.MainActivity;
import com.eddelacruz.wishmaker.Managers.DataManager;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.Models.Settings;
import com.eddelacruz.wishmaker.Models.Tracking;
import com.eddelacruz.wishmaker.Models.User;
import com.eddelacruz.wishmaker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;


public class SignUpNextStep extends Fragment implements View.OnClickListener {
    private static String TAG = "Sign Up Next Step";

    private ImageView backArrow;
    private String uid = DataManager.getInstance().getUserId();
    private Button nextStep;
    private String email, firstName, lastName;
    private EditText ETName, ETLast;
    private FirebaseAuth auth;
    private String encryptedPassword;

    public SignUpNextStep() { }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.sign_up_step_two, container, false);
        auth = FirebaseAuth.getInstance();
        try {
            Bundle extras = this.getArguments();
            email = extras.getString("email", "");
            final String password = extras.getString("password", "");
            //try {
                //encryptedPassword = encryptExample.generateStorngPasswordHash(password);
                encryptedPassword = password;
                Log.d(TAG, "ENCRYPTED PASSWORD: " + encryptedPassword);
            /*} catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } */

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setUpListeners(root);
        return root;
    }

    private void setUpListeners(View rootView) {
        //backArrow = rootView.findViewById(R.id.backArrow);
        nextStep = rootView.findViewById(R.id.sign_in_button);
        ETName = rootView.findViewById(R.id.firstname);
        ETLast  = rootView.findViewById(R.id.lastname);
        backArrow = rootView.findViewById(R.id.backArrow);

        backArrow.setOnClickListener(this);
        nextStep.setOnClickListener(this);
        //backArrow.setOnClickListener(this);
        // firebasecall();

    }

    private void firebasecall() {
        firstName = ETName.getText().toString().trim();
        lastName = ETLast.getText().toString().trim();

        try {
            if (!firstName.equals("") && !lastName.equals("")) {
                addUserToDatabase(firstName, lastName);
            } else {
                try {
                    hideKeyboard(getActivity());
                    FancyToast.makeText(getActivity(),"PLEASE FILL ALL REQUIRED FIELDS !",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                try {
                    hideKeyboard(getActivity());
                    getActivity().getSupportFragmentManager().popBackStack(TransactionNameAndFragmentTag.SignUpNextStep,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.sign_in_button:
                hideKeyboard(getActivity());
                setUpLoading();
                firebasecall();
                break;
            default:
                break;
        }
    }

    private void addUserToDatabase(final String FirstName, final String lastName) {
        hideKeyboard(getActivity());

        Log.d(TAG, "CREATE USER DB ");

        //create user
        auth.createUserWithEmailAndPassword(email, encryptedPassword)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getContext(), "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "CREATE USER DB FINALIZATION");
                        removeLoading();
                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        try {
                            long current = System.currentTimeMillis();
                            final User user = new User(email,lastName.toLowerCase(), firstName.toLowerCase(), currentFirebaseUser.getUid(), "happy", current);
                            final Tracking tracking = new Tracking(currentFirebaseUser.getUid(), current, "Y", "Y", "Y");
                            final Settings settings = new Settings(currentFirebaseUser.getUid(),true);
                            FirebaseDatabase.getInstance().getReference("users").child(currentFirebaseUser.getUid()).push().setValue(user);
                            FirebaseDatabase.getInstance().getReference("tracking").child(currentFirebaseUser.getUid()).push().setValue(tracking);
                            FirebaseDatabase.getInstance().getReference("settings").child(currentFirebaseUser.getUid()).push().setValue(settings);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            try {
                                FancyToast.makeText(getActivity(),"An error occured. Please try again !",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        } else {
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            try {
                                getActivity().finish();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

    }

    private void setUpLoading() {
        try {
            hideKeyboard(getActivity());
            LoadingFragment loadingFragment = new LoadingFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.add(R.id.signup_root_frame, loadingFragment, TransactionNameAndFragmentTag.LoadingFragment);
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

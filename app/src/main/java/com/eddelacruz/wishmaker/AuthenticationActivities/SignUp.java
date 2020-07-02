package com.eddelacruz.wishmaker.AuthenticationActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.eddelacruz.wishmaker.LoadingFragments.LoadingFragment;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

public class SignUp extends AppCompatActivity {
    private EditText inputEmail, inputPassword, confirmpassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        confirmpassword = (EditText) findViewById(R.id.confirmPassword);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getParent());
                startActivity(new Intent(SignUp.this, PasswordReset.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(getParent());
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final String confirmPassword = confirmpassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    try {
                        hideKeyboard(getParent());
                        FancyToast.makeText(getBaseContext(),"Enter your email !",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    try {
                        hideKeyboard(getParent());
                        FancyToast.makeText(getBaseContext(),"Enter your password !",FancyToast.LENGTH_LONG, FancyToast.ERROR,true).show();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if (password.length() < 6) {
                    try {
                        hideKeyboard(getParent());
                        FancyToast.makeText(getBaseContext(),"PASSWORD TOO SHORT. PLEASE INPUT A PASSWORD AT LEAST 6 CHARACTERS LONG !",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    try {
                        hideKeyboard(getParent());
                        FancyToast.makeText(getBaseContext(),"PASSWORDS DO NOT MATCH !",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                addUserTodatabase(email, password);

            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private void addUserTodatabase(String email, String password) {

        try {
            Bundle data = new Bundle();
            data.putString("email", email);
            data.putString("password", password);
            SignUpNextStep signUpNextStep = new SignUpNextStep();
            signUpNextStep.setArguments(data);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.add(R.id.signup_root_frame, signUpNextStep, TransactionNameAndFragmentTag.SignUpNextStep);
            fragmentTransaction.addToBackStack(TransactionNameAndFragmentTag.SignUpNextStep);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
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

    private void setUpLoading() {
        try {
            LoadingFragment loadingFragment = new LoadingFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
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

}

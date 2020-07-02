package com.eddelacruz.wishmaker.AuthenticationActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import com.eddelacruz.wishmaker.MainActivity;
import com.eddelacruz.wishmaker.Managers.TransactionNameAndFragmentTag;
import com.eddelacruz.wishmaker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;


public class Login extends AppCompatActivity {
    private static String TAG = "LOGIN";
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private Button btnSignup, btnLogin, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getParent());
                startActivity(new Intent(Login.this, SignUp.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getParent());
                startActivity(new Intent(Login.this, PasswordReset.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getParent());
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    try {
                        FancyToast.makeText(getParent(),"Please Input Email !",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    try {
                        hideKeyboard(getParent());
                        FancyToast.makeText(getParent(),"Please Input Password !",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                Log.e(TAG, "SET UP LOAD");
                setUpLoading();


                hideKeyboard(getParent());
                //try {
                   // final String login = encryptExample.generateStorngPasswordHash(password);
                    final String login = password;
                    Log.d(TAG, "ENCRYPTED PASSWORD: " + login);
                    auth.signInWithEmailAndPassword(email, login)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.e(TAG, "SIGN IN TASK COMPLETED");
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.

                                       if(task.isSuccessful()) {
                                           Log.e(TAG, "REMOVE LOAD");
                                           removeLoading();
                                           Intent intent = new Intent(Login.this, MainActivity.class);
                                           startActivity(intent);
                                           finish();
                                       } else {
                                           Log.e(TAG, "REMOVE LOAD");
                                           removeLoading();
                                           if (password.length() < 6) {
                                               inputPassword.setError(getString(R.string.minimum_password));
                                           } else {
                                              // FancyToast.makeText(getParent(), "Authentication Failed !", FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                                           }
                                       }
                                }
                            });
                /*} catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } */
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



    private void setUpLoading() {
        try {
            hideKeyboard(this);
            LoadingFragment loadingFragment = new LoadingFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.add(R.id.login_root_frame, loadingFragment, TransactionNameAndFragmentTag.LoadingFragment);
            fragmentTransaction.addToBackStack(TransactionNameAndFragmentTag.LoadingFragment);
            fragmentTransaction.commitAllowingStateLoss();
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

    private void removeLoading() {
        try {
            getSupportFragmentManager().popBackStack(TransactionNameAndFragmentTag.LoadingFragment,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}

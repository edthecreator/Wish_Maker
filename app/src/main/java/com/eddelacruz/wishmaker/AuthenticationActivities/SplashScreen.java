package com.eddelacruz.wishmaker.AuthenticationActivities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.eddelacruz.wishmaker.MainActivity;
import com.eddelacruz.wishmaker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {
    private static String TAG = "Splash Screen";

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        auth = FirebaseAuth.getInstance();

        checkVersionControl();
    }

    private void checkVersionControl() {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("version_control");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String retrievedVN = dataSnapshot.getValue().toString();
                    PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                    String version = pInfo.versionName;

                    Log.d(TAG, "version control " + retrievedVN + version);
                    if(!retrievedVN.equals(version)) {
                        startActivity(new Intent(getApplicationContext(), GetUpdate.class));
                        finish();
                    } else if(retrievedVN.equals(version) && auth.getCurrentUser() != null){
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }  if (retrievedVN.equals(version) && auth.getCurrentUser() == null) {
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        finish();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

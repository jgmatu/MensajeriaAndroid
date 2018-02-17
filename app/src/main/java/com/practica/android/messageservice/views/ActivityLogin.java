package com.practica.android.messageservice.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.practica.android.messageservice.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;

public class ActivityLogin extends AppCompatActivity {

    public static final String idToken = "id_token";
    public static final String UID = "uid";
    public static final String EMAIL = "email";

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = ActivityLogin.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setFirebaseUIAuth();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (resultCode == RESULT_OK && firebaseAuth.getCurrentUser() != null) {
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            Intent groups = new Intent(this, ActivityGroups.class);

            if (idpResponse != null) {
                groups.putExtra(idToken, idpResponse.getIdpToken());
                groups.putExtra(EMAIL, idpResponse.getEmail());
            }
            groups.putExtra(UID, firebaseAuth.getCurrentUser().getUid());
            runActivityGroups(groups);
        } else {
            setFirebaseUIAuth();
        }
    }

    private void setFirebaseUIAuth() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.AppTheme)
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.TwitterBuilder().build()))
                        .build(),
                RC_SIGN_IN);
    }

    private void runActivityGroups(Intent groups) {
        startActivity(groups);
        finish();
    }
}

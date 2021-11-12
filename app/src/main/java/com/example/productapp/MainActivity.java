package com.example.productapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /** Variables globales **/
    private static final String TAG = "Main Activity";
    private View baseView;

    /** Initialisation des composants **/
    public void init(){
        baseView = findViewById(R.id.mainLayout);
    }

    /** Gestion du clic sur le bouton SIGN UP **/
    public void startSignUpActivity(View view){
        Log.i(TAG, "startSignUpActivity: ");
        signUpActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void showSnackBar(String message){
        Snackbar.make(baseView, message, Snackbar.LENGTH_LONG).show();
    }

    /** La variable du callbask de retour du signLauncher **/
    private final ActivityResultLauncher<Intent> signLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignResult(result);
                }
            }
    );

    /** Méthode de gestion du retour callback **/
    private void onSignResult(FirebaseAuthUIAuthenticationResult result){
        IdpResponse reponse = result.getIdpResponse();
        if(result.getResultCode() == RESULT_OK) {
            /// Connecté
            showSnackBar("Connected !");
        } else {
            /// Pas Connecté
            showSnackBar("Not connected !!");
        }
    }

    private void signUpActivity(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();

        signLauncher.launch(signInIntent);

    }
}










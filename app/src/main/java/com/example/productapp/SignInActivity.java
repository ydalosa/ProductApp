package com.example.productapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.productapp.commons.Utils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

/** #0 Connexion au compte Google qui gère Firebase **/
/** #1 Ajout de l'app à un projet Firebase au besoin en créer un avec génération du SHA1 **/
/** #1.1 Activer Authenticator dans Firebase > Email Password **/
/** #2 Ajout de Google Authenticator via Email and Password en utilisant le menu Tools > Firebase **/
/** #3 Ajout des dépendances pour FirebaseUI dans le Gradle Module **/

/** #4 Préparation du thème à afficher dans colors.xml - dimens.xml et theme.xml **/
/** #4.1 Ajout du theme dans le manifest et changement du logo **/
/** #5 Préparation du design de la page du choix de connexion **/
/** #5.1 Ajout de la shape pour la forme du bouton **/
public class SignInActivity extends AppCompatActivity {

    /**
     * Variables globales
     **/
    private static final String TAG = "Main Activity";
    private View baseView;

    /**
     * Initialisation des composants
     **/
    public void init() {
        baseView = findViewById(R.id.mainLayoutSignIn);
    }

    /**
     * Gestion du clic sur le bouton SIGN UP
     **/
    public void startSignUpActivity(View view) {
        Log.i(TAG, "startSignUpActivity: ");
        signUpActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            // Intent vers l'activité principale
            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
        }
    }




    /**
     * La variable du callback de retour du signLauncher
     **/
    private final ActivityResultLauncher<Intent> signLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignResult(result);
                }
            }
    );

    /**
     * Méthode de gestion du retour callback
     **/
    private void onSignResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse reponse = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            /// Connecté
            Utils.showSnackBar(baseView,"Connected !");
            /** #1 Gestion de l'affichage de l'activité de l'app **/
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        } else {
            /// Pas Connecté
            if(reponse == null) {
                Utils.showSnackBar(baseView, getString(R.string.sign_result_canceled));
            } else if(reponse.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                Utils.showSnackBar(baseView, getString(R.string.sign_result_no_internet));
            } else if(reponse.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                Utils.showSnackBar(baseView, getString(R.string.sign_result_unknow_error));
            }
        }
    }

    private void signUpActivity() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                /*** On recommence ici **/
                // Enfin vers la modfication du theme pour le rendre plus personnalisable
                // Ajout du logo et du theme
                .setLogo(R.drawable.firebase_logo) // Le logo à afficher
                .setTheme(R.style.LoginTheme) // Le theme à associer aux pages de FirebaseUI
                .setTosAndPrivacyPolicyUrls("https://google.fr", "https://yahoo.fr") // RGPD
                .setIsSmartLockEnabled(true) // Enregistre l'état de l'utilisateur, s'il est connécté alors sign In auto
                .build();

        signLauncher.launch(signInIntent);
    }
}










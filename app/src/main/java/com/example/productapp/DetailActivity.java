package com.example.productapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.productapp.commons.NodesNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailActivity extends AppCompatActivity {

    /** 1 Var globales de widgets **/
    private ImageView ivDetailAffiche;
    private TextView tvDetailTitre,tvDetailAnnee,tvDetailActeurs,tvDetailSynopsis;

    /** Var Firebase **/
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference filmDocRef;

    private String idFilm;

    private static final String TAG = "DETAIL";

    private String titre,  acteurs, affiche, synopsis;
    private long annee;

    /** 2 Méthode d'initialisation des widgets **/
    public void init(){
        ivDetailAffiche = findViewById(R.id.ivDetailAffiche);
        tvDetailTitre = findViewById(R.id.tvDetailTitre);
        tvDetailAnnee = findViewById(R.id.tvDetailAnnee);
        tvDetailActeurs = findViewById(R.id.tvDetailActeurs);
        tvDetailSynopsis = findViewById(R.id.tvDetailSynopsis);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /** 3 Appel de la méthode d'initialisation des widgets **/
        init();

        // Réucpération du string de l'idFilm
        Intent intent = getIntent();
        idFilm = intent.getStringExtra(NodesNames.KEY_ID);
        Log.e(TAG, "onCreate: " + idFilm);
        // Péraparation de la reférence au document en fonction de l'ID
        filmDocRef = db.collection(NodesNames.KEY_COLLECTION_PRODUCTS).document(idFilm);

        getFilmDetail();
    }

    /** #4 Ajout de la méthode pour récupérer les données en fonction de l'id **/
    private void getFilmDetail() {
        filmDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            // Récupèrer mes données
                            titre = documentSnapshot.getString(NodesNames.KEY_TITRE);
                            acteurs = documentSnapshot.getString(NodesNames.KEY_ACTEURS);
                            affiche = documentSnapshot.getString(NodesNames.KEY_AFFICHE);
                            synopsis = documentSnapshot.getString(NodesNames.KEY_SYNOPSIS);
                            annee = documentSnapshot.getLong(NodesNames.KEY_ANNEE);

                            tvDetailTitre.setText(titre);
                            tvDetailActeurs.setText(acteurs);
                            tvDetailAnnee.setText("Année : " + annee);
                            tvDetailSynopsis.setText(synopsis);

                            // Ajout de Glide pour l'affichage de l'affiche (image)
                            RequestOptions options = new RequestOptions()
                                    .centerCrop()
                                    .error(R.mipmap.ic_launcher)
                                    .placeholder(R.mipmap.ic_launcher);

                            Context context = ivDetailAffiche.getContext();
                            Glide.with(context)
                                    .load(affiche)
                                    .apply(options)
                                    .fitCenter()
//                                    .override(150, 150)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(ivDetailAffiche);
                        } else {
                            Toast.makeText(DetailActivity.this, "An error occur, no data",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }
}
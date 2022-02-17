package com.example.productapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.productapp.commons.NodesNames;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity {

    /** Pour ajouter la recherche commencer par ajouter
     * - Un vector asset avec un icone pour la recherche
     * - Un menu pour l'insérer
     * - Model pour la gestion des id et du texte lors de la recherche
     * Inflater ce menu cf #5 Ajout du menu
     */

    Context context;
    /**
     * Var compposants
     **/
    private RecyclerView recyclerView;
    private AdapterFilms adapterFilms;

    /**
     * Var Firebase
     **/
    private FirebaseFirestore db;

    /** 5.1 Ajout de la Toolbar **/
    private Toolbar myToolbar;

    /**
     * Méthode d'initialisation des composants
     **/
    public void init() {
        recyclerView = findViewById(R.id.rvFilms);
        recyclerView.setHasFixedSize(true);

        // Initialisation du recycler avec le linear Wrapper cf ci-dessous
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(context, LinearLayoutManager.VERTICAL, false));

        db = FirebaseFirestore.getInstance();

        /** 5.2 Initialidation de la toolbar, cette toolbar a été ajoutée dans le layout de l'activité **/
        myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
    }

    private void getDataFromFirestore() {
        Query query = db.collection("products").orderBy("titre");

        FirestoreRecyclerOptions<ModelFilms> products =
                new FirestoreRecyclerOptions.Builder<ModelFilms>()
                        .setQuery(query, ModelFilms.class)
                        .build();

        adapterFilms = new AdapterFilms(products);

        recyclerView.setAdapter(adapterFilms);
    }

    private void addSampleData() {
//        SharedPreferences sharedPreferences = getSharedPreferences("com.example.productapp.prefs", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = getSharedPreferences(R.class.getPackage().getName()
                + ".prefs", Context.MODE_PRIVATE);

        // La vérifcationb du boolean
        if (!sharedPreferences.getBoolean("dataInsertIntoFireBase", false)) {
            AddSampleDatasToFireBase.addDatasToFireBase(getApplicationContext());
        }
    }

    /** Le wrapper pour éviter le crash de l'app lors de l'appuie sur le bouton retour depuis l'activité détail
     * Ce wrapper sera à appeler pour la génération du Linear Layout qui gère le recyclerView
     */
    public class LinearLayoutManagerWrapper extends LinearLayoutManager {

        public LinearLayoutManagerWrapper(Context context) {
            super(context);
        }

        public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    /** #5 Ajout du menu
     * Voir 5.1 pour l'ajout de la Toolbar **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Lien vers le layaout du menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Lien avec les widgets du menu
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        // Ajout du listener sur le menu et ajout des méthodes après on va implémenter Filtrable dans l'adapter
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Ici on ne met rien c'est la partie qui agit lors du clic sur un bouton
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchFilm(s.toString());
                return false;

            }
        });
        return true;
    }

    // Méthode pour filtrer la liste de films
    private void searchFilm(String s){
        // Récupération de la collection de films dans Firestore
        Query query = db.collection("products");

        if(!String.valueOf(s).equals("")){
            query = query
                    .orderBy("titre_minuscule")
                    .startAt(s)
                    .endAt(s+"\uf8ff");
        }

        FirestoreRecyclerOptions<ModelFilms> searchFilm =
                new FirestoreRecyclerOptions.Builder<ModelFilms>()
                        .setQuery(query, ModelFilms.class)
                        .build();

        adapterFilms = new AdapterFilms(searchFilm);
        recyclerView.setAdapter(adapterFilms);
        adapterFilms.startListening();
    }

    /**
     * ############## CYCLES DE VIE ##############
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.i(" ################## 1", "onCreate: ");
        init();

        addSampleData();

        getDataFromFirestore();

        adapterFilms.setOnItemClickListener(new AdapterFilms.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                // On peut créer un objet comprenant toutes les données en se basant sur notre modèle
//                ModelFilm film = documentSnapshot.toObject(ModelFilm.class);
//              // On peut alors récupérer une des données du modèle
//                String titrDuFilm = film.getTitre();
//              // On peut avoir une référence à ce document pour un update ou une suppression du document
//                documentSnapshot.getReference();
                // On peut juste récupèrer l'ID du film pour le passer dans l'activité suivante et alors l'utiliser pour afficher les données de la base
                String idFilm = documentSnapshot.getId();
                // POur le test avant l'intent
//                 Toast.makeText(HomeActivity.this, "ID : " + idFilm, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                intent.putExtra(NodesNames.KEY_ID, idFilm);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(HomeActivity.this, SignInActivity.class));
            Log.i("##################START", "USER NULL: ");
        } else {
            adapterFilms.startListening();
            Log.i("##################START", "USER OK: ");

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterFilms.stopListening();
    }
}
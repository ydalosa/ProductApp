package com.example.productapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    /** Var compposants **/
    private RecyclerView recyclerView;
    private AdapterFilms adapterFilms;

    /** Var Firebase **/
    private FirebaseFirestore db;

    /** Méthode d'initialisation des composants **/
    public void init(){
        recyclerView = findViewById(R.id.rvFilms);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
    }

    private void getDataFromFirestore(){

    }

    private void addSampleData(){
//        SharedPreferences sharedPreferences = getSharedPreferences("com.example.productapp.prefs", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = getSharedPreferences(R.class.getPackage().getName()
                + ".prefs", Context.MODE_PRIVATE);

        // La vérifcationb du boolean
        if(!sharedPreferences.getBoolean("dataInsertIntoFireBase", false)){
            AddSampleDatasToFireBase.addDatasToFireBase(getApplicationContext());
        }
    }

    /** ############## CYCLES DE VIE ############## **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();

        addSampleData();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(HomeActivity.this, SignInActivity.class));
        } else {
//            adapterFilms.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        adapterFilms.stopListening();
    }
}
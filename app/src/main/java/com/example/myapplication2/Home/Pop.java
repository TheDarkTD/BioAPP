package com.example.myapplication2.Home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.ConectInsole;
import com.example.myapplication2.ConectInsole2;
import com.example.myapplication2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Pop extends AppCompatActivity{

    FloatingActionButton mCloseBtn;
    private String followInRight, followInLeft;
    TextView atualiza;
    DatabaseReference databaseReference;
    private List<String> Listevents;
    private FirebaseAuth fAuth;
    String senddatainsole1="Não há leituras.";
    String senddatainsole2="Não há leituras.";
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popupwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);


        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.8));

        ConectInsole conectar = new ConectInsole(this);
        ConectInsole2 conectar2 = new ConectInsole2(this);

        SharedPreferences sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
        followInRight = sharedPreferences.getString("Sright", "default");
        followInLeft = sharedPreferences.getString("Sleft", "default");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            uid = user.getUid();  // Pega o UID do usuário logado
            databaseReference = FirebaseDatabase.getInstance("https://bioapp-496ae-default-rtdb.firebaseio.com/")
                    .getReference()
                    .child("Users")
                    .child(uid);  // Salvar dados no nó "Users/{UID}"

            // Inicializando SharedPreferences para armazenar dados localmente
            sharedPreferences = getSharedPreferences("offline_data", Context.MODE_PRIVATE);
        }

        if (followInRight.equals("true")){

            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(uid)
                    .child("novaVariavel");


            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        // Converter o valor para uma lista de strings
                        for (DataSnapshot item : snapshot.getChildren()) {
                            String valor = item.getValue(String.class);
                            if (valor != null) {
                                senddatainsole1 = valor;
                            }
                        }

                        // Agora você pode usar listeventsright como quiser
                        Log.d("Firebase", "listeventsright: " + senddatainsole1.toString());

                    } else {
                        Log.d("Firebase", "novaVariavel está vazia ou não existe.");
                    }
                } else {
                    Log.e("Firebase", "Erro ao buscar dados: ", task.getException());
                }
            });


        }
        if (followInLeft.equals("true")) {
            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(uid)
                    .child("novaVariavel2");


            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        // Converter o valor para uma lista de strings
                        for (DataSnapshot item : snapshot.getChildren()) {
                            String valor = item.getValue(String.class);
                            if (valor != null) {
                                senddatainsole2=valor;
                            }
                        }

                        // Agora você pode usar listeventsright como quiser
                        Log.d("Firebase", "listeventsright: " + senddatainsole2.toString());

                    } else {
                        Log.d("Firebase", "novaVariavel está vazia ou não existe.");
                    }
                } else {
                    Log.e("Firebase", "Erro ao buscar dados: ", task.getException());
                }
            });




        }

        Listevents.add(senddatainsole1);
        Listevents.add(senddatainsole2);

        StringBuilder builder = new StringBuilder();
        for (String item : Listevents) {
            builder.append(item).append("\n");
        }
        atualiza.setText(builder.toString());



        mCloseBtn =(FloatingActionButton) findViewById(R.id.buttonclose);
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}

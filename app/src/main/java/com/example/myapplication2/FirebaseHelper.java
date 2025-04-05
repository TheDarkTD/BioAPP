package com.example.myapplication2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FirebaseHelper {

    private DatabaseReference mDatabase;
    private String userId;

    // Construtor que recupera o UID do usuário logado e inicializa o banco de dados com o UID
    public FirebaseHelper() {
        // Recuperar o UID do usuário logado usando FirebaseAuth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userId = user.getUid();  // Pega o UID do usuário logado
            // Inicializando a referência ao Firebase Realtime Database usando o URL fornecido
            mDatabase = FirebaseDatabase.getInstance("https://bioapp-496ae-default-rtdb.firebaseio.com/")
                    .getReference()
                    .child("Users")
                    .child(userId);  // Salvar dados no nó "Users/{UID}"
        }
    }

    // Método para salvar ConfigData no Firebase para o usuário logado
    public void saveConfigData(ConectInsole.ConfigData configData) {
        // Usando push() para gerar um ID único para cada entrada
        mDatabase.child("config").push().setValue(configData)
                .addOnSuccessListener(aVoid -> {
                    // Sucesso ao salvar
                    System.out.println("ConfigData salvo no Firebase com sucesso!");
                })
                .addOnFailureListener(e -> {
                    // Falha ao salvar
                    System.err.println("Erro ao salvar ConfigData: " + e.getMessage());
                });
    }

    // Método para salvar SendData no Firebase para o usuário logado
    // Método para salvar SendData no Firebase para o usuário logado
    public void saveSendData(ConectInsole.SendData sendData) {
        // Formata a data atual no padrão dia-mês-ano
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // Cria o caminho: DATA -> currentDate -> id único (push)
        mDatabase.child("DATA").child(currentDate).push().setValue(sendData)
                .addOnSuccessListener(aVoid -> {
                    // Sucesso ao salvar
                    System.out.println("SendData salvo no Firebase com sucesso!");
                })
                .addOnFailureListener(e -> {
                    // Falha ao salvar
                    System.err.println("Erro ao salvar SendData: " + e.getMessage());
                });
    }


    // Método para salvar SendData2 no Firebase para o usuário logado
    public void saveSendData2(ConectInsole2.SendData sendData) {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        // Usando push() para gerar um ID único para cada entrada
        mDatabase.child("DATA2").child(currentDate).push().setValue(sendData)
                .addOnSuccessListener(aVoid -> {
                    // Sucesso ao salvar
                    System.out.println("SendData2 salvo no Firebase com sucesso!");
                })
                .addOnFailureListener(e -> {
                    // Falha ao salvar
                    System.err.println("Erro ao salvar SendData2: " + e.getMessage());
                });
    }
}

package com.example.myapplication2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    public void saveSendData(ConectInsole.SendData sendData) {
        // Usando push() para gerar um ID único para cada entrada
        mDatabase.child("DATA").push().setValue(sendData)
                .addOnSuccessListener(aVoid -> {
                    // Sucesso ao salvar
                    System.out.println("SendData salvo no Firebase com sucesso!");
                })
                .addOnFailureListener(e -> {
                    // Falha ao salvar
                    System.err.println("Erro ao salvar SendData: " + e.getMessage());
                });
    }

    // Método para salvar ConfigData2 no Firebase para o usuário logado
    public void saveConfigData2(ConectInsole2.ConfigData configData) {
        // Usando push() para gerar um ID único para cada entrada
        mDatabase.child("config2").push().setValue(configData)
                .addOnSuccessListener(aVoid -> {
                    // Sucesso ao salvar
                    System.out.println("ConfigData2 salvo no Firebase com sucesso!");
                })
                .addOnFailureListener(e -> {
                    // Falha ao salvar
                    System.err.println("Erro ao salvar ConfigData2: " + e.getMessage());
                });
    }

    // Método para salvar SendData2 no Firebase para o usuário logado
    public void saveSendData2(ConectInsole2.SendData sendData) {
        // Usando push() para gerar um ID único para cada entrada
        mDatabase.child("Data2").push().setValue(sendData)
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

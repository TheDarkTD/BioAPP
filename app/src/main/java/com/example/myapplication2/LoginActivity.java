package com.example.myapplication2;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.Home.HomeActivity;
import com.example.myapplication2.Register.Register1Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mCreateBtn;
    FirebaseAuth fAuth;
    DatabaseReference databaseReference;
    ConectInsole conectInsole;
    ConectInsole2 conectInsole2;
    String uid = null; // Definir o uid como null
    String left,right;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        conectInsole = new ConectInsole(this);  // Inicializa a ConectInsole

        // Limpar qualquer sessão anterior
        fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();  // Garantir que o FirebaseAuth comece sem usuário logado

        // Definir uid como null para garantir que ele seja redefinido ao abrir a tela de login
        uid = null;

    }

    @Override
    public void onStart() {
        super.onStart();

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.btnLogin);
        mCreateBtn = findViewById(R.id.textRegister);

        // Definindo o idioma explicitamente para português do Brasil
        fAuth.setLanguageCode("pt-BR");

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email obrigatório.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Senha obrigatória.");
                    return;
                }

                // Autenticar o usuário
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login bem-sucedido", Toast.LENGTH_LONG).show();
                            FirebaseUser user = fAuth.getCurrentUser();
                            if (user != null) {
                                uid = user.getUid();  // Pega o UID do usuário autenticado
                                loadinsoleinfo(uid, v);

                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Erro no login: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register1Activity.class));
            }
        });
    }

    private void loadUserData1(String uid, View v) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {

                    ConectInsole.ConfigData configData1 = dataSnapshot.child("config_data1").getValue(ConectInsole.ConfigData.class);
                    right=dataSnapshot.child("InsolesR").getValue(String.class);
                    conectInsole.setConfigData(configData1);
                    System.out.println("config data recebido do login" + configData1);
                    Intent myIntent = new Intent(v.getContext(), HomeActivity.class);
                    startActivity(myIntent);
                } else {
                    Toast.makeText(LoginActivity.this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Falha ao carregar os dados.", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadUserData2(String uid, View v) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    // Recuperar os dados do usuário e ConfigData
                    ConectInsole2.ConfigData configData2 = dataSnapshot.child("config_data2").getValue(ConectInsole2.ConfigData.class);
                    conectInsole2.setConfigData(configData2);
                    left=dataSnapshot.child("InsolesL").getValue(String.class);
                    System.out.println("config data recebido do login" + configData2);

                    Intent myIntent = new Intent(v.getContext(), HomeActivity.class);
                    startActivity(myIntent);
                } else {
                    Toast.makeText(LoginActivity.this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Falha ao carregar os dados.", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadinsoleinfo(String uid, View v) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    right = dataSnapshot.child("InsolesR").getValue(String.class);
                    Log.e(TAG, "Falha ao verificar novos dados: " + right);
                    left = dataSnapshot.child("InsolesL").getValue(String.class);
                    Log.e(TAG, "Falha ao verificar novos dados: " + left);
                    Intent myIntent = new Intent(v.getContext(), HomeActivity.class);
                    startActivity(myIntent);
                } else {
                    Toast.makeText(LoginActivity.this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Falha ao carregar os dados.", Toast.LENGTH_SHORT).show();
            }
        });

        if (right.equals("true")) {
            loadUserData1(uid, v);  // Carrega os dados do Firebase
        }
        if (left.equals("true")) {
            loadUserData2(uid, v);  // Carrega os dados do Firebase
        }
        SharedPreferences sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Sleft", left);
        editor.putString("Sright", right);
        editor.apply();
    }
}

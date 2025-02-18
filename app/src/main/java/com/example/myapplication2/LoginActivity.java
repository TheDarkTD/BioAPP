package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    String uid = null; // Definir o uid como null

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
                                loadUserData(uid, v);  // Carrega os dados do Firebase
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

    private void loadUserData(String uid, View v) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    // Recuperar os dados do usuário e ConfigData
                    ConectInsole.ConfigData configData = dataSnapshot.child("config_data").getValue(ConectInsole.ConfigData.class);
                    conectInsole.setConfigData(configData);
                    System.out.println("config data recebido do login" + configData);
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

}

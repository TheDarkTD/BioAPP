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
                                loadUserData(uid, v);

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

    private void loadUserData(String uid, View view) {
        Log.d("LoadUserData", "Iniciando carregamento de dados para uid: " + uid);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                Log.d("LoadUserData", "DataSnapshot recebido: " + dataSnapshot.getValue());

                if (dataSnapshot.exists()) {
                    Log.d("LoadUserData", "Dados encontrados para uid: " + uid);

                    // Listando todas as chaves presentes para depuração
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Log.d("LoadUserData", "Chave encontrada: " + child.getKey());
                    }

                    // Recupera as flags para os insole e loga
                    String flagInsoleR = dataSnapshot.child("InsolesR").getValue(String.class);
                    String flagInsoleL = dataSnapshot.child("InsolesL").getValue(String.class);
                    Log.d("LoadUserData", "FlagInsoleR: " + flagInsoleR + " | FlagInsoleL: " + flagInsoleL);

                    // Carrega ConfigData1 caso o insole direito esteja ativado
                    if ("true".equalsIgnoreCase(flagInsoleR)) {
                        DataSnapshot configRightSnapshot = dataSnapshot.child("ConfigData1");
                        Log.d("LoadUserData", "Dados do ConfigData1: " + configRightSnapshot.getValue());

                        if (configRightSnapshot.exists()) {
                            for (DataSnapshot child : configRightSnapshot.getChildren()) {
                                Log.d("LoadUserData", "ConfigData1 Child: "
                                        + child.getKey() + " - " + child.getValue());
                            }
                        } else {
                            Log.e("LoadUserData", "Nó ConfigData1 não existe!");
                        }

                        ConectInsole.ConfigData configData1 = configRightSnapshot.getValue(ConectInsole.ConfigData.class);
                        if (configData1 != null) {
                            Log.d("LoadUserData", "ConfigData1 carregado com sucesso: " + configData1.toString());
                        } else {
                            Log.e("LoadUserData", "ConfigData1 retornou null!");
                        }
                        conectInsole.setConfigData(configData1);
                    } else {
                        Log.d("LoadUserData", "FlagInsoleR não está 'true'. Não carregou ConfigData1.");
                    }

                    // Carrega ConfigData2 caso o insole esquerdo esteja ativado
                    if ("true".equalsIgnoreCase(flagInsoleL)) {
                        DataSnapshot configLeftSnapshot = dataSnapshot.child("ConfigData2");
                        Log.d("LoadUserData", "Dados do ConfigData2: " + configLeftSnapshot.getValue());

                        ConectInsole2.ConfigData configData2 = configLeftSnapshot.getValue(ConectInsole2.ConfigData.class);
                        if (configData2 != null) {
                            Log.d("LoadUserData", "ConfigData2 carregado com sucesso: " + configData2.toString());
                        } else {
                            Log.e("LoadUserData", "ConfigData2 retornou null!");
                        }
                        conectInsole2.setConfigData(configData2);
                    } else {
                        Log.d("LoadUserData", "FlagInsoleL não está 'true'. Não carregou ConfigData2.");
                    }

                    // Salva as flags em SharedPreferences (já existente no seu código)
                    SharedPreferences sharedPreferences = getSharedPreferences("My_Appinsolesamount", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Sright", flagInsoleR);
                    editor.putString("Sleft", flagInsoleL);
                    editor.apply();
                    Log.d("LoadUserData", "Flags salvas nas SharedPreferences.");

                    // --- Recuperando os dados de vibração ---
                    DataSnapshot vibraSnapshot = dataSnapshot.child("vibra");
                    if (vibraSnapshot.exists()) {
                        Integer vibraTime = vibraSnapshot.child("time").getValue(Integer.class);
                        Integer vibraThreshold = vibraSnapshot.child("threshold").getValue(Integer.class);
                        Integer vibraInterval = vibraSnapshot.child("interval").getValue(Integer.class);
                        Integer vibraPulse = vibraSnapshot.child("pulse").getValue(Integer.class);

                        SharedPreferences vibraPref = getSharedPreferences("vibra", MODE_PRIVATE);
                        SharedPreferences.Editor vibraEditor = vibraPref.edit();
                        if (vibraTime != null) {
                            vibraEditor.putInt("time", vibraTime);
                        }
                        if (vibraThreshold != null) {
                            vibraEditor.putInt("threshold", vibraThreshold);
                        }
                        if (vibraInterval != null) {
                            vibraEditor.putInt("interval", vibraInterval);
                        }
                        if (vibraPulse != null) {
                            vibraEditor.putInt("pulse", vibraPulse);
                        }
                        vibraEditor.apply();
                        Log.d("LoadUserData", "Dados de vibração salvos em SharedPreferences 'vibra'.");
                    } else {
                        Log.d("LoadUserData", "Nenhuns dados de vibração encontrados no DataSnapshot.");
                    }
                    // --- Fim da recuperação de vibração ---

                    // Após carregar os dados, inicia a HomeActivity
                    Log.d("LoadUserData", "Iniciando HomeActivity...");
                    Intent intent = new Intent(view.getContext(), HomeActivity.class);
                    startActivity(intent);
                } else {
                    Log.e("LoadUserData", "Nenhum dado encontrado para uid: " + uid);
                    Toast.makeText(LoginActivity.this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("LoadUserData", "Erro ao carregar os dados: " + task.getException().getMessage());
                Toast.makeText(LoginActivity.this, "Falha ao carregar os dados.", Toast.LENGTH_SHORT).show();
            }
        });
    }





}

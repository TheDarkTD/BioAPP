package com.example.myapplication2.Settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.Connection.ConnectionActivity;
import com.example.myapplication2.Data.DataActivity;
import com.example.myapplication2.Home.HomeActivity;
import com.example.myapplication2.LoginActivity;
import com.example.myapplication2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class SettingsActivity extends AppCompatActivity {
    Button mLogoutBtn;
    TextView mAccountBtn, mParametersBtn, mVibraBtn, mNotificationsBtn, mUpdatesBtn, mUseInstructionsBtn;
    FirebaseAuth fAuth;
    ImageView fotoid;
    String userName, userEmail;
    private SharedPreferences sharedPreferences;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("User_info", MODE_PRIVATE);
        userName = sharedPreferences.getString("Name", "default");
        userEmail = sharedPreferences.getString("Email", "default");
    }


    @Override
    public void onStart() {
        super.onStart();

        mAccountBtn = findViewById(R.id.Conta);
        mParametersBtn = findViewById(R.id.Parametros);
        mVibraBtn = findViewById(R.id.Vibra);
        mNotificationsBtn = findViewById(R.id.Notifica);
        mUpdatesBtn = findViewById(R.id.Update);
        mUseInstructionsBtn = findViewById(R.id.Manual);
        fotoid=findViewById(R.id.fotoid);
        TextView mNamespace = findViewById(R.id.nomeusuario);
        TextView mEmailspace = findViewById(R.id.emailusuario);

        mNamespace.setText(userName);
        mEmailspace.setText(userEmail);

        mLogoutBtn = findViewById(R.id.btnLogout);
        fAuth = FirebaseAuth.getInstance();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavview2);
        bottomNavigationView.setSelectedItemId(R.id.settings);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                    return true;
                case R.id.settings:
                    return true;
                case R.id.connection:
                    startActivity(new Intent(getApplicationContext(), ConnectionActivity.class));
                    finish();
                    return true;
                case R.id.data:
                    startActivity(new Intent(getApplicationContext(), DataActivity.class));
                    finish();
                    return true;
            }
            return false;
        });


        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fAuth.getCurrentUser() != null)
                    fAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        mAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
            }
        });

        mParametersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ParametersActivity.class));
            }
        });

        mVibraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), VibraActivity.class));
            }
        });

        mNotificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
            }
        });

        mUpdatesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UpdateActivity.class));
            }
        });

        mUseInstructionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UseInstructionsActivity.class));
            }
        });

        fotoid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Escolha sua imagem"), 1);
            }
        });
    }

    protected void onActivityForResult(int RequestCode, int ResultCode, Intent dados){
        super.onActivityResult(RequestCode, ResultCode, dados);
        if(ResultCode == Activity.RESULT_OK){
            if(RequestCode == 1){
                fotoid.setImageURI(dados.getData());
            }
        }
    }
}
package com.jorgejag.cuidaportmanoperario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    //Elementos en la activity

    private EditText editEmail;
    private EditText editPassword;
    private Button btnLogin;

    //Datos para logear

    private String email = "";
    private String password = "";

    //Autenticacion
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmailLogin);
        editPassword = findViewById(R.id.editPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);

        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editEmail.getText().toString();
                password = editPassword.getText().toString();

                //Comprobamos que los campos no estan vacios
                if (!email.isEmpty() && !password.isEmpty()) {
                    loginUsuario();
                } else {
                    Toast.makeText(LoginActivity.this, "Completa los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Metodo para hacer login en la base de datos
    private void loginUsuario() {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Datos incorrectos, no se pudo iniciar sesion.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}


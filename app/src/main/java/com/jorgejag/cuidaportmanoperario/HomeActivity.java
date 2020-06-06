package com.jorgejag.cuidaportmanoperario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

public class HomeActivity extends AppCompatActivity {

    private TextView textViewUser;

    private FirebaseAuth auth;
    private DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnSingOut = findViewById(R.id.btnSingOut);
        Button btnIncidencias = findViewById(R.id.btnVerIncidecias);

        textViewUser = findViewById(R.id.textViewUser);


        auth = FirebaseAuth.getInstance();
        usersDatabase = FirebaseDatabase.getInstance().getReference();

        //Accion de cada boton

        btnIncidencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ReportActivity.class));
            }
        });

        //Hacer logout
        btnSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        String email = auth.getCurrentUser().getEmail();
        //Toast.makeText(this, email, Toast.LENGTH_SHORT).show();

        OneSignal.sendTag("User_ID", email);

        getUserInfo();

    }


    //Trabaja con el usuario que ha iniciado sesion
    //Pedimos a la base de datos los datos del id que ha iniciado sesion
    private void getUserInfo() {
        String id = auth.getCurrentUser().getUid();
        usersDatabase.child("Usuarios").child(id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String email = auth.getCurrentUser().getEmail();
                    String user = dataSnapshot.child("userName").getValue().toString();
                    String fullName = dataSnapshot.child("fullName").getValue().toString();
                    textViewUser.setText(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(HomeActivity.this, "Pulse en CERRAR SESION", Toast.LENGTH_SHORT).show();
    }
}

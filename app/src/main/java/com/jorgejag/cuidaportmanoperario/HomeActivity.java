package com.jorgejag.cuidaportmanoperario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
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

    //String para el canal de notificaciones
    private static final String CHANNEL_ID = "MY_CHANNEL_ID";

    private FirebaseAuth auth;
    private DatabaseReference usersDatabase;
    private DatabaseReference reportsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createNotificationChannel();

        Button btnSingOut = findViewById(R.id.btnSingOut);
        Button btnReports = findViewById(R.id.btnVerIncidecias);
        textViewUser = findViewById(R.id.textViewUser);

        auth = FirebaseAuth.getInstance();
        usersDatabase = FirebaseDatabase.getInstance().getReference();
        reportsDatabase = FirebaseDatabase.getInstance().getReference("Incidencias");

        //Creamos un addValueEventListener sobre el nodo incidencias, cuando exista un cambio
        //en el nodo llamada a triggerNotificacion para mostrar la notificacion en la app
        reportsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                triggerNotification();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Accion de cada boton
        btnReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ReportActivity.class));
                finish();
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
                    String user = dataSnapshot.child("userName").getValue().toString();
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

    //Canal de notificaciones
    private void createNotificationChannel() {
        // Creamos el canal de Notificaciones, pero solo en el API26+
        // porque la clase NotificationChannel es nueva y no esta en la libreria de soporte
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //Disparador de notificacion
    private void triggerNotification() {
        //Creamos un intent explicito
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                .setContentTitle("Cuida Portman Notificacion")
                .setContentText("Nueva incidencia o incidencia modificada")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //el notification id es unico para cada notificacion
        notificationManager.notify(1, builder.build());
    }
}

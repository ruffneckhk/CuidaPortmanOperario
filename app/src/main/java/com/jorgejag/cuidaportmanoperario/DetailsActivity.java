package com.jorgejag.cuidaportmanoperario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private ImageView imageView;
    private TextView textView;
    private Button btnResolve;

    private String resolve;

    private DatabaseReference database;
    private DatabaseReference databaseComent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        imageView = findViewById(R.id.imageViewFull);
        textView = findViewById(R.id.txtCommentFull);
        btnResolve = findViewById(R.id.btnResolve);

        resolve = "Solucionado -> ";

        database = FirebaseDatabase.getInstance().getReference("Incidencias");
        databaseComent = database.getRef().child("coment");

        //Intent con la informacion de la incidencia
        Intent intent = getIntent();
        final String comentTextView = intent.getStringExtra("comment");
        byte[] bytes = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        textView.setText(comentTextView);
        imageView.setImageBitmap(bitmap);

        btnResolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                            String coment = zoneSnapshot.child("comment").getValue(String.class);
                            Log.i(TAG, zoneSnapshot.child("comment").getValue(String.class));

                            if (coment.equalsIgnoreCase(comentTextView)) {
                                zoneSnapshot.child("comment").getRef().setValue(resolve + comentTextView);
                                Toast.makeText(DetailsActivity.this, "Incidencia Solucionada", Toast.LENGTH_SHORT).show();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(DetailsActivity.this, HomeActivity.class));
                                        finish();
                                    }
                                }, 3000);


                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "onCancelled", databaseError.toException());
                    }
                });

            }
        });

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(DetailsActivity.this, ReportActivity.class));
        finish();
    }
}
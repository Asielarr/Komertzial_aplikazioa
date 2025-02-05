package com.example.komertzial_aplikazioa;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText editerabiltzailea, editPasahitza;
    private Button buttonhasi;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editerabiltzailea = findViewById(R.id.editerabiltzailea);
        editPasahitza = findViewById(R.id.editPasahitza);
        buttonhasi = findViewById(R.id.buttonhasi);

        dbHelper = new DatabaseHelper(this);

        // Eguneratuta daudela egiaztatu
        dbHelper.getWritableDatabase();


        buttonhasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String erabiltzailea = editerabiltzailea.getText().toString().trim();
                String pasahitza = editPasahitza.getText().toString().trim();

                if (erabiltzailea.isEmpty() || pasahitza.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Eremu guztiak bete behar dira", Toast.LENGTH_SHORT).show();
                } else {
                    if (kredentzialakegiaztatu(erabiltzailea, pasahitza)) {

                        Toast.makeText(MainActivity.this, "Ongi etorri, " + erabiltzailea, Toast.LENGTH_SHORT).show();
                        redirigirASarrera(erabiltzailea);
                    } else {
                        Toast.makeText(MainActivity.this, "Erabiltzailea edo pasahitza okerra", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Erabiltzaile eta pasahitza egokiak sartu direla egiaztatzen du
    private boolean kredentzialakegiaztatu(String erabiltzailea, String pasahitza) {
        Cursor cursor = dbHelper.LoginKontsulta(erabiltzailea, pasahitza);

        boolean resultado = cursor.getCount() > 0;
        cursor.close();
        return resultado;
    }

    // Hurrengo orrialdera bidaltzen du erabiltzailea eramanez
    private void redirigirASarrera(String nombreUsuario) {
        Intent intent = new Intent(MainActivity.this, Sarrera.class);

        intent.putExtra("nombreUsuario", nombreUsuario);

        startActivity(intent);
        finish();
    }

}

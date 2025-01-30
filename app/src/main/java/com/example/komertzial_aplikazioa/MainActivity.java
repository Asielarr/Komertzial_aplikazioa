package com.example.komertzial_aplikazioa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar elementos de UI
        editerabiltzailea = findViewById(R.id.editerabiltzailea);
        editPasahitza = findViewById(R.id.editPasahitza);
        buttonhasi = findViewById(R.id.buttonhasi);

        // Inicializar Base de Datos
        dbHelper = new DatabaseHelper(this);

        // Comprobar que las tablas y columnas están actualizadas
        dbHelper.getWritableDatabase(); // Esto llamará a onUpgrade si es necesario

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);

        // Verificar si ya hay una sesión iniciada
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            redirigirASarrera();
        }

        // Evento de botón para iniciar sesión
        buttonhasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String erabiltzailea = editerabiltzailea.getText().toString().trim();
                String pasahitza = editPasahitza.getText().toString().trim();

                if (erabiltzailea.isEmpty() || pasahitza.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Eremu guztiak bete behar dira", Toast.LENGTH_SHORT).show();
                } else {
                    if (verificarCredenciales(erabiltzailea, pasahitza)) {
                        // Guardar sesión
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("erabiltzailea", erabiltzailea);
                        editor.apply();

                        Toast.makeText(MainActivity.this, "Ongi etorri, " + erabiltzailea, Toast.LENGTH_SHORT).show();
                        redirigirASarrera();
                    } else {
                        Toast.makeText(MainActivity.this, "Erabiltzailea edo pasahitza okerra", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Método para verificar credenciales en la base de datos
    private boolean verificarCredenciales(String erabiltzailea, String pasahitza) {
        Cursor cursor = dbHelper.obtenerUsuarioPorNombreYPass(erabiltzailea, pasahitza);

        boolean resultado = cursor.getCount() > 0;
        cursor.close();
        return resultado;
    }

    // Método para redirigir a la actividad SarreraActivity
    private void redirigirASarrera() {
        Intent intent = new Intent(MainActivity.this, Sarrera.class);
        startActivity(intent);
        finish(); // Cierra esta actividad
    }
    protected void onPause() {
        super.onPause();

        // Eliminar el estado de sesión cuando la actividad se pause (esto se ejecutará cuando el usuario cierre la app)
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);  // Cambiamos el estado a no logueado
        editor.putString("erabiltzailea", "");   // Limpiamos el nombre de usuario
        editor.apply();
    }
}

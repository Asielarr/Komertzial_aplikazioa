package com.example.komertzial_aplikazioa;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;






public class Sarrera extends AppCompatActivity {
    Button btnMenu;
    WebView webView ;
    TextView txterab;
    private DatabaseHelper db;
    private SharedPreferences sharedPreferences;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sarrera);
        // Inicializamos el TextView
        txterab = findViewById(R.id.txterab);
        db = new DatabaseHelper(this);
        Intent intent = getIntent();
        // Recuperar el nombre de usuario del Intent
        String nombreUsuario = intent.getStringExtra("nombreUsuario");

        // Verificar si el nombre de usuario existe y mostrarlo en el TextView
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            txterab.setText(nombreUsuario);
        } else {
            txterab.setText("Erabiltzailea ez da aurkitu");
        }

        btnMenu = findViewById(R.id.btnMenu);
        webView = findViewById(R.id.mapa);


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        String mapUrl = "https://www.openstreetmap.org/export/embed.html?bbox=-2.068561,43.149192,-2.0700,43.1450&layer=mapnik";
        webView.loadUrl(mapUrl);
        btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(Sarrera.this, v);
            popup.getMenu().add("Agenda");
            popup.getMenu().add("Partner kudeaketa");
            popup.getMenu().add("Eskaerak");


            popup.setOnMenuItemClickListener(item -> {
                Toast.makeText(Sarrera.this, "Seleccionaste: " + item.getTitle(), Toast.LENGTH_SHORT).show();


                // Lógica para cargar la actividad correspondiente
                switch (item.getTitle().toString()) {
                    case "Agenda":

                        // Realizamos la consulta para obtener el ID del usuario desde la base de datos
                        int userId = db.obtenerUserIdPorNombre(nombreUsuario);

                        // Si obtenemos el ID correctamente, pasamos al Intent con el ID del usuario
                        if (userId != -1) {
                            Intent agendaIntent = new Intent(Sarrera.this, AgendaActivity.class);
                            agendaIntent.putExtra("user_id", userId);  // Pasamos el ID del usuario
                            startActivity(agendaIntent);
                        } else {
                            Toast.makeText(Sarrera.this, "No se pudo obtener el ID del usuario", Toast.LENGTH_SHORT).show();
                        }
                        break;


                    case "Partner kudeaketa":


                        //Intent partnerIntent = new Intent(Sarrera.this, PartnerActivity.class);
                        //startActivity(partnerIntent);
                        break;


                    case "Eskaerak":


                        //Intent eskaerakIntent = new Intent(Sarrera.this, EskaerakActivity.class);
                        //startActivity(eskaerakIntent);
                        break;
                }


                return true;
            });


            popup.show();
        });
    }

}

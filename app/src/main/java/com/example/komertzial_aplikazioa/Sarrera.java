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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sarrera);
        txterab = findViewById(R.id.txterab);
        db = new DatabaseHelper(this);
        Intent intent = getIntent();
        // Erabiltzaile izena hartu
        String nombreUsuario = intent.getStringExtra("nombreUsuario");

        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            txterab.setText(nombreUsuario);
        } else {
            txterab.setText("Erabiltzailea ez da aurkitu");
        }

        btnMenu = findViewById(R.id.btnMenu);
        webView = findViewById(R.id.mapa);
        int userId = db.obtenerUserIdPorNombre(nombreUsuario);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        String mapUrl = "https://www.openstreetmap.org/export/embed.html?bbox=-2.068561,43.149192,-2.0700,43.1450&layer=mapnik";
        webView.loadUrl(mapUrl);
        btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(Sarrera.this, v);
            popup.getMenu().add("Agenda");
            popup.getMenu().add("Partner kudeaketa");
            popup.getMenu().add("Eskaerak");
            popup.getMenu().add("Komertzialak eguneratu");



            popup.setOnMenuItemClickListener(item -> {
                Toast.makeText(Sarrera.this, "Seleccionaste: " + item.getTitle(), Toast.LENGTH_SHORT).show();

                switch (item.getTitle().toString()) {
                    case "Agenda":

                        if (userId != -1) {
                            Intent agendaIntent = new Intent(Sarrera.this, AgendaActivity.class);
                            agendaIntent.putExtra("user_id", userId);
                            agendaIntent.putExtra("user_name", nombreUsuario);
                            startActivity(agendaIntent);
                        } else {
                            Toast.makeText(Sarrera.this, "Ezin izan da ID-a lortu", Toast.LENGTH_SHORT).show();
                        }
                        break;


                    case "Partner kudeaketa":
                        if (userId != -1) {
                        Intent partnerIntent = new Intent(Sarrera.this, PartnerActivity.class);
                        partnerIntent.putExtra("user_id", userId);
                        startActivity(partnerIntent);
                        } else {
                            Toast.makeText(Sarrera.this, "Ezin izan da ID-a lortu", Toast.LENGTH_SHORT).show();
                        }
                        break;


                    case "Eskaerak":


                        //Intent eskaerakIntent = new Intent(Sarrera.this, EskaerakActivity.class);
                        //startActivity(eskaerakIntent);
                        break;

                    case "Komertzialak eguneratu":
                        Intent eguneraketaintent = new Intent(Sarrera.this, xml_inportatu.class);
                        startActivity(eguneraketaintent);
                }


                return true;
            });


            popup.show();
        });
    }

}

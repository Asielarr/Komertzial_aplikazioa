package com.example.komertzial_aplikazioa;
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
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sarrera);
        // Inicializamos el TextView
        txterab = findViewById(R.id.txterab);

        // Inicializamos el SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);

        // Obtener el nombre de usuario desde SharedPreferences
        String nombreUsuario = sharedPreferences.getString("erabiltzailea", "Usuario no encontrado");


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


                        Intent agendaIntent = new Intent(Sarrera.this, AgendaActivity.class);
                        startActivity(agendaIntent);
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

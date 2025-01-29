package com.example.komertzial_aplikazioa;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;



public class Sarrera extends AppCompatActivity {
    Button btnMenu;
    WebView webViewMapa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sarrera);
        // Inicializar el WebView
        webViewMapa = findViewById(R.id.mapa);

        // Habilitar JavaScript en WebView
        WebSettings webSettings = webViewMapa.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Forzar WebView a abrirse en la app en lugar del navegador
        webViewMapa.setWebViewClient(new WebViewClient());

        // Cargar el mapa con la ubicación de Móstoles
        String urlMapa = "https://www.google.com/maps?q=Móstoles,España&output=embed";
        webViewMapa.loadUrl(urlMapa);

        btnMenu = findViewById(R.id.btnMenu);
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

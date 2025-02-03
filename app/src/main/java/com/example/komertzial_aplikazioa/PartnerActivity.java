package com.example.komertzial_aplikazioa;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PartnerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PartnerAdapter partnerAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);

        recyclerView = findViewById(R.id.recypartner);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<Partner> partnerList = databaseHelper.getAllPartners();

        partnerAdapter = new PartnerAdapter(partnerList);
        recyclerView.setAdapter(partnerAdapter);
    }
}

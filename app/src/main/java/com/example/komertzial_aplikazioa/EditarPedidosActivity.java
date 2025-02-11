package com.example.komertzial_aplikazioa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EditarPedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PedidoAdapter pedidoAdapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_pedidos);

        recyclerView = findViewById(R.id.recyclerViewPedidos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseHelper(this);
        KargatuEskaerak();
    }

    //Eskaerak kargatzeko metodoa
    private void KargatuEskaerak() {
        List<Eskaera> listaPedidos = db.LortuEskaeraGuztiak(this);

        if (listaPedidos.isEmpty()) {
            Toast.makeText(this, "Ez dago eskaerarik", Toast.LENGTH_SHORT).show();
        } else {
            pedidoAdapter = new PedidoAdapter(listaPedidos, pedido -> {
                Intent intent = new Intent(EditarPedidosActivity.this, DetallePedidoActivity.class);
                intent.putExtra("pedido_id", pedido.getCodigoProducto());
                startActivity(intent);
            });
            recyclerView.setAdapter(pedidoAdapter);
        }
    }
}

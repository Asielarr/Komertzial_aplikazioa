package com.example.komertzial_aplikazioa;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetallePedidoActivity extends AppCompatActivity {

    private EditText editTextCantidad;
    private Button btnGuardar, btnEliminar;
    private DatabaseHelper db;
    private int pedidoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido);

        editTextCantidad = findViewById(R.id.editTextCantidad);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);
        db = new DatabaseHelper(this);


        //Lortu Eskaeraren Id-a
        pedidoId = getIntent().getIntExtra("pedido_id", -1);

        if (pedidoId != -1) {
            Eskaera pedido = db.obtenerPedidoPorId(this,pedidoId);
            if (pedido != null) {
                editTextCantidad.setText(String.valueOf(pedido.getCantidad()));
            }
        }

//Eskaerak adlatzeko botoia
        btnGuardar.setOnClickListener(v -> {
            int nuevaCantidad = Integer.parseInt(editTextCantidad.getText().toString());
            db.actualizarCantidadEnXml(this,pedidoId, nuevaCantidad);
            Toast.makeText(this, "Eskaera Eguneratua", Toast.LENGTH_SHORT).show();
            finish();
        });

//Eskaerak ezabatzeko botoia
        btnEliminar.setOnClickListener(v -> {
            db.eliminarPedido(pedidoId);
            Toast.makeText(this, "Eskaera Ezabatua", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

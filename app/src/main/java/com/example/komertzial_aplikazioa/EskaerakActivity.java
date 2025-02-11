package com.example.komertzial_aplikazioa;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EskaerakActivity extends AppCompatActivity implements ProductoAdapter.OnPedidoListener {

    private Button buttonGordeEskaerak;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eskaerak);

        db = new DatabaseHelper(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lortu eskaerak Datu Basetikan
        List<Producto> produktuak = db.LortuProduktuak();
        Log.d("Produktuak", "Lortutako Produktuak: " + produktuak.size());
        if (produktuak.isEmpty()) {
            //Zerredna hutsik badagakgo produktuak gehitu
            db.GehituProduktuak("Producto 1", 19.99, 100);
            db.GehituProduktuak("Producto 2", 24.99, 150);
            db.GehituProduktuak("Producto 3", 29.99, 200);
            produktuak = db.LortuProduktuak();
        }


        ProductoAdapter adapter = new ProductoAdapter(produktuak, this, this);
        recyclerView.setAdapter(adapter);


        buttonGordeEskaerak = findViewById(R.id.buttonGuardarPedidos);



        buttonGordeEskaerak.setOnClickListener(v -> guardarPedidosEnXML());
    }

    private void guardarPedidosEnXML() {
        List<Eskaera> eskaerak = LortuEskaerakDB();
        if (eskaerak.isEmpty()) {
            Toast.makeText(this, "Ez dago eskaerarik gordetzeko", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
//XML a gordeko den tokiaren direktorioa
            File downloadsDirectory = new File(getFilesDir(), "Downloads");
            if (!downloadsDirectory.exists()) {
                downloadsDirectory.mkdirs();
            }

            //Sortu fitxategia
            File archivoXML = new File(downloadsDirectory, "pedidos.xml");
            FileOutputStream fos = new FileOutputStream(archivoXML);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            xmlSerializer.setOutput(fos, "UTF-8");

            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, "Pedidos");

            // Gorde eskaera bakoitza
            for (Eskaera eskaera : eskaerak) {
                xmlSerializer.startTag(null, "Eskaera");

                xmlSerializer.startTag(null, "CodigoProducto");
                xmlSerializer.text(String.valueOf(eskaera.getCodigoProducto()));
                xmlSerializer.endTag(null, "CodigoProducto");

                xmlSerializer.startTag(null, "NombreProducto");
                xmlSerializer.text(eskaera.getNombreProducto());
                xmlSerializer.endTag(null, "NombreProducto");

                xmlSerializer.startTag(null, "Precio");
                xmlSerializer.text(String.valueOf(eskaera.getPrecio()));
                xmlSerializer.endTag(null, "Precio");

                xmlSerializer.startTag(null, "Cantidad");
                xmlSerializer.text(String.valueOf(eskaera.getCantidad()));
                xmlSerializer.endTag(null, "Cantidad");

                xmlSerializer.startTag(null, "EstadoPedido");
                xmlSerializer.text(eskaera.getEstadoPedido());
                xmlSerializer.endTag(null, "EstadoPedido");

                xmlSerializer.startTag(null, "IdComercial");
                xmlSerializer.text(String.valueOf(eskaera.getIdComercial()));
                xmlSerializer.endTag(null, "IdComercial");

                xmlSerializer.startTag(null, "IdPartner");
                xmlSerializer.text(String.valueOf(eskaera.getIdPartner()));
                xmlSerializer.endTag(null, "IdPartner");


                String fechaPedido = eskaera.getFechaPedido();
                if (fechaPedido == null) {
                    fechaPedido = "Ez dago eskuragarri";
                }

                xmlSerializer.startTag(null, "FechaPedido");
                xmlSerializer.text(fechaPedido);
                xmlSerializer.endTag(null, "FechaPedido");

                xmlSerializer.startTag(null, "Total");
                xmlSerializer.text(String.valueOf(eskaera.getTotal()));
                xmlSerializer.endTag(null, "Total");


                String direccionEnvio = eskaera.getDireccionEnvio();
                if (direccionEnvio == null) {
                    direccionEnvio = "Ez dago eskuragarri";
                }

                xmlSerializer.startTag(null, "DireccionEnvio");
                xmlSerializer.text(direccionEnvio);
                xmlSerializer.endTag(null, "DireccionEnvio");

                xmlSerializer.endTag(null, "Eskaera");
            }

            xmlSerializer.endTag(null, "Pedidos");
            xmlSerializer.endDocument();
            fos.close();

            Toast.makeText(this, "Eskaerak Gorde Dira: " + archivoXML.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Errorea XML-a Gordetzean", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Eskaera> LortuEskaerakDB() {
        List<Eskaera> EskaeraZerrenda = new ArrayList<>();
        SQLiteDatabase database = db.getReadableDatabase();

        // Eskaerak Lortzeko datubasea
        String query = "SELECT g.codigo_pedido, g.direccion_envio, g.fecha, g.estado, g.id_comercial, g.id_partner, " +
                "x.codigo_producto, x.precio_x_unidad, x.total, p.nombre, x.cantidad " +
                "FROM Eskaera_Goiburua g " +
                "JOIN Eskaera_Xehetasuna x ON g.codigo_pedido = x.codigo_pedido " +
                "JOIN Produktua p ON x.codigo_producto = p.codigo";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int codigoPedido = cursor.getInt(0);
                String direccionEnvio = cursor.getString(1);
                String fechaPedido = cursor.getString(2);
                String estadoPedido = cursor.getString(3);
                int idComercial = cursor.getInt(4);
                int idPartner = cursor.getInt(5);
                int codigoProducto = cursor.getInt(6);
                double precio = cursor.getDouble(7);
                float total = cursor.getFloat(8);
                String nombreProducto = cursor.getString(9);
                int cantidad = cursor.getInt(10);

                Eskaera eskaera = new Eskaera(codigoProducto, nombreProducto, precio, cantidad,
                        estadoPedido, idComercial, idPartner,
                        direccionEnvio, fechaPedido, total);

                EskaeraZerrenda.add(eskaera);

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return EskaeraZerrenda;
    }

    @Override
    public void onPedidoRealizado(Eskaera pedido) {

        db.insertarPedido(pedido);
        Log.d("Pedido", "Eskaera Eginda: " + pedido.getNombreProducto());
        Toast.makeText(this, "Eskaera Eginda: " + pedido.getNombreProducto(), Toast.LENGTH_SHORT).show();
    }
}

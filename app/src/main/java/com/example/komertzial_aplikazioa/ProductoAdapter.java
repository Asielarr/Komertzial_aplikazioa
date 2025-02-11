package com.example.komertzial_aplikazioa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> produktuak; //Erakutsiko diren produktuen zerrenda
    private Context context;
    private OnPedidoListener listener;


    public interface OnPedidoListener {
        void onPedidoRealizado(Eskaera pedido);
    }


    //Eraikitzailea
    public ProductoAdapter(List<Producto> produktuak, Context context, OnPedidoListener listener) {
        this.produktuak = produktuak;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(itemView);
    }

/**
 * RecyclerView batean bisiten zerrenda erakusteko egokigailua.
 * */

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto produktua = produktuak.get(position);
        holder.IzenaTextView.setText(produktua.getIzena());
        holder.prezioTextView.setText(String.format("$%.2f", produktua.getPrezio()));

        // Kargatu Argazkia
        String IzenaArgazki = "p" + produktua.getId();
        int resourceId = context.getResources().getIdentifier(IzenaArgazki, "drawable", context.getPackageName());
        holder.imagenImageView.setImageResource(resourceId != 0 ? resourceId : R.drawable.p1);

        // Eskatu Botoia
        holder.buttonEskatu.setOnClickListener(v -> {
            String cantidadTexto = holder.cantidadEditText.getText().toString().trim();

            if (!cantidadTexto.isEmpty()) {
                int cantidad = Integer.parseInt(cantidadTexto);
                String nombreProducto = produktua.getIzena();
                double precio = produktua.getPrezio();
                int codigoProducto = produktua.getId();
                String estadoPedido = "Pendiente";
                int idComercial = 1;
                int idPartner = 1;

                //Eskaera berri abt sortu
                Eskaera EskaeraBerria = new Eskaera(codigoProducto, nombreProducto, precio, cantidad, estadoPedido, idComercial, idPartner);

                if (listener != null) {
                    listener.onPedidoRealizado(EskaeraBerria);
                }
            } else {
                holder.cantidadEditText.setError("Sartu kantitate Egoki Bat");
            }
        });
    }

    // Zerrendan dauden produktuen kopurua itzultzen du
    @Override
    public int getItemCount() {
        return produktuak.size();
    }

    // Zerrendako elementu bakoitza irudikatzen duen barne-klasea
    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView IzenaTextView, prezioTextView;
        ImageView imagenImageView;
        EditText cantidadEditText;
        Button buttonEskatu;

        public ProductoViewHolder(View itemView) {
            super(itemView);
            IzenaTextView = itemView.findViewById(R.id.textViewNombre);
            prezioTextView = itemView.findViewById(R.id.textViewPrecio);
            imagenImageView = itemView.findViewById(R.id.imageViewProducto);
            cantidadEditText = itemView.findViewById(R.id.editTextNumber);
            buttonEskatu = itemView.findViewById(R.id.button2);
        }
    }
}

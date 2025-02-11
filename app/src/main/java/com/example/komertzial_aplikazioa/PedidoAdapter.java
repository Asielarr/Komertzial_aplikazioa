package com.example.komertzial_aplikazioa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private final List<Eskaera> ZerrendaEskaerak; //Eskaeren errenda
    private final OnPedidoClickListener listener;

    public interface OnPedidoClickListener {
        void onPedidoClick(Eskaera pedido);
    }

    public PedidoAdapter(List<Eskaera> listaPedidos, OnPedidoClickListener listener) {
        this.ZerrendaEskaerak = listaPedidos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Eskaera eskaera = ZerrendaEskaerak.get(position);
        holder.textNombre.setText(eskaera.getNombreProducto());
        holder.textCantidad.setText("Kantitatea: " + eskaera.getCantidad());

        holder.itemView.setOnClickListener(v -> listener.onPedidoClick(eskaera));
    }

    @Override
    public int getItemCount() {
        return ZerrendaEskaerak.size();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView textNombre, textCantidad;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombre = itemView.findViewById(R.id.textNombre);
            textCantidad = itemView.findViewById(R.id.textCantidad);
        }
    }
}

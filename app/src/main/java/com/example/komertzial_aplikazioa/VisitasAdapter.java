package com.example.komertzial_aplikazioa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VisitasAdapter extends RecyclerView.Adapter<VisitasAdapter.ViewHolder> {

    public List<Visita> visitasList;

    public VisitasAdapter(List<Visita> visitasList) {
        this.visitasList = visitasList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visita, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Visita visita = visitasList.get(position);
        holder.txtTitulo.setText(visita.getTitulo());
        holder.txtDetalles.setText(visita.getDetalles());
    }

    @Override
    public int getItemCount() {
        return visitasList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtDetalles;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDetalles = itemView.findViewById(R.id.txtDetalles);
        }
    }
}


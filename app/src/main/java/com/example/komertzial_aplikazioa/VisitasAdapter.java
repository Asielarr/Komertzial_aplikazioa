package com.example.komertzial_aplikazioa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class VisitasAdapter extends RecyclerView.Adapter<VisitasAdapter.VisitaViewHolder> {

    private List<Visita> visitasList;
    private OnVisitaDeletedListener deleteListener;

    //Bisita ezabatzeko bista deitzen du
    public interface OnVisitaDeletedListener {
        void onVisitaDeleted(Visita visita);
    }

    public VisitasAdapter(List<Visita> visitasList, OnVisitaDeletedListener deleteListener) {
        this.visitasList = visitasList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    //Bisita ezabatzeko bista irekitzen du
    public VisitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_visita, parent, false);
        return new VisitaViewHolder(view);
    }

    //Bisita ezabatzeko bistari botoiak gehitzen dizkio
    @Override
    public void onBindViewHolder(@NonNull VisitaViewHolder holder, int position) {
        Visita visita = visitasList.get(position);
        holder.txtTituloVisita.setText(visita.getTitulo());
        holder.txtDetallesVisita.setText(visita.getDetalles());

        holder.btnEliminarVisita.setOnClickListener(v -> {
            deleteListener.onVisitaDeleted(visita);
        });
    }

    //Datuak lortzen ditu
    @Override
    public int getItemCount() {
        return visitasList.size();
    }

    public static class VisitaViewHolder extends RecyclerView.ViewHolder {
        TextView txtTituloVisita, txtDetallesVisita;
        Button btnEliminarVisita;

        public VisitaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTituloVisita = itemView.findViewById(R.id.txtTituloVisita);
            txtDetallesVisita = itemView.findViewById(R.id.txtDetallesVisita);
            btnEliminarVisita = itemView.findViewById(R.id.btnEliminarVisita);
        }
    }
}



package com.example.komertzial_aplikazioa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PartnerAdapter extends RecyclerView.Adapter<PartnerAdapter.PartnerViewHolder> {

    private List<Partner> partnerList;

    public PartnerAdapter(List<Partner> partnerList) {
        this.partnerList = partnerList;
    }

    @NonNull
    @Override
    //Partner-a sartzeko view berri bati deitzen dio
    public PartnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partner, parent, false);
        return new PartnerViewHolder(view);
    }

    @Override
    //Partner-aren datuak hartzen ditu
    public void onBindViewHolder(@NonNull PartnerViewHolder holder, int position) {
        Partner partner = partnerList.get(position);

        holder.tvIdPartner.setText(String.valueOf(partner.getPartnerId()));
        holder.tvNombre.setText(partner.getNombre());
        holder.tvDireccion.setText(partner.getDireccion());
        holder.tvTelefono.setText(partner.getTelefono());
        holder.tvEstado.setText(partner.getEstado() == 1 ? "Activo" : "Inactivo");
        holder.tvIdComercial.setText(String.valueOf(partner.getIdComercial()));
    }




    //Partner-aren datuak ezartzen ditu
    @Override
    public int getItemCount() {
        return partnerList.size();
    }

    public static class PartnerViewHolder extends RecyclerView.ViewHolder {

        private TextView tvIdPartner,tvNombre, tvDireccion, tvTelefono, tvEstado, tvIdComercial;

        public PartnerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdPartner = itemView.findViewById(R.id.tvIdPartner);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvIdComercial = itemView.findViewById(R.id.tvIdComercial);
        }

        public void bind(Partner partner) {
            tvIdPartner.setText("ID: "+partner.getPartnerId());
            tvNombre.setText("Izena: "+partner.getNombre());
            tvDireccion.setText("Helbidea: "+partner.getDireccion());
            tvTelefono.setText("Telefonoa: "+partner.getTelefono());
            tvEstado.setText(partner.getEstado() == 1 ? "Partner aktiboa" : "Baja emanda");
            tvIdComercial.setText("Komertziala: "+String.valueOf(partner.getIdComercial()));
        }
    }
}

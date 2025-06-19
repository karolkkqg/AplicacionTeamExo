package com.example.aplicacionteamexo;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionteamexo.data.modelo.notificacion.Notificacion;

import java.util.List;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.ViewHolder> {

    private List<Notificacion> lista;
    private int seleccionada = -1;

    public interface OnNotificacionClickListener {
        void onClick(Notificacion notificacion);
    }

    private final OnNotificacionClickListener listener;

    public NotificacionAdapter(List<Notificacion> lista, OnNotificacionClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public int getSeleccionada() {
        return seleccionada;
    }

    public Notificacion getNotificacionSeleccionada() {
        return seleccionada >= 0 ? lista.get(seleccionada) : null;
    }

    public void eliminarSeleccionada() {
        if (seleccionada >= 0 && seleccionada < lista.size()) {
            lista.remove(seleccionada);
            notifyItemRemoved(seleccionada);
            seleccionada = -1;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_activated_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notificacion n = lista.get(position);
        holder.text.setText(n.getMensaje());
        holder.itemView.setBackgroundColor(n.isLeida() ? Color.LTGRAY : Color.WHITE);
        holder.itemView.setActivated(position == seleccionada);

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                seleccionada = pos;
                listener.onClick(lista.get(pos));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        public ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(android.R.id.text1);
        }
    }
}

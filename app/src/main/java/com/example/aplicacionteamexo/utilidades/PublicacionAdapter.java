package com.example.aplicacionteamexo.utilidades;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionteamexo.R;
import com.example.aplicacionteamexo.data.modelo.comentario.Comentario;
import com.example.aplicacionteamexo.data.modelo.comentario.ComentarioRegistro;
import com.example.aplicacionteamexo.data.modelo.publicacion.Publicacion;
import com.example.aplicacionteamexo.data.modelo.reaccion.ReaccionRegistro;
import com.example.aplicacionteamexo.data.modelo.reaccion.ReaccionRespuesta;
import com.example.aplicacionteamexo.data.repositorio.ComentarioRepository;
import com.example.aplicacionteamexo.data.repositorio.ReaccionRepository;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder> {

    private List<Publicacion> listaPublicaciones;
    private boolean esModerador;
    private OnPublicacionInteractionListener listener;

    private ReaccionRepository reaccionRepository;
    private SharedPreferences sharedPreferences;

    public PublicacionAdapter(List<Publicacion> listaPublicaciones, boolean esModerador, OnPublicacionInteractionListener listener, Context context) {
        this.listaPublicaciones = listaPublicaciones;
        this.esModerador = esModerador;
        this.listener = listener;
        this.reaccionRepository = new ReaccionRepository();
        this.sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
    }

    // Método faltante que causa el error
    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new PublicacionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {
        TextView tvComentarios;

        Publicacion publicacion = listaPublicaciones.get(position);

        holder.txtTitulo.setText(publicacion.getTitulo());
        holder.btnEliminar.setVisibility(esModerador ? View.VISIBLE : View.GONE);

        // Listeners
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(publicacion));

        View.OnClickListener reaccionClickListener = v -> {
            final String tipo;

            if (v == holder.btnLike) tipo = "like";
            else if (v == holder.btnHeart) tipo = "heart";
            else if (v == holder.btnBroken) tipo = "broken";
            else if (v == holder.btnDislike) tipo = "dislike";
            else {
                Log.e("Reaccion", "Botón no reconocido");
                return;
            }


            int usuarioId = sharedPreferences.getInt("usuarioId", -1);
            String nombreUsuario = sharedPreferences.getString("nombreUsuario", "Anónimo");

            // Ya reaccionó
            if (tipo.equals(publicacion.getTipoReaccionUsuarioActual())) {
                // Eliminar reacción
                reaccionRepository.eliminarReaccion(publicacion.getReaccionIdUsuarioActual(), new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        publicacion.setTipoReaccionUsuarioActual(null);
                        publicacion.setReaccionIdUsuarioActual(-1);
                        notifyItemChanged(holder.getAdapterPosition());
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("Reaccion", "Error al eliminar reacción", t);
                    }
                });
            } else {
                // Crear nueva reacción
                ReaccionRegistro req = new ReaccionRegistro(
                        new Random().nextInt(1000000),
                        tipo,
                        publicacion.getIdentificador(), // Aquí lo explico abajo
                        usuarioId,
                        nombreUsuario
                );

                reaccionRepository.crearReaccion(req, new Callback<ReaccionRespuesta>() {
                    @Override
                    public void onResponse(Call<ReaccionRespuesta> call, Response<ReaccionRespuesta> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            publicacion.setTipoReaccionUsuarioActual(tipo);
                            publicacion.setReaccionIdUsuarioActual(response.body().reaccionId);
                            notifyItemChanged(holder.getAdapterPosition());
                        }
                    }

                    @Override
                    public void onFailure(Call<ReaccionRespuesta> call, Throwable t) {
                        Log.e("Reaccion", "Error al reaccionar", t);
                    }
                });
            }
        };

        holder.btnLike.setOnClickListener(reaccionClickListener);
        holder.btnHeart.setOnClickListener(reaccionClickListener);
        holder.btnBroken.setOnClickListener(reaccionClickListener);
        holder.btnDislike.setOnClickListener(reaccionClickListener);


        holder.itemView.setOnClickListener(v -> listener.onPublicacionClick(publicacion));



        holder.etComentario.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String comentario = holder.etComentario.getText().toString().trim();
                if (!comentario.isEmpty()) {
                    listener.onComentarioSubmit(publicacion, comentario);
                    holder.etComentario.setText("");
                }
                return true;
            }
            return false;
        });





        String tipoUsuario = publicacion.getTipoReaccionUsuarioActual();
        Context context = holder.itemView.getContext();

// Primero, reseteamos todos los botones
        holder.btnLike.clearColorFilter();
        holder.btnHeart.clearColorFilter();
        holder.btnBroken.clearColorFilter();
        holder.btnDislike.clearColorFilter();


// Luego aplicamos el color si hay reacción
        if (tipoUsuario != null) {
            int color = ContextCompat.getColor(context, R.color.reaccionActiva); // Define este color en colors.xml

            switch (tipoUsuario) {
                case "like":
                    holder.btnLike.setColorFilter(color);
                    break;
                case "heart":
                    holder.btnHeart.setColorFilter(color);
                    break;
                case "broken":
                    holder.btnBroken.setColorFilter(color);
                    break;
                case "dislike":
                    holder.btnDislike.setColorFilter(color);
                    break;
            }


        }

        holder.tvComentarios.setText(publicacion.getComentariosTexto().isEmpty()
                ? "Sin comentarios"
                : publicacion.getComentariosTexto());

    }

    @Override
    public int getItemCount() {
        return listaPublicaciones.size();
    }

    public static class PublicacionViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitulo;
        ImageView imgMultimedia;
        ImageButton btnLike, btnHeart, btnBroken, btnDislike, btnEliminar;
        EditText etComentario;

        TextView tvComentarios;

        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitulo = itemView.findViewById(R.id.txt_titulo);
            imgMultimedia = itemView.findViewById(R.id.img_multimedia);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnHeart = itemView.findViewById(R.id.btn_emoji_heart);
            btnBroken = itemView.findViewById(R.id.btn_emoji_broken);
            btnDislike = itemView.findViewById(R.id.btn_dislike);
            btnEliminar = itemView.findViewById(R.id.btn_delete);
            etComentario = itemView.findViewById(R.id.et_comentario);
            tvComentarios = itemView.findViewById(R.id.tv_comentarios);
        }
    }


}

package com.example.aplicacionteamexo.utilidades;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionteamexo.R;
import com.example.aplicacionteamexo.data.modelo.Usuario;
import com.example.aplicacionteamexo.data.modelo.UsuarioRespuestaBusqueda;
import com.example.aplicacionteamexo.data.modelo.comentario.Comentario;
import com.example.aplicacionteamexo.data.repositorio.ComentarioRepository;
import com.example.aplicacionteamexo.data.repositorio.UsuarioRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {

    private List<Comentario> listaComentarios;
    private UsuarioRepository usuarioRepository;
    private int usuarioActualId;
    private Context context;
    private ComentarioRepository comentarioRepository;
    private ComentarioCallback callback;

    private Map<Integer, String> cacheUsuarios = new HashMap<>();

    public ComentarioAdapter(List<Comentario> listaComentarios, int usuarioActualId, Context context, ComentarioCallback callback) {
        this.listaComentarios = listaComentarios;
        this.usuarioRepository = new UsuarioRepository();
        this.usuarioActualId = usuarioActualId;
        this.context = context;
        this.comentarioRepository = new ComentarioRepository();
        this.callback = callback;
    }

    @Override
    @NonNull
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);
        holder.tvTextoComentario.setText(comentario.getTexto());

        int usuarioId = comentario.getUsuarioId();

        if (usuarioId == usuarioActualId) {
            holder.btnMenuComentario.setVisibility(View.VISIBLE);
            holder.btnMenuComentario.setOnClickListener(v -> mostrarPopupMenu(v, comentario, position));
        } else {
            holder.btnMenuComentario.setVisibility(View.GONE);
        }

        if (cacheUsuarios.containsKey(usuarioId)) {
            holder.tvNombreUsuario.setText(cacheUsuarios.get(usuarioId));
        } else if (usuarioId > 0) {
            usuarioRepository.obtenerUsuarioPorId(usuarioId, new Callback<UsuarioRespuestaBusqueda>() {
                @Override
                public void onResponse(Call<UsuarioRespuestaBusqueda> call, Response<UsuarioRespuestaBusqueda> response) {
                    UsuarioRespuestaBusqueda r = response.body();
                    Usuario u = (response.isSuccessful() && r != null && r.isOk()) ? r.getUsuario() : null;
                    String nombre = u != null ? u.getNombreUsuario() : "Usuario " + usuarioId;
                    cacheUsuarios.put(usuarioId, nombre);
                    holder.tvNombreUsuario.setText(nombre);
                }

                @Override
                public void onFailure(Call<UsuarioRespuestaBusqueda> call, Throwable t) {
                    holder.tvNombreUsuario.setText("Error al cargar usuario");
                }
            });
        } else {
            holder.tvNombreUsuario.setText("Usuario desconocido");
        }


    }

    private void mostrarPopupMenu(View view, Comentario comentario, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.getMenuInflater().inflate(R.menu.menu_comentario, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_editar) {
                mostrarDialogoEditar(comentario, position);
                return true;
            } else if (id == R.id.menu_eliminar) {
                eliminarComentario(comentario.getComentarioId(), position);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void mostrarDialogoEditar(Comentario comentario, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Editar comentario");

        final EditText input = new EditText(context);
        input.setText(comentario.getTexto());
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoTexto = input.getText().toString().trim();
            if (!nuevoTexto.isEmpty()) {
                comentarioRepository.actualizarComentario(comentario.getComentarioId(), nuevoTexto, new Callback<Comentario>() {
                    @Override
                    public void onResponse(Call<Comentario> call, Response<Comentario> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            comentario.setTexto(response.body().getTexto());
                            notifyItemChanged(position);
                            Toast.makeText(context, "Comentario actualizado", Toast.LENGTH_SHORT).show();

                            if (callback != null) {
                                callback.onComentarioEditado(comentario, position);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Comentario> call, Throwable t) {
                        Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }



    private void eliminarComentario(int comentarioId, int position) {
        comentarioRepository.eliminarComentario(comentarioId, new Callback<Comentario>() {
            @Override
            public void onResponse(Call<Comentario> call, Response<Comentario> response) {
                if (response.isSuccessful()) {
                    listaComentarios.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Comentario eliminado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "No se pudo eliminar el comentario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comentario> call, Throwable t) {
                Toast.makeText(context, "Error al eliminar el comentario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void actualizarComentarios(List<Comentario> nuevosComentarios) {
        this.listaComentarios = nuevosComentarios;
        notifyDataSetChanged();
    }

    public Map<Integer, String> getCacheUsuarios() {
        return cacheUsuarios;
    }


    @Override
    public int getItemCount() {
        return listaComentarios != null ? listaComentarios.size() : 0;
    }

    // ViewHolder
    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreUsuario, tvTextoComentario;
        ImageButton btnMenuComentario;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreUsuario = itemView.findViewById(R.id.tv_nombre_usuario);
            tvTextoComentario = itemView.findViewById(R.id.tv_texto_comentario);
            btnMenuComentario = itemView.findViewById(R.id.btn_menu_comentario);
        }
    }

    public interface ComentarioCallback {
        void onComentarioEditado(Comentario comentario, int position);
        void onComentarioEliminado(int comentarioId, int position);
    }

}



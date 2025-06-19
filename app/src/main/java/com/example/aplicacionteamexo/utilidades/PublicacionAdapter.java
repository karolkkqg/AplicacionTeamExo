package com.example.aplicacionteamexo.utilidades;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aplicacionteamexo.R;
import com.example.aplicacionteamexo.data.modelo.Usuario;
import com.example.aplicacionteamexo.data.modelo.UsuarioRespuestaBusqueda;
import com.example.aplicacionteamexo.data.modelo.comentario.Comentario;
import com.example.aplicacionteamexo.data.modelo.comentario.ComentarioRegistro;
import com.example.aplicacionteamexo.data.modelo.publicacion.Publicacion;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionConRecurso;
import com.example.aplicacionteamexo.data.modelo.reaccion.Reaccion;
import com.example.aplicacionteamexo.data.modelo.reaccion.ReaccionRegistro;
import com.example.aplicacionteamexo.data.modelo.reaccion.ReaccionRespuesta;
import com.example.aplicacionteamexo.data.modelo.recurso.Recurso;
import com.example.aplicacionteamexo.data.repositorio.ComentarioRepository;
import com.example.aplicacionteamexo.data.repositorio.PublicacionRepository;
import com.example.aplicacionteamexo.data.repositorio.ReaccionRepository;
import com.example.aplicacionteamexo.data.repositorio.UsuarioRepository;


import com.example.aplicacionteamexo.grpc.recurso.DescargarRecursoRequest;
import com.example.aplicacionteamexo.grpc.recurso.DescargarRecursoResponse;
import com.example.aplicacionteamexo.grpc.recurso.RecursoServiceGrpc;
import com.google.gson.Gson;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder> {
    private List<PublicacionConRecurso> listaPublicaciones;
    private boolean esModerador;
    private OnPublicacionInteractionListener listener;
    private ReaccionRepository reaccionRepository;
    private SharedPreferences sharedPreferences;

    private Map<Integer, List<Comentario>> cacheComentarios = new HashMap<>();

    private RecyclerView recyclerView;
    private ComentarioRepository comentarioRepository;

    private String rolUsuario;
    private int usuarioActualId;



    public PublicacionAdapter(List<PublicacionConRecurso> listaPublicaciones, boolean esModerador, Context context) {
        this.listaPublicaciones = listaPublicaciones;
        this.esModerador = esModerador;
        this.reaccionRepository = new ReaccionRepository();
        this.sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        this.rolUsuario = sharedPreferences.getString("rol", "fan");
        this.usuarioActualId = sharedPreferences.getInt("usuarioId", -1);
    }


    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new PublicacionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {
        Log.d("PublicacionAdapter", "onBindViewHolder llamado");

        PublicacionConRecurso publicacionConRecurso = listaPublicaciones.get(position);
        Recurso recursoMultimedia = publicacionConRecurso.getRecurso();

        holder.txtTitulo.setText(publicacionConRecurso.getTitulo());
        holder.txtContenido.setText(publicacionConRecurso.getContenido());

        if (recursoMultimedia != null && recursoMultimedia.getUrl() != null) {
            String tipo = recursoMultimedia.getTipo(); // "Foto", "Video", "Audio"
            String url = recursoMultimedia.getUrl();

            holder.imgMultimedia.setVisibility(View.GONE);
            holder.videoView.setVisibility(View.GONE);
            holder.videoControls.setVisibility(View.GONE);
            holder.audioLayout.setVisibility(View.GONE);

            //holder.resetMediaPlayer();

            if (tipo != null && tipo.trim().equalsIgnoreCase("Foto")) {
                Glide.with(holder.itemView.getContext())
                        .load(url)
                        .placeholder(R.drawable.placeholder_image)
                        .into(holder.imgMultimedia);
                holder.imgMultimedia.setVisibility(View.VISIBLE);

            } else if (tipo.trim().equalsIgnoreCase("Video")) {
                holder.videoView.setVideoPath(url);
                holder.videoView.setVisibility(View.VISIBLE);
                holder.videoControls.setVisibility(View.VISIBLE);

                holder.videoView.setOnPreparedListener(mp -> {
                    mp.setLooping(true); // opcional
                });

                holder.btnPlayVideo.setOnClickListener(v -> {
                    if (!holder.videoView.isPlaying()) {
                        holder.videoView.start();
                    }
                });

                holder.btnPauseVideo.setOnClickListener(v -> {
                    if (holder.videoView.isPlaying()) {
                        holder.videoView.pause();
                    }
                });
            } else if (tipo.trim().equalsIgnoreCase("Audio")) {
                holder.audioLayout.setVisibility(View.VISIBLE);

                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                holder.btnPlayAudio.setOnClickListener(v -> mediaPlayer.start());
                holder.btnPauseAudio.setOnClickListener(v -> mediaPlayer.pause());
            }
        }


        Publicacion publicacion = publicacionConRecurso.toPublicacion(); // ← ahora es local
        int publicacionId = publicacion.getIdentificador();
        Log.d("onBindViewHolder", "Bind de publicación ID: " + publicacionId + " en posición: " + position);

        holder.txtTitulo.setText(publicacion.getTitulo());

        configurarListenerReacciones(holder, publicacion);
        cargarReaccionesExistentes(publicacion, publicacionId, holder);
        actualizarBotonesReaccion(holder, publicacion);

        List<Comentario> comentarios = cacheComentarios.get(publicacionId);
        Context context = holder.itemView.getContext();

        if (comentarios == null) {
            ComentarioRepository comentarioRepository = new ComentarioRepository();
            comentarioRepository.obtenerComentariosPorPublicacion(publicacionId, new Callback<List<Comentario>>() {
                @Override
                public void onResponse(Call<List<Comentario>> call, Response<List<Comentario>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Comentario> comentarios = response.body();
                        cacheComentarios.put(publicacionId, comentarios);

                        ComentarioAdapter comentarioAdapter = crearComentarioAdapter(comentarios, publicacionId, context);
                        holder.rvComentarios.setLayoutManager(new LinearLayoutManager(context));
                        holder.rvComentarios.setAdapter(comentarioAdapter);
                        holder.comentarioAdapter = comentarioAdapter;

                    }
                }

                @Override
                public void onFailure(Call<List<Comentario>> call, Throwable t) {
                    Toast.makeText(context, "Error al cargar comentarios", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ComentarioAdapter comentarioAdapter = crearComentarioAdapter(comentarios, publicacionId, context);
            holder.rvComentarios.setLayoutManager(new LinearLayoutManager(context));
            holder.rvComentarios.setAdapter(comentarioAdapter);
            holder.comentarioAdapter = comentarioAdapter;


        }

        holder.btnConfirmarComentario.setOnClickListener(v -> {
            String textoComentario = holder.etComentario.getText().toString().trim();
            if (!textoComentario.isEmpty()) {
                crearComentario(publicacion, textoComentario, holder);  // Esto lo tienes bien
                Log.d("BtnComentario", "Click: publicacionId=" + publicacion.getIdentificador() + ", texto=" + textoComentario);

                holder.etComentario.setText(""); // Limpia el campo después de enviar
            } else {
                Toast.makeText(context, "El comentario no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });


        int creadorId = publicacion.getUsuarioId(); // 👈 Asegúrate de tener este método/getter

// Mostrar botón si es moderador o si es el autor de la publicación
        if (rolUsuario.equals("Moderador") || rolUsuario.equals("Administrador") || creadorId == usuarioActualId) {
            holder.btnEliminar.setVisibility(View.VISIBLE);
        } else {
            holder.btnEliminar.setVisibility(View.GONE);
        }

        holder.btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Eliminar publicación")
                    .setMessage("¿Estás seguro de que deseas eliminar esta publicación?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        eliminarPublicacion(publicacion, holder.getAdapterPosition(), holder.itemView.getContext());
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        holder.btnDescargar.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    ManagedChannel channel = ManagedChannelBuilder
                            .forAddress("192.168.100.28", 50054)
                            .usePlaintext()
                            .maxInboundMessageSize(50 * 1024 * 1024)
                            .build();

                    Log.d("DescargaRecurso", "ID que se envía para descargar: " + publicacionConRecurso.getRecurso().getIdentificador());
                    RecursoServiceGrpc.RecursoServiceBlockingStub recursoStub = RecursoServiceGrpc.newBlockingStub(channel);

                    DescargarRecursoRequest request = DescargarRecursoRequest.newBuilder()

                            .setTipo(publicacionConRecurso.getRecurso().getTipo()) // Cambia dinámicamente si sabes si es Audio/Video
                            .setIdentificador(publicacionConRecurso.getRecurso().getIdentificador()) // Asegúrate de tener este ID
                            .build();

                    DescargarRecursoResponse response = recursoStub.descargarRecurso(request);

                    byte[] archivo = response.getArchivo().toByteArray();

                    channel.shutdown();

                    // Guardar archivo y notificar en UI
                    ((Activity) holder.itemView.getContext()).runOnUiThread(() -> {
                        if (response.getExito()) {
                            guardarArchivoEnDispositivo(holder.itemView.getContext(), archivo, publicacionConRecurso.getRecurso().getTipo(), publicacionConRecurso.getRecurso().getIdentificador());
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "Error: " + response.getMensaje(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    ((Activity) holder.itemView.getContext()).runOnUiThread(() ->
                            Toast.makeText(holder.itemView.getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }

    private void eliminarPublicacion(Publicacion publicacion, int position, Context context) {
        int publicacionId = publicacion.getIdentificador();

        new PublicacionRepository().eliminarPublicacion(publicacionId, rolUsuario, usuarioActualId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            listaPublicaciones.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Publicación eliminada", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 403) {
                            Toast.makeText(context, "No tienes permisos para eliminar esta publicación", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("EliminarPub", "Código de error: " + response.code());
                            try {
                                Log.e("EliminarPub", "Error body: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(context, "Error al eliminar la publicación", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Error de red al eliminar", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void cargarReaccionesExistentes(Publicacion publicacion, int publicacionId, PublicacionViewHolder holder) {
        ReaccionRepository reaccionRepository = new ReaccionRepository();
        int usuarioIdActual = sharedPreferences.getInt("usuarioId", -1);
        reaccionRepository.obtenerReaccionesPorPublicacion(publicacionId)
                .enqueue(new Callback<List<Reaccion>>() {
                    @Override
                    public void onResponse(Call<List<Reaccion>> call, Response<List<Reaccion>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int likes = 0;
                            int dislikes = 0;
                            int emojis = 0;

                            for (Reaccion reaccion : response.body()) {
                                switch (reaccion.getTipo()) {
                                    case "like": likes++; break;
                                    case "dislike": dislikes++; break;
                                    case "emoji": emojis++; break;
                                }

                                if (reaccion.getUsuarioId() == usuarioIdActual) {
                                    publicacion.setTipoReaccionUsuarioActual(reaccion.getTipo());
                                    publicacion.setReaccionIdUsuarioActual(reaccion.getReaccionId());
                                }
                            }

                            holder.txtLikes.setText(String.valueOf(likes));
                            holder.txtDislikes.setText(String.valueOf(dislikes));
                            holder.txtEmojis.setText(String.valueOf(emojis));

                            actualizarBotonesReaccion(holder, publicacion);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Reaccion>> call, Throwable t) {
                        Toast.makeText(holder.itemView.getContext(), "Error al cargar reacciones", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void configurarListenerReacciones(@NonNull PublicacionViewHolder holder, Publicacion publicacion) {
        View.OnClickListener reaccionClickListener = v -> {
            final String tipo;

            if (v == holder.btnLike) tipo = "like";
            else if (v == holder.btnHeart) tipo = "emoji";
            else if (v == holder.btnDislike) tipo = "dislike";
            else {
                Log.e("Reaccion", "Botón no reconocido");
                return;
            }

            if (tipo.equals(publicacion.getTipoReaccionUsuarioActual())) {
                eliminarReaccion(holder, publicacion);
                publicacion.setTipoReaccionUsuarioActual(null);
            } else {
                if (publicacion.getTipoReaccionUsuarioActual() != null) {
                    actualizarReaccion(holder, publicacion, tipo);
                } else {
                    int usuarioId = sharedPreferences.getInt("usuarioId", -1);
                    String nombreUsuario = sharedPreferences.getString("nombreUsuario", "Anónimo");
                    crearReaccion(holder, publicacion, tipo, usuarioId, nombreUsuario);
                }
                publicacion.setTipoReaccionUsuarioActual(tipo);
            }
        };

        holder.btnLike.setOnClickListener(reaccionClickListener);
        holder.btnHeart.setOnClickListener(reaccionClickListener);
        holder.btnDislike.setOnClickListener(reaccionClickListener);
    }

    private void eliminarReaccion(@NonNull PublicacionViewHolder holder, Publicacion publicacion) {
        int reaccionId = publicacion.getReaccionIdUsuarioActual();

        if (reaccionId == -1) {
            Toast.makeText(holder.itemView.getContext(),
                    "No se encontró una reacción para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        reaccionRepository.eliminarReaccion(reaccionId).enqueue(new Callback<ReaccionRespuesta>() {
            @Override
            public void onResponse(Call<ReaccionRespuesta> call, Response<ReaccionRespuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    publicacion.setTipoReaccionUsuarioActual(null);
                    publicacion.setReaccionIdUsuarioActual(-1);

                    Toast.makeText(holder.itemView.getContext(),
                            "Reacción eliminada", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(holder.getAdapterPosition());
                    cargarReaccionesExistentes(publicacion, publicacion.getIdentificador(), holder);
                    actualizarBotonesReaccion(holder, publicacion);

                } else {
                    Log.e("Reaccion", "Fallo al eliminar: " + response.code());
                    Toast.makeText(holder.itemView.getContext(),
                            "No se pudo eliminar la reacción", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReaccionRespuesta> call, Throwable t) {
                Log.e("Reaccion", "Error de red al eliminar reacción", t);
                Toast.makeText(holder.itemView.getContext(),
                        "Error de red al eliminar", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void actualizarReaccion(@NonNull PublicacionViewHolder holder, Publicacion publicacion, String tipo) {
        int reaccionId = publicacion.getReaccionIdUsuarioActual();

        if (reaccionId == -1) {
            Toast.makeText(holder.itemView.getContext(),
                    "No se encontró una reacción para actualizar", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("tipo", tipo);

        reaccionRepository.actualizarReaccion(reaccionId, body).enqueue(new Callback<ReaccionRespuesta>() {
            @Override
            public void onResponse(Call<ReaccionRespuesta> call, Response<ReaccionRespuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    publicacion.setTipoReaccionUsuarioActual(tipo);
                    notifyItemChanged(holder.getAdapterPosition());

                    Toast.makeText(holder.itemView.getContext(),
                            "Reacción actualizada", Toast.LENGTH_SHORT).show();
                    cargarReaccionesExistentes(publicacion, publicacion.getIdentificador(), holder);
                    actualizarBotonesReaccion(holder, publicacion);

                } else {
                    Log.e("Reaccion", "Error al actualizar: código " + response.code());
                    Toast.makeText(holder.itemView.getContext(),
                            "No se pudo actualizar la reacción", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReaccionRespuesta> call, Throwable t) {
                Log.e("Reaccion", "Error de red al actualizar", t);
                Toast.makeText(holder.itemView.getContext(),
                        "Error de red al actualizar reacción", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void crearReaccion(@NonNull PublicacionViewHolder holder, Publicacion publicacion,
                               String tipo, int usuarioId, String nombreUsuario) {

        ReaccionRegistro req = new ReaccionRegistro(
                tipo,
                publicacion.getIdentificador(),
                usuarioId,
                nombreUsuario
        );

        reaccionRepository.crearReaccion(req).enqueue(new Callback<ReaccionRespuesta>() {
            @Override
            public void onResponse(Call<ReaccionRespuesta> call, Response<ReaccionRespuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int reaccionId = response.body().getReaccion().getReaccionId();

                    publicacion.setTipoReaccionUsuarioActual(tipo);
                    publicacion.setReaccionIdUsuarioActual(reaccionId);

                    notifyItemChanged(holder.getAdapterPosition());
                    Toast.makeText(holder.itemView.getContext(),
                            "Reacción registrada", Toast.LENGTH_SHORT).show();
                    //publicacion.setTipoReaccionUsuarioActual(tipo);
                    cargarReaccionesExistentes(publicacion, publicacion.getIdentificador(), holder);
                    actualizarBotonesReaccion(holder, publicacion);


                } else {
                    Log.e("Reaccion", "Error en respuesta: " + response.code());
                    Toast.makeText(holder.itemView.getContext(), "Error al registrar reacción", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReaccionRespuesta> call, Throwable t) {
                Log.e("Reaccion", "Error al registrar reacción", t);
                Toast.makeText(holder.itemView.getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void actualizarBotonesReaccion(@NonNull PublicacionViewHolder holder, Publicacion publicacion) {
        String tipoUsuario = publicacion.getTipoReaccionUsuarioActual();
        Context context = holder.itemView.getContext();

        holder.btnLike.clearColorFilter();
        holder.btnHeart.clearColorFilter();
        holder.btnDislike.clearColorFilter();

        if (tipoUsuario != null) {
            int color = ContextCompat.getColor(context, R.color.reaccionActiva);

            switch (tipoUsuario) {
                case "like":
                    holder.btnLike.setColorFilter(color);
                    break;
                case "emoji":
                    holder.btnHeart.setColorFilter(color);
                    break;
                case "dislike":
                    holder.btnDislike.setColorFilter(color);
                    break;
            }
        }
    }


    private void crearComentario(Publicacion publicacion, String texto, @NonNull PublicacionViewHolder holder) {
        int usuarioId = sharedPreferences.getInt("usuarioId", -1);

        if (usuarioId == -1) {
            Toast.makeText(holder.itemView.getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        ComentarioRegistro solicitud = new ComentarioRegistro(
                publicacion.getIdentificador(),
                usuarioId,
                texto
        );

        Log.d("ComentarioRegistro", "Enviando comentario: publicacionId=" + publicacion.getIdentificador() + ", usuarioId=" + usuarioId + ", texto=" + texto);

        ComentarioRepository comentarioRepository = new ComentarioRepository();
        UsuarioRepository usuarioRepository = new UsuarioRepository();

        comentarioRepository.crearComentario(solicitud, new Callback<Comentario>() {
            @Override
            public void onResponse(Call<Comentario> call, Response<Comentario> response) {
                Log.d("CrearComentario", "Código: " + response.code());
                Log.d("CrearComentario", "Body: " + new Gson().toJson(response.body()));

                if (response.isSuccessful() && response.body() != null) {
                    cacheComentarios.remove(publicacion.getIdentificador());
                    // Reconsultar la lista completa desde el servidor para evitar inconsistencias
                    comentarioRepository.obtenerComentariosPorPublicacion(publicacion.getIdentificador(), new Callback<List<Comentario>>() {
                        @Override
                        public void onResponse(Call<List<Comentario>> call, Response<List<Comentario>> responseComentarios) {
                            if (responseComentarios.isSuccessful() && responseComentarios.body() != null) {

                                List<Comentario> comentariosActualizados = responseComentarios.body();
                                cacheComentarios.put(publicacion.getIdentificador(), comentariosActualizados);

                                // Cargar nombre del usuario si no está cacheado
                                usuarioRepository.obtenerUsuarioPorId(usuarioId, new Callback<UsuarioRespuestaBusqueda>() {
                                    @Override
                                    public void onResponse(Call<UsuarioRespuestaBusqueda> call, Response<UsuarioRespuestaBusqueda> responseUsuario) {
                                        UsuarioRespuestaBusqueda r = responseUsuario.body();
                                        Usuario u = (responseUsuario.isSuccessful() && r != null && r.isOk()) ? r.getUsuario() : null;
                                        String nombre = u != null ? u.getNombreUsuario() : "Usuario " + usuarioId;

                                        // Asignar el cache de nombre si el adapter ya está
                                        if (holder.comentarioAdapter != null) {
                                            holder.comentarioAdapter.getCacheUsuarios().put(usuarioId, nombre);
                                            holder.comentarioAdapter.actualizarComentarios(comentariosActualizados);
                                        } else {
                                            ComentarioAdapter nuevoAdapter = crearComentarioAdapter(comentariosActualizados, publicacion.getIdentificador(), holder.itemView.getContext());
                                            nuevoAdapter.getCacheUsuarios().put(usuarioId, nombre);
                                            holder.rvComentarios.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                                            holder.rvComentarios.setAdapter(nuevoAdapter);
                                            holder.comentarioAdapter = nuevoAdapter;
                                        }

                                        holder.etComentario.setText("");
                                        Toast.makeText(holder.itemView.getContext(), "Comentario enviado", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<UsuarioRespuestaBusqueda> call, Throwable t) {
                                        Log.e("Comentario", "Error al obtener nombre de usuario", t);
                                        if (holder.comentarioAdapter != null) {
                                            holder.comentarioAdapter.actualizarComentarios(comentariosActualizados);
                                        }
                                        Toast.makeText(holder.itemView.getContext(), "Comentario enviado (sin nombre)", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Comentario>> call, Throwable t) {
                            Log.e("ActualizarComentarios", "Error al obtener lista de comentarios", t);
                            Toast.makeText(holder.itemView.getContext(), "Comentario enviado, pero no se actualizaron", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(holder.itemView.getContext(), "No se pudo enviar el comentario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comentario> call, Throwable t) {
                Log.e("Comentario", "Error al enviar comentario", t);
                Toast.makeText(holder.itemView.getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }





    @Override
    public int getItemCount() {
        return listaPublicaciones.size();
    }


    private ComentarioAdapter crearComentarioAdapter(List<Comentario> comentarios, int publicacionId, Context context) {
        return new ComentarioAdapter(
                comentarios,
                usuarioActualId,
                context,
                new ComentarioAdapter.ComentarioCallback() {
                    @Override
                    public void onComentarioEditado(Comentario comentarioEditado, int position) {
                        List<Comentario> cache = cacheComentarios.get(publicacionId);
                        if (cache != null && position >= 0 && position < cache.size()) {
                            cache.set(position, comentarioEditado);
                        }
                    }

                    @Override
                    public void onComentarioEliminado(int comentarioId, int position) {
                        List<Comentario> cache = cacheComentarios.get(publicacionId);
                        if (cache != null && position >= 0 && position < cache.size()) {
                            cache.remove(position);
                        }
                    }
                }
        );
    }

    private void guardarArchivoEnDispositivo(Context context, byte[] datos, String tipo, int id) {
        try {
            String extension = tipo.equalsIgnoreCase("Foto") ? ".jpg"
                    : tipo.equalsIgnoreCase("Video") ? ".mp4"
                    : tipo.equalsIgnoreCase("Audio") ? ".mp3"
                    : ".bin";

            File file = new File(context.getExternalFilesDir(null), "recurso_" + id + extension);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(datos);
            fos.close();

            Log.i("Descarga", "Archivo guardado en: " + file.getAbsolutePath());
            Toast.makeText(context, "Archivo guardado: " + file.getName(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Log.e("Descarga", "Error al guardar archivo: " + e.getMessage());
            Toast.makeText(context, "Error al guardar archivo", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public static class PublicacionViewHolder extends RecyclerView.ViewHolder {
        ComentarioAdapter comentarioAdapter;

        TextView txtTitulo;
        TextView txtContenido;
        ImageView imgMultimedia;
        ImageButton btnLike, btnHeart, btnDislike, btnEliminar;
        ImageButton btnConfirmarComentario;
        EditText etComentario;
        RecyclerView rvComentarios;

        TextView txtLikes, txtEmojis, txtDislikes;

        ImageButton btnDescargar;

        public VideoView videoView;
        public LinearLayout audioLayout;
        public ImageButton btnPlayAudio;
        public ImageButton btnPauseAudio;
        public LinearLayout videoControls;
        public ImageButton btnPlayVideo;
        public ImageButton btnPauseVideo;

        public MediaPlayer mediaPlayer;


        public PublicacionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txt_titulo);
            txtContenido = itemView.findViewById(R.id.txt_contenido);
            imgMultimedia = itemView.findViewById(R.id.img_multimedia);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnHeart = itemView.findViewById(R.id.btn_emoji_heart);
            btnDislike = itemView.findViewById(R.id.btn_dislike);
            btnEliminar = itemView.findViewById(R.id.btn_delete);
            btnDescargar = itemView.findViewById(R.id.btn_descargar);
            etComentario = itemView.findViewById(R.id.et_comentario);
            btnConfirmarComentario = itemView.findViewById(R.id.btn_confirmar_comentario);
            rvComentarios = itemView.findViewById(R.id.rv_comentarios);

            txtLikes = itemView.findViewById(R.id.txt_likes);
            txtEmojis = itemView.findViewById(R.id.txt_emojis);
            txtDislikes = itemView.findViewById(R.id.txt_dislikes);

            videoView = itemView.findViewById(R.id.videoView);
            audioLayout = itemView.findViewById(R.id.audioLayout);
            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudio);
            btnPauseAudio = itemView.findViewById(R.id.btnPauseAudio);

            videoView = itemView.findViewById(R.id.videoView);
            videoControls = itemView.findViewById(R.id.videoControls);
            btnPlayVideo = itemView.findViewById(R.id.btnPlayVideo);
            btnPauseVideo = itemView.findViewById(R.id.btnPauseVideo);

            mediaPlayer = new MediaPlayer();

            // Configura listeners una vez
            btnPlayAudio.setOnClickListener(v -> {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            });

            btnPauseAudio.setOnClickListener(v -> {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            });
        }

        public void resetMediaPlayer() {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
        }


    }
}



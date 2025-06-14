package com.example.aplicacionteamexo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionteamexo.data.modelo.comentario.Comentario;
import com.example.aplicacionteamexo.data.modelo.comentario.ComentarioRegistro;
import com.example.aplicacionteamexo.data.modelo.publicacion.Publicacion;
import com.example.aplicacionteamexo.data.repositorio.ComentarioRepository;
import com.example.aplicacionteamexo.data.repositorio.PublicacionRepository;
import com.example.aplicacionteamexo.utilidades.OnPublicacionInteractionListener;
import com.example.aplicacionteamexo.utilidades.PublicacionAdapter;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaPerfil extends AppCompatActivity implements OnPublicacionInteractionListener {

    private TextView tvNombreUsuario;
    private Button btnEditarPerfil, btnSubirPublicacion;
    private RecyclerView recyclerPublicaciones;
    private PublicacionAdapter publicacionAdapter;
    private List<Publicacion> listaPosts;
    private boolean esModerador;
    private int usuarioId;
    private PublicacionRepository publicacionRepository;
    private List<Publicacion> listaPublicaciones;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_perfil);

        publicacionRepository = new PublicacionRepository();
        obtenerDatosUsuario();
        inicializarVistas();
        configurarBotones();
        cargarPublicacionesUsuario();
    }

    private void obtenerDatosUsuario() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String nombreUsuario = prefs.getString("nombreUsuario", "Usuario");
        String rolUsuario = prefs.getString("rol", "fan");

        usuarioId = prefs.getInt("usuarioId", -1);
        esModerador = rolUsuario.equalsIgnoreCase("moderador");
        tvNombreUsuario.setText(nombreUsuario);
    }

    private void inicializarVistas() {
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnSubirPublicacion = findViewById(R.id.btnSubirPublicacion);
        recyclerPublicaciones = findViewById(R.id.recyclerPublicaciones);

        Button btnVerEstadisticas = findViewById(R.id.btnVerEstadisticas);
        btnVerEstadisticas.setVisibility(esModerador ? View.VISIBLE : View.GONE);
    }

    private void configurarBotones() {
        ImageButton btnAtras = findViewById(R.id.btnVolver);
        btnAtras.setOnClickListener(v -> finish());

        btnEditarPerfil.setOnClickListener(v ->
                Toast.makeText(this, "Editar perfil aún no implementado", Toast.LENGTH_SHORT).show());

        btnSubirPublicacion.setOnClickListener(v -> {
            Intent intent = new Intent(this, PantallaPublicacion.class);
            intent.putExtra("usuarioId", usuarioId);
            startActivity(intent);
        });

        findViewById(R.id.btnVerEstadisticas).setOnClickListener(v ->
                Toast.makeText(this, "Ver estadísticas (pendiente)", Toast.LENGTH_SHORT).show());
    }

    private void cargarPublicacionesUsuario() {
        publicacionRepository.obtenerPorUsuario(usuarioId).enqueue(new Callback<List<Publicacion>>() {
            @Override
            public void onResponse(Call<List<Publicacion>> call, Response<List<Publicacion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaPosts = response.body();
                    publicacionAdapter = new PublicacionAdapter(listaPosts, esModerador, PantallaPerfil.this, PantallaPerfil.this);
                    recyclerPublicaciones.setLayoutManager(new LinearLayoutManager(PantallaPerfil.this));
                    recyclerPublicaciones.setAdapter(publicacionAdapter);
                } else {
                    Toast.makeText(PantallaPerfil.this, "Error al obtener publicaciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Publicacion>> call, Throwable t) {
                Toast.makeText(PantallaPerfil.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Implementación de OnPublicacionInteractionListener
    @Override
    public void onEliminarClick(Publicacion publicacion) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar publicación")
                .setMessage("¿Estás seguro de eliminar esta publicación?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarPublicacion(publicacion))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onLikeClick(Publicacion publicacion) {
        Toast.makeText(this, "Like a: " + publicacion.getTitulo(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReaccionClick(Publicacion publicacion, String tipoReaccion) {
        Toast.makeText(this, "Reacción " + tipoReaccion, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onComentarioSubmit(Publicacion publicacion, String comentario) {
        int usuarioId = sharedPreferences.getInt("usuarioId", -1);
        int comentarioId = new Random().nextInt(1000000);

        ComentarioRegistro solicitud = new ComentarioRegistro(
                comentarioId,
                publicacion.getIdentificador(),
                usuarioId,
                comentario
        );

        ComentarioRepository comentarioRepo = new ComentarioRepository();
        comentarioRepo.crearComentario(solicitud, new Callback<Comentario>() {
            @Override
            public void onResponse(Call<Comentario> call, Response<Comentario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PantallaPerfil.this, "Comentario publicado", Toast.LENGTH_SHORT).show();

                    // Agregar comentario localmente en el modelo
                    publicacion.agregarComentarioTexto(response.body().getTexto());

                    // Notificar al adaptador para que se actualice el ítem
                    publicacionAdapter.notifyItemChanged(listaPublicaciones.indexOf(publicacion));
                } else {
                    Toast.makeText(PantallaPerfil.this, "Error al publicar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comentario> call, Throwable t) {
                Log.e("Comentario", "Error al comentar", t);
                Toast.makeText(PantallaPerfil.this, "Error al comentar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPublicacionClick(Publicacion publicacion) {
        Intent intent = new Intent(this, PantallaPublicacion.class);
        intent.putExtra("publicacion_id", publicacion.getIdentificador());
        startActivity(intent);
    }

    private void eliminarPublicacion(Publicacion publicacion) {
        publicacionRepository.eliminarPublicacion(publicacion.getIdentificador())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            listaPosts.remove(publicacion);
                            publicacionAdapter.notifyDataSetChanged();
                            Toast.makeText(PantallaPerfil.this, "Publicación eliminada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PantallaPerfil.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(PantallaPerfil.this, "Error de red", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
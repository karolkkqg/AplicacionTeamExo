package com.example.aplicacionteamexo;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActividadBusqueda extends AppCompatActivity implements OnPublicacionInteractionListener {

    private List<Publicacion> listaPublicaciones;
    private PublicacionAdapter publicacionAdapter;
    private boolean esModerador;
    private PublicacionRepository publicacionRepository;
    private String queryBusqueda;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_busqueda);

        publicacionRepository = new PublicacionRepository();
        obtenerDatosIntent();
        configurarRecyclerView();
        configurarBotonAtras();
        buscarPublicaciones();
    }

    private void obtenerDatosIntent() {
        esModerador = getIntent().getBooleanExtra("esModerador", false);
        queryBusqueda = getIntent().getStringExtra("query");

        // Refuerzo por string si llega como texto
        String rolUsuario = getIntent().getStringExtra("rol");
        if (rolUsuario != null && rolUsuario.equalsIgnoreCase("Moderador")) {
            esModerador = true;
        }
    }

    private void configurarRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewPublicaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaPublicaciones = new ArrayList<>();
        publicacionAdapter = new PublicacionAdapter(listaPublicaciones, esModerador, this, this);
        recyclerView.setAdapter(publicacionAdapter);
    }

    private void configurarBotonAtras() {
        ImageButton btnAtras = findViewById(R.id.btn_atras);
        btnAtras.setOnClickListener(v -> finish());
    }

    private void buscarPublicaciones() {
        if (queryBusqueda != null && !queryBusqueda.isEmpty()) {
            publicacionRepository.buscarPublicaciones(queryBusqueda, "activo", 20, 1)
                    .enqueue(new Callback<List<Publicacion>>() {
                        @Override
                        public void onResponse(Call<List<Publicacion>> call, Response<List<Publicacion>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                listaPublicaciones.clear();
                                listaPublicaciones.addAll(response.body());
                                publicacionAdapter.notifyDataSetChanged();

                                if (listaPublicaciones.isEmpty()) {
                                    Toast.makeText(ActividadBusqueda.this, "No se encontraron resultados", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ActividadBusqueda.this, "Error en la búsqueda", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Publicacion>> call, Throwable t) {
                            Toast.makeText(ActividadBusqueda.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
        Toast.makeText(this, "Reacción: " + tipoReaccion, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ActividadBusqueda.this, "Comentario publicado", Toast.LENGTH_SHORT).show();

                    // Agregar comentario localmente en el modelo
                    publicacion.agregarComentarioTexto(response.body().getTexto());

                    // Notificar al adaptador para que se actualice el ítem
                    publicacionAdapter.notifyItemChanged(listaPublicaciones.indexOf(publicacion));
                } else {
                    Toast.makeText(ActividadBusqueda.this, "Error al publicar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comentario> call, Throwable t) {
                Log.e("Comentario", "Error al comentar", t);
                Toast.makeText(ActividadBusqueda.this, "Error al comentar", Toast.LENGTH_SHORT).show();
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
                            listaPublicaciones.remove(publicacion);
                            publicacionAdapter.notifyDataSetChanged();
                            Toast.makeText(ActividadBusqueda.this, "Publicación eliminada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ActividadBusqueda.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ActividadBusqueda.this, "Error de red", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
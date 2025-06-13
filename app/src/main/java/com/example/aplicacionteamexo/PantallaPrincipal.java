package com.example.aplicacionteamexo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaPrincipal extends AppCompatActivity implements OnPublicacionInteractionListener{

    private PublicacionAdapter adapter;
    private List<Publicacion> lista;
    private boolean esModerador;
    private PublicacionRepository publicacionRepository;

    private List<Publicacion> listaPublicaciones;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_principal);

        publicacionRepository = new PublicacionRepository();

        ImageButton btnFiltro = findViewById(R.id.btn_filtro);
        btnFiltro.setOnClickListener(v -> mostrarMenuBottomSheet());

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        lista = cargarPosts(); // Datos iniciales (puedes reemplazar con llamada a API)

        String rol = getSharedPreferences("auth", MODE_PRIVATE).getString("rol", "fan");
        esModerador = rol.equals("moderador");

        // Inicialización del adapter con la implementación de la interfaz
        adapter = new PublicacionAdapter(lista, esModerador, this, this);
        recyclerView.setAdapter(adapter);

        ImageButton btnPerfil = findViewById(R.id.btn_perfil);
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaPrincipal.this, PantallaPerfil.class);
            intent.putExtra("esModerador", esModerador);
            startActivity(intent);
        });

        configurarBusqueda();
    }

    private void configurarBusqueda() {
        EditText editTextSearch = findViewById(R.id.editTextSearch);
        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscarPublicaciones(v.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void buscarPublicaciones(String query) {
        if (!query.isEmpty()) {
            publicacionRepository.buscarPublicaciones(query, "activo", 10, 1)
                    .enqueue(new retrofit2.Callback<List<Publicacion>>() {
                        @Override
                        public void onResponse(Call<List<Publicacion>> call, Response<List<Publicacion>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                lista.clear();
                                lista.addAll(response.body());
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Publicacion>> call, Throwable t) {
                            Toast.makeText(PantallaPrincipal.this, "Error al buscar", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Implementación de la interfaz OnPublicacionInteractionListener

    public void onEliminarClick(Publicacion publicacion) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar publicación")
                .setMessage("¿Estás seguro de eliminar esta publicación?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eliminarPublicacionAPI(publicacion.getIdentificador());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    public void onLikeClick(Publicacion publicacion) {
        Toast.makeText(this, "Like a: " + publicacion.getTitulo(), Toast.LENGTH_SHORT).show();
    }


    public void onReaccionClick(Publicacion publicacion, String tipoReaccion) {
        Toast.makeText(this, "Reacción " + tipoReaccion + " a: " + publicacion.getTitulo(), Toast.LENGTH_SHORT).show();
    }


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
                    Toast.makeText(PantallaPrincipal.this, "Comentario publicado", Toast.LENGTH_SHORT).show();

                    // Agregar comentario localmente en el modelo
                    publicacion.agregarComentarioTexto(response.body().getTexto());

                    // Notificar al adaptador para que se actualice el ítem
                    adapter.notifyItemChanged(listaPublicaciones.indexOf(publicacion));
                } else {
                    Toast.makeText(PantallaPrincipal.this, "Error al publicar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comentario> call, Throwable t) {
                Log.e("Comentario", "Error al comentar", t);
                Toast.makeText(PantallaPrincipal.this, "Error al comentar", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onPublicacionClick(Publicacion publicacion) {
        // Abrir actividad de detalle
        Intent intent = new Intent(this, PantallaPublicacion.class);
        intent.putExtra("publicacion_id", publicacion.getIdentificador());
        startActivity(intent);
    }

    private void eliminarPublicacionAPI(int publicacionId) {
        publicacionRepository.eliminarPublicacion(publicacionId)
                .enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Eliminar de la lista local
                            for (int i = 0; i < lista.size(); i++) {
                                if (lista.get(i).getIdentificador() == publicacionId) {
                                    lista.remove(i);
                                    adapter.notifyItemRemoved(i);
                                    break;
                                }
                            }
                            Toast.makeText(PantallaPrincipal.this, "Publicación eliminada", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(PantallaPrincipal.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarMenuBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_menu, null);
        bottomSheetDialog.setContentView(sheetView);

        TextView optionVideos = sheetView.findViewById(R.id.optionVideos);
        TextView optionImagenes = sheetView.findViewById(R.id.optionImagenes);
        TextView optionAudios = sheetView.findViewById(R.id.optionAudios);
        TextView optionUsuarios = sheetView.findViewById(R.id.optionUsuarios);
        View divider = sheetView.findViewById(R.id.divider);



        if (esModerador) {
            optionUsuarios.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
        } else {
            optionUsuarios.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }

        optionVideos.setOnClickListener(v -> {
            Toast.makeText(this, "Filtrar Videos", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        optionUsuarios.setOnClickListener(v -> {
            Toast.makeText(this, "Lista de Usuarios", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private List<Publicacion> cargarPosts() {
        List<Publicacion> lista = new ArrayList<>();
        lista.add(new Publicacion(1, "Título 1", "Publicado", "Contenido 1", 123, "2024-06-01"));
        lista.add(new Publicacion(2, "Título 2", "Borrador", "Contenido 2", 456, "2024-06-02"));
        return lista;
    }

    private void eliminarPost(Publicacion post) {
        Toast.makeText(this, "Eliminado: " + post.getContenido(), Toast.LENGTH_SHORT).show();
        // Aquí podrías también eliminar de base de datos o notificar al servidor
    }
}


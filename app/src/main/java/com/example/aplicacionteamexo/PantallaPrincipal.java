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
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionConRecurso;
import com.example.aplicacionteamexo.data.repositorio.ComentarioRepository;
import com.example.aplicacionteamexo.data.repositorio.PublicacionRepository;
import com.example.aplicacionteamexo.utilidades.OnPublicacionInteractionListener;
import com.example.aplicacionteamexo.utilidades.PublicacionAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaPrincipal extends AppCompatActivity implements OnPublicacionInteractionListener{

    private PublicacionAdapter publicacionAdapter;
    private List<PublicacionConRecurso> listaPublicaciones;
    private boolean esModerador;
    private PublicacionRepository publicacionRepository;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;

    private List<PublicacionConRecurso> listaPublicacionesOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_principal);

        recyclerView = findViewById(R.id.recyclerViewPosts); // Usar variable de clase
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String rol = getSharedPreferences("auth", MODE_PRIVATE).getString("rol", "fan");
        esModerador = rol.equals("moderador");

        obtenerPublicacionesDesdeBackend();

        ImageButton btnPerfil = findViewById(R.id.btn_perfil);
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaPrincipal.this, PantallaPerfil.class);
            intent.putExtra("esModerador", esModerador);
            startActivity(intent);
        });

        ImageButton btnFiltro = findViewById(R.id.btn_filtro);
        btnFiltro.setOnClickListener(v -> mostrarMenuBottomSheet());

        EditText editTextSearch = findViewById(R.id.editTextSearch);

        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = editTextSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    Intent intent = new Intent(PantallaPrincipal.this, ActividadBusqueda.class);
                    intent.putExtra("query", query);
                    startActivity(intent);
                } else {
                    Toast.makeText(PantallaPrincipal.this, "Ingrese texto para buscar", Toast.LENGTH_SHORT).show();
                }
                return true; // Evento consumido
            }
            return false;
        });


    }

    private void obtenerPublicacionesDesdeBackend() {
        PublicacionRepository publicacionRepository = new PublicacionRepository();

        publicacionRepository.obtenerPublicacionesConRecursos().enqueue(new Callback<List<PublicacionConRecurso>>() {
            @Override
            public void onResponse(Call<List<PublicacionConRecurso>> call, Response<List<PublicacionConRecurso>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaPublicacionesOriginal = response.body(); // 👈 Guarda lista completa
                    List<PublicacionConRecurso> publicaciones = new ArrayList<>(listaPublicacionesOriginal);

                    Collections.sort(publicaciones, (p1, p2) -> p2.getFechaCreacion().compareTo(p1.getFechaCreacion()));

                    PublicacionAdapter adapter = new PublicacionAdapter(publicaciones, esModerador, PantallaPrincipal.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(PantallaPrincipal.this, "Error al obtener publicaciones", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PublicacionConRecurso>> call, Throwable t) {
                Toast.makeText(PantallaPrincipal.this, "Error de red", Toast.LENGTH_SHORT).show();
                Log.e("PantallaPrincipal", "Error al cargar publicaciones", t);
            }
        });
    }

    public void onPublicacionClick(Publicacion publicacion) {
        // PARA EDITAR LA PUBLICACION

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
            filtrarPorTipoRecurso("Video");
            bottomSheetDialog.dismiss();
        });

        optionImagenes.setOnClickListener(v -> {
            filtrarPorTipoRecurso("Foto");
            bottomSheetDialog.dismiss();
        });

        optionAudios.setOnClickListener(v -> {
            filtrarPorTipoRecurso("Audio");
            bottomSheetDialog.dismiss();
        });


        bottomSheetDialog.show();
    }

     // guarda todas las publicaciones

    private void filtrarPorTipoRecurso(String tipo) {
        if (listaPublicacionesOriginal == null) return;

        List<PublicacionConRecurso> filtradas = new ArrayList<>();

        for (PublicacionConRecurso pub : listaPublicacionesOriginal) {
            if (pub.getRecurso() != null && tipo.equals(pub.getRecurso().getTipo())) {
                filtradas.add(pub);
            }
        }

        Collections.sort(filtradas, (p1, p2) -> p2.getFechaCreacion().compareTo(p1.getFechaCreacion()));

        PublicacionAdapter adapter = new PublicacionAdapter(filtradas, esModerador, PantallaPrincipal.this);
        recyclerView.setAdapter(adapter);
    }

}


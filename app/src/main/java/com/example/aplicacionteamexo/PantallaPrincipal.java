package com.example.aplicacionteamexo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

        int usuarioId = getSharedPreferences("auth", MODE_PRIVATE).getInt("usuarioId", 0);
        if (usuarioId != 0) {
            NotificacionGrpcService notificacionService = new NotificacionGrpcService(this);
            notificacionService.suscribirseANotificaciones(usuarioId);
        }

        View layoutPrincipal = findViewById(R.id.bottom_bar);

        ViewCompat.setOnApplyWindowInsetsListener(layoutPrincipal, (v, insets) -> {
            Insets barrasSistema = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, barrasSistema.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setClipToPadding(false);

        int paddingBottomPx = (int) (80 * getResources().getDisplayMetrics().density);
        recyclerView.setPadding(0, 0, 0, paddingBottomPx);

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

        ImageButton btnCasa = findViewById(R.id.btn_home);
        btnCasa.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaPrincipal.this, PantallaPrincipal.class);
            startActivity(intent);
        });


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
                return true;
            }
            return false;
        });

        ImageButton btnNotificaciones = findViewById(R.id.btn_notificaciones);

        btnNotificaciones.setOnClickListener(v -> {
            Intent intent = new Intent(this, PantallaNotificaciones.class);
            startActivity(intent);
        });

    }

    private void obtenerPublicacionesDesdeBackend() {
        PublicacionRepository publicacionRepository = new PublicacionRepository();

        publicacionRepository.obtenerPublicacionesConRecursos().enqueue(new Callback<List<PublicacionConRecurso>>() {
            @Override
            public void onResponse(Call<List<PublicacionConRecurso>> call, Response<List<PublicacionConRecurso>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaPublicacionesOriginal = response.body();
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
                boolean estaConectado = false;

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    estaConectado = networkInfo != null && networkInfo.isConnected();
                }

                if (estaConectado) {
                    Toast.makeText(PantallaPrincipal.this, "Ocurrió un problema con el servidor", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PantallaPrincipal.this, "Sin conexión a Internet. Verifica tu red.", Toast.LENGTH_SHORT).show();
                }
                Log.e("PantallaPrincipal", "Error al cargar publicaciones", t);
            }
        });
    }

    public void onPublicacionClick(Publicacion publicacion) {


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


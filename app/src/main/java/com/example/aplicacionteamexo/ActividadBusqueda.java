package com.example.aplicacionteamexo;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionteamexo.data.modelo.publicacion.Publicacion;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionBusquedaRespuesta;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionConRecurso;
import com.example.aplicacionteamexo.data.repositorio.PublicacionRepository;
import com.example.aplicacionteamexo.utilidades.PublicacionAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActividadBusqueda extends AppCompatActivity {

    private List<PublicacionConRecurso> listaPublicaciones;
    private PublicacionAdapter publicacionAdapter;
    private boolean esModerador;
    private PublicacionRepository publicacionRepository;
    private String queryBusqueda;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_busqueda);

        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);

        publicacionRepository = new PublicacionRepository();

        EditText editTextSearch = findViewById(R.id.editTextSearch);
        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                queryBusqueda = editTextSearch.getText().toString().trim();
                buscarPublicaciones();
                return true;
            }
            return false;
        });


        obtenerDatosIntent();
        configurarRecyclerView();
        configurarBotonAtras();
        buscarPublicaciones();
    }

    private void obtenerDatosIntent() {
        esModerador = getIntent().getBooleanExtra("esModerador", false);
        queryBusqueda = getIntent().getStringExtra("query");

        String rolUsuario = getIntent().getStringExtra("rol");
        if (rolUsuario != null && rolUsuario.equalsIgnoreCase("Moderador")) {
            esModerador = true;
        }
    }

    private void configurarRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewPublicaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaPublicaciones = new ArrayList<>();
        publicacionAdapter = new PublicacionAdapter(listaPublicaciones, esModerador, this);
        recyclerView.setAdapter(publicacionAdapter);
    }


    private void configurarBotonAtras() {
        ImageButton btnAtras = findViewById(R.id.btn_atras);
        btnAtras.setOnClickListener(v -> finish());
    }

    private void buscarPublicaciones() {
        if (queryBusqueda != null && !queryBusqueda.isEmpty()) {
            String categorias = "";
            String tipoRecurso = "";

            publicacionRepository.buscarPublicaciones(
                    queryBusqueda,
                    categorias,
                    tipoRecurso,
                    20,
                    1
            ).enqueue(new Callback<PublicacionBusquedaRespuesta>() {
                @Override
                public void onResponse(Call<PublicacionBusquedaRespuesta> call, Response<PublicacionBusquedaRespuesta> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<PublicacionConRecurso> resultados = response.body().getResultados();
                        listaPublicaciones.clear();
                        listaPublicaciones.addAll(resultados);
                        publicacionAdapter.notifyDataSetChanged();

                        if (listaPublicaciones.isEmpty()) {
                            Toast.makeText(ActividadBusqueda.this, "No se encontraron resultados", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ActividadBusqueda.this, "Error en la búsqueda", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PublicacionBusquedaRespuesta> call, Throwable t) {
                    boolean estaConectado = false;

                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    if (connectivityManager != null) {
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        estaConectado = networkInfo != null && networkInfo.isConnected();
                    }

                    if (estaConectado) {
                        Toast.makeText(ActividadBusqueda.this, "Ocurrió un problema con el servidor", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ActividadBusqueda.this, "Sin conexión a Internet. Verifica tu red.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



}
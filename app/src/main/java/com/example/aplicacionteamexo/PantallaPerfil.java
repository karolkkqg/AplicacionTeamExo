package com.example.aplicacionteamexo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionConRecurso;
import com.example.aplicacionteamexo.data.repositorio.PublicacionRepository;
import com.example.aplicacionteamexo.utilidades.PublicacionAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaPerfil extends AppCompatActivity{

    private TextView tvNombreUsuario;
    private Button btnEditarPerfil, btnSubirPublicacion;
    private RecyclerView recyclerPublicaciones;
    private PublicacionAdapter publicacionAdapter;
    private List<PublicacionConRecurso> listaPosts = new ArrayList<>();
    private boolean esModerador;
    private int usuarioId;
    private SharedPreferences sharedPreferences;

    private PublicacionRepository publicacionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_perfil);

        int usuarioId = getSharedPreferences("auth", MODE_PRIVATE).getInt("usuarioId", 0);
        if (usuarioId != 0) {
            NotificacionGrpcService notificacionService = new NotificacionGrpcService(this);
            notificacionService.suscribirseANotificaciones(usuarioId);
        }

        publicacionRepository = new PublicacionRepository();

        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> mostrarDialogoCerrarSesion());

        inicializarVistas();
        obtenerDatosUsuario();
        configurarBotones();
        cargarPublicacionesUsuario();
    }

    private void inicializarVistas() {
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnSubirPublicacion = findViewById(R.id.btnSubirPublicacion);
        recyclerPublicaciones = findViewById(R.id.recyclerPublicaciones);
        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);

        recyclerPublicaciones.setLayoutManager(new LinearLayoutManager(this));
    }

    private void obtenerDatosUsuario() {
        usuarioId = sharedPreferences.getInt("usuarioId", -1);

        Log.d("USER_DEBUG", "ID de usuario obtenido de SharedPreferences: " + usuarioId);

        if (usuarioId == -1) {
            Toast.makeText(this, "Error: ID de usuario no válido", Toast.LENGTH_LONG).show();
            Log.e("USER_ERROR", "ID de usuario no válido (-1)");
            finish();
            return;
        }

        String nombreUsuario = sharedPreferences.getString("nombreUsuario", "Usuario");
        String rolUsuario = sharedPreferences.getString("rol", "fan");
        esModerador = rolUsuario.equalsIgnoreCase("moderador");

        tvNombreUsuario.setText(nombreUsuario);
        Button btnVerEstadisticas = findViewById(R.id.btnVerEstadisticas);
        btnVerEstadisticas.setVisibility(esModerador ? View.VISIBLE : View.GONE);
        publicacionAdapter = new PublicacionAdapter(listaPosts, esModerador, this);
        recyclerPublicaciones.setAdapter(publicacionAdapter);

        Log.d("USER_DEBUG", "ID de usuario obtenido: " + usuarioId);
    }

    private void configurarBotones() {
        ImageButton btnAtras = findViewById(R.id.btnVolver);
        btnAtras.setOnClickListener(v -> finish());

        btnEditarPerfil.setOnClickListener(v ->{
            Intent intent = new Intent(this, PantallaDetallePerfil.class);
            intent.putExtra("usuarioId", usuarioId);
            startActivity(intent);
        });


        btnSubirPublicacion.setOnClickListener(v -> {
            Intent intent = new Intent(this, PantallaSubirPost.class);
            intent.putExtra("usuarioId", usuarioId);
            startActivity(intent);
        });

        findViewById(R.id.btnVerEstadisticas).setOnClickListener(v ->{
            Intent intent = new Intent(this, PantallaEstadisticas.class);
            intent.putExtra("usuarioId", usuarioId);
            startActivity(intent);
        });

    }

    private void cargarPublicacionesUsuario() {
        Log.d("API_DEBUG", "Cargando publicaciones del usuario con ID: " + usuarioId);

        publicacionRepository.obtenerPublicacionesPorUsuario(usuarioId)
                .enqueue(new Callback<List<PublicacionConRecurso>>() {
                    @Override
                    public void onResponse(Call<List<PublicacionConRecurso>> call, Response<List<PublicacionConRecurso>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            listaPosts.clear();
                            listaPosts.addAll(response.body());
                            publicacionAdapter.notifyDataSetChanged();

                            if (listaPosts.isEmpty()) {
                                Toast.makeText(PantallaPerfil.this, "No hay publicaciones para mostrar", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            manejarErrorRespuesta(response.code());
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
                            Toast.makeText(PantallaPerfil.this, "Ocurrió un problema con el servidor", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PantallaPerfil.this, "Sin conexión a Internet. Verifica tu red.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void manejarErrorRespuesta(int codigoError) {
        String mensajeError;

        switch (codigoError) {
            case 401:
                mensajeError = "No autorizado - Sesión expirada";
                break;
            case 404:
                mensajeError = "Usuario no encontrado";
                break;
            case 500:
                mensajeError = "Error interno del servidor";
                break;
            default:
                mensajeError = "Error al obtener publicaciones. Código: " + codigoError;
        }

        Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show();
        Log.e("API_ERROR", "Código HTTP: " + codigoError);
    }

    private void mostrarDialogoCerrarSesion() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás segura de que quieres cerrar sesión? Puedes volver, pero yo no me olvido fácil.")
                .setPositiveButton("Sí, cerrar", (dialog, which) -> cerrarSesion())
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void cerrarSesion() {
        getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, ActividadLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
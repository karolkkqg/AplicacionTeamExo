package com.example.aplicacionteamexo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionteamexo.data.api.NotificacionAPI;
import com.example.aplicacionteamexo.data.modelo.notificacion.NotificacionRespuesta;
import com.example.aplicacionteamexo.data.network.RetrofitClient;
import com.example.aplicacionteamexo.grpc.notificacion.StreamNotificacionesRequest;
import com.example.aplicacionteamexo.grpc.notificacion.NotificacionResponse;
import com.example.aplicacionteamexo.data.modelo.notificacion.Notificacion;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaNotificaciones extends AppCompatActivity {
    private RecyclerView recycler;
    private Button btnEliminar;
    private NotificacionAdapter adapter;
    private NotificacionAPI api;
    private List<Notificacion> notificaciones = new ArrayList<>();
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_notificaciones);

        recycler = findViewById(R.id.recyclerNotificaciones);
        btnEliminar = findViewById(R.id.btnEliminar);
        usuarioId = getSharedPreferences("auth", MODE_PRIVATE).getInt("usuarioId", 0);

        api = RetrofitClient.getInstance().create(NotificacionAPI.class);

        adapter = new NotificacionAdapter(notificaciones, notificacion -> {
            if (!notificacion.isLeida()) {
                api.marcarComoLeida(notificacion.getNotificacionId()).enqueue(new Callback<Void>() {
                    @Override public void onResponse(Call<Void> call, Response<Void> response) {}
                    @Override public void onFailure(Call<Void> call, Throwable t) {}
                });
                notificacion.setLeida(true);
                adapter.notifyDataSetChanged();
            }
            btnEliminar.setEnabled(true);
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        btnEliminar.setOnClickListener(v -> {
            Notificacion seleccionada = adapter.getNotificacionSeleccionada();
            if (seleccionada != null) {
                api.eliminarNotificacion(seleccionada.getNotificacionId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        adapter.eliminarSeleccionada();
                        btnEliminar.setEnabled(false);
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(PantallaNotificaciones.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        cargarNotificaciones();

        ImageButton btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaNotificaciones.this, PantallaPrincipal.class);
            startActivity(intent);
            finish();
        });

    }

    private void cargarNotificaciones() {
        api.obtenerNotificacionesUsuario(usuarioId, null, 50, 1).enqueue(new Callback<NotificacionRespuesta>() {
            @Override
            public void onResponse(Call<NotificacionRespuesta> call, Response<NotificacionRespuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notificaciones.clear();
                    notificaciones.addAll(response.body().getResultados());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<NotificacionRespuesta> call, Throwable t) {
                Toast.makeText(PantallaNotificaciones.this, "Error al obtener notificaciones", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

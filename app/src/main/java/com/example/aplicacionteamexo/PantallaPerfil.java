package com.example.aplicacionteamexo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantallaPerfil extends AppCompatActivity {

    private TextView tvNombreUsuario;
    private Button btnEditarPerfil, btnSubirPublicacion;
    private RecyclerView recyclerPublicaciones;
    private PostAdapter postAdapter;
    private List<Post> listaPosts;

    private boolean esModerador = true; // Esto puedes obtenerlo desde tu sistema de sesión real
    private int usuarioId = 1; // Ejemplo, puedes obtenerlo de SesionManager si lo creas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_perfil);

        boolean esModerador = getIntent().getBooleanExtra("esModerador", false);

        String rolUsuario = getIntent().getStringExtra("rol");

        if (rolUsuario != null && rolUsuario.equals("Moderador")) {
            esModerador = true;
        }

        // 2. Mostrar u ocultar el botón según el rol
        Button btnVerEstadisticas = findViewById(R.id.btnVerEstadisticas);

        if (esModerador) {
            btnVerEstadisticas.setVisibility(View.VISIBLE);
        } else {
            btnVerEstadisticas.setVisibility(View.GONE);
        }

        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnSubirPublicacion = findViewById(R.id.btnSubirPublicacion);
        recyclerPublicaciones = findViewById(R.id.recyclerPublicaciones);

        tvNombreUsuario.setText("Moderador123"); // Cambia por el nombre del usuario

        recyclerPublicaciones.setLayoutManager(new LinearLayoutManager(this));

        // Simulamos lista de publicaciones (aquí puedes hacer la petición a tu servidor)
        listaPosts = new ArrayList<>();
        listaPosts.add(new Post(1, "Título 1", "activo", "Contenido 1", usuarioId, "2024-01-01"));
        listaPosts.add(new Post(2, "Título 2", "activo", "Contenido 2", usuarioId, "2024-01-02"));

        postAdapter = new PostAdapter(listaPosts, esModerador, post -> {
            // Aquí lógica para eliminar, también desde el servidor si hace falta
            listaPosts.remove(post);
            postAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Publicación eliminada", Toast.LENGTH_SHORT).show();
        });

        recyclerPublicaciones.setAdapter(postAdapter);

        ImageButton btnAtras = findViewById(R.id.btnVolver);
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Esto cierra esta pantalla y regresa a la anterior (PantallaPrincipal)
            }
        });


        btnEditarPerfil.setOnClickListener(v -> {
            // Si tienes EditarPerfilActivity, ponla aquí. Por ahora mostramos un toast.
            Toast.makeText(this, "Editar perfil aún no implementado", Toast.LENGTH_SHORT).show();
        });

        btnSubirPublicacion.setOnClickListener(v -> {
            // Si tienes SubirPublicacionActivity, ponla aquí.
            Toast.makeText(this, "Subir publicación aún no implementado", Toast.LENGTH_SHORT).show();
        });
    }
}

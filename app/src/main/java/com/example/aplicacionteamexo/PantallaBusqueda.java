package com.example.aplicacionteamexo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import com.example.aplicacionteamexo.Post;
import com.example.aplicacionteamexo.PostAdapter;



public class PantallaBusqueda extends AppCompatActivity {

    private List<Post> listaDePost;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_busqueda);

        boolean esModerador = getIntent().getBooleanExtra("esModerador", false);
        String query = getIntent().getStringExtra("query");

        List<Post> todos = obtenerPostsDesdeServidor(); // Simulado por ahora
        listaDePost = filtrarPosts(todos, query);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPublicaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postAdapter = new PostAdapter(listaDePost, esModerador, post -> {
            listaDePost.remove(post);
            postAdapter.notifyDataSetChanged();
        });

        ImageButton btnAtras = findViewById(R.id.btn_atras);
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Esto cierra esta pantalla y regresa a la anterior (PantallaPrincipal)
            }
        });


        recyclerView.setAdapter(postAdapter);
    }

    private List<Post> obtenerPostsDesdeServidor() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1, "Juan", "Texto de prueba", "imagen", 0, "2025-06-08"));
        return posts;
    }

    private List<Post> filtrarPosts(List<Post> todos, String query) {
        List<Post> filtrados = new ArrayList<>();
        for (Post p : todos) {
            if (p.getContenido().toLowerCase().contains(query.toLowerCase()) ||
                    p.getTitulo().toLowerCase().contains(query.toLowerCase())) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }


}


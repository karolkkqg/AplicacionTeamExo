package com.example.aplicacionteamexo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class PantallaPrincipal extends AppCompatActivity {

    private PostAdapter adapter; // ← Variable de clase
    private List<Post> lista; // ← También lo hacemos variable para acceder desde otros métodos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_principal);

        ImageButton btnFiltro = findViewById(R.id.btn_filtro);
        btnFiltro.setOnClickListener(v -> mostrarMenuBottomSheet());

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        lista = cargarPosts(); // Cargamos la lista
        boolean esModerador = false; // Simulación

        adapter = new PostAdapter(lista, esModerador, post -> {
            lista.remove(post);
            adapter.notifyDataSetChanged();
            eliminarPost(post);
        });

        ImageButton btnPerfil = findViewById(R.id.btn_perfil);
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaPrincipal.this, PantallaPerfil.class);
            intent.putExtra("esModerador", esModerador); // ← pasamos el booleano
            startActivity(intent);

        });

        EditText editTextSearch = findViewById(R.id.editTextSearch);

        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String query = editTextSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    Intent intent = new Intent(PantallaPrincipal.this, PantallaBusqueda.class);
                    intent.putExtra("query", query); // Enviar búsqueda
                    intent.putExtra("esModerador", true); // O el valor real del usuario actual
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Escribe algo para buscar", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });


        recyclerView.setAdapter(adapter);
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

        boolean esModerador = false;

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

    private List<Post> cargarPosts() {
        List<Post> lista = new ArrayList<>();
        lista.add(new Post(1, "Título 1", "Publicado", "Contenido 1", 123, "2024-06-01"));
        lista.add(new Post(2, "Título 2", "Borrador", "Contenido 2", 456, "2024-06-02"));
        return lista;
    }

    private void eliminarPost(Post post) {
        Toast.makeText(this, "Eliminado: " + post.getContenido(), Toast.LENGTH_SHORT).show();
        // Aquí podrías también eliminar de base de datos o notificar al servidor
    }
}


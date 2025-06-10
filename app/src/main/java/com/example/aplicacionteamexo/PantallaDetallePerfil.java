package com.example.aplicacionteamexo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PantallaDetallePerfil extends AppCompatActivity {
    private boolean mostrarCampoContrasena = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_detalle_perfil);
        EditText etContrasena = findViewById(R.id.etContrasena);
        Button btnGuardarContrasena = findViewById(R.id.btnGuardarContrasena);
        Button btnMostrarEditarContrasena = findViewById(R.id.btnMostrarEditarContrasena);

// Ocultar ambos al inicio
        etContrasena.setVisibility(View.GONE);
        btnGuardarContrasena.setVisibility(View.GONE);

        btnMostrarEditarContrasena.setOnClickListener(v -> {
            mostrarCampoContrasena = !mostrarCampoContrasena;

            if (mostrarCampoContrasena) {
                etContrasena.setVisibility(View.VISIBLE);
                btnGuardarContrasena.setVisibility(View.VISIBLE);
                btnMostrarEditarContrasena.setText("Cancelar edición");
            } else {
                etContrasena.setVisibility(View.GONE);
                btnGuardarContrasena.setVisibility(View.GONE);
                btnMostrarEditarContrasena.setText("Editar contraseña");
            }
        });


    }
}
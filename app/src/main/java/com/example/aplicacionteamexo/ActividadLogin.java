package com.example.aplicacionteamexo;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicacionteamexo.grpc.LoginRequest;
import com.example.aplicacionteamexo.grpc.LoginResponse;
import com.example.aplicacionteamexo.grpc.UsuarioServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ActividadLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.actividad_login);

        EditText etCorreo = findViewById(R.id.etCorreo);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        Button btnRegistrarse = findViewById(R.id.btnRegistrarse);

        // Mostrar u ocultar la contraseña al tocar el ícono
        etPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    if (etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_candado_abierto, 0);
                    } else {
                        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_candado_cerrado, 0);
                    }
                    etPassword.setSelection(etPassword.length());
                    return true;
                }
            }
            return false;
        });

        // Navegar al registro
        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(ActividadLogin.this, actividadRegistro.class);
            startActivity(intent);
        });

        // Ejecutar login al dar clic en "Iniciar sesión"
        btnIniciarSesion.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etPassword.getText().toString();

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    ManagedChannel channel = ManagedChannelBuilder
                            .forAddress("192.168.0.108", 50051) // IP y puerto del servidor gRPC
                            .usePlaintext()
                            .build();

                    UsuarioServiceGrpc.UsuarioServiceBlockingStub stub = UsuarioServiceGrpc.newBlockingStub(channel);

                    LoginRequest request = LoginRequest.newBuilder()
                            .setCorreo(correo)
                            .setContrasena(contrasena)
                            .build();

                    LoginResponse response = stub.login(request);

                    runOnUiThread(() -> {
                        if (response.getExito()) {
                            String token = response.getToken();
                            String mensaje = response.getMensaje();

                            getSharedPreferences("auth", MODE_PRIVATE)
                                    .edit()
                                    .putString("token", token)
                                    .apply();

                            Toast.makeText(this, "Inicio de sesión exitoso: " + mensaje, Toast.LENGTH_SHORT).show();

                            // Redirigir a pantalla principal si deseas
                            // startActivity(new Intent(this, ActividadPrincipal.class));
                            // finish();
                        } else {
                            Toast.makeText(this, "Error: " + response.getMensaje(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    channel.shutdown();

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}

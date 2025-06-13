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
import com.example.aplicacionteamexo.utils.Validador;

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

        // Mostrar u ocultar la contraseña
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

        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(ActividadLogin.this, actividadRegistro.class);
            startActivity(intent);
        });

        btnIniciarSesion.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etPassword.getText().toString();

            StringBuilder errores = new StringBuilder();

            String errCorreo = Validador.validarCorreo(correo);
            if (errCorreo != null) errores.append("- ").append(errCorreo).append("\n");

            String errPass = Validador.validarPassword(contrasena);
            if (errPass != null) errores.append("- ").append(errPass).append("\n");

            if (errores.length() > 0) {
                Toast.makeText(getApplicationContext(), errores.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            new Thread(() -> {
                try {
                    ManagedChannel channel = ManagedChannelBuilder
                            .forAddress("192.168.0.157", 50051)
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
                            int usuarioId = response.getUsuarioId();

                            getSharedPreferences("auth", MODE_PRIVATE)
                                    .edit()
                                    .putString("token", token)
                                    .putInt("usuarioId", usuarioId)
                                    .apply();

                            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(this, PantallaSubirPost.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(this, "Error: " + response.getMensaje(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    channel.shutdown();

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();
        });
    }
}

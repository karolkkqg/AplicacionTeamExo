package com.example.aplicacionteamexo;

import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aplicacionteamexo.grpc.recurso.RecursoServiceGrpc;
import com.example.aplicacionteamexo.grpc.recurso.CrearRecursoRequest;
import com.example.aplicacionteamexo.grpc.recurso.CrearRecursoResponse;
import com.example.aplicacionteamexo.utilidades.Configuracion;
import com.example.aplicacionteamexo.utils.Validador;
import com.google.protobuf.ByteString;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
//import recurso.Recurso;
//import recurso.RecursoServiceGrpc;

public class PantallaSubirPost extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECCIONAR_ARCHIVO = 1001;

    private Uri archivoSeleccionadoUri;
    private byte[] archivoBytes;
    private String tipoRecurso;
    private int formato, resolucionODuracion;

    private TextView archivoSeleccionadoTextView;
    private EditText tituloEditText, descripcionEditText;
    private Button seleccionarArchivoBtn, subirPublicacionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_subir_post);

        ImageButton btnRegresar = findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaSubirPost.this, PantallaPerfil.class);
            startActivity(intent);
            finish();
        });


        archivoSeleccionadoTextView = findViewById(R.id.archivoSeleccionadoTextView);
        tituloEditText = findViewById(R.id.tituloEditText);
        descripcionEditText = findViewById(R.id.descripcionEditText);
        seleccionarArchivoBtn = findViewById(R.id.btnSeleccionarArchivo);
        subirPublicacionBtn = findViewById(R.id.subirPublicacionBtn);

        seleccionarArchivoBtn.setOnClickListener(v -> abrirSelectorDeArchivo());
        subirPublicacionBtn.setOnClickListener(v -> subirPublicacionYRecurso());
    }

    private void abrirSelectorDeArchivo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo"), REQUEST_CODE_SELECCIONAR_ARCHIVO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECCIONAR_ARCHIVO && resultCode == RESULT_OK && data != null) {
            archivoSeleccionadoUri = data.getData();
            String nombreArchivo = obtenerNombreArchivo(archivoSeleccionadoUri);
            archivoSeleccionadoTextView.setText("📎 " + nombreArchivo);

            try (InputStream inputStream = getContentResolver().openInputStream(archivoSeleccionadoUri)) {
                archivoBytes = IOUtils.toByteArray(inputStream);

                String mime = getContentResolver().getType(archivoSeleccionadoUri);
                if (mime != null) {
                    if (mime.startsWith("image/")) {
                        tipoRecurso = "Foto";
                        formato = 1;
                        resolucionODuracion = 1080;
                    } else if (mime.startsWith("audio/")) {
                        tipoRecurso = "Audio";
                        formato = 2;
                        resolucionODuracion = 180;
                    } else if (mime.startsWith("video/")) {
                        tipoRecurso = "Video";
                        formato = 3;
                        resolucionODuracion = 1080;
                    } else {
                        tipoRecurso = "Otro";
                        formato = 0;
                        resolucionODuracion = 0;
                    }
                }

            } catch (IOException e) {
                archivoSeleccionadoTextView.setText("Error al leer archivo");
            }
        }
    }

    private String obtenerNombreArchivo(Uri uri) {
        String resultado = "Archivo seleccionado";
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        resultado = cursor.getString(nameIndex);
                    }
                }
            }
        } else if ("file".equals(uri.getScheme())) {
            resultado = uri.getLastPathSegment();
        }
        return resultado;
    }

    private void subirPublicacionYRecurso() {
        String titulo = tituloEditText.getText().toString().trim();
        String descripcion = descripcionEditText.getText().toString().trim();

        StringBuilder errores = new StringBuilder();

        String errNombre = Validador.validarTitulo(titulo);
        if (errNombre != null) errores.append("- ").append(errNombre).append("\n");

        if (!descripcion.isEmpty()) {
            String errDescLongitud = Validador.validarLongitud(descripcion, "Descripción", 0, 200);
            if (errDescLongitud != null) {
                errores.append("- ").append(errDescLongitud).append("\n");
            }
        }

        if (errores.length() > 0) {
            Toast.makeText(getApplicationContext(), errores.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        if (titulo.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Título y descripción son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (archivoBytes == null || archivoBytes.length == 0 || tipoRecurso.equals("Otro")) {
            Toast.makeText(this, "Archivo no válido o no seleccionado", Toast.LENGTH_SHORT).show();
            return;
        }

        int usuarioId = getSharedPreferences("auth", MODE_PRIVATE).getInt("usuarioId", -1);
        if (usuarioId == -1) {
            Toast.makeText(this, "Error: usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("titulo", titulo);
            json.put("contenido", descripcion);
            json.put("usuarioId", usuarioId);
            json.put("estado", "Publicado");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String ip = Configuracion.obtenerIP(getApplicationContext());
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://"+ip+"/api/publicaciones";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                response -> {
                    try {
                        if (!response.has("publicacion")) {
                            Toast.makeText(this, "Error: publicación no creada", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int publicacionId = response.getJSONObject("publicacion").getInt("identificador");
                        crearRecursoGrpc(publicacionId, usuarioId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar respuesta de publicación", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("REST", "Error al crear publicación: " + error.getMessage());
                    Toast.makeText(this, "Error en servidor al crear publicación", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }


    private void crearRecursoGrpc(int publicacionId, int usuarioId) {
        String ip = Configuracion.obtenerIP(getApplicationContext());
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(ip, 50054)
                .usePlaintext()
                .build();

        RecursoServiceGrpc.RecursoServiceStub stub = RecursoServiceGrpc.newStub(channel);

        CrearRecursoRequest.Builder builder = CrearRecursoRequest.newBuilder()
                .setTipo(tipoRecurso)
                .setIdentificador((int) (System.currentTimeMillis() % 100000))
                .setFormato(formato)
                .setTamano(archivoBytes.length)
                .setUsuarioId(usuarioId)
                .setPublicacionId(publicacionId)
                .setArchivo(ByteString.copyFrom(archivoBytes));

        if (tipoRecurso.equals("Foto") || tipoRecurso.equals("Video")) {
            builder.setResolucion(resolucionODuracion);
        } else if (tipoRecurso.equals("Audio")) {
            builder.setDuracion(resolucionODuracion);
        }

        stub.crearRecurso(builder.build(), new StreamObserver<CrearRecursoResponse>() {
            @Override
            public void onNext(CrearRecursoResponse value) {
                runOnUiThread(() -> {
                    if (value.getExito()) {
                        Toast.makeText(PantallaSubirPost.this, "" + value.getMensaje(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PantallaSubirPost.this, "Error: " + value.getMensaje(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                boolean estaConectado = false;

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    estaConectado = networkInfo != null && networkInfo.isConnected();
                }

                if (estaConectado) {
                    Toast.makeText(PantallaSubirPost.this, "Ocurrió un problema con el servidor", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PantallaSubirPost.this, "Sin conexión a Internet. Verifica tu red.", Toast.LENGTH_SHORT).show();
                }
                Log.e("gRPC", "Error al crear recurso: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                channel.shutdown();
            }
        });
    }
}

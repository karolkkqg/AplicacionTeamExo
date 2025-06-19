package com.example.aplicacionteamexo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionRegistro;
import com.example.aplicacionteamexo.data.modelo.publicacion.PublicacionRespuesta;
import com.example.aplicacionteamexo.data.modelo.recurso.RecursoRegistro;
import com.example.aplicacionteamexo.data.modelo.recurso.RecursoRespuesta;
import com.example.aplicacionteamexo.data.repositorio.PublicacionRepository;
import com.example.aplicacionteamexo.data.repositorio.RecursoRepository;
import com.example.aplicacionteamexo.grpc.recurso.CrearRecursoRequest;
import com.example.aplicacionteamexo.grpc.recurso.CrearRecursoResponse;
import com.example.aplicacionteamexo.grpc.recurso.RecursoServiceGrpc;
import com.example.aplicacionteamexo.utilidades.Configuracion;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaPublicacion extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    private static final int PICK_AUDIO_REQUEST = 3;

    private EditText editTitulo, editContenido;
    private Uri archivoUri;
    private String tipoArchivo;
    private byte[] archivoBytes;
    private PublicacionRepository publicacionRepository;
    private RecursoRepository recursoRepository;
    private TextView txtArchivoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_publicacion);

        publicacionRepository = new PublicacionRepository();
        recursoRepository = new RecursoRepository();
        inicializarVistas();
        configurarBotones();
    }

    private void inicializarVistas() {
        editTitulo = findViewById(R.id.editTextTitulo);
        editContenido = findViewById(R.id.editTextComentario);
        txtArchivoSeleccionado = findViewById(R.id.txt_archivo_seleccionado);
    }

    private void configurarBotones() {
        AppCompatButton btnPublicar = findViewById(R.id.btn_publicar);
        btnPublicar.setOnClickListener(v -> {
            try {
                publicarContenido();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ImageButton btnAtras = findViewById(R.id.btn_atras);
        btnAtras.setOnClickListener(v -> finish());

        findViewById(R.id.btn_imagen).setOnClickListener(v -> seleccionarArchivo("Foto"));
        findViewById(R.id.btn_video).setOnClickListener(v -> seleccionarArchivo("Video"));
        findViewById(R.id.btn_audio).setOnClickListener(v -> seleccionarArchivo("Audio"));
    }

    private void publicarContenido() throws IOException {
        String titulo = editTitulo.getText().toString().trim();
        String contenido = editContenido.getText().toString().trim();

        if (titulo.isEmpty() || contenido.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        int usuarioId = prefs.getInt("usuarioId", -1);

        if (archivoBytes != null) {
            subirRecursoPrimero(titulo, contenido, usuarioId);
        } else {
            crearPublicacion(titulo, contenido, usuarioId, null);
        }
    }

    private void subirRecursoPrimero(String titulo, String contenido, int usuarioId) throws IOException {
        RecursoRegistro registro = new RecursoRegistro(
                tipoArchivo,
                obtenerFormato(archivoUri),
                archivoBytes.length,
                usuarioId,
                tipoArchivo.equals("Foto") || tipoArchivo.equals("Video") ? 1080 : null,
                tipoArchivo.equals("Audio") ? obtenerDuracion(archivoUri) : null,
                archivoBytes
        );

        new Thread(() -> {
            String ip = Configuracion.obtenerIP(getApplicationContext());
            try {
                ManagedChannel channel = ManagedChannelBuilder
                        .forAddress(ip, 50051)
                        .usePlaintext()
                        .build();

                RecursoServiceGrpc.RecursoServiceBlockingStub stub =
                        RecursoServiceGrpc.newBlockingStub(channel);

                CrearRecursoRequest.Builder builder = CrearRecursoRequest.newBuilder()
                        .setTipo(tipoArchivo)
                        .setIdentificador((int) (Math.random() * 100000))
                        .setFormato(obtenerFormato(archivoUri))
                        .setTamano(archivoBytes.length)
                        .setUsuarioId(usuarioId)
                        .setArchivo(com.google.protobuf.ByteString.copyFrom(archivoBytes));

                if (tipoArchivo.equals("Foto") || tipoArchivo.equals("Video")) {
                    builder.setResolucion(1080);
                } else if (tipoArchivo.equals("Audio")) {
                    builder.setDuracion(obtenerDuracion(archivoUri));
                }

                CrearRecursoResponse response = stub.crearRecurso(builder.build());

                runOnUiThread(() -> {
                    if (response.getExito()) {
                        Toast.makeText(PantallaPublicacion.this, "Recurso subido", Toast.LENGTH_SHORT).show();
                        crearPublicacion(titulo, contenido, usuarioId, null);
                    } else {
                        Toast.makeText(PantallaPublicacion.this, "Error: " + response.getMensaje(), Toast.LENGTH_SHORT).show();
                    }
                });

                channel.shutdown();
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(PantallaPublicacion.this, "Error al subir recurso", Toast.LENGTH_SHORT).show());
            }
        }).start();

    }

    private void crearPublicacion(String titulo, String contenido, int usuarioId, Integer recursoId) {
        PublicacionRegistro publicacion = new PublicacionRegistro(
                titulo,
                contenido,
                "Publicado",
                usuarioId,
                recursoId
        );

        publicacionRepository.crearPublicacion(publicacion)
                .enqueue(new Callback<PublicacionRespuesta>() {
                    @Override
                    public void onResponse(Call<PublicacionRespuesta> call, Response<PublicacionRespuesta> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(PantallaPublicacion.this, "Publicación creada", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(PantallaPublicacion.this, "Error al publicar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PublicacionRespuesta> call, Throwable t) {
                        Toast.makeText(PantallaPublicacion.this, "Error de red", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void seleccionarArchivo(String tipo) {
        this.tipoArchivo = tipo;
        String mimeType = tipo.equals("Foto") ? "image/*" :
                tipo.equals("Video") ? "video/*" : "audio/*";

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);
        startActivityForResult(intent,
                tipo.equals("Foto") ? PICK_IMAGE_REQUEST :
                        tipo.equals("Video") ? PICK_VIDEO_REQUEST : PICK_AUDIO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            archivoUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(archivoUri);
                archivoBytes = IOUtils.toByteArray(inputStream);

                txtArchivoSeleccionado.setText("Archivo: " + getFileName(archivoUri));
                txtArchivoSeleccionado.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Toast.makeText(this, "Error al leer archivo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;

        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }

        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result != null ? result : "desconocido";
    }

    private int obtenerFormato(Uri uri) {
        String mime = getContentResolver().getType(uri);
        if (mime == null) return 0;

        switch (mime) {
            case "image/jpeg": return 1;
            case "audio/mpeg": return 2;
            case "video/mp4": return 3;
            default: return 0;
        }
    }

    private int obtenerDuracion(Uri uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, uri);
            String duracionStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (duracionStr != null) {
                long duracionMs = Long.parseLong(duracionStr);
                return (int) (duracionMs / 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return 0;
    }
}
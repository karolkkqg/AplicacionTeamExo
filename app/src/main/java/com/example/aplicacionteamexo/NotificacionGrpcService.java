package com.example.aplicacionteamexo;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;
import android.os.Handler;
import android.util.Log;

import com.example.aplicacionteamexo.grpc.notificacion.NotificacionServiceGrpc;
import com.example.aplicacionteamexo.grpc.notificacion.StreamNotificacionesRequest;
import com.example.aplicacionteamexo.grpc.notificacion.NotificacionResponse;



import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.grpc.InsecureChannelCredentials;

public class NotificacionGrpcService {
    private final NotificacionServiceGrpc.NotificacionServiceStub asyncStub;
    private final Context context;

    public NotificacionGrpcService(Context context) {
        this.context = context;

        ManagedChannel channel = OkHttpChannelBuilder
                .forAddress("192.168.233.88", 50055)
                .usePlaintext()
                .build();

        asyncStub = NotificacionServiceGrpc.newStub(channel);
    }

    public void suscribirseANotificaciones(int usuarioId) {
        StreamNotificacionesRequest request = StreamNotificacionesRequest.newBuilder()
                .setUsuarioId(usuarioId)
                .build();

        asyncStub.streamNotificaciones(request, new StreamObserver<NotificacionResponse>() {
            @Override
            public void onNext(NotificacionResponse notificacion) {
                mostrarNotificacionEnApp(notificacion);
            }

            @Override
            public void onError(Throwable t) {
                Log.e("gRPC", "Error en stream de notificaciones", t);
            }

            @Override
            public void onCompleted() {
                Log.d("gRPC", "Stream de notificaciones completado");
            }
        });
    }

    private void mostrarNotificacionEnApp(NotificacionResponse notificacion) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(context, "🔔 " + notificacion.getMensaje(), Toast.LENGTH_LONG).show();
        });
    }
}

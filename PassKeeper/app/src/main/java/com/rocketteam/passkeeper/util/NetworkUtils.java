package com.rocketteam.passkeeper.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {
    public interface PremiumStatusCallback {
        void onResult(boolean isPremium);
        void onError(Exception e);
    }

    public static void getPremiumStatusFromAPI(String userEmail, PremiumStatusCallback callback) {

        if (userEmail == null) {
            callback.onError(new IllegalArgumentException("El email del usuario no puede ser nulo."));
            return;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("email", userEmail)
                    .add("key", "SMNVY6k0LcFkW2ygmgrVRNf09Bb2Kjpo")
                    .build();
            Request request = new Request.Builder()
                    .url("https://drf-passkeeper.onrender.com/account/is_premium/")
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                Log.i("TAG", "NetworkUtils: respuesta de API: " + responseBody);

                if (response.isSuccessful()) {
                    // Parsear la respuesta JSON
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean isPremium = jsonObject.optBoolean("is_premium", false);
                    handler.post(() -> callback.onResult(isPremium));
                } else {
                    handler.post(() -> callback.onError(new Exception("Error en la respuesta: " + response.code())));
                }
            } catch (IOException | JSONException e) {
                handler.post(() -> callback.onError(e));
            }
        });
    }
}


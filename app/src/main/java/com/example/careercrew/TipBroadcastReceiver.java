package com.example.careercrew;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TipBroadcastReceiver extends BroadcastReceiver {

    OkHttpClient client = new OkHttpClient();

    @Override
    public void onReceive(Context context, Intent intent) {
        String role = intent.getStringExtra("role");
        String message = intent.getStringExtra("message");
        String apiUrl = intent.getStringExtra("apiUrl");
        String apiKey = intent.getStringExtra("apiKey");
        String conversationId = intent.getStringExtra("conversationId");
        String personaId = intent.getStringExtra("personaId");

        if (role != null && message != null && apiUrl != null && apiKey != null && conversationId != null && personaId != null) {
            sendMessageToHyperleap(role, message, apiUrl, apiKey, conversationId, personaId, context);
        } else {
            Log.e("TipBroadcastReceiver", "Received intent with missing extras.");
        }
    }

    private void sendMessageToHyperleap(String role, String message, String apiUrl, String apiKey, String conversationId, String personaId, Context context) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("personaId", personaId);
            jsonBody.put("message", message);

            String requestUrl = apiUrl + conversationId + "/continue-sync";
            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(requestUrl)
                    .header("x-hl-api-key", apiKey)
                    .patch(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("API Request", "Failed to load response", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseObject = new JSONObject(response.body().string());
                            String tip = extractReply(responseObject);
                            Log.d("TipBroadcastReceiver", "Tip for " + role + ": " + tip);

                            Intent uiIntent = new Intent("com.example.careercrew.TIP_RESPONSE");
                            uiIntent.putExtra("tip", tip);
                            context.sendBroadcast(uiIntent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("API Request", "Failed to get tips. Response error: " + response.code());
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String extractReply(JSONObject responseObject) {
        try {
            if (responseObject.has("choices") && responseObject.getJSONArray("choices").length() > 0) {
                JSONObject choice = responseObject.getJSONArray("choices").getJSONObject(0);
                return choice.getJSONObject("message").getString("content");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "No reply found in the response.";
    }
}

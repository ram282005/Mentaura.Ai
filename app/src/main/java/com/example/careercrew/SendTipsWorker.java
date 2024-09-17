package com.example.careercrew;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

public class SendTipsWorker extends Worker {

    private static final String API_URL = "https://api.hyperleap.ai/conversations/";
    private static final String API_KEY = "YTE0M2JhN2FlNjFmNDNlNzhjM2UwZTBkNTg3ZjY1MmE="; // Replace with your Hyperleap API Key
    private static final String CONVERSATION_ID = "7bcf7e1d-2cd9-4c8e-aa5f-408cc6de3d76"; // Replace with the actual conversation ID
    private static final String PERSONA_ID = "3d7cab73-cf2c-4595-8638-e6c380fa2847"; // Replace with the persona ID

    private OkHttpClient client = new OkHttpClient();
    private DatabaseReference mDatabase;

    public SendTipsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public Result doWork() {
        String role = getInputData().getString("role");
        int messageIndex = getInputData().getInt("messageIndex", 0);
        String userEmailKey = getInputData().getString("userEmailKey"); // Fetch userEmailKey from input data

        if (role != null && userEmailKey != null) {
            fetchPersonalizedTips(role, messageIndex, userEmailKey);
        }

        return Result.success();
    }

    // Fetch personalized tips based on role and message index
    private void fetchPersonalizedTips(String role, int messageIndex, String userEmailKey) {
        String[] messages = {
                "Provide career tips for someone aspiring to be a " + role,
                "Provide motivation for someone aspiring to be a " + role,
                "Provide latest updates for someone aspiring to be a " + role
        };

        if (messageIndex >= messages.length) {
            return;
        }

        String message = messages[messageIndex];

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("personaId", PERSONA_ID);
            jsonBody.put("message", message);

            String apiUrl = API_URL + CONVERSATION_ID + "/continue-sync";
            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("x-hl-api-key", API_KEY)
                    .patch(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("API Request", "Failed to load response", e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseObject = new JSONObject(response.body().string());
                            String tip = extractReply(responseObject);

                            // Log the received tip for debugging
                            Log.d("Worker", "Received Tip: " + tip);

                            // Save the new tip to Firebase to ensure it is not shown again
                            saveTipToFirebase(role, tip, userEmailKey);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("API Request", "Failed to get tips. Response code: " + response.code());
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Extract the reply from the Hyperleap API response
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

    // Save the new tip to Firebase to avoid duplicates
    private void saveTipToFirebase(String role, String tip, String userEmailKey) {
        DatabaseReference userTipsRef = mDatabase.child("users").child(userEmailKey).child("career_tips").push();
        userTipsRef.setValue(tip);
    }
}

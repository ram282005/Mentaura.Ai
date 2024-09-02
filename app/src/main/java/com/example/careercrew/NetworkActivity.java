package com.example.careercrew;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<YourResponseModel> call = apiService.getResponse();

        call.enqueue(new Callback<YourResponseModel>() {
            @Override
            public void onResponse(Call<YourResponseModel> call, Response<YourResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    YourResponseModel responseData = response.body();
                    String convoId = responseData.getConvoId();
                    String message = responseData.getMessage();

                    // Do something with the response data
                    Toast.makeText(NetworkActivity.this, "Convo ID: " + convoId, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NetworkActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<YourResponseModel> call, Throwable t) {
                Toast.makeText(NetworkActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

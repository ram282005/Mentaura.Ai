package com.example.careercrew;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({
            "Authorization: Bearer YOUR_API_KEY_HERE",
            "Content-Type: application/json"
    })
    @POST("your-endpoint")
    Call<YourResponseType> getResponse(@Body YourRequestType request);
}

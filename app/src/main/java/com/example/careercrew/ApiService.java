package com.example.careercrew;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ApiService {
    @GET("conversations/persona")
    @Headers("x-hl-api-key: YzhlMGM1MzAyY2YwNDU3MDk2NmRiNDI0OWM5MTI4NTE=")
    Call<YourResponseModel> getResponse();
}

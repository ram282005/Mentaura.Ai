package com.example.careercrew;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

interface HyperLeapService {

    @GET("/fetch-roadmap")
    Call<ApiResponse> getRoadmap(
            @Header("YTE0M2JhN2FlNjFmNDNlNzhjM2UwZTBkNTg3ZjY1MmE=") String apiKey,
            @Header("7b53326e-8121-4936-b931-f1809fcf7f3e") String personaId,
            @Header("c2dca827-5ddb-41b6-8963-dc1bb3443065") String convoId,
            @Query("roadmap+role") String query
    );

    @GET("/fetch-weekly-goals")
    Call<ApiResponse> getWeeklyGoals(
            @Header("YTE0M2JhN2FlNjFmNDNlNzhjM2UwZTBkNTg3ZjY1MmE=") String apiKey,
            @Header("7b53326e-8121-4936-b931-f1809fcf7f3e") String personaId,
            @Header("c2dca827-5ddb-41b6-8963-dc1bb3443065") String convoId,
            @Query("weekly assesment+ role+weekly goals") String query
    );
}

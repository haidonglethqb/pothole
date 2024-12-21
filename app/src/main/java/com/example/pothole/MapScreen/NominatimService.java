package com.example.pothole.MapScreen;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NominatimService {
    @GET("reverse")
    Call<NominatimResponse> getStreetName(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("format") String format
    );
}

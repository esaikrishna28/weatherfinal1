package com.example.weather.airmodel;

import retrofit2.converter.gson.GsonConverterFactory;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AirService {
    //http://api.openweathermap.org/data/2.5/air_pollution?lat=50&lon=50&appid=e52cee364ac0886a6d8878e7fbd3e679

    AirService airService = new Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AirService.class);

    @GET("data/2.5/air_pollution")
    Call<AirResponse> AirInformation(@Query("lat") double lat,
                                     @Query("lon") double lon,
                                     @Query("appid") String appid);
}

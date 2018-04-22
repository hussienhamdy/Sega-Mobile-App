package com.example.ussien.sega.Model;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by ussien on 18/09/2017.
 */

public class ApiClient {
    public static final String baseUrl="https://segateam.000webhostapp.com/api/";
    public static Retrofit retrofit=null;
    public static Retrofit getApiClient()
    {
        if(retrofit==null)
            retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).
                    addConverterFactory(ScalarsConverterFactory.create())
            .build();
        return retrofit;
    }
}

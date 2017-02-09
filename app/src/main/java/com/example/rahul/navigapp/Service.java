package com.example.rahul.navigapp;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Rahul on 1/19/2017.
 */

public class Service {

    public void getClient() {

        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }
}

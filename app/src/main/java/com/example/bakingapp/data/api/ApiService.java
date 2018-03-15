package com.example.bakingapp.data.api;

import com.example.bakingapp.data.models.RecipesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Victor Holotescu on 07-03-2018.
 */

public interface ApiService {

    @GET("android-baking-app-json")
    Call<List<RecipesResponse>> getRecipeResponse();
}

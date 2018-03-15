package com.example.bakingapp.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.bakingapp.data.db.RecipeDao;
import com.example.bakingapp.data.db.RecipeRoomDatabase;
import com.example.bakingapp.data.models.Ingredient;
import com.example.bakingapp.data.models.RecipeDetails;
import com.example.bakingapp.data.api.ApiService;
import com.example.bakingapp.data.models.Recipe;
import com.example.bakingapp.data.models.RecipesResponse;
import com.example.bakingapp.data.models.Step;
import com.example.bakingapp.utils.AppExecutors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Victor Holotescu on 09-03-2018.
 */

@Singleton
public class RecipesRepository {

    private final RecipeRoomDatabase mDb;
    private final RecipeDao mRecipeDao;
    private final AppExecutors mAppExecutors;
    private final ApiService mApiService;

    @Inject
    public RecipesRepository(AppExecutors appExecutors, RecipeRoomDatabase db, RecipeDao recipeDao,
                             ApiService apiService) {
        mDb = db;
        mRecipeDao = recipeDao;
        mApiService = apiService;
        mAppExecutors = appExecutors;
    }

    /**
     * Gets data from database
     * Update data from network if available and if diferent from database
     * Else error
     */
    public LiveData<List<RecipeDetails>> getInitialRecipeList() {
        MediatorLiveData<List<RecipeDetails>> recipesList = new MediatorLiveData<>();

        LiveData<List<RecipeDetails>> databaseRecipes = mRecipeDao.getRecipeDetails();

        recipesList.addSource(databaseRecipes, newData -> {
            if (recipesList.getValue() != newData) {
                recipesList.setValue(newData);
            }
        });

        LiveData<List<RecipesResponse>> networkRecipesList = getRecipesFromNetwork();
        networkRecipesList.observeForever(recipesResponse -> {
            if (recipesResponse != null && recipesResponse.size() > 0) {
                mAppExecutors.networkIO().execute(() -> {
                    List<Recipe> recipes = getParsedResponse(recipesResponse);
                    addRecipesToDatabase(recipes);
                });
            } else {
                recipesList.setValue(null);
            }
        });

        return recipesList;
    }

    public LiveData<Recipe> getRecipeById(int id) {
        return mRecipeDao.getRecipeById(id);
    }

    public List<Ingredient> getWidgetIngredientseById(int id) {
        return mRecipeDao.getWidgetIngredientsByRecipeId(id);
    }

    public LiveData<List<Step>> getSteps(int recipeId) {
        if (recipeId == -1) return null;
        else return mRecipeDao.getSteps(recipeId);
    }

    //executed on main thread
    //called from intent service
    public List<Recipe> getWidgetRecipes() {
        List<Recipe> recipes = mRecipeDao.getWidgetRecipes();
        if (recipes != null && recipes.size() > 0) {
            return recipes;
        } else {
            try {
                List<RecipesResponse> responseList = mApiService.getRecipeResponse().execute().body();
                recipes = getParsedResponse(responseList);
                addRecipesToDatabase(recipes);
                return recipes;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private LiveData<List<RecipesResponse>> getRecipesFromNetwork() {
        final MutableLiveData<List<RecipesResponse>> data = new MutableLiveData<>();

        mApiService.getRecipeResponse().enqueue(new Callback<List<RecipesResponse>>() {
            @Override
            public void onResponse(Call<List<RecipesResponse>> call, Response<List<RecipesResponse>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }

            }

            @Override
            public void onFailure(Call<List<RecipesResponse>> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;
    }


    private List<Recipe> getParsedResponse(List<RecipesResponse> recipesResponse) {
        List<Recipe> recipeList = new ArrayList<>();
        for (int i = 0; i < recipesResponse.size(); i++) {

            int recipeId = recipesResponse.get(i).getId();

            String recipeName = recipesResponse.get(i).getName();
            int recipeServings = recipesResponse.get(i).getServings();
            String imagePath = recipesResponse.get(i).getImage();
            RecipeDetails recipeDetails = new RecipeDetails(recipeId, recipeName, recipeServings, imagePath);

            List<Ingredient> ingredients = recipesResponse.get(i).getIngredients();
            for (int j = 0; j < ingredients.size(); j++) {
                ingredients.get(j).setRecipeId(recipeId);
            }

            List<Step> steps = recipesResponse.get(i).getSteps();
            for (int k = 0; k < steps.size(); k++) {
                steps.get(k).setRecipeId(recipeId);
            }

            Recipe recipe = new Recipe();
            recipe.setRecipeDetails(recipeDetails);
            recipe.setSteps(steps);
            recipe.setIngredients(ingredients);

            recipeList.add(recipe);
        }
        return recipeList;
    }

    private void addRecipesToDatabase(List<Recipe> recipes) {

        try {
            mDb.beginTransaction();
            mDb.recipeDao().deleteAll();
            for (int i = 0; i < recipes.size(); i++) {
                mDb.recipeDao().insertRecipeDetails(recipes.get(i).getRecipeDetails());
                mDb.recipeDao().insertSteps(recipes.get(i).getSteps());
                mDb.recipeDao().insertIngredients(recipes.get(i).getIngredients());
            }

            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
    }
}

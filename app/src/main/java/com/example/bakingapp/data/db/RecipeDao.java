package com.example.bakingapp.data.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.example.bakingapp.data.models.Ingredient;
import com.example.bakingapp.data.models.Recipe;
import com.example.bakingapp.data.models.RecipeDetails;
import com.example.bakingapp.data.models.Step;

import java.util.List;

/**
 * Created by Victor Holotescu on 07-03-2018.
 */
@Dao
public abstract class RecipeDao {

    @Insert
    public abstract void insertRecipeDetails(RecipeDetails recipe);

    @Insert
    public abstract void insertSteps(List<Step> steps);

    @Insert
    public abstract void insertIngredients(List<Ingredient> ingredients);

    @Query("DELETE FROM RecipeDetails")
    public abstract void deleteAll();

    @Query("SELECT * FROM RecipeDetails")
    @Transaction
    public abstract LiveData<List<Recipe>> getRecipes();

    @Query("SELECT * FROM RecipeDetails")
    @Transaction
    public abstract List<Recipe> getWidgetRecipes();

    @Query("SELECT * FROM RecipeDetails")
    @Transaction
    public abstract LiveData<List<RecipeDetails>> getRecipeDetails();

    @Query("SELECT * FROM Ingredient WHERE recipeId = :id")
    public abstract List<Ingredient> getWidgetIngredientsByRecipeId(int id);

    @Query("SELECT * FROM RecipeDetails WHERE id = :id")
    @Transaction
    public abstract LiveData<Recipe> getRecipeById(int id);

    @Query("SELECT * FROM Step WHERE recipeId = :recipeId")
    public abstract LiveData<List<Step>> getSteps(int recipeId);
}

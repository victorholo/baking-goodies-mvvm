package com.example.bakingapp.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.bakingapp.data.models.Ingredient;
import com.example.bakingapp.data.models.RecipeDetails;
import com.example.bakingapp.data.models.Step;

/**
 * Created by Victor Holotescu on 07-03-2018.
 */

@Database(entities = {RecipeDetails.class, Ingredient.class, Step.class}, version = 1)
public abstract class RecipeRoomDatabase extends RoomDatabase{

    public abstract RecipeDao recipeDao();


}

package com.example.bakingapp.data.models;

import java.util.ArrayList;
import java.util.List;
import android.arch.persistence.room.Relation;
import android.arch.persistence.room.Embedded;
/**
 * Created by Victor Holotescu on 09-03-2018.
 */

public class Recipe {

    @Embedded
    private RecipeDetails recipeDetails;

    @Relation(parentColumn = "id",
            entityColumn = "recipeId", entity = Ingredient.class)
    private List<Ingredient> ingredients = new ArrayList<>();

    @Relation(parentColumn = "id",
            entityColumn = "recipeId", entity = Step.class)
    private List<Step> steps = new ArrayList<>();

    public RecipeDetails getRecipeDetails() {
        return recipeDetails;
    }

    public void setRecipeDetails(RecipeDetails recipeDetails) {
        this.recipeDetails = recipeDetails;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}

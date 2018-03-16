package com.example.bakingapp.widget;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.bakingapp.data.models.Ingredient;
import com.example.bakingapp.data.models.Recipe;
import com.example.bakingapp.data.repository.RecipesRepository;
import com.example.bakingapp.ui.steps.StepsViewModel;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Victor Holotescu on 16-03-2018.
 */

public class WidgetViewModel extends ViewModel {

    private final RecipesRepository mRepository;

    private final LiveData<List<Recipe>> recipes;

    @Inject
    public WidgetViewModel(RecipesRepository repository) {
        mRepository = repository;
        recipes = mRepository.getConfigurationRecipes();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    public Recipe getRecipeByPosition(int position){
        return recipes.getValue().get(position);
    }

    public List<Ingredient> getIngredientsIdByPosition(int position){
        return recipes.getValue().get(position).getIngredients();
    }
}

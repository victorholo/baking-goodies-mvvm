package com.example.bakingapp.ui.recipes;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.example.bakingapp.data.models.RecipeDetails;
import com.example.bakingapp.data.repository.RecipesRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Victor Holotescu on 09-03-2018.
 */

public class RecipesViewModel extends ViewModel {

    private final RecipesRepository mRepository;

    private final LiveData<List<RecipeDetails>> recipesDetails;

    @VisibleForTesting
    private final MutableLiveData<Boolean> refresh = new MutableLiveData<>();

    @SuppressWarnings("unchecked")
    @Inject
    public RecipesViewModel(RecipesRepository repository) {
        mRepository = repository;
        refresh.setValue(true);
        recipesDetails = Transformations.switchMap(refresh, refresh ->  mRepository.getInitialRecipeList());
    }

    @VisibleForTesting
    public LiveData<List<RecipeDetails>> getRecipes() {
        return recipesDetails;
    }

    @VisibleForTesting
    public void refreshRecipes() {
        refresh.setValue(true);
    }
}

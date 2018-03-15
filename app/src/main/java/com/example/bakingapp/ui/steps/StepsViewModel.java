package com.example.bakingapp.ui.steps;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.bakingapp.data.models.Recipe;
import com.example.bakingapp.data.models.Step;
import com.example.bakingapp.data.repository.RecipesRepository;

import javax.inject.Inject;

/**
 * Created by Victor Holotescu on 12-03-2018.
 */

public class StepsViewModel extends ViewModel {

    private final RecipesRepository mRepository;

    private final MutableLiveData<Integer> recipeId = new MutableLiveData<>();
    private final LiveData<Recipe> recipes;

    OnStepItemClickedListener mOnStepItemClickListener;

    @Inject
    public StepsViewModel(RecipesRepository repository) {
        mRepository = repository;
        recipes = Transformations.switchMap(recipeId, recipeId -> mRepository.getRecipeById(recipeId));
    }

    public void setRecipeId(int id) {
        this.recipeId.setValue(id);
    }

    public int getRecipeId() {
        return recipeId.getValue();
    }

    public LiveData<Recipe> getRecipe() {
        return recipes;
    }



    public void onStepClicked(Step step){
        mOnStepItemClickListener.onStepItemClicked(step);
    }

    public void setStepItemClickListener(OnStepItemClickedListener listener){
        mOnStepItemClickListener = listener;
    }

    public OnStepItemClickedListener getStepItemClickListener(){
        return mOnStepItemClickListener;
    }

    public interface OnStepItemClickedListener {
        void onStepItemClicked(Step step);
    }

}

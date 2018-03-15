package com.example.bakingapp.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.bakingapp.data.models.Step;
import com.example.bakingapp.data.repository.RecipesRepository;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Victor Holotescu on 12-03-2018.
 */

public class DetailViewModel extends ViewModel {

    private final MutableLiveData<Integer> mRecipeId;
    private final MutableLiveData<Integer> mStepId;
    private final RecipesRepository mRepository;

    private final LiveData<List<Step>> mSteps;
    private final MediatorLiveData<Step> mCurrentStep = new MediatorLiveData<>();

    @Inject
    public DetailViewModel(RecipesRepository repository) {
        mRepository = repository;
        this.mRecipeId = new MutableLiveData<>();
        this.mStepId = new MutableLiveData<>();

        mSteps = Transformations.switchMap(mRecipeId, input ->
        {
            if(input != null) {
                return mRepository.getSteps(input);
            }
            return null;
        });

        mCurrentStep.addSource(mSteps, input -> {
            if(input != null && !input.isEmpty() && mStepId.getValue() != null){
                mCurrentStep.setValue(input.get(mStepId.getValue()));
            }else mCurrentStep.setValue(input.get(0));
        });

        mCurrentStep.addSource(mStepId, input -> {
            if(input != null && mSteps.getValue() != null && !mSteps.getValue().isEmpty()){
                mCurrentStep.setValue(mSteps.getValue().get(input));
            }else mCurrentStep.setValue(null);
        });
    }

    public Boolean hasNext(){
        return mSteps.getValue().size() - 1 > mStepId.getValue();
    }

    public Boolean hasPrevious(){
        return 0 < mStepId.getValue();
    }
    public void getNext(){
        mStepId.setValue(mStepId.getValue() + 1);
    }

    public void getPrevious(){
        mStepId.setValue(mStepId.getValue() - 1);
    }

    public LiveData<Step> getStep(){
        return mCurrentStep;
    }

    public void setRecipeId(int recipeId) {
        if(mRecipeId.getValue() == null || mRecipeId.getValue() != recipeId) {
            mRecipeId.setValue(recipeId);
        }
    }

    public void setStepId(int stepId) {
        if(mStepId.getValue() == null || mStepId.getValue() != stepId){
            mStepId.setValue(stepId);
        }
    }

    public int getRecipeId(){
        return mRecipeId.getValue();
    }

    public int getStepId(){
        if(mStepId.getValue() == null) return -1;
        return mStepId.getValue();
    }

}

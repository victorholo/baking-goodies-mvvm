package com.example.bakingapp.ui.steps;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bakingapp.R;
import com.example.bakingapp.data.models.Step;
import com.example.bakingapp.databinding.FragmentStepsBinding;
import com.example.bakingapp.di.Injectable;
import com.example.bakingapp.utils.RecipesUtils;

import javax.inject.Inject;

import static com.example.bakingapp.utils.RecipesUtils.RECIPE_ID_EXTRA;


public class StepsFragment extends Fragment implements Injectable, StepsAdapter.StepAdapterOnItemClickHandler {

    private static final String SCROLL_POSITION_EXTRA = "scroll_position_extra";
    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private FragmentStepsBinding mFragmentStepsBinding;
    private StepsAdapter mStepsAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private StepsViewModel mStepsViewModel;

    private StepsViewModel.OnStepItemClickedListener mOnClickListener;
    private int[] mScrollPosition = new int[2];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentStepsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_steps, container, false);

        return mFragmentStepsBinding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mStepsAdapter = new StepsAdapter(this);
        mFragmentStepsBinding.recyclerView.setNestedScrollingEnabled(false);
        mFragmentStepsBinding.recyclerView.setAdapter(mStepsAdapter);

        mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mFragmentStepsBinding.recyclerView.setLayoutManager(mLinearLayoutManager);

        // Inflate the layout for this fragment
        mStepsViewModel = ViewModelProviders.of(this, mViewModelFactory).get(StepsViewModel.class);

        if(mStepsViewModel.getStepItemClickListener() == null){
            mStepsViewModel.setStepItemClickListener(mOnClickListener);
        }

        int recipeId;
        if (savedInstanceState == null) recipeId = getArguments().getInt(RECIPE_ID_EXTRA, -1);
        else {
            recipeId = savedInstanceState.getInt(RECIPE_ID_EXTRA, -1);
            mScrollPosition = savedInstanceState.getIntArray(SCROLL_POSITION_EXTRA);
        }

        if (recipeId != -1) {
            mStepsViewModel.setRecipeId(recipeId);
        }
        // Update the list when the data changes
        mStepsViewModel.getRecipe().observe(this, recipe -> {
            if (recipe != null) {
                ActionBar activityActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                if (activityActionBar != null)
                    activityActionBar.setTitle(recipe.getRecipeDetails().getName());
                mFragmentStepsBinding.ingredientsLayout.ingredientsTextView.setText(RecipesUtils.getIngredientsTextFromList(recipe.getIngredients()));
                mStepsAdapter.swapSteps(recipe.getSteps());
                mFragmentStepsBinding.scrollView.scrollTo(mScrollPosition[0], mScrollPosition[1]);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RECIPE_ID_EXTRA, mStepsViewModel.getRecipeId());
        outState.putIntArray(SCROLL_POSITION_EXTRA, new int[]{mFragmentStepsBinding.scrollView.getScrollX(), mFragmentStepsBinding.scrollView.getScrollY()});
    }

    public void setStepItemClickListener(StepsViewModel.OnStepItemClickedListener listener){
        mOnClickListener = listener;
    }

    @Override
    public void onItemClick(Step step) {
        mStepsViewModel.onStepClicked(step);
    }
}

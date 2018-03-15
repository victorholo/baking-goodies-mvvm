package com.example.bakingapp.ui.recipes;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bakingapp.R;
import com.example.bakingapp.data.models.RecipeDetails;
import com.example.bakingapp.databinding.FragmentRecipesBinding;
import com.example.bakingapp.di.Injectable;
import com.example.bakingapp.ui.StepsActivity;

import java.util.List;

import javax.inject.Inject;

import static com.example.bakingapp.utils.RecipesUtils.RECIPE_ID_EXTRA;

public class RecipesFragment extends Fragment implements Injectable, RecipesAdapter.RecipeAdapterOnItemClickHandler, View.OnClickListener {


    private static final String RECYCLER_POSITION_STATE = "recycler_position_state";

    @Inject
    public
    ViewModelProvider.Factory mViewModelFactory;

    private RecipesViewModel mRecipesViewModel;

    private RecipesAdapter mRecipesAdapter;

    private FragmentRecipesBinding mFragmentRecipesBinding;

    private LinearLayoutManager mLinearLayoutManager;

    private GridLayoutManager mGridLayoutManager;

    private Parcelable mRecyclerPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mFragmentRecipesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipes, container, false);

        return mFragmentRecipesBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecipesAdapter = new RecipesAdapter(this);
        mFragmentRecipesBinding.recyclerView.setAdapter(mRecipesAdapter);

        if(savedInstanceState != null){
            mRecyclerPosition = savedInstanceState.getParcelable(RECYCLER_POSITION_STATE);
        }
        if(getResources().getBoolean(R.bool.is_tablet)){
            mGridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), numberOfColumns());
            mFragmentRecipesBinding.recyclerView.setLayoutManager(mGridLayoutManager);
        }else{
            mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            mFragmentRecipesBinding.recyclerView.setLayoutManager(mLinearLayoutManager);
        }

        mFragmentRecipesBinding.recyclerView.setHasFixedSize(true);
        mFragmentRecipesBinding.retryButton.setOnClickListener(this);
        showLoading();
        // Inflate the layout for this fragment
        mRecipesViewModel = ViewModelProviders.of(this, mViewModelFactory).get(RecipesViewModel.class);
        // Update the list when the data changes
        mRecipesViewModel.getRecipes().observe(this, recipes -> {
            // Show error if list is null, it means that no data in database and also no internet connection
            if(recipes != null && recipes.size() != 0) {
                showList(recipes);
                if(mGridLayoutManager != null) mGridLayoutManager.onRestoreInstanceState(mRecyclerPosition);
                else if(mLinearLayoutManager != null) mLinearLayoutManager.onRestoreInstanceState(mRecyclerPosition);
            }else if(recipes != null){
                showError();
            }
        });
    }

    private void showLoading(){
        mFragmentRecipesBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
        mFragmentRecipesBinding.recyclerView.setVisibility(View.GONE);
        mFragmentRecipesBinding.errorTextview.setVisibility(View.GONE);
        mFragmentRecipesBinding.retryButton.setVisibility(View.GONE);
    }

    private void showError(){
        mFragmentRecipesBinding.pbLoadingIndicator.setVisibility(View.GONE);
        mFragmentRecipesBinding.recyclerView.setVisibility(View.GONE);
        mFragmentRecipesBinding.errorTextview.setVisibility(View.VISIBLE);
        mFragmentRecipesBinding.retryButton.setVisibility(View.VISIBLE);
    }

    private void showList(List<RecipeDetails> recipes){
        mFragmentRecipesBinding.pbLoadingIndicator.setVisibility(View.GONE);
        mFragmentRecipesBinding.recyclerView.setVisibility(View.VISIBLE);
        mFragmentRecipesBinding.errorTextview.setVisibility(View.GONE);
        mFragmentRecipesBinding.retryButton.setVisibility(View.GONE);
        mRecipesAdapter.swapRecipes(recipes);
    }

    //recyclerview click handler
    @Override
    public void onItemClick(int id) {
        Intent intent = new Intent(getActivity(), StepsActivity.class);
        intent.putExtra(RECIPE_ID_EXTRA, id);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.retry_button){
            showLoading();
            mRecipesViewModel.refreshRecipes();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mGridLayoutManager != null) {
            outState.putParcelable(RECYCLER_POSITION_STATE, mGridLayoutManager.onSaveInstanceState());
        }else {
            outState.putParcelable(RECYCLER_POSITION_STATE, mLinearLayoutManager.onSaveInstanceState());
        }
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 600;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }
}

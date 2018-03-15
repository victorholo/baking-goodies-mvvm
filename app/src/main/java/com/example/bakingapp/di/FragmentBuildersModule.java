package com.example.bakingapp.di;

import com.example.bakingapp.ui.detail.DetailFragment;
import com.example.bakingapp.ui.recipes.RecipesFragment;
import com.example.bakingapp.ui.steps.StepsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Victor Holotescu on 09-03-2018.
 */

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract RecipesFragment contributeRecipesFragment();

    @ContributesAndroidInjector
    abstract DetailFragment contributeDetailFragment();

    @ContributesAndroidInjector
    abstract StepsFragment contributeStepsFragment();
}

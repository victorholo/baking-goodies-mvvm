package com.example.bakingapp.di;

import com.example.bakingapp.ui.DetailActivity;
import com.example.bakingapp.ui.StepsActivity;
import com.example.bakingapp.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Victor Holotescu on 09-03-2018.
 */

@Module
public abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract StepsActivity contributeStepsActivity();

    @ContributesAndroidInjector
    abstract DetailActivity contributeDetailActivity();
}

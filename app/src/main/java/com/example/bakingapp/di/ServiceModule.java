package com.example.bakingapp.di;

import com.example.bakingapp.widget.IngredientsRemoteViewsService;
import com.example.bakingapp.widget.RecipeWidgetService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Victor Holotescu on 13-03-2018.
 */
@Module
public abstract class ServiceModule {
    @ContributesAndroidInjector
    abstract RecipeWidgetService contributeWidgetService();

    @ContributesAndroidInjector
    abstract IngredientsRemoteViewsService contributeRemoteViewsService();
}

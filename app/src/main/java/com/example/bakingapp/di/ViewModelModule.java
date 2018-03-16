package com.example.bakingapp.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.bakingapp.ui.detail.DetailViewModel;
import com.example.bakingapp.ui.recipes.RecipesViewModel;
import com.example.bakingapp.ui.steps.StepsViewModel;
import com.example.bakingapp.viewmodels.ViewModelFactory;
import com.example.bakingapp.widget.WidgetViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by Victor Holotescu on 09-03-2018.
 */

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(WidgetViewModel.class)
    abstract ViewModel bindWidgetViewModel(WidgetViewModel widgetViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RecipesViewModel.class)
    abstract ViewModel bindRecipesViewModel(RecipesViewModel recipesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(StepsViewModel.class)
    abstract ViewModel bindStepsViewModel(StepsViewModel stepsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel.class)
    abstract ViewModel bindDetailViewModel(DetailViewModel detailViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}

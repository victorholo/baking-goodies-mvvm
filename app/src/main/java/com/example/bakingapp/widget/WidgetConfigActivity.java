package com.example.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.bakingapp.R;
import com.example.bakingapp.data.models.Recipe;
import com.example.bakingapp.databinding.ActivityWidgetConfigBinding;
import com.example.bakingapp.di.Injectable;
import com.example.bakingapp.ui.detail.DetailViewModel;
import com.example.bakingapp.utils.RecipesUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class WidgetConfigActivity extends AppCompatActivity implements Injectable, View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String PREF_WIDGET_PREFIX = "widget_pref_id_";
    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    WidgetViewModel mWidgetViewModel;

    ActivityWidgetConfigBinding mActivityWidgetConfigDataBinding;

    ArrayAdapter mSpinnerAdapter;

    List<String> mRecipes;
    SharedPreferences mPrefs;

    int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityWidgetConfigDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_widget_config);


        AndroidInjection.inject(this);
        setResult(RESULT_CANCELED);


        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Set layout size of activity
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRecipes = new ArrayList<>();

        mSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mRecipes);

        mActivityWidgetConfigDataBinding.widgetSpinnerRecipes.setAdapter(mSpinnerAdapter);
        mActivityWidgetConfigDataBinding.widgetSpinnerRecipes.setOnItemSelectedListener(this);
        mActivityWidgetConfigDataBinding.addRecipeWidget.setOnClickListener(this);

        mWidgetViewModel = ViewModelProviders.of(this, mViewModelFactory).get(WidgetViewModel.class);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        int recipeId = loadRecipeId(this, mAppWidgetId);

        mWidgetViewModel.getRecipes().observe(this, recipes -> {
            if(recipes != null){
                mRecipes.clear();
                int selectedRecipe = 0;
                for(int i = 0; i < recipes.size(); i++){
                    if(recipes.get(i).getRecipeDetails().getId() == recipeId) selectedRecipe = i;
                    mRecipes.add(recipes.get(i).getRecipeDetails().getName());
                }

                mSpinnerAdapter.notifyDataSetChanged();
                mActivityWidgetConfigDataBinding.widgetSpinnerRecipes.setSelection(selectedRecipe);
                mActivityWidgetConfigDataBinding.widgetIngredients.setText(RecipesUtils.getIngredientsTextFromList(recipes.get(selectedRecipe).getIngredients()));
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.add_recipe_widget){
            Recipe recipe = mWidgetViewModel.getRecipeByPosition(mActivityWidgetConfigDataBinding.widgetSpinnerRecipes.getSelectedItemPosition());
            createWidget(this, recipe);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mActivityWidgetConfigDataBinding.widgetIngredients.setText(RecipesUtils.getIngredientsTextFromList(mWidgetViewModel.getIngredientsIdByPosition(i)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void createWidget(Context context, Recipe recipe) {
        // Store the string locally
        saveRecipeId( mAppWidgetId, recipe.getRecipeDetails().getId());

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RecipeWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId, recipe);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    // Write the prefix to the SharedPreferences object for this widget
    private void saveRecipeId(int appWidgetId, int recipeId) {
        SharedPreferences.Editor e = mPrefs.edit();
        e.putInt(PREF_WIDGET_PREFIX + appWidgetId, recipeId);
        e.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static  int loadRecipeId(Context context, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(PREF_WIDGET_PREFIX + appWidgetId, 0);
    }

    public static void deleteRecipeIdPref(Context context, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.remove(PREF_WIDGET_PREFIX + appWidgetId);
        e.apply();
    }
}

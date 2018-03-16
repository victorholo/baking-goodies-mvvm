package com.example.bakingapp.widget;

import android.app.IntentService;
import android.app.Notification;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;

import com.example.bakingapp.R;
import com.example.bakingapp.data.models.Recipe;
import com.example.bakingapp.data.repository.RecipesRepository;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.example.bakingapp.utils.RecipesUtils.RECIPE_ID_EXTRA;
import static com.example.bakingapp.widget.RecipeWidget.APP_WIDGET_ID_EXTRA;

/**
 * Created by Victor Holotescu on 13-03-2018.
 */

public class RecipeWidgetService extends IntentService {

    @Inject
    RecipesRepository mRecipesRepository;

    public RecipeWidgetService() {
        super("RecipeWidgetService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, new Notification());
        AndroidInjection.inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        int recipeId = intent.getIntExtra(RECIPE_ID_EXTRA, 0);
        int appWidgetId = intent.getIntExtra(APP_WIDGET_ID_EXTRA, AppWidgetManager.INVALID_APPWIDGET_ID);

        if(appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return;

        Recipe recipe = mRecipesRepository.getWidgetRecipeById(recipeId);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_ingredients_list);

        if (recipe != null) {
            RecipeWidget.updateAppWidget(this, appWidgetManager, appWidgetId, recipe);
        } else {
            RecipeWidget.updateAppWidget(this, appWidgetManager, appWidgetId, null);
        }

    }

    public static void updateWidget(Context context, int recipeId, int appWidgetId) {
        Intent intent = new Intent(context, RecipeWidgetService.class);
        intent.putExtra(APP_WIDGET_ID_EXTRA, appWidgetId);
        intent.putExtra(RECIPE_ID_EXTRA, recipeId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

}
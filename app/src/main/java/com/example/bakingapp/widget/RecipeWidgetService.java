package com.example.bakingapp.widget;

import android.app.IntentService;
import android.app.Notification;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;

import com.example.bakingapp.R;
import com.example.bakingapp.data.models.Recipe;
import com.example.bakingapp.data.repository.RecipesRepository;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

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

        List<Recipe> recipes = mRecipesRepository.getWidgetRecipes();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidget.class));

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_ingredients_list);

        if(recipes != null && recipes.size() > 0){
            //will get the recipe of the day, every day the next recipe in the database
            Random r = new Random();
            int randomRecipe = r.nextInt(recipes.size());

            //Now update all widgets
            RecipeWidget.updateRecipeWidgets(this, appWidgetManager, appWidgetIds, recipes.get(randomRecipe));
        }else{
            //Now update all widgets
            RecipeWidget.updateRecipeWidgets(this, appWidgetManager, appWidgetIds, null);
        }

    }

    public static void updateWidget(Context context){
        Intent intent = new Intent(context, RecipeWidgetService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

}

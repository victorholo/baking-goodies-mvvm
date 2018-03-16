package com.example.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.example.bakingapp.R;
import com.example.bakingapp.data.models.Recipe;
import com.example.bakingapp.ui.StepsActivity;
import com.example.bakingapp.ui.MainActivity;

import static com.example.bakingapp.utils.RecipesUtils.RECIPE_ID_EXTRA;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidget extends AppWidgetProvider {

    public static final String APP_WIDGET_ID_EXTRA = "app_widget_id_extra";

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, Recipe recipe) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_list_widget);

        if (recipe != null) {
            views.setViewVisibility(R.id.widget_title, View.VISIBLE);
            views.setViewVisibility(R.id.widget_ingredients_list, View.VISIBLE);
            views.setViewVisibility(R.id.widget_error_layout, View.GONE);

            Intent intent = new Intent(context, IngredientsRemoteViewsService.class);
            intent.setData(Uri.fromParts("content", String.valueOf(recipe.getRecipeDetails().getId()), null));

            views.setRemoteAdapter(R.id.widget_ingredients_list, intent);
            // Set the PlantDetailActivity intent to launch when clicked
            Intent appIntent = new Intent(context, StepsActivity.class);
            appIntent.putExtra(RECIPE_ID_EXTRA, recipe.getRecipeDetails().getId());
            PendingIntent appPendingIntent = PendingIntent.getActivity(context, appWidgetId, appIntent, 0);

            views.setPendingIntentTemplate(R.id.widget_ingredients_list, appPendingIntent);

            views.setTextViewText(R.id.widget_title, recipe.getRecipeDetails().getName());
            views.setOnClickPendingIntent(R.id.widget_title, appPendingIntent);

        } else {
            views.setViewVisibility(R.id.widget_title, View.GONE);
            views.setViewVisibility(R.id.widget_ingredients_list, View.GONE);
            views.setViewVisibility(R.id.widget_error_layout, View.VISIBLE);

            Intent intent = new Intent(context, RecipeWidgetService.class);
            PendingIntent pendingIntent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                pendingIntent = PendingIntent.getForegroundService(context, 1, intent, 0);
            } else {
                pendingIntent = PendingIntent.getService(context, 1, intent, 0);
            }
            views.setOnClickPendingIntent(R.id.widget_retry_button, pendingIntent);

            Intent intentMainActivity = new Intent(context, MainActivity.class);
            PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 1, intentMainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_error_layout, activityPendingIntent);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
             int recipeId = WidgetConfigActivity.loadRecipeId(context, appWidgetId);
            RecipeWidgetService.updateWidget(context, recipeId, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            WidgetConfigActivity.deleteRecipeIdPref(context, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


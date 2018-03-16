package com.example.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.bakingapp.R;
import com.example.bakingapp.data.models.Ingredient;
import com.example.bakingapp.data.repository.RecipesRepository;
import com.example.bakingapp.utils.RecipesUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.example.bakingapp.utils.RecipesUtils.RECIPE_ID_EXTRA;


/**
 * Created by Victor Holotescu on 03-03-2018.
 */

public class IngredientsRemoteViewsService extends RemoteViewsService {
    @Inject
    RecipesRepository mRecipesRepository;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        AndroidInjection.inject(this);
        if(intent == null) return null;
        int recipeId = Integer.valueOf(intent.getData().getSchemeSpecificPart());
        return new ListRemoteViewsFactory(this.getApplicationContext(), recipeId, mRecipesRepository);
    }


    class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        final Context mContext;
        final int mRecipeId;
        List<Ingredient> mIngredients;
        final RecipesRepository mRecipesRepository;

        public ListRemoteViewsFactory(Context context, int recipeId, RecipesRepository recipesRepository) {
            mContext = context;
            mRecipeId = recipeId;
            mRecipesRepository = recipesRepository;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            mIngredients = mRecipesRepository.getWidgetIngredientsById(mRecipeId);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (mIngredients == null) return 0;
            return mIngredients.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_widget);
            views.setTextViewText(R.id.widget_ingredient_name, RecipesUtils.getIngredientTextFromObject(mIngredients.get(position)));
            views.setOnClickFillInIntent(R.id.widget_ingredient_name, new Intent());
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


    }
}

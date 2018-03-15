
package com.example.bakingapp.ui.recipes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.bakingapp.BR;
import com.example.bakingapp.R;
import com.example.bakingapp.data.models.RecipeDetails;
import com.example.bakingapp.databinding.RecipesListItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipesAdapterViewHolder> {

    private final RecipeAdapterOnItemClickHandler mClickHandler;

    private List<RecipeDetails> mRecipeDetails;

    public RecipesAdapter(RecipeAdapterOnItemClickHandler clickHandler) {
        mRecipeDetails = new ArrayList<>();
        mClickHandler = clickHandler;
    }

    @Override
    public RecipesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        return RecipesAdapterViewHolder.create(layoutInflater, viewGroup, mClickHandler);
    }

    @Override
    public void onBindViewHolder(RecipesAdapterViewHolder holder, int position) {
        holder.bind(mRecipeDetails.get(position), mClickHandler);
    }

    @Override
    public int getItemCount() {
        if (mRecipeDetails == null) return 0;
        return mRecipeDetails.size();
    }

    void swapRecipes(final List<RecipeDetails> recipes) {
        if (mRecipeDetails == null || mRecipeDetails.size() == 0) {
            mRecipeDetails = recipes;
            notifyDataSetChanged();
        }
    }

    public interface RecipeAdapterOnItemClickHandler {
        void onItemClick(int id);
    }

    static class RecipesAdapterViewHolder extends RecyclerView.ViewHolder {

        private final RecipesListItemBinding mViewDataBinding;

        public RecipesAdapterViewHolder(RecipesListItemBinding listItemBinding, final RecipeAdapterOnItemClickHandler callback) {
            super(listItemBinding.getRoot());
            this.mViewDataBinding = listItemBinding;
            listItemBinding.getRoot().setOnClickListener( view -> callback.onItemClick(listItemBinding.getRecipeDetails().getId()));

        }

        public static RecipesAdapterViewHolder create(LayoutInflater inflater, ViewGroup parent, RecipeAdapterOnItemClickHandler clickHandler) {
            RecipesListItemBinding itemListBinding = RecipesListItemBinding.inflate(inflater, parent, false);
            return new RecipesAdapterViewHolder(itemListBinding, clickHandler);
        }

        public void bind(RecipeDetails recipe, RecipeAdapterOnItemClickHandler clickHandler) {

            mViewDataBinding.setVariable(BR.recipeDetails, recipe);
            mViewDataBinding.setVariable(BR.clickHandler, clickHandler);
            mViewDataBinding.executePendingBindings();

            if(recipe.getImage() != null && !recipe.getImage().isEmpty()){
                Picasso.get().load(recipe.getImage()).placeholder(R.drawable.recipe_placeholder).error(R.drawable.recipe_placeholder).into(mViewDataBinding.thumbnail);
            }else{
                Picasso.get().load(R.drawable.recipe_placeholder).into(mViewDataBinding.thumbnail);
            }

        }
    }
}
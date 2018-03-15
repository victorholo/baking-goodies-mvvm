package com.example.bakingapp.ui.steps;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.bakingapp.BR;
import com.example.bakingapp.data.models.Step;
import com.example.bakingapp.databinding.StepListItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor Holotescu on 12-03-2018.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepAdapterViewHolder> {

    private final StepsAdapter.StepAdapterOnItemClickHandler mClickHandler;

    private List<Step> mSteps;

    public StepsAdapter(StepsAdapter.StepAdapterOnItemClickHandler clickHandler) {
        mSteps = new ArrayList<>();
        mClickHandler = clickHandler;
    }

    @Override
    public StepAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        return StepAdapterViewHolder.create(layoutInflater, viewGroup, mClickHandler);
    }

    @Override
    public void onBindViewHolder(StepAdapterViewHolder holder, int position) {
        holder.bind(mSteps.get(position), mClickHandler);
    }

    @Override
    public int getItemCount() {
        if (mSteps == null) return 0;
        return mSteps.size();
    }

    void swapSteps(final List<Step> steps) {
        if (mSteps == null || mSteps.size() == 0) {
            mSteps = steps;
            notifyDataSetChanged();
        }
    }

    public interface StepAdapterOnItemClickHandler {
        void onItemClick(Step step);
    }

    static class StepAdapterViewHolder extends RecyclerView.ViewHolder {

        private final StepListItemBinding mViewDataBinding;

        public StepAdapterViewHolder(StepListItemBinding listItemBinding, final StepsAdapter.StepAdapterOnItemClickHandler callback) {
            super(listItemBinding.getRoot());
            this.mViewDataBinding = listItemBinding;
            listItemBinding.getRoot().setOnClickListener(view -> callback.onItemClick(listItemBinding.getStepDetails()));

        }

        public static StepAdapterViewHolder create(LayoutInflater inflater, ViewGroup parent, StepsAdapter.StepAdapterOnItemClickHandler clickHandler) {
            StepListItemBinding itemListBinding = StepListItemBinding.inflate(inflater, parent, false);
            return new StepAdapterViewHolder(itemListBinding, clickHandler);
        }

        public void bind(Step step, StepsAdapter.StepAdapterOnItemClickHandler clickHandler) {
            mViewDataBinding.setVariable(BR.stepDetails, step);
            mViewDataBinding.setVariable(BR.stepClickHandler, clickHandler);
            mViewDataBinding.executePendingBindings();

        }
    }
}
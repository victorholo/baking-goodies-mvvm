package com.example.bakingapp.ui;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.bakingapp.R;
import com.example.bakingapp.data.models.Step;
import com.example.bakingapp.ui.detail.DetailFragment;
import com.example.bakingapp.ui.steps.StepsFragment;
import com.example.bakingapp.ui.steps.StepsViewModel;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static com.example.bakingapp.utils.RecipesUtils.RECIPE_ID_EXTRA;
import static com.example.bakingapp.utils.RecipesUtils.STEP_ID_EXTRA;

public class StepsActivity extends AppCompatActivity implements HasSupportFragmentInjector, StepsViewModel.OnStepItemClickedListener{

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        mFragmentManager = getSupportFragmentManager();

        if(savedInstanceState == null) {
            StepsFragment stepsFragment = new StepsFragment();
            stepsFragment.setStepItemClickListener(this);
            Bundle b = new Bundle();
            int recipeId = getIntent().getIntExtra(RECIPE_ID_EXTRA, -1);
            b.putInt(RECIPE_ID_EXTRA, recipeId);
            stepsFragment.setArguments(b);
            mFragmentManager.beginTransaction()
                    .add(R.id.container, stepsFragment)
                    .commit();
            if(getResources().getBoolean(R.bool.is_tablet)) {
                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setArguments(b);
                mFragmentManager.beginTransaction()
                        .add(R.id.container_detail, detailFragment)
                        .commit();
            }
        }

    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public void onStepItemClicked(Step step) {
        if(getResources().getBoolean(R.bool.is_tablet)){
            DetailFragment detailFragment = new DetailFragment();
            Bundle b = new Bundle();
            b.putInt(RECIPE_ID_EXTRA, step.getRecipeId());
            b.putInt(STEP_ID_EXTRA, step.getId());
            detailFragment.setArguments(b);
            mFragmentManager.beginTransaction()
                    .replace(R.id.container_detail, detailFragment)
                    .commit();
        }else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(RECIPE_ID_EXTRA, step.getRecipeId());
            intent.putExtra(STEP_ID_EXTRA, step.getId());
            startActivity(intent);
        }
    }
}

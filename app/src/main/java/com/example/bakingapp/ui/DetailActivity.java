package com.example.bakingapp.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.bakingapp.R;
import com.example.bakingapp.ui.detail.DetailFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static com.example.bakingapp.utils.RecipesUtils.RECIPE_ID_EXTRA;
import static com.example.bakingapp.utils.RecipesUtils.STEP_ID_EXTRA;

public class DetailActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getBoolean(R.bool.is_landscape) && !getResources().getBoolean(R.bool.is_tablet)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_detail);
        if (getResources().getBoolean(R.bool.is_landscape) && !getResources().getBoolean(R.bool.is_tablet)) {
            getSupportActionBar().hide();
        }

        if (savedInstanceState == null) {
            DetailFragment detailFragment = new DetailFragment();
            Bundle b = new Bundle();
            b.putInt(RECIPE_ID_EXTRA, getIntent().getIntExtra(RECIPE_ID_EXTRA, -1));
            b.putInt(STEP_ID_EXTRA, getIntent().getIntExtra(STEP_ID_EXTRA, -1));
            detailFragment.setArguments(b);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, detailFragment)
                    .commit();
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

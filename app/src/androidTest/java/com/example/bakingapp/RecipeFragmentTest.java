package com.example.bakingapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.bakingapp.testing.SingleFragmentActivity;
import com.example.bakingapp.data.models.RecipeDetails;
import com.example.bakingapp.ui.StepsActivity;
import com.example.bakingapp.ui.recipes.RecipesFragment;
import com.example.bakingapp.ui.recipes.RecipesViewModel;
import com.example.bakingapp.utils.RecyclerViewMatcher;
import com.example.bakingapp.utils.TaskExecutorWithIdlingResourceRule;
import com.example.bakingapp.utils.TestUtil;
import com.example.bakingapp.utils.ViewModelUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.example.bakingapp.utils.RecipesUtils.RECIPE_ID_EXTRA;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Victor Holotescu on 15-03-2018.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeFragmentTest {

    @Rule
    public final ActivityTestRule<SingleFragmentActivity> activityRule =
            new ActivityTestRule<>(SingleFragmentActivity.class, true, true);

    @Rule
    public TaskExecutorWithIdlingResourceRule executorRule =
            new TaskExecutorWithIdlingResourceRule();

    private final MutableLiveData<List<RecipeDetails>> recipes = new MutableLiveData<>();

    @Before
    public void init() {

        RecipesFragment recipesFragment = new RecipesFragment();
        RecipesViewModel viewModel = mock(RecipesViewModel.class);

        when(viewModel.getRecipes()).thenReturn(recipes);

        recipesFragment.mViewModelFactory = ViewModelUtil.createFor(viewModel);

        activityRule.getActivity().setFragment(recipesFragment);
    }

    @Test
    public void aloadError() {

        recipes.postValue(new ArrayList<>());

        onView(withId(R.id.recycler_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.error_textview)).check(matches(isDisplayed()));
        onView(withId(R.id.pb_loading_indicator)).check(matches(not(isDisplayed())));
    }

    @Test
    public void bloadRecipe() {

        RecipeDetails recipeDetails = TestUtil.createRecipeDetail(0, "foo", 3, "");
        recipes.postValue(Arrays.asList(recipeDetails));

        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("foo"))));
        onView(withId(R.id.error_textview)).check(matches(not(isDisplayed())));
        onView(withId(R.id.pb_loading_indicator)).check(matches(not(isDisplayed())));
    }

    @Test
    public void checkMovesToSteps() {

        RecipeDetails recipeDetails = TestUtil.createRecipeDetail(0, "foo", 3, "");
        recipes.postValue(Arrays.asList(recipeDetails));

        //using an activity monitor, we will check if the intent has all the necessary data
        //without actually launching it
        Intent intent = new Intent();
        intent.putExtra(RECIPE_ID_EXTRA, 0);
        Instrumentation.ActivityResult activityResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
        Instrumentation.ActivityMonitor am = new Instrumentation.ActivityMonitor(StepsActivity.class.getName(), activityResult, true);
        InstrumentationRegistry.getInstrumentation().addMonitor(am);

        onView(listMatcher().atPosition(0)).perform(click());

        assertTrue(InstrumentationRegistry.getInstrumentation().checkMonitorHit(am, 1));
    }


    @NonNull
    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view);
    }
}

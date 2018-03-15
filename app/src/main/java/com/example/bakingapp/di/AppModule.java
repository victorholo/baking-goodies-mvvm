package com.example.bakingapp.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.bakingapp.data.api.ApiService;
import com.example.bakingapp.data.db.RecipeDao;
import com.example.bakingapp.data.db.RecipeRoomDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Victor Holotescu on 09-03-2018.
 */

@Module(includes = ViewModelModule.class)
class AppModule {
    @Singleton
    @Provides
    ApiService provideRecipeService() {
        return new Retrofit.Builder()
                .baseUrl("http://go.udacity.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    @Singleton @Provides
    RecipeRoomDatabase provideDb(Application app) {
        return Room.databaseBuilder(app, RecipeRoomDatabase.class,"recipes.db").build();
    }

    @Singleton @Provides
    RecipeDao provideUserDao(RecipeRoomDatabase db) {
        return db.recipeDao();
    }
}

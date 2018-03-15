package com.example.bakingapp.utils;

import com.example.bakingapp.data.models.Ingredient;

import java.util.List;

/**
 * Created by Victor Holotescu on 13-03-2018.
 */

public class RecipesUtils {
    public static final String RECIPE_ID_EXTRA = "recipe_id_extra";
    public static final String STEP_ID_EXTRA = "step_id_extra";

    public static String getIngredientsTextFromList(List<Ingredient> list) {
        StringBuilder ingredients = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Ingredient ingredient = list.get(i);
            String quantity = quanityToFraction(ingredient.getQuantity(), 10);
            String measurement = ingredient.getMeasure();
            String ingredientName = ingredient.getIngredient();
            ingredients.append(quantity).append(" ").append(measurement).append(" - ").append(ingredientName);
            if (i == list.size() - 1) {
                ingredients.append(".");
            } else ingredients.append(",\n");
        }
        return ingredients.toString();
    }

    public static String getIngredientTextFromObject(Ingredient ingredient) {
        String formattedIngredient = "";

        String quantity = quanityToFraction(ingredient.getQuantity(), 10);
        String measurement = ingredient.getMeasure();
        String ingredientName = ingredient.getIngredient();
        formattedIngredient += quantity + " " + measurement + " - " + ingredientName;

        return formattedIngredient;
    }

    /**
     * https://stackoverflow.com/a/18455788/2768211
     */
    private static String quanityToFraction(double d, int factor) {
        StringBuilder sb = new StringBuilder();
        if (d < 0) {
            sb.append('-');
            d = -d;
        }
        long l = (long) d;
        if (l != 0) sb.append(l);
        d -= l;
        double error = Math.abs(d);
        int bestDenominator = 1;
        for (int i = 2; i <= factor; i++) {
            double error2 = Math.abs(d - (double) Math.round(d * i) / i);
            if (error2 < error) {
                error = error2;
                bestDenominator = i;
            }
        }
        if (bestDenominator > 1) {
            if (!sb.toString().isEmpty()) sb.append(' ');
            sb.append(Math.round(d * bestDenominator)).append('/').append(bestDenominator);
        }

        return sb.toString();
    }
}

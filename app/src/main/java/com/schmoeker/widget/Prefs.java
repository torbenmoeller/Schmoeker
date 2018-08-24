package com.schmoeker.widget;

import android.content.Context;
import android.content.SharedPreferences;

import com.schmoeker.KEYS;
import com.schmoeker.feed.FeedItem;

import java.util.List;

//Source https://github.com/dnKaratzas/udacity-baking-recipes
public class Prefs {

    public static final String PREFS_NAME = "prefs";

    public static void saveRecipe(Context context, List<FeedItem>  recipe) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        prefs.putString(KEYS.WIDGETKEY, FeedItem.toJson(recipe));
        prefs.apply();
    }

    public static List<FeedItem> loadRecipe(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String recipeString = prefs.getString(KEYS.WIDGETKEY, "");
        if("".equals(recipeString)){
            return null;
        }else{
            return FeedItem.fromJson(recipeString);
        }
    }
}

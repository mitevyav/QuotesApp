package com.example.yavor.naxexmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by mitevyav on 13.5.2017 г..
 */

public class Utils {

    /**
     * Retrieve the query with all the symbols selected from the user saved in the prefs.
     *
     * @param context
     * @return symbols query
     */
    public static String getSymbolsQuery(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.symbols_pref_key),
                                     context.getString(R.string.symbols_default_query));
    }
}

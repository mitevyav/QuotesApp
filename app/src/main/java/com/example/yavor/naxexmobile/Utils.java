package com.example.yavor.naxexmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

/**
 * Created by mitevyav on 13.5.2017 г..
 */

public class Utils {

    public static String[] getSymbols(Resources res) {
        String[] array = new String[res.getInteger(R.integer.symbols_count)];
        array[0] = res.getString(R.string.EURUSD);
        array[1] = res.getString(R.string.GBPUSD);
        array[2] = res.getString(R.string.USDCHF);
        array[3] = res.getString(R.string.USDJPY);
        array[4] = res.getString(R.string.AUDUSD);
        array[5] = res.getString(R.string.USDCAD);
        array[6] = res.getString(R.string.GBPJPY);
        array[7] = res.getString(R.string.EURGBP);
        array[8] = res.getString(R.string.EURJPY);
        array[9] = res.getString(R.string.AUDCAD);
        return array;
    }

    public static String getSymbolsQuery(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.symbols_pref_key),
                                     context.getString(R.string.symbols_default_query));
    }
}

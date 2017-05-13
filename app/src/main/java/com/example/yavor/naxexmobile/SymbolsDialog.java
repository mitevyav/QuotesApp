package com.example.yavor.naxexmobile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static android.R.layout.simple_list_item_multiple_choice;
import static com.example.yavor.naxexmobile.MainActivity.LOADER_ID;

/**
 * Created by mitevyav on 13.5.2017 г..
 */

public class SymbolsDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.symbols_dialog_title));

        // Create the ListView and set the adapter
        final ListView listView = new ListView(getActivity());
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        final String[] stringArray = Utils.getSymbols(getResources());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                                          simple_list_item_multiple_choice,
                                                          android.R.id.text1,
                                                          stringArray);
        listView.setAdapter(adapter);
        builder.setView(listView);

        loadCheckedPositions(listView, stringArray);

        builder.setPositiveButton(getString(android.R.string.ok),
                                  new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialogInterface, int i) {
                                          String query =
                                                  getSymbolsQueryValue(listView.getCheckedItemPositions(),
                                                                       stringArray);
                                          saveQuery(query);
                                          getActivity().getSupportLoaderManager().getLoader(
                                                  LOADER_ID).forceLoad();
                                          dialogInterface.dismiss();
                                      }
                                  });

        builder.setNegativeButton(getString(android.R.string.cancel),
                                  new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialogInterface, int i) {
                                          dialogInterface.dismiss();
                                      }
                                  });
        return builder.create();
    }

    /**
     * Create the symbols query depending on the checked positions
     *
     * @param checkedItemPositions
     *         the positions checked
     * @param stringArray
     */
    private String getSymbolsQueryValue(SparseBooleanArray checkedItemPositions,
                                        String[] stringArray) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < stringArray.length; i++) {
            if (checkedItemPositions.get(i)) {
                builder.append(stringArray[i] + ",");
            }
        }
        // Remove the last ","
        if (builder.length() > 0) {
            builder.replace(builder.length()-1, builder.length(), "");
        }
        return builder.toString();
    }

    /**
     * Save the quotes query in a pref field.
     *
     * @param query
     */
    private void saveQuery(String query) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.symbols_pref_key), query);
        editor.commit();
    }

    /**
     * Set the list items checked depending on the current pref query
     *
     * @param listView
     *         listView showing the items
     * @param stringArray
     *         array with all possible symbol query params.
     */
    private void loadCheckedPositions(ListView listView, String[] stringArray) {
        String query = Utils.getSymbolsQuery(getActivity());

        for (int i = 0; i < stringArray.length; i++) {
            boolean contains = query.contains(stringArray[i]);
            if (contains) {
                listView.setItemChecked(i, true);
            }
        }
    }
}

package com.example.yavor.naxexmobile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_list_item_multiple_choice;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<QuotesInfo>>, View.OnClickListener {

    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    private static final int LOADER_ID = 1;

    private QuotesAdapter quotesAdapter;

    @Override
    public Loader<List<QuotesInfo>> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader");
        return new QuotesLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<QuotesInfo>> loader, List<QuotesInfo> data) {
        Log.v(LOG_TAG, "onLoadFinished");
        quotesAdapter.clear();
        quotesAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<QuotesInfo>> loader) {
        Log.v(LOG_TAG, "onLoaderReset");
        quotesAdapter.clear();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                showAddRemoveDialog();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AbsListView listView = (AbsListView) findViewById(R.id.list);

        // Add header depending on device orientation
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == ORIENTATION_PORTRAIT) {
            View view = getLayoutInflater().inflate(R.layout.listview_header, null);
            ((ListView) listView).addHeaderView(view);
        }

        // Create the adapter and set it to the AbsListView
        quotesAdapter = new QuotesAdapter(this,
                                          R.layout.quotes_list_item,
                                          new ArrayList<QuotesInfo>());
        listView.setAdapter(quotesAdapter);

        // Start the loader
        getSupportLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();

        //
        findViewById(R.id.fab).setOnClickListener(this);
    }

    /**
     * Show a dialog with where the user can select the symbols to be shown.
     */
    private void showAddRemoveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.symbols_dialog_title));

        // Create the ListView and set the adapter
        final ListView listView = new ListView(this);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        final String[] stringArray = Utils.getSymbols(getResources());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
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
                                          getSupportLoaderManager().getLoader(LOADER_ID)
                                                                   .forceLoad();
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
        final Dialog dialog = builder.create();

        dialog.show();
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
            builder.replace(builder.length(), builder.length(), "");
        }
        return builder.toString();
    }

    /**
     * Save the quotes query in a pref field.
     *
     * @param query
     */
    private void saveQuery(String query) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.symbols_pref_key), query);
        editor.commit();
    }

    /**
     * Set the list items checked depending on the current pref query
     *
     * @param listView listView showing the items
     * @param stringArray array with all possible symbol query params.  
     */
    private void loadCheckedPositions(ListView listView, String[] stringArray) {
        String query = Utils.getSymbolsQuery(this);

        for (int i = 0; i < stringArray.length; i++) {
            int result = query.indexOf(stringArray[i]);
            if (result >= 0) {
                listView.setItemChecked(i, true);
            }
        }
    }
}

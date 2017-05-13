package com.example.yavor.naxexmobile;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<QuotesInfo>>, View.OnClickListener {

    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    public static final int LOADER_ID = 1;

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
        FragmentManager fm = getSupportFragmentManager();
        SymbolsDialog dialogFragment = new SymbolsDialog();
        dialogFragment.show(fm, "");
    }

}

package com.example.yavor.naxexmobile;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mitevyav on 12.5.2017 г..
 */

public class QuotesLoader extends AsyncTaskLoader<List<QuotesInfo>> {

    private static final String LOG_TAG = QuotesLoader.class.getCanonicalName();

    private static java.net.CookieManager msCookieManager = new java.net.CookieManager();

    private PollSymbolsThread pollSymbolsThread;

    public QuotesLoader(Context context) {
        super(context);

        // Start PollSymbolsThread thread
        pollSymbolsThread = new PollSymbolsThread(this);
        pollSymbolsThread.start();
    }

    @Override
    public List<QuotesInfo> loadInBackground() {

        Log.v(LOG_TAG, "loadInBackground()= " + Utils.getSymbolsQuery(getContext()));

        String jsonStr = getJSONString(Utils.getSymbolsQuery(getContext()));
        if (jsonStr == null) {
            showFailToast();
            return new ArrayList<>();
        }
        try {
            return getQuotesDataFromJson(jsonStr);
        } catch (JSONException e) {
            showFailToast();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method to fetch the info from the network and create a String with the result
     *
     * @param symbols
     *         the symbols that needs to be fetched.
     *
     * @return JSON string
     */
    public String getJSONString(String symbols) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String quotesJsonStr = null;

        try {
            // Construct the URL for the Quotes query
            final String FORECAST_BASE_URL =
                    "http://eu.tradenetworks.com/QuotesBox/quotes/GetQuotesBySymbols?";
            final String LANGUAGE_PARAM = "languageCode";
            final String SYMBOLS_PARAM = "symbols";

            final String LANGUAGE_VALUE = "en-US";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().appendQueryParameter(
                    LANGUAGE_PARAM,
                    LANGUAGE_VALUE).appendQueryParameter(SYMBOLS_PARAM, symbols).build();

            URL url = new URL(builtUri.toString());

            // Create the request to Tradenetworks, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            if (msCookieManager.getCookieStore().getCookies().size() > 0) {
                // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
                urlConnection.setRequestProperty("Cookie",
                                                 TextUtils.join(";",
                                                                msCookieManager.getCookieStore()
                                                                               .getCookies()));
            }
            urlConnection.connect();

            final String COOKIES_HEADER = "Set-Cookie";
            final String SESSION_ID = "ASP.NET_SessionId";

            msCookieManager = new java.net.CookieManager();
            Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
            if (cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    if (cookie.contains(SESSION_ID)) {
                        msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                }
            }

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return quotesJsonStr;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return quotesJsonStr;
            }
            quotesJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return quotesJsonStr;
    }

    @Override
    protected void onStopLoading() {
        Log.v(LOG_TAG, "onStopLoading()");
        super.onStopLoading();
        pollSymbolsThread.stop();
    }

    /**
     * Parse the json string and create a List<QuotesInfo> with the result
     *
     * @param quotesJsonStr
     *         the json string to be parsed
     *
     * @return List<QuotesInfo> with the result data
     *
     * @throws JSONException
     */
    private List<QuotesInfo> getQuotesDataFromJson(String quotesJsonStr) throws JSONException {

        final String BID = "Bid";
        final String ASK = "Ask";
        final String DISPLAY_NAME = "DisplayName";
        final String CHANGE_ORIENTATION = "ChangeOrientation";

        quotesJsonStr = formatJSONString(quotesJsonStr);

        JSONArray json = new JSONArray(quotesJsonStr);

        List<QuotesInfo> quotesList = new ArrayList<>();

        for (int i = 0; i < json.length(); i++) {
            JSONObject quotesObject = json.getJSONObject(i);
            String ask = quotesObject.getString(ASK);
            String bid = quotesObject.getString(BID);
            int changeOrientation = quotesObject.getInt(CHANGE_ORIENTATION);
            String displayName = quotesObject.getString(DISPLAY_NAME);
            quotesList.add(new QuotesInfo(ask, bid, changeOrientation, displayName));
        }
        return quotesList;
    }

    /**
     * As the JSON string is invalid we need to format it.
     *
     * @param jsonStr
     *         the json string that needs to be formatted.
     *
     * @return valid json string
     */
    private String formatJSONString(String jsonStr) {
        int startIndex = 1;
        int endIndex = jsonStr.length() - 1;
        return jsonStr.substring(startIndex, endIndex);
    }

    /**
     * Show toast to the user when the info fetch is no successful
     */
    private void showFailToast() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),
                               getContext().getString(R.string.fail_toast),
                               Toast.LENGTH_SHORT).show();
            }
        });
    }
}

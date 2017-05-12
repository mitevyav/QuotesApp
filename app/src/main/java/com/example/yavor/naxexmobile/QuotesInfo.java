package com.example.yavor.naxexmobile;

/**
 * Created by mitevyav on 11.5.2017 г..
 */

public class QuotesInfo {

    /**
     * Ask label
     */
    private float askCurrent = 0;

    private float askPrev = 0;

    /**
     * Sell label
     */
    private float bidCurrent = 0;

    private float bidPrev = 0;

    /**
     * The title of the view.
     */
    private String displayName;

    public float getAskCurrent() {
        return askCurrent;
    }

    public float getBidCurrent() {
        return bidCurrent;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBidColorResId() {
        int colorResId = R.color.neutralColor;
        float delta = bidCurrent - bidPrev;
        if (delta > 0) {
            colorResId = R.color.UPColor;
        } else if (delta < 0) {
            colorResId = R.color.DownColor;
        }
        return colorResId;
    }

    public int getAskColorResId() {
        int colorResId = R.color.neutralColor;
        float delta = askCurrent - askPrev;
        if (delta > 0) {
            colorResId = R.color.UPColor;
        } else if (delta < 0) {
            colorResId = R.color.DownColor;
        }
        return colorResId;
    }

    public boolean isAskChanged() {
        return (askCurrent - askPrev) != 0;
    }

    public boolean isBidChanged() {
        return (bidCurrent - bidPrev) != 0;
    }

}

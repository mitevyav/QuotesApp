package com.example.yavor.naxexmobile;

/**
 * Created by mitevyav on 11.5.2017 г..
 */

public class QuotesInfo {

    private static final int DOWN = 2;

    private static final int UNCHANGED = 3;

    private static final int UP = 1;

    /**
     * Ask label
     */
    private String ask;

    /**
     * Sell label
     */
    private String bid;

    /**
     * Change value which defines the color used
     */
    private int changeOrientation;

    /**
     * The title of the view.
     */
    private String displayName;

    public QuotesInfo(String ask, String bid, int changeOrientation, String displayName) {
        this.ask = ask;
        this.bid = bid;
        this.changeOrientation = changeOrientation;
        this.displayName = displayName;
    }

    public String getAsk() {
        return ask;
    }

    public String getBid() {
        return bid;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns a color res id depending on the current changeOrientation id.
     *
     * @return Color res id
     */
    public int getColorResId() {
        int colorResId;
        switch (changeOrientation) {
            case UP:
                colorResId = R.color.UPColor;
                break;
            case DOWN:
                colorResId = R.color.DownColor;
                break;
            default:
                colorResId = R.color.neutralColor;
                break;
        }
        return colorResId;
    }

    /**
     * Shows if the quotes value is changed
     *
     * @return true if it is changed
     */
    public boolean isChanged() {
        return changeOrientation != UNCHANGED;
    }

    /**
     * Returns the resId for the current change. For no change return also icon,
     * so there will be no check for invalid id. The icon will be not visible in that case.
     *
     * @return
     */
    public int getIconResId() {
        int resId = R.drawable.ic_arrow_drop_down_white_18dp;
        if (changeOrientation == UP) {
            resId = R.drawable.ic_arrow_drop_up_white_18dp;
        }
        return resId;
    }

}

package com.example.yavor.naxexmobile;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by mitevyav on 11.5.2017 г..
 */

public class QuotesAdapter extends ArrayAdapter<QuotesInfo> {

    private Context context;

    public QuotesAdapter(@NonNull Context context,
                         @LayoutRes int resource,
                         @NonNull List<QuotesInfo> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.quotes_list_item, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder(convertView);

            // store the holder with the view.
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource every time
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        bindView(viewHolder, position);

        return convertView;
    }

    /**
     * Method to bind the view to the data.
     *
     * @param viewHolder
     *         the object holding the reference to the views that need to be filled with data.
     * @param position
     *         the position of the current item
     */
    private void bindView(ViewHolder viewHolder, int position) {
        QuotesInfo qInfo = getItem(position);

        int colorResId = qInfo.getColorResId();
        int color = ContextCompat.getColor(context, colorResId);

        viewHolder.askArrow.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        viewHolder.bidArrow.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        int iconResId = qInfo.getIconResId();
        Drawable drawable = ContextCompat.getDrawable(context, iconResId);
        int arrowVisibility = qInfo.isChanged() ? View.VISIBLE : View.INVISIBLE;
        viewHolder.askArrow.setVisibility(arrowVisibility);
        viewHolder.bidArrow.setVisibility(arrowVisibility);
        viewHolder.askArrow.setImageDrawable(drawable);
        viewHolder.bidArrow.setImageDrawable(drawable);

        viewHolder.askValue.setText(setTextSize(qInfo.getAsk()));
        viewHolder.bidValue.setText(setTextSize(qInfo.getBid()));
        viewHolder.askValue.setTextColor(color);
        viewHolder.bidValue.setTextColor(color);

        viewHolder.displayName.setText(qInfo.getDisplayName());

        viewHolder.buyButton.setTag(position);
        viewHolder.sellButton.setTag(position);

        viewHolder.buyButton.setOnClickListener(new BuyClickListener());
        viewHolder.sellButton.setOnClickListener(new SellClickListener());
    }

    private SpannableString setTextSize(String string) {
        SpannableString spanString = new SpannableString(string);
        spanString.setSpan(new RelativeSizeSpan(1.5f), string.length() - 2, string.length(), 0);
        return spanString;
    }

    /**
     * Class to hold a reference to the views so we would not traverse
     * the view hierarchy every time.
     */
    private class ViewHolder {

        ImageView askArrow;

        TextView askValue;

        ImageView bidArrow;

        TextView bidValue;

        Button buyButton;

        TextView displayName;

        Button sellButton;

        ViewHolder(View view) {
            displayName = (TextView) view.findViewById(R.id.display_name);
            askValue = (TextView) view.findViewById(R.id.ask);
            bidValue = (TextView) view.findViewById(R.id.bid);
            askArrow = (ImageView) view.findViewById(R.id.ask_arrow);
            bidArrow = (ImageView) view.findViewById(R.id.bid_arrow);
            buyButton = (Button) view.findViewById(R.id.buy_button);
            sellButton = (Button) view.findViewById(R.id.sell_button);
        }
    }

    private class BuyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            QuotesInfo qInfo = getItem(position);
            Toast.makeText(context,
                           qInfo.getDisplayName() + " " + qInfo.getAsk(),
                           Toast.LENGTH_LONG).show();
        }
    }

    private class SellClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            QuotesInfo qInfo = getItem(position);
            Toast.makeText(context,
                           qInfo.getDisplayName() + " " + qInfo.getBid(),
                           Toast.LENGTH_LONG).show();
        }
    }

}

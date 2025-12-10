package com.example.finalproject.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.finalproject.R;
/**
 * SettingsAdapter
 * ----------------
 * This is a custom ArrayAdapter used to display the items inside the
 * ProfileFragment's ListView ("Personal", "Notifications", "Friends").
 *
 * A normal ArrayAdapter only displays simple text.
 * But we needed icons + labels, so we must override getView() and inflate
 * our own layout (settings_row.xml).
 */
public class SettingsAdapter extends ArrayAdapter<String> {

        Context context;
        String[] titles;
        int[] icons;
    /**
     * Constructor
     * -----------
     * @param c  Context from the fragment
     * @param t  Array of labels
     * @param i  Array of icon resource IDs (same positions as labels)
     *
     */
        public SettingsAdapter(Context c, String[] t, int[] i) {
            super(c, R.layout.settings_row, t);
            context = c;
            titles = t;
            icons = i;
        }
    /**
     * getView()
     * ---------
     * This method is called by ListView for EVERY row it needs to draw.
     * Because we want a custom row layout (icon + text), we:
     *
     * 1. Inflate settings_row.xml for this row
     * 2. Find the ImageView and TextView inside the row
     * 3. Fill them with the correct icon and label
     * 4. Return the completed row view to the ListView
     */
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View row = inflater.inflate(R.layout.settings_row, parent, false);
            // Get references to the widgets inside the row layout
            ImageView icon = row.findViewById(R.id.setting_icon);
            TextView title = row.findViewById(R.id.setting_title);

            title.setText(titles[position]);
            icon.setImageResource(icons[position]);

            return row; // give the finished row to the ListView to display
        }
    }


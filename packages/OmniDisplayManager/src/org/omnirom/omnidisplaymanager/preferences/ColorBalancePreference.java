/*
 * Copyright (C) 2018 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.omnirom.omnidisplaymanager.preferences;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import android.database.ContentObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Button;
import android.os.Bundle;
import android.util.Log;
import android.os.Vibrator;

import org.omnirom.omnidisplaymanager.DisplayManagement;
import org.omnirom.omnidisplaymanager.R;

public class ColorBalancePreference extends Preference implements
        SeekBar.OnSeekBarChangeListener {

    private SeekBar mSeekBar;
    private int mOldStrength;
    private int mMinValue;
    private int mMaxValue;
    private static final String TAG = "ColorBalancePreference";

    public ColorBalancePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMinValue = 0;
        mMaxValue = 100;

        setLayoutResource(R.layout.preference_seek_bar);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        mOldStrength = getValue();
        mSeekBar = (SeekBar) holder.findViewById(R.id.seekbar);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setProgress(mOldStrength - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    public int getValue() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        return Integer.valueOf(sharedPrefs.getString(DisplayManagement.KEY_COLOR_BALANCE,
                DisplayManagement.getDefaultValue(DisplayManagement.KEY_COLOR_BALANCE)));
    }

    private void setValue(String newValue, boolean withFeedback) {
        DisplayManagement.setColorBalance(Integer.parseInt(newValue));
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString(DisplayManagement.KEY_COLOR_BALANCE, newValue);
        editor.commit();
    }


    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromTouch) {
        setValue(String.valueOf(progress + mMinValue), true);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // NA
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        // NA
    }

    public void resetToDefaults() {
        mSeekBar.setProgress(Integer.valueOf(DisplayManagement.getDefaultValue(DisplayManagement.KEY_COLOR_BALANCE)), true);
    }
}


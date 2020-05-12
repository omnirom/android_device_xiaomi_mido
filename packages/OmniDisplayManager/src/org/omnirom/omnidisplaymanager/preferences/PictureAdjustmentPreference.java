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

import org.omnirom.omnidisplaymanager.DisplayManagement;
import org.omnirom.omnidisplaymanager.R;

import java.util.Arrays;

public class PictureAdjustmentPreference extends Preference implements
        SeekBar.OnSeekBarChangeListener {

    private SeekBar mSeekBar;
    private int mOldStrength;
    private int mMinValue;
    private int mMaxValue;
    private PictureAdjustment currentAdjustment;

    public PictureAdjustmentPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        int[] ranges = DisplayManagement.getRangePAParameter();
        Log.i("ranges", Arrays.toString(ranges));
        if (ranges != null && ranges.length > 0) {
            mMinValue = ranges[currentAdjustment.minRangeIndex];
            mMaxValue = ranges[currentAdjustment.maxRangeIndex];
        } else {
            mMinValue = 0;
            mMaxValue = 0;
        }

        setLayoutResource(R.layout.preference_seek_bar);
    }

    private void init(AttributeSet attrs) {
        String ajustmentType = attrs.getAttributeValue(null, "adjustmentType");
        switch (ajustmentType) {
            case "contrast":
                currentAdjustment = new PictureAdjustment(6, 7, 4, DisplayManagement.KEY_CONTRAST_VALUE);
                break;
            case "hue":
                currentAdjustment = new PictureAdjustment(0, 1, 1, DisplayManagement.KEY_HUE_VALUE);
                break;
            case "intensity":
                currentAdjustment = new PictureAdjustment(4, 5, 3, DisplayManagement.KEY_INTENSITY_VALUE);
                break;
            case "saturation":
                currentAdjustment = new PictureAdjustment(2, 3, 2, DisplayManagement.KEY_SATURATION_VALUE);
                break;
        }
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
        return Integer.valueOf(sharedPrefs.getString(currentAdjustment.preferenceKey,
                DisplayManagement.getDefaultValue(currentAdjustment.preferenceKey)));
    }

    private void setValue(String newValue) {
        int[] currentValue = DisplayManagement.getPAParameters();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        int contrast = Integer.valueOf(sharedPrefs.getString(DisplayManagement.KEY_CONTRAST_VALUE,
                DisplayManagement.getDefaultValue(DisplayManagement.KEY_CONTRAST_VALUE)));
        int hue = Integer.valueOf(sharedPrefs.getString(DisplayManagement.KEY_HUE_VALUE,
                DisplayManagement.getDefaultValue(DisplayManagement.KEY_HUE_VALUE)));
        int intensity = Integer.valueOf(sharedPrefs.getString(DisplayManagement.KEY_INTENSITY_VALUE,
                DisplayManagement.getDefaultValue(DisplayManagement.KEY_INTENSITY_VALUE)));
        int saturation = Integer.valueOf(sharedPrefs.getString(DisplayManagement.KEY_SATURATION_VALUE,
                DisplayManagement.getDefaultValue(DisplayManagement.KEY_SATURATION_VALUE)));

        switch (currentAdjustment.preferenceKey) {
            case DisplayManagement.KEY_CONTRAST_VALUE:
                DisplayManagement.setPAParameters(0, currentValue[0], hue, saturation, intensity, Integer.parseInt(newValue), currentValue[5]);
                break;
            case DisplayManagement.KEY_HUE_VALUE:
                DisplayManagement.setPAParameters(0, currentValue[0], Integer.parseInt(newValue), saturation, intensity, contrast, currentValue[5]);
                break;
            case DisplayManagement.KEY_INTENSITY_VALUE:
                DisplayManagement.setPAParameters(0, currentValue[0], hue, saturation, Integer.parseInt(newValue), contrast, currentValue[5]);
                break;
            case DisplayManagement.KEY_SATURATION_VALUE:
                DisplayManagement.setPAParameters(0, currentValue[0], hue, Integer.parseInt(newValue), intensity, contrast, currentValue[5]);
                break;
        }

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString(currentAdjustment.preferenceKey, newValue);
        editor.commit();
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromTouch) {
        setValue(String.valueOf(progress + mMinValue));
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // NA
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        // NA
    }

    public void resetToDefaults() {
        mSeekBar.setProgress(Integer.valueOf(DisplayManagement.getDefaultValue(currentAdjustment.preferenceKey)), true);
    }

    public class PictureAdjustment {
        public int minRangeIndex;
        public int maxRangeIndex;
        public int parameterIndex;
        public String preferenceKey;

        public PictureAdjustment(int min, int max, int param, String key) {
            minRangeIndex = min;
            maxRangeIndex = max;
            parameterIndex = param;
            preferenceKey = key;
        }
    }
}



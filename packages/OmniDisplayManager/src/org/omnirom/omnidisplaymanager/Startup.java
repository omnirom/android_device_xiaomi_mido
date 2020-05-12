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
package org.omnirom.omnidisplaymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;

public class Startup extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent bootintent) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int[] currentValue = DisplayManagement.getPAParameters();

        int contrast = Integer.valueOf(sharedPrefs.getString(DisplayManagement.KEY_CONTRAST_VALUE,
                DisplayManagement.getDefaultValue(DisplayManagement.KEY_CONTRAST_VALUE)));
        int hue = Integer.valueOf(sharedPrefs.getString(DisplayManagement.KEY_HUE_VALUE,
                DisplayManagement.getDefaultValue(DisplayManagement.KEY_HUE_VALUE)));
        int intensity = Integer.valueOf(sharedPrefs.getString(DisplayManagement.KEY_INTENSITY_VALUE,
                DisplayManagement.getDefaultValue(DisplayManagement.KEY_INTENSITY_VALUE)));
        int saturation = Integer.valueOf(sharedPrefs.getString(DisplayManagement.KEY_SATURATION_VALUE,
                DisplayManagement.getDefaultValue(DisplayManagement.KEY_SATURATION_VALUE)));
        DisplayManagement.setPAParameters(0, currentValue[0], hue, saturation, intensity, contrast, currentValue[5]);

        if (context.getResources().getBoolean(R.bool.color_balance_support)) {
            int colorBalance = Integer.valueOf(sharedPrefs.getString(DisplayManagement.KEY_COLOR_BALANCE,
                    DisplayManagement.getDefaultValue(DisplayManagement.KEY_COLOR_BALANCE)));
            DisplayManagement.setColorBalance(colorBalance);
        }

        // always reset on reboot
        sharedPrefs.edit().putBoolean(ColorSettings.PREF_READING_MODE, false).commit();
    }
}

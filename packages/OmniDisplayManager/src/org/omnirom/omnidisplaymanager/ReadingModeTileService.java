/*
 *  Copyright (C) 2018 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.omnirom.omnidisplaymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.service.quicksettings.TileService;
import androidx.preference.PreferenceManager;


public class ReadingModeTileService extends TileService {
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPrefs.getBoolean(ColorSettings.PREF_READING_MODE, false)) {
            ColorSettings.setReadingMode(false);
            sharedPrefs.edit().putBoolean(ColorSettings.PREF_READING_MODE, false).commit();
        } else {
            ColorSettings.setReadingMode(true);
            sharedPrefs.edit().putBoolean(ColorSettings.PREF_READING_MODE, true).commit();
        }
    }
}

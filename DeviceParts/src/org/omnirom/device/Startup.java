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
package org.omnirom.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;

public class Startup extends BroadcastReceiver {

     private static void restore(String file, boolean enabled) {
        if (file == null) {
            return;
        }
        Utils.writeValue(file, enabled ? "1" : "0");
    }

    private static void restore(String file, String value) {
        if (file == null) {
            return;
        }
        Utils.writeValue(file, value);
    }
       private void maybeImportOldSettings(Context context) {
        boolean imported = Settings.System.getInt(context.getContentResolver(), "omni_device_setting_imported", 0) != 0;
             if (!imported) {
                 SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                  boolean enabled = sharedPrefs.getBoolean(DeviceSettings.HW_KEY_SWITCH, false);
                 Settings.System.putInt(context.getContentResolver(), HWKeySwitch.SETTINGS_KEY, enabled ? 1 : 0);

                 enabled = sharedPrefs.getBoolean(DeviceSettings.DOUB_TAP, false);
                 Settings.System.putInt(context.getContentResolver(), DoubTap.SETTINGS_KEY, enabled ? 1 : 0);

                 Settings.System.putInt(context.getContentResolver(), "omni_device_setting_imported", 1);

      }
}
          public static void restoreAfterUserSwitch(Context context) {
              boolean enabled =Settings.System.getInt(context.getContentResolver(), HWKeySwitch.SETTINGS_KEY, 0) != 0;
              restore(HWKeySwitch.getFile(), enabled);

              enabled =Settings.System.getInt(context.getContentResolver(), DoubTap.SETTINGS_KEY, 0) != 0;
              restore(DoubTap.getFile(), enabled);
}

    @Override
    public void onReceive(final Context context, final Intent bootintent) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        maybeImportOldSettings(context);
        restoreAfterUserSwitch(context);
        context.startService(new Intent(context, DiracService.class));
	DisplayCalibration.restore(context);
    }
}

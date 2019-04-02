/*
* Copyright (C) 2016 The OmniROM Project
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
* along with this program. If not, see <http://www.gnu.com/licenses/>.
*
*/
package com.lineageos.settings.device;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.TwoStatePreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;

public class DeviceSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String KEY_BATTERY_CHARGING_LIMITER = "battery_charging_limiter";
    public static final String KEY_VIBSTRENGTH = "vib_strength";
    public static final String KEY_YELLOW_TORCH_BRIGHTNESS = "yellow_torch_brightness";
    public static final String KEY_WHITE_TORCH_BRIGHTNESS = "white_torch_brightness";
    public static final String KEY_GLOVE_MODE = "glove_mode";
    public static final String USB_FASTCHARGE_KEY = "fastcharge";
    public static final String USB_FASTCHARGE_PATH = "/sys/kernel/fast_charge/force_fast_charge";

    private static final String GLOVE_MODE_FILE = "/sys/devices/virtual/tp_glove/device/glove_enable";

    private static final String SPECTRUM_KEY = "spectrum";
    private static final String SPECTRUM_SYSTEM_PROPERTY = "persist.spectrum.profile";

    private static final String KEY_CATEGORY_USB_FASTCHARGE = "usb_fastcharge";

    public static final String KEY_SLOW_WAKEUP_FIX = "slow_wakeup_fix";
    public static final String FILE_LEVEL_WAKEUP = "/sys/devices/soc/qpnp-smbcharger-18/power_supply/battery/subsystem/bms/hi_power";

    private VibratorStrengthPreference mVibratorStrength;
    private YellowTorchBrightnessPreference mYellowTorchBrightness;
    private WhiteTorchBrightnessPreference mWhiteTorchBrightness;
    private TwoStatePreference mGloveMode;
    private ListPreference mSpectrum;
    private SwitchPreference mFastcharge;
    private PreferenceCategory mUsbFastcharge;
    private SwitchPreference slowWakeupFixPreference;
    private BatteryChargingLimiterPreference mBatteryChargingLimiter;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main, rootKey);

        PreferenceScreen prefSet = getPreferenceScreen();

        PreferenceScreen mKcalPref = (PreferenceScreen) findPreference("kcal");
        mKcalPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
             @Override
             public boolean onPreferenceClick(Preference preference) {
                 Intent intent = new Intent(getActivity().getApplicationContext(), DisplayCalibration.class);
                 startActivity(intent);
                 return true;
             }
        });

        mVibratorStrength = (VibratorStrengthPreference) findPreference(KEY_VIBSTRENGTH);
        if (mVibratorStrength != null) {
            mVibratorStrength.setEnabled(VibratorStrengthPreference.isSupported());
        }

        mYellowTorchBrightness = (YellowTorchBrightnessPreference) findPreference(KEY_YELLOW_TORCH_BRIGHTNESS);
        if (mYellowTorchBrightness != null) {
            mYellowTorchBrightness.setEnabled(YellowTorchBrightnessPreference.isSupported());
        }

        mWhiteTorchBrightness = (WhiteTorchBrightnessPreference) findPreference(KEY_WHITE_TORCH_BRIGHTNESS);
        if (mWhiteTorchBrightness != null) {
            mWhiteTorchBrightness.setEnabled(WhiteTorchBrightnessPreference.isSupported());
        }

        mGloveMode = (TwoStatePreference) findPreference(KEY_GLOVE_MODE);
        mGloveMode.setChecked(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(DeviceSettings.KEY_GLOVE_MODE, false));
        mGloveMode.setOnPreferenceChangeListener(this);

        mSpectrum = (ListPreference) findPreference(SPECTRUM_KEY);
        if( mSpectrum != null ) {
            mSpectrum.setValue(SystemProperties.get(SPECTRUM_SYSTEM_PROPERTY, "0"));
            mSpectrum.setOnPreferenceChangeListener(this);
            mSpectrum.setSummary(mSpectrum.getEntry());
        }

        if (Utils.fileWritable(USB_FASTCHARGE_PATH)) {
          mFastcharge = (SwitchPreference) findPreference(USB_FASTCHARGE_KEY);
          mFastcharge.setChecked(Utils.getFileValueAsBoolean(USB_FASTCHARGE_PATH, false));
          mFastcharge.setOnPreferenceChangeListener(this);
        } else {
          mUsbFastcharge = (PreferenceCategory) prefSet.findPreference("usb_fastcharge");
          prefSet.removePreference(mUsbFastcharge);
        }

        slowWakeupFixPreference = (SwitchPreference) findPreference(KEY_SLOW_WAKEUP_FIX);
        slowWakeupFixPreference.setChecked(Utils.getFileValueAsBoolean(FILE_LEVEL_WAKEUP, false));
        slowWakeupFixPreference.setOnPreferenceChangeListener(this);

        mBatteryChargingLimiter = (BatteryChargingLimiterPreference) findPreference(KEY_BATTERY_CHARGING_LIMITER);
        if (mBatteryChargingLimiter != null) {
            mBatteryChargingLimiter.setEnabled(BatteryChargingLimiterPreference.isSupported());
        }
    }

    private void setFastcharge(boolean value) {
            Utils.writeValue(USB_FASTCHARGE_PATH, value ? "1" : "0");
    }

    public static void restore(Context context) {
        boolean gloveModeData = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(DeviceSettings.KEY_GLOVE_MODE, false);
        Utils.writeValue(GLOVE_MODE_FILE, gloveModeData ? "1" : "0");
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mGloveMode) {
            Boolean enabled = (Boolean) newValue;
            SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            prefChange.putBoolean(KEY_GLOVE_MODE, enabled).commit();
            Utils.writeValue(GLOVE_MODE_FILE, enabled ? "1" : "0");
            return true;
        } else if (preference == mSpectrum) {
            String strvalue = (String) newValue;
            int index = mSpectrum.findIndexOfValue(strvalue);
            mSpectrum.setSummary(mSpectrum.getEntries()[index]);
            SystemProperties.set(SPECTRUM_SYSTEM_PROPERTY, strvalue);
            return true;
        } else if (preference == mFastcharge) {
            boolean value = (Boolean) newValue;
            mFastcharge.setChecked(value);
            setFastcharge(value);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.putBoolean(USB_FASTCHARGE_KEY, value);
            editor.apply();
            return true;
        } else if (preference == slowWakeupFixPreference) {
            boolean value = (Boolean) newValue;
            slowWakeupFixPreference.setChecked(value);
            setSlowWakeupFix(value);
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.putBoolean(KEY_SLOW_WAKEUP_FIX, value);
            editor.apply();
            return true;
        }
        return false;
    }

    public static void setSlowWakeupFix(boolean value) {
        if (value) 
            Utils.writeValue(FILE_LEVEL_WAKEUP, "1");
        else
            Utils.writeValue(FILE_LEVEL_WAKEUP, "0"); 
    }

    public static void restoreSpectrumProp(Context context) {
        String spectrumStoredValue = PreferenceManager.getDefaultSharedPreferences(context).getString(SPECTRUM_KEY, "0");
        SystemProperties.set(SPECTRUM_SYSTEM_PROPERTY, spectrumStoredValue);
    }
}

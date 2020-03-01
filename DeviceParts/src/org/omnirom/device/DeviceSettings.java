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
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.omnirom.device;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import androidx.preference.PreferenceFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import android.preference.PreferenceActivity;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import androidx.preference.SwitchPreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;

import com.android.internal.util.omni.DeviceUtils;

public class DeviceSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String KEY_SETTINGS_PREFIX = "device_setting_";
    public static final String HW_KEY_SWITCH = "hwd";
    private static final String KEYS_SHOW_NAVBAR_KEY = "navigation_bar_show";
    private static final String KEYS_NAVBAR_CATEGORY = "category_navigationbar";
    public static final String DOUB_TAP = "dt2w";
    private static TwoStatePreference mHWKeySwitch;
    private static TwoStatePreference mDoubleTapWake;
    private SwitchPreference mEnableNavBar;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main, rootKey);

        mHWKeySwitch = (TwoStatePreference) findPreference(HW_KEY_SWITCH);
        mHWKeySwitch.setEnabled(HWKeySwitch.isSupported());
        mHWKeySwitch.setChecked(HWKeySwitch.isCurrentlyEnabled(this.getContext()));
        mHWKeySwitch.setOnPreferenceChangeListener(new HWKeySwitch(getContext()));
       
        mDoubleTapWake = (TwoStatePreference) findPreference(DOUB_TAP);
        mDoubleTapWake.setEnabled(DoubTap.isSupported());
        mDoubleTapWake.setChecked(DoubTap.isCurrentlyEnabled(this.getContext()));
        mDoubleTapWake.setOnPreferenceChangeListener(new DoubTap(getContext()));

        PreferenceScreen prefScreen = getPreferenceScreen(); 

       final boolean navBarDevice = getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar);
        mEnableNavBar = (SwitchPreference) prefScreen.findPreference(KEYS_SHOW_NAVBAR_KEY);
        if (navBarDevice) {
            final PreferenceCategory navBarCategory =
                (PreferenceCategory) prefScreen.findPreference(KEYS_NAVBAR_CATEGORY);
            navBarCategory.removePreference(mEnableNavBar);
        } else {
            boolean showNavBarDefault = DeviceUtils.deviceSupportNavigationBar(getActivity());
            boolean showNavBar = Settings.System.getInt(getContext().getContentResolver(),
                    Settings.System.OMNI_NAVIGATION_BAR_SHOW, showNavBarDefault ? 1 : 0) == 1;
            mEnableNavBar.setChecked(showNavBar);
        }

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
	if (preference == mEnableNavBar) {
	boolean checked = ((SwitchPreference)preference).isChecked();
	Settings.System.putInt(getContext().getContentResolver(),
	Settings.System.OMNI_NAVIGATION_BAR_SHOW, checked ? 1:0);
	return true;
	}
	return super.onPreferenceTreeClick(preference);
	}

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }
}

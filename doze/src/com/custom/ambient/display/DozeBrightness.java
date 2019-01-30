/*
 * Copyright (C) 2017 The ABC rom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.custom.ambient.display;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v14.preference.PreferenceFragment;
import android.util.KeyValueListParser;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.custom.ambient.display.preference.CustomSeekBarPreference;

public class DozeBrightness extends PreferenceActivity implements PreferenceFragment.OnPreferenceStartFragmentCallback {

    private static final String BRIGHTNESS_FRAGMENT_TAG = "custom_doze_brigthness_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, getNewFragment(), BRIGHTNESS_FRAGMENT_TAG)
                    .commit();
        }
    }

    private PreferenceFragment getNewFragment() {
        return new BrightnessSettingsFragment();
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment preferenceFragment,
            Preference preference) {
        Fragment instantiate = Fragment.instantiate(this, preference.getFragment(),
            preference.getExtras());
        getFragmentManager().beginTransaction().replace(
                android.R.id.content, instantiate).addToBackStack(preference.getKey()).commit();

        return true;
    }


    public static class BrightnessSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private CustomSeekBarPreference mNight;
        private CustomSeekBarPreference mLow;
        private CustomSeekBarPreference mHigh;
        private CustomSeekBarPreference mSun;

        private KeyValueListParser mParser;
        private int[] mScreenBrightnessArray;
        private static final String KEY_SCREEN_BRIGHTNESS_ARRAY = "screen_brightness_array";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            setPreferencesFromResource(R.xml.doze_brightness, rootKey);

            getActivity().setTitle(R.string.doze_brightness_title);

            final ContentResolver resolver = getActivity().getContentResolver();

            mParser = new KeyValueListParser(',');
            final String value = Settings.System.getString(getActivity().getContentResolver(),
                    Settings.System.ALWAYS_ON_DISPLAY_CONSTANTS_CUST);
            try {
                mParser.setString(value);
            } catch (IllegalArgumentException e) {
            }
            final int[]  defaultBrightnessArray = getActivity().getResources().getIntArray(
                    R.array.config_doze_brightness_sensor_to_brightness);
            mScreenBrightnessArray = mParser.getIntArray(KEY_SCREEN_BRIGHTNESS_ARRAY, defaultBrightnessArray);

            // mScreenBrightnessArray[0] is OFF (-1)
            final int night = mScreenBrightnessArray[1];
            final int low = mScreenBrightnessArray[2];
            final int high = mScreenBrightnessArray[3];
            final int sun = mScreenBrightnessArray[4];

            mNight = (CustomSeekBarPreference) findPreference("night");
            mNight.setValue(night);
            mNight.setOnPreferenceChangeListener(this);

            mLow = (CustomSeekBarPreference) findPreference("low");
            mLow.setValue(low);
            mLow.setOnPreferenceChangeListener(this);

            mHigh = (CustomSeekBarPreference) findPreference("high");
            mHigh.setValue(high);
            mHigh.setOnPreferenceChangeListener(this);

            mSun = (CustomSeekBarPreference) findPreference("sun");
            mSun.setValue(sun);
            mSun.setOnPreferenceChangeListener(this);

        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference == mNight) {
                int val = (Integer) newValue;
                mScreenBrightnessArray[1] = val;
            } else if (preference == mLow) {
                int val = (Integer) newValue;
                mScreenBrightnessArray[2] = val;
            } else if (preference == mHigh) {
                int val = (Integer) newValue;
                mScreenBrightnessArray[3] = val;
            } else if (preference == mSun) {
                int val = (Integer) newValue;
                mScreenBrightnessArray[4] = val;
            }
            setBrightness(mScreenBrightnessArray, getActivity());

            return true;
        }

        public static void setBrightness(int[] values, Context ctx) {
            StringBuilder b = new StringBuilder();
            b.append(KEY_SCREEN_BRIGHTNESS_ARRAY + "=");
            for (int i = 0; i < values.length; i++) {
                b.append(String.valueOf(values[i]));
                if (i < values.length - 1) {
                    b.append(":");
                }
            }
            String brightnessValues = b.toString();

            Settings.System.putString(ctx.getContentResolver(),
                    Settings.System.ALWAYS_ON_DISPLAY_CONSTANTS_CUST, brightnessValues);
        }

        public void resetSeekBars(int[] values) {
            mNight.refresh(values[1]);
            mLow.refresh(values[2]);
            mHigh.refresh(values[3]);
            mSun.refresh(values[4]);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.reset) {
            resetBrightness();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hide_menu, menu);
        return true;
    }

    private void resetBrightness() {
        final int[]  defaultBrightnessArray = this.getResources().getIntArray(
                R.array.config_doze_brightness_sensor_to_brightness);
        BrightnessSettingsFragment.setBrightness(defaultBrightnessArray, this);

        Fragment currentFragment = this.getFragmentManager().findFragmentByTag(BRIGHTNESS_FRAGMENT_TAG);
        if (currentFragment != null && currentFragment instanceof BrightnessSettingsFragment) {
            ((BrightnessSettingsFragment)currentFragment).resetSeekBars(defaultBrightnessArray);
        }
    }
}

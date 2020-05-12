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

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import android.os.ServiceManager;
import android.provider.Settings;
import android.provider.SearchIndexableResource;
import android.util.SparseArray;
import android.util.Slog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.internal.util.omni.DeviceUtils;

import com.qti.snapdragon.sdk.display.ModeInfo;

import org.omnirom.omnidisplaymanager.preferences.ColorBalancePreference;
import org.omnirom.omnidisplaymanager.preferences.SunlightVisibilityPreference;
import org.omnirom.omnidisplaymanager.preferences.PictureAdjustmentPreference;

import java.util.List;
import java.util.Arrays;

public class ColorSettings extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "DisplaySettings";
    private ListPreference mDisplayMode;
    private ListPreference mAdaptiveBacklightMode;
    private SwitchPreference mReadingMode;

    private static final String PREF_DISPLAY_MODE = "display_management_mode";
    private static final String PREF_COLOR_BALANCE = "color_balance";
    private static final String PREF_SVI = "sunlight_enhancement";
    public static final String PREF_READING_MODE = "reading_mode";

    private static final int LEVEL_COLOR_MATRIX_READING = 201;

    private static final SparseArray<float[]> mColorMatrix = new SparseArray<>(3);
    private static final int SURFACE_FLINGER_TRANSACTION_COLOR_MATRIX = 1015;
    private static final float[][] mTempColorMatrix = new float[2][16];

    private static final int MENU_RESET = Menu.FIRST;

    /**
     * Matrix and offset used for converting color to grayscale.
     * Copied from com.android.server.accessibility.DisplayAdjustmentUtils.MATRIX_GRAYSCALE
     */
    private static final float[] MATRIX_GRAYSCALE = new float[] {
            .2126f, .2126f, .2126f, 0,
            .7152f, .7152f, .7152f, 0,
            .0722f, .0722f, .0722f, 0,
            0,      0,      0, 1
    };

    /** Full color matrix and offset */
    private static final float[] MATRIX_NORMAL = new float[] {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.display_management_settings, rootKey);
        if (!DisplayManagement.isFeatureSupported(DisplayManagement.FEATURE_COLOR_BALANCE) ||
                !getResources().getBoolean(R.bool.color_balance_support)) {
            Preference pref = getPreferenceScreen().findPreference(PREF_COLOR_BALANCE);
            if (pref != null) {
                getPreferenceScreen().removePreference(pref);
            }
        }

        if (!DisplayManagement.isFeatureSupported(DisplayManagement.FEATURE_SUNLIGHT_VISBILITY_IMPROVEMENT)) {
            Preference pref = getPreferenceScreen().findPreference(PREF_SVI);
            if (pref != null) {
                getPreferenceScreen().removePreference(pref);
            }
        }

        if (!DisplayManagement.isFeatureSupported(DisplayManagement.FEATURE_COLOR_MODE_SELECTION) ||
                !getResources().getBoolean(R.bool.display_mode_support)) {
            Preference pref = getPreferenceScreen().findPreference(PREF_DISPLAY_MODE);
            if (pref != null) {
                getPreferenceScreen().removePreference(pref);
            }
        } else {
            mDisplayMode = (ListPreference) findPreference(PREF_DISPLAY_MODE);
            mDisplayMode.setOnPreferenceChangeListener(this);

            ModeInfo[] modes = DisplayManagement.getModes();
            if (modes != null) {
                CharSequence[] entries = new CharSequence[modes.length];
                CharSequence[] entryValues = new CharSequence[modes.length];
                for (int i = 0; i < modes.length; i++) {
                    entries[i] = modes[i].getName();
                    entryValues[i] = String.valueOf(modes[i].getId());
                }

                mDisplayMode.setEntries(entries);
                mDisplayMode.setEntryValues(entryValues);
                mDisplayMode.setValueIndex(DisplayManagement.getActiveMode());
            }
        }

        mReadingMode = (SwitchPreference) findPreference(PREF_READING_MODE);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mReadingMode) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            setReadingMode(checked);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mDisplayMode) {
            int val = Integer.parseInt((String) newValue);
            int index = mDisplayMode.findIndexOfValue((String) newValue);
            DisplayManagement.setMode(index);
            mDisplayMode.setSummary(mDisplayMode.getEntries()[index]);
            return true;
        }
        return false;
    }

    public static void setReadingMode(boolean state) {
        setColorMatrix(LEVEL_COLOR_MATRIX_READING,
                state ? MATRIX_GRAYSCALE : MATRIX_NORMAL);
    }

    public static void setColorMatrix(int level, float[] value) {
        if (value != null && value.length != 16) {
            throw new IllegalArgumentException("Expected length: 16 (4x4 matrix)"
                    + ", actual length: " + value.length);
        }

        synchronized (mColorMatrix) {
            final float[] oldValue = mColorMatrix.get(level);
            if (!Arrays.equals(oldValue, value)) {
                if (value == null) {
                    mColorMatrix.remove(level);
                } else if (oldValue == null) {
                    mColorMatrix.put(level, Arrays.copyOf(value, value.length));
                } else {
                    System.arraycopy(value, 0, oldValue, 0, value.length);
                }

                // Update the current color transform.
                applyColorMatrix(computeColorMatrixLocked());
            }
        }
    }

    private static void applyColorMatrix(float[] m) {
        final IBinder flinger = ServiceManager.getService("SurfaceFlinger");
        if (flinger != null) {
            final Parcel data = Parcel.obtain();
            data.writeInterfaceToken("android.ui.ISurfaceComposer");
            if (m != null) {
                data.writeInt(1);
                for (int i = 0; i < 16; i++) {
                    data.writeFloat(m[i]);
                }
            } else {
                data.writeInt(0);
            }
            try {
                flinger.transact(SURFACE_FLINGER_TRANSACTION_COLOR_MATRIX, data, null, 0);
            } catch (RemoteException ex) {
                Slog.e(TAG, "Failed to set color transform", ex);
            } finally {
                data.recycle();
            }
        }
    }

    private static float[] computeColorMatrixLocked() {
        final int count = mColorMatrix.size();
        if (count == 0) {
            return null;
        }

        final float[][] result = mTempColorMatrix;
        Matrix.setIdentityM(result[0], 0);
        for (int i = 0; i < count; i++) {
            float[] rhs = mColorMatrix.valueAt(i);
            Matrix.multiplyMM(result[(i + 1) % 2], 0, result[i % 2], 0, rhs, 0);
        }
        return result[count % 2];
    }

    private void resetToDefaults() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(DisplayManagement.KEY_CONTRAST_VALUE);
        editor.remove(DisplayManagement.KEY_HUE_VALUE);
        editor.remove(DisplayManagement.KEY_INTENSITY_VALUE);
        editor.remove(DisplayManagement.KEY_SATURATION_VALUE);
        editor.remove(DisplayManagement.KEY_COLOR_BALANCE);
        editor.remove(DisplayManagement.KEY_SUNLIGHT_VISIBILITY);
        editor.commit();
            
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            Preference p = getPreferenceScreen().getPreference(i);
            if (p instanceof PictureAdjustmentPreference) {
                ((PictureAdjustmentPreference)p).resetToDefaults();
            }
            if (p instanceof ColorBalancePreference) {
                ((ColorBalancePreference)p).resetToDefaults();
            }
            if (p instanceof SunlightVisibilityPreference) {
                ((SunlightVisibilityPreference)p).resetToDefaults();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_backup_restore)
                .setAlphabeticShortcut('r')
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                resetToDefaults();
                return true;
        }
        return false;
    }
}


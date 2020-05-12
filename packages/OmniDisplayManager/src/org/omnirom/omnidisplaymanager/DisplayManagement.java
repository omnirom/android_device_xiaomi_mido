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

import android.os.SystemProperties;
import android.util.Log;

import com.qti.service.colorservice.IColorServiceImpl;
import com.qti.snapdragon.sdk.display.ModeInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DisplayManagement {

    private static IColorServiceImpl mColorService;
    public static final String TAG = "DisplayManagement";
    private static boolean isDisplayManagementSupported;

    public static final int FEATURE_COLOR_BALANCE = 0;
    public static final int FEATURE_COLOR_MODE_SELECTION = 1;
    public static final int FEATURE_COLOR_MODE_MANAGEMENT = 2;
    public static final int FEATURE_ADAPTIVE_BACKLIGHT = 3;
    public static final int FEATURE_GLOBAL_PICTURE_ADJUSTMENT = 4;
    public static final int FEATURE_MEMORY_COLOR_ADJUSTMENT = 5;
    public static final int FEATURE_SUNLIGHT_VISBILITY_IMPROVEMENT = 6;

    public static final String KEY_CONTRAST_VALUE = "contrast_value";
    public static final String KEY_HUE_VALUE = "hue_value";
    public static final String KEY_INTENSITY_VALUE = "intensity_value";
    public static final String KEY_SATURATION_VALUE = "saturation_value";
    public static final String KEY_COLOR_BALANCE = "color_balance";
    public static final String KEY_SUNLIGHT_VISIBILITY = "sunlight_enhancement";

    private static Map<String, String> mDefaultValues = new HashMap();

    private static void init(){
        if (mColorService == null) {
            try {
                mColorService = new IColorServiceImpl();
                mColorService.native_init();
                isDisplayManagementSupported = true;
                saveDefaults();
            } catch (Throwable t) {
                // Ignore, DisplayEngineService not available.
                Log.e(TAG, "init", t);
            }
        }
    }

    public static int getActiveMode() {
        try {
            int defaultMode = getColorService().native_getDefaultMode(0);
            return defaultMode;
        } catch (Exception e) {
            return 0;
        }
    }

    public static ModeInfo[] getModes() {
        try {
            ModeInfo[] modes = getColorService().native_getModes(0, 2);
            return modes;
        } catch (Exception e) {
            return null;
        }
    }

    public static void setMode(int modeId) {
        try {
            getColorService().native_setActiveMode(0, modeId);
        } catch (Exception e) {
        }
    }

    public static boolean isFeatureSupported(int featId) {
        try {
            int supported = getColorService().native_isFeatureSupported(0, featId);
            return supported > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getColorBalance() {
        try {
            int balanceValue = getColorService().native_getColorBalance(0);
            return balanceValue;
        } catch (Exception e) {
            return 0;
        }
    }

    public static void setColorBalance(int newValue) {
        try {
            getColorService().native_setColorBalance(0, newValue);
        } catch (Exception e) {
        }
    }

    public static int getSVI() {
        try {
            return getColorService().native_getSVI(0);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getRangeSVIStrength(int index) {
        try {
            return getColorService().native_getRangeSVI(0, index);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void setSVI(int newValue) {
        try {
            getColorService().native_setSVI(0, newValue);
        } catch (Exception e) {
        }
    }

    public static int getBacklightQualityLevel() {
        try {
            return getColorService().native_getBacklightQualityLevel(0);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void setBacklightQualityLevel(int index) {
        try {
            getColorService().native_setBacklightQualityLevel(0, index);
        } catch (Exception e) {
        }
    }

    public static int[] getRangePAParameter() {
        try {
            return getColorService().native_getRangePAParameter(0);
        } catch (Exception e) {
            return null;
        }
    }

    public static int[] getPAParameters() {
        try {
            int[] v = getColorService().native_getPAParameters(0);
            return v;
        } catch (Exception e) {
            return new int[] {0, 0, 0, 0, 0, 0};
        }
    }

    public static void setPAParameters(int displayId, int flag, int hue, int saturation, int intensity, int contrast, int satThreshold){
        try {
            getColorService().native_setPAParameters(displayId, flag, hue, saturation, intensity, contrast, satThreshold);
        } catch (Exception e) {
        }
    }

    public static String getDefaultValue(String key) {
        return mDefaultValues.get(key);
    }

    private static IColorServiceImpl getColorService() {
        if (mColorService == null) {
            init();
        }
        return mColorService;
    }

    private static void saveDefaults() {
        if (mDefaultValues.size() == 0) {
            mDefaultValues.put(KEY_CONTRAST_VALUE, "100");
            mDefaultValues.put(KEY_HUE_VALUE, "180");
            mDefaultValues.put(KEY_INTENSITY_VALUE, "100");
            mDefaultValues.put(KEY_SATURATION_VALUE, "50");
            mDefaultValues.put(KEY_COLOR_BALANCE, "0");
            mDefaultValues.put(KEY_SUNLIGHT_VISIBILITY, "-1");
            Log.i(TAG, "defaults = " + mDefaultValues);
        }
    }
}

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

package com.qti.service.colorservice;

import android.util.Log;
import java.util.Hashtable;

import com.qti.snapdragon.sdk.display.ModeInfo;

public class IColorServiceImpl {
    private static final String TAG = "DeviceParts";

    public native int native_createNewMode(int i, String str, int i2, long j, int i3);

    public native int native_createNewModeAllFeatures(int i, String str, int i2);

    public native void native_deinit();

    public native int native_deleteMode(int i, int i2);

    public native int native_disableMemoryColorConfiguration(int i, int i2);

    public native long[] native_getActiveMode(int i);

    public native int native_getAdaptiveBacklightScale(int i);

    public native int native_getBacklightQualityLevel(int i);

    public native int native_getColorBalance(int i);

    public native int native_getDefaultMode(int i);

    public native int[] native_getMemoryColorParameters(int i, int i2);

    public native ModeInfo[] native_getModes(int i, int i2);

    public native int native_getNumberOfModes(int i, int i2);

    public native int[] native_getPAParameters(int i);

    public native int[] native_getRangeMemoryColorParameter(int i, int i2);

    public native int[] native_getRangePAParameter(int i);

    public native int native_getRangeSVI(int i, int i2);

    public native int native_getSVI(int i);

    public native int native_init();

    public native int native_isActiveFeatureOn(int i, int i2);

    public native int native_isFeatureSupported(int i, int i2);

    public native int native_modifyMode(int i, int i2, String str, int i3, long j, int i4);

    public native int native_modifyModeAllFeatures(int i, int i2, String str, int i3);

    public native int native_setActiveFeatureControl(int i, int i2, int i3);

    public native int native_setActiveMode(int i, int i2);

    public native int native_setBacklightQualityLevel(int i, int i2);

    public native int native_setColorBalance(int i, int i2);

    public native int native_setDefaultMode(int i, int i2);

    public native int native_setMemoryColorParameters(int i, int i2, int i3, int i4, int i5);

    public native int native_setPAParameters(int i, int i2, int i3, int i4, int i5, int i6, int i7);

    public native int native_setSVI(int i, int i2);

    public IColorServiceImpl() {
        System.loadLibrary("sdm-disp-apis");
        System.loadLibrary("sd_sdk_display");
    }

}

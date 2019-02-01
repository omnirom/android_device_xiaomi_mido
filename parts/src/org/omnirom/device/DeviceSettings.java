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
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v14.preference.SwitchPreference;
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

    private static final String KEY_CATEGORY_DISPLAY = "display";
    private static final String KEY_CATEGORY_CAMERA = "camera_pref";
    private static final String QC_SYSTEM_PROPERTY = "persist.sys.le_fast_chrg_enable";
    private static final String SYSTEM_PROPERTY_CAMERA_FOCUS_FIX = "persist.camera.focus_fix";
    private static final String SYSTEM_PROPERTY_VOLTE_FIX = "persist.volte.fix";
    private static final String SYSTEM_PROPERTY_HALL_SRV = "persist.sys.hall_sensor";
    private static final String SYSTEM_PROPERTY_PS_FB_BOOST = "persist.ps.fb_boost";
    private static final String SYSTEM_PROPERTY_QFP_WUP = "persist.qfp.wup_display";
    private static final String SYSTEM_PROPERTY_QFP_ENABLE = "persist.qfp_enable";
    private static final String SYSTEM_PROPERTY_HW_0D_DISABLE = "persist.hw.0d_disable";

    private static final String KEY_DIRAC_SOUND_ENHANCER = "dirac_enhancer";
    private static final String KEY_DIRAC_HEADSET_TYPE = "dirac_headsets";
    private static final String KEY_DIRAC_MUSIC_MODE = "dirac_mode";
    private static final String KEY_DIRAC_PRESET = "dirac_preset";


    private static final String KEY_MISOUND_SOUND_ENHANCER = "mi_sound_enhancer";
    private static final String KEY_MISOUND_HEADSET_TYPE = "mi_sound_headsets";
    private static final String KEY_MISOUND_MUSIC_MODE = "mi_sound_mode";


    final String KEY_DEVICE_DOZE = "device_doze";
    final String KEY_DEVICE_DOZE_PACKAGE_NAME = "org.lineageos.settings.doze";


    private TwoStatePreference mDiracEnableDisable;
    private ListPreference mDiracHeadsetType, mDiracMusicMode, mDiracPreset;

    private TwoStatePreference mMiSoundEnableDisable;
    private ListPreference mMiSoundHeadsetType, mMiSoundMusicMode;

    private Preference mKcalPref;

    private Context mContext;



    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main, rootKey);

        mContext = getActivity();

        mKcalPref = findPreference("kcal");
        if(  mKcalPref != null ) {
            mKcalPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getContext(), DisplayCalibration.class);
                    startActivity(intent);
                    return true;
                }
            });
        }


        mDiracEnableDisable = (TwoStatePreference) findPreference(KEY_DIRAC_SOUND_ENHANCER);


        if (DiracAudioEnhancerService.du == null ) {
            mContext.startService(new Intent(mContext, DiracAudioEnhancerService.class));
        }

        if (DiracAudioEnhancerService.du != null && DiracAudioEnhancerService.du.hasInitialized()) {
          mDiracEnableDisable.setChecked(DiracAudioEnhancerService.du.isEnabled(mContext) ? true:false);
        }
        mDiracEnableDisable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            if(((TwoStatePreference) preference).isChecked() != (Boolean) newValue) {
              DiracAudioEnhancerService.du.setEnabled(mContext, (Boolean) newValue ? true:false);
            }
            return true;
          }
        });
        mDiracHeadsetType = (ListPreference) findPreference(KEY_DIRAC_HEADSET_TYPE);
        mDiracMusicMode = (ListPreference) findPreference(KEY_DIRAC_MUSIC_MODE);
        mDiracPreset = (ListPreference) findPreference(KEY_DIRAC_PRESET);



        if (DiracAudioEnhancerService.du != null && DiracAudioEnhancerService.du.hasInitialized()) {
            mDiracHeadsetType.setValue(Integer.toString(DiracAudioEnhancerService.du.getHeadsetType(mContext)));
        }
        mDiracHeadsetType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            int val = Integer.parseInt(newValue.toString());
            DiracAudioEnhancerService.du.setHeadsetType(mContext, val);
            return true;
          }
        });

        /*
        mDiracMusicMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            int val = Integer.parseInt(newValue.toString());
            DiracAudioEnhancerService.du.setMode(mContext, val);
            return true;
          }
        });*/

        if (DiracAudioEnhancerService.du != null && DiracAudioEnhancerService.du.hasInitialized()) {
            mDiracPreset.setValue(DiracAudioEnhancerService.du.getLevel());
        }

        mDiracPreset.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            DiracAudioEnhancerService.du.setLevel(String.valueOf(newValue));
            return true;
          }
        });

        /*
        if( MiSoundAudioEnhancerService.du == null ) {
            mContext.startService(new Intent(mContext, MiSoundAudioEnhancerService.class));
        }
        mMiSoundEnableDisable = (TwoStatePreference) findPreference(KEY_MISOUND_SOUND_ENHANCER);
        if (MiSoundAudioEnhancerService.du != null && MiSoundAudioEnhancerService.du.hasInitialized()) {
          mMiSoundEnableDisable.setChecked(MiSoundAudioEnhancerService.du.isEnabled(mContext) ? true:false);
        }
        mMiSoundEnableDisable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            if(((TwoStatePreference) preference).isChecked() != (Boolean) newValue) {
              MiSoundAudioEnhancerService.du.setEnabled(mContext, (Boolean) newValue ? true:false);
            }
            return true;
          }
        });
        mMiSoundHeadsetType = (ListPreference) findPreference(KEY_MISOUND_HEADSET_TYPE);
        mMiSoundMusicMode = (ListPreference) findPreference(KEY_MISOUND_MUSIC_MODE);

        if (MiSoundAudioEnhancerService.du != null && DiracAudioEnhancerService.du.hasInitialized()) {
          mMiSoundHeadsetType.setValue(Integer.toString(MiSoundAudioEnhancerService.du.getHeadsetType(mContext)));
        }
        mMiSoundHeadsetType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            int val = Integer.parseInt(newValue.toString());
            MiSoundAudioEnhancerService.du.setHeadsetType(mContext, val);
            return true;
          }
        });

        mMiSoundMusicMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
          public boolean onPreferenceChange(Preference preference, Object newValue) {
            int val = Integer.parseInt(newValue.toString());
            MiSoundAudioEnhancerService.du.setMode(mContext, val);
            return true;
          }
        });
        */
        if (!isAppInstalled(KEY_DEVICE_DOZE_PACKAGE_NAME)) {
            PreferenceCategory displayCategory = (PreferenceCategory) findPreference(KEY_CATEGORY_DISPLAY);
            displayCategory.removePreference(findPreference(KEY_DEVICE_DOZE));
        }

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        boolean value;
        value = (Boolean) newValue;
        ((SwitchPreference)preference).setChecked(value);
        setEnable(key,value);
        return true;
    }

    private boolean isAppInstalled(String uri) {
        PackageManager pm = getContext().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    private void setEnable(String key, boolean value) {
        if(value) {
            SystemProperties.set(key, "1");
        } else {
            SystemProperties.set(key, "0");
        }
    }
}

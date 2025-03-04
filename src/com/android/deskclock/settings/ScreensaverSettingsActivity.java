/*
 * Copyright (C) 2009 The Android Open Source Project
 * Copyright (C) 2023-2025 The LineageOS Project
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

package com.android.deskclock.settings;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.android.deskclock.R;
import com.android.deskclock.data.DataModel;
import com.android.deskclock.widget.CollapsingToolbarBaseActivity;

/**
 * Settings for Clock screen saver
 */
public final class ScreensaverSettingsActivity extends CollapsingToolbarBaseActivity {

    public static final String KEY_CLOCK_STYLE = "screensaver_clock_style";
    public static final String KEY_CLOCK_COLOR = "screensaver_clock_color";
    public static final String KEY_NIGHT_MODE = "screensaver_night_mode";
    public static final String KEY_NIGHT_MODE_COLOR = "screensaver_clock_night_mode_color";
    public static final String KEY_NIGHT_MODE_DND = "screensaver_clock_night_mode_dnd";
    public static final String KEY_NIGHT_MODE_BRIGHTNESS =
            "screensaver_clock_night_mode_brightness";
    public static final String KEY_SHOW_AMPM = "screensaver_show_ampm";
    public static final String KEY_BOLD_TEXT = "screensaver_bold_text";
    private static final String PREFS_FRAGMENT_TAG = "prefs_fragment";
    private static final String CLOCK_STYLE_DIGITAL = "digital";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new PrefsFragment(), PREFS_FRAGMENT_TAG)
                    .disallowAddToBackStack()
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class PrefsFragment extends PreferenceFragmentCompat
            implements Preference.OnPreferenceChangeListener {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            getPreferenceManager().setStorageDeviceProtected();
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.screensaver_settings);
        }

        @Override
        public void onResume() {
            super.onResume();
            refresh();
        }

        @Override
        public boolean onPreferenceChange(Preference pref, Object newValue) {
            switch (pref.getKey()) {
                case KEY_CLOCK_STYLE:
                    final SimpleMenuPreference clockStylePref = (SimpleMenuPreference) pref;
                    final int clockStyleindex = clockStylePref.findIndexOfValue((String) newValue);
                    clockStylePref.setSummary(clockStylePref.getEntries()[clockStyleindex]);
                    setVisibility(newValue.equals(CLOCK_STYLE_DIGITAL));
                    break;
                case KEY_NIGHT_MODE_COLOR:
                case KEY_CLOCK_COLOR:
                    final SimpleMenuPreference clockColorPref = (SimpleMenuPreference) pref;
                    final int clockColorindex = clockColorPref.findIndexOfValue((String) newValue);
                    clockColorPref.setSummary(clockColorPref.getEntries()[clockColorindex]);
                    break;
                case KEY_NIGHT_MODE_BRIGHTNESS:
                    final SeekBarPreference clockBrightness = (SeekBarPreference) pref;
                    final String progress = getResources().getString(
                            R.string.clock_brightness_percentage, String.valueOf(newValue));
                    clockBrightness.setSummary(progress);
                    break;
            }
            return true;
        }

        private void refresh() {
            final SimpleMenuPreference clockStylePref = findPreference(KEY_CLOCK_STYLE);
            final SimpleMenuPreference clockColorPref = findPreference(KEY_CLOCK_COLOR);
            final SimpleMenuPreference nightModeColorPref = findPreference(KEY_NIGHT_MODE_COLOR);
            final SwitchPreferenceCompat nightModePref = findPreference(KEY_NIGHT_MODE);
            final SwitchPreferenceCompat nightModeDndPref = findPreference(KEY_NIGHT_MODE_DND);
            final SwitchPreferenceCompat showAmPmPref = findPreference(KEY_SHOW_AMPM);
            final SwitchPreferenceCompat boldTextPref = findPreference(KEY_BOLD_TEXT);
            final SeekBarPreference nightModeBrightness = findPreference(KEY_NIGHT_MODE_BRIGHTNESS);
            if (clockStylePref != null) {
                final int index = clockStylePref.findIndexOfValue(DataModel.getDataModel().
                        getScreensaverClockStyle().toString().toLowerCase());
                clockStylePref.setValueIndex(index);
                clockStylePref.setSummary(clockStylePref.getEntries()[index]);
                clockStylePref.setOnPreferenceChangeListener(this);
            }
            if (clockColorPref != null) {
                final int indexColor = clockColorPref.findIndexOfValue(DataModel.getDataModel().
                        getScreensaverClockColor());
                clockColorPref.setValueIndex(indexColor);
                clockColorPref.setSummary(clockColorPref.getEntries()[indexColor]);
                clockColorPref.setOnPreferenceChangeListener(this);
            }
            if (nightModeColorPref != null) {
                final int indexColor = nightModeColorPref.findIndexOfValue(DataModel.getDataModel().
                        getScreensaverClockNightModeColor());
                nightModeColorPref.setValueIndex(indexColor);
                nightModeColorPref.setSummary(clockColorPref.getEntries()[indexColor]);
                nightModeColorPref.setOnPreferenceChangeListener(this);
            }
            if (nightModePref != null) {
                nightModePref.setChecked(DataModel.getDataModel().getScreensaverNightModeOn());
            }
            if (nightModeDndPref != null) {
                nightModeDndPref.setChecked(DataModel.getDataModel()
                        .getScreensaverNightModeDndOn());
            }
            if (showAmPmPref != null) {
                showAmPmPref.setChecked(DataModel.getDataModel().getScreensaverShowAmPmOn());
                showAmPmPref.setEnabled(!DateFormat.is24HourFormat(getContext()));
            }
            if (boldTextPref != null) {
                boldTextPref.setChecked(DataModel.getDataModel().getScreensaverBoldTextOn());
            }
            if (nightModeBrightness != null) {
                final int percentage = DataModel.getDataModel().getScreensaverNightModeBrightness();
                nightModeBrightness.setValue(percentage);
                final String progress = getResources().getString(
                        R.string.clock_brightness_percentage, String.valueOf(percentage));
                nightModeBrightness.setSummary(progress);
                nightModeBrightness.setOnPreferenceChangeListener(this);
                nightModeBrightness.setUpdatesContinuously(true);
            }
            setVisibility(DataModel.getDataModel().getScreensaverClockStyle() ==
                    DataModel.ClockStyle.DIGITAL);
        }

        private void setVisibility(boolean isDigitalClock) {
            final SwitchPreferenceCompat showAmPmPref = findPreference(KEY_SHOW_AMPM);
            final SwitchPreferenceCompat boldTextPref = findPreference(KEY_BOLD_TEXT);
            if (showAmPmPref != null) {
                showAmPmPref.setVisible(isDigitalClock);
            }
            if (boldTextPref != null) {
                boldTextPref.setVisible(isDigitalClock);
            }
        }
    }
}

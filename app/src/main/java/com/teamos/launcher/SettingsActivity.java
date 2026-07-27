package com.teamos.launcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences mPrefs;
    private Spinner spinnerTheme;
    private Spinner spinnerIconShape;
    private Spinner spinnerGridSize;
    private SwitchCompat switchGlow;

    private static final String PREFS_NAME = "aero_launcher_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load theme from prefs before onCreate
        mPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        applyThemeFromPrefs();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinnerTheme = findViewById(R.id.spinner_theme);
        spinnerIconShape = findViewById(R.id.spinner_icon_shape);
        spinnerGridSize = findViewById(R.id.spinner_grid_size);
        switchGlow = findViewById(R.id.switch_glow);

        setupSpinners();
        loadSettings();
    }

    private void applyThemeFromPrefs() {
        String theme = mPrefs.getString("theme", "dark");
        if ("light".equals(theme)) {
            setTheme(R.style.Theme_AeroLauncher_Settings);
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        } else if ("dark".equals(theme)) {
            setTheme(R.style.Theme_AeroLauncher_Settings);
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void setupSpinners() {
        // Theme Spinner
        String[] themeLabels = {getString(R.string.theme_dark), getString(R.string.theme_light), getString(R.string.theme_system)};
        final String[] themeValues = {"dark", "light", "system"};
        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, themeLabels);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTheme.setAdapter(themeAdapter);

        // Icon Shape Spinner
        String[] shapeLabels = {getString(R.string.shape_hexagon), getString(R.string.shape_squircle), getString(R.string.shape_circle), getString(R.string.shape_none)};
        final String[] shapeValues = {"hexagon", "squircle", "circle", "none"};
        ArrayAdapter<String> shapeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, shapeLabels);
        shapeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIconShape.setAdapter(shapeAdapter);

        // Grid Size Spinner
        String[] gridLabels = {"4 x 5", "5 x 5", "5 x 6"};
        final String[] gridValues = {"4x5", "5x5", "5x6"};
        ArrayAdapter<String> gridAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gridLabels);
        gridAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGridSize.setAdapter(gridAdapter);

        // Listeners
        spinnerTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newVal = themeValues[position];
                String currentVal = mPrefs.getString("theme", "dark");
                if (!newVal.equals(currentVal)) {
                    mPrefs.edit().putString("theme", newVal).apply();
                    applyThemeFromPrefs();
                    recreate();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerIconShape.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPrefs.edit().putString("icon_shape", shapeValues[position]).apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerGridSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPrefs.edit().putString("grid_size", gridValues[position]).apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        switchGlow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mPrefs.edit().putBoolean("glow_effect", isChecked).apply();
        });
    }

    private void loadSettings() {
        // Theme
        String theme = mPrefs.getString("theme", "dark");
        if ("dark".equals(theme)) spinnerTheme.setSelection(0);
        else if ("light".equals(theme)) spinnerTheme.setSelection(1);
        else spinnerTheme.setSelection(2);

        // Shape
        String shape = mPrefs.getString("icon_shape", "hexagon");
        if ("hexagon".equals(shape)) spinnerIconShape.setSelection(0);
        else if ("squircle".equals(shape)) spinnerIconShape.setSelection(1);
        else if ("circle".equals(shape)) spinnerIconShape.setSelection(2);
        else spinnerIconShape.setSelection(3);

        // Grid
        String grid = mPrefs.getString("grid_size", "4x5");
        if ("4x5".equals(grid)) spinnerGridSize.setSelection(0);
        else if ("5x5".equals(grid)) spinnerGridSize.setSelection(1);
        else spinnerGridSize.setSelection(2);

        // Glow
        boolean glow = mPrefs.getBoolean("glow_effect", true);
        switchGlow.setChecked(glow);
    }
}

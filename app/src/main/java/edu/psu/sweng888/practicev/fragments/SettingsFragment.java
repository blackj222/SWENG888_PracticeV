package edu.psu.sweng888.practicev.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import edu.psu.sweng888.practicev.R;

public class SettingsFragment extends Fragment {

    private SwitchCompat switchLanguage; // toggle to switch between languages

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the settings layout
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchLanguage = view.findViewById(R.id.switchLanguage);

        // Get saved language code (default to device language)
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        String savedLang = prefs.getString("app_lang", Locale.getDefault().getLanguage());

        // Set switch state based on saved language
        switchLanguage.setChecked(savedLang.equals("fr"));

        // Save selected language and refresh activity
        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveLanguage(isChecked ? "fr" : "en");
            requireActivity().recreate();
        });
    }

    // Save chosen language to preferences
    private void saveLanguage(String langCode) {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        prefs.edit().putString("app_lang", langCode).apply();
    }
}

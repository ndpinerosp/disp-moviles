package co.edu.unal.tictactoe;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class settings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        final EditTextPreference victoryMessagePref = (EditTextPreference)
                findPreference("victory_message");
        String victoryMessage = prefs.getString("victory_message",
                getResources().getString(R.string.result_human_wins));
        victoryMessagePref.setSummary(victoryMessage);
        victoryMessagePref
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference,
                                                      Object newValue) {
                        victoryMessagePref.setSummary((CharSequence) newValue);
                        // Since we are handling the pref, we must save it
                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putString("victory_message", newValue.toString());
                        ed.commit();
                        return true;
                    }
                });

        final ColorPickerPreference colorPref = (ColorPickerPreference) findPreference("board_color");
        colorPref
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference,
                                                      Object newValue) {
                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putInt("board_color",
                                Integer.valueOf(String.valueOf(newValue)));
                        ed.commit();
                        return true;
                    }
                });


    }

}

package aie.amg.theshow.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import aie.amg.theshow.R;
import aie.amg.theshow.util.ConfigurationHelper;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ConfigurationHelper helper = new ConfigurationHelper(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment(helper))
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViewById(R.id.share).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://theshow.xyz/download-app.php"));
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private ConfigurationHelper helper;

        SettingsFragment(ConfigurationHelper helper) {
            this.helper = helper;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            SwitchPreferenceCompat adm = getPreferenceScreen().findPreference("adm");

            SwitchPreferenceCompat gridMode = getPreferenceScreen().findPreference("gridMode");
            SwitchPreferenceCompat download = getPreferenceScreen().findPreference("download");
            adm.setChecked(helper.isAdm());
            gridMode.setChecked(helper.isListGridMode());

            adm.setOnPreferenceClickListener((preference) -> {
                helper.setAdm(adm.isChecked());
                Log.d("adm", String.valueOf(adm.isChecked()));
                return true;
            });

            gridMode.setOnPreferenceClickListener(preference -> {
                helper.setGridMode(gridMode.isChecked());
                return true;
            });
          }

    }
}
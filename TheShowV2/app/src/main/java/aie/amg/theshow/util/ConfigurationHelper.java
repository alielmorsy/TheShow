package aie.amg.theshow.util;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigurationHelper {
    private SharedPreferences sharedPreferences;

    public ConfigurationHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
    }

    public boolean isAdm() {
        return sharedPreferences.getBoolean("adm", false);
    }

    public void setAdm(boolean status) {
        sharedPreferences.edit().putBoolean("adm", status).apply();
    }

    public boolean isMxPlayer() {
        return sharedPreferences.getBoolean("mxPlayer", false);
    }

    public void setMxPlayer(boolean status) {
        sharedPreferences.edit().putBoolean("mxPlayer", status).apply();
    }

    public boolean isListGridMode() {
        return sharedPreferences.getBoolean("gridMode", true);
    }

    public void setGridMode(boolean gridMode) {
        sharedPreferences.edit().putBoolean("gridMode", gridMode).apply();
    }

    public boolean isAnimationOpened() {
        return sharedPreferences.getBoolean("animation", false);
    }

    public void setAnimationOpened(boolean animationOpened) {
        sharedPreferences.edit().putBoolean("animation", animationOpened).apply();
    }

    public boolean isLocalDownload() {
        return sharedPreferences.getBoolean("download", true);
    }

    public void setLocalDownload(boolean download) {
        sharedPreferences.edit().putBoolean("download", download).apply();

    }
}
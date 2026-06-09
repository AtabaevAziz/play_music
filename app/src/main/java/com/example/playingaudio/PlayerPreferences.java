package com.example.playingaudio;

import android.content.Context;
import android.content.SharedPreferences;

public class PlayerPreferences {

    private static final String PREFS_NAME = "player_prefs";
    private static final String KEY_LAST_TRACK_ID = "last_track_id";
    private static final String KEY_LAST_VOLUME = "last_volume";

    private final SharedPreferences sharedPreferences;

    public PlayerPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveLastPosition(String trackId, int positionMs) {
        sharedPreferences.edit()
                .putInt(buildTrackPositionKey(trackId), Math.max(0, positionMs))
                .putString(KEY_LAST_TRACK_ID, trackId)
                .apply();
    }

    public int getLastPosition(String trackId) {
        return sharedPreferences.getInt(buildTrackPositionKey(trackId), 0);
    }

    public void saveLastTrackId(String trackId) {
        sharedPreferences.edit().putString(KEY_LAST_TRACK_ID, trackId).apply();
    }

    public String getLastTrackId() {
        return sharedPreferences.getString(KEY_LAST_TRACK_ID, null);
    }

    public void saveLastVolume(int volume) {
        sharedPreferences.edit().putInt(KEY_LAST_VOLUME, Math.max(0, volume)).apply();
    }

    public int getLastVolume(int fallbackValue) {
        return sharedPreferences.getInt(KEY_LAST_VOLUME, fallbackValue);
    }

    private String buildTrackPositionKey(String trackId) {
        return "last_position_" + trackId;
    }
}

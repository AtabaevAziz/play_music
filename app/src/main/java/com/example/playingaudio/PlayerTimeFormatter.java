package com.example.playingaudio;

import java.util.Locale;

public final class PlayerTimeFormatter {

    private PlayerTimeFormatter() {
    }

    public static String formatTime(int positionMs) {
        int safePositionMs = Math.max(0, positionMs);
        int totalSeconds = safePositionMs / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.US, "%d:%02d", minutes, seconds);
    }

    public static int clampPosition(int positionMs, int durationMs) {
        if (durationMs <= 0) {
            return 0;
        }
        return Math.max(0, Math.min(positionMs, durationMs));
    }

    public static int clampVolume(int volume, int maxVolume) {
        if (maxVolume <= 0) {
            return 0;
        }
        return Math.max(0, Math.min(volume, maxVolume));
    }
}

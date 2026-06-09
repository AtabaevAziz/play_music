package com.example.playingaudio;

public final class PlayerUiStateFactory {

    private PlayerUiStateFactory() {
    }

    public static PlayerUiState create(
            Track track,
            int trackIndex,
            int trackCount,
            PlaybackState playbackState,
            int currentPositionMs,
            int durationMs,
            int volumeProgress,
            int volumeMax,
            String errorMessage
    ) {
        int safeDurationMs = Math.max(0, durationMs);
        int safePositionMs = PlayerTimeFormatter.clampPosition(currentPositionMs, safeDurationMs);
        int safeVolumeProgress = PlayerTimeFormatter.clampVolume(volumeProgress, volumeMax);
        boolean hasError = playbackState == PlaybackState.ERROR;
        boolean controlsEnabled = playbackState != PlaybackState.IDLE && !hasError;
        boolean canSeek = safeDurationMs > 0 && controlsEnabled;
        boolean isPlaying = playbackState == PlaybackState.PLAYING;

        return new PlayerUiState(
                track.getTitle(),
                track.getArtist(),
                track.getAlbum(),
                buildTrackIndexLabel(trackIndex, trackCount),
                PlayerTimeFormatter.formatTime(safePositionMs),
                PlayerTimeFormatter.formatTime(safeDurationMs),
                errorMessage,
                safePositionMs,
                safeDurationMs,
                safeVolumeProgress,
                Math.max(volumeMax, 0),
                track.getArtworkResId(),
                isPlaying,
                controlsEnabled,
                canSeek,
                hasError
        );
    }

    private static String buildTrackIndexLabel(int trackIndex, int trackCount) {
        if (trackCount <= 0) {
            return "";
        }
        return "Track " + (trackIndex + 1) + " of " + trackCount;
    }
}

package com.example.playingaudio;

public class PlayerUiState {

    private final String title;
    private final String artist;
    private final String album;
    private final String trackIndexLabel;
    private final String currentTimeLabel;
    private final String durationLabel;
    private final String errorMessage;
    private final int currentPositionMs;
    private final int durationMs;
    private final int volumeProgress;
    private final int volumeMax;
    private final int artworkResId;
    private final boolean isPlaying;
    private final boolean controlsEnabled;
    private final boolean canSeek;
    private final boolean hasError;

    public PlayerUiState(
            String title,
            String artist,
            String album,
            String trackIndexLabel,
            String currentTimeLabel,
            String durationLabel,
            String errorMessage,
            int currentPositionMs,
            int durationMs,
            int volumeProgress,
            int volumeMax,
            int artworkResId,
            boolean isPlaying,
            boolean controlsEnabled,
            boolean canSeek,
            boolean hasError
    ) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.trackIndexLabel = trackIndexLabel;
        this.currentTimeLabel = currentTimeLabel;
        this.durationLabel = durationLabel;
        this.errorMessage = errorMessage;
        this.currentPositionMs = currentPositionMs;
        this.durationMs = durationMs;
        this.volumeProgress = volumeProgress;
        this.volumeMax = volumeMax;
        this.artworkResId = artworkResId;
        this.isPlaying = isPlaying;
        this.controlsEnabled = controlsEnabled;
        this.canSeek = canSeek;
        this.hasError = hasError;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getTrackIndexLabel() {
        return trackIndexLabel;
    }

    public String getCurrentTimeLabel() {
        return currentTimeLabel;
    }

    public String getDurationLabel() {
        return durationLabel;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getCurrentPositionMs() {
        return currentPositionMs;
    }

    public int getDurationMs() {
        return durationMs;
    }

    public int getVolumeProgress() {
        return volumeProgress;
    }

    public int getVolumeMax() {
        return volumeMax;
    }

    public int getArtworkResId() {
        return artworkResId;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isControlsEnabled() {
        return controlsEnabled;
    }

    public boolean canSeek() {
        return canSeek;
    }

    public boolean hasError() {
        return hasError;
    }
}

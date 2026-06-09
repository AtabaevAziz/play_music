package com.example.playingaudio;

public interface PlayerController {

    interface Listener {
        void onUiStateChanged(PlayerUiState uiState);
    }

    void initialize();

    void startObservingProgress();

    void stopObservingProgress();

    void togglePlayback();

    void playNext();

    void playPrevious();

    void seekTo(int positionMs);

    void setVolume(int volumeProgress);

    void persistState();

    void release();
}

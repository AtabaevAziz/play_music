package com.example.playingaudio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

public class MediaPlayerController implements PlayerController {

    private static final long PROGRESS_UPDATE_DELAY_MS = 500L;

    private final Context context;
    private final List<Track> tracks;
    private final PlayerPreferences preferences;
    private final Listener listener;
    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private final Runnable progressUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            emitUiState();
            if (observingProgress && playbackState == PlaybackState.PLAYING && mediaPlayer != null) {
                progressHandler.postDelayed(this, PROGRESS_UPDATE_DELAY_MS);
            }
        }
    };

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private PlaybackState playbackState = PlaybackState.IDLE;
    private boolean observingProgress;
    private String errorMessage;
    private int currentVolume;
    private int maxVolume;
    private int currentTrackIndex;

    public MediaPlayerController(Context context, List<Track> tracks, Listener listener) {
        this.context = context.getApplicationContext();
        this.tracks = tracks;
        this.listener = listener;
        this.preferences = new PlayerPreferences(this.context);
    }

    @Override
    public void initialize() {
        if (tracks == null || tracks.isEmpty()) {
            playbackState = PlaybackState.ERROR;
            errorMessage = context.getString(R.string.error_audio_init);
            emitUiState();
            return;
        }

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        setupVolume();
        currentTrackIndex = resolveInitialTrackIndex();
        prepareTrack(false);
    }

    @Override
    public void startObservingProgress() {
        observingProgress = true;
        scheduleProgressUpdates();
        emitUiState();
    }

    @Override
    public void stopObservingProgress() {
        observingProgress = false;
        progressHandler.removeCallbacks(progressUpdateRunnable);
    }

    @Override
    public void togglePlayback() {
        if (mediaPlayer == null) {
            return;
        }

        if (playbackState == PlaybackState.PLAYING) {
            mediaPlayer.pause();
            playbackState = PlaybackState.PAUSED;
            emitUiState();
            stopObservingProgress();
            return;
        }

        if (playbackState == PlaybackState.COMPLETED) {
            mediaPlayer.seekTo(0);
        }

        mediaPlayer.start();
        playbackState = PlaybackState.PLAYING;
        emitUiState();
        scheduleProgressUpdates();
    }

    private void restartCurrentTrack() {
        if (mediaPlayer == null) {
            return;
        }

        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
        playbackState = PlaybackState.PAUSED;
        emitUiState();
        stopObservingProgress();
    }

    @Override
    public void playNext() {
        if (tracks == null || tracks.isEmpty()) {
            return;
        }

        persistCurrentTrackPosition();
        currentTrackIndex = (currentTrackIndex + 1) % tracks.size();
        prepareTrack(true);
    }

    @Override
    public void playPrevious() {
        if (tracks == null || tracks.isEmpty()) {
            return;
        }

        if (mediaPlayer != null && mediaPlayer.getCurrentPosition() > 3000) {
            restartCurrentTrack();
            return;
        }

        persistCurrentTrackPosition();
        currentTrackIndex = currentTrackIndex == 0 ? tracks.size() - 1 : currentTrackIndex - 1;
        prepareTrack(true);
    }

    @Override
    public void seekTo(int positionMs) {
        if (mediaPlayer == null) {
            return;
        }

        int clampedPosition = PlayerTimeFormatter.clampPosition(positionMs, mediaPlayer.getDuration());
        mediaPlayer.seekTo(clampedPosition);
        emitUiState();
    }

    @Override
    public void setVolume(int volumeProgress) {
        if (audioManager == null) {
            return;
        }

        currentVolume = PlayerTimeFormatter.clampVolume(volumeProgress, maxVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        preferences.saveLastVolume(currentVolume);
        emitUiState();
    }

    @Override
    public void persistState() {
        persistCurrentTrackPosition();
        preferences.saveLastTrackId(getCurrentTrack().getId());
        preferences.saveLastVolume(currentVolume);
    }

    @Override
    public void release() {
        stopObservingProgress();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        playbackState = PlaybackState.IDLE;
    }

    private void setupVolume() {
        if (audioManager == null) {
            maxVolume = 0;
            currentVolume = 0;
            if (errorMessage == null) {
                errorMessage = context.getString(R.string.error_audio_manager);
            }
            return;
        }

        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        currentVolume = PlayerTimeFormatter.clampVolume(
                preferences.getLastVolume(systemVolume),
                maxVolume
        );
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
    }

    private void scheduleProgressUpdates() {
        progressHandler.removeCallbacks(progressUpdateRunnable);
        if (observingProgress && playbackState == PlaybackState.PLAYING && mediaPlayer != null) {
            progressHandler.post(progressUpdateRunnable);
        }
    }

    private void emitUiState() {
        if (listener == null) {
            return;
        }

        listener.onUiStateChanged(
                PlayerUiStateFactory.create(
                        getCurrentTrack(),
                        currentTrackIndex,
                        tracks.size(),
                        playbackState,
                        getCurrentPosition(),
                        getDuration(),
                        currentVolume,
                        maxVolume,
                        playbackState == PlaybackState.ERROR ? errorMessage : null
                )
        );
    }

    private void prepareTrack(boolean autoplay) {
        stopObservingProgress();
        releaseMediaPlayer();

        Track currentTrack = getCurrentTrack();
        mediaPlayer = MediaPlayer.create(context, currentTrack.getAudioResId());
        if (mediaPlayer == null) {
            playbackState = PlaybackState.ERROR;
            errorMessage = context.getString(R.string.error_audio_init);
            emitUiState();
            return;
        }

        errorMessage = null;
        int restoredPosition = PlayerTimeFormatter.clampPosition(
                preferences.getLastPosition(currentTrack.getId()),
                mediaPlayer.getDuration()
        );
        if (restoredPosition > 0) {
            mediaPlayer.seekTo(restoredPosition);
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playbackState = PlaybackState.COMPLETED;
                emitUiState();
                stopObservingProgress();
            }
        });

        playbackState = PlaybackState.READY;
        emitUiState();

        if (autoplay) {
            mediaPlayer.start();
            playbackState = PlaybackState.PLAYING;
            emitUiState();
            if (observingProgress) {
                scheduleProgressUpdates();
            }
        }
    }

    private void persistCurrentTrackPosition() {
        if (mediaPlayer != null) {
            preferences.saveLastPosition(getCurrentTrack().getId(), mediaPlayer.getCurrentPosition());
        }
    }

    private int resolveInitialTrackIndex() {
        String lastTrackId = preferences.getLastTrackId();
        if (lastTrackId == null) {
            return 0;
        }

        for (int i = 0; i < tracks.size(); i++) {
            if (lastTrackId.equals(tracks.get(i).getId())) {
                return i;
            }
        }
        return 0;
    }

    private Track getCurrentTrack() {
        return tracks.get(currentTrackIndex);
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private int getCurrentPosition() {
        if (mediaPlayer == null) {
            return 0;
        }
        return mediaPlayer.getCurrentPosition();
    }

    private int getDuration() {
        if (mediaPlayer == null) {
            return 0;
        }
        return mediaPlayer.getDuration();
    }
}

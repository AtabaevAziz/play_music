package com.example.playingaudio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PlayerController playerController;
    private View previousControl;
    private View playControl;
    private View nextControl;
    private ImageView artworkIcon;
    private ImageView playPauseIcon;
    private ImageView previousIcon;
    private ImageView nextIcon;
    private SeekBar playbackSeekBar;
    private SeekBar volumeSeekBar;
    private TextView trackTitle;
    private TextView trackArtist;
    private TextView trackAlbum;
    private TextView trackIndex;
    private TextView currentTime;
    private TextView durationTime;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        setupControls();
        playerController = new MediaPlayerController(
                this,
                buildTracks(),
                new PlayerController.Listener() {
                    @Override
                    public void onUiStateChanged(final PlayerUiState uiState) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                render(uiState);
                            }
                        });
                    }
                }
        );
        playerController.initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerController.startObservingProgress();
    }

    @Override
    protected void onStop() {
        playerController.persistState();
        playerController.stopObservingProgress();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        playerController.release();
        super.onDestroy();
    }

    private void bindViews() {
        previousControl = findViewById(R.id.previousControl);
        playControl = findViewById(R.id.playControl);
        nextControl = findViewById(R.id.nextControl);
        artworkIcon = findViewById(R.id.artworkIcon);
        playPauseIcon = findViewById(R.id.playPauseIcon);
        previousIcon = findViewById(R.id.previousIcon);
        nextIcon = findViewById(R.id.nextIcon);
        playbackSeekBar = findViewById(R.id.playbackSeekBar);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        trackTitle = findViewById(R.id.trackTitle);
        trackArtist = findViewById(R.id.trackArtist);
        trackAlbum = findViewById(R.id.trackAlbum);
        trackIndex = findViewById(R.id.trackIndex);
        currentTime = findViewById(R.id.currentTime);
        durationTime = findViewById(R.id.durationTime);
        errorMessage = findViewById(R.id.errorMessage);
    }

    private void setupControls() {
        View.OnClickListener togglePlaybackListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerController.togglePlayback();
            }
        };
        View.OnClickListener restartTrackListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerController.playPrevious();
            }
        };
        View.OnClickListener skipToEndListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerController.playNext();
            }
        };

        previousControl.setOnClickListener(restartTrackListener);
        previousIcon.setOnClickListener(restartTrackListener);
        playControl.setOnClickListener(togglePlaybackListener);
        playPauseIcon.setOnClickListener(togglePlaybackListener);
        nextControl.setOnClickListener(skipToEndListener);
        nextIcon.setOnClickListener(skipToEndListener);

        playbackSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    playerController.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    playerController.setVolume(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void render(PlayerUiState uiState) {
        trackTitle.setText(uiState.getTitle());
        trackArtist.setText(uiState.getArtist());
        trackAlbum.setText(uiState.getAlbum());
        trackIndex.setText(uiState.getTrackIndexLabel());
        currentTime.setText(uiState.getCurrentTimeLabel());
        durationTime.setText(uiState.getDurationLabel());
        errorMessage.setVisibility(uiState.hasError() ? View.VISIBLE : View.GONE);
        errorMessage.setText(uiState.getErrorMessage());
        artworkIcon.setImageResource(uiState.getArtworkResId());

        playbackSeekBar.setEnabled(uiState.canSeek());
        playbackSeekBar.setMax(uiState.getDurationMs());
        playbackSeekBar.setProgress(uiState.getCurrentPositionMs());

        volumeSeekBar.setEnabled(uiState.getVolumeMax() > 0);
        volumeSeekBar.setMax(uiState.getVolumeMax());
        volumeSeekBar.setProgress(uiState.getVolumeProgress());

        playPauseIcon.setImageResource(
                uiState.isPlaying() ? R.drawable.ic_pause_black_24dp : R.drawable.ic_play_arrow_black_24dp
        );

        updateControlEnabledState(previousControl, uiState.isControlsEnabled());
        updateControlEnabledState(playControl, uiState.isControlsEnabled());
        updateControlEnabledState(nextControl, uiState.isControlsEnabled());
        updateControlEnabledState(previousIcon, uiState.isControlsEnabled());
        updateControlEnabledState(playPauseIcon, uiState.isControlsEnabled());
        updateControlEnabledState(nextIcon, uiState.isControlsEnabled());
    }

    private void updateControlEnabledState(View view, boolean enabled) {
        view.setEnabled(enabled);
        view.setAlpha(enabled ? 1f : 0.35f);
    }

    private List<Track> buildTracks() {
        List<Track> tracks = new ArrayList<>();
        tracks.add(
                new Track(
                        "queen_we_will_rock_you",
                        getString(R.string.track_title_we_will_rock_you),
                        getString(R.string.track_artist_queen),
                        getString(R.string.track_album_greatest_hits),
                        R.raw.wewillrockyou,
                        R.drawable.ic_music_note_black_24dp
                )
        );
        tracks.add(
                new Track(
                        "stuff_demo_track",
                        getString(R.string.track_title_stuff),
                        getString(R.string.track_artist_demo),
                        getString(R.string.track_album_local_library),
                        R.raw.stuff,
                        R.drawable.ic_music_note_black_24dp
                )
        );
        return tracks;
    }
}

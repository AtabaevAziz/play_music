package com.example.playingaudio;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlayerLogicTest {

    @Test
    public void formatTime_usesMinuteSecondFormat() {
        assertEquals("0:00", PlayerTimeFormatter.formatTime(0));
        assertEquals("1:05", PlayerTimeFormatter.formatTime(65000));
        assertEquals("10:01", PlayerTimeFormatter.formatTime(601000));
    }

    @Test
    public void clampPosition_keepsValueInsideDuration() {
        assertEquals(0, PlayerTimeFormatter.clampPosition(-20, 2000));
        assertEquals(1200, PlayerTimeFormatter.clampPosition(1200, 2000));
        assertEquals(2000, PlayerTimeFormatter.clampPosition(2800, 2000));
    }

    @Test
    public void createUiState_mapsPlayingState() {
        Track track = new Track("track_1", "Song", "Artist", "Album", 1, 2);

        PlayerUiState uiState = PlayerUiStateFactory.create(
                track,
                1,
                2,
                PlaybackState.PLAYING,
                15000,
                210000,
                4,
                10,
                null
        );

        assertEquals("Song", uiState.getTitle());
        assertEquals("Artist", uiState.getArtist());
        assertEquals("Album", uiState.getAlbum());
        assertEquals("Track 2 of 2", uiState.getTrackIndexLabel());
        assertEquals("0:15", uiState.getCurrentTimeLabel());
        assertEquals("3:30", uiState.getDurationLabel());
        assertTrue(uiState.isPlaying());
        assertTrue(uiState.isControlsEnabled());
        assertTrue(uiState.canSeek());
        assertFalse(uiState.hasError());
    }

    @Test
    public void createUiState_mapsErrorState() {
        Track track = new Track("track_1", "Song", "Artist", "Album", 1, 2);

        PlayerUiState uiState = PlayerUiStateFactory.create(
                track,
                0,
                2,
                PlaybackState.ERROR,
                5000,
                210000,
                3,
                10,
                "Unable to initialize audio playback."
        );

        assertFalse(uiState.isPlaying());
        assertFalse(uiState.isControlsEnabled());
        assertFalse(uiState.canSeek());
        assertTrue(uiState.hasError());
        assertEquals("Unable to initialize audio playback.", uiState.getErrorMessage());
    }
}

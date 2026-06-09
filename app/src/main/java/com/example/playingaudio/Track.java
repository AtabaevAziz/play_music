package com.example.playingaudio;

public class Track {

    private final String id;
    private final String title;
    private final String artist;
    private final String album;
    private final int audioResId;
    private final int artworkResId;

    public Track(String id, String title, String artist, String album, int audioResId, int artworkResId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.audioResId = audioResId;
        this.artworkResId = artworkResId;
    }

    public String getId() {
        return id;
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

    public int getAudioResId() {
        return audioResId;
    }

    public int getArtworkResId() {
        return artworkResId;
    }
}

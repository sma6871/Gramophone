package com.rahnema.gramophone.models;

import org.parceler.Parcel;

/**
 * @author Payam1991 on 9/18/2017.
 */

@Parcel
public class Song {
    private long id;
    private long albumId;
    String title;
    String artistName;
    private long duration;


    public Song() {
    }

    public Song(long id, long albumId, String title, String artistName, long duration) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.artistName = artistName;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}

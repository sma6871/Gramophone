package com.rahnema.gramophone.MusicPlayer;

/**
 * @author Payam1991 on 9/18/2017.
 */

class MusicManager {
    private static final MusicManager ourInstance = new MusicManager();

    static MusicManager getInstance() {
        return ourInstance;
    }

    private MusicManager() {
    }
}

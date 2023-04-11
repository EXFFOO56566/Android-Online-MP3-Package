package com.vpapps.interfaces;

import com.vpapps.item.ItemSong;

import java.util.ArrayList;

public interface SongListener {
    void onStart();

    void onEnd(String success, ArrayList<ItemSong> arrayListSong);
}

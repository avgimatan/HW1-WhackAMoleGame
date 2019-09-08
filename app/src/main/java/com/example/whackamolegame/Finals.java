package com.example.whackamolegame;

import android.Manifest;

public interface Finals {

    int SIZE = 9;
    int MAX_MISSES = 3;
    int BOMB_TIME = 3;
    int TOGETHER_TIME = 1;

    int LOCATION_PERMISSION_REQUEST_CODE = 1;
    String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
}
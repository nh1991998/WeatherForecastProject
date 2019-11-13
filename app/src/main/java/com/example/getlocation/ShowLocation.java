package com.example.getlocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;


public class ShowLocation {
    private FusedLocationProviderClient client;
    private LocationManager lm;
    private Location loc;
    private Activity activity;

    public ShowLocation(Activity activity) {
        this.activity = activity;

        lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ){
            return;
        }
        double longitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
        double latitude = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
        loc = new Location(latitude,longitude);
    }
    public Location getLocation(){
        return loc;
    }
}

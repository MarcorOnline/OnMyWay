package com.onmyway.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.onmyway.NewAppointmentActivity;
import com.onmyway.R;
import com.onmyway.adapters.PlaceAutocompleteAdapter;

public class LocationHelper
{
    private Location lastUserLocation;
    private Marker userMarker;
    private GoogleApiClient GPlayClient;

    public GoogleApiClient getGoogleApiClient(Context context, GoogleApiClient.ConnectionCallbacks connectionCallback, GoogleApiClient.OnConnectionFailedListener connectionFailed)
    {
        GPlayClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(connectionCallback)
                .addOnConnectionFailedListener(connectionFailed)
                .addApi(LocationServices.API)
                .build();

        return GPlayClient;
    }

    public GoogleApiClient getGoogleApiClientWithAutocomplete(FragmentActivity activity, GoogleApiClient.ConnectionCallbacks connectionCallback, GoogleApiClient.OnConnectionFailedListener connectionFailed)
    {
        GPlayClient = new GoogleApiClient.Builder(activity).addConnectionCallbacks(connectionCallback)
                .addOnConnectionFailedListener(connectionFailed)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(activity, 1, connectionFailed).build();

        return GPlayClient;
    }

    public LatLng getLocation() {
        lastUserLocation = LocationServices.FusedLocationApi.getLastLocation(GPlayClient);
        if (lastUserLocation != null)
            return new LatLng(lastUserLocation.getLatitude(), lastUserLocation.getLongitude());
        else
            return new LatLng(0,0);
    }

    public void drawPushPin(GoogleMap map, LatLng position, boolean autoCenter)
    {
        if (userMarker == null)
        {
            userMarker = map.addMarker(new MarkerOptions()
                            .position(position)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_place))
            );
        }
        else
        {
            userMarker.setPosition(position);
        }

        if(autoCenter)
            centerMap(map, position);
    }

    public void centerMap(GoogleMap map, LatLng position)
    {
        map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(position).zoom(15).build()));
    }
}
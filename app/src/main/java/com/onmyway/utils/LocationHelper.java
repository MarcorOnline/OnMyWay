package com.onmyway.utils;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.onmyway.NewAppointmentActivity;
import com.onmyway.R;
import com.onmyway.adapters.PlaceAutocompleteAdapter;

/**
 * Created by Marco on 08/06/2015.
 */
public class LocationHelper
{
    private Location lastUserLocation;
    private Marker userMarker;
    private GoogleApiClient GPlayClient;

    public GoogleApiClient getGoogleApiClient(FragmentActivity activity, GoogleApiClient.ConnectionCallbacks connectionCallback, GoogleApiClient.OnConnectionFailedListener connectionFailed, boolean withAutocomplete)
    {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(activity);

        builder.addConnectionCallbacks(connectionCallback)
                .addOnConnectionFailedListener(connectionFailed)
                .addApi(LocationServices.API);

        if (withAutocomplete) {
            //autocomplete
            builder.addApi(Places.GEO_DATA_API)
                    .enableAutoManage(activity, 1, connectionFailed);
        }

        GPlayClient = builder.build();

        return GPlayClient;
    }

    public LatLng getLocation() {
        lastUserLocation = LocationServices.FusedLocationApi.getLastLocation(GPlayClient);
        if (lastUserLocation != null)
            return new LatLng(lastUserLocation.getLatitude(), lastUserLocation.getLongitude());
        else
            return new LatLng(0,0);
    }

    public void centerMap(GoogleMap map, LatLng position)
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

        map.moveCamera(CameraUpdateFactory.newLatLng(position));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
}
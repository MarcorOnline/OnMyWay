package com.onmyway.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.onmyway.model.Appointment;
import com.onmyway.model.AppointmentBase;
import com.onmyway.responses.SyncResponse;
import com.onmyway.utils.ApiCallback;
import com.onmyway.utils.LocationHelper;
import com.onmyway.utils.PreferencesHelper;
import com.onmyway.utils.ServiceGateway;
import com.onmyway.utils.StorageHelper;
import com.onmyway.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class SynchronizeService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private LocationHelper lh;
    private GoogleApiClient gClient;
    private Context context;
    private Integer toSyncCount;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this.getBaseContext();

        lh = new LocationHelper();
        gClient = lh.getGoogleApiClient(context, this, this);
        gClient.connect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT);

            LatLng location = lh.getLocation();

            if (location != null) {
                String phoneNumber = PreferencesHelper.getPhoneNumber(context);
                String userStatus = PreferencesHelper.getStatus(context);

                if (!StringUtils.isNullOrWhiteSpaces(phoneNumber)) {
                    ArrayList<AppointmentBase> appointments = StorageHelper.readAppointments(context);

                    Collections.sort(appointments, new AppointmentBase.TrackingTimeComparator());

                    Calendar start = Calendar.getInstance();
                    Calendar end = Calendar.getInstance();
                    end.add(Calendar.MINUTE, 30);

                    final ArrayList<AppointmentBase> toSync = new ArrayList<>();
                    for (AppointmentBase a : appointments) {
                        if (a.getTrackingDateTime().compareTo(start) == 1 && a.getStartDateTime().compareTo(end) == -1) {
                            toSync.add(a);
                        }
                    }

                    toSyncCount = toSync.size();

                    for (AppointmentBase a : toSync) {
                        //SYNC
                        ServiceGateway.SynchronizeBackground(a.getId(), phoneNumber, location, userStatus, new ApiCallback<SyncResponse>() {
                            @Override
                            public void OnComplete(SyncResponse result) {
                                synchronized (toSyncCount) {
                                    toSyncCount--;

                                    if (toSyncCount == 0) {
                                        SynchronizeService.this.stopSelf();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
        catch(Exception e){
            this.stopSelf();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        SynchronizeService.this.stopSelf();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        SynchronizeService.this.stopSelf();
    }
}

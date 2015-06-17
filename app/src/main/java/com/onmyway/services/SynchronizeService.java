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
import com.onmyway.model.AppointmentBase;
import com.onmyway.model.Notification;
import com.onmyway.responses.SyncResponse;
import com.onmyway.utils.ApiCallback;
import com.onmyway.utils.LocationHelper;
import com.onmyway.utils.NotificationsHelper;
import com.onmyway.utils.PreferencesHelper;
import com.onmyway.utils.ServiceGateway;
import com.onmyway.utils.StorageHelper;
import com.onmyway.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class SynchronizeService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    //TODO togliere tutti i Toast che parlando di Background

    private LocationHelper lh;
    private GoogleApiClient gClient;
    private Context context;
    private Integer toSyncCount;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this.getBaseContext();

        Toast.makeText(context, "Background started", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(context, "Background connected", Toast.LENGTH_SHORT).show();

            LatLng location = lh.getLocation();

            if (location != null) {
                final String myPhoneNumber = PreferencesHelper.getPhoneNumber(context);
                String userStatus = PreferencesHelper.getStatus(context);

                if (!StringUtils.isNullOrWhiteSpaces(myPhoneNumber)) {
                    ArrayList<AppointmentBase> appointments = StorageHelper.readAppointments(context);

                    if(appointments != null && appointments.size() > 0)
                    {
                        Collections.sort(appointments, new AppointmentBase.TrackingTimeComparator());

                        Calendar now = Calendar.getInstance();
                        Calendar end = Calendar.getInstance();
                        end.add(Calendar.MINUTE, -30);

                        final ArrayList<AppointmentBase> toSync = new ArrayList<>();
                        for (AppointmentBase a : appointments) {
                            if (a.getTrackingDateTime().compareTo(now) == -1 && a.getStartDateTime().compareTo(end) == 1) {
                                toSync.add(a);
                            }
                        }

                        toSyncCount = toSync.size();

                        if(toSyncCount == 0) {
                            Toast.makeText(context, "Background nothing to sync", Toast.LENGTH_SHORT).show();
                            SynchronizeService.this.stopSelf();
                            return;
                        }

                        for (AppointmentBase a : toSync) {
                            //SYNC
                            ServiceGateway.SynchronizeBackground(a.getId(), myPhoneNumber, location, userStatus, new ApiCallback<SyncResponse>() {
                                @Override
                                public void OnComplete(SyncResponse result) {
                                    Context context = getApplicationContext();

                                    if (result != null && result.Data.Notifications != null && result.Data.Notifications.size() > 0) {
                                        for (Notification n : result.Data.Notifications) {
                                            NotificationsHelper.ShowNotificationInNotificationCenter(n, context, myPhoneNumber, result.Data.AppointmentId);
                                        }
                                    }

                                    synchronized (toSyncCount) {
                                        toSyncCount--;

                                        if (toSyncCount <= 0) {
                                            Toast.makeText(context, "Background completed", Toast.LENGTH_SHORT).show();
                                            SynchronizeService.this.stopSelf();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
        catch(Exception e){
            Toast.makeText(context, "Background error", Toast.LENGTH_SHORT).show();
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

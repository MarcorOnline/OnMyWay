package com.onmyway.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.onmyway.MainActivity;
import com.onmyway.model.AppointmentBase;
import com.onmyway.model.Location;
import com.onmyway.responses.SyncResponse;
import com.onmyway.utils.ActivityHelper;
import com.onmyway.utils.ApiCallback;
import com.onmyway.utils.LocationHelper;
import com.onmyway.utils.PreferencesHelper;
import com.onmyway.utils.ServiceGateway;
import com.onmyway.utils.StorageHelper;
import com.onmyway.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        arg0.startService(new Intent(arg0.getApplicationContext(), SynchronizeService.class));
    }
}
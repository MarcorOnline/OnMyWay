package com.onmyway;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.onmyway.model.Appointment;
import com.onmyway.model.User;
import com.onmyway.model.UserStatus;
import com.onmyway.responses.AppointmentResponse;
import com.onmyway.responses.UsersStatusResponse;
import com.onmyway.utils.ApiCallback;
import com.onmyway.utils.ServiceGateway;

import java.util.ArrayList;
import java.util.HashMap;


public class MapActivity extends ActionBarActivity implements OnMapReadyCallback
{
    private static String appointmentId;
    private static GoogleMap map;
    private static HashMap<String, Marker> markers = new HashMap<>();

    private Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        appointmentId = getIntent().getStringExtra("appointmentId");

        ServiceGateway.GetFullAppointmentAsync(appointmentId, new ApiCallback<AppointmentResponse>()
        {
            @Override
            public void OnComplete(AppointmentResponse result)
            {
                appointment = result.Data;
                InitMap(result.Data);
            }
        });

        mapUpdater = new Handler();
        startRepeatingTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

//        this.map.addMarker(new MarkerOptions()
//                .position(new LatLng(0, 0))
//                .title("Marker"))
//                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.Destination));
    }

    private void InitMap(Appointment appointment)
    {
        //primo draw, devo disegnare l'appuntamento
        Marker appointMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(appointment.getLocation().getLatitude(), appointment.getLocation().getLongitude()))
                .title(appointment.getLocation().getTitle())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination)));

        markers.put(appointment.getId(), appointMarker);

        Marker userMarker;
        for (User user : appointment.getValidUsers())
        {
            //L'utente non ha un marker, lo creo
            userMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(user.getLatitude(), user.getLongitude()))
                    .title(user.getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination))); //TODO avatar

            markers.put(appointment.getId(), userMarker);
        }
    }

    private void Refresh()
    {
        ServiceGateway.GetUsersStatusAsync(appointmentId, new ApiCallback<UsersStatusResponse>()
        {
            @Override
            public void OnComplete(UsersStatusResponse result)
            {
                Marker userMarker;

                for(UserStatus user : result.Data)
                {
                    //L'utente ha gia un marker, lo aggiorno
                    userMarker = markers.get(user.getPhoneNumber());
                    userMarker.setPosition(new LatLng(user.getLatitude(), user.getLongitude())); //TODO update status
                }
            }
        });
    }


    private int mInterval = 3000; // aggiorna ogni 3 secondi
    //private int mInterval = 30000; // aggiorna ogni 30 secondi
    private Handler mapUpdater;

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            Refresh();
            mapUpdater.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mapUpdater.removeCallbacks(mStatusChecker);
    }
}

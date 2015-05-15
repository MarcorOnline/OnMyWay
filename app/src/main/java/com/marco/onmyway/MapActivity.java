package com.marco.onmyway;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.marco.onmyway.model.Appointment;
import com.marco.onmyway.model.GlobalData;
import com.marco.onmyway.utils.ApiCallback;
import com.marco.onmyway.utils.ServiceGateway;


public class MapActivity extends ActionBarActivity  implements OnMapReadyCallback
{
    private static String appointmentId;

    private Appointment appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        appointmentId = getIntent().getStringExtra("appointmentId");

        ServiceGateway.GetFullAppointmentAsync(appointmentId, new ApiCallback<Appointment>() {
            @Override
            public void OnComplete(Appointment result) {
                appointment = result;
                DrawOnMap(result);
            }
        });
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
        map.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }

    private void DrawOnMap(Appointment appointment){
        //disegnare sulla mappa
    }

    private void Refresh(){
        //chiama ServiceGateway.GetUsersStatusAsync e poi chiama DrawOnMap
    }
}

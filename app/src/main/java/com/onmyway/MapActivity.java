package com.onmyway;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.onmyway.model.Appointment;
import com.onmyway.model.GlobalData;
import com.onmyway.model.User;
import com.onmyway.model.UserStatus;
import com.onmyway.responses.AppointmentResponse;
import com.onmyway.responses.BooleanResponse;
import com.onmyway.responses.SyncResponse;
import com.onmyway.utils.ActivityHelper;
import com.onmyway.utils.ApiCallback;
import com.onmyway.utils.ContactsHelper;
import com.onmyway.utils.LocationHelper;
import com.onmyway.utils.ServiceGateway;
import com.onmyway.utils.StringUtils;

import java.util.HashMap;


public class MapActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks
{
    private static String appointmentId;
    private static GoogleMap map;
    private static HashMap<String, Marker> markers = new HashMap<>();
    private GoogleApiClient gClient;
    private LocationHelper lh;

    private Appointment appointment;

    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        toolbar = ActivityHelper.setActionBar(this);

        lh = new LocationHelper();
        gClient = lh.getGoogleApiClient(this, this, this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        appointmentId = getIntent().getStringExtra("appointmentId");
    }

    @Override
    public void onConnected(Bundle bundle) {
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
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_status){
            DialogFragment dialog = new StatusDialog();
            dialog.show(getFragmentManager(), "StatusDialog");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        ServiceGateway.GetFullAppointmentAsync(appointmentId, new ApiCallback<AppointmentResponse>()
        {
            @Override
            public void OnComplete(AppointmentResponse result)
            {
                appointment = result.Data;
                InitMap(result.Data);
            }
        });
    }

    private void InitMap(Appointment appointment)
    {
        ContactsHelper.ResolveContactsNames(this, appointment.getValidUsers());

        //primo draw, devo disegnare l'appuntamento
        Marker appointMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(appointment.getLocation().getLatitude(), appointment.getLocation().getLongitude()))
                .title(appointment.getLocation().getTitle())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination)));

        markers.put(appointment.getId(), appointMarker);

        Marker userMarker;
        String userLabel;
        for (User user : appointment.getValidUsers())
        {
            userLabel = user.getName();
            if (StringUtils.isNullOrWhiteSpaces((userLabel)))
                userLabel = user.getPhoneNumber();

            //L'utente non ha un marker, lo creo
            userMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(user.getLatitude(), user.getLongitude()))
                    .title(userLabel)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination))); //TODO avatar

            markers.put(appointment.getId(), userMarker);
        }
    }

    private void RefreshMarkersAndSendPosition()
    {
        if (markers.size() > 0) {
            User user = GlobalData.getLoggedUser();
            LatLng location = lh.getLocation();

            ServiceGateway.SynchronizeForeground(appointmentId, user.getPhoneNumber(), user.getStatus(), location, new ApiCallback<SyncResponse>() {
                @Override
                public void OnComplete(SyncResponse result) {
                    Marker userMarker;

                    if (result.AttendeeStates != null)
                        for (UserStatus user : result.AttendeeStates) {
                            //L'utente ha gia un marker, lo aggiorno
                            userMarker = markers.get(user.getPhoneNumber());
                            userMarker.setTitle(user.getStatus());
                            userMarker.setPosition(new LatLng(user.getLatitude(), user.getLongitude()));
                        }
                }
            });
        }
    }


    private int mInterval = 3000; // aggiorna ogni 3 secondi
    //private int mInterval = 30000; // aggiorna ogni 30 secondi
    private Handler mapUpdater;

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            RefreshMarkersAndSendPosition();
            mapUpdater.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask() {
        mapUpdater = new Handler();
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        if (mapUpdater != null)
            mapUpdater.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void showToast(String message, boolean shortDuration){
        Toast toast = Toast.makeText(this, message, shortDuration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        toast.show();
    }

    public static class StatusDialog extends DialogFragment
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            final LayoutInflater inflater = getActivity().getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.input_dialog, null);
            final EditText inputText = (EditText)dialogView.findViewById(R.id.inputText);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(dialogView)
                    // Add action buttons
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //update user status
                            User user = GlobalData.getLoggedUser();
                            user.setStatus(inputText.getText().toString());

                            ServiceGateway.UpdateUser(user.getPhoneNumber(), user.getStatus(), user.getAvatar(), new ApiCallback<BooleanResponse>() {
                                @Override
                                public void OnComplete(BooleanResponse result) {
                                    String message;

                                    if(result.Data)
                                        message = "Status updated";
                                    else
                                        message = "Status update error";

                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                }
                            });

                            StatusDialog.this.getDialog().dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            StatusDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }
}

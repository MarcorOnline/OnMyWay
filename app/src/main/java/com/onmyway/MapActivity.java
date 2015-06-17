package com.onmyway;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
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
import com.onmyway.model.Notification;
import com.onmyway.model.User;
import com.onmyway.model.UserStatus;
import com.onmyway.responses.AppointmentResponse;
import com.onmyway.responses.BooleanResponse;
import com.onmyway.responses.SyncResponse;
import com.onmyway.responses.UserResponse;
import com.onmyway.utils.ActivityHelper;
import com.onmyway.utils.ApiCallback;
import com.onmyway.utils.AvatarHelper;
import com.onmyway.utils.ContactsHelper;
import com.onmyway.utils.LocationHelper;
import com.onmyway.utils.MessageHelper;
import com.onmyway.utils.NotificationsHelper;
import com.onmyway.utils.ServiceGateway;
import com.onmyway.utils.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;


public class MapActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks
{
    private static String appointmentId;
    private static GoogleMap map;
    private static HashMap<String, Marker> markers = new HashMap<>();
    private GoogleApiClient gClient;
    private LocationHelper lh;

    private boolean isGClientConnected = false;
    private boolean isMapReady = false;

    private Appointment appointment;

    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        toolbar = ActivityHelper.setActionBar(this);

        lh = new LocationHelper();
        gClient = lh.getGoogleApiClient(this, this, this);
        gClient.connect();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        appointmentId = getIntent().getStringExtra("appointmentId");
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        stopRepeatingTask();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        isGClientConnected = true;
        if(isMapReady)
            autoLogin();
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        this.map = map;

        isMapReady = true;
        if (isGClientConnected)
            autoLogin();
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
        } else if (id == R.id.action_status)
        {
            DialogFragment dialog = new StatusDialog();
            dialog.show(getFragmentManager(), "StatusDialog");
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean loginTask = false;

    private void autoLogin()
    {
        String phoneNumber = ActivityHelper.getCurrentPhoneNumber(this);

        if (GlobalData.getLoggedUser() == null)
        {
            if (!loginTask)
            {
                loginTask = true;

                ServiceGateway.LoginAsync(phoneNumber, new ApiCallback<UserResponse>()
                {
                    @Override
                    public void OnComplete(UserResponse result)
                    {
                        loginTask = false;

                        if (result != null && result.Data != null && !StringUtils.isNullOrWhiteSpaces(result.Data.getPhoneNumber()))
                        {
                            GlobalData.setLoggedUser(result.Data);
                            getFullAppointment();
                        } else
                        {
                            MessageHelper.showDialog(MapActivity.this,
                                    getString(R.string.unable_to_login),
                                    getString(R.string.ok_to_retry),
                                    new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            dialog.dismiss();
                                            autoLogin();
                                        }
                                    });
                        }
                    }
                });
            }
        }
        else
        {
            getFullAppointment();
        }
    }

    private void getFullAppointment()
    {
        ServiceGateway.GetFullAppointmentAsync(appointmentId, new ApiCallback<AppointmentResponse>()
        {
            @Override
            public void OnComplete(AppointmentResponse result)
            {
                if (result.Data != null)
                {
                    getSupportActionBar().setTitle(result.Data.getTitle());
                    appointment = result.Data;
                    InitMap(result.Data);

                    //start periodic task
                    startRepeatingTask();
                }
            }
        });
    }

    private void InitMap(Appointment appointment)
    {
        ContactsHelper.resolveContactsNames(this, appointment.getValidUsers());

        DateTimeFormatter formatter = org.joda.time.format.DateTimeFormat.shortTime();
        String formattedTime = formatter.print(new DateTime(appointment.getStartDateTime()));
        String appointMarkerTitle = appointment.getLocation().getTitle();

        Marker userMarker;
        String userLabel;
        Bitmap bmp;
        for (User user : appointment.getValidUsers())
        {
            userLabel = user.getName();
            if (StringUtils.isNullOrWhiteSpaces((userLabel)))
                userLabel = user.getPhoneNumber();

            bmp = getResizedBitmap(AvatarHelper.GetDrawableAvatarFromString(user.getAvatar()), null, null);

            //L'utente non ha un marker, lo creo
            userMarker = map.addMarker(new MarkerOptions()
                            .position(user.getLatLng())
                            .title(userLabel)
                            .snippet(user.getStatus())
                            .icon(BitmapDescriptorFactory.fromBitmap(bmp))
            );

            markers.put(user.getPhoneNumber(), userMarker);
        }

        LatLng appointPosition = appointment.getLocation().toLatLng();

        //primo draw, devo disegnare l'appuntamento
        Marker appointMarker = map.addMarker(new MarkerOptions()
                .position(appointPosition)
                .title(appointMarkerTitle)
                .snippet(formattedTime)
                .icon(BitmapDescriptorFactory.fromBitmap(getResizedBitmap(R.drawable.destination, null, null))));

        markers.put(appointment.getId(), appointMarker);

        lh.centerMap(map, appointPosition);
    }

    private Bitmap getResizedBitmap(int resourceId, @Nullable Integer width, @Nullable Integer height)
    {
        if (width == null)
            width = 75;

        if (height == null)
            height = 75;

        BitmapDrawable bd = (BitmapDrawable) getResources().getDrawable(resourceId);
        Bitmap b = bd.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }

    private void SynchronizeAll()
    {
        if (markers.size() > 0)
        {
            User user = GlobalData.getLoggedUser();
            LatLng location = lh.getLocation();

            ServiceGateway.SynchronizeForeground(appointmentId, user.getPhoneNumber(), user.getStatus(), location, new ApiCallback<SyncResponse>()
            {
                @Override
                public void OnComplete(SyncResponse result)
                {
                    if (result != null && result.Data != null)
                    {
                        Marker userMarker;

                        if (result.Data.AttendeeStates != null)
                            for (UserStatus user : result.Data.AttendeeStates)
                            {
                                //L'utente ha gia un marker, lo aggiorno
                                userMarker = markers.get(user.getPhoneNumber());
                                userMarker.setSnippet(user.getStatus());
                                userMarker.setPosition(user.getLatLng());
                            }

                        String myPhoneNumber = GlobalData.getLoggedUser().getPhoneNumber();

                        if (result.Data.Notifications != null)
                            for (Notification n : result.Data.Notifications)
                                NotificationsHelper.ShowDialog(n, myPhoneNumber, MapActivity.this);
                    }
                }
            });
        }
    }

    //TODO in produzione mettere 20 secondi
    private int mInterval = 5000; // aggiorna ogni 5 secondi
    //private int mInterval = 20000; // aggiorna ogni 20 secondi
    private Handler mapUpdater;

    Runnable mStatusChecker = new Runnable()
    {
        @Override
        public void run()
        {
            SynchronizeAll();
            mapUpdater.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask()
    {
        mapUpdater = new Handler();
        mStatusChecker.run();
    }

    void stopRepeatingTask()
    {
        if (mapUpdater != null)
            mapUpdater.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }

    public static class StatusDialog extends DialogFragment
    {
        private Activity parentActivity;

        @Override
        public void onAttach(Activity activity)
        {
            super.onAttach(activity);
            parentActivity = activity;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            final LayoutInflater inflater = getActivity().getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.input_dialog, null);
            final EditText inputText = (EditText) dialogView.findViewById(R.id.inputText);

            //riempio con lo stato attuale
            if (GlobalData.getLoggedUser().getStatus() != null)
                inputText.setText(GlobalData.getLoggedUser().getStatus());

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(dialogView)
                    // Add action buttons
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            //update user status
                            User user = GlobalData.getLoggedUser();
                            user.setStatus(inputText.getText().toString());

                            ServiceGateway.UpdateUser(user.getPhoneNumber(), user.getStatus(), user.getAvatar(), new ApiCallback<BooleanResponse>()
                            {
                                @Override
                                public void OnComplete(BooleanResponse result)
                                {
                                    String message;

                                    if (result != null && result.Data)
                                        message = "Status updated";
                                    else
                                        message = "Status update error";

                                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show();
                                }
                            });

                            StatusDialog.this.getDialog().dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            StatusDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }
}

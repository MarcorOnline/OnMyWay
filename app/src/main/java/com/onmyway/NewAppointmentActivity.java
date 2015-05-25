package com.onmyway;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.*;
import com.google.android.gms.location.places.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.onmyway.adapters.PlaceAutocompleteAdapter;
import com.onmyway.model.Appointment;
import com.onmyway.model.GlobalData;
import com.onmyway.utils.ApiCallback;
import com.onmyway.utils.ServiceGateway;
import com.onmyway.utils.StringUtils;
import com.onmyway.responses.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class NewAppointmentActivity extends ActionBarActivity  implements OnMapReadyCallback
{
    public final static int MODE_START_DATETIME = 1;
    public final static int MODE_TRACKING_DATETIME = 2;

    // UI references.
    private AutoCompleteTextView locationView;
    private EditText titleView;

    private static EditText dateView;
    private static EditText timeView;

    private static EditText trackingDateView;
    private static EditText trackingTimeView;

    private static Calendar trackingDate;
    private static Calendar trackingTime;
    private static Calendar startDate;
    private static Calendar startTime;

    private View progressView;
    private View normalView;

    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private static Appointment newAppointment = new Appointment();

    private GoogleMap map;
    private static GoogleApiClient GPlayClient;
    private LocationHelper lh;
    private Place place;

    // Async Tasks
    private boolean uploadTask;

    // Autocomplete utils
    private PlaceAutocompleteAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);

        //GET UI items
        locationView = (AutoCompleteTextView) findViewById(R.id.locationBox);

        titleView = (EditText) findViewById(R.id.titleBox);

        timeView = (EditText) findViewById(R.id.timeBox);
        dateView = (EditText) findViewById(R.id.dateBox);

        trackingTimeView = (EditText) findViewById(R.id.trackingTimeBox);
        trackingDateView = (EditText) findViewById(R.id.trackingDateBox);

        normalView = findViewById(R.id.form);
        progressView = findViewById(R.id.progress);

        Button addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    DialogFragment newFragment = new DatePickerFragment(MODE_START_DATETIME);
                    newFragment.show(getFragmentManager(), "datePicker");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DialogFragment newFragment = new TimePickerFragment(MODE_START_DATETIME);
                    newFragment.show(getFragmentManager(), "timePicker");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        trackingDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    DialogFragment newFragment = new DatePickerFragment(MODE_TRACKING_DATETIME);
                    newFragment.show(getFragmentManager(), "datePicker");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        trackingTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DialogFragment newFragment = new TimePickerFragment(MODE_TRACKING_DATETIME);
                    newFragment.show(getFragmentManager(), "timePicker");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //pre-populate tracking datetime
        Calendar now = Calendar.getInstance();
        setTrackingDate(now);
        setTrackingTime(now);

        //pre-populate appointment datetime
        now.add(Calendar.MINUTE, 30);
        setAppointmentDate(now);
        setAppointmentTime(now);


        lh = new LocationHelper();

        GPlayClient = new GoogleApiClient.Builder(this)
                //map
                .addConnectionCallbacks(lh)
                .addOnConnectionFailedListener(lh)
                .addApi(LocationServices.API)
                //autocomplete
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, 1, lh)
                .build();

        GPlayClient.connect();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private static void setAppointmentDate(Calendar date)
    {
        startDate = date;
        dateView.setText(dateFormat.format(date.getTime()));

        Calendar startTime = newAppointment.getStartDateTime();
        startTime.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
    }

    private static void setAppointmentTime(Calendar time)
    {
        startTime = time;
        timeView.setText(timeFormat.format(time.getTime()));

        Calendar startTime = newAppointment.getStartDateTime();
        startTime.set(Calendar.HOUR, time.get(Calendar.HOUR));
        startTime.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
    }

    private static void setTrackingTime(Calendar time)
    {
        trackingTime = time;
        trackingTimeView.setText(timeFormat.format(time.getTime()));

        Calendar trackingTime = newAppointment.getTrackingDateTime();
        trackingTime.set(Calendar.HOUR, time.get(Calendar.HOUR));
        trackingTime.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
    }

    private static void setTrackingDate(Calendar date)
    {
        trackingDate = date;
        trackingDateView.setText(dateFormat.format(date.getTime()));

        Calendar trackingTime = newAppointment.getTrackingDateTime();
        trackingTime.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_appointment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_confirm) {
            //fill ne appointment
            newAppointment.setTitle(titleView.getText().toString());
            newAppointment.setLocation(place);
            newAppointment.setStartDateTime(startDate, startTime);
            newAppointment.setTrackingDateTime(trackingDate, trackingTime);

            /* TODO set users
            newAppointment.setInvalidUsers();
            newAppointment.setValidUsers();*/

            //validate and save
            saveAppointment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addFriend()
    {
        //TODO logica di aggiunta amici tramite picker rubrica
    }

    public void saveAppointment() {
        if (uploadTask)
            return;

        boolean canUpload = false;

        //logica di validazione di newAppointment
        if (StringUtils.IsNullOrWhiteSpaces(newAppointment.getTitle()))
            showToast(getString(R.string.missing_title), false);
        else
        {
            com.onmyway.model.Location l = newAppointment.getLocation();
            if (l.getLatitude() == 0 && l.getLongitude() == 0)
                showToast("You must set a location", false);
            else
                canUpload = true;
        }

        if (canUpload)
        {
            uploadTask = true;
            showProgress(true);

            ServiceGateway.UploadAppointmentAsync(GlobalData.getLoggedUser().getPhoneNumber(), newAppointment, new ApiCallback<AppointmentResponse>() {
                @Override
                public void OnComplete(AppointmentResponse result) {
                    showProgress(false);
                    uploadTask = false;
                }
            });
        }
    }

    private void showToast(String message, boolean shortDuration){
        Toast toast = Toast.makeText(this, message, shortDuration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            normalView.setVisibility(show ? View.GONE : View.VISIBLE);
            normalView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    normalView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            normalView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        /*ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.map = googleMap;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private int mode;

        public DatePickerFragment(int mode) throws Exception {
            if(mode != MODE_START_DATETIME && mode != MODE_TRACKING_DATETIME)
                throw new Exception("invalid mode");

            this.mode = mode;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar date;
            if(mode == MODE_START_DATETIME)
                date = newAppointment.getStartDateTime();
            else
                date = newAppointment.getTrackingDateTime();

            int year = date.get(Calendar.YEAR);
            int month = date.get(Calendar.MONTH);
            int day = date.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar date = Calendar.getInstance();
            date.set(year, month, day);

            if(mode == MODE_START_DATETIME)
                setAppointmentDate(date);
            else
                setTrackingDate(date);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private int mode;

        public TimePickerFragment(int mode) throws Exception
        {
            if(mode != MODE_START_DATETIME && mode != MODE_TRACKING_DATETIME)
                throw new Exception("invalid mode");

            this.mode = mode;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar date;
            if(mode == MODE_START_DATETIME)
                date = newAppointment.getStartDateTime();
            else
                date = newAppointment.getTrackingDateTime();

            int hour = date.get(Calendar.HOUR);
            int minute = date.get(Calendar.MINUTE);

            // Create a new instance of DatePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hour, int minute) {
            Calendar date = Calendar.getInstance();
            date.set(Calendar.HOUR, hour);
            date.set(Calendar.MINUTE, minute);

            if(mode == MODE_START_DATETIME)
                setAppointmentTime(date);
            else
                setTrackingTime(date);
        }
    }

    public class LocationHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
    {
        Location lastUserLocation;
        Marker userMarker;

        @Override
        public void onConnected(Bundle bundle)
        {
            lastUserLocation = LocationServices.FusedLocationApi.getLastLocation(GPlayClient);

            if (lastUserLocation != null)
            {
                LatLng position = new LatLng(lastUserLocation.getLatitude(), lastUserLocation.getLongitude());
                centerMap(position);
            }


            //calcolato empiricamente dai boundary di esempio di Sidney
            LatLngBounds bounds = new LatLngBounds(new LatLng(lh.lastUserLocation.getLatitude() - 0.18, lh.lastUserLocation.getLongitude() - 0.29), new LatLng(lh.lastUserLocation.getLatitude() + 0.18, lh.lastUserLocation.getLongitude() + 0.29));


        /*LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include();*/
            mAdapter = new PlaceAutocompleteAdapter(NewAppointmentActivity.this, android.R.layout.simple_list_item_1,
                    GPlayClient, bounds, null);

            locationView.setAdapter(mAdapter);

            locationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                    final String placeId = String.valueOf(item.placeId);

                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                            .getPlaceById(GPlayClient, placeId);
                    placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                }
            });
            locationView.setThreshold(3);
        }

        private void centerMap(LatLng position)
        {
            if(userMarker == null)
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

        @Override
        public void onConnectionSuspended(int i)
        {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult)
        {

        }
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {

            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            place = places.get(0);
            lh.centerMap(place.getLatLng());

            places.release();
        }
    };
}




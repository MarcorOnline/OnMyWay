package com.onmyway;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.*;
import com.google.android.gms.location.places.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.onmyway.adapters.PlaceAutocompleteAdapter;
import com.onmyway.model.Appointment;
import com.onmyway.model.GlobalData;
import com.onmyway.model.Location;
import com.onmyway.model.User;
import com.onmyway.utils.ActivityHelper;
import com.onmyway.utils.ApiCallback;
import com.onmyway.utils.ContactsHelper;
import com.onmyway.utils.LocationHelper;
import com.onmyway.utils.ServiceGateway;
import com.onmyway.utils.StringUtils;
import com.onmyway.responses.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class NewAppointmentActivity extends ActionBarActivity  implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
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

    private static Appointment newAppointment;

    //invited adapter
    private InvitedAdapter invitedAdapter;

    private GoogleMap map;
    private static GoogleApiClient GPlayClient;
    private LocationHelper lh;
    private Location location;

    // Async Tasks
    private boolean uploadTask;

    // Autocomplete utils
    private PlaceAutocompleteAdapter mAdapter;

    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);

        toolbar = ActivityHelper.setActionBar(this);

        newAppointment = new Appointment();
        //TODO si dovrebbe recuperare da savedInstanceState nel caso di sospensione dell'app (get...)

        //GET UI items
        locationView = (AutoCompleteTextView) findViewById(R.id.locationBox);

        titleView = (EditText) findViewById(R.id.titleBox);

        timeView = (EditText) findViewById(R.id.timeBox);
        dateView = (EditText) findViewById(R.id.dateBox);

        trackingTimeView = (EditText) findViewById(R.id.trackingTimeBox);
        trackingDateView = (EditText) findViewById(R.id.trackingDateBox);

        invitedAdapter = new InvitedAdapter(this, newAppointment.getValidUsers());
        ListView invitedList = (ListView) findViewById(R.id.invitedList);
        invitedList.setAdapter(invitedAdapter);

        normalView = findViewById(R.id.new_appointment_form);
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
                try {
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

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GPlayClient = lh.getGoogleApiClient(this, this, this);
        GPlayClient.connect();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        //TODO salvare per poter riprendere in caso di sospensione (put...)
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        LatLng userLocation = lh.getLocation();

        LatLngBounds bounds = null;

        if (userLocation != null) {
            lh.centerMap(map, userLocation);

            bounds = new LatLngBounds(new LatLng(userLocation.latitude - 0.18, userLocation.longitude - 0.29), new LatLng(userLocation.latitude + 0.18, userLocation.longitude + 0.29));
        }

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
            newAppointment.setLocation(location);
            newAppointment.setStartDateTime(startDate, startTime);
            newAppointment.setTrackingDateTime(trackingDate, trackingTime);

            //validate and save
            saveAppointment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addFriend()
    {
        ContactsHelper.startPickContact(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            User user = ContactsHelper.getPhoneNumberFromPickResponse(this, requestCode, data);

            if (!StringUtils.isNullOrWhiteSpaces(user.getPhoneNumber())) {
                newAppointment.getValidUsers().add(user);
                invitedAdapter.notifyDataSetChanged();
            }
            else
                showToast("Unable to add this contact because he haven't a mobile phone number", false);
        }
    }

    public void saveAppointment() {
        if (uploadTask)
            return;

        boolean canUpload = false;

        //logica di validazione di newAppointment
        if (StringUtils.isNullOrWhiteSpaces(newAppointment.getTitle()))
            showToast(getString(R.string.missing_title), false);
        else
        {
            com.onmyway.model.Location l = newAppointment.getLocation();
            if (l == null || (l.getLatitude() == 0 && l.getLongitude() == 0))
                showToast("You must set a location", false);
            else
            {
                if(newAppointment.getValidUsers().size() == 0)
                    showToast("You must invite at least one friend", false);
                else
                    canUpload = true;
            }
        }

        if (canUpload)
        {
            uploadTask = true;
            showProgress(true);

            User loggedUser = GlobalData.getLoggedUser();

            if (loggedUser != null)
                makeUpload();
            else
                loginAndContinue();
        }
    }

    private void loginAndContinue(){
        ServiceGateway.LoginAsync(ActivityHelper.getCurrentPhoneNumber(this), new ApiCallback<UserResponse>() {
            @Override
            public void OnComplete(UserResponse result) {
                if (result != null && result.Data != null) {
                    GlobalData.setLoggedUser(result.Data);
                    makeUpload();
                } else
                    loginAndContinue();
            }
        });
    }

    private void makeUpload() {
        ServiceGateway.UploadAppointmentAsync(GlobalData.getLoggedUser().getPhoneNumber(), newAppointment, new ApiCallback<AppointmentResponse>() {
            @Override
            public void OnComplete(AppointmentResponse result) {
                showProgress(false);
                uploadTask = false;

                if (result.Data != null && !StringUtils.isNullOrWhiteSpaces(result.Data.getId()))
                {
                    //SUCCESS
                    GlobalData.getAppointments().add(result.Data);
                    getParent().finish();
                }
                else
                    showToast("Error: unable to create the event", true);
            }
        });
    }

    private void showToast(String message, boolean shortDuration){
        Toast toast = Toast.makeText(this, message, shortDuration ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Shows the progress UI and hides the loginAndContinue form.
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

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @SuppressLint("ValidFragment")
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

    @SuppressLint("ValidFragment")
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
            Place place = places.get(0);
            lh.centerMap(map, place.getLatLng());

            String address = "";
            if (place.getAddress() != null)
                address = place.getAddress().toString();

            location = new Location(place.getName().toString(), address, place.getLatLng().latitude, place.getLatLng().longitude);

            places.release();
        }
    };

    public class InvitedAdapter extends ArrayAdapter {
        private LayoutInflater inflater;
        private Context context;
        private ArrayList<User> users;

        public InvitedAdapter(Context context, ArrayList<User> users) {
            super(context, android.R.layout.simple_list_item_2, users);

            this.context = context;
            this.users = users;

            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);

            User user = users.get(position);

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(user.getName());
            ((TextView) convertView.findViewById(android.R.id.text2)).setText(user.getPhoneNumber());

            return convertView;
        }
    }
}




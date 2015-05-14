package com.marco.onmyway;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;

import com.marco.onmyway.model.Appointment;
import com.marco.onmyway.model.AppointmentBase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class NewAppointmentActivity extends ActionBarActivity {

    // UI references.
    private AutoCompleteTextView locationView;
    private EditText titleView;
    private EditText dateView;
    private EditText timeView;
    private EditText trackingTimeView;
    private View progressView;
    private View formView;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private Appointment newAppointment = new Appointment();

    // Async Tasks
    private UploadAppointmentTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);

        //GET UI items

        locationView = (AutoCompleteTextView) findViewById(R.id.locationBox);
        populateAutoComplete();

        titleView = (EditText) findViewById(R.id.titleBox);
        timeView = (EditText) findViewById(R.id.timeBox);
        dateView = (EditText) findViewById(R.id.dateBox);
        trackingTimeView = (EditText) findViewById(R.id.trackingTimeBox);

        formView = findViewById(R.id.form);
        progressView = findViewById(R.id.progress);

        dateView.setFocusable(false);
        timeView.setFocusable(false);

        dateView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                                                @Override
                                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                    Calendar date = Calendar.getInstance();
                                                    date.set(year, monthOfYear, dayOfMonth);
                                                    setAppointmentDate(date);
                                                }
                                            };

                                            Calendar date = newAppointment.getStartTime();
                                            DatePickerDialog picker = new DatePickerDialog(getBaseContext(), listener,
                                                    date.get(Calendar.YEAR),
                                                    date.get(Calendar.MONTH),
                                                    date.get(Calendar.DAY_OF_MONTH));
                                        }
                                    });

        //pre-populate some items
        Calendar now = Calendar.getInstance();
        setAppointmentDate(now);
        setAppointmentTrackingTime(now);
        now.add(Calendar.MINUTE, 30);
        setAppointmentTime(now);

        //TODO: popolare mappa/location su posizione corrente
    }

    private void setAppointmentDate(Calendar date)
    {
        dateView.setText(dateFormat.format(date.getTime()));

        newAppointment.getStartTime().set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
    }

    private void setAppointmentTime(Calendar date)
    {
        timeView.setText(timeFormat.format(date.getTime()));

        Calendar startTime = newAppointment.getStartTime();
        startTime.set(Calendar.HOUR, date.get(Calendar.HOUR));
        startTime.set(Calendar.MINUTE, date.get(Calendar.MINUTE));
    }

    private void setAppointmentTrackingTime(Calendar date)
    {
        trackingTimeView.setText(timeFormat.format(date.getTime()));

        Calendar trackingTime = newAppointment.getTrackTime();
        trackingTime.set(Calendar.HOUR, date.get(Calendar.HOUR));
        trackingTime.set(Calendar.MINUTE, date.get(Calendar.MINUTE));
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_confirm) {
            //TODO gestire bottone confirm

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateAutoComplete() {
        //getLoaderManager().initLoader(0, null, this);
    }

    public void addFriend(View button)
    {
        //TODO logica di aggiunta amici tramite picker rubrica
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (uploadTask != null) {
            return;
        }

        boolean uploadable = false;

        //TODO logica di validazione e creazione oggetto
        AppointmentBase a = new AppointmentBase();

        if (uploadable)
        {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            uploadTask = new UploadAppointmentTask(a);
            uploadTask.execute((Void) null);
        }
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

            formView.setVisibility(show ? View.GONE : View.VISIBLE);
            formView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    formView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            formView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        /*ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);*/
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UploadAppointmentTask extends AsyncTask<Void, Void, Boolean> {

        private final AppointmentBase appointment;

        UploadAppointmentTask(AppointmentBase appointment) {
            this.appointment = appointment;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean success = false;
            // TODO: logica di invio al server di appointment (e torna true o false in base al risultato)

            return success;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            uploadTask = null;
            showProgress(false);

            if (success) {
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            uploadTask = null;
            showProgress(false);
        }
    }
}




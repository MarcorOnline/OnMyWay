package com.onmyway;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.onmyway.model.AppointmentBase;
import com.onmyway.model.GlobalData;
import com.onmyway.model.User;
import com.onmyway.responses.AppointmentsPreviewResponse;
import com.onmyway.responses.UserResponse;
import com.onmyway.utils.ActivityHelper;
import com.onmyway.utils.ApiCallback;
import com.onmyway.utils.ServiceGateway;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    //view to swap for progress ui
    private View normalView;
    private View progressView;

    //to remember working tasks
    private boolean loginTask = false;
    private boolean getAppointmentsTask = false;

    //adapter
    private AppointmentsAdapter appointmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityHelper.changeActionBarColor(this);

        setContentView(R.layout.activity_main);

        progressView = findViewById(R.id.progress);
        normalView = findViewById(R.id.appointmentsList);

        autoLogin(ActivityHelper.getCurrentPhoneNumber(this));

        ListView lv = (ListView) findViewById(R.id.appointmentsList);
        appointmentsAdapter = new AppointmentsAdapter(this, GlobalData.getAppointments());
        lv.setAdapter(appointmentsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent inte = new Intent(getApplicationContext(), MapActivity.class);
                inte.putExtra("appointmentId", appointmentsAdapter.appointments.get(i).getId());
                startActivity(inte);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent i = new Intent(getApplicationContext(), NewAppointmentActivity.class);
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    private void autoLogin(String phoneNumber) {

        if (GlobalData.getLoggedUser() == null) {
            if (!loginTask) {
                loginTask = true;
                showProgress(true);

                ServiceGateway.LoginAsync(phoneNumber, new ApiCallback<UserResponse>() {
                    @Override
                    public void OnComplete(UserResponse result) {
                        loginTask = false;
                        showProgress(false);

                        if (result != null) {
                            GlobalData.setLoggedUser(result.Data);
                            downloadAppointments();
                        }
                    }
                });
            }
        } else {
            downloadAppointments();
        }
    }

    private void downloadAppointments() {
        User user = GlobalData.getLoggedUser();
        if (user != null && !getAppointmentsTask) {
            getAppointmentsTask = true;
            showProgress(true);

            ServiceGateway.GetAppointmentsPreviewAsync(user.getPhoneNumber(), new ApiCallback<AppointmentsPreviewResponse>() {
                @Override
                public void OnComplete(AppointmentsPreviewResponse result) {
                    getAppointmentsTask = false;
                    showProgress(false);

                    if (result != null) {
                        for(AppointmentBase appointment : result.Data)
                        {
                            //unformat dates
                            appointment.calendarsFromStrings();
                        }

                        ArrayList<AppointmentBase> oldAppointments = GlobalData.getAppointments();
                        oldAppointments.clear();
                        oldAppointments.addAll(result.Data);

                        appointmentsAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public class AppointmentsAdapter extends ArrayAdapter {
        private LayoutInflater inflater;
        private Context context;
        private ArrayList<AppointmentBase> appointments;

        public AppointmentsAdapter(Context context, ArrayList<AppointmentBase> appointments) {
            super(context, R.layout.appointment_list_item, appointments);

            this.context = context;
            this.appointments = appointments;

            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.appointment_list_item, parent, false);

            AppointmentBase a = appointments.get(position);

            ((TextView) convertView.findViewById(R.id.itemTitle)).setText(a.getTitle());

            DateTimeFormatter formatter = org.joda.time.format.DateTimeFormat.shortDateTime();
            String formattedDateTime = formatter.print(new DateTime(a.getStartDateTime()));

            ((TextView) convertView.findViewById(R.id.itemDateTime)).setText(formattedDateTime);
            ((TextView) convertView.findViewById(R.id.itemLocation)).setText(a.getLocation().getTitle());

            return convertView;
        }
    }
}
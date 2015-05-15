package com.marco.onmyway;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.marco.onmyway.model.AppointmentBase;
import com.marco.onmyway.model.GlobalData;
import com.marco.onmyway.model.User;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    private View normalView;
    private View progressView;

    private LoginTask loginTask;
    private GetAppointmentsTask getAppointmentsTask;
    private AppointmentsAdapter appointmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressView = findViewById(R.id.progress);
        normalView = findViewById(R.id.appointmentsList);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = tm.getLine1Number();
        autoLogin(phoneNumber);

        ListView lv = (ListView) findViewById(R.id.appointmentsList);
        appointmentsAdapter = new AppointmentsAdapter(this, GlobalData.getAppointments());
        lv.setAdapter(appointmentsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
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

    private void autoLogin(String phoneNumber){

        if (GlobalData.getLoggedUser() == null)
        {
            if(loginTask == null) {
                loginTask = new LoginTask(phoneNumber);
                loginTask.execute();
            }
        }
        else {
            downloadAppointments();
        }
    }

    private void downloadAppointments()
    {
        User user = GlobalData.getLoggedUser();
        if (user != null && getAppointmentsTask == null)
        {
            getAppointmentsTask = new GetAppointmentsTask(user.getId());
            getAppointmentsTask.execute();
        }
    }

    public class AppointmentsAdapter extends ArrayAdapter
    {
        private LayoutInflater inflater;
        private Context context;
        private ArrayList<AppointmentBase> appointments;

        public  AppointmentsAdapter(Context context, ArrayList<AppointmentBase> appointments){
            super(context, R.layout.appointment_list_item, appointments);

            this.context = context;
            this.appointments = appointments;

            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.appointment_list_item, parent, false);

            AppointmentBase a = appointments.get(position);

            ((TextView)convertView.findViewById(R.id.itemTitle)).setText(a.getTitle());

            ((TextView)convertView.findViewById(R.id.itemDateTime)).setText("data");
            ((TextView)convertView.findViewById(R.id.itemLocation)).setText("location");

            //((TextView)convertView.findViewById(R.id.itemDateTime)).setText(a.getDateTime().toString());
            //((TextView)convertView.findViewById(R.id.itemLocation)).setText(a.getLocation().toString());

            return convertView;
        }
    }

    public class LoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String phoneNumber;

        LoginTask(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            showProgress(true);

            User user = ServiceGateway.Login(phoneNumber);

            if (user != null) {
                GlobalData.setLoggedUser(user);
                downloadAppointments();
            }

            return user != null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            loginTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            loginTask = null;
            showProgress(false);
        }
    }

    public class GetAppointmentsTask extends AsyncTask<Void, Void, Boolean> {

        private final String userId;

        GetAppointmentsTask(String userId) {
            this.userId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            showProgress(true);

            ArrayList<AppointmentBase> freshAppointments = ServiceGateway.GetAppointmentsPreview(userId);

            if (freshAppointments != null) {
                ArrayList<AppointmentBase> oldAppointments = GlobalData.getAppointments();
                oldAppointments.clear();
                oldAppointments.addAll(freshAppointments);

                appointmentsAdapter.notifyDataSetChanged();
            }

            return freshAppointments != null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            getAppointmentsTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            getAppointmentsTask = null;
            showProgress(false);
        }
    }
}
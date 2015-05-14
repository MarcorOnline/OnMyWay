package com.marco.onmyway;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.marco.onmyway.model.AppointmentBase;
import com.marco.onmyway.model.GlobalData;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = tm.getLine1Number();

        //TODO usare phoneNumber per l'autoregistrazione dell'utente

        ListView lv = (ListView)findViewById(R.id.appointmentsList);

        //TODO: eliminare e scaricare dal server
        AppointmentBase a;
        for (int i=0; i<10; i++)
        {
            a = new AppointmentBase();
            a.setTitle("Appuntamento di prova " + i);
            GlobalData.getAppointments().add(a);
        }

        AppointmentsAdapter adapter = new AppointmentsAdapter(this, GlobalData.getAppointments());
        lv.setAdapter(adapter);
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
}

package com.onmyway;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.onmyway.model.GlobalData;
import com.onmyway.model.User;
import com.onmyway.utils.ActivityHelper;
import com.onmyway.utils.AvatarHelper;

import java.util.ArrayList;
import java.util.Calendar;


public class SettingsActivity extends ActionBarActivity
{
    private ImageView avatarView;
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = ActivityHelper.setActionBar(this);

        avatarView = (ImageView) findViewById(R.id.avatar);

        updateAvatarView();
    }

    private void updateAvatarView(){
        User user = GlobalData.getLoggedUser();
        if (user != null)
            avatarView.setImageResource(AvatarHelper.GetDrawableAvatarFromString(user.getAvatar()));
    }

    public void imageClickListener(View v) {
        try {
            new AvatarSelectFragment().show(getFragmentManager(), "avatarSelector");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class AvatarSelectFragment extends DialogFragment
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            final SettingsActivity activity = (SettingsActivity)getActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            builder.setTitle(R.string.choose_avatar).setSingleChoiceItems(new AvatarAdapter(activity), 0, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    GlobalData.getLoggedUser().setAvatar(AvatarHelper.AVATARS[which]);
                    activity.updateAvatarView();
                    dismiss();
                }
            });
            return builder.create();
        }

    }

    private static class AvatarAdapter extends ArrayAdapter
    {
        private LayoutInflater inflater;

        public AvatarAdapter(Context context) {
            super(context, R.layout.avatar_list_item, AvatarHelper.AVATARS);

            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.avatar_list_item, parent, false);

            String avatar = AvatarHelper.AVATARS[position];

            ImageView image = (ImageView) convertView.findViewById(R.id.avatar_image);
            image.setImageResource(AvatarHelper.GetDrawableAvatarFromString(avatar));
            return convertView;
        }
    }
}
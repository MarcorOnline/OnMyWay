package com.onmyway;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.onmyway.model.GlobalData;
import com.onmyway.model.User;
import com.onmyway.utils.ActivityHelper;
import com.onmyway.utils.AvatarHelper;


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

        User user = GlobalData.getLoggedUser();
        if (user != null)
            avatarView.setImageResource(AvatarHelper.GetDrawableAvatarFromString(user.getAvatar()));
    }

    public void imageClickListener(View v) {
        try {
            Toast.makeText(this.getBaseContext(), "Click", Toast.LENGTH_SHORT);
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
}

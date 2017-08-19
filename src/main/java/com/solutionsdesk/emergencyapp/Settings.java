package com.solutionsdesk.emergencyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * In this activity the user can, if necessary, choose to update personal or
 * emergency contact information. Depending which view is selected, the user makes that
 * update. There is also an option to go back to the home (main) screen.
 *
 */
public class Settings extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.updateEM_button) {
            startActivity(new Intent(this,updateEM.class));
        } else if (id == R.id.updateUser_button) {
            startActivity(new Intent(this,UpdateUser.class));
        } else if (id == R.id.home_button) {
            //startActivity(new Intent(this,MainActivity.class));
            this.finish();
        }
    }
    /**
     * This method should stop any services before killing process.
     */
    public void onDestroy(){
        super.onDestroy();
        //STOP Services
    }
    /**
     * an empty onPause method
     */
    public void onPause(){
        super.onPause();
        Log.v(null, "Setting's onPause Method !!!");
    }
    /** used upon resuming the application.
     *
     */
    public void onResume(){
        super.onResume();
        Log.v(null, "Setting's onResume Method !!!");
    }
    /**
     * Called before interrupt to save data.
     */
    protected void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        // nothing needs to be saved here since nothing is changed or being used.
    }
    /**
     * Called during onResume() to restore data.
     */
    protected void onRestoreInstanceState (Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        // Restore session score
        // nothing needs to be restored here since nothing is used previously.
    }
}


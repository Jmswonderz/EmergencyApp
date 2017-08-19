package com.solutionsdesk.emergencyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.IOException;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * In this activity the user makes various selections about their location,
 * blood type, name, and number. The output (text message) will vary depending
 * on the scenario.
 */
public class BloodBank extends Activity implements OnClickListener {
    public static EditText guest_name, guest_number, gps_field;
    public static CheckBox username, usersblood, usersnumber, gps_checkbox;

    // Keys to go with each field upon onpause and onresume.
    public static final String GUESTNAME = "guestname";
    public static final String GUESTBLOOD = "guestblood";
    public static final String GUESTNUMBER = "guestnumber";
    public static final String GPSFIELD = "gpsfield";

    // CharSequences to re populate the EditText fields.
    public static CharSequence gname, gblood, gnumber, gpfield;

    // Declaring blood type spinner
    public static Spinner spinner;

    // This helps in determinig the user's location and
    // helping store user's location as an address in a list.
    LocationManager lm;
    LocationListener locationListener;
    Location loc;
    Geocoder gc;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blood_bank);

        guest_name = (EditText) findViewById(R.id.guest_name_field);

        // guest_blood = (EditText)findViewById(R.id.guest_blood_type_field);
        guest_number = (EditText) findViewById(R.id.guest_phone_number_field);
        guest_number.setInputType(InputType.TYPE_CLASS_PHONE);
        gps_field = (EditText) findViewById(R.id.location_field);

        username = (CheckBox) findViewById(R.id.name_checkBox);
        usersblood = (CheckBox) findViewById(R.id.blood_type_checkBox);
        usersnumber = (CheckBox) findViewById(R.id.phone_number_checkBox);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        // loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        gc = new Geocoder(this);
        // Log.v("Shravan", "guest_name");
        // populate spinner
        spinner = (Spinner) findViewById(R.id.blood_type_spinner);
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                try {
                    addresses = gc.getFromLocation(loc.getLatitude(),
                            loc.getLongitude(), 1);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                    Log.d("ERROR", "UNABLE TO GET LOCATION");
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Context context = getBaseContext();
            String title = "Warning!";
            String message = "Provider: " + provider + " disabled";
            String button1String = "Ok";
            AlertDialog.Builder ad = new AlertDialog.Builder(context);
            ad.setTitle(title);
            ad.setMessage(message);
            ad.setPositiveButton(button1String,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            // ok! does nothing.
                            dialog.cancel();
                        }
                    });
            ad.show();
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    /**
     * This function will check the information is inputed (checkbox or
     * textfield) and then insert it into SMS message) create the following
     * message Hi, I am _____ I got into an accident and am in need of blood. I
     * have a blood type of _____ and am in City Name. If you are in the city
     * and have this blood type please contact me at Phone number.
     */
    public void preview() {
        // this boolean is used to determine if the message is completed
        // correctly.
        Boolean msg = false;
        StringBuilder message = new StringBuilder();

        message.append("Hi, this is ");
        if (username.isChecked()) {
            message.append(Preferences.readString(this, Preferences.NAME));
        } else {
            // check to see if the guest text field has text and append to
            // message. spit error if not.
            if (guest_name.length() != 0) {
                message.append(guest_name.getText().toString());
            } else {
                msg = true;
            }

        }
        message.append(". I got into an accident and am in need of blood. I have a blood type of ");
        LinearLayout lay = (LinearLayout) findViewById(R.id.blood_layout);
        if (usersblood.isChecked()) {
            message.append(spinner.getItemAtPosition(Integer
                    .parseInt(Preferences.readString(this,
                            Preferences.BLOOD_TYPE))));
            lay.setBackgroundColor(Color.LTGRAY);
        } else {
            // check to see if the guest text field has text and append to
            // message. spit error if not.
            if (username.isChecked()) {
                lay.setBackgroundColor(Color.LTGRAY);
                usersblood.setBackgroundColor(Color.RED);
                msg = true;
            } else {
                if (spinner.getSelectedItemPosition() != 0) {
                    message.append(spinner.getSelectedItem().toString());
                    lay.setBackgroundColor(Color.LTGRAY);
                } else {

                    lay.setBackgroundColor(Color.RED);
                    msg = true;
                }
            }

        }
        message.append(" and am in ");

        // check to see if the location text field has text and append to
        // message. spit error if not.
        if (gps_field.length() != 0) {
            gps_field.setBackgroundColor(Color.WHITE);
            message.append(gps_field.getText().toString());
            // msg=true;
        } else {
            Log.v("Shravan", "Red");
            gps_field.setBackgroundColor(Color.RED);
            msg = true;
        }
        message.append(". If you are near this area and have this blood type, please contact me at: ");
        if (usersnumber.isChecked()) {
            message.append(Preferences.readString(this,
                    Preferences.PHONE_NUMBER));
        } else {
            // check to see if the guest text field has text and append to
            // message. spit error if not.
            if (guest_number.length() != 0) {
                message.append(guest_number.getText().toString());
            } else {
                msg = true;
            }
        }
        message.append(".\nThank you!");
        // SOME HOW WE NEED TO SEND THIS MESSAGE TO THE NEXT ACTIVITY.
        if (msg == false) {
            Preferences.writeString(this, Preferences.MESSAGE,
                    message.toString());
            startActivity(new Intent(this, PreviewSMS.class));
            // Toast.makeText(this,"Successfully stored message",
            // Toast.LENGTH_SHORT).show();
        } else {
            // Toast.makeText(this,"Invalid message format. need to have a value for all fields/checkboxes!",
            // Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.previewSMS_button) {
            preview();
        } else if (id == R.id.home_button) {
            this.finish();
        }
    }

    /**
     * used upon resuming the application.
     *
     */
    public void onResume() {
        super.onResume();
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        Log.v(null, "Bloodbank's onResume Method !!!");
    }

    /**
     * an empty onPause method
     */
    public void onPause() {
        super.onPause();
        lm.removeUpdates(locationListener);
        Log.v(null, "Bloodbank's onPause Method !!!");
    }

    /**
     * Called before interrupt to save data.
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // saves the guests information to to be displayed again on resume. DOES
        // NOT SAVE IF CHECKBOXES ARE CHECKED!!(yet)
        if (guest_name.length() != 0) {
            outState.putString(GUESTNAME, guest_name.getText().toString());
        }
        if (spinner.getSelectedItem() != null) {
            outState.putString(GUESTBLOOD, spinner.getSelectedItem().toString());
        }
        if (guest_number.length() != 0) {
            outState.putString(GUESTNUMBER, guest_number.getText().toString());
        }
        if (gps_field.length() != 0) {
            outState.putString(GPSFIELD, gps_field.getText().toString());
        }
    }

    /**
     * Called during onResume() to restore data.
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore session score
        // scorePlayerOne = savedInstanceState.getInt(SCOREPLAYERONEKEY);

        if (savedInstanceState.getString(GUESTNAME) != null) {
            gname = savedInstanceState.getString(GUESTNAME).subSequence(0,
                    savedInstanceState.getString(GUESTNAME).length());
        }

        if (savedInstanceState.getString(GUESTBLOOD) != null) {
            gblood = savedInstanceState.getString(GUESTBLOOD).subSequence(0,
                    savedInstanceState.getString(GUESTBLOOD).length());
        }

        if (savedInstanceState.getString(GUESTNUMBER) != null) {
            gnumber = savedInstanceState.getString(GUESTNUMBER).subSequence(0,
                    savedInstanceState.getString(GUESTNUMBER).length());
        }

        if (savedInstanceState.getString(GPSFIELD) != null) {
            gpfield = savedInstanceState.getString(GPSFIELD).subSequence(0,
                    savedInstanceState.getString(GPSFIELD).length());
        }
    }
}

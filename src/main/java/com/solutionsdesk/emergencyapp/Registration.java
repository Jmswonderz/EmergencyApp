package com.solutionsdesk.emergencyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class Registration extends Activity implements OnClickListener {

    public static EditText name, phonenumber, address,
            emergency_name, emergency_number;
    public static TextView fname, fnum, faddress, fename, fenum;
    public static Spinner spinner;

    // Pattern to validate Name, Number, Address, Blood Type;
    Pattern vname = Pattern.compile("[A-Za-z]+|[A-Za-z]+\\s[A-Za-z]+");
    Pattern vnumber = Pattern.compile("[0-9]{10}|[0-9]{7}");
    Pattern vaddr = Pattern
            .compile("[0-9]{1,4}\\s([A-Za-z]+\\s?([A-Za-z]{2,})?)\\s[A-Za-z]+,\\s[A-Za-z]{2,}");
    Pattern vblood = Pattern
            .compile("([O|o][+|-])|([A|a][+|-])|([B|b][+|-])|((AB|ab)[+|-])");
    Pattern em_name = Pattern.compile("[A-Za-z]+|[A-Za-z]+\\s[A-Za-z]+");
    Pattern em_number = Pattern.compile("[0-9]{10}|[0-9]{7}");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        name = (EditText) findViewById(R.id.home_name_field);
        phonenumber = (EditText) findViewById(R.id.home_phone_field);
        phonenumber.setInputType(InputType.TYPE_CLASS_PHONE);
        address = (EditText) findViewById(R.id.home_address_field);
//		blood_type = (EditText) findViewById(R.id.blood_type_field);
        emergency_name = (EditText) findViewById(R.id.Emergency_contacts_name);
        emergency_number = (EditText) findViewById(R.id.Emergency_contacts_number);
        emergency_number.setInputType(InputType.TYPE_CLASS_PHONE);

        // format textviews
        fname = (TextView) findViewById(R.id.format_name);
        fname.setVisibility(TextView.INVISIBLE);
        fnum = (TextView) findViewById(R.id.format_number);
        fnum.setVisibility(TextView.INVISIBLE);
        faddress = (TextView) findViewById(R.id.format_address);
        faddress.setVisibility(TextView.INVISIBLE);
        //fblood = (TextView) findViewById(R.id.format_blood);
        //fblood.setVisibility(TextView.INVISIBLE);
        fename = (TextView) findViewById(R.id.format_emergency_name);
        fename.setVisibility(TextView.INVISIBLE);
        fenum = (TextView) findViewById(R.id.format_emergency_number);
        fenum.setVisibility(TextView.INVISIBLE);

        spinner = (Spinner) findViewById(R.id.blood_type_spinner);
    }

    /**
     * This method would save all the fields to the
     *
     * @param v
     */
    public void save() {

        Preferences.writeString(this, Preferences.NAME, name.getText()
                .toString());
        Preferences.writeString(this, Preferences.PHONE_NUMBER, phonenumber
                .getText().toString());
        Preferences.writeString(this, Preferences.HOMEADDRESS, address
                .getText().toString());
        Preferences.writeString(this, Preferences.BLOOD_TYPE, Integer.toString(spinner.getSelectedItemPosition()));
        Preferences.writeString(this, Preferences.EMERGENCY_CONTACT,
                emergency_name.getText().toString());
        Preferences.writeString(this, Preferences.EM_NUMBER, emergency_number
                .getText().toString());

        // startActivity(new Intent(this,MainActivity.class));
        // this.finish();
    }

    // used to make sure the text fields are valid inputs before saving.
    public boolean validator() {
        boolean ans = false;
        boolean nm = false;
        boolean num = false;
        boolean addr = false;
        boolean bld = false;
        boolean emnum = false;
        boolean emname = false;

        if (name.getText().toString() != null) {
            Matcher mname = vname.matcher(name.getText().toString());

            if (mname.matches()) {
                nm = true;
                name.setTextColor(Color.BLACK);
                fname.setVisibility(TextView.INVISIBLE);
            } else {
                nm = false;
                Log.v("VALIDATIONS", "INVALID NAME");
                name.setTextColor(Color.RED);
                name.setHintTextColor(Color.RED);
                fname.setVisibility(TextView.VISIBLE);
                fname.setTextColor(Color.RED);
            }
        }
        if (phonenumber.getText().toString() != null) {
            Matcher mnumber = vnumber.matcher(phonenumber.getText().toString());
            if (mnumber.matches()) {
                num = true;
                phonenumber.setTextColor(Color.BLACK);
                fnum.setVisibility(TextView.INVISIBLE);
            } else {
                num = false;
                Log.v("VALIDATIONS", "INVALID NUMBER");
                phonenumber.setTextColor(Color.RED);
                phonenumber.setHintTextColor(Color.RED);
                fnum.setVisibility(TextView.VISIBLE);
                fnum.setTextColor(Color.RED);
            }
        }
        if (address.getText().toString() != null) {
            Matcher maddr = vaddr.matcher(address.getText().toString());

            if (maddr.matches()) {
                addr = true;
                address.setTextColor(Color.BLACK);
                faddress.setVisibility(TextView.INVISIBLE);
            } else {
                addr = false;
                Log.v("VALIDATIONS", "INVALID ADDRESS");
                address.setTextColor(Color.RED);
                address.setHintTextColor(Color.RED);
                faddress.setVisibility(TextView.VISIBLE);
                faddress.setTextColor(Color.RED);
            }
        }
        if (spinner.getSelectedItemPosition() != 0) {
            bld = true;
//			if (mblood.matches()) {
//				bld = true;
//				blood_type.setTextColor(Color.BLACK);
//				fblood.setVisibility(TextView.INVISIBLE);
//			} else {
//				bld = false;
//				Log.v("VALIDATIONS", "INVALID BLOOD");
//				blood_type.setTextColor(Color.RED);
//				blood_type.setHintTextColor(Color.RED);
//				fblood.setVisibility(TextView.VISIBLE);
//				fblood.setTextColor(Color.RED);
//			}
        }
        else {
            spinner.setBackgroundColor(Color.RED);
            bld = false;
        }
        if (emergency_name.getText().toString() != null) {
            Matcher memname = em_name.matcher(emergency_name.getText()
                    .toString());

            if (memname.matches()) {
                emname = true;
                emergency_name.setTextColor(Color.BLACK);
                fename.setVisibility(TextView.INVISIBLE);
            } else {
                emname = false;
                Log.v("VALIDATIONS", "INVALID EMERGENCY NAME");
                emergency_name.setTextColor(Color.RED);
                emergency_name.setHintTextColor(Color.RED);
                fename.setVisibility(TextView.VISIBLE);
                fename.setTextColor(Color.RED);
            }
        }
        if (emergency_number.getText().toString() != null) {
            Matcher memnumber = em_number.matcher(emergency_number.getText()
                    .toString());

            if (memnumber.matches()) {
                emnum = true;
                emergency_number.setTextColor(Color.BLACK);
                fenum.setVisibility(TextView.INVISIBLE);
            } else {
                emnum = false;
                Log.v("VALIDATIONS", "INVALID EMERGENCY NUMBER");
                emergency_number.setTextColor(Color.RED);
                emergency_number.setHintTextColor(Color.RED);
                fenum.setVisibility(TextView.VISIBLE);
                fenum.setTextColor(Color.RED);
            }
        }
        // if both name and number are valid, then ans = true;
        if (nm && num && addr && bld && emname && emnum) {
            ans = true;
        }
        return ans;
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.save_button) {
            if (validator()) {
                save();
                startActivity(new Intent(this, MainActivity.class));
                this.finish();
            }
        } else if (id == R.id.cancel_button) {
            this.finish();
        }
    }

    /**
     * This method should stop any services before killing process.
     */
    public void onDestroy() {
        super.onDestroy();
        // STOP Services
    }

    /**
     * used upon resuming the application.
     *
     */
    public void onResume() {
        super.onResume();
        Log.v(null, "Registration's onResume Method !!!");
    }

    /**
     * an empty onPause method
     */
    public void onPause() {
        super.onPause();
        Log.v(null, "Registration's onPause Method !!!");
    }

    /**
     * Called before interrupt to save data.
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // nothing needs to be saved here since nothing is changed or being
        // used.
        // ex. outState.putInt(SCOREPLAYERONEKEY, scorePlayerOne);
        // outState.putInt(SCOREPLAYERTWOKEY, scorePlayerTwo);
    }

    /**
     * Called during onResume() to restore data.
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore session score
        // scorePlayerOne = savedInstanceState.getInt(SCOREPLAYERONEKEY);
    }
}

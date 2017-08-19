package com.solutionsdesk.emergencyapp;

/**
 * Created by Robert on 8/13/2017.
 */

        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

        import android.app.Activity;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.text.InputType;
        import android.util.Log;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;


/**
 * In this Acitivity, the user is able to update their emergency contact's
 * first and last name, and their contact number. They are also able to cancel
 * and go back to last screen.
 *
 */
public class updateEM extends Activity implements OnClickListener {

    public static EditText name, number;
    public static String em_name, em_number;
    public static TextView fename, fenum;

    //Pattern to validate Name, Number;
    Pattern vname = Pattern.compile("[A-Za-z]+|[A-Za-z]+\\s[A-Za-z]+");
    Pattern vnumber = Pattern.compile("[0-9]{10}|[0-9]{7}");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_contact);
        name = (EditText)findViewById(R.id.name_field);
        number = (EditText)findViewById(R.id.number_field);
        number.setInputType(InputType.TYPE_CLASS_PHONE);

        em_name = Preferences.readString(this, Preferences.EMERGENCY_CONTACT);
        em_number = Preferences.readString(this, Preferences.EM_NUMBER);

        name.setText(em_name.toCharArray(), 0, em_name.length());
        number.setText(em_number.toCharArray(), 0, em_number.length());

        //format textviews
        fename = (TextView)findViewById(R.id.format_emergency_name);
        fename.setVisibility(TextView.INVISIBLE);
        fenum = (TextView)findViewById(R.id.format_emergency_number);
        fenum.setVisibility(TextView.INVISIBLE);

    }

    public void save(){
        if(name.getText().toString() != null){
            Preferences.writeString(this, Preferences.EMERGENCY_CONTACT, name.getText().toString());
        }
        if(number.getText().toString() != null){
            Preferences.writeString(this, Preferences.EM_NUMBER, number.getText().toString());
        }
        Toast.makeText(this, "successfully updated!", Toast.LENGTH_SHORT).show();
    }

    // used to make sure the text fields are valid inputs before saving.
    public boolean validator(){
        boolean ans = false;
        boolean nm = false;
        boolean num = false;
        if(name.getText().toString() != null){
            Matcher mname = vname.matcher(name.getText().toString());

            if(mname.matches()){
                name.setTextColor(Color.BLACK);
                fename.setVisibility(TextView.INVISIBLE);
                nm = true;
            } else {
                name.setTextColor(Color.RED);
                fename.setVisibility(TextView.VISIBLE);
                fename.setTextColor(Color.RED);
                nm=false;
                Log.v("VALIDATIONS", "INVALID NAME");
            }
        }
        if(number.getText().toString() != null){
            Matcher mnumber = vnumber.matcher(number.getText().toString());
            if(mnumber.matches()){
                number.setTextColor(Color.BLACK);
                fenum.setVisibility(TextView.INVISIBLE);
                num = true;
            } else {
                num = false;
                number.setTextColor(Color.RED);
                fenum.setVisibility(TextView.VISIBLE);
                fenum.setTextColor(Color.RED);
                Log.v("VALIDATIONS", "INVALID NUMBER");
            }

        }

        //if both name and number are valid, then ans = true;
        if(nm && num){
            ans = true;
        }
        return ans;
    }
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.updateEM_button) {
            if(validator()){
                save();
                this.finish();
            }
        } else if (id == R.id.cancel_button) {
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
    /** used upon resuming the application.
     *
     */
    public void onResume(){
        super.onResume();
        Log.v(null, "updateEM's onResume Method !!!");
    }
    /**
     * an empty onPause method
     */
    public void onPause(){
        super.onPause();
        Log.v(null, "updateEM's onPause Method !!!");
    }
    /**
     * Called before interrupt to save data.
     */
    protected void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        // nothing needs to be saved here since nothing is changed or being used.
        //ex. outState.putInt(SCOREPLAYERONEKEY, scorePlayerOne);
        //outState.putInt(SCOREPLAYERTWOKEY, scorePlayerTwo);
    }
    /**
     * Called during onResume() to restore data.
     */
    protected void onRestoreInstanceState (Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        // Restore session score
        // scorePlayerOne = savedInstanceState.getInt(SCOREPLAYERONEKEY);
    }
}

package com.solutionsdesk.emergencyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * This Activity dynamically creates buttons for the user to press which will
 * dial different emergency numbers in the event there isn't a unified number
 * like 911.
 *
 */

public class Sos extends Activity{

    //create an array list of buttons
    private ArrayList<Button> list = new ArrayList<Button>();
    private static TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sos);

        //adding buttons to list so that can use onclick function.
        addRow(list.size(), "Ambulance Hotline:", "102");
        addRow(list.size(), "Fire Hotline:", "101");
        addRow(list.size(), "Police Hotline:", "112");
        addRow(list.size(), "Hotline:", "911");

        //adding home button to layout
        Button home = new Button(this);
        home.setText("Home");
        int index = list.size()+ 1;
        home.setId(index);
        home.setOnClickListener(btnclick);
        home.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        table.addView(home);

    }

    /**
     *
     * @param index id to give the button in the list
     * @param textview the text to put in the textview
     * @param number the phone number to display on the button
     * @param sample the button to add the id and number to
     */
    public void addRow(int index, String textview, String number){
        table = (TableLayout)findViewById(R.id.table);
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
        TextView test = new TextView(this);
        test.setText(textview);
        test.setTextSize(18);
        test.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));

        Button sample = new Button(this);
        sample.setText(number);
        sample.setId(index);
        sample.setOnClickListener(btnclick);
        sample.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        row.addView(test);
        row.addView(sample);

        table.addView(row);
        list.add(sample);
    }
    OnClickListener btnclick = new OnClickListener(){

        @Override
        public void onClick(View v){
            //if the id clicked is in the list, then call number for that button.
            if(v.getId() < list.size()){
                try{
                    StringBuilder number = new StringBuilder();
                    number.append("tel:");
                    number.append(list.get(v.getId()).getText().toString());
                    Intent callIntent = new Intent (Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse(number.toString()));
                    startActivity(callIntent);
                }catch (ActivityNotFoundException activityException) {
                    Log.e("First Response", "Call failed");
                }
            } else{
                //the home button was clicked.
                finish();
            }
        }
    };
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
        Log.v(null, "Sos's onPause Method !!!");
    }
    /** used upon resuming the application.
     *
     */
    public void onResume(){
        super.onResume();
        Log.v(null, "Sos's onResume Method !!!");
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

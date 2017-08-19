package com.solutionsdesk.emergencyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import android.telephony.SmsManager;
/**
 *
 *
 */
public class PreviewSMS extends Activity implements OnClickListener {
    public static EditText sms_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_sms);
        sms_message = (EditText)findViewById(R.id.sms_message);
        char[] sms = Preferences.readString(this, Preferences.MESSAGE).toCharArray();
        sms_message.setText(sms,0,sms.length);
    }

    /**
     * Here a user is able to send text messages to all of their contacts.
     * The user also has the option to cancel and not send a text.
     */
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.send_button) {
            try{
                SmsManager sms = SmsManager.getDefault();
                //Toast.makeText(getBaseContext(), sms_message.getText().toString(), Toast.LENGTH_LONG).show();
                ArrayList<String> text_message = sms.divideMessage(sms_message.getText().toString());
                String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
                String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";
                Cursor cursor = null;
                Cursor phones = null;
                try
                {
                    cursor =  getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection, selection, null, null);
                    while (cursor.moveToNext())
                    {
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        //String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
                        while (phones.moveToNext())
                        {
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                            phoneNumber.replaceAll("\\([^)]*\\)", "");
                            phoneNumber.replaceAll(" ", "");
                            phoneNumber.replaceAll("-", "");
    			            	/*phoneNumber.replaceAll(")", "");
    			            	phoneNumber.replaceAll(" ", "");
    			            	phoneNumber.replaceAll("-", "");*/
                            System.out.println("phoneNumber = "+phoneNumber);
                            //Toast.makeText(getBaseContext(), phoneNumber, Toast.LENGTH_SHORT).show();
                            //sms.sendMultipartTextMessage(phonenumber, null, text_message, null, null);
                        }
                    }
                    cursor.close();
                }
                catch (NullPointerException npe)
                {
                    Log.e("TAG", "Error trying to get Contacts.");
                }
                //sms.sendMultipartTextMessage("+15136523144", null, text_message, null, null);
            } catch (Exception e) {
                e.printStackTrace();
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
        Log.v(null, "PreviewSMS's onResume Method !!!");
    }
    /**
     * an empty onPause method
     */
    public void onPause(){
        super.onPause();
        Log.v(null, "PreviewSMS's onPause Method !!!");
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


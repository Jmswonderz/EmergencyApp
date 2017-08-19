package com.solutionsdesk.emergencyapp;

/**
 * Created by Robert on 8/13/2017.
 */

        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.content.SharedPreferences.Editor;
        import android.util.Log;

public class Preferences {

    /**
     * These are static variables used in accessing/storing the data in the file.
     */
    public static final String PREF_NAME = "LOGIN_PREFERENCES";
    public static final int MODE = Context.MODE_PRIVATE;
    public static final String NAME = "username";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String  HOMEADDRESS = "home address";
    public static final String EMERGENCY_CONTACT = "emergency_contact";
    public static final String EM_NUMBER = "em_number";
    public static final String BLOOD_TYPE = "blood_type";
    public static final String MESSAGE = "message";

    public static final String MLNG = "mlng";
    public static final String MLAT = "mlat";


    //*************** GETTER METHODS *****************************************************************************

    /**
     * This is a getter method that makes it easier for the application to gain access to the same file.
     * @param context
     * @return returns a SharedPreference that stores data into the PREF_NAME private file.
     */
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    /**
     * This is a getter method that makes it easier for the application to edit the data of the same file.
     * @param context
     * @return returns an editor that is used to edit the data in the SharedPreference file.
     */
    public static Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    //*******************************************************************************************
    /**
     *  This function writes the current direction marker.
     */
    public static void writeLatLng(Context context, String key, LatLng marker){
        boolean val;
        val = getEditor(context).putFloat(key, (float) marker.latitude).commit();
        Log.v("PREFERENCE LAT SAVED", Boolean.toString(val));
        getEditor(context).putFloat(MLNG, (float) marker.longitude).commit();
    }
    /**
     * This method writes the (key, value) pair to the private file.
     * @param context
     * @param key A String used to save/access the value stored.
     * @param value A String that is the value to be stored in the file with the key string parameter as its accessor key.
     */
    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();

    }

    /**
     * This method reads from the file give the key.
     * @param context
     * @param key A String used to access the value stored.
     * @return A String that returns the value stored by the key.
     */
    public static String readString(Context context, String key){
        return getPreferences(context).getString(key, null);
    }

    /**
     *
     */
    @SuppressWarnings("null")
    public static LatLng readMarker(Context context, String key){
        //Log.v("PREFERENCES READ",Float.toString(getPreferences(context).getFloat(key, 0)));
        LatLng p = new LatLng(getPreferences(context).getFloat(MLAT, 0), getPreferences(context).getFloat(MLNG, 0));
        return p;
    }
}

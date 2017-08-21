package com.solutionsdesk.emergencyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *
 */
public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "services_db";
    private static final int DATABASE_VERSION = 1;

    public static final String NAME = "NAME";
    public static final String CONTACT = "CONTACT";
    public static final String LAT = "LAT";
    public static final String LNG = "LNG";
    public static final String TABLE_NAME = "ambulances";
    public static final String TABLE_HEADER = NAME+", "+CONTACT+", "+LAT+", "+LNG;
    private ArrayList<String> result = new ArrayList<String>();
    private SQLiteDatabase dbase;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//		dbase = this.getWritableDatabase();
//		onCreate(dbase);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        result = new ArrayList<String>();

        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + NAME + " VARCHAR(255),"
                + CONTACT + " VARCHAR(255)," + LAT + " FLOAT," + LNG + " FLOAT);");


        //db.execSQL("INSERT INTO "+TABLE_NAME+"("+taxiHeaders+") VALUES ('Car', 1, 4, 4, 3, 2, 3, 1)");
        addAmbulance("COMMUNITY EMS", "6147516651",
                Float.parseFloat("39.985801"), Float.parseFloat("-82.870903"),
                db);
        addAmbulance("CRITICAL CARE TRANSPORT", "6147750421",
                Float.parseFloat("40.00132"), Float.parseFloat("-82.926521"),
                db);
        addAmbulance("LIFE CARE MEDICAL SERVICES", "6144292000",
                Float.parseFloat("40.001057"), Float.parseFloat("-82.923775"),
                db);
        addAmbulance("MED CORP INC", "6144619999",
                Float.parseFloat("39.992904"), Float.parseFloat("-82.906265"),
                db);
        addAmbulance("ADVANCED CARE AMBULANCE SERVICE", "6142351155",
                Float.parseFloat("39.99343"), Float.parseFloat("-82.907295"),
                db);
        addAmbulance("UNITY HEALTH CARE", "6144787257",
                Float.parseFloat("40.026826"), Float.parseFloat("-82.918968"),
                db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // WRAPPER METHOD FOR ADDING A STUDENT
    public void addAmbulance(String name, String contact, float lat, float lng,
                             SQLiteDatabase db) {
        // CREATE A CONTENTVALUE OBJECT
		/*
		 * ContentValues cv = new ContentValues(); cv.put(Database.NAME, name);
		 * cv.put(Database.CONTACT, contact); cv.put(Database.LAT, lat);
		 * cv.put(Database.LNG, lng); // RETRIEVE WRITEABLE DATABASE AND INSERT
		 * long result = sqdb.insert(Database.TABLE_NAME, null, cv); return
		 * result;
		 */

        String insertQuery = "INSERT INTO " + Database.TABLE_NAME + "("
                + Database.NAME + ", " + Database.CONTACT + ", " + Database.LAT
                + ", " + Database.LNG + ") VALUES ('" + name + "', " + contact
                + ", " + lat + ", " + lng + ")";
        db.execSQL(insertQuery);
    }

    public ArrayList<String> sort(LatLng current){
        dbase = this.getWritableDatabase();
        Cursor c = dbase.rawQuery("SELECT NAME, CONTACT, LAT, LNG,((LAT -"+current.latitude+") * (LAT - "+current.latitude+") + ((LNG - "+current.longitude+") * 2) * ((LNG - "+current.longitude+") * 2)) " +
                "AS DISTANCE FROM AMBULANCES ORDER BY DISTANCE", null );

        if (c != null ) {
            if  (c.moveToFirst()) {
                do {
                    String NAME = c.getString(c.getColumnIndex("NAME"));
                    String CONTACT = c.getString(c.getColumnIndex("CONTACT"));
                    String LAT = c.getString(c.getColumnIndex("LAT"));
                    String LONG = c.getString(c.getColumnIndex("LNG"));
                    result.add("" + NAME + "," + CONTACT + ","+ LAT + ","+ LONG);
                }while (c.moveToNext());
            }
        }
        c.close();
        return result;
    }
}

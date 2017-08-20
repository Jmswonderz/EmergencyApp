package com.solutionsdesk.emergencyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * In this activity, emergency icons are drawn according to the view that was clicked
 * from MainActivity. Icons are numbered in order of increasing distance
 * from user's current location.
 *
 */
public class Maps extends FragmentActivity {
    private GoogleMap myMap;
    LocationManager lm;
    LocationListener locationListener;
    Location mLoc;
    int code;
    boolean zoomb = true;
    ArrayList<String> ambulances = new ArrayList<String>();
    boolean directioncheck = false;

    // LIST VIEW
    ListView listview;
    ArrayList<String> itemlist = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ArrayList<Marker> markerlist;

    Direction direction = new Direction();
    ArrayList<LatLng> pline = new ArrayList<LatLng>();
    LatLng currentmarker;
    Integer cid;

    String provider = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


		/*
		 * Check if any of the radios (Mobile Data or Wi-Fi) are on.
		 */
        final WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(telephonyManager.getDataState() == 0 && wifiManager.isWifiEnabled() == false && manager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
            AlertDialog.Builder ad = new AlertDialog.Builder(Maps.this);
            ad.setTitle("First-Responder");
            ad.setMessage("Your network and gps providers are off. Please enable one of them.");
            ad.setPositiveButton("Network", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder network = new AlertDialog.Builder(Maps.this);
                    network.setTitle("First-Responder");
                    network.setMessage("Please choose between Wi-Fi and Mobile Data");
                    network.setPositiveButton("Wi-Fi", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                            startActivity(intent);
                            provider = "network";
                        }
                    });
                    network.setNegativeButton("Mobile Data", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent (android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
                            startActivity(intent);
                            provider = "network";
                        }
                    });
                    network.show();
                }
            });
            ad.setNegativeButton("GPS", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    provider = "gps";
                }
            });
            ad.show();
        }

        if(telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED) {
            provider = "network";
        } else if (wifiManager.isWifiEnabled()) {
            provider = "network";
        } else if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = "gps";
        }

        listview = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,itemlist);
        listview.setAdapter(adapter);
        markerlist = new ArrayList<Marker>();

        FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment mySupportMapFragment = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.map);
        myMap = mySupportMapFragment.getMap();
        myMap.setMyLocationEnabled(true);
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        Intent intent = getIntent();
        code = intent.getIntExtra("key", 0);

        if (provider != null) {
            mLoc = lm.getLastKnownLocation(provider);
        }

        if (mLoc != null) {
            query(mLoc);
        }

        listview.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //Toast.makeText(getApplicationContext(), Integer.toString(markerlist.size()), Toast.LENGTH_LONG).show();
                LatLng point = markerlist.get(position).getPosition();
                CameraUpdate center = CameraUpdateFactory.newLatLng(point);
                myMap.animateCamera(center, 2000, null);
            }
        });
        myMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                // TODO Auto-generated method stub
                AlertDialog.Builder ad = new AlertDialog.Builder(Maps.this);
                ad.setTitle("First-Responder");
                ad.setMessage("Would you like directions?");
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int arg1) {
                        myMap.clear();
                        query(mLoc);
                        currentmarker = marker.getPosition();
                        LatLng pt = marker.getPosition();
                        direction.getmapsinfo(myMap, pt, mLoc, itemlist, adapter);
                        listview.setClickable(false);
                    }
                });
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //ok, do nothing
                        dialog.cancel();
                    }
                });
                ad.show();
            }
        });
    }

    /**
     * This method takes in a parameter loc, which is the user's current location. Then, according to the view
     * pressed from MainActivity the according case is executed. The method performs a Google nearby search and
     * returns a JSON String for parsing.
     * @param loc
     */
    public void query(Location loc) {
        switch (code) {
            case 1:
                new GetHospitals()
                        .execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                                + Double.toString(mLoc.getLatitude())
                                + ","
                                + Double.toString(mLoc.getLongitude())
                                + "&rankby=distance&types=hospital&sensor=false&key=AIzaSyAPrOxAoTKUdaXtktg4B2QrdPZO5SpM0VQ");
                break;
            case 2:
                new GetHospitals()
                        .execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                                + Double.toString(mLoc.getLatitude())
                                + ","
                                + Double.toString(mLoc.getLongitude())
                                + "&rankby=distance&types=police&sensor=false&key=AIzaSyAPrOxAoTKUdaXtktg4B2QrdPZO5SpM0VQ");
                break;
            case 3:
                new GetHospitals()
                        .execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                                + Double.toString(mLoc.getLatitude())
                                + ","
                                + Double.toString(mLoc.getLongitude())
                                + "&rankby=distance&types=fire_station&sensor=false&key=AIzaSyAPrOxAoTKUdaXtktg4B2QrdPZO5SpM0VQ");
                break;
            case 4:
                Database db = new Database(Maps.this);
                LatLng point = new LatLng(mLoc.getLatitude(), mLoc.getLongitude());
                ambulances = db.sort(point);
                placemarkers(ambulances);
                db.close();
                break;
        }
    }


    /**
     * Assigns a number according to distance the nearest ambulance.
     * @param ambulances
     */
    private void placemarkers(ArrayList<String> ambulances){
        Bitmap myWrittenBitmap;
        for(int i = 0; i < ambulances.size(); i++){
            String item = ambulances.get(i);
            Marker m;
            myWrittenBitmap = customImage(i+1, R.drawable.ambulance_launcher);

            String[] values = item.split(",");
            String name = values[0];
            String number = values[1];
            String itemlat = values[2];
            String itemlong = values[3];

            LatLng point = new LatLng(Double.parseDouble(itemlat), Double.parseDouble(itemlong));
            m = myMap.addMarker(new MarkerOptions().position(point).title(name).snippet(number).icon(BitmapDescriptorFactory.fromBitmap(myWrittenBitmap)));
            markerlist.add(m);
            if(!directioncheck){
                itemlist.add(Integer.toString(i+1)+"  "+ name);
                adapter.notifyDataSetChanged();
            }
        }
        LatLng point = new LatLng(mLoc.getLatitude(), mLoc.getLongitude());
        CameraUpdate center = CameraUpdateFactory.newLatLng(point);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
        myMap.moveCamera(center);
        myMap.animateCamera(zoom);
    }


    /**
     * This method takes in an index and a picture (represented as an int)
     * and creates a canvas on which to paint and write the number on.
     * @param index
     * @param resource
     * @return bitmap
     */
    // adding custom images to markers.
    private Bitmap customImage(int index, int resource ){
        Bitmap bm;
        Bitmap myWrittenBitmap;
        Canvas canvas;
        Paint txtPaint;

        bm = BitmapFactory.decodeResource(getResources(), resource);
        myWrittenBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_4444);
        // create a Canvas on which to draw and a Paint to write text.
        canvas = new Canvas(myWrittenBitmap);
        txtPaint = new Paint();
        txtPaint.setColor(Color.RED);
        txtPaint.setTextSize(12);
        txtPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTypeface(Typeface.DEFAULT_BOLD);
        //draw ref bitmap then text on our canvas
        canvas.drawBitmap(bm, 0, 0, null);
        canvas.drawText(Integer.toString(index), 10,10 , txtPaint);
        return myWrittenBitmap;

    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                mLoc = new Location(loc);
                //query(mLoc);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Context context = Maps.this;
            AlertDialog.Builder ad = new AlertDialog.Builder(context);
            ad.setTitle("Warning!");
            ad.setMessage("Provider: " + provider + " disabled");
            if(provider.equals("network")) {
                String button1String = "Enable network";
                ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
            }
            ad.show();
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    public String readConnectionString(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("ConnectionString", "Failed to connect");
            }
        } catch (Exception e) {
            Log.d("ConnectionString", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }

    private class GetHospitals extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return readConnectionString(urls[0]);
        }

        protected void onPostExecute(String JSONString) {
            try {
                JSONObject jsonObject = new JSONObject(JSONString);
                JSONArray EmergencyItems = new JSONArray(
                        jsonObject.getString("results"));
                for (int i = 0; i < EmergencyItems.length(); i++) {
                    JSONObject EmergencyItem = EmergencyItems.getJSONObject(i);
                    Double lat = Double.parseDouble(EmergencyItem
                            .getJSONObject("geometry")
                            .getJSONObject("location").getString("lat"));
                    Double lng = Double.parseDouble(EmergencyItem
                            .getJSONObject("geometry")
                            .getJSONObject("location").getString("lng"));
                    LatLng point = new LatLng(lat,lng);
                    Bitmap myWrittenBitmap;
                    Marker m;
                    switch (code) {
                        case 1:
                            myWrittenBitmap = customImage(i+1, R.drawable.hsp_launcher);
                            m = myMap.addMarker(new MarkerOptions().position(point).title(EmergencyItem.getString("name")).snippet(EmergencyItem.getString("vicinity")).icon(BitmapDescriptorFactory.fromBitmap(myWrittenBitmap)));
                            if(directioncheck == false){
                                itemlist.add(Integer.toString(i+1)+"  "+ EmergencyItem.getString("name"));
                                adapter.notifyDataSetChanged();
                            }
                            markerlist.add(m);
                            break;
                        case 2:
                            myWrittenBitmap = customImage(i+1, R.drawable.pol_launcher);
                            m = myMap.addMarker(new MarkerOptions().position(point).title(EmergencyItem.getString("name")).snippet(EmergencyItem.getString("vicinity")).icon(BitmapDescriptorFactory.fromBitmap(myWrittenBitmap)));
                            if(directioncheck == false){
                                itemlist.add(Integer.toString(i+1)+"  "+ EmergencyItem.getString("name"));
                                adapter.notifyDataSetChanged();
                            }
                            markerlist.add(m);
                            break;
                        case 3:
                            myWrittenBitmap = customImage(i+1, R.drawable.fire_launcher);
                            m = myMap.addMarker(new MarkerOptions().position(point).title(EmergencyItem.getString("name")).snippet(EmergencyItem.getString("vicinity")).icon(BitmapDescriptorFactory.fromBitmap(myWrittenBitmap)));
                            if(directioncheck == false){
                                itemlist.add(Integer.toString(i+1)+"  "+ EmergencyItem.getString("name"));
                                adapter.notifyDataSetChanged();
                            }
                            markerlist.add(m);
                            break;
                    }
                }
                if(zoomb) {
                    LatLng point = new LatLng(mLoc.getLatitude(), mLoc.getLongitude());
                    CameraUpdate center = CameraUpdateFactory.newLatLng(point);
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
                    myMap.moveCamera(center);
                    myMap.animateCamera(zoom);
                    zoomb=false;
                }
            } catch (Exception e) {
                Log.d("EmergencyItem", e.getLocalizedMessage());
            }
        }
    }

    /** used upon resuming the application.
     *
     */
    @Override
    public void onResume(){
        super.onResume();
//		if(provider == "network") {
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//		} else if (provider == "gps") {
//			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//		}
        Log.v(null, "Maps's onResume Method !!!");
    }
    /**
     * Android onPause method. Clears any stored location in location manager.
     */
    @Override
    public void onPause(){
        super.onPause();
        lm.removeUpdates(locationListener);
        zoomb = true;
        Log.v(null, "Maps's onPause Method !!!");
    }

    /**
     * Android onSaveInstanceState method. Here, the
     * driving instructions, latitudes and longitudes
     * are stored.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(direction.getDirections() != null){
            Log.v("!NullDirection", "!NULL");
            outState.putStringArrayList("DIRECTIONS", direction.getDirections());
            Preferences.writeLatLng(this, Preferences.MLAT, currentmarker);
        } else {
            Log.v("NullDirection", "NULL");
        }
    }

    /**
     *
     */
    @Override
    protected void onRestoreInstanceState(Bundle inputState){
        Log.v("INRESTORE", "onRestoreInstanceState");
        if(inputState != null){
            if(inputState.containsKey("DIRECTIONS")){
                itemlist.clear();
                //adapter.clear();
                adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,itemlist);
                ArrayList<String> tempitemlist = inputState.getStringArrayList("DIRECTIONS");
                Log.v("FIRST DIRECTION", Integer.toString(itemlist.size()));
                for(int i = 0; i < tempitemlist.size(); i++){
                    itemlist.add(tempitemlist.get(i));
                    adapter.notifyDataSetChanged();
                }
                directioncheck = true;
            }
            if(Preferences.readMarker(this, Preferences.MLAT) != null){
                LatLng pts = Preferences.readMarker(this, Preferences.MLAT);
                currentmarker = pts;
                direction.getmapsinfo(myMap, pts, mLoc, itemlist, adapter);
            }
        }
    }
}

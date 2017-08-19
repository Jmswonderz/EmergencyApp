package com.solutionsdesk.emergencyapp;

/**
 * Created by Robert on 8/13/2017.
 */

        import java.io.BufferedReader;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.util.ArrayList;
        import java.util.List;

        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.StatusLine;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.PolylineOptions;

        import android.graphics.Color;
        import android.location.Location;
        import android.os.AsyncTask;
        import android.util.Log;
        import android.widget.ArrayAdapter;
/**
 * In this class, two actions are being executed. One, a list of driving
 * directions is generated and displayed in a ListView and the second is
 * route drawn from a users current location to an emergency item.
 *
 */
public class Direction {

    /**
     * JString is a string that is parsed for driving instructions to an emergency item.
     * directions is the ArrayList that stores each driving instruction.
     * arraAdapter is the adapter used for displaying each driving instruction.
     * list contains all the decoded LatLng points from the JSON package.
     */
    String JString;
    ArrayList<String> directions;
    ArrayAdapter<String> arrayAdapter;
    private static List<LatLng> list;
    GoogleMap temp;

    /**
     * Constructor. Initializing list.
     */
    public Direction()
    {
        list = new ArrayList<LatLng>();
    }

    /**
     * This method takes in a location from the user and the location of the snippet that was clicked
     * and builds a search URL string. The other parameters (map, itemlist, and adapter) are for drawing
     * a route on the map, putting driving instructions in the itemlist, and displaying those instructions
     * with the adapter.
     * @param map
     * @param pts
     * @param loc
     * @param itemlist
     * @param adapter
     */
    public void getmapsinfo(GoogleMap map, LatLng pts, Location loc, ArrayList<String> itemlist, ArrayAdapter<String> adapter) {
        temp = map;
        arrayAdapter = adapter;
        directions = itemlist;
        new GetLocations()
                .execute("https://maps.googleapis.com/maps/api/directions/json?origin="
                        + Double.toString(loc.getLatitude())
                        + ","
                        + Double.toString(loc.getLongitude())
                        + "&destination="
                        + Double.toString(pts.latitude)
                        + "," + Double.toString(pts.longitude)+"&sensor=false");
    }

    /**
     * This method takes in a search URL built above and returns a JSON string which we later parse
     * for driving instructions and drawing our route.
     * @param URL
     * @return JSON String
     */
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

    /* This private class creates an asynchronous task where a route is drawn from the user's current
     * location to the selected emergency item.
     */
    private class GetLocations extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            System.out.println("doInBackground");
            return readConnectionString(urls[0]);
        }

        protected void onPostExecute(String JSONString) {
            System.out.println("onPostExecute");
            JString = JSONString;
            try {
                JSONObject jsonObject = new JSONObject(JSONString);
                JSONArray routesArray = new JSONArray(jsonObject.getString("routes"));
                JSONObject direction = routesArray.getJSONObject(0);
                JSONObject overviewPolylines = direction.getJSONObject("overview_polyline");
                String encodedPoints = overviewPolylines.getString("points");
                list.addAll(decodeCoord(encodedPoints));
                System.out.println("list size = " + list.size());
                for(int z = 0; z<list.size()-1;z++){
                    LatLng src= list.get(z);
                    LatLng dest= list.get(z+1);
                    temp.addPolyline(new PolylineOptions()
                            .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
                            .width(5)
                            .color(Color.RED).geodesic(true));
                }
                instructions();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String[] getList()
    {
        String[] temp = (String[]) list.toArray();
        return temp;
    }

    public ArrayList<String> getDirections() {
        return directions;
    }

    // Parsing the JSON for instructions on how to get to destination.
    void instructions () throws JSONException {
        System.out.println("instructions");
        JSONObject JSONobject = new JSONObject(JString);
        JSONObject routes = JSONobject.getJSONArray("routes").getJSONObject(0);
        JSONObject legs = routes.getJSONArray("legs").getJSONObject(0);
        JSONArray steps = legs.getJSONArray("steps");
        arrayAdapter.clear();
        directions.clear();
        for (int i = 0; i < steps.length(); i++) {
            JSONObject step = steps.getJSONObject(i);
            directions.add(step.getString("html_instructions").replaceAll("<(.*?)*>", " "));
            arrayAdapter.notifyDataSetChanged();
        }
        System.out.println("directions size = " + directions.size());
    }

    // Decoding the coordinates from the JSON package.
    private List<LatLng> decodeCoord(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }
        return poly;
    }
}

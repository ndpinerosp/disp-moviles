package co.edu.unal.maps_gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.os.AsyncTask;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private GoogleMap mMap;
    private LocationManager locMan;
    private SharedPreferences mPrefs;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentlat=4.653332, currentlong=-74.083652;
    SupportMapFragment supportMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        String defaultMessage = getResources().getString(R.string.radio);
        mPrefs.getString("Radio", defaultMessage);
        String[] placesTypeList = {"atm", "bank", "park"};

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getlocation();
        }
        else {
            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1222);
        }


    }

    private void urlplaces(double currentlat, double currentlong, int radio) {
        String url ="https://maps.googleapis.com/maps/api/place/nearbysearch/json"+//url
                "?location="+currentlat+","+currentlong+"&radius="+radio+
                "&types=parque"+
                "&sensor=true"+
                "&key="+ getResources().getString(R.string.google_maps_key);

        new PlaceTask().execute(url);
    }

    private void getlocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    currentlat=location.getLatitude();
                    currentlong=location.getLongitude();
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentlat,currentlong),10
                            ));

                        }
                    });
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.setting:
                startActivityForResult(new Intent(this, settings.class), 0);
                return true;

            case R.id.quit:
               // showDialog(DIALOG_QUIT_ID);
                return true;
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        LatLng Bogota = new LatLng(4.6533326, -74.083652);
        //mMap.addMarker(new MarkerOptions().position(Bogota).title("This is Colombia").snippet("Es la capital del pais"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Bogota, 15));


        mMap.getUiSettings().setZoomControlsEnabled(true);



        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);


        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1222);
        }
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Mi Ubicacion", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

        String defaultMessage = getResources().getString(R.string.radio);
        int radio=Integer.parseInt(mPrefs.getString("Radio",defaultMessage))*1000 ;
        LatLng Bogota = new LatLng(location.getLatitude(), location.getLongitude());
        CircleOptions circleOptions = new CircleOptions()
                .center(Bogota)
                .radius(radio)
                .strokeColor(Color.parseColor("#0D47A1"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));
        Circle circle = mMap.addCircle(circleOptions);
        currentlat=location.getLatitude();
        currentlong=location.getLongitude();

        urlplaces(currentlat,currentlong, radio);

    }


    private class PlaceTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            String data= null;
            try {
                 data= downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }

        private String downloadUrl(String string) throws IOException {
            URL url = new URL(string);

            HttpURLConnection connection=(HttpURLConnection) url.openConnection();

            connection.connect();

            InputStream stream= connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder builder= new StringBuilder();

            String line = "";

            while((line=reader.readLine()) != null){
                builder.append(line);
            }
            String data = builder.toString();

            reader.close();
            return data;
        }

        private class ParserTask extends  AsyncTask<String,Integer, List<HashMap<String,String>>> {
            @Override
            protected List<HashMap<String, String>> doInBackground(String... strings) {

                JsonParser jsonParser= new JsonParser();

                List<HashMap<String,String>> mapList = null;
                JSONObject object= null;
                try {
                    object= new JSONObject(strings[0]);

                    mapList= jsonParser.parseResult(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return mapList;
            }

            @Override
            protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
                mMap.clear();

                for (int i =0;i<hashMaps.size();i++){
                    HashMap<String,String> hashMapList = hashMaps.get(i);

                    double lat = Double.parseDouble(hashMapList.get("lat"));

                    double lng = Double.parseDouble(hashMapList.get("lng"));

                    String name = hashMapList.get("name");

                    LatLng latLng= new LatLng(lat,lng);
                    MarkerOptions options= new MarkerOptions();

                    options.position(latLng);
                    options.title(name);

                    mMap.addMarker(options);



                }
            }
        }
    }
}
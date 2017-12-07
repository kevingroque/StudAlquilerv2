package app.roque.com.studalquilerv2.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import app.roque.com.studalquilerv2.R;
import app.roque.com.studalquilerv2.models.Inmueble;
import app.roque.com.studalquilerv2.services.ApiService;
import app.roque.com.studalquilerv2.services.ApiServiceGenerator;
import app.roque.com.studalquilerv2.services.HttpDataHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, OnMapReadyCallback, LocationListener {


    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;

    private SharedPreferences sharedPreferences;

    private double latitud = 0;
    private double longitud = 0;
    private String direccion;

    double lat, lng;
    String distrito, depart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (status == ConnectionResult.SUCCESS) {


            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(MapsActivity.this, "Revise su conexión", Toast.LENGTH_LONG).show();
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity) getApplicationContext(), 10);
            dialog.setTitle("Sin conexión");
            dialog.show();

        }

        // init SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }


    public void iniciar() {
        ApiService service = ApiServiceGenerator.createService(ApiService.class);

        Call<List<Inmueble>> call = service.getImuebles();
        call.enqueue(new Callback<List<Inmueble>>() {
            @Override
            public void onResponse(Call<List<Inmueble>> call, Response<List<Inmueble>> response) {
                try {

                    int statusCode = response.code();
                    Log.d("CODE STATUS", "HTTP status code: " + statusCode);

                    if (response.isSuccessful()) {

                        List<Inmueble> inmuebles = response.body();
                        Log.d("Inmueble", "inmuebles: " + inmuebles);
                        for (Inmueble denuncia : inmuebles) {
                            float lat = denuncia.getLatitud();
                            float lng = denuncia.getLongitud();

                            LatLng cosmo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .title(denuncia.getDescripcion())
                                    .snippet(denuncia.getDescripcion())
                                    .position(cosmo)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(cosmo));
                            float zoon = 8;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cosmo, zoon));
                        }


                    } else {
                        Log.e("Servidor", "onError: " + response.errorBody().string());
                        throw new Exception("Error en el servicio");
                    }

                } catch (Throwable t) {
                    try {
                        Log.e("t", "onThrowable: " + t.toString(), t);
                        Toast.makeText(MapsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (Throwable x) {
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Inmueble>> call, Throwable t) {
                Log.e("OnFallo", "onFailure: " + t.toString());
                Toast.makeText(MapsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        iniciar();
        mMap = googleMap;

        if (mMap != null) {
            setUpMap();
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

        //mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(getApplicationContext())));

    }

    private void setUpMap()
    {
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        mMap.clear();

        Geocoder geocoder = new Geocoder(getApplicationContext());

        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("Escena del crimen"));

        latitud = point.latitude;
        longitud = point.longitude;

        Log.d(TAG, "LatLng: " + latitud + ", "+ longitud);
        new GetAddress().execute(String.format("%.4f,%.4f",latitud,longitud));
    }

    @Override
    public void onMapLongClick(LatLng point) {
        mMap.clear();

        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("Aqui te rechazo :'v"));

        latitud = point.latitude;
        longitud = point.longitude;

        Log.d(TAG, "LatLng: " + latitud + ", "+ longitud);
    }

    public void Save(View view){

        if(latitud == 0 && longitud == 0){
            Toast.makeText(getApplicationContext(),"Primero marca la escena del crimen.", Toast.LENGTH_LONG).show();

        } else {

            // Save to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("latitud", (float) latitud);
            editor.putFloat("longitud", (float) longitud);
            editor.putString("direccion", direccion);

            Log.d(TAG, "direccion: " + direccion);
            Log.d(TAG, "longitud: " + longitud);
            Log.d(TAG, "latitud: " + latitud);
            editor.commit();

            finish();

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        new GetAddress().execute(String.format("%.4f,%.4f",lat,lng));
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private class GetAddress extends AsyncTask<String,Void,String> {

        ProgressDialog dialog = new ProgressDialog(MapsActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                double lat = Double.parseDouble(strings[0].split(",")[0]);
                double lng = Double.parseDouble(strings[0].split(",")[1]);
                String response;
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%.4f,%.4f&sensor=false",lat,lng);
                response = http.GetHTTPData(url);
                return response;
            }
            catch (Exception ex)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);

                direccion = ((JSONArray)jsonObject.get("results")).getJSONObject(0).get("formatted_address").toString();


            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(dialog.isShowing())
                dialog.dismiss();
        }
    }

}

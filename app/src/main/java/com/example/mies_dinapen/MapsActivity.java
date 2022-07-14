package com.example.mies_dinapen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.mies_dinapen.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    ArrayList datosCoordenadas = new ArrayList();
    String datosEnv;
    //EditText etEnviar;
    Button btnEnviar;
    String [] datosCoord ;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Mapa - Ubicación");

        // GUARDA EN UN ARRAY
       /* btnEnviar = findViewById(R.id.btnGuardar);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle enviadDatos = new Bundle();
                enviadDatos.putStringArrayList("keyDatos", datosCoordenadas);

                Intent intent = new Intent(MapsActivity.this, Mies_Dinapen.class);
                intent.putExtras(enviadDatos);
                startActivity(intent);
            }
        });*/

        // GUARDA EN UN ARRAY
      /* btnEnviar = findViewById(R.id.btnGuardar);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle enviadDatos = new Bundle();

               // Mies_Dinapen mies_dinapen= new Mies_Dinapen();
               //enviadDatos.putStringArrayList("keyDatos", Mies_Dinapen.ObtenerCoordenadas);
               // mies_dinapen.ObtenerCoordenadas(datosCoordenadas);
                Intent intent = new Intent(MapsActivity.this, Mies_Dinapen.class);
               // intent.putExtras(enviadDatos);
                startActivity(intent);
            }
        });*/

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocalizacion();

    }

    private void getLocalizacion() {
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        System.out.println("UBICACION 0 " + permiso);
        if(permiso == PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        LocationManager locationManager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //SE RECUPERA LA LATITUD Y LONGITUD
                LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(miUbicacion).title("ubicacion actual"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
                mMap.getUiSettings().setZoomControlsEnabled(true);
                System.out.println("Ubicacion 1"+mMap);
                //string convertido
                datosEnv= miUbicacion.toString();
                System.out.println("PRUEBA ENVIAR "+ datosEnv);
                System.out.println("Ubicacion 2"+miUbicacion);
                //Pruebas de escritorio
                datosCoordenadas.add(miUbicacion);
                System.out.println(" DATOS DE LAS COORDENADAS" +datosCoordenadas);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(miUbicacion)
                        .zoom(14)
                        .bearing(90)
                        .tilt(45)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                System.out.println("UBICACION 3 +  " +  locationManager);

                //Enviar datos pruebas
               // btnEnviar = findViewById(R.id.btnGuardar);

            /*    btnEnviar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle enviadDatos = new Bundle();
                        enviadDatos.putString("keyDatos", datosEnv);
                        Intent intent = new Intent(MapsActivity.this, Mies_Dinapen.class);
                        intent.putExtras(enviadDatos);
                        startActivity(intent);
                    }
                });*/



            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


    }
}
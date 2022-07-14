package com.example.mies_dinapen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;

import com.example.mies_dinapen.BDSQLITE.BDHELPER;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//***tiempo ***
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class Mies_Dinapen extends AppCompatActivity implements View.OnClickListener {

    //************************ Tiempo Hora **************************************
    TextView GetDateTime;
    //************************ BASE DE DATOS *********************************
    BDHELPER DB;

    //GRABAR
    private static int MICROPHONE_PERMISSION_CODE = 200;
    MediaPlayer mediaPlayer;

    FloatingActionButton btnAudio, BtnGuardar;
    MediaRecorder grabacion; //Objeto con el cual se grabara en la aplicacion

    Button btnMap;
    public static TextView txtlatitud, txtlongitud;

    // Valores globales estaticos
    public static String latitud;
    public static String longitud;


    public static Boolean insert;
    public static String nombreImagenIncidente;


    //FOTO
    Button BtonTomarFoto, BTonSaveImagen;
    ImageView imagenFoto;
    Bitmap bitmap;


    //permisos para tomar fotos, permiso de la camara, permiso que se guarda en el movil
    private static final int REQUEST_PERMISSION_CAMERA = 100;
    private static final int TAKE_PICTURE = 101;
    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mies_dinapen);
        setTitle("INCIDENTE");


        //****************************** BASE DE DATOS **************************/////////////
        DB = new BDHELPER(this);


        //Fotos

        imagenFoto = findViewById(R.id.ImagenFoto);

        BtnGuardar = (FloatingActionButton) findViewById(R.id.GuardarBtn);

        BtnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // String imagen = new String(imagenFoto.toString());
                //    String IDLugar = +1;
                String coordenadaslatitud = latitud;
                String coordenadaslongitud = longitud;
                //   String audios =
                System.out.println("PRUEBA DE DATOS LATITUD Y LONGITUD *******************" + coordenadaslatitud + coordenadaslongitud);
                if (!TextUtils.isEmpty(coordenadaslatitud) || !TextUtils.isEmpty(coordenadaslongitud)) {
                    insert = DB.insertDataCoordenadas(coordenadaslatitud, coordenadaslongitud); // guardo coordenadas

                    //guardo BLOB de imagen
                    String imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Incidentes/Fotos/";
                    File f = new File(imageDir + nombreImagenIncidente + ".jpg");
                    try { // convierto en binario antes de persistir
                        FileInputStream fl = new FileInputStream(f);
                        byte[] arr = new byte[(int) f.length()];
                        fl.read(arr);
                        fl.close();
                        DB.insertDataFotos(arr, new Date());
                        Toast.makeText(Mies_Dinapen.this, "Foto y coordenadas guardadas", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(Mies_Dinapen.this, "Primero guarde la img", Toast.LENGTH_LONG).show();

                    }

                } else {
                    Toast.makeText(Mies_Dinapen.this, "Error en coordenadas", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //******************************* Tiempo y hora*************
        GetDateTime = findViewById(R.id.txthora);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" HH:mm:ss");
        GetDateTime.setText(simpleDateFormat.format(new Date()));


        //***************************************************************************************************************************************

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Mies_Dinapen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
            //Comprobacion para saber si el archivo manifest contiene los permisos, si no los posee, la aplicacion no funcionara
        }
        ////************************************************************************************************

        //UI
        initUI();

        BtonTomarFoto.setOnClickListener(this);
        BTonSaveImagen.setOnClickListener(this);

        //UBICACION COORDENADAS
        txtlatitud = findViewById(R.id.txtAreaLatitud);
        txtlongitud = findViewById(R.id.txtAreaLongitud);

        Localizacion lc = new Localizacion();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {

            locationStart();
        }

        //BtnMapa
        btnMap = (Button) findViewById(R.id.btnDireccion);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i1 = new Intent(Mies_Dinapen.this, com.example.mies_dinapen.MapsActivity.class);
                startActivity(i1);
            }
        });

        // Grabacion
        btnAudio = findViewById(R.id.btnAudio);
        btnAudio = (FloatingActionButton) findViewById(R.id.btnAudio);
        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(Mies_Dinapen.this, com.example.mies_dinapen.RecordActivity.class);
                startActivity(i2);
            }
        });

    }


    // CAMARA\

    private void initUI() {
        BTonSaveImagen = findViewById(R.id.botonGuardar);
        BtonTomarFoto = findViewById(R.id.BtnTomarFotos);
        imagenFoto = findViewById(R.id.ImagenFoto);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.BtnTomarFotos) {
            checkPermissionCamera();
            System.out.println("CAMARA" + imagenFoto);
        } else if (id == R.id.botonGuardar) {
            checkPermissionStorage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == TAKE_PICTURE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                imagenFoto.setImageBitmap(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
                // locationStart();


            }
        } else if (requestCode == REQUEST_PERMISSION_WRITE_STORAGE) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImagen();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkPermissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_PERMISSION_CAMERA

                );
            }
        } else {
            takePicture();
        }

    }

    private void checkPermissionStorage() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveImagen();
                } else {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION_WRITE_STORAGE
                    );
                }
            } else {
                saveImagen();
            }
        } else {
            saveImagen();
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_PICTURE);

        }
    }

    private void saveImagen() {
        OutputStream fos = null;
        File file = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues values = new ContentValues();
            //uso de escritura
            this.nombreImagenIncidente = System.currentTimeMillis() + "incidentes";

            values.put(MediaStore.Images.Media.DISPLAY_NAME, nombreImagenIncidente);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Incidentes/Fotos");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);

            Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri imageUri = resolver.insert(collection, values);

            try {
                fos = resolver.openOutputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(imageUri, values, null, null);


        } else {
            String imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

            String fileName = System.currentTimeMillis() + ".jpg";
            file = new File(imageDir, fileName);
            System.out.println("Foto muestra" + file);
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //DAR UNA MEJOR CALIDAD MAXIMA DE LAS FOTOS
        boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        if (saved) {
            Toast.makeText(this, "IMAGEN GUARDADA!", Toast.LENGTH_SHORT).show();
        }
        if (fos != null) {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        // API < 29
        if (file != null) {
            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, null);
        }

    }
    //*************************************************************************************************


    //***************************************************  UBICACION **************************************
    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMies_Dinapen(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);


    }


    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        Mies_Dinapen mies_dinapen;

        public Mies_Dinapen getMies_Dinapen() {
            return mies_dinapen;
        }

        public void setMies_Dinapen(Mies_Dinapen mies_dinapen) {
            this.mies_dinapen = mies_dinapen;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            latitud = String.valueOf(loc.getLatitude());
            longitud = String.valueOf(loc.getLongitude());

            txtlatitud.setText(latitud);
            txtlongitud.setText(longitud);

        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            txtlatitud.setText("GPS Desactivado");
            txtlongitud.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            txtlatitud.setText("GPS Activado");
            txtlongitud.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }


}
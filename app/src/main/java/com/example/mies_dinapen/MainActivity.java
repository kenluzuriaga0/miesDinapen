package com.example.mies_dinapen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //...
    Button iniciarSesion;
    private EditText et_cedula , et_contrasena;
    //...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("LOGIN");

        //Validacion logeo
        et_cedula = findViewById(R.id.et_cedula);
        et_contrasena = findViewById(R.id.et_contrasena);

        //...
        iniciarSesion=(Button)findViewById(R.id.iniciar_sesiónBTN);
        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i1 = new Intent(MainActivity.this, com.example.mies_dinapen.Mies_Dinapen.class);
                startActivity(i1);
            }
        });
        //...
    }
    public void verificar(View v) {
        String clave=et_contrasena.getText().toString();
        if (clave.length()==0) {
            Toast notificacion= Toast.makeText(this,"La clave no puede quedar vacía",Toast.LENGTH_LONG);
            notificacion.show();
        }
    }
}
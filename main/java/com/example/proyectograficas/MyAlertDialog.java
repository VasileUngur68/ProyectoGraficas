package com.example.proyectograficas;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

/**
 * Clase transparente utilizada para presentar el dialogo de la alarma y para la reproduccion del sonido
 * Solamente se vera el dialogo
 */
public class MyAlertDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //Esconde el titulo y la interfaz entera
        setContentView(R.layout.activity_my_alert_dialog);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alert);
        mediaPlayer.start();
         //Configuracion de la alerta
        AlertDialog.Builder Builder=new AlertDialog.Builder(this)
                .setMessage("!!!! Timbrando ALARMA !!!!")
                .setTitle("ALARMA Proyecto Graficas")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton("PARAR Alarma", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Parar la reproduccion del soñido
                        mediaPlayer.stop();
                        //Se cancela el PendingIntent
                        Configuracion.manager.cancel(Configuracion.pIntent);
                        //Se para el servicio del segundo plano
                        stopService(new Intent(MyAlertDialog.this, AuxAlarma.class));

                        //Se ciera esta clase
                        MyAlertDialog.this.finish();

                    }
                })
                .setPositiveButton("POSPONER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mediaPlayer.stop();
                        MyAlertDialog.this.finish();

                    }
                });
        AlertDialog alertDialog=Builder.create();
        alertDialog.show();

    }

}

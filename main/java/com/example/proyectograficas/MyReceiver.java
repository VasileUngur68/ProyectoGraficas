package com.example.proyectograficas;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


/**
 * BroadcastReceiver llamado desde la clase Configuracion
 * Aranca y para el servicio AuxAlarma
 * Si cumple el if, aranca la clase transparente MyAlertDialog
 */
public class MyReceiver extends android.content.BroadcastReceiver {

     static String campo;
     //Se recuperan las variables ruta y tipo desde la clase Configuracion, dado que son publicas
    //Se prefiere de esta forma, pero es posible recuperarlas, forzando un intent a traves de onReceive

    final String ruta=Configuracion.ru;
    final String tipo=Configuracion.tipo;


    @Override
    public void onReceive(Context context, Intent intent) {

        //La variable campo se cambia de valor desde la clase AuxAlarma, una vez realizado el
        // Intent service para arrancar la clase AuxAlarma

        try {
            if (campo!=null&&campo.equals("1")) {

                //Se llama a la clase MyAlertDialog para ejecutar la alerta
                //Ya que es imprecidible una clase que extiende a Activity para los dialogos
                Intent i=new Intent(context.getApplicationContext(),MyAlertDialog.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//pila de actividades
                context.startActivity(i);
            }

            //Se crea un nuevo intent para arancar el IntentService AuxAlarm que se ejecutara en segundo plano
            Intent service = new Intent(context,  AuxAlarma.class);
            //Se envia los campos tipo y ruta, necesarios para la conexion al servidor y
            //recuperacion del string que interesa
            service.putExtra("movim", tipo);
            service.putExtra("ruta", ruta);

            //Se arranca el servicio
            context.startService(service);

            //se espera 2,5 segundos (opcional)
            //Thread.sleep(2500);
            //Prueba de funcionalidad
            //Toast.makeText(context," CAMPO en MyReceiver "+campo , Toast.LENGTH_LONG).show();

            //Despues se para el servici ya que la peticion al servidor esta en ejecucion
            context.stopService(service);
             //InterruptedException necesario para Thread.sleep(2500)
        //} catch (InterruptedException e) {
            //Toast.makeText(context, "InterruptedException "+e.getMessage(), Toast.LENGTH_LONG).show();

            //e.printStackTrace();
        }catch (Exception e){
            Toast.makeText(context, "Exception en MyReceiver "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        //Prueba de funcionalidad
        //Toast.makeText(context,"CAMPO "+campo, Toast.LENGTH_LONG).show();

    }

}

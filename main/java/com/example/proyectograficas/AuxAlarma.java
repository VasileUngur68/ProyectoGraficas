package com.example.proyectograficas;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * IntentService que se ejecuta en segundo plano para no consumir recursos del hilo principal
 */
public class AuxAlarma extends IntentService{
    private Auxiliar r;
    static String campoA="2";


    /**
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AuxAlarma(String name) {
        super(name);
    }

    public AuxAlarma() {
        super("AuxAlarma");
    }



    @Override
    public void onCreate() {
        //Prueba de funcionalidad
        //displayExceptionMessage("Servicio creado" );
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {

        //Se reciben las variables necesarias desde la clase MyReceiver a traves del intent
        final String campoParaRecuperar = intent.getStringExtra("movim");
        final String ruta= intent.getStringExtra("ruta");

        //Se crea la cola del volley (comunicacion con el servidor)
        final RequestQueue cola = Volley.newRequestQueue(AuxAlarma.this);

        //Prueba de funcionalidad
        //displayExceptionMessage("Empieza onStartCommand CAMPO "+campoParaRecuperar );

        //Se pasa las variables a la funcion recuperarInfo()
        recuperarInfo(ruta,campoParaRecuperar,cola);
        // Se reinicia el servicio si ha sido parado por el sistema por algun problema de recursos
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent( Intent intent) { }

    @Override
    public void onDestroy() {
        //Prueba de funcionalidad
         //displayExceptionMessage("Servicio parado"  );
    }


    /**
     * @param ruta
     * @param recuperar
     * @param cola
     */
    public void recuperarInfo(final String ruta, final String recuperar,final RequestQueue cola){
        Response.Listener<String> respuesta = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try{

                    //Creo el objeto jsonRespuesta para la respuesta del servidor
                    JSONObject jsonRespuesta = new JSONObject(response);
                    String campo=jsonRespuesta.getString(recuperar);
                    //Pasar el resultado a la funcion
                    procesarResultados(campo);

                }catch (JSONException e){
                    displayExceptionMessage("JSONException " +e.getMessage() );
                    e.getMessage(); //Trato las posibles excepciones

                }
            }

        };
        //Llamo al constructor de la clase Auxiliar
        r = new Auxiliar(ruta,respuesta);
        // Utilizo Volley para la comunicacion. En este caso cola se vasea antes de añadir la nueva respuesta
        cola.cancelAll(respuesta);
        cola.add(r);

    }


    /**
     * @param campoRecuperado
     * Recibe el string recuperado del servidor y cambia el valor en la clase MyReceiver
     */
    public void procesarResultados(String campoRecuperado ){

        campoA = campoRecuperado;
        //Se cambia el valor del String campo de la clase MyReceiver con cada iteracion
        MyReceiver.campo = campoA;

        //Prueba de funcionalidad
        // displayExceptionMessage("Campo A "+campoA);

    }

    /**
     * @param msg
     * Mensages adicionales; se utiliza en el desarollo del codigo para encontrar los errores
     */
    public void displayExceptionMessage(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}

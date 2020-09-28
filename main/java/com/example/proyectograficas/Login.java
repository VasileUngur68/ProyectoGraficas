package com.example.proyectograficas;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static java.lang.Thread.sleep;

/**
 * Segunda Activity. Es arrancada por la Main
 * Comprueba los credenciales a traves de la clase Auxiliar
 * y arranca la Activity Configuracion pasandole la dir. del server
 */
public class Login extends AppCompatActivity {
    public static String server;
    public boolean conectado=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Defino los campos de la interfaz grafica
        final EditText usuarioLog = (EditText) findViewById(R.id.usuarioLogin);
        final EditText claveLog = (EditText) findViewById(R.id.claveLogin);
        final EditText dirServer = (EditText) findViewById(R.id.servidor);
        Button butonRegistro = (Button) findViewById(R.id.btnLogin);
        butonRegistro.setOnClickListener(new View.OnClickListener() {//Cuando se presiona el buton btnLogin de la interfaz de esta clase
            @Override
            public void onClick(View v) {
                //Se recupera la informacion de la interfaz
                String usu = usuarioLog.getText().toString();
                String pass = claveLog.getText().toString();
                server = dirServer.getText().toString();
                String ruta = "http://"+server+"/ProyectoGraficas/login.php";//Se configura la ruta al servidor
                String mensageToast1 ="Intentando conectar al servidor: "+server;

                //Respuesta por parte del RegistroRequest al pasar los datos
                 Response.Listener<String> respuesta = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try{

                            //Se crea el objeto jsonRespuesta para la respuesta del servidor
                            JSONObject jsonRespuesta = new JSONObject(response);


                            //Se recibe el parametro boolean succes desde el fichero php del servidor
                            boolean ok = jsonRespuesta.getBoolean("success");

                            if (ok==true){

                                displayExceptionMessage("CONEXION EXITOSA");

                                //Atraves del intent pasa la informacion a la clase Configuracion
                                Intent conf = new Intent (Login.this, Configuracion.class);
                                // Se pasa a la clase Configuracion el servidor introducido
                                conf.putExtra("server",server);
                                //Aranca la actividad Configuracion
                                Login.this.startActivity(conf);
                                Login.this.finish();//Si comentamos la linea anterior, la clase Login no se apaga

                            }else{
                                conectado=true;
                                //Se crea la alerta
                                AlertDialog.Builder alerta = new AlertDialog.Builder(Login.this);
                                alerta.setMessage("Fallo del usuaro o contraseña ")
                                        .setNegativeButton("Reintentar", null)
                                        .create()
                                        .show();

                            }
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    displayExceptionMessage("ERROR LISTENER "+error.getMessage());

                                }
                            };

                        }catch (JSONException e){
                            displayExceptionMessage("Exception JSON : "+e.getMessage());
                            e.getMessage(); //Trato las posibles excepciones

                        }
                    }

                 };
               displayExceptionMessage(mensageToast1);
               //Respuestas segun el error de conexion
                Response.ErrorListener error=  new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message ="";
                        if (error instanceof NetworkError) {
                            message = "El servidor indicado no puede ser encontrado";
                        } else if (error instanceof ServerError) {
                            message = "Actualmente hay un problema con el servidor, Por favor intente mas tarde";
                        } else if (error instanceof NoConnectionError) {
                            message = "Hay un problema con la conexion a internet. Por favor revise tu conexion a internet";
                        } else if (error instanceof TimeoutError) {
                            message = "El tiempo de espera ha sido superado.El servidor no responde.";
                        }
                        displayExceptionMessage("ERROR DE CONEXION : "+message);
                    }
                };
                //Llamo al constructor de la clase Auxiliar
                Auxiliar r = new Auxiliar(usu,pass,ruta,respuesta,error);
                // Utilizo Volley para la comunicacion
                RequestQueue cola = Volley.newRequestQueue(Login.this);
                cola.add(r);
            }

        });
    }
      //Mensages adicionales; se utiliza en el desarollo del codigo para encontrar los errores
    public void displayExceptionMessage(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

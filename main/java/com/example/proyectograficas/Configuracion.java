package com.example.proyectograficas;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;




import static com.example.proyectograficas.Login.server;

/**
 * Tercera Activity
 * Esta arancada por la Activity Login y recibe la direccion del servidor
 * Utiliza las classes DatePickerDialog y TimePickerFragment para la eleccion de la fecha y hora
 * Recoje desde el servidor los datos segun la eleccion del usuario y los pasa a las Activities corespondientes
 * Desde Menu aranca la clase MyReceiver que extiende a BroadcastReceiver para el arranque de la alarma
 */
public class Configuracion extends AppCompatActivity implements View.OnClickListener{
    public static String ru;
    static  String tipo;
    static AlarmManager manager=null;
    static PendingIntent pIntent;
    //ArrayList para guardar los CheckBox
     ArrayList<CheckBox> checkbox =new ArrayList<>();
    //contadores necesarios
    //Contador que representa el numero de sublistas que se creara: contadorIsChecked+2
    int contadorSubListas=0;
    //variables necesarias para la recuperacion de los datos enviados desde el servidor
    String parm="",resultParm="";
    //Array con los parametros en string necesarios para la recuperacion de los datos enviados desde el servidor
    private String parametros[]={"temperatura","humedad","nivelCO2","movimiento","luzExterior","luzSalon","ventanas","puertas"};
    //Array que va a almacenar la pozicion de cada Checkbox seleccionado.
    ArrayList<Integer> pozicionCheckbox =new ArrayList<>();

    Spinner cambiar;//se crea el objeto spinner
    ImageView img;//tambien el imagenview

    //Calendario para obtener fecha & hora
    Calendar calendario = Calendar.getInstance();

    // Con el sigueinte array se tomara el numero entero que corresponde a cada una de las imagenes
    // este se encuentra en gen/R.java, clase generada por el mismo proyecto
    int[] imagenes = {R.mipmap.img01, R.mipmap.img02, R.mipmap.img03};
    //Componentes de la interfaz
    private CheckBox temperatura,humedad,codos,movimiento,luzExterior,luzSalon,ventanas,puertas;
    private EditText hora,fecha;
    private Button configurar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        temperatura= (CheckBox) findViewById(R.id.temp);
        checkbox.add(temperatura);
        humedad= (CheckBox) findViewById(R.id.hum);
        checkbox.add(humedad);
        codos= (CheckBox) findViewById(R.id.codos);
        checkbox.add(codos);
        movimiento= (CheckBox) findViewById(R.id.movim);
        checkbox.add(movimiento);
        luzExterior= (CheckBox) findViewById(R.id.exterior);
        checkbox.add(luzExterior);
        luzSalon= (CheckBox) findViewById(R.id.salon);
        checkbox.add(luzSalon);
        ventanas= (CheckBox) findViewById(R.id.ventanas);
        checkbox.add(ventanas);
        puertas= (CheckBox) findViewById(R.id.puertas);
        checkbox.add(puertas);
        hora= (EditText) findViewById(R.id.hor);
        fecha= (EditText) findViewById(R.id.fech);
        configurar= (Button) findViewById(R.id.button);


        //Intent para recuperar la direccion del server desde Login
        Intent i = this.getIntent();
        final String server = i.getStringExtra("server");


        //Le añadimos un listener para la fecha
        EditText inputFecha = (EditText) findViewById(R.id.fech);
        inputFecha.setOnClickListener(this);
        //Le añadimos un listener para la hora
        EditText inputHora = (EditText) findViewById(R.id.hor);
        inputHora.setOnClickListener(this);

        //Para iniciar los objectos del spinner
        initialize();

        //Cuando se presiona el buton  de la interfaz de esta clase
        configurar.setOnClickListener(new View.OnClickListener(){
            String ruta;
            @Override
            public void onClick(View v) {
                String serve;
                if(server.isEmpty()||server== null){
                    //Se accede a la clase Chart para recuperar la ruta al servidor en el caso
                    //de que usuario vuelve a esta Activity
                  serve=Chart.servidor;
                    if(serve.isEmpty()||serve== null){
                        //Se accede a la clase BarLineChart para recuperar la ruta al servidor en el caso
                        //de que usuario vuelve a esta Activity
                        serve=BarLineChart.servidor;
                    }
                    ruta = "http://"+serve+"/ProyectoGraficas/configuracion.php";//Se configura la ruta al servidor
                }else {
                    ruta = "http://"+server+"/ProyectoGraficas/configuracion.php";//Se configura la ruta al servidor
                }
                //Se recupera la informacion de la interfaz
                String hour = hora.getText().toString();
                // Se prepara y concatena para la base de datos
                hour=hour+":00";
                String fech = fecha.getText().toString();
                String fechahora =fech+" "+hour;

                //Se crea un arrayList de doble dimension con los datos que se van a recuperar
               final ArrayList<List<String>> datosServidor = new ArrayList<List<String>>();
                //Se crea la primera sublista
                datosServidor.add(new ArrayList<String>());

                //Se crea la nueva respuesta del servidor

                 Response.Listener<String> respuesta = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {

                        try{
                        //Creamos el objeto jsonRespuesta para la respuesta del servidor
                        JSONObject jsonRespuesta = new JSONObject(response);


                            //Recibimos el parametro boolean succes desde el fichero php del servidor
                        boolean ok = jsonRespuesta.getBoolean("success");


                             if (ok==true){
                                 //Prueba de funcionalidad
                                 //displayExceptionMessage("FECHA ENCONTRADA");

                                 //El numero de filas encontradas encontradas en la BBDD;
                                 // representa el numero de datos que se almacenara en cada sublista
                                 String numDeIndicesSublistas=jsonRespuesta.getString("num");
                                 int numeroDeIndicesSublistas=Integer. parseInt(numDeIndicesSublistas);

                                 //Se añade en la primera pozicion de la primera sublista el numero de filas(numeroDeIndicesSublistas) encontradas en la BBDD
                                 //Se añade como string ya que las listas acceptan solamente strings
                                 // En esta pozicion el contadorListas=0 que representa la primera sublista
                                 datosServidor.get(contadorSubListas).add(numDeIndicesSublistas);

                                 //Se añade el nombre de la grafica seleccionada en la segunda pozicion de la primera sublista
                                 //Se recupra el nombre de la grafica elegida para presentar y se guarda en la primera sublista
                                 String grafica=cambiar.getSelectedItem().toString();
                                 datosServidor.get(contadorSubListas).add(grafica);


                                 //Se incrementa el contador de las sublistas para la creacion de las siguentes sublistas necesarias
                                 contadorSubListas++;

                                 //RECUPERAR LOS PARAMETROS QUE INTERESAN DESDE EL SERVIDOR
                                 //Se añade las fechas en la segunda lista
                                 datosServidor.add(new ArrayList<String>());

                                 for (int i=1;i<=numeroDeIndicesSublistas;i++) {
                                     parm="fecha"+i;
                                     resultParm=jsonRespuesta.getString(parm);
                                     //En esta pozicion contadorSubListas=1
                                     datosServidor.get(contadorSubListas).add(resultParm);
                                 }

                                 //Se incrementa el contador de las sublistas para la creacion de las siguentes sublistas necesarias
                                 contadorSubListas++;

                                 //Se almacena las poziciones de cada checkbox seleccionado
                                 for (int i=0;i<=7;i++){
                                     if(checkbox.get(i).isChecked()){
                                         pozicionCheckbox.add(i);
                                     }
                                 }

                                 //Se añade al ArrayList datosServidor los valores recibidos, dependiendo de las casillas seleccionadas
                                 //Se creara tantas sublistas los que se necesitan, segun las casillas seleccionadas
                                 for (int i=0;i<=7;i++){
                                     if(checkbox.get(i).isChecked()){
                                         //Se añade una nueva sublista por cada checkbox seleccionado
                                         datosServidor.add(new ArrayList<String>());

                                         //bucle que crea el parametro con el nombre necesario para recibir el dato del servidor
                                         //y lo guarda en su pozicion exacta en la sublista
                                         for (int y=1;y<=numeroDeIndicesSublistas;y++) {
                                             parm = parametros[i] + y;
                                             resultParm = jsonRespuesta.getString(parm);
                                             datosServidor.get(contadorSubListas).add(resultParm);
                                         }
                                         contadorSubListas++;
                                     }
                                 }
                                 //Prueba de funcionalidad
                                 //displayExceptionMessage("SUBLISTAS "+contadorSubListas);

                                 //Despues de añadir todos los demas datos, se añade en la tercera pozicion de la primera sublista
                                 // el numero total de sublistas creadas en el array de doble dimension datosServidor
                                 String numSublistas= Integer.toString(contadorSubListas);
                                 datosServidor.get(0).add(numSublistas);

                                 //INTERESA TENER ALMACENADOS EL NUMERO DE FILAS DEL SERVIDOR, COMO EL NUMERO DE SUBLISTAS CREADAS
                                 // PARA SABER EXTRAER LOS DATOS DEL ARRAYLIST DE DOBLE DIMENSION EN LA SIGUIENTE ACTIVITY

                                 //Atraves del intent pasa la informacion a la clase elegida segun el promt de los dibujos de graficas
                                 Intent in;
                                 //Prueba de funcionalidad
                                 //displayExceptionMessage("GRAFICA  "+datosServidor.get(0).get(1));

                                 if(datosServidor.get(0).get(1).equals("LineChart")||datosServidor.get(0).get(1).equals("BarChart")) {
                                     in = new Intent(Configuracion.this, Chart.class);
                                 }else {
                                     in = new Intent(Configuracion.this, BarLineChart.class);
                                 }
                                 //Se pasa de primero el numero de filas sleccionadas de la BBDD a travez de la fecha y hora elegida
                                 String contSbuList=Integer.toString(contadorSubListas);
                                 in.putExtra("sublistas",contSbuList);
                                 //Se pasa el array de poziciones
                                 in.putExtra("pozicion", pozicionCheckbox);
                                 //Se pasa la direccion del servidor
                                 in.putExtra("servidor", server);

                                 // Se pasa a la clase corespondiente los datos obtenidos del servidor
                                 for (int i=0;i<=contadorSubListas-1;i++){
                                     String ar="ar"+i;
                                     ArrayList pasar=new ArrayList(datosServidor.get(i));
                                     in.putExtra(ar, pasar);

                                 }

                                 //Prueba de funcionalidad
                                 //displayExceptionMessage("Primera fecha "+datosServidor.get(1).get(0)+ "Segunda Fecha "+datosServidor.get(1).get(1));
                                 //displayExceptionMessage("POZICION 1: "+pozicionCheckbox.get(0)+" y POZICION 2:"+pozicionCheckbox.get(1));

                                 Configuracion.this.startActivity(in);
                                 //Se pone el contador a 0 por si el usuario vuelve a esta Activity
                                 contadorSubListas=0;
                                 //Se vasea por si el usuario vuelve a esta Activity
                                 pozicionCheckbox.clear();
                                 //Si se descomenta esta linea, la clase Configuracion se apaga al cambiar a otra activity
                                 //Configuracion.this.finish();

                             }else{
                                 //Se crea la alerta
                                 AlertDialog.Builder alerta = new AlertDialog.Builder(Configuracion.this);
                                 alerta.setMessage("No existen datos desde esta fecha ")
                                         .setNegativeButton("Reintentar", null)
                                         .create()
                                         .show();
                             }
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    displayExceptionMessage("Error de conexion http : "+error.getMessage());

                                }
                            };

                        }catch (JSONException e){
                            displayExceptionMessage("Exception JSON : "+e.getMessage());
                            e.getMessage(); //Tratamos las posibles excepciones

                        }catch (Exception e) {
                            displayExceptionMessage("Exception  : " + e.getMessage());
                        }//Final del bloque try-catch

                    }
                };//FINAL DEL new Response.Listener<String>

                //Llamo al constructor de la clase Auxiliar
                Auxiliar r = new Auxiliar(fechahora,ruta,respuesta);
                // Utilizo Volley para la comunicacion, Crear nueva cola de peticiones
                //Usar versión no oficial de Volley desde Maven Central,obtener el .jar a través de una regla de de construcción externa en build.gradle
                RequestQueue cola = Volley.newRequestQueue(Configuracion.this);
                cola.add(r);

            }//Final del onClick
        });// Final del setOnClickListener
    } //FINAL DEL onCreate


    /*
     * El siguente metodo es usado para crear los objetos en concretos
     * asi como tambien los eventos que hara el spinner
     */
    private void initialize() {
        //busca el imagenview del activity_configuracion.xml
        img = (ImageView) findViewById(R.id.imageView);
        //busca el spinner del activity_configuracion.xml
        cambiar = (Spinner) findViewById(R.id.getIma);
        //OnItemSelectedListener() se ejecuta al hacer clic en el spinner
        cambiar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            public void onItemSelected(AdapterView arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                //por medio de arg2 se obtiene un indice del array_anime.xml
                //y de esa forma lo pasa a img, para recuperar la imagen esperada
                img.setImageResource(imagenes[arg2]);
            }

            public void onNothingSelected(AdapterView arg0) {
                // TODO Auto-generated method stub

            }

        });

    }


    /**
     * @param view
     * PARA LA ELECCION DE LA FECHA Y HORA
     */
    @Override
    public void onClick(View view) {
        //Determinamos qué elemento ha sido pulsado
        switch (view.getId()) {
            case R.id.fech:
                //Mostrar el datePicker
                showDatePickerDialog((EditText) view);
                break;
            case R.id.hor:
                // Monstrar el TimePicker
                showTimePickerFragment((EditText) view);
                break;
        }
    }

    /**
     * @param v
     * Para crear una instancia de nuestro datePicker
     * pasándole el EditText debemos usar el método estático que definimos como "newInstance"
     * en lugar de usar el constructor por defecto
     */
    public void showDatePickerDialog(EditText v) {
        DialogFragment newFragment = DatePickerDialog.newInstance(v);
        // Mostrar el datePicker
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * @param vi
     * Para crear una instancia del TimePicker
     * pasándole el EditText se debe usar el método estático que se define como "newInstance"
     * en lugar de usar el constructor por defecto
     */
    public void showTimePickerFragment(EditText vi){
        DialogFragment newFragment = TimePickerFragment.newInstance(vi);
        // Mostrar el TimePicker
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }

    /**
     * @param menu
     * @return
     * PARA LA PRESENTACION DEL MENU
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * @param item
     * @return
     * Mantiene el estado visible del elemento seleccionado en el menu
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Se establece la ruta al servidor, ya que volviendo a esta clase desde otra Activity, el server=null
        try {
            String serve,rutaAlarma;
            if(server.isEmpty()||server== null){
                //Se accede a la clase Chart para recuperar la ruta al servidor en el caso
                //de que usuario vuelve a esta Activity
                serve=Chart.servidor;
                if(serve.isEmpty()||serve== null){
                    //Se accede a la clase BarLineChart para recuperar la ruta al servidor en el caso
                    //de que usuario vuelve a esta Activity
                    serve=BarLineChart.servidor;
                }
                rutaAlarma = "http://"+serve+"/ProyectoGraficas/consulta.php";//Se configura la ruta al servidor
            }else {
                rutaAlarma = "http://"+server+"/ProyectoGraficas/consulta.php";//Se configura la ruta al servidor
            }


            ru=rutaAlarma;
            String titulo;
            switch (item.getItemId()) {
             //Se utiliza el el titulo de cada item del menu para establecer que buton ha sido seleccionado
                case R.id.Opcion2:
                    //Varias formas de cerrar la actividad
                    //System.exit(0);
                    finishAffinity();
                    //android.os.Process.killProcess(android.os.Process.myPid());
                case R.id.item_movimiendo:
                    titulo = (String) item.getTitle();
                    if (titulo.equals("Movimiento")){
                    tipo="movimiento";
                    try {
                        apagarAlarma();
                        establecerAlarma();

                    } catch (Exception e) {
                        displayExceptionMessage("  Exception MOVIMIENTO " + e.getMessage());
                        e.printStackTrace();
                    }}
                case R.id.item_exterior:
                     titulo = (String) item.getTitle();
                    if (titulo.equals("Luz Exterior")){
                        tipo="luzExterior";

                        try {
                            apagarAlarma();
                            establecerAlarma();

                        } catch (Exception e) {
                            displayExceptionMessage("  Exception luzExterior " + e.getMessage());
                            e.printStackTrace();
                        }}

                case R.id.item_salon:
                    titulo = (String) item.getTitle();
                    if (titulo.equals("Luz Salon")){
                        tipo="luzSalon";

                        try {
                            apagarAlarma();
                            establecerAlarma();

                        } catch (Exception e) {
                            displayExceptionMessage("  Exception luzSalon " + e.getMessage());
                            e.printStackTrace();
                        }}

                case R.id.item_ventanas:
                    titulo = (String) item.getTitle();
                    if (titulo.equals("Ventanas")){
                        tipo="ventanas";
                    try {
                        apagarAlarma();
                        establecerAlarma();
                    } catch (Exception e) {
                        displayExceptionMessage("  Exception ventanas " + e.getMessage());
                        e.printStackTrace();
                    }}
                case R.id.item_puertas:

                    titulo = (String) item.getTitle();
                    if (titulo.equals("Puertas")){
                        tipo="puertas";
                    try {
                        apagarAlarma();
                        establecerAlarma();

                    } catch (Exception e) {
                        displayExceptionMessage("  Exception puertas " + e.getMessage());
                        e.printStackTrace();
                    }}
                case R.id.desactivar:
                    //Mantiene el estado visible del elemento seleccionado en el menu
                    if (item.isChecked()) item.setChecked(false);
                    else item.setChecked(true);

                    titulo = (String) item.getTitle();
                    if (titulo.equals("Desactivar Alarma")){
                        try {
                            apagarAlarma();
                            displayExceptionMessage("Alarma Apagada en class Configuracion ");

                        } catch (Exception e) {
                            displayExceptionMessage("  Exception puertas " + e.getMessage());
                            e.printStackTrace();
                        }}
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }catch (Exception e){
            displayExceptionMessage("Exception onOptionsItemSelected " + e.getMessage());
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Funcion utilizada para arrancar la alarma
     * Se utiliza ALARM_SERVICE del systema
     */
    public  void establecerAlarma(){
        try {
            Toast.makeText(this, "Alarma activada", Toast.LENGTH_SHORT).show();
             manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(this, MyReceiver.class);
            intent.putExtra("movim", tipo);
            intent.putExtra("ruta", ru);
            //token que permite a MyReceiver.class usar los permisos de la aplicación para ejecutar un código predefinido
            //PendingIntent.getBroadcast inicia el BroadcastReceiver
            pIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            //Repite el intent impezando desde tiempo actual a intervalos de 60 segundos
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() ,1000 * 60*1 , pIntent);

        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Funcion que se utiliza para apagar la alarma
     */
    public  void apagarAlarma(){
        if (manager!= null) {
            manager.cancel(pIntent);
            stopService(new Intent(Configuracion.this, AuxAlarma.class));
            //Prueba de funcionalidad
            //displayExceptionMessage("Alarma Apagada en class Configuracion ");
        }else{
            stopService(new Intent(Configuracion.this, AuxAlarma.class));
            //Prueba de funcionalidad
            //displayExceptionMessage("Servicio Parado en class Configuracion ");
        }
    }


    /**
     * @param msg
     * Mensages adicionales
     */

    public void displayExceptionMessage(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}


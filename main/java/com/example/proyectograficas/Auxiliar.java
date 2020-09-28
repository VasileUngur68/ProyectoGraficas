package com.example.proyectograficas;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Esta Clase es utilizada para la comunicacion con el servidor.
 * Los constructores de esta clase son llamados desde AuxAlarma, Login y Configuracion
 */
public class Auxiliar extends StringRequest{
    String usuario="";//Se ponen a nada por si se repite el Login. De esta forma no guarda el valor antiguo
    String clave="";//Se ponen a nada por si se repite el Login. De esta forma no guarda el valor antiguo


    private Map<String, String> parametros; //Para el envio de datos

    /**
     * @param ruta
     * @param listener
     * Constroctor para el servicio AuxAlarma
     */
        public Auxiliar(String ruta, Response.Listener<String> listener) {
            super(Request.Method.POST, ruta, listener,  null);
            parametros = new HashMap<>();
        }

    /**
     * @param usuario
     * @param clave
     * @param server
     * @param listener
     * @param errorListener
     * Constructor para la clase Login
     */
        public Auxiliar( String usuario, String clave, String server, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(Request.Method.POST, server, listener, errorListener);
            this.usuario=usuario;
            this.clave=clave;
            parametros = new HashMap<>();
            parametros.put("usuario",usuario+"");
            parametros.put("clave",clave+"");
        }

    /**
     * @param fechahora
     * @param server
     * @param listener
     * Constructor para la clase Configuracion
     */
        public Auxiliar( String fechahora, String server, Response.Listener<String> listener) {
            super(Request.Method.POST, server, listener, null);
            parametros = new HashMap<>();
            parametros.put("fechahora",fechahora+"");
        }


    // Sobescribir el metodo getParams()
        @Override
        protected Map<String, String> getParams() {
            return parametros;
        }

}

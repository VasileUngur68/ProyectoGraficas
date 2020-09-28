package com.example.proyectograficas;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;

public class Chart extends AppCompatActivity {
    private LineChart linechart;
    private com.github.mikephil.charting.charts.BarChart barchart;

    public static String servidor;
    //Se crea adicional 2 arrays con los nombres todas las columnas y con los colores de cada una
    String parametros[]={"temperatura","humedad","nivelCO2","movimiento","luzExterior","luzSalon","ventanas","puertas"};
    int[] col = {Color.rgb(255, 0, 0), Color.rgb(1, 1, 223), Color.rgb(189, 189, 189)
            , Color.rgb(58, 223, 0) , Color.rgb(245, 218, 129) , Color.rgb(255, 255, 0),
            Color.rgb(137, 4, 177), Color.rgb(1, 1, 1)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        barchart = (com.github.mikephil.charting.charts.BarChart) findViewById(R.id.barchart);
        linechart = (LineChart) findViewById(R.id.linechart);

        //Intent para recuperar los datos
        Intent in = this.getIntent();
        //reprezenta el numero de datos almacenados en cada sublista
        final String numeroDeSublistas = in.getStringExtra("sublistas");
        int contadorSubListas= Integer.parseInt(numeroDeSublistas);
        try {
            //Direccion del Servidor
            servidor = in.getStringExtra("servidor");
            //Prueba de funcionalidad
            //displayExceptionMessage("SERVER RECUPERADO "+servidor);
        }catch (Exception e){
            displayExceptionMessage("Exception 0 : "+e.getMessage());
            e.getMessage(); //Tratamos las posibles excepciones
        }

        //Se recupera el array de poziciones
        ArrayList<Integer> pozicion=(ArrayList<Integer>) getIntent().getSerializableExtra("pozicion");

        //Se crea el ArrayList de doble dimension
        ArrayList<List<String>> datosS = new ArrayList<List<String>>();

        for (int i = 0; i <= contadorSubListas - 1; i++) {
            //Se crea tantas sublistas, lo que se espera desde la Activity Configuracion y se guarda cada una en el ArrayList datosS
            String ar = "ar" + i;
            ArrayList lista = (ArrayList<String>) getIntent().getSerializableExtra(ar);
            datosS.add(lista);
        }
        //Prueba de funcionalidad
        //displayExceptionMessage("Grafica elegida: "+datosS.get(0).get(1));

        if(datosS.get(0).get(1).equals("LineChart")) {
            // Evita presentar el mensage que el barchart no tiene datos
            barchart.setNoDataText("");
            //Para el LineChart
            xAxisDataLineChart(datosS, pozicion);
        }else  {
            // Evita presentar el mensage que el linechart no tiene datos
            linechart.setNoDataText("");
            //Para el BarChart
            xAxisDataBarChart(datosS, pozicion);
        }
    }

    public void xAxisDataLineChart(ArrayList<List<String>> datosS,ArrayList<Integer> pozicion ) {
        //Array String de las fechas recibidas en la funcion getXAxisValues()
        final String[] labels = getXAxisValues(datosS);
        XAxis xAxisLine = linechart.getXAxis();

        //Las lineas orizontales del grid de la grafica con las dimensiones establecidas
        xAxisLine.enableGridDashedLine(10f, 10f, 0f);
        //Dimension orizontal del grid
        xAxisLine.setAxisMaximum(10f);
        xAxisLine.setAxisMinimum(0f);
        //xAxisLine.setCenterAxisLabels(true);
        xAxisLine.setDrawLabels(true);
        xAxisLine.setDrawAxisLine(true);
        //Añade a la axisX el array String de las fechas recibidas en la funcion getXAxisValues()
        xAxisLine.setValueFormatter(new IndexAxisValueFormatter(labels));

        YAxis leftAxisLine =linechart.getAxisLeft();
        leftAxisLine.removeAllLimitLines();
        //Las lineas verticales del grid de la grafica con las dimensiones establecidas
        leftAxisLine.enableGridDashedLine(10f, 10f, 0f);
        leftAxisLine.setDrawZeroLine(false);
        leftAxisLine.setDrawLimitLinesBehindData(false);

        linechart.getAxisRight().setEnabled(false);
        setDataLineChart(datosS,pozicion);
    }

    private void setDataLineChart(ArrayList<List<String>> datosS,ArrayList<Integer> pozicion ) {
        //Se crea el Array para la axix Y
        ArrayList<ILineDataSet> yAxix = new ArrayList();
        //Se crea la leyenda LineDataSet
        LineDataSet leyenda=null;


        try{
            //Se saca el numero de sublistas (cuantos checkbox seleccionados +2 hay) para el bucle
            String numDeListas =datosS.get(0).get(2);
            int numeroDeListas=Integer.parseInt(numDeListas);

            //Se saca el numero de filas sleccionadas de la BBDD a travez de la fecha y hora elegida  para el bucle
            //Reprezenta tambien el numero de datos almacenados en cada sublista
            String numDeFilas =datosS.get(0).get(0);
            int numeroDeFilas=Integer.parseInt(numDeFilas);

            //Prueba de funcionalidad
            //displayExceptionMessage("LISTAS "+numeroDeListas+" INDICES SUBLISTAS "+numeroDeFilas);

            //Interesa empezar por la tercera sublista ya que en la primera (poz.0 del array)
            // se almacena el numero de filas y columnas de la BBDD
            //I en la segunda (poz.1 del array) las fechas
            int contador=0;
            int cont=0;
            for (int i=2;i<=numeroDeListas-1;i++){
                //El contador representa la pozicion en Axis X
                contador=0;//Por cada bucle se pone el contador a 0
                //Se crean tantos ArrayList cuatos tipos de datos seleccionados con los checkbox hay
                ArrayList datos = new ArrayList();

                //Bucle para extraer los datos de cada sublista
                for (int y=0;y<=numeroDeFilas-1;y++) {
                    //Prueba de funcionalidad
                    //displayExceptionMessage("DATOS FILA "+i+" POZICION "+y+" : "+datosS.get(i).get(y));
                    String dato =datosS.get(i).get(y);
                    int datoInteger=Integer.parseInt(dato);
                    // Se cambia el valor para una mejor presentacion
                    if(datoInteger==0){
                        datoInteger=3;
                    }
                    // Se cambia el valor para una mejor presentacion
                    if(datoInteger==1){
                        datoInteger=5;
                        //Entry va1or=new Entry(Float.valueOf(1+"ABIERTO"), contador);

                    }

                    Entry va1or = new Entry(contador, datoInteger);
                    //Se añaden los datos al ArrayList
                    datos.add(va1or);
                    contador++;
                }


                //Linea de datos
                //Se añade los datos y la descripcion del color
                leyenda = new LineDataSet(datos, parametros[pozicion.get(cont)]);
                //Color de la linea
                leyenda.setColor(col[pozicion.get(cont)]);
                //Linea de datos dibujo
                leyenda.enableDashedLine(10f, 5f, 2f);
                //Linea de datos grueso
                leyenda.setLineWidth(3f);
                //Puntos de las lineas
                leyenda.setCircleColor(Color.DKGRAY);
                leyenda.setCircleRadius(4f);
                //Los puntos estan rellenos
                leyenda.setDrawCircleHole(false);
                //Texto
                leyenda.setValueTextSize(9f);
                //Habilita el fondo de color por debajo de las lineas
                leyenda.setDrawFilled(true);
                leyenda.setFormLineWidth(1f);

                leyenda.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                leyenda.setFormSize(15.f);

                //Color de fondo en degradable soportado a partir del API level 18
                if (Utils.getSDKInt() >= 18) {
                    Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
                    leyenda.setFillDrawable(drawable);

                } else {
                    leyenda.setFillColor(Color.DKGRAY);
                }

                yAxix.add(leyenda);
                cont++;
            }
        }catch (Exception e){
            displayExceptionMessage("Exception LineData CLASS:CHART : "+e.getMessage());
            e.getMessage(); //Tratamos las posibles excepciones

        }
        LineData datosLineChart = new LineData(yAxix);
        try{
            linechart.animateXY(2000, 2000);
            //Se dibuja el mChart
            linechart.setData(datosLineChart);
            Description description = new Description();
            description.setText("Grafica LineChart; 3.0 representa CERADO y 5.0 ABIERTO ");
            linechart.setDescription(description);
            //Dimension minimo y maximo, dibuja tantas lineas verticales cuantas fechas hay disponibles
            linechart.getXAxis().setAxisMinimum(-1);
            linechart.getXAxis().setAxisMaximum(Integer.parseInt(datosS.get(0).get(0)));
            //Coloca las fechas desde la primera linea vertical del grid
            linechart.getXAxis().setLabelCount(Integer.parseInt(datosS.get(0).get(0))+2, true);
            if(Integer.parseInt(datosS.get(0).get(0))==1){
                linechart.setNoDataText("ADVERTENCIA: Esta grafica no representa correctamente los datos debido a que hay solamente una fecha;");
            }
            linechart.invalidate();// refrescar el linechart
        }catch (Exception e){
            displayExceptionMessage("Excepcion linechart CLASS:CHART : "+e.getMessage());
        }
    }



    public void xAxisDataBarChart(final ArrayList<List<String>> datosS, ArrayList<Integer> pozicion ) {

        //BLOQUE ENCARGADO DE CONFIGURAR EL XAXIS
        try{
            final String[] labels = getXAxisValues(datosS);
            //Prueba de funcionalidad
            //displayExceptionMessage("labels size "+labels.length);

            XAxis xAxisBar = barchart.getXAxis();
            //xAxisBar.setDrawGridLines(true);
            xAxisBar.setDrawAxisLine(true);
            xAxisBar.setDrawLabels(true);
            //xAxisBar.setAxisMaximum(1);
            //Coloca en el centro de los recuadros las fechas, a partir de la linea con el valor
            xAxisBar.setCenterAxisLabels(true);
            //xAxisBar.setLabelCount(Integer.parseInt(datosS.get(0).get(0)));
            xAxisBar.setPosition(XAxis.XAxisPosition.TOP);

            //Añade a la axisX el array String de las fechas recibidas en la funcion getXAxisValues()

            xAxisBar.setValueFormatter(new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return labels[(int) value % labels.length];
                }
            });

            LimitLine ll1 = new LimitLine(5f, "ABIERTO");
            ll1.setLineWidth(4f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(15f);
            ll1.setTextColor(RED);

            LimitLine ll2 = new LimitLine(2f, "CERADO");
            ll2.setLineWidth(4f);
            ll2.enableDashedLine(10f, 10f, 0f);
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll2.setTextSize(15f);
            ll2.setTextColor(BLUE);
            ll2.setLineColor(BLUE);




            YAxis leftAxis = barchart.getAxisLeft();
            leftAxis.removeAllLimitLines();
            leftAxis.addLimitLine(ll1);
            leftAxis.addLimitLine(ll2);
            leftAxis.setAxisMinimum(-0.2f);
            leftAxis.enableGridDashedLine(10f, 10f, 0f);
            leftAxis.setDrawZeroLine(false);
            leftAxis.setDrawLimitLinesBehindData(false);

            //Quita los valores presentados en yAxis de la derecha
            YAxis rightAxis = barchart.getAxisRight();
            rightAxis.removeAllLimitLines();
            rightAxis.setDrawLabels(false);

            //Llamo a ejecutar la funcion setDataBarChart()
            setDataBarChart(datosS,pozicion);
        }catch (Exception x){
            displayExceptionMessage("Exception xAxisDataBarChart CLASS:Chart: "+x.getMessage());
        }
    }

    private void setDataBarChart(ArrayList<List<String>> datosS,ArrayList<Integer> pozicion ) {
        //Se crea el Array para la axix Y
        ArrayList<IBarDataSet> yAxix = new ArrayList();
        //Se crea la leyenda BarDataSet
        BarDataSet leyenda=null;
        //ESTE BLOQUE SE ENCARGA DE EXTRAER LA INFORMACION DEL ARRAYLIST Y COLOCARLA EN EL BARDATASET (VALORES YAXIS)
        try{
            //Se saca el numero de sublistas (cuantos checkbox seleccionados +2 hay) para el bucle
            String numDeListas =datosS.get(0).get(2);
            int numeroDeListas=Integer.parseInt(numDeListas);

            //Se saca el numero de filas sleccionadas de la BBDD a travez de la fecha y hora elegida  para el bucle
            //Reprezenta tambien el numero de datos almacenados en cada sublista
            String numDeFilas =datosS.get(0).get(0);
            int numeroDeFilas=Integer.parseInt(numDeFilas);

            //Prueba de funcionalidad
            //displayExceptionMessage("LISTAS "+numeroDeListas+" INDICES SUBLISTAS (numeroDeFilas) "+numeroDeFilas);

            //Interesa empezar por la tercera sublista ya que en la primera (poz.0 del array)
            // se almacena el numero de filas y columnas de la BBDD
            //I en la segunda (poz.1 del array) las fechas
            int contador=0;
            int cont=0;
            for (int i=2;i<=numeroDeListas-1;i++) {
                //El contador representa la pozicion en Axis X
                contador = 0;//Por cada bucle se pone el contador a 0
                //Se crean tantos ArrayList cuatos tipos de datos seleccionados con los checkbox hay
                ArrayList datos = new ArrayList();
                try{
                    //Bucle para extraer los datos de cada sublista
                    for (int y = 0; y <= numeroDeFilas - 1; y++) {
                        //Prueba de funcionalidad
                        //displayExceptionMessage("DATOS FILA "+i+" POZICION "+y+" : "+datosS.get(i).get(y));
                        String dato = datosS.get(i).get(y);
                        int datoInteger = Integer.parseInt(dato);
                        // Se cambia el valor para una mejor presentacion
                        if (datoInteger == 0) {
                            datoInteger = 2;
                        }
                        // Se cambia el valor para una mejor presentacion
                        if (datoInteger == 1) {
                            datoInteger = 5;
                        }
                        BarEntry va1or = new BarEntry(contador, datoInteger);
                        //Se añaden los datos al ArrayList
                        datos.add(va1or);
                        contador++;
                    }
                }catch(Exception ex){
                    displayExceptionMessage("Exception bucle for : "+ex.getMessage());
                    ex.getMessage(); //Tratamos las posibles excepciones
                }

                //Se añade por cada linea la descripccion de la columna y el color corespondiente
                leyenda = new BarDataSet(datos, parametros[pozicion.get(cont)]);
                leyenda.setColor(col[pozicion.get(cont)]);
                yAxix.add(leyenda);
                cont++;
            }
            //Prueba de funcionalidad
            //displayExceptionMessage("CONTADOR "+contador+" CONT "+cont);
        }catch (Exception e){
            displayExceptionMessage("Exception setDataBarChart CLASS:CHART : "+e.getMessage());
            e.getMessage(); //Tratamos las posibles excepciones
        }
        //BLOQUE ENCARGADO DE CONFIGURAR EL BARCHART PARA DEPUES PASARLO AL XML
         try {
             BarData dataBarChart = new BarData(yAxix);
             float groupSpace = 0.06f;//espacio entre los grupos de baras
             float barSpace = 0.08f; // espaccio entre baras
             float barWidth = 0.48f; // Grueso de las baras
             //Configura el BarData con el grueso de las barras establecido
             dataBarChart.setBarWidth(barWidth);
             barchart.setData(dataBarChart);
             //Dibuja las columnas del grid
             barchart.getXAxis().setLabelCount(Integer.parseInt(datosS.get(0).get(0))+1, true);
             //Se controla la grafica en funcion del numero de datos seleccionados
             if(Integer.parseInt(datosS.get(0).get(2))==3){
                 barWidth = 0.25f; // Grueso de las baras
                 dataBarChart.setBarWidth(barWidth);
                 // Integer.parseInt(datosS.get(0).get(0)) es el numero de entradas
                 //Presenta el numero de fechas encontradas colocandolas en el TOP
                 barchart.getXAxis().setAxisMaximum(Integer.parseInt(datosS.get(0).get(0)));
             }else {

                 barchart.groupBars(0, groupSpace, barSpace);
                 // Integer.parseInt(datosS.get(0).get(0)) es el numero de entradas
                 //Presenta el numero de fechas encontradas colocandolas en el TOP
                 barchart.getXAxis().setAxisMaximum(0+barchart.getBarData().getGroupWidth(groupSpace, barSpace) * (Integer.parseInt(datosS.get(0).get(0))));
             }

             Description description = new Description();
             description.setText("Grafica BarChart; 2.0 representa CERADO y 5.0 ABIERTO ");
             barchart.setDescription(description);
             barchart.getDescription().setTextSize(12);
             //Marco del BarChart
             barchart.setDrawBorders(true);
             barchart.setBorderWidth(1);
             //Animacion de la presentacion
             barchart.animateXY(2000, 2000);
             barchart.getXAxis().setAxisMinimum(0);
             //para el scroolbar, presenta maximo 3 barras por fecha
             //barchart.setVisibleXRangeMaximum(3);
             barchart.invalidate();// refrescar el BarChart
         }catch (Exception bar){
             displayExceptionMessage("Exception BarData : "+bar.getMessage());
             bar.getMessage(); //Se trata las posibles excepciones
         }

    }


    //Valores del Axis X: Las Fechas del servidor
    private String[] getXAxisValues(ArrayList<List<String>> datosS) {
        String[] fechasXAxis= new String[Integer.parseInt(datosS.get(0).get(0))];
        try{
            //Numero de fechas encontradas conforme con la busqueda; reprezenta tambien el
            // numero de datos almacenados en cada sublista
            String numDeFechas =datosS.get(0).get(0);
            int numeroDeFechas=Integer.parseInt(numDeFechas);
            String fecha;

            for (int i=0;i<=numeroDeFechas-1;i++){
                //Prueba de funcionalidad
                //displayExceptionMessage("FECHAS FILA 1 : "+datosS.get(1).get(i));

                //Se añade a la Axis X las fechas quitando el año y los segundos
                fecha=datosS.get(1).get(i);
                fecha=fecha.substring(5,16);

                //Se remplaza el caracter - por / para una mejor prezentacion
                fecha=fecha.replace('-','/');
                fecha=fecha.replace(' ','-');
                fechasXAxis[i]=fecha;
                //Prueba de funcionalidad
                //displayExceptionMessage("PRESENTAR FECHA : "+i+" "+fechasXAxis[i]);
            }
        }catch (Exception e){
            displayExceptionMessage("Exception 2 : "+e.getMessage());
            e.getMessage(); //Se trata las posibles excepciones
        }
        return fechasXAxis;
    }



    //Mensages adicionales
    public void displayExceptionMessage(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }



}




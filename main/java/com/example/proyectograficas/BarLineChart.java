package com.example.proyectograficas;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

public class BarLineChart extends AppCompatActivity {
    private com.github.mikephil.charting.charts.CombinedChart barlinechart;
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
        setContentView(R.layout.activity_bar_line_chart);
        barchart = (com.github.mikephil.charting.charts.BarChart) findViewById(R.id.barchartx);
        linechart = (LineChart) findViewById(R.id.linechartx);

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
            displayExceptionMessage("Exception obtener server CLASS:BarLineChart: "+e.getMessage());
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

        //Para el BarLineChart
        xAxisDataBarChart(datosS, pozicion);
        xAxisDataLineChart(datosS, pozicion);
    }


    public void xAxisDataLineChart(ArrayList<List<String>> datosS,ArrayList<Integer> pozicion ) {
        //BLOQUE ENCARGADO DE CONFIGURAR EL XAXIS E YAXIS
        try{
            final String[] labels = getXAxisValues(datosS);
            XAxis xAxisLine = linechart.getXAxis();

            //Las lineas verticales del grid de la grafica con las dimensiones establecidas
            xAxisLine.enableGridDashedLine(10f, 10f, 0f);
            //Dimension orizontal del grid
            xAxisLine.setAxisMaximum(10f);
            xAxisLine.setAxisMinimum(0f);
            //Coloca en el centro de los recuadros las fechas, a partir de la linea con el valor
            xAxisLine.setCenterAxisLabels(true);
            xAxisLine.setDrawLabels(true);
            xAxisLine.setDrawAxisLine(true);
            //Añade a la axisX el array String de las fechas recibidas en la funcion getXAxisValues()
            xAxisLine.setValueFormatter(new IndexAxisValueFormatter(labels));

            YAxis leftAxisLine =linechart.getAxisLeft();
            leftAxisLine.removeAllLimitLines();
            //Las lineas orizontales del grid de la grafica con las dimensiones establecidas
            leftAxisLine.enableGridDashedLine(10f, 10f, 0f);
            leftAxisLine.setDrawZeroLine(false);
            leftAxisLine.setDrawLimitLinesBehindData(false);

            linechart.getAxisRight().setEnabled(false);
            generateLineData(datosS,pozicion);
        }catch (Exception x){
            displayExceptionMessage("Exception xAxisDataLineChart CLASS:BarLineChart: "+x.getMessage());
        }
    }

    public void xAxisDataBarChart(ArrayList<List<String>> datosS,ArrayList<Integer> pozicion ) {
        //BLOQUE ENCARGADO DE CONFIGURAR EL XAXIS
        try{
            final String[] labels = getXAxisValues(datosS);
            //Prueba de funcionalidad
            //displayExceptionMessage("labels size "+labels.length);

            XAxis xAxisBar = barchart.getXAxis();
            //xAxisBar.setDrawGridLines(false);
            //xAxisBar.setDrawAxisLine(false);
            //xAxisBar.setDrawLabels(false);
            //Coloca en el centro de los recuadros las fechas, a partir de la linea con el valor
            xAxisBar.setCenterAxisLabels(true);
            xAxisBar.setPosition(XAxis.XAxisPosition.TOP);
            //Añade a la axisX el array String de las fechas recibidas en la funcion getXAxisValues()
            xAxisBar.setValueFormatter(new IndexAxisValueFormatter(labels));


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
            leftAxis.setAxisMaximum(7f);
            leftAxis.setAxisMinimum(0f);
            leftAxis.enableGridDashedLine(10f, 10f, 0f);
            leftAxis.setDrawZeroLine(false);
            leftAxis.setDrawLimitLinesBehindData(false);

            //Quita los valores presentados en yAxis de la derecha
            YAxis rightAxis = barchart.getAxisRight();
            rightAxis.removeAllLimitLines();
            rightAxis.setDrawLabels(false);

            //Llamo a ejecutar la funcion setDataBarChart()
            generateBarData(datosS,pozicion);
        }catch (Exception x){
            displayExceptionMessage("Exception xAxisDataBarChart CLASS:BarLineChart: "+x.getMessage());
        }

    }

    private void generateLineData(ArrayList<List<String>> datosS, ArrayList<Integer> pozicion ) {

        //Se crea el Array para la axix Y
        ArrayList<ILineDataSet> yAxisLineData = new ArrayList();
        //Se crea la leyenda LineDataSet
        LineDataSet leyendaDataLineBarChart=null;

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
            for (int i=2;i<=numeroDeListas-1;i++) {
                //El contador representa la pozicion en Axis X
                contador = 0;//Por cada bucle se pone el contador a 0
                //Se crean tantos ArrayList cuatos tipos de datos seleccionados con los checkbox hay
                ArrayList datosDataLineBarChart = new ArrayList();

                //Bucle para extraer los datos de cada sublista
                for (int y = 0; y <=numeroDeFilas-1; y++) {
                    //Prueba de funcionalidad
                    //displayExceptionMessage("DATOS FILA "+i+" POZICION "+y+" : "+datosS.get(i).get(y));
                    String dato = datosS.get(i).get(y);
                    int datoInteger = Integer.parseInt(dato);

                    //Se controla la carga de los datos, en este apartado solamente para LineChart
                    if(datoInteger>1) {
                        Entry va1or = new Entry(contador, datoInteger);
                        //Se añaden los datos al ArrayList
                        datosDataLineBarChart.add(va1or);
                        contador++;
                    }else{
                        Entry va1or = new Entry(0, 0);
                        //Se añaden los datos al ArrayList
                        datosDataLineBarChart.add(va1or);
                    }

                }

                //Se controla la presentacion de los colores con su descripcion
                if(parametros[pozicion.get(cont)].equals("temperatura")||parametros[pozicion.get(cont)].equals("humedad")||parametros[pozicion.get(cont)].equals("nivelCO2")) {
                    //Linea de datos
                    //Se añade los datos y la descripcion del color
                    leyendaDataLineBarChart = new LineDataSet(datosDataLineBarChart, ( parametros[pozicion.get(cont)]));
                    //Color de la linea
                    leyendaDataLineBarChart.setColor(col[pozicion.get(cont)]);
                }else{
                    //Se añade los datos y la descripcion del color
                    leyendaDataLineBarChart = new LineDataSet(datosDataLineBarChart, "");
                    //Color de la linea
                    leyendaDataLineBarChart.setColor(Color.WHITE);
                }
                //Linea de datos dibujo
                leyendaDataLineBarChart.enableDashedLine(10f, 5f, 2f);
                //Linea de datos grueso
                leyendaDataLineBarChart.setLineWidth(2f);
                //Puntos de las lineas
                leyendaDataLineBarChart.setCircleColor(Color.DKGRAY);
                leyendaDataLineBarChart.setCircleRadius(5f);
                //Los puntos estan rellenos
                leyendaDataLineBarChart.setDrawCircleHole(false);
                //Tamaño de las letras de la leyenda
                leyendaDataLineBarChart.setValueTextSize(9f);

                //Habilita el fondo de color por debajo de las lineas
                //leyendaDataLineBarChart.setDrawFilled(true);

                leyendaDataLineBarChart.setFormLineWidth(2f);

                leyendaDataLineBarChart.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 2f));
                //Tamaño del recuadro de color da la leyenda
                leyendaDataLineBarChart.setFormSize(15.f);

                yAxisLineData.add(leyendaDataLineBarChart);
                cont++;
            }
        }catch (Exception e){
            displayExceptionMessage("Exception LineData CLASS:BARLINECHART : "+e.getMessage());
            e.getMessage(); //Trato las posibles excepciones
        }
        LineData datosLDataLineBarChart = new LineData(yAxisLineData);
        try{
            linechart.animateXY(4000, 4000);
            //Se dibuja el mChart
            linechart.setData(datosLDataLineBarChart);
            Description description = new Description();
            description.setText("Grafica BarLineChart;");
            linechart.setDescription(description);
            linechart.getDescription().setTextSize(12);
            //Marco del LineChart
            linechart.setDrawBorders(true);
            linechart.setBorderWidth(1);
            // PRUEBA DESPLAZAR GRAFICA
            linechart.setScaleMinima((float) datosLDataLineBarChart.getXMin() / 7f, 1f);
            linechart.moveViewTo(0, 7, YAxis.AxisDependency.LEFT);
            //Dimension minimo y maximo, dibuja tantas lineas verticales cuantas fechas hay disponibles
            linechart.getXAxis().setAxisMinimum(0);
            linechart.getXAxis().setAxisMaximum(Integer.parseInt(datosS.get(0).get(0)));
            //Coloca las fechas desde la primera linea vertical del grid
            linechart.getXAxis().setLabelCount(Integer.parseInt(datosS.get(0).get(0))+1, true);
            linechart.invalidate();// refrescar el linechart
        }catch (Exception e){
            displayExceptionMessage("Excepcion linechart CLASS:BARLINECHART : "+e.getMessage());
        }
    }

    private void generateBarData(ArrayList<List<String>> datosS, ArrayList<Integer> pozicion ) {
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
                        //Se controla la carga de los datos, en este apartado solamente para BarChart
                        if(datoInteger<6) {
                            BarEntry va1or = new BarEntry(contador, datoInteger);
                            //Se añaden los datos al ArrayList
                            datos.add(va1or);
                            contador++;
                        }
                    }
                }catch(Exception ex){
                    displayExceptionMessage("Exception bucle for : "+ex.getMessage());
                    ex.getMessage(); //Tratamos las posibles excepciones
                }

                //Se añade por cada linea la descripccion de la columna y el color corespondiente
                leyenda = new BarDataSet(datos,parametros[pozicion.get(cont)]);
                leyenda.setColor(col[pozicion.get(cont)]);
                //Tamaño de las letras de la leyenda
                leyenda.setValueTextSize(9f);
                //Quita los valores de los puntos de cada columna
                //leyenda.setDrawValues(false);
                //Color del texto de los valores de los puntos de cada columna
                leyenda.setValueTextColor(GRAY);
                yAxix.add(leyenda);
                cont++;

            }
            //Prueba de funcionalidad
            //displayExceptionMessage("CONTADOR "+contador+" CONT "+cont);
        }catch (Exception e){
            displayExceptionMessage("Exception generateBarData CLASS:BARLINECHART : "+e.getMessage());
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
                barchart.getXAxis().setAxisMaximum(barchart.getBarData().getGroupWidth(groupSpace, barSpace) * Integer.parseInt(datosS.get(0).get(0)));
            }

            Description description = new Description();
            description.setText("Grafica LineBarChart; 2:CERADO 5:ABIERTO ");
            barchart.setDescription(description);
            //Se quita la descripcion
            barchart.getDescription().setEnabled(false);
            //Dibuja las columnas del grid
            barchart.getXAxis().setLabelCount(Integer.parseInt(datosS.get(0).get(0))+1, true);
            //Marco del BarChart
            barchart.setDrawBorders(true);
            barchart.setBorderWidth(1);
            barchart.setDrawBarShadow(true);
            barchart.getXAxis().setAxisMinimum(1);
            barchart.getXAxis().setDrawGridLines(false);
            barchart.getXAxis().setDrawAxisLine(false);
            //Se quita la presentacion de las fechas
            barchart.getXAxis().setDrawLabels(false);
            //Animacion de la presentacion
            barchart.animateXY(2000, 2000);
            //para el scroolbar, presenta maximo 4 barras por fecha
            //barchart.setVisibleXRangeMaximum(4);
            barchart.invalidate();// refrescar el BarChart
        }catch (Exception bar){
            displayExceptionMessage("Exception BarData CLASS:BARLINECHART : "+bar.getMessage());
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

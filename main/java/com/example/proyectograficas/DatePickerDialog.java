package com.example.proyectograficas;

import android.app.Dialog;
import android.os.Bundle;;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



/*Para crear un selector de fechas o un selector de hora para que se escriba automáticamente la fecha/hora
formateada en un «EditText» se requiere:

1.- definir un «EditText» como no seleccionable,

2.- crear una clase Java que herede de «DialogFragment» que cree un objeto de la clase «DatePickerDialog» en el método heredado «onCreateDialog«

3.- asignar un «OnclickListener» al «EditText» para que, al ser seleccionado, aparezca un «date picker» usando un objeto de la clase Java que hereda de «DialogFragment«.

El picker se mostrará usando el método «DialogFragment::show()«, y se cerrará automáticamente cuando el usuario seleccione la fecha.

Del mismo modo ocurre con el time picker, con la diferencia de que la clase que debe heredar de «DialogFragment»
debe crear un «TimePickerDialog» en su método heredado «onCreateDialog«.


A esta clase le pasamos el EditText que ha seleccionado el usuario para meter una fecha.

Como esta clase hereda de un fragmento, no se pueden redefinir los constructores, así que, para pasarle el
EditText al instanciarlo, debemos crear un método estático que nos devuelva la instancia de este objeto y
además asignarle el atributo EditText para que lo pueda usar. En este caso la hemos llamado «newInstance«.

Como además queremos que sea el mismo objeto picker el que modifique el EditText, este objeto debe implementar
la interfaz «OnDateSetListener» para convertirse a sí mismo en un listener.

Cuando se seleccione una fecha, se lanzará el método «onDateSet» (implementado de OnDateSetListener), y es ahí donde
se va a modificar el valor del EditText que le hemos pasado por parámetro.
 */
public class DatePickerDialog extends DialogFragment
        // El propio framento es un listener así que
        // debe implementar: OnDateSetListener
        implements android.app.DatePickerDialog.OnDateSetListener {
    // EditText al que se le asocia este Picker
    private EditText editText;
    /*
    Queremos pasarle el EditText al constructor de
    la clase pero no se puede modificar el constructor
    de un fragmento, así que debemos crear un método
    estático que devuelva una instancia de esta clase,
    pasándole a este "constructor estático" el EditText
    al que está asociado y en el que queremos que se
    escriban las fechas al ser seleccionadas
    */
    public static DatePickerDialog newInstance(EditText editText) {
        DatePickerDialog fragment = new DatePickerDialog();
        fragment.setEditText(editText);
        return fragment;
    }
    // Tenemos que crear un setter ya que el
    // "constructor" es estático
    public void setEditText(EditText editText){
        this.editText = editText;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Ponemos la fecha actual para el datepicker
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        //Si ya hemos seleccionado una fecha en el picker y sigue
        //el formato que le hemos puesto nosotros (en este caso
        //dd/MM/yyyy) muestra la fecha seleccionada en el picker en
        //lugar de la fecha actual
        if(this.editText.getText().toString().length()>0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = null;
            try {
                parsedDate = formatter.parse(this.editText.getText().toString());
                c.setTime(parsedDate);
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Devolver una instancia del DatePickerDialog
        // indicando quién es el listener del picker (this)
        // y la fecha pre-seleccionada (year, month, day)
        return new android.app.DatePickerDialog(getActivity(), this, year, month, day);
    }
    //Cuando se seleccione una fecha, el
    // evento llamará a este método
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String selectedDate = String.format("%04d", year) + "-" + String.format("%02d", (month+1))  + "-" + String.format("%02d", day);
        this.editText.setText(selectedDate);
    }
}
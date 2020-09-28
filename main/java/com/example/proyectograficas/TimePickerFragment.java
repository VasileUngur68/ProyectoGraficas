package com.example.proyectograficas;

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

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;


import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;




public class TimePickerFragment extends DialogFragment
        // El propio framento es un listener así que
        // debe implementar: OnTimeSetListener
        implements TimePickerDialog.OnTimeSetListener {
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
    public static TimePickerFragment newInstance(EditText editText) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setEditText(editText);
        return fragment;
    }
    // Tenemos que crear un setter ya que el
    // "constructor" es estático
    public void setEditText(EditText editText){
        this.editText = editText;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int seconds=c.get(Calendar.SECOND);
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
    //Cuando se seleccione una hora, el
    // evento llamará a este método

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String selectedHour = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) ;
        this.editText.setText(selectedHour);
    }
}


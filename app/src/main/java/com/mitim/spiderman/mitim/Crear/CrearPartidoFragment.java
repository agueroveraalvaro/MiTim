package com.mitim.spiderman.mitim.Crear;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mitim.spiderman.mitim.Main.MainActivity;
import com.mitim.spiderman.mitim.Modelo.Partido;
import com.mitim.spiderman.mitim.R;


public class CrearPartidoFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private OnFragmentInteractionListener mListener;
    private View v;
    private EditText edtTitulo,edtPlaces,edtHora;
    private int PLACE_PICKER_REQUEST=1;
    private TimePicker timePicker;
    private Spinner spinnerJugadores;
    private Button btnCrear;
    private Boolean newTimePicker=false;

    public CrearPartidoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_crear_partido, container, false);
        cargarElementos(v);
        return v;
    }

    private void cargarElementos(View v)
    {
        edtTitulo=(EditText)v.findViewById(R.id.edtTitulo);
        edtPlaces=(EditText)v.findViewById(R.id.edtPlaces);
        edtHora=(EditText)v.findViewById(R.id.edtHora);
        timePicker=(TimePicker)v.findViewById(R.id.timePicker);//es el antiguo sin dialog
        spinnerJugadores=(Spinner)v.findViewById(R.id.spinnerJugadores);
        btnCrear=(Button)v.findViewById(R.id.btnCrear);
        spinnerJugadores.setOnItemSelectedListener(this);
        edtPlaces.setOnClickListener(this);
        edtHora.setOnClickListener(this);
        btnCrear.setOnClickListener(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        {
            newTimePicker=true;
            edtHora.setVisibility(View.VISIBLE);//para mostrar dialog
        }
        else
        {
            timePicker.setVisibility(View.VISIBLE);//es el antiguo sin dialog
        }
        cargarSpinner();
        animarBotonCrear();
    }

    private void animarBotonCrear()
    {
        btnCrear.setY(150f);
        btnCrear.animate().translationY(0f).setDuration(400);
    }

    private void cargarSpinner()
    {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.jugadores, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerJugadores.setAdapter(adapter);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.edtPlaces:
                PlacePicker.IntentBuilder builder=new PlacePicker.IntentBuilder();
                Intent intent;
                try
                {
                    intent=builder.build(getActivity());//seguir tutorial
                    startActivityForResult(intent,PLACE_PICKER_REQUEST);
                }
                catch (Exception e)
                {
                    System.out.println("EXEPCION DE GOOGLE MAPS PLAY : "+e.getMessage());
                }
                break;
            case R.id.edtHora:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                {
                    final Calendar calendar;
                    calendar = Calendar.getInstance();
                    final int hora=calendar.get(Calendar.HOUR_OF_DAY);
                    int minutos=calendar.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog=new TimePickerDialog(getContext(),new TimePickerDialog.OnTimeSetListener()
                    {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hour, int minute)
                        {
                            edtHora.setText(hour+":"+minute);
                        }
                    },hora,minutos,false);
                }
                break;
            case R.id.btnCrear:
                String titulo=edtTitulo.getText().toString();
                String lugar=edtPlaces.getText().toString();
                String hora=":";
                if (newTimePicker)
                {
                    hora=edtHora.getText().toString();
                }
                else
                {
                    if(timePicker.getCurrentMinute()<10)
                    {
                        hora=":0";
                    }
                    hora=timePicker.getCurrentHour()+hora+timePicker.getCurrentMinute();
                }
                String jugadores=spinnerJugadores.getSelectedItem().toString();
                if(titulo!="" && lugar!="" && hora!="" && jugadores!="")
                {
                    try
                    {
                        Partido partido=new Partido(titulo,lugar,hora);
                        partido.setJugadores(jugadores);
                        partido.setCreador(MainActivity.obtenerUID());//obtengo id de autor
                        DatabaseReference dbPartidos = FirebaseDatabase.getInstance().getReference().child("mitim_bd").child("Partidos");
                        dbPartidos.push().setValue(partido);//ingreso partido con id nuevo creado por fbase
                        Toast.makeText(getContext(),"Partido creado!",Toast.LENGTH_SHORT).show();

                        //se va a menu de partido...
                    }
                    catch (Exception e)
                    {
                        System.out.println("Excepcion de ingreso de partido. "+e.getMessage());
                    }
                }
                else
                {
                    Toast.makeText(getContext(),"Todos los campos deben estar completos :(",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data)//resultado de google maps place
    {
        if (requestCode==PLACE_PICKER_REQUEST)
        {
            if (resultCode==MainActivity.RESULT_OK)//si resulta bien, selecciona el gooogle place
            {
                Place place=PlacePicker.getPlace(getActivity(),data);
                edtPlaces.setText(place.getAddress());
            }
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        ((TextView)parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorLetras));
        //System.out.println("Selecciono : " + parent.getSelectedItem().toString() + " Posicion : " + position);
        //Toast.makeText(getContext(), "Ahora es un " + parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //de item selected del spineer
    }
}

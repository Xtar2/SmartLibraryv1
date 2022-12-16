package es.upv.a3c.smartlibrary;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;



class Plazas extends Fragment {

    CalendarView calendarView; //Lola
    TextView Fecha; //Lola
    private long msFechaSelect; //Lola
    private long msHoraSelect; //Lola
    private int idBotonSelect; //Lola
    View view;
    private String idCabinaSeleccionada;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cabinas, container, false);

        Inicializar_Interfaz ();

        //Calendario
        calendarView = view.findViewById(R.id.calendarView2);
        Fecha = (TextView) view.findViewById(R.id.textViewFecha1);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                                                 @Override
                                                 public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                                                     String fechaHoy = dayOfMonth + "/" + (month + 1) + "/" + year;
                                                     Log.d("Fecha", fechaHoy);
                                                     Fecha.setText(fechaHoy);

                                                     //Pasar a milisegundos
                                                     // ----------------------------------
                                                     Calendar c = Calendar.getInstance();
                                                     c.setTimeInMillis(0);
                                                     c.set(year, month, dayOfMonth);
                                                     msFechaSelect = c.getTimeInMillis();
                                                     // ----------------------------------

//
                                                     buscarReservasCabinasFecha(idCabinaSeleccionada, msFechaSelect);
//
                                                 }
                                             }
        );

//
        // Fecha actual del calendario.
        Calendar c = Calendar.getInstance();
        int day= c.get(Calendar.DAY_OF_MONTH);
        int month= c.get (Calendar.MONTH);
        int year= c.get(Calendar.YEAR);
        c.setTimeInMillis(0);
        c.set(year,month,day, 0,0);
        msFechaSelect = c.getTimeInMillis();


        //idCabinaSeleccionada = "Cabina1";

        if(idCabinaSeleccionada == null){
            idCabinaSeleccionada = "Cabina1";
        }


        buscarReservasCabinasFecha(idCabinaSeleccionada, msFechaSelect);
//




        return view;
    }


    //
    //Insertar una reserva en la BD para la silla, fecha, hora e ID de usuario indicado.
    private void hacerReserva(String cabina, long fecha_hora, String usuario) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Reserva r = new Reserva(fecha_hora, usuario);

        db.collection("Cabinas").document(cabina).collection("Reservas")
                .add(r)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("RESERVA", "Reserva realizada correctamente.");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    public void onFailure(@NonNull Exception e) {
                        Log.w("RESERVA", "Error realizando reserva.", e);
                    }
                });
    }

//


    // ------------------------------------------------------------------------------------------------------------

    public void onClickBotonHora (View v) {
        // Realiza la reserva de la silla seleccionada en la fecha seleccionada
        // a la hora indicada en el botón.
        Button b= (Button) v;
        idBotonSelect= b.getId();
        msHoraSelect= getMilliseconds (b);
    }

    public void onClickBotonConfirmar (View v) {

        // Realiza la reserva de la silla seleccionada en la fecha seleccionada
        // a la hora indicada en el botón.

        Button b= (Button) view.findViewById(idBotonSelect);
        activarBoton(b, false);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        hacerReserva (idCabinaSeleccionada,msFechaSelect + msHoraSelect,userId);

    }


    public long getMilliseconds (Button b)
    {
        String hora= b.getText().toString();

        String [] s= hora.split ("-");
        String [] hhmm= s[0].split (":");

        long ms= (Integer.parseInt  (hhmm[0]) * 3600 + Integer.parseInt (hhmm[1]) * 60)*1000;

        return ms;
    }


    // Buscar todas las reservas de la silla con ID "idSilla" y día "fecha".
    public void buscarReservasCabinasFecha(String idCabina, long fecha) {

        // Milisegundos para limitar el final del día.
        long fecha_fin_dia = fecha + 24 * 60 * 60 * 1000;

        ArrayList<Reserva> lista_reservas = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Cabinas").document(idCabina).collection("Reservas")
                .whereGreaterThanOrEqualTo("fecha", fecha)
                .whereLessThan("fecha", fecha_fin_dia)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documento : queryDocumentSnapshots) {
//
                            Reserva r = new Reserva (documento.getLong ("fecha"), documento.getString ("idusuario"));
//
                            lista_reservas.add(r);
                        }

                        DesactivarBotonesReservados(lista_reservas, fecha);
                    }
                });
    }


//

    // Desactiva y pinta de color rojo los botones cuya hora coincida con su texto.
    public void DesactivarBotonesReservados(ArrayList<Reserva> lista_reservas, long fecha_seleccionada) {

        // Activamos todos los botones primero.
        ActivarBotones ();


        for (Reserva r : lista_reservas) {
            // Hora de la fecha seleccionada.
            long hora = r.getFecha() - fecha_seleccionada;


            // Pregunto a cada botón.

            // BOTÓN 9
            Button b = (Button) view.findViewById(R.id.bn9);
            if (getMilliseconds(b) == hora) {
                activarBoton (b, false);
            }


            // BOTÓN 10
            b = (Button) view.findViewById(R.id.bn10);
            if (getMilliseconds(b) == hora) {
                activarBoton (b, false);
            }

            // BOTÓN 11
            b = (Button) view.findViewById(R.id.bn11);
            if (getMilliseconds(b) == hora) {
                activarBoton (b, false);
            }

            // BOTÓN 17
            b = (Button) view.findViewById(R.id.bn17);
            if (getMilliseconds(b) == hora) {
                activarBoton (b, false);
            }

            // BOTÓN 18
            b = (Button) view.findViewById(R.id.bn18);
            if (getMilliseconds(b) == hora) {
                activarBoton (b, false);
            }

            // BOTÓN 19
            b = (Button) view.findViewById(R.id.bn19);
            if (getMilliseconds(b) == hora) {
                activarBoton (b, false);
            }
        }
    }


    // Activación de todos los botones.
    private void ActivarBotones ()
    {
        // BOTÓN 9
        Button b = (Button) view.findViewById(R.id.bn9);
        activarBoton (b, true);

        // BOTÓN 10
        b = (Button) view.findViewById(R.id.bn10);
        activarBoton (b, true);

        // BOTÓN 11
        b = (Button) view.findViewById(R.id.bn11);
        activarBoton (b, true);

        // BOTÓN 17
        b = (Button) view.findViewById(R.id.bn17);
        activarBoton (b, true);

        // BOTÓN 18
        b = (Button) view.findViewById(R.id.bn18);
        activarBoton (b, true);

        // BOTÓN 19
        b = (Button) view.findViewById(R.id.bn19);
        activarBoton (b, true);
    }


    // Activación o desactivación de un botón.
    private void activarBoton (Button b, boolean activar)
    {
        if (activar)
        {
            // Activar
            b.setBackgroundColor(Color.WHITE);
            b.setEnabled(true);
        }
        else
        {
            // Desactivar
            b.setBackgroundColor(Color.RED);
            b.setEnabled(false);
        }
    }


    // Valor inicial de los controles.
    private void Inicializar_Interfaz ()
    {
        // BOTÓN 9
        Button b = (Button) view.findViewById(R.id.bn9);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Plazas.this.onClickBotonHora (view);
            }
        });

        // BOTÓN 10
        b = (Button) view.findViewById(R.id.bn10);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Plazas.this.onClickBotonHora (view);
            }
        });

        // BOTÓN 11
        b = (Button) view.findViewById(R.id.bn11);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Plazas.this.onClickBotonHora (view);
            }
        });

        // BOTÓN 17
        b = (Button) view.findViewById(R.id.bn17);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Plazas.this.onClickBotonHora (view);
            }
        });

        // BOTÓN 18
        b = (Button) view.findViewById(R.id.bn18);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Plazas.this.onClickBotonHora (view);
            }
        });

        // BOTÓN 19
        b = (Button) view.findViewById(R.id.bn19);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Plazas.this.onClickBotonHora (view);
            }
        });

        // BOTÓN ACEPTAR
        b = (Button) view.findViewById(R.id.button5);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Plazas.this.onClickBotonConfirmar (view);
            }
        });


        // BOTÓN SILLA 1
        ImageButton i= (ImageButton) view.findViewById(R.id.idCabina1);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Plazas.this.idCabinaSeleccionada= "Cabina1";
            }
        });

        // BOTÓN SILLA 2
        i = (ImageButton) view.findViewById(R.id.idCabina2);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Plazas.this.idCabinaSeleccionada= "Cabina2";
            }
        });

        // BOTÓN SILLA 3
        i = (ImageButton) view.findViewById(R.id.idCabina3);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Plazas.this.idCabinaSeleccionada= "Cabina3";
            }
        });

        // BOTÓN SILLA 4
        i= (ImageButton) view.findViewById(R.id.idCabina4);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Plazas.this.idCabinaSeleccionada= "Cabina4";
            }
        });


    }


}

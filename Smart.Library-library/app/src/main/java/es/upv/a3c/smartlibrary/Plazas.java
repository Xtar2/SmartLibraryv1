package es.upv.a3c.smartlibrary;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;


class CabinasFragment extends Fragment {

    CalendarView calendarView; //Lola
    TextView Fecha; //Lola
    private long msFechaSelect; //Lola
    private long msHoraSelect; //Lola
    private int idBotonSelect; //Lola
    View view;
    private DatabaseReference mDatabase;
    private TextView mTextView;
    private ValueEventListener valueEventListener;
    private String idSillaSeleccionada;
    private Button botonSeleccionado;
    private Button todostrue;
    private FirebaseFirestore fStore;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.plazas, container, false);
        fStore = FirebaseFirestore.getInstance();
        Inicializar_Interfaz ();

        //Calendario
        calendarView = view.findViewById(R.id.calendarView);
        Fecha = (TextView) view.findViewById(R.id.textViewFecha);

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


                                                     buscarReservasSillaFecha(idSillaSeleccionada, msFechaSelect);

                                                 }
                                             }
        );

        mTextView = view.findViewById(R.id.textView40); // NUEVO -- esto es para el textview28
        mDatabase = FirebaseDatabase.getInstance().getReference();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Obtiene el valor de la base de datos en la ruta especificada
                String value = dataSnapshot.child("Temperatura").getValue(String.class);
                // Actualiza el texto del TextView con el valor obtenido
                mTextView.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Maneja el error
            }
        };
        mDatabase.addValueEventListener(valueEventListener);


        // Fecha actual del calendario.
        Calendar c = Calendar.getInstance();
        int day= c.get(Calendar.DAY_OF_MONTH);
        int month= c.get (Calendar.MONTH);
        int year= c.get(Calendar.YEAR);
        c.setTimeInMillis(0);
        c.set(year,month,day, 0,0);
        msFechaSelect = c.getTimeInMillis();


        if (idSillaSeleccionada == null) {
            idSillaSeleccionada = "Silla1"; // Asignar un valor predeterminado a la variable
        }
        buscarReservasSillaFecha(idSillaSeleccionada, msFechaSelect);



        return view;

    }





    //
    //Insertar una reserva en la BD para la silla, fecha, hora e ID de usuario indicado.
    private void hacerReserva(String silla, long fecha_hora, String usuario) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Reserva r = new Reserva(fecha_hora, usuario);

        db.collection("Sillas").document(silla).collection("Reservas")
                .add(r)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText (getContext(), "La reserva ha sido realizada.", Toast.LENGTH_SHORT).show ();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference sillasRef = database.getReference("Sillas");
                        DatabaseReference sillas1Ref = sillasRef.child(silla);
                        DatabaseReference ledRef = sillas1Ref.child("led");
                        ledRef.setValue("false");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    public void onFailure(@NonNull Exception e) {
                        Log.w("RESERVA", "Error realizando reserva.", e);
                    }
                });
    }




    // ------------------------------------------------------------------------------------------------------------

    public void onClickBotonHora (View v) {

        Button b= (Button) v;
        idBotonSelect= b.getId();
        msHoraSelect= getMilliseconds (b);
    }

    public void onClickBotonConfirmar (View v) {

        Button b= (Button) view.findViewById(idBotonSelect);
        activarBoton(b, false);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        hacerReserva (idSillaSeleccionada,msFechaSelect + msHoraSelect,userId);

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
    public void buscarReservasSillaFecha(String idSilla, long fecha) {

        // Milisegundos para limitar el final del día.
        long fecha_fin_dia = fecha + 24 * 60 * 60 * 1000;

        ArrayList<Reserva> lista_reservas = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Sillas").document(idSilla).collection("Reservas")
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
            Button b = (Button) view.findViewById(R.id.b9);
            if (getMilliseconds(b) == hora) {
                activarBoton (b, false);
            }


            // BOTÓN 10
            b = (Button) view.findViewById(R.id.b10);
            if (getMilliseconds(b) == hora) {
                activarBoton (b, false);
            }

            // BOTÓN 11
            b = (Button) view.findViewById(R.id.b11);
            if (getMilliseconds(b) == hora) {
                activarBoton (b, false);
            }

            // BOTÓN 17
            b = (Button) view.findViewById(R.id.b17);
            if (getMilliseconds(b) == hora) {
                activarBoton (b, false);
            }

            // BOTÓN 18
            b = (Button) view.findViewById(R.id.b18);
            if (getMilliseconds(b) == hora) {
                activarBoton (b, false);
            }

            // BOTÓN 19
            b = (Button) view.findViewById(R.id.b19);
            if (getMilliseconds(b) == hora) {
                activarBoton (b, false);
            }
        }
    }


    // Activación de todos los botones.
    private void ActivarBotones ()
    {
        // BOTÓN 9
        Button b = (Button) view.findViewById(R.id.b9);
        activarBoton (b, true);

        // BOTÓN 10
        b = (Button) view.findViewById(R.id.b10);
        activarBoton (b, true);

        // BOTÓN 11
        b = (Button) view.findViewById(R.id.b11);
        activarBoton (b, true);

        // BOTÓN 17
        b = (Button) view.findViewById(R.id.b17);
        activarBoton (b, true);

        // BOTÓN 18
        b = (Button) view.findViewById(R.id.b18);
        activarBoton (b, true);

        // BOTÓN 19
        b = (Button) view.findViewById(R.id.b19);
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
        Button b = (Button) view.findViewById(R.id.b9);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CabinasFragment.this.onClickBotonHora (view);
            }
        });

        // BOTÓN 10
        b = (Button) view.findViewById(R.id.b10);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CabinasFragment.this.onClickBotonHora (view);
            }
        });

        // BOTÓN 11
        b = (Button) view.findViewById(R.id.b11);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CabinasFragment.this.onClickBotonHora (view);
            }
        });

        // BOTÓN 17
        b = (Button) view.findViewById(R.id.b17);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CabinasFragment.this.onClickBotonHora (view);
            }
        });

        // BOTÓN 18
        b = (Button) view.findViewById(R.id.b18);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CabinasFragment.this.onClickBotonHora (view);
            }
        });

        // BOTÓN 19
        b = (Button) view.findViewById(R.id.b19);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CabinasFragment.this.onClickBotonHora (view);
            }
        });

        // BOTÓN ACEPTAR
        b = (Button) view.findViewById(R.id.botonAceptar);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CabinasFragment.this.onClickBotonConfirmar (view);
            }
        });


// BOTÓN SILLA 1
        Button f = (Button) view.findViewById(R.id.idSilla1);
        f.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view)
            {
                // Cambia el color del botón seleccionado anteriormente a su color original
                if (botonSeleccionado != null) {
                    int color = getResources().getColor(R.color.ColorPrincipalSuave);
                    botonSeleccionado.setBackgroundColor(color);
                }
                // Actualiza el botón seleccionado actualmente y cambia su color
                botonSeleccionado = f;
                f.setBackgroundColor(R.color.ColorPrincipal);
                CabinasFragment.this.idSillaSeleccionada = "Silla1";
            }
        });

        // BOTÓN SILLA 2
        Button  c = (Button) view.findViewById(R.id.idSilla2);
        c.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view)
            {
                // Cambia el color del botón seleccionado anteriormente a su color original
                if (botonSeleccionado != null) {
                    int color = getResources().getColor(R.color.ColorPrincipalSuave);
                    botonSeleccionado.setBackgroundColor(color);
                }
                // Actualiza el botón seleccionado actualmente y cambia su color
                botonSeleccionado = c;
                c.setBackgroundColor(R.color.ColorPrincipal);
                CabinasFragment.this.idSillaSeleccionada = "Silla2";
            }
        });

        // BOTÓN SILLA 3
        Button d = (Button) view.findViewById(R.id.idSilla3);
        d.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view)
            {
                // Cambia el color del botón seleccionado anteriormente a su color original
                if (botonSeleccionado != null) {
                    int color = getResources().getColor(R.color.ColorPrincipalSuave);
                    botonSeleccionado.setBackgroundColor(color);
                }
                // Actualiza el botón seleccionado actualmente y cambia su color
                botonSeleccionado = d;
                d.setBackgroundColor(R.color.ColorPrincipal);
                CabinasFragment.this.idSillaSeleccionada = "Silla3";
            }
        });

        // BOTÓN SILLA 4
        Button a = (Button) view.findViewById(R.id.idSilla4);

        a.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view)
            {
                // Cambia el color del botón seleccionado anteriormente a su color original
                if (botonSeleccionado != null) {
                    int color = getResources().getColor(R.color.ColorPrincipalSuave);
                    botonSeleccionado.setBackgroundColor(color);
                }
                // Actualiza el botón seleccionado actualmente y cambia su color
                botonSeleccionado = a;
                a.setBackgroundColor(R.color.ColorPrincipal);
                CabinasFragment.this.idSillaSeleccionada = "Silla4";
            }
        });
        //todo  onclick start
        TextView textView29 = view.findViewById(R.id.textView29);
        TextView textView28 = view.findViewById(R.id.textView28);
        TextView textView26 = view.findViewById(R.id.textView26);
        TextView textView35 = view.findViewById(R.id.textView35);

        final DocumentReference docRef = fStore.collection("Estado")
                .document("Utilizable");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("TAG111", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Map<String, Object> data = snapshot.getData();
                    Log.e("TAG111", "Current data: " + snapshot.getData());
                    if (data==null||data.isEmpty())
                        return;
                    f.setClickable((boolean)data.get("1"));
                    c.setClickable((boolean)data.get("2"));
                    d.setClickable((boolean)data.get("3"));
                    a.setClickable((boolean)data.get("4"));

                    f.setEnabled((boolean)data.get("1"));
                    c.setEnabled((boolean)data.get("2"));
                    d.setEnabled((boolean)data.get("3"));
                    a.setEnabled((boolean)data.get("4"));

                    textView29.setText((boolean)data.get("1")?"":"rota");
                    textView28.setText((boolean)data.get("2")?"":"rota");
                    textView26.setText((boolean)data.get("3")?"":"rota");
                    textView35.setText((boolean)data.get("4")?"":"rota");
                } else {
                    Log.e("TAG111", "Current data: null");
                }
            }
        });
        //todo  onclick end
    }


    public void onStop() {
        super.onStop();
        // Quita el listener cuando ya no sea necesario
        mDatabase.removeEventListener(valueEventListener);
    }

}
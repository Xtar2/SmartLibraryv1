package es.upv.a3c.smartlibrary;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


class CabinasFragment extends Fragment {
    View view;
    private DatabaseReference mDatabase;
    private TextView mTextView;
    private ValueEventListener valueEventListener;
    Button BotonConfirmar;
    Button BotonSilla1;
    Button BotonSilla2;
    Button BotonSilla3;
    Button BotonSilla4;
    TextView sillatextosleccion;
    private String sillaSeleccionada = null;
    private FirebaseFirestore fStore;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference sillasRef = db.collection("Sillas");
  TextView textosilla;
    Calendar calendar = Calendar.getInstance();
    String idReserva = String.valueOf(calendar.getTimeInMillis());


    CollectionReference usuariosRef = db.collection("usuarios");

    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    CollectionReference reservasRef = usuariosRef.document(userId).collection("ReservasSillas");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.plazas, container, false);
        fStore = FirebaseFirestore.getInstance();
    textosilla = view.findViewById(R.id.Textosilla);
     BotonConfirmar = view.findViewById(R.id.botonAceptar);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference sillasRef = db.collection("Sillas");
BotonSilla1 = view.findViewById(R.id.idSilla1);
        BotonSilla2 = view.findViewById(R.id.idSilla2);
        BotonSilla3 = view.findViewById(R.id.idSilla3);
        BotonSilla4 = view.findViewById(R.id.idSilla4);
sillatextosleccion = view.findViewById(R.id.sillaselecciontexto);





// BOTÓN SILLA 1
        BotonSilla1= (Button) view.findViewById(R.id.idSilla1);
        BotonSilla1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sillaSeleccionada = "Silla1";
                // Verificar si la silla seleccionada esta ocupada
                sillasRef.document(sillaSeleccionada).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                sillatextosleccion.setText(sillaSeleccionada);
                                String estado = document.getString("estado");
                                if(estado.equals("libre")){
                                    // Cambiar el color del botón seleccionado a verde
                                    BotonSilla1.setBackgroundColor(Color.GREEN);

                                    // Guardar el id de la silla seleccionada
                                    sillaSeleccionada = "Silla1";
                                    // Actualizar el TextView con el estado de la silla seleccionada
                                    textosilla.setText(estado);
                                    textosilla.setTextColor(Color.GREEN);
                                }
                                else if(estado.equals("reservada")){
                                    textosilla.setTextColor(Color.YELLOW);
                                    sillaSeleccionada = "Silla1";
                                    BotonSilla1.setBackgroundColor(Color.YELLOW);
                                    // Actualizar el TextView con el estado de la silla seleccionada
                                    textosilla.setText(estado);
                                    textosilla.setTextColor(Color.YELLOW);
                                }
                                else if(estado.equals("ocupada")){
                                    sillaSeleccionada = "Silla1";
                                    BotonSilla1.setBackgroundColor(Color.RED);
                                    // Actualizar el TextView con el estado de la silla seleccionada
                                    textosilla.setText(estado);
                                    textosilla.setTextColor(Color.RED);
                                }
                            }
                        }
                    }
                });
            }
        });


        sillasRef.document("Silla1").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String estado = snapshot.getString("estado");
                    if(estado.equals("reservada")){
                        textosilla.setTextColor(Color.YELLOW);
                        BotonSilla1.setBackgroundColor(Color.YELLOW);
                    }
                    else if (estado.equals("libre")){
                        BotonSilla1.setBackgroundColor(Color.GREEN);
                    }
                } else {
                    Log.d("TAG", "Current data: null");
                }
            }
        });


        final Handler handler4 = new Handler();
        Runnable runnable4 = new Runnable() {
            @Override
            public void run() {
                // Actualizar el estado de la silla en Firestore
                sillasRef.document("Silla1").update("estado" , "libre");
                BotonSilla1.setBackgroundColor(Color.GREEN);
                Toast.makeText(getContext(),"Su ocupación ha acabado, esperamos que le haya servido su estancia",Toast.LENGTH_LONG).show();
            }
        };

        sillasRef.document("Silla1").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot ,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG" , "Listen failed." , e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    String estado = snapshot.getString("estado");
                    if (estado.equals("reservada")) {
                        textosilla.setTextColor(Color.YELLOW);
                        BotonSilla1.setBackgroundColor(Color.YELLOW);
                        DatabaseReference ultrasonidosRef = FirebaseDatabase.getInstance().getReference("Sillas/Silla1/Ultrasonidos");
                        ultrasonidosRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String value = dataSnapshot.getValue().toString();
                                    if (value.equals("0")) {
                                        // check if the chair is already in the "ocupada" state before starting the countdown
                                        sillasRef.document("Silla1").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        String estado = document.getString("estado");
                                                        if (estado.equals("ocupada")) {
                                                            handler4.postDelayed(runnable4, 5000); // delay of 20 seconds
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    } else if (value.equals("1")) {
                                        handler4.removeCallbacks(runnable4);
                                        // Actualizar el estado de la silla en Firestore
                                        sillasRef.document("Silla1").update("estado" , "ocupada");
                                        // Cambiar el color del botón a rojo
                                        BotonSilla1.setBackgroundColor(Color.RED);
                                        Toast.makeText(getContext(),"Ha llegado a su silla, Bienvenido",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    } else if (estado.equals("ocupada")) {
                        sillaSeleccionada = "Silla1";
                        BotonSilla1.setBackgroundColor(Color.RED);
                        Eliminarocupado(sillaSeleccionada);
                    } else if (estado.equals("libre")) {
                        BotonSilla1.setBackgroundColor(Color.GREEN);
                    }
                }
            }
        });





        // BOTÓN SILLA 2
        BotonSilla2= (Button) view.findViewById(R.id.idSilla2);
        BotonSilla2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sillaSeleccionada = "Silla2";
                // Verificar si la silla seleccionada esta ocupada
                sillasRef.document(sillaSeleccionada).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                sillatextosleccion.setText(sillaSeleccionada);
                                String estado = document.getString("estado");
                                if(estado.equals("libre")){
                                    // Cambiar el color del botón seleccionado a verde
                                    BotonSilla2.setBackgroundColor(Color.GREEN);

                                    // Guardar el id de la silla seleccionada
                                    sillaSeleccionada = "Silla2";
                                    // Actualizar el TextView con el estado de la silla seleccionada
                                    textosilla.setText(estado);
                                    textosilla.setTextColor(Color.GREEN);
                                }
                                else if(estado.equals("reservada")){
                                    sillaSeleccionada = "Silla2";
                                    BotonSilla2.setBackgroundColor(Color.YELLOW);
                                    // Actualizar el TextView con el estado de la silla seleccionada
                                    textosilla.setText(estado);
                                    textosilla.setTextColor(Color.YELLOW);
                                }
                                else if(estado.equals("ocupada")){
                                    sillaSeleccionada = "Silla2";
                                    BotonSilla2.setBackgroundColor(Color.RED);
                                    // Actualizar el TextView con el estado de la silla seleccionada
                                    textosilla.setText(estado);
                                    textosilla.setTextColor(Color.RED);
                                }
                            }
                        }
                    }
                });
            }
        });





        sillasRef.document("Silla2").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String estado = snapshot.getString("estado");
                    if(estado.equals("reservada")){
                        textosilla.setTextColor(Color.YELLOW);
                        BotonSilla2.setBackgroundColor(Color.YELLOW);
                    }
                    else if (estado.equals("libre")){
                        BotonSilla2.setBackgroundColor(Color.GREEN);
                    }
                } else {
                    Log.d("TAG", "Current data: null");
                }
            }
        });

        final Handler handler2 = new Handler();
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                // Actualizar el estado de la silla en Firestore
                sillasRef.document("Silla2").update("estado" , "libre");
                BotonSilla2.setBackgroundColor(Color.GREEN);
                Toast.makeText(getContext(),"Su ocupación ha acabado, esperamos que le haya servido su estancia",Toast.LENGTH_LONG).show();
            }
        };

        sillasRef.document("Silla2").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot ,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG" , "Listen failed." , e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    String estado = snapshot.getString("estado");
                    if (estado.equals("reservada")) {
                        textosilla.setTextColor(Color.YELLOW);
                        BotonSilla2.setBackgroundColor(Color.YELLOW);
                        DatabaseReference ultrasonidosRef = FirebaseDatabase.getInstance().getReference("Sillas/Silla2/Ultrasonidos");
                        ultrasonidosRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String value = dataSnapshot.getValue().toString();
                                    if (value.equals("0")) {
                                        // check if the chair is already in the "ocupada" state before starting the countdown
                                        sillasRef.document("Silla2").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        String estado = document.getString("estado");
                                                        if (estado.equals("ocupada")) {
                                                            handler2.postDelayed(runnable2, 5000); // delay of 20 seconds
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    } else if (value.equals("1")) {
                                        handler2.removeCallbacks(runnable2);
                                        // Actualizar el estado de la silla en Firestore
                                        sillasRef.document("Silla2").update("estado" , "ocupada");
                                        // Cambiar el color del botón a rojo
                                        BotonSilla2.setBackgroundColor(Color.RED);
                                        Toast.makeText(getContext(),"Ha llegado a su silla, Bienvenido",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }else if(estado.equals("ocupada")){
                        sillaSeleccionada = "Silla2";
                        BotonSilla2.setBackgroundColor(Color.RED);
                        Eliminarocupado(sillaSeleccionada);
                    }
                    else if(estado.equals("libre")){
                        BotonSilla2.setBackgroundColor(Color.GREEN);
                    }
                }
            }
        });


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Actualizar el estado de la silla en Firestore
                sillasRef.document("Silla4").update("estado" , "libre");
                BotonSilla4.setBackgroundColor(Color.GREEN);
                Toast.makeText(getContext(),"Su ocupación ha acabado, esperamos que le haya servido su estancia",Toast.LENGTH_LONG).show();
            }
        };


        // BOTÓN SILLA 4
        BotonSilla4= (Button) view.findViewById(R.id.idSilla4);
        BotonSilla4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sillaSeleccionada = "Silla4";
                // Verificar si la silla seleccionada esta ocupada
                sillasRef.document(sillaSeleccionada).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                sillatextosleccion.setText(sillaSeleccionada);
                                String estado = document.getString("estado");
                                if(estado.equals("libre")){
                                    // Cambiar el color del botón seleccionado a verde
                                    BotonSilla4.setBackgroundColor(Color.GREEN);

                                    // Guardar el id de la silla seleccionada
                                    sillaSeleccionada = "Silla4";
                                    // Actualizar el TextView con el estado de la silla seleccionada
                                    textosilla.setText(estado);
                                    textosilla.setTextColor(Color.GREEN);

                                }
                                else if(estado.equals("reservada")){
                                    sillaSeleccionada = "Silla4";
                                    BotonSilla4.setBackgroundColor(Color.YELLOW);
                                    // Actualizar el TextView con el estado de la silla seleccionada
                                    textosilla.setText(estado);
                                    textosilla.setTextColor(Color.YELLOW);
                                }
                                else if(estado.equals("ocupada")){
                                    sillaSeleccionada = "Silla4";
                                    BotonSilla4.setBackgroundColor(Color.RED);
                                    // Actualizar el TextView con el estado de la silla seleccionada
                                    textosilla.setText(estado);
                                    textosilla.setTextColor(Color.RED);
                                }
                            }
                        }
                    }
                });
            }
        });



        sillasRef.document("Silla4").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot ,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG" , "Listen failed." , e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    String estado = snapshot.getString("estado");
                    if (estado.equals("reservada")) {
                        textosilla.setTextColor(Color.YELLOW);
                        BotonSilla4.setBackgroundColor(Color.YELLOW);
                        DatabaseReference ultrasonidosRef = FirebaseDatabase.getInstance().getReference("Sillas/Silla4/Ultrasonidos");
                        ultrasonidosRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String value = dataSnapshot.getValue().toString();
                                    if (value.equals("0")) {
                                        // check if the chair is already in the "ocupada" state before starting the countdown
                                        sillasRef.document("Silla4").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        String estado = document.getString("estado");
                                                        if (estado.equals("ocupada")) {
                                                            handler.postDelayed(runnable, 5000); // delay of 20 seconds
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    } else if (value.equals("1")) {
                                        handler.removeCallbacks(runnable);
                                        // Actualizar el estado de la silla en Firestore
                                        sillasRef.document("Silla4").update("estado" , "ocupada");
                                        // Cambiar el color del botón a rojo
                                        BotonSilla4.setBackgroundColor(Color.RED);
                                        Toast.makeText(getContext(),"Ha llegado a su silla, Bienvenido",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }else if(estado.equals("ocupada")){
                        sillaSeleccionada = "Silla4";
                        BotonSilla4.setBackgroundColor(Color.RED);
                        Eliminarocupado(sillaSeleccionada);

                    }
                    else if(estado.equals("libre")){
                        BotonSilla4.setBackgroundColor(Color.GREEN);
                    }
                }
            }
        });



        // BOTÓN SILLA 3
        BotonSilla3= (Button) view.findViewById(R.id.idSilla3);
        BotonSilla3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sillaSeleccionada = "Silla3";
                // Verificar si la silla seleccionada esta ocupada
                sillasRef.document(sillaSeleccionada).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                sillatextosleccion.setText(sillaSeleccionada);
                                String estado = document.getString("estado");
                                if(estado.equals("libre")){
                                    // Cambiar el color del botón seleccionado a verde
                                    BotonSilla3.setBackgroundColor(Color.GREEN);

                                    // Guardar el id de la silla seleccionada
                                    sillaSeleccionada = "Silla3";
                                    // Actualizar el TextView con el estado de la silla seleccionada
                                    textosilla.setText(estado);
                                    textosilla.setTextColor(Color.GREEN);
                                }
                                else if(estado.equals("reservada")){
                                    sillaSeleccionada = "Silla3";
                                    BotonSilla3.setBackgroundColor(Color.YELLOW);
                                    // Actualizar el TextView con el estado de la silla seleccionada
                                    textosilla.setText(estado);
                                    textosilla.setTextColor(Color.YELLOW);
                                }
                                else if(estado.equals("ocupada")){
                                    sillaSeleccionada = "Silla3";
                                    BotonSilla3.setBackgroundColor(Color.RED);
                                    // Actualizar el TextView con el estado de la silla seleccionada
                                    textosilla.setText(estado);
                                    textosilla.setTextColor(Color.RED);
                                }
                            }
                        }
                    }
                });
            }
        });

        final Handler handler3 = new Handler();
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                // Actualizar el estado de la silla en Firestore
                sillasRef.document("Silla3").update("estado" , "libre");
                BotonSilla3.setBackgroundColor(Color.GREEN);
                Toast.makeText(getContext(),"Su ocupación ha acabado, esperamos que le haya servido su estancia",Toast.LENGTH_LONG).show();
            }
        };

        sillasRef.document("Silla3").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot ,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG" , "Listen failed." , e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    String estado = snapshot.getString("estado");
                    if (estado.equals("reservada")) {
                        textosilla.setTextColor(Color.YELLOW);
                        BotonSilla3.setBackgroundColor(Color.YELLOW);
                        DatabaseReference ultrasonidosRef = FirebaseDatabase.getInstance().getReference("Sillas/Silla3/Ultrasonidos");
                        ultrasonidosRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String value = dataSnapshot.getValue().toString();
                                    if (value.equals("0")) {
                                        // check if the chair is already in the "ocupada" state before starting the countdown
                                        sillasRef.document("Silla3").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        String estado = document.getString("estado");
                                                        if (estado.equals("ocupada")) {
                                                            handler3.postDelayed(runnable3, 5000); // delay of 20 seconds
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    } else if (value.equals("1")) {
                                        handler3.removeCallbacks(runnable3);
                                        // Actualizar el estado de la silla en Firestore
                                        sillasRef.document("Silla3").update("estado" , "ocupada");
                                        // Cambiar el color del botón a rojo
                                        BotonSilla3.setBackgroundColor(Color.RED);
                                        Toast.makeText(getContext(),"Ha llegado a su silla, Bienvenido",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                    else if(estado.equals("ocupada")){
                        sillaSeleccionada = "Silla3";
                        BotonSilla3.setBackgroundColor(Color.RED);
                        Eliminarocupado(sillaSeleccionada);
                    }
                    else if(estado.equals("libre")){
                        BotonSilla3.setBackgroundColor(Color.GREEN);
                    }
                }
            }
        });


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





            Inicializar_Interfaz();
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















    public void Eliminarocupado(String numerosilla){
        reservasRef.whereEqualTo("numero", numerosilla).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                } else {
                    Log.w("Silla", "Error al buscar y eliminar la reserva", task.getException());
                }
            }
        });


    }








    // Valor inicial de los controles.
    private void Inicializar_Interfaz ()
    {





       BotonConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sillaSeleccionada != null){
                    sillasRef.document(sillaSeleccionada).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String estado = document.getString("estado");
                                    if(estado.equals("libre")) {
                                        sillasRef.document(sillaSeleccionada).update("estado" , "reservada");
                                        textosilla.setText("Reservada");
                                        Toast.makeText(getContext(), sillaSeleccionada + "reservada, si no acude en una hora su reserva sera cancelada",Toast.LENGTH_LONG).show();
                                        // Cambiar el color del botón correspondiente a la silla seleccionada a amarillo
                                        switch (sillaSeleccionada) {

                                            case "Silla1":
                                                Map<String, Object> reserva = new HashMap<>();
                                                reserva.put("numero", sillaSeleccionada);
                                                db.collection("usuarios").document(userId).collection("ReservasSillas")
                                                        .add(reserva)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {

                                                                Log.d(TAG, "Reserva guardada con éxito en la subcolección del usuario");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error al guardar la reserva en la subcolección del usuario", e);
                                                            }
                                                        });
                                                BotonSilla1.setBackgroundColor(Color.YELLOW);
                                                break;
                                            case "Silla2":

                                                Map<String, Object> reserva2 = new HashMap<>();
                                                reserva2.put("numero", sillaSeleccionada);
                                                db.collection("usuarios").document(userId).collection("ReservasSillas")
                                                        .add(reserva2)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {

                                                                Log.d(TAG, "Reserva guardada con éxito en la subcolección del usuario");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error al guardar la reserva en la subcolección del usuario", e);
                                                            }
                                                        });
                                                BotonSilla2.setBackgroundColor(Color.YELLOW);
                                                break;
                                            case "Silla3":

                                                Map<String, Object> reserva3 = new HashMap<>();
                                                reserva3.put("numero", sillaSeleccionada);
                                                db.collection("usuarios").document(userId).collection("ReservasSillas")
                                                        .add(reserva3)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Log.d(TAG, "Reserva guardada con éxito en la subcolección del usuario");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error al guardar la reserva en la subcolección del usuario", e);
                                                            }
                                                        });
                                                BotonSilla3.setBackgroundColor(Color.YELLOW);
                                                break;
                                            case "Silla4":

                                                Map<String, Object> reserva4 = new HashMap<>();
                                                reserva4.put("numero", sillaSeleccionada);
                                                db.collection("usuarios").document(userId).collection("ReservasSillas")
                                                        .add(reserva4)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Log.d(TAG, "Reserva guardada con éxito en la subcolección del usuario");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error al guardar la reserva en la subcolección del usuario", e);
                                                            }
                                                        });
                                                BotonSilla4.setBackgroundColor(Color.YELLOW);
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
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
                    BotonSilla1.setClickable((boolean)data.get("1"));
                    BotonSilla2.setClickable((boolean)data.get("2"));
                    BotonSilla3.setClickable((boolean)data.get("3"));
                    BotonSilla4.setClickable((boolean)data.get("4"));

                    BotonSilla1.setEnabled((boolean)data.get("1"));
                    BotonSilla2.setEnabled((boolean)data.get("2"));
                    BotonSilla3.setEnabled((boolean)data.get("3"));
                    BotonSilla4.setEnabled((boolean)data.get("4"));

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
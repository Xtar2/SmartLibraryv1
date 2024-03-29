package es.upv.a3c.smartlibrary;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;


import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.upv.a3c.smartlibrary.adaptadores.AdaptadorLibros;

public class Libros extends Fragment implements AdaptadorLibros.EventListener {


    RecyclerView recycler;
    EditText Codigo;
    Button Escaneo;
    Button Btnadd;
    SearchView searchView;
    AdaptadorLibros adapter;
    TextView LibroEscogido;
    TextView LibroEscogido2;
    private final ArrayList<LibrosVo> Listalibros = new ArrayList<LibrosVo>();
    String CogerNumero;
    TextView Fecha;
    CalendarView Calendarvista;
    Button Confirmar;
     long diferencia;
     long diferenciadias;
Date dateObject2;
Date dateObject1;
long Fecha1;
long Fecha2;
long Fechadevolucion;
String Fechadevoluciontexto;
List<String> Prueba;
String FechaBBDD;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Obtener una referencia a la colección de usuarios
    CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("usuarios");

    // Obtener la ID del usuario actual
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // Crear una referencia a la subcolección de perfiles para el usuario actual
    CollectionReference userProfileSubcollection = usersCollection.document(userId).collection("Mis Libros");



    // Obtener una referencia a la colección de usuarios
    CollectionReference usersCollection2 = FirebaseFirestore.getInstance().collection("usuarios");

    // Obtener la ID del usuario actual
    String userId2 = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // Crear una referencia a la subcolección de perfiles para el usuario actual
    CollectionReference userProfileSubcollection2 = usersCollection2.document(userId2).collection("HistorialReservas");



    public Libros() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.libros , container , false);




        searchView = view.findViewById(R.id.searchView);
        Button button = view.findViewById(R.id.button3);         // ESTO LO PONES ANTES DEL BUTTONSETONCKICKLISTENER

        button.setOnClickListener(new View.OnClickListener() {  // TODO ESTO LO PONES ANTES DE LA LINEA (recycler = view.findViewById(R.id.recycler);)
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
                db.collection("ListadoLibros").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            int randomIndex = new Random().nextInt(documents.size());
                            DocumentSnapshot randomBook = documents.get(randomIndex);
                            String bookName = randomBook.getString("nombre");
                            searchView.setQuery(bookName, false);
                        }
                    }
                });
            }
        });


        recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setItemAnimator(null);

        FirestoreRecyclerOptions<LibrosVo> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<LibrosVo>().setQuery(FirebaseFirestore.getInstance().collection("ListadoLibros") , LibrosVo.class).build();
        adapter = new AdaptadorLibros(firestoreRecyclerOptions , this , Listalibros);
        recycler.setAdapter(adapter);

        Fecha = view.findViewById(R.id.Fecha);
        Confirmar = view.findViewById(R.id.DatosLibrosBoton);

        db = FirebaseFirestore.getInstance();
        Codigo = view.findViewById(R.id.Codigo);
        Escaneo = view.findViewById(R.id.Escaneo);
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        Btnadd = view.findViewById(R.id.DatosLibrosBoton);
        LibroEscogido = view.findViewById(R.id.LibroEscogido);
        Calendarvista = view.findViewById(R.id.calendarViewlibros);
        LibroEscogido2 = view.findViewById(R.id.LibroEscogido2);

        Calendar cl = Calendar.getInstance();//here your time in miliseconds
        long fecha = Calendar.getInstance().getTimeInMillis();
        Calendarvista.setMinDate(fecha);
        Calendarvista.setMaxDate( fecha + 1209600000L);
//-------------------------------------------------------
//-------------------------------------------------------
        //Calendario
//-------------------------------------------------------
//-------------------------------------------------------
        Calendarvista.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView , int i , int i1 , int i2) {
                String date =  i2 + "/" + (i1 + 1) + "/" + i;
                Log.d(TAG,"Cambio a la fecha: " + date);
                Fecha.setText(date);
            }
        });
        BuscarLibros();
        Confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmacionReserva(LibroEscogido.getText().toString(),Fecha.getText().toString(),LibroEscogido2.getText().toString());
            }
        });
        Escaneo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escanear();
            }
        });
        return view;
    }




    private void BuscarLibros2(String Numero) {
        FirestoreRecyclerOptions<LibrosVo> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<LibrosVo>().setQuery(FirebaseFirestore.getInstance().collection("ListadoLibros").orderBy("ISBN").startAt(Numero).endAt(Numero) , LibrosVo.class).build();
        adapter = new AdaptadorLibros(firestoreRecyclerOptions , this , Listalibros);
        adapter.startListening();
        recycler.setAdapter(adapter);

    }

    //---------------------------------------------------
//-------------------------------------------------------
    //Busqueda de Libros
//-------------------------------------------------------
//-------------------------------------------------------
    private void BuscarLibros() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                textSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                textSearch(s);
                return false;
            }

        });


    }

    //---------------------------------------------------
//-------------------------------------------------------
    //Llamada a la interfaz del adaptadorLibros
//-------------------------------------------------------
//-------------------------------------------------------
    @Override
    public void onEventName(String nombre) {
        LibroEscogido.setText(nombre);
    }

    @Override
    public void onEventName2(String nombre2) {
       LibroEscogido2.setText(nombre2);
    }




    //-------------------------------------------------------
//-------------------------------------------------------
    //Reserva de libros
//-------------------------------------------------------
//-------------------------------------------------------
   public void ConfirmacionReserva(String IsbnLibro, String FechaLibro,String NombreLibro){

    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());


        dialogo1.setTitle("Confirmación de la reserva");
        dialogo1.setMessage("¿Quiere reservar el libro "+ NombreLibro + " desde el dia " + FechaLibro +  " durante 14 dias? (siempre puede devolverlo antes) ");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1 , int id) {

          if (NombreLibro.equals("Ninguno") || Fecha.equals("Fecha")){
            Toast.makeText(getContext(),"Introduzca una fecha o libro validos",Toast.LENGTH_SHORT).show();
                }
                else{ db.collection("ReservaLibros")
                  .get()
                  .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                      @Override
                      public void onComplete(@NonNull Task<QuerySnapshot> task) {
                          if (task.isSuccessful()) {
                              }
                                  CollectionReference collectionReference = db.collection("ReservaLibros");
                                  Task<QuerySnapshot> querySnapshotTask = collectionReference.whereEqualTo("isbn" , IsbnLibro).get();
                                  querySnapshotTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                      @Override
                                      public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                          if (task.isSuccessful()) {
                                              QuerySnapshot querySnapshot = task.getResult();
                                              List<DocumentSnapshot> libros = querySnapshot.getDocuments();
                                              if (task.getResult().getDocuments().size() <= 0) {
                                                  Map<String, Object> LibrosReservados = new HashMap<>();
                                                  LibrosReservados.put("nombre",NombreLibro);
                                                  LibrosReservados.put("isbn" , IsbnLibro);
                                                  LibrosReservados.put("fecha" , FechaLibro);


                                                  db.collection("ReservaLibros").add(LibrosReservados).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                      @Override
                                                      public void onSuccess(DocumentReference documentReference) {
                                                          Toast.makeText(getContext() , "Reserva Confirmada" , Toast.LENGTH_SHORT).show();
                                                      }
                                                  }).addOnFailureListener(new OnFailureListener() {
                                                      @Override
                                                      public void onFailure(@NonNull Exception e) {
                                                          Toast.makeText(getContext() , "Error al reservar" , Toast.LENGTH_SHORT).show();
                                                      }
                                                  });




                                                      Map<String, Object> LibrosReservado = new HashMap<>();
                                                      LibrosReservado.put("nombre" , NombreLibro);
                                                      LibrosReservado.put("isbn" , IsbnLibro);
                                                      LibrosReservado.put("fecha" , FechaLibro);



                                                      userProfileSubcollection.add(LibrosReservado).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                          @Override
                                                          public void onSuccess(DocumentReference documentReference) {

                                                          }
                                                      }).addOnFailureListener(new OnFailureListener() {
                                                          @Override
                                                          public void onFailure(@NonNull Exception e) {

                                                          }
                                                      });


                                                  // Crear un mapa con los datos a agregar
                                                  Map<String, Object> historialReserva = new HashMap<>();
                                                  historialReserva.put("nombre", NombreLibro);
                                                  historialReserva.put("isbn", IsbnLibro);
                                                  historialReserva.put("fecha", FechaLibro);

// Agregar el mapa a la colección "HistorialReservas"
                                                  userProfileSubcollection2.add(historialReserva)
                                                          .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                              @Override
                                                              public void onSuccess(DocumentReference documentReference) {
                                                                  // Mostrar un mensaje de éxito
                                                                  Toast.makeText(getContext(), "Reserva agregada al historial", Toast.LENGTH_SHORT).show();
                                                              }
                                                          })
                                                          .addOnFailureListener(new OnFailureListener() {
                                                              @Override
                                                              public void onFailure(@NonNull Exception e) {
                                                                  // Mostrar un mensaje de error
                                                                  Toast.makeText(getContext(), "Error al agregar la reserva al historial", Toast.LENGTH_SHORT).show();
                                                              }
                                                          });


                                              } else {
                                                  for (QueryDocumentSnapshot document : task.getResult()) {
                                                      Prueba = Collections.singletonList(((document.getData().get("isbn").toString())));
                                                      FechaBBDD = document.getData().get("fecha").toString();

                                                      System.out.println(Prueba);
                                                      if (Prueba.contains(IsbnLibro)) {
                                                          if (!libros.isEmpty()) {
                                                              DocumentSnapshot libro = libros.get(0);
                                                              int index = querySnapshot.getDocuments().indexOf(libro);
                                                              System.out.println(index);
                                                              System.out.println(libro);
                                                              FechaBBDD = libro.getData().get("fecha").toString();


                                                              SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                              try {
                                                                  dateObject1 = dateFormat.parse(FechaBBDD);
                                                                  Fecha1 = dateObject1.getTime();
                                                                  Fechadevolucion = dateObject1.getTime();
                                                                  Fechadevolucion = Fechadevolucion + 1209600000;
                                                                  dateObject1.setTime(Fechadevolucion);
                                                                  Fechadevoluciontexto = dateFormat.format(dateObject1);
                                                                  try {
                                                                      dateObject2 = dateFormat.parse(FechaLibro);
                                                                      Fecha2 = dateObject2.getTime();
                                                                      diferenciadias = Fecha1 - Fecha2;
                                                                      diferencia = diferenciadias / 86400000;
                                                                  } catch (ParseException e) {
                                                                      e.printStackTrace();
                                                                  }
                                                              } catch (ParseException e) {
                                                                  e.printStackTrace();

                                                              }
                                                              if (diferencia <= 14) {
                                                                  Toast.makeText(getContext() , "Esta reservado hasta el dia " + Fechadevoluciontexto , Toast.LENGTH_SHORT).show();
                                                              }
                                                          }
                                                      }
                                                  }
                                              }
                                          }
                                      }
                                  });
                          }
            });
          }
            }
                });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogo1, int id) {

        }
    });
        dialogo1.show();
}






//-------------------------------------------------------
//-------------------------------------------------------
    //Busqueda Libros
//-------------------------------------------------------
//-------------------------------------------------------
    public void textSearch(String s) {

        FirestoreRecyclerOptions<LibrosVo> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<LibrosVo>().setQuery(FirebaseFirestore.getInstance().collection("ListadoLibros").orderBy("nombre").startAt(s).endAt(s + "~") , LibrosVo.class).build();
        adapter = new AdaptadorLibros(firestoreRecyclerOptions , this , Listalibros);
        adapter.startListening();
        recycler.setAdapter(adapter);

    }

    //-------------------------------------------------------
//-------------------------------------------------------
    //Escaneo
//-------------------------------------------------------
//-------------------------------------------------------
    public void escanear() {

        IntentIntegrator integrador = IntentIntegrator.forSupportFragment(Libros.this);
        integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrador.setPrompt("Lector Barras");
        integrador.setCameraId(0);
        integrador.setBeepEnabled(false);
        integrador.setBarcodeImageEnabled(true);
        integrador.initiateScan();

    }

    //Por si cancelas el escaneo
    @Override
    public void onActivityResult(int requestCode , int resultCode , Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode , resultCode , data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext() , "Cancelaste el escaneo" , Toast.LENGTH_SHORT).show();
            } else {
                Codigo.setText(result.getContents().toString());
                CogerNumero = Codigo.getText().toString();
                BuscarLibros2(CogerNumero);
            }
        } else {
            super.onActivityResult(requestCode , resultCode , data);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}

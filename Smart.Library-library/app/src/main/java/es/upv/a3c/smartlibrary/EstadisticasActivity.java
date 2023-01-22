package es.upv.a3c.smartlibrary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import es.upv.a3c.smartlibrary.adaptadores.AdaptadorLibros;
import es.upv.a3c.smartlibrary.adaptadores.AdaptadorReservas;

public class EstadisticasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView textView;
    private FirebaseFirestore db;
    private AdaptadorLibros adapter;
    private Timer timer;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    String time;
    private static final String PREFS_NAME = "TimerPrefs";
    private static final String KEY_HOURS = "hours";
    private static final String KEY_MINUTES = "minutes";
    private static final String KEY_SECONDS = "seconds";
    TextView textooo;
    TextView textooo2;
    TextView tiempoTranscurrido;
    private ArrayList<LibrosVo> Listalibros = new ArrayList<LibrosVo>();

    EventListener eventListener;
    private EventListener<QuerySnapshot> historialListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.estadisticas);


        textView = findViewById(R.id.Librosreservados);
        recyclerView = findViewById(R.id.recyclerHistorial);


        db = FirebaseFirestore.getInstance();




        /// Obtener la ID del usuario actual
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

// Crear una referencia a la colección "usuarios"
        CollectionReference usersCollection = db.collection("usuarios");

// Crear una referencia a la subcolección "HistorialReservas" para el usuario actual
        CollectionReference historialReservasSubcollection = usersCollection.document(userId).collection("HistorialReservas");

// Crear opciones para el adaptador con una consulta para obtener los libros reservados del usuario actual
        FirestoreRecyclerOptions<LibrosVo> options = new FirestoreRecyclerOptions.Builder<LibrosVo>()
                .setQuery(historialReservasSubcollection , LibrosVo.class)
                .build();

        // Inicializar el adaptador con las opciones creadas
        adapter = new AdaptadorLibros(options , (AdaptadorLibros.EventListener) eventListener , Listalibros);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Iniciar el adaptador para escuchar cambios en la colección y actualizar la lista
        adapter.startListening();


    }



    @Override
    protected void onResume() {
        super.onResume();


        }

        @Override
        protected void onStart () {
            super.onStart();


        }


        @Override
        protected void onStop () {
            super.onStop();
        }
    }

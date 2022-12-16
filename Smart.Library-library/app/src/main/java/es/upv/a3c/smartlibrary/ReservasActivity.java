package es.upv.a3c.smartlibrary;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ReservasActivity extends AppCompatActivity {
    RecyclerView recycler;
    private ArrayList<LibrosVo> Listalibros = new ArrayList<LibrosVo>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference Librosreservados = db.collection("ReservaLibros");

    // Obtener la ID del usuario actual
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

   DocumentReference userprfile = db.collection("usuarios").document(userId);



    // Crear una referencia a la subcolección de perfiles para el usuario actual
    CollectionReference userProfileSubcollection = userprfile.collection("Mis Libros");
    AdaptadorReservas adapter;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservas);

        recycler = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recycler.setLayoutManager(layoutManager);
        FirestoreRecyclerOptions<ReservasVo> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<ReservasVo>().setQuery(userProfileSubcollection, ReservasVo.class).build();
        adapter = new AdaptadorReservas(firestoreRecyclerOptions, Listalibros);
        adapter.startListening();
        recycler.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}

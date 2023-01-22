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

import es.upv.a3c.smartlibrary.adaptadores.AdaptadorReservas;

public class ReservasActivity extends AppCompatActivity {
    RecyclerView recycler;
    RecyclerView recyclerSillas;
    RecyclerView recyclerviewCabinas;
    private ArrayList<LibrosVo> Listalibros = new ArrayList<LibrosVo>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference Librosreservados = db.collection("ReservaLibros");
    private static Sillas sillas;
    private static Cabinass cabinas;
    AdaptadorReservasSillas adapterSC;
    AdaptadorReservasCabinas adapterSCa;
    private RecyclerView recyclerView;
    private  ReservaSillaAdapter adapter2;
    // Obtener la ID del usuario actual

    CollectionReference usuariosRef = db.collection("usuarios");

    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    CollectionReference reservasRef = usuariosRef.document(userId).collection("ReservasSillas");


    String userId2 = FirebaseAuth.getInstance().getCurrentUser().getUid();

    DocumentReference userprfile = db.collection("usuarios").document(userId2);


    // Crear una referencia a la subcolección de perfiles para el usuario actual
    CollectionReference userProfileSubcollection = userprfile.collection("Mis Libros");
    AdaptadorReservas adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservas);

        FirestoreRecyclerOptions<PojoSillas> options = new FirestoreRecyclerOptions.Builder<PojoSillas>()
                .setQuery(reservasRef, PojoSillas.class)
                .build();
        recyclerView = findViewById(R.id.recyclerviewSillas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new ReservaSillaAdapter(options);
        adapter2.startListening();
        recyclerView.setAdapter(adapter2);

        recycler = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        FirestoreRecyclerOptions<ReservasVo> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<ReservasVo>().setQuery(userProfileSubcollection, ReservasVo.class).build();
        adapter = new AdaptadorReservas(firestoreRecyclerOptions, Listalibros);
        adapter.startListening();
        recycler.setAdapter(adapter);
        this.cabinas = new Cabinass();

        rellenarListaCabinas();

    }




    public void rellenarListaCabinas() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<ReservaSC> lista = new ArrayList<>();
        ReservasActivity.cabinas.cargarCabinasBDUsuario(userId, new CabinasCallback() {
                    @Override
                    public void cabinasCargadas() {
                        for (int i = 0; i < ReservasActivity.cabinas.numCabinas(); i++) {
                            Cabina cabina = ReservasActivity.cabinas.getCabina(i);

                            for (int r = 0; r < cabina.numReservas(); r++) {
                                Reserva reserva = cabina.getReserva(r);
                                String idCabina = cabina.getId();
                                ReservaSC rSC = new ReservaSC(idCabina, reserva);
                                lista.add(rSC);
                            }
                        }

                        recyclerviewCabinas = findViewById(R.id.recyclerviewCabinas);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(ReservasActivity.this);
                        recyclerviewCabinas.setLayoutManager(layoutManager);
                        adapterSCa = new AdaptadorReservasCabinas(lista);
                        recyclerviewCabinas.setAdapter(adapterSCa);
                    }
                }
        );


    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        adapter2.stopListening();
    }
}

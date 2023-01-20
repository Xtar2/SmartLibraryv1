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
        this.sillas = new Sillas();
        this.cabinas = new Cabinass();
        rellenarListaSillas();
        rellenarListaCabinas();

    }


    public void rellenarListaSillas() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<ReservaSC> lista = new ArrayList<>();
        ReservasActivity.sillas.cargarSillasBDUsuario(userId, new SillasCallback() {
                    @Override
                    public void sillasCargadas() {
                        for (int i = 0; i < ReservasActivity.sillas.numSillas(); i++) {
                            Silla silla = ReservasActivity.sillas.getSilla(i);

                            for (int r = 0; r < silla.numReservas(); r++) {
                                Reserva reserva = silla.getReserva(r);
                                String idSilla = silla.getId();
                                ReservaSC rSC = new ReservaSC(idSilla, reserva);
                                lista.add(rSC);
                            }
                        }

                        recyclerSillas = findViewById(R.id.recyclerviewSillas);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(ReservasActivity.this);
                        recyclerSillas.setLayoutManager(layoutManager);
                        adapterSC = new AdaptadorReservasSillas(lista);
                        recyclerSillas.setAdapter(adapterSC);
                    }
                }
        );


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
    }
}

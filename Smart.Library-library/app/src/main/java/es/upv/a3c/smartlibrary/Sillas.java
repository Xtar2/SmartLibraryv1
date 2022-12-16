package es.upv.a3c.smartlibrary;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Sillas {

    // ATRIBUTOS

    private ArrayList<Silla> listaSillas;

    private int num_sillas, num_reservas, nsillas, nreservas;


    // MÉTODOS

    // Constructor por defecto.
    public Sillas() {
        this.listaSillas = new ArrayList<Silla>();
    }


    // Tamaño.
    public int numSillas() {
        return this.listaSillas.size();
    }


    // Buscar silla por posición.
    public Silla getSilla(int pos) {
        if ((pos >= 0) && (pos < this.numSillas())) {
            return listaSillas.get(pos);
        }
        return null;
    }


    // Insertar silla.
    public void insertarSilla(Silla silla) {
        this.listaSillas.add(silla);
    }


    // Borrar silla.
    // Devuelve si la silla ha podido ser eliminada.
    public boolean borrarSilla(int pos) {
        if ((pos >= 0) && (pos < this.numSillas())) {
            listaSillas.remove(pos);
            return true;
        }
        return false;
    }


    // Reservar silla.
    public boolean reservarSilla(int pos, Reserva reserva) {
        Silla silla = getSilla(pos);
        if (silla != null) {
            return silla.insertarReserva(reserva);
        }
        return false;
    }


    // Cargar todas las reservas de todas las sillas.
    public void cargarSillasBD(SillasCallback cb) {
        nsillas = 0;
        nreservas = 0;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Sillas").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                num_sillas = queryDocumentSnapshots.size();

                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    // Insertar cada silla.


                    Silla s = new Silla();
                    Sillas.this.insertarSilla(s);

                    CollectionReference colref = db.collection("Sillas").document(doc.getId()).collection("Reservas");
                    colref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            nsillas++;
                            num_reservas = num_reservas + queryDocumentSnapshots.size();

                            if ((nsillas == num_sillas) && (nreservas == num_reservas)) {
                                cb.sillasCargadas();
                            }

                            for (DocumentSnapshot ej : queryDocumentSnapshots) {
                                // Insertar cada reserva de una silla.

                                Reserva r = ej.toObject(Reserva.class);
                                int id_silla = Integer.parseInt(ej.getReference().getParent().getParent().getId());

                                Sillas.this.reservarSilla(id_silla, r);
                                nreservas++;

                                if ((nsillas == num_sillas) && (nreservas == num_reservas)) {
                                    cb.sillasCargadas();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int i = 0;
                        }
                    });
                }
            }
        });
    }
}


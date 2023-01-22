package es.upv.a3c.smartlibrary;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

public class Cabinass {

    // ATRIBUTOS

    private ArrayList<Cabina> listaCabinas;

    private int num_cabinas, num_reservas, ncabinas, nreservas;


    // MÉTODOS

    // Constructor por defecto.
    public Cabinass() {
        this.listaCabinas = new ArrayList<Cabina>();
    }


    // Tamaño.
    public int numCabinas() {
        return this.listaCabinas.size();
    }


    // Busca la silla segun su identificador.
    public Cabina getCabina(String id) {
        for (int i = 0; i < this.numCabinas(); i++) {
            if (this.listaCabinas.get(i).getId().equals(id)) {
                return this.listaCabinas.get(i);
            }
        }
        return null;
    }


    // Insertar silla. Si no encuentra la silla inserta una.
    public void insertarCabina(Cabina cabina) {
        if (this.getCabina(cabina.getId()) == null) {
            this.listaCabinas.add(cabina);
        }
    }


    // Borrar silla.
    // Devuelve si la silla ha podido ser eliminada.
    public boolean borrarSilla(int pos) {
        if ((pos >= 0) && (pos < this.numCabinas())) {
            listaCabinas.remove(pos);
            return true;
        }
        return false;
    }


    // Reservar silla.
    public boolean reservarCabina(String id, Reserva reserva) {
        Cabina cabina = getCabina(id);
        if (cabina != null) {
            return cabina.insertarReserva(reserva);
        }
        return false;
    }

    // Get silla
    Cabina getCabina(int i) {
        return this.listaCabinas.get(i);
    }

    // Cargar todas las reservas de todas las sillas.

    // Buscar las sillas reservadas por un usuario.

    // Cargar todas las reservas de todas las sillas de un usuario específico.
    public void cargarCabinasBDUsuario(String user, CabinasCallback cb) {
        ncabinas = 0;
        nreservas = 0;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Cabinas").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() { //Busca las sillas
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                num_cabinas = queryDocumentSnapshots.size();

                for (DocumentSnapshot doc : queryDocumentSnapshots) { //For para recorrer las sillas
                    // Insertar cada silla.

                    Cabinass.this.listaCabinas.clear(); //Vacio la lista de silla

                    CollectionReference colref = db.collection("Cabinas").document(doc.getId()).collection("Reservas");
                    colref.whereEqualTo("idusuario", user)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    ncabinas++;
                                    num_reservas = num_reservas + queryDocumentSnapshots.size();

                                    if ((ncabinas == num_cabinas) && (nreservas == num_reservas)) {
                                        cb.cabinasCargadas();
                                    }

                                    for (DocumentSnapshot ej : queryDocumentSnapshots) {
                                        // Insertar cada reserva de una silla.

                                        Reserva r = ej.toObject(Reserva.class);
                                        String id_cabina = ej.getReference().getParent().getParent().getId();

                                        Cabina c = new Cabina(id_cabina);
                                        Cabinass.this.insertarCabina(c);
                                        Cabinass.this.reservarCabina(id_cabina, r);
                                        nreservas++;

                                        if ((ncabinas == num_cabinas) && (nreservas == num_reservas)) {
                                            cb.cabinasCargadas();
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



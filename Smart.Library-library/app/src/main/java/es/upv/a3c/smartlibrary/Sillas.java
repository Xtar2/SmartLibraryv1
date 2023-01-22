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


    // Busca la silla segun su identificador.
    public Silla getSilla(String id) {
        for (int i = 0; i < this.numSillas(); i++) {
            if (this.listaSillas.get(i).getId().equals(id)) {
                return this.listaSillas.get(i);
            }
        }
        return null;
    }


    // Insertar silla. Si no encuentra la silla inserta una.
    public void insertarSilla(Silla silla) {
        if (this.getSilla(silla.getId()) == null) {
            this.listaSillas.add(silla);
        }
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
    public boolean reservarSilla(String id, Reserva reserva) {
        Silla silla = getSilla(id);
        if (silla != null) {
            return silla.insertarReserva(reserva);
        }
        return false;
    }

    // Get silla
    Silla getSilla(int i) {
        return this.listaSillas.get(i);
    }

    // Cargar todas las reservas de todas las sillas.

/*
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


                    Silla s = new Silla(doc.getId());
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
                                String id_silla = ej.getReference().getParent().getParent().getId();

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

 */


    // Buscar las sillas reservadas por un usuario.

    // Cargar todas las reservas de todas las sillas de un usuario específico.
    public void cargarSillasBDUsuario(String user, SillasCallback cb) {
        nsillas = 0;
        nreservas = 0;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Sillas").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() { //Busca las sillas
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                num_sillas = queryDocumentSnapshots.size();

                for (DocumentSnapshot doc : queryDocumentSnapshots) { //For para recorrer las sillas
                    // Insertar cada silla.

                    Sillas.this.listaSillas.clear(); //Vacio la lista de silla

                    CollectionReference colref = db.collection("Sillas").document(doc.getId()).collection("Reservas");
                    colref.whereEqualTo("idusuario", user)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                                        String id_sillaa = ej.getReference().getParent().getParent().getId();

                                        Silla s = new Silla(id_sillaa);
                                        Sillas.this.insertarSilla(s);
                                        Sillas.this.reservarSilla(id_sillaa, r);
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


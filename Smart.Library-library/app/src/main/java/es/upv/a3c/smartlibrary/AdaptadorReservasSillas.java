package es.upv.a3c.smartlibrary;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class
















































































































AdaptadorReservasSillas extends RecyclerView.Adapter<AdaptadorReservasSillas.ViewHolderReservas> {

    ArrayList<ReservaSC> listaReservas;
    int globalPosition = -1;
    public ViewGroup parent;

    public AdaptadorReservasSillas(ArrayList<ReservaSC> lista) {
        this.listaReservas = lista;

    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorReservasSillas.ViewHolderReservas holder, @SuppressLint("RecyclerView") int position) {
        holder.numeroSilla.setText(this.listaReservas.get(position).getIdSC());
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(this.listaReservas.get(position).getReserva().getFecha());
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH + 1);
        int year = c.get(Calendar.YEAR);
        String f = "" + day + "/" + month + "/" + year;
        holder.Fecha.setText(f);
        holder.Boton.setText("Cancelar");
        holder.Boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AdaptadorReservasSillas.this.parent.getContext());
                // Add the buttons
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        long fecha = AdaptadorReservasSillas.this.listaReservas.get(position).getReserva().getFecha();
                        String usuario = AdaptadorReservasSillas.this.listaReservas.get(position).getReserva().getIdusuario();
                        String idsc = AdaptadorReservasSillas.this.listaReservas.get(position).getIdSC();
                        eliminarReserva(idsc, usuario, fecha, position);
                        ledSillaFalse(idsc);

                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                String mg = "¿Estas seguro de que desea eliminar la reserva?";
                builder.setMessage(mg)
                        .setTitle("Eliminar reserva");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.listaReservas.size();
    }

    @androidx.annotation.NonNull
    @Override
    //Enlaza esta clase con el archivo de item_lista
    public AdaptadorReservasSillas.ViewHolderReservas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista3, parent, false);
        this.parent = parent;
        return new AdaptadorReservasSillas.ViewHolderReservas(v);
    }


    public class ViewHolderReservas extends RecyclerView.ViewHolder {
        TextView numeroSilla;
        LinearLayout Fondo;
        Button Boton;
        TextView Fecha;


        public ViewHolderReservas(@androidx.annotation.NonNull View itemView) {
            super(itemView);

            numeroSilla = itemView.findViewById(R.id.numeroSilla);
            Fondo = itemView.findViewById(R.id.FondoRecycler);
            Boton = itemView.findViewById(R.id.cancelButton);
            Fecha = itemView.findViewById(R.id.fechaReserva);

        }
    }

    // Elimina la reserva de una silla realizada por un usuario en una fecha determinada.
    private void eliminarReserva(String idSilla, String idUsuario, long fecha, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Sillas").document(idSilla).collection("Reservas").whereEqualTo("idusuario", idUsuario)
                .whereEqualTo("fecha", fecha).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {
                            String idReserva = queryDocumentSnapshots.getDocuments().get(0).getId();
                            DocumentReference doc = db.collection("Sillas").document(idSilla).collection("Reservas").document(idReserva);
                            doc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("ELIMINACIÓN DE RESERVA", "OK");
                                            AdaptadorReservasSillas.this.listaReservas.remove(position);
                                            AdaptadorReservasSillas.this.notifyItemRemoved(position);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("ELIMINACIÓN DE RESERVA", e.getMessage());
                                        }
                                    });

                        }
                    }
                });

    }

    public void ledSillaFalse(String idsc) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference sillasRef = database.getReference("Sillas");
        DatabaseReference idSillasRef = sillasRef.child(idsc);
        DatabaseReference ledRef = idSillasRef.child("led");
        ledRef.setValue("false");

    }


}

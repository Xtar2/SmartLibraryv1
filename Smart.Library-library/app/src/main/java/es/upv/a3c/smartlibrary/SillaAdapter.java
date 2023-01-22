package es.upv.a3c.smartlibrary;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ReservaSillaAdapter extends FirestoreRecyclerAdapter<PojoSillas, ReservaSillaAdapter.ReservaSillaViewHolder> {
FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usuariosRef = db.collection("usuarios");

    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    CollectionReference reservasRef = usuariosRef.document(userId).collection("ReservasSillas");
    public ReservaSillaAdapter(FirestoreRecyclerOptions<PojoSillas> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(ReservaSillaViewHolder holder, int position, PojoSillas model) {
        holder.numeroSilla.setText(model.getNumero());
        holder.Boton.setText("Cancelar");

        holder.Boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                // Add the buttons
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        eliminarReserva(model.getNumero());
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




    public void eliminarReserva(String numeroSilla) {
        reservasRef.whereEqualTo("numero", numeroSilla).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

        // Actualizar el estado de la silla correspondiente en la colección "Sillas" a "libre"
        Map<String, Object> updates = new HashMap<>();
        updates.put("estado", "libre");
        db.collection("Sillas").document(numeroSilla).update(updates);
    }

    @Override
    public ReservaSillaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pojo_sillas_lista,
                parent, false);

        return new ReservaSillaViewHolder(v);

    }

    class ReservaSillaViewHolder extends RecyclerView.ViewHolder {
        TextView numeroSilla;
        TextView estadoSilla;
        Button Boton;

        public ReservaSillaViewHolder(View itemView) {
            super(itemView);
            numeroSilla = itemView.findViewById(R.id.numeroSilla);
            Boton = itemView.findViewById(R.id.cancelButton);
        }
    }
}
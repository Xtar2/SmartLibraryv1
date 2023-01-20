package es.upv.a3c.smartlibrary.adaptadores;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import es.upv.a3c.smartlibrary.LibrosVo;
import es.upv.a3c.smartlibrary.R;
import es.upv.a3c.smartlibrary.ReservasVo;

public class AdaptadorReservas extends FirestoreRecyclerAdapter<ReservasVo, AdaptadorReservas.ViewHolderReservas> {



    ArrayList<LibrosVo> Libros;
    int globalPosition = -1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference doc;

    public AdaptadorReservas(FirestoreRecyclerOptions<ReservasVo> options, ArrayList<LibrosVo> Libros) {
        super(options);
        this.Libros = Libros;


    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolderReservas holder , @SuppressLint("RecyclerView") int position , @NonNull ReservasVo model) {
        holder.Nombre.setText(model.getNombre());
        holder.Fecha.setText(model.getFecha());
        holder.Descripcion.setText(model.getDescripcion());
        holder.Boton.setText("Cancelar");

        // Add a click listener to the button
        // Add a click listener to the button
        holder.Boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

// Obtener una referencia a la colección de usuarios
                CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("usuarios");

// Obtener la ID del usuario actual
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

// Crear una referencia a la subcolección de perfiles para el usuario actual
                CollectionReference userProfileSubcollection = usersCollection.document(userId).collection("Mis Libros");

// Crear una consulta para encontrar el documento con el ISBN especificado
                Query query = userProfileSubcollection.whereEqualTo("isbn", getItem(position).getISBN());

// Ejecutar la consulta y borrar el documento encontrado
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                        }
                    }
                });
                // Obtener una referencia a la colección "ReservaLibros"
                CollectionReference reservaLibrosCollection = FirebaseFirestore.getInstance().collection("ReservaLibros");

// Crear una consulta para encontrar el documento con el ISBN especificado
                Query query1 = reservaLibrosCollection.whereEqualTo("isbn", getItem(position).getISBN());

// Ejecutar la consulta y borrar el documento encontrado
                query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }
                        }
                    }
                });
            }
        });
    }

    @androidx.annotation.NonNull
    @Override
    //Enlaza esta clase con el archivo de item_lista
    public ViewHolderReservas onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista2 , parent , false);
        return new ViewHolderReservas(v);
    }


    public class ViewHolderReservas extends RecyclerView.ViewHolder {
        TextView Nombre, Descripcion, ISBN;
        ImageView img;
        LinearLayout Fondo;
        Button Boton;
        TextView Fecha;


        public ViewHolderReservas(@androidx.annotation.NonNull View itemView) {
            super(itemView);

            Nombre = itemView.findViewById(R.id.Nombrelibro);
            Descripcion = itemView.findViewById(R.id.Infolibro);

            Fondo = itemView.findViewById(R.id.FondoRecycler);
            Boton = itemView.findViewById(R.id.cancelButton);
            Fecha = itemView.findViewById(R.id.FechaLibro);



        }
    }
}

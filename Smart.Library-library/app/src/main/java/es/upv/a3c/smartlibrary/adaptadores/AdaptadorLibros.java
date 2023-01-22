package es.upv.a3c.smartlibrary.adaptadores;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import es.upv.a3c.smartlibrary.LibrosVo;
import es.upv.a3c.smartlibrary.R;
import es.upv.a3c.smartlibrary.SendData;

public class AdaptadorLibros extends FirestoreRecyclerAdapter<LibrosVo, AdaptadorLibros.ViewHolderLibros> {

    ArrayList<LibrosVo> Libros;
    private EventListener eventListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference doc;
    DocumentReference doc2;
    DocumentReference doc3;

    public AdaptadorLibros(FirestoreRecyclerOptions<LibrosVo> options , EventListener eventListener , ArrayList<LibrosVo> Libros) {
        super(options);
        this.eventListener = eventListener;
        this.Libros = Libros;


    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolderLibros holder , @SuppressLint("RecyclerView") int position , @NonNull LibrosVo model) {
        holder.Nombre.setText(model.getNombre());
        holder.ISBN.setText(model.getISBN());
        holder.Descripcion.setText(model.getDescripcion());
        Glide.with(holder.img.getContext()).load(model.getImg()).into(holder.img);
        holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doc = db.collection("ListadoLibros").document(getItem(position).getISBN());

                    doc.addSnapshotListener(new com.google.firebase.firestore.EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value , @Nullable FirebaseFirestoreException error) {
                            eventListener.onEventName(value.getId());
                        }
                    });
                    doc2 = db.collection("ListadoLibros").document(getItem(position).getNombre());
                    doc2.addSnapshotListener(new com.google.firebase.firestore.EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value , @Nullable FirebaseFirestoreException error) {
                            eventListener.onEventName2(value.getId());
                        }
                    });

                }
            });


    }

    @androidx.annotation.NonNull
    @Override
    //Enlaza esta clase con el archivo de item_lista
    public ViewHolderLibros onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent , int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista , parent , false);
        return new ViewHolderLibros(v);
    }


    public interface EventListener {
        void onEventName(String nombre);
        void onEventName2(String nombre2);

    }




    public class ViewHolderLibros extends RecyclerView.ViewHolder {


        TextView Nombre, Descripcion, ISBN;
        ImageView img;
        LinearLayout Fondo;


        public ViewHolderLibros(@androidx.annotation.NonNull View itemView) {
            super(itemView);

            Nombre = itemView.findViewById(R.id.Nombrelibro);
            Descripcion = itemView.findViewById(R.id.Infolibro);
            img = itemView.findViewById(R.id.Imagenrecycler);
            ISBN = itemView.findViewById(R.id.FechaLibro);
            Fondo = itemView.findViewById(R.id.FondoRecycler);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }
}

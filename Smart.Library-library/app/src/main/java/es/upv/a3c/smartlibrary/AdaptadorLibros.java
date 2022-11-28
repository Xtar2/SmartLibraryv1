package es.upv.a3c.smartlibrary;


import android.app.Activity;
import android.os.Build;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import  android.content.Context;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdaptadorLibros extends FirestoreRecyclerAdapter<LibrosVo,AdaptadorLibros.ViewHolderLibros> {


    private EventListener eventListener;

    public AdaptadorLibros(@NonNull FirestoreRecyclerOptions<LibrosVo> options) {
        super(options);
        this.eventListener = eventListener;

    }

    @Override
    public void onBindViewHolder( ViewHolderLibros holder, int position, @NonNull LibrosVo model) {
        holder.Nombre.setText(model.getNombre());
        holder.ISBN.setText(model.getISBN());
        holder.Descripcion.setText(model.getDescripcion());
        Glide.with(holder.img.getContext()).load(model.getImg()).into(holder.img);





    }

    @androidx.annotation.NonNull
    @Override
    //Enlaza esta clase con el archivo de item_lista
    public ViewHolderLibros onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista,parent,false);
        return new ViewHolderLibros(v);

        

    }




    public class ViewHolderLibros extends RecyclerView.ViewHolder {


        TextView Nombre,Descripcion,ISBN;
        ImageView img;



        public ViewHolderLibros(@androidx.annotation.NonNull View itemView) {
            super(itemView);

         Nombre=itemView.findViewById(R.id.Nombrelibro);
          Descripcion=itemView.findViewById(R.id.Infolibro);
            img=itemView.findViewById(R.id.Imagenrecycler);
            ISBN = itemView.findViewById(R.id.ISBNLibro);



        }


    }
    public interface EventListener {
        void onEventName(String nombre);
    }




}

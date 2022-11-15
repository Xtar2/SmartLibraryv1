package es.upv.a3c.smartlibrary;


import android.os.Build;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdaptadorLibros extends RecyclerView.Adapter<AdaptadorLibros.ViewHolderLibros> {
    ArrayList<LibrosVo> listaLibros;

    //lista filtrar
    ArrayList<LibrosVo> listaOriginal;
    public AdaptadorLibros(ArrayList<LibrosVo> listaLibros) {
        this.listaLibros = listaLibros;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaLibros);
    }






    @androidx.annotation.NonNull
    @Override
    //Enlaza esta clase con el archivo de item_lista
    public ViewHolderLibros onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista,null,false);
        return new ViewHolderLibros(view);
    }

    @Override
    // enlaza el adaptador con el viewholder
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolderLibros holder, int position) {
   holder.EtiNombre.setText(listaLibros.get(position).getNombre());
        holder.EtiInformacion.setText(listaLibros.get(position).getInfo());
        holder.foto.setImageResource(listaLibros.get(position).getFoto());
    }

    //Metodo de filtrado
    public  void filtrado(String searchview){
        int longitud = searchview.length();
        if(longitud == 0){
            listaLibros.clear();
            listaLibros.addAll(listaOriginal);
       } else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                List<LibrosVo> coleccion = listaOriginal.stream().filter(i -> i.getNombre().toLowerCase().contains(searchview.toLowerCase()))
                        .collect(Collectors.toList());
                listaLibros.clear();
                listaLibros.addAll(coleccion);

            } else {
                for (LibrosVo c: listaOriginal) {
                    if (c.getNombre().toLowerCase().contains(searchview.toLowerCase())){
                        listaLibros.add(c);
                    }
                }
                    
            }
        }
        notifyDataSetChanged();
    }

    @Override
    //tamaño de la lista
    public int getItemCount() {
        return listaLibros.size();
    }

    public class ViewHolderLibros extends RecyclerView.ViewHolder {

        TextView EtiNombre,EtiInformacion;
        ImageView foto;

        public ViewHolderLibros(@androidx.annotation.NonNull View itemView) {
            super(itemView);

            EtiNombre=itemView.findViewById(R.id.Nombrelibro);
            EtiInformacion=itemView.findViewById(R.id.Infolibro);
            foto=itemView.findViewById(R.id.Imagenrecycler);

        }


    }
}

package es.upv.a3c.smartlibrary.adaptadores;

import android.content.Context;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.safetynet.SafetyNetClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import es.upv.a3c.smartlibrary.Foro;
import es.upv.a3c.smartlibrary.R;

public class adaptadorForos extends FirestoreRecyclerAdapter<Foro, adaptadorForos.ForoViewHolder> {

    private String categoriaSeleccionada;




    public adaptadorForos(FirestoreRecyclerOptions<Foro> options, String categoriaSeleccionada) {
        super(options);
        this.categoriaSeleccionada = categoriaSeleccionada;
    }

    @Override
    protected void onBindViewHolder(ForoViewHolder holder, int position, Foro model) {
        try {
            URI uri = new URI(model.getLink());
            String domain = uri.getHost();
            List<String> permitidos = Arrays.asList("youtube.com","youtu.be", "google.com", "wuolah.com");
            if(permitidos.contains(domain)){
                holder.textViewLink.setText(model.getLink());
                Linkify.addLinks(holder.textViewLink,Linkify.WEB_URLS);
                holder.textViewDescription.setText(model.getDescripcion());
            }
            else{
                holder.textViewLink.setText("Link no permitido");
            }
        } catch (URISyntaxException e) {
            holder.textViewLink.setText("Link no válido");
        }


        String link = model.getLink();


    }

    @Override
    public ForoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista_foro,
                parent, false);
        return new ForoViewHolder(v);
    }

    class ForoViewHolder extends RecyclerView.ViewHolder {
        TextView textViewLink;
        TextView textViewDescription;

        public ForoViewHolder(View itemView) {
            super(itemView);
            textViewLink = itemView.findViewById(R.id.text_view_link);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
        }
    }
}
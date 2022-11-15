package es.upv.a3c.smartlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


public class PrincipalLibros extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.principalibros, container, true);

        Button PasarLibros = view.findViewById(R.id.BotonPrestamolibros);




                PasarLibros.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarLibros(null);
            }
        });

        return view;
    }

    private void lanzarLibros(View view) {
        Intent i = new Intent(getActivity(),Libros.class);
        startActivity(i);
    }

}
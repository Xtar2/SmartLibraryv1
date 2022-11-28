package es.upv.a3c.smartlibrary;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Libros extends Fragment implements SendData  {


    RecyclerView recycler;
    EditText Codigo;
    Button Escaneo;
    Button Btnadd;
     SearchView searchView;
    Spinner PrestamoLibros;
    AdaptadorLibros adapter;
    Spinner spinnerlibros;
    Button boton;
    TextView LibroEscogido;

    public Libros(){

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.libros, container, false);

        recycler=view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));



        FirestoreRecyclerOptions<LibrosVo> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder <LibrosVo>().setQuery(FirebaseFirestore.getInstance().collection("ListadoLibros"), LibrosVo.class).build();



        adapter = new AdaptadorLibros(firestoreRecyclerOptions);
        recycler.setAdapter(adapter);


//Spinner
        spinnerlibros = view.findViewById(R.id.SpinnerLibros);
        ArrayList<HorasLibro> HorasLibros = new ArrayList<>();
        HorasLibros.add (new HorasLibro(1,"2 dias"));
        HorasLibros.add (new HorasLibro(2, "3 dias"));
        HorasLibros.add (new HorasLibro(3, "4 dias"));
        HorasLibros.add (new HorasLibro(4, "1 semana"));
        HorasLibros.add (new HorasLibro(5, "2 semanas"));

        ArrayAdapter<HorasLibro> adapterlibros = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,HorasLibros);


spinnerlibros.setAdapter(adapterlibros);

//Recyclerview






       //ISBN BASE DATOS
        db = FirebaseFirestore.getInstance();
        Codigo = view.findViewById(R.id.TextoEscaneo);
        Escaneo = view.findViewById(R.id.Escaneo);
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        Btnadd = view.findViewById (R.id.DatosLibrosBoton);
        LibroEscogido = view.findViewById(R.id.LibroEscogido);

        BuscarLibros();
        Btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llenarSpinner();
                String ISBN = Codigo.getText().toString();
                String title = searchView.getQuery().toString();
                if(title.isEmpty()&&ISBN.isEmpty()){
                    Toast.makeText(getContext(),"empty",Toast.LENGTH_SHORT);
                    return;
                }
                putISBN(title,ISBN);
            }

            private void llenarSpinner() {
            }
        });



        Escaneo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escanear();
            }
        });
        return view;



    }


    private void BuscarLibros(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                textSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                textSearch(s);
                return false;
            }
        });



    }
    public void textSearch(String s){

        FirestoreRecyclerOptions<LibrosVo> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder <LibrosVo>().setQuery(FirebaseFirestore.getInstance().collection("ListadoLibros").orderBy("nombre").startAt(s).endAt(s+"~"), LibrosVo.class).build();
adapter = new AdaptadorLibros(firestoreRecyclerOptions);
      adapter.startListening();
      recycler.setAdapter(adapter);

    }



    //ISBN a base de datos
private void putISBN(String title,String isbn) {
    Map<String,Object> ISBN= new HashMap<>();



    String uid= FirebaseAuth.getInstance().getUid();
    FirebaseDatabase.getInstance().getReference().child("ISBNs").child(uid)
            .setValue(ISBN);
}
//Funcion Escaneo
    public void escanear()
    {

            IntentIntegrator integrador = IntentIntegrator.forSupportFragment(Libros.this);
        integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrador.setPrompt("Lector Barras");
        integrador.setCameraId(0);
        integrador.setBeepEnabled(false);
        integrador.setBarcodeImageEnabled(true);
        integrador.initiateScan();

    }
//Por si cancelas el escaneo
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);

        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(getContext(),"Cancelaste el escaneo",Toast.LENGTH_SHORT).show();
            } else{
            Codigo.setText(result.getContents().toString());
            }
        }
        else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void sendInfo(String texto) {
     LibroEscogido.setText(texto);
    }
}

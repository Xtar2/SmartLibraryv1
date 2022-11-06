package es.upv.a3c.smartlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class Libros extends Fragment  {

    EditText Codigo;
    Button Escaneo;
    Button Btnadd;
    private SearchView searchView;

    public Libros(){

    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.libros, container, false);
        db = FirebaseFirestore.getInstance();
        Codigo = view.findViewById(R.id.TextoEscaneo);
        Escaneo = view.findViewById(R.id.Escaneo);
        searchView = view.findViewById(R.id.searchView);
        Btnadd = view.findViewById (R.id.DatosLibrosBoton);

        Btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ISBN = Codigo.getText().toString();
                String title = searchView.getQuery().toString();
                if(title.isEmpty()){
                    Toast.makeText(getContext(),"empty",Toast.LENGTH_SHORT);
                    return;
                }
                if(ISBN.isEmpty()){
                    Toast.makeText(getContext(),"Ingresar ISBN",Toast.LENGTH_SHORT);
                    return;
                }
                putISBN(title,ISBN);
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

    private void putISBN(String title,String isbn) {
        Map<String,Object> map= new HashMap<>();
        map.put("ISBN",isbn);
        map.put("title",title);
        String uid= FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference().child("ISBNs").child(uid)
                .setValue(map);
    }


    public void escanear()
    {

            IntentIntegrator integrador = IntentIntegrator.forSupportFragment(Libros.this);
        integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrador.setPrompt("Lector Barras");
        integrador.setCameraId(0);
        integrador.setBeepEnabled(false);
        integrador.setBarcodeImageEnabled(false);
        integrador.initiateScan();
    }

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
}

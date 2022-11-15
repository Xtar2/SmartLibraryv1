package es.upv.a3c.smartlibrary;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Libros extends Fragment implements SearchView.OnQueryTextListener {

    ArrayList<LibrosVo> listaLibros;
    RecyclerView recycler;

    EditText Codigo;
    Button Escaneo;
    Button Btnadd;
     SearchView searchView;
    Spinner PrestamoLibros;
    AdaptadorLibros adapter;
    Spinner spinnerlibros;
    Button boton;
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

        listaLibros=new ArrayList<>();
        recycler=view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        llenarLibros();


        adapter = new AdaptadorLibros(listaLibros);
        recycler.setAdapter(adapter);





       //ISBN BASE DATOS
        db = FirebaseFirestore.getInstance();
        Codigo = view.findViewById(R.id.TextoEscaneo);
        Escaneo = view.findViewById(R.id.Escaneo);
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        Btnadd = view.findViewById (R.id.DatosLibrosBoton);

        //Listener del search view
        searchView.setOnQueryTextListener(this);




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



    //Llena la lista del recycler
    private void llenarLibros() {
        listaLibros.add(new LibrosVo("Norman y Mix","Norman y Mix son dos superhéroes muy especiales... Sus poderes son cuanto menos imprevisibles. Vaya, que varían en función del día. Una mañana se levantan con la capacidad de volverse invisibles (¡mola!), y al día siguiente todo lo que tocan se tiñe de otro color (ehem...).",R.drawable.normanymix));
        listaLibros.add(new LibrosVo("Boruto tomo 1","En esta era la Gran Guerra Ninja ya es cosa del pasado y la Villa Oculta de la Hoja goza de paz. El hijo del Séptimo Hokage, Boruto, ha estado viviendo tristemente a la sombra de la enorme influencia de su padre, ajeno a la nueva catástrofe que se cierne sobre el mundo... ¡¡Aquí empieza la leyenda de una nueva generación!",R.drawable.boruto1));
        listaLibros.add(new LibrosVo("Boruto tomo 2","\n" +
                "Boruto ha superado la segunda prueba del examen de ascenso a grado medio, ¡¡e incluso su padre, Naruto, lo ha felicitado!! Boruto rompe a llorar de la alegría, pero, de todos modos, hace uso de la herramienta científica ninja, que se supone que está prohibida, para obtener la victoria en la tercera prueba, que consiste en un combate individual. Sin embargo, ¡en el momento en el que Naruto anuncia la descalificación de Boruto...!",R.drawable.boruto2));
        listaLibros.add(new LibrosVo("Boruto tomo 3","Naruto y sus aliados tienen a Momoshiki bajo control hasta que éste se transforma en un ser más poderoso. Naruto y Sasuke tendrán que trabajar en equipo si quieren tener alguna posibilidad de superar a un rival tan feroz, ¿pero acaso no será Boruto quien posee la clave de la victoria? ",R.drawable.boruto3));
        listaLibros.add(new LibrosVo("Boruto tomo 4","Boruto ha sido nombrado guardaespaldas de Tentô, el hijo del señor feudal. Al principio, sus caprichos propios de un niño de papá lo sacaban de quicio, pero poco a poco va dejando de lado sus reticencias gracias a un juego de cartas y a un entrenamiento con el shuriken. ¡Sin embargo, alguien secuestra a Tentô y Boruto se dirige a rescatarlo él solo!",R.drawable.boruto4));
        listaLibros.add(new LibrosVo("Boruto tomo 5","La misteriosa organización “cascarón” se ha puesto en marcha para buscar el “receptáculo” que han perdido. Mientras tanto, Boruto está haciendo prácticas de combate ninja con Naruto, pero resulta que este está empleando una herramienta científica ninja en la mano derecha... Boruto se indigna ante esto, ¡pero entonces le asignan una nueva misión! ¡¡La catástrofe se cierne de nuevo sobre el mundo!! ",R.drawable.boruto5));
        listaLibros.add(new LibrosVo("La torre oscura","En un mundo extrañamente parecido al nuestro Roland Deschain de Gilead persigue a su enemigo, el hombre de negro. Roland, solitario, quizá maldito, anda sin descanso a través de un paisaje triste y abandonado. Conoce a Jake, un chico de Nueva York pero de otro tiempo, y ambos unen sus destinos. Ante ellos están las montañas. Y mucho más allá, la Torre Oscura...",R.drawable.torreoscura));
    }

    //ISBN a base de datos
private void putISBN(String title,String isbn) {
    Map<String,Object> map= new HashMap<>();
    if (!TextUtils.isEmpty(title)){
        map.put("title",title);
    }else{
        map.put("title","");
    }
    if (!TextUtils.isEmpty(isbn)){
        map.put("ISBN",isbn);
    }else{
        map.put("ISBN","");
    }


    String uid= FirebaseAuth.getInstance().getUid();
    FirebaseDatabase.getInstance().getReference().child("ISBNs").child(uid)
            .setValue(map);
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


    //searchview filtrar
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
     adapter.filtrado(s);
        return true;
    }
}

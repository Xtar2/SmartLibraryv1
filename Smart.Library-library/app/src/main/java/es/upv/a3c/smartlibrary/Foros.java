package es.upv.a3c.smartlibrary;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.upv.a3c.smartlibrary.adaptadores.adaptadorForos;
import es.upv.a3c.smartlibrary.adaptadores.adaptadorForos;

public class Foros extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private String categoriaSeleccionada;
    private Spinner spinner;

    private RecyclerView recyclerViewGTI;
    private RecyclerView recyclerViewTurismo;
    private RecyclerView recyclerViewCienciasAmbientales;
    private RecyclerView recyclerViewAudiovisuales;
    private RecyclerView recyclerViewTeleco;

    private adaptadorForos adapterGTI;
    private adaptadorForos adapterTurismo;
    private adaptadorForos adapterCienciasAmbientales;
    private adaptadorForos adapterAudiovisuales;
    private adaptadorForos adapterTeleco;

    private EditText editTextLink;
    private EditText editTextDescription;
    private Button buttonSubmit;
    private String query;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foros);

        db = FirebaseFirestore.getInstance();

       searchView = findViewById(R.id.search_view);
        editTextLink = findViewById(R.id.edit_text_link);
        editTextDescription = findViewById(R.id.edit_text_description);

        recyclerViewGTI = findViewById(R.id.recyclerGTI);
        recyclerViewTurismo = findViewById(R.id.recyclerTURISMO);
        recyclerViewCienciasAmbientales = findViewById(R.id.recyclerAMBIENTALES);
        recyclerViewAudiovisuales = findViewById(R.id.recyclerAUDIOVISUALES);
        recyclerViewTeleco = findViewById(R.id.recyclerTELECO);

        recyclerViewGTI.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTurismo.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCienciasAmbientales.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAudiovisuales.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTeleco.setLayoutManager(new LinearLayoutManager(this));


        db.collection("foros").document("GTI").collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    FirestoreRecyclerOptions<Foro> optionsGTI = new FirestoreRecyclerOptions.Builder<Foro>()
                            .setQuery(db.collection("foros").document("GTI").collection("posts"), Foro.class)
                            .build();
                    adapterGTI = new adaptadorForos(optionsGTI, "GTI");
                    adapterGTI.startListening();
                    recyclerViewGTI.setAdapter(adapterGTI);
                }
            }
        });


        String link = editTextLink.getText().toString();








        db.collection("foros").document("Turismo").collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    FirestoreRecyclerOptions<Foro> optionsTurismo = new FirestoreRecyclerOptions.Builder<Foro>()
                            .setQuery(db.collection("foros").document("Turismo").collection("posts"), Foro.class)
                            .build();
                    adapterTurismo = new adaptadorForos(optionsTurismo, "Turismo");
adapterTurismo.startListening();
                    recyclerViewTurismo.setAdapter( adapterTurismo);

                }
            }
        });
        db.collection("foros").document("Ciencias Ambientales").collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    FirestoreRecyclerOptions<Foro> optionsAMBIENTALES = new FirestoreRecyclerOptions.Builder<Foro>()
                            .setQuery(db.collection("foros").document("Ciencias Ambientales").collection("posts"), Foro.class)
                            .build();
                    adapterCienciasAmbientales = new adaptadorForos(optionsAMBIENTALES, "Ciencias Ambientales");
                    adapterCienciasAmbientales.startListening();
                    recyclerViewCienciasAmbientales.setAdapter(adapterCienciasAmbientales);

                }
            }
        });

        db.collection("foros").document("Audiovisuales").collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    FirestoreRecyclerOptions<Foro> optionsAUDIOVISUALES = new FirestoreRecyclerOptions.Builder<Foro>()
                            .setQuery(db.collection("foros").document("Audiovisuales").collection("posts"), Foro.class)
                            .build();
                    adapterAudiovisuales = new adaptadorForos(optionsAUDIOVISUALES, "Audiovisuales");
                    adapterAudiovisuales.startListening();
                    recyclerViewAudiovisuales.setAdapter(adapterAudiovisuales);


                }
            }
        });
        db.collection("foros").document("Teleco").collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    FirestoreRecyclerOptions<Foro> optionsTELECO = new FirestoreRecyclerOptions.Builder<Foro>()
                            .setQuery(db.collection("foros").document("Teleco").collection("posts"), Foro.class)
                            .build();
                    adapterTeleco = new adaptadorForos(optionsTELECO, "Teleco");
                    adapterTeleco.startListening();
                    recyclerViewTeleco.setAdapter(adapterTeleco);




                }
            }
        });



        spinner = findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this,
                R.array.categorias, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoriaSeleccionada = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        buttonSubmit = findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = editTextLink.getText().toString();
                String description = editTextDescription.getText().toString();
                String categoria = spinner.getSelectedItem().toString();
                if (!link.isEmpty() && !description.isEmpty()) {
                    if (categoriaSeleccionada == null || categoriaSeleccionada.isEmpty()) {
                        Toast.makeText(Foros.this, "Debe seleccionar una categoría", Toast.LENGTH_SHORT).show();
                        return;
                    }
                 else if(link.isEmpty() || description.isEmpty()){
                        Toast.makeText(Foros.this, "Debes rellenar los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Crear un nuevo objeto Foro con los datos de los EditTexts
                    Foro foro = new Foro(link, description, categoria);
                    // Añadir el objeto Foro a la colección correspondiente en Firestore
                    switch (categoria) {
                        case "GTI":
                            // Consultar si el link ya existe en la base de datos
                            db.collection("foros").document("GTI").collection("posts")
                                    .whereEqualTo("link", link)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if (!querySnapshot.isEmpty()) {
                                                    // El link ya existe en la base de datos
                                                    Toast.makeText(getApplicationContext(), "El link ya existe", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // El link no existe en la base de datos, se puede subir
                                                    String descripcion = editTextDescription.getText().toString();
                                                    Map<String, Object> post = new HashMap<>();
                                                    post.put("link", link);
                                                    post.put("descripcion", descripcion);
                                                    db.collection("foros").document(categoriaSeleccionada).collection("posts").add(post);
                                                    Toast.makeText(getApplicationContext(), "Post subido exitosamente", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error al consultar la base de datos",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            break;
                        case "Turismo":
                            // Consultar si el link ya existe en la base de datos
                            db.collection("foros").document("Turismo").collection("posts")
                                    .whereEqualTo("link", link)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if (!querySnapshot.isEmpty()) {
                                                    // El link ya existe en la base de datos
                                                    Toast.makeText(getApplicationContext(), "El link ya existe", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // El link no existe en la base de datos, se puede subir
                                                    String descripcion = editTextDescription.getText().toString();
                                                    Map<String, Object> post = new HashMap<>();
                                                    post.put("link", link);
                                                    post.put("descripcion", descripcion);
                                                    db.collection("foros").document(categoriaSeleccionada).collection("posts").add(post);
                                                    Toast.makeText(getApplicationContext(), "Post subido exitosamente", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error al consultar la base de datos",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            break;
                        case "Ciencias Ambientales":
                            // Consultar si el link ya existe en la base de datos
                            db.collection("foros").document("Ciencias Ambientales").collection("posts")
                                    .whereEqualTo("link", link)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if (!querySnapshot.isEmpty()) {
                                                    // El link ya existe en la base de datos
                                                    Toast.makeText(getApplicationContext(), "El link ya existe", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // El link no existe en la base de datos, se puede subir
                                                    String descripcion = editTextDescription.getText().toString();
                                                    Map<String, Object> post = new HashMap<>();
                                                    post.put("link", link);
                                                    post.put("descripcion", descripcion);
                                                    db.collection("foros").document(categoriaSeleccionada).collection("posts").add(post);
                                                    Toast.makeText(getApplicationContext(), "Post subido exitosamente", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error al consultar la base de datos",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            break;
                        case "Audiovisuales":
                            // Consultar si el link ya existe en la base de datos
                            db.collection("foros").document("Audiovisuales").collection("posts")
                                    .whereEqualTo("link", link)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if (!querySnapshot.isEmpty()) {
                                                    // El link ya existe en la base de datos
                                                    Toast.makeText(getApplicationContext(), "El link ya existe", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // El link no existe en la base de datos, se puede subir
                                                    String descripcion = editTextDescription.getText().toString();
                                                    Map<String, Object> post = new HashMap<>();
                                                    post.put("link", link);
                                                    post.put("descripcion", descripcion);
                                                    db.collection("foros").document(categoriaSeleccionada).collection("posts").add(post);
                                                    Toast.makeText(getApplicationContext(), "Post subido exitosamente", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error al consultar la base de datos",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            break;
                        case "Teleco":
                            // Consultar si el link ya existe en la base de datos
                            db.collection("foros").document("Teleco").collection("posts")
                                    .whereEqualTo("link", link)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if (!querySnapshot.isEmpty()) {
                                                    // El link ya existe en la base de datos
                                                    Toast.makeText(getApplicationContext(), "El link ya existe", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // El link no existe en la base de datos, se puede subir
                                                    String descripcion = editTextDescription.getText().toString();
                                                    Map<String, Object> post = new HashMap<>();
                                                    post.put("link", link);
                                                    post.put("descripcion", descripcion);
                                                    db.collection("foros").document(categoriaSeleccionada).collection("posts").add(post);
                                                    Toast.makeText(getApplicationContext(), "Post subido exitosamente", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error al consultar la base de datos",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                query = s;
                filterRecyclerViews();
                return false;
            }
        });

    }

    private void filterRecyclerViews() {
        // Crear una nueva consulta para GTI
        Query queryGTI = db.collection("foros").document("GTI").collection("posts")
                .whereGreaterThanOrEqualTo("descripcion" , query)
                .whereLessThanOrEqualTo("descripcion" , query + "\uf8ff");

        // Crear una nueva opción de FirestoreRecycler para GTI
        FirestoreRecyclerOptions<Foro> optionsGTI = new FirestoreRecyclerOptions.Builder<Foro>()
                .setQuery(queryGTI , Foro.class)
                .build();
        FirestoreRecyclerOptions<Foro> optionsTURISMO = new FirestoreRecyclerOptions.Builder<Foro>()
                .setQuery(queryGTI , Foro.class)
                .build();
        FirestoreRecyclerOptions<Foro> optionsAMBIENTALES = new FirestoreRecyclerOptions.Builder<Foro>()
                .setQuery(queryGTI , Foro.class)
                .build();
        FirestoreRecyclerOptions<Foro> optionsAUDIOVISUALES = new FirestoreRecyclerOptions.Builder<Foro>()
                .setQuery(queryGTI , Foro.class)
                .build();
        FirestoreRecyclerOptions<Foro> optionsTELECO = new FirestoreRecyclerOptions.Builder<Foro>()
                .setQuery(queryGTI , Foro.class)
                .build();

        // Actualizar el adaptador y la consulta del RecyclerView GTI
        adapterGTI = new adaptadorForos(optionsGTI , "GTI");
        adapterGTI.startListening();
        recyclerViewGTI.setAdapter(adapterGTI);
        adapterTurismo = new adaptadorForos(optionsTURISMO , "Turismo");
        adapterTurismo.startListening();
        recyclerViewTurismo.setAdapter(adapterTurismo);
        adapterCienciasAmbientales = new adaptadorForos(optionsAMBIENTALES , "Ciencias Ambientales");
        adapterCienciasAmbientales.startListening();
        recyclerViewCienciasAmbientales.setAdapter(adapterCienciasAmbientales);
        adapterAudiovisuales = new adaptadorForos(optionsAUDIOVISUALES , "Audiovisuales");
        adapterAudiovisuales.startListening();
        recyclerViewAudiovisuales.setAdapter(adapterAudiovisuales);
        adapterTeleco = new adaptadorForos(optionsTELECO , "Teleco");
        adapterTeleco.startListening();
        recyclerViewTeleco.setAdapter(adapterTeleco);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
    super.onStop();
        adapterGTI.stopListening();
        adapterTurismo.stopListening();
        adapterTeleco.stopListening();
        adapterAudiovisuales.stopListening();
        adapterCienciasAmbientales.stopListening();}
}
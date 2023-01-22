package es.upv.a3c.smartlibrary;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class PerfilUsuario extends AppCompatActivity {
     FirebaseFirestore db;
     EditText NombreUsuario;
    EditText EmailUsuario;
    EditText ContraseñaUsuario;
   FirebaseFirestore fStore;
    FirebaseAuth mAuth;
    private String idUser;
    Button GuardarDatos;

    DocumentReference documentReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario);

       GuardarDatos = findViewById(R.id.Guardarcambios);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser usuario = mAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        NombreUsuario = findViewById(R.id.Nombreusuario);
        EmailUsuario = findViewById(R.id.CorreoUsuario);



            DocumentReference documentReference = fStore.collection("usuarios").document(idUser);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                  NombreUsuario.setText(value.getString("name"));
                  EmailUsuario.setText(value.getString("Email"));
                }
            });





        GuardarDatos.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                ActualizarDatos();
                 Intent intent = new Intent(PerfilUsuario.this,MainActivity.class);
                 startActivity(intent);

             }
         });


    }
    private void ActualizarDatos() {
        String Nombre = NombreUsuario.getText().toString();
        String Email = EmailUsuario.getText().toString();
        String Contraseña = ContraseñaUsuario.getText().toString();

        Map<String, Object> map = new HashMap<>();
        map.put("name", Nombre);
        map.put("Email", Email);

        fStore.collection("usuarios").document(idUser).update(map);
    }
}




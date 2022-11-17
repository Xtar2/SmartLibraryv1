package es.upv.a3c.smartlibrary;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class PerfilUsuario extends AppCompatActivity {
     FirebaseFirestore db;
     EditText NombreUsuario;
    EditText EmailUsuario;
    EditText ContraseñaUsuario;
    FirebaseUser usuario;
    FirebaseAuth firebaseAuth;

    DocumentReference documentReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario);


  db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        usuario = firebaseAuth.getCurrentUser();
        NombreUsuario = findViewById(R.id.Nombreusuario);
        EmailUsuario = findViewById(R.id.CorreoUsuario);
        ContraseñaUsuario = findViewById(R.id.ContraseñaUsuario);

db.collection("usuarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful()){
            for (QueryDocumentSnapshot document : task.getResult()) {
                Log.d(TAG, document.getId() + "" + document.getData());
            }

            }else{
            Log.w(TAG,"Error getting documents", task.getException());
        }
    }
});
    }
}




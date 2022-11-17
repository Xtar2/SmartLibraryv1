package es.upv.a3c.smartlibrary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity  extends AppCompatActivity {
    private EditText txtUsuario;
    private EditText txtMail;
    private EditText txtPassword;
    private Button BtnRegistrar;
    private TextView lblLogin;

    private String userID;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_registro);

        txtUsuario = findViewById(R.id.usuarioregistro);
        txtMail = findViewById(R.id.mailregistro);
        txtPassword = findViewById(R.id.passwordregistro);
        lblLogin = findViewById(R.id.lblLogin);
        BtnRegistrar = findViewById(R.id.btnRegistrar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        BtnRegistrar.setOnClickListener(view -> {
            CrearUsuario();
        });

        lblLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
    });
    }

    // Fin de oncreate

        public void openLoginActivity() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        }

        public void CrearUsuario(){

            String name = txtUsuario.getText().toString();
            String mail = txtMail.getText().toString();
            String password = txtPassword.getText().toString();


            if (TextUtils.isEmpty(name)){
                txtMail.setError("Ingrese un nombre");
                txtMail.requestFocus();
            }else if (TextUtils.isEmpty(mail)) {
                txtMail.setError("Ingrese un correo");
                txtMail.requestFocus();
            }else if (TextUtils.isEmpty(password)) {
                txtMail.setError("Ingrese una contraseña");
                txtMail.requestFocus();
            }else{
                mAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = db.collection("usuarios").document(userID);

                            Map<String,Object> usuario = new HashMap<>();
                            usuario.put("name", name);
                            usuario.put("Email", mail);
                            usuario.put("Contraseña", password);

                            documentReference.set(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                Log.d("TAG","onSuccess: Datos almacenados"+userID);
                                }
                            });
                            Toast.makeText(RegisterActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                        }else{
                            Toast.makeText(RegisterActivity.this, "Usuario no registrado"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }

}
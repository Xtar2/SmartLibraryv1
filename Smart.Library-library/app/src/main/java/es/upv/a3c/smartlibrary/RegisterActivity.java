package es.upv.a3c.smartlibrary;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import com.google.firebase.auth.FirebaseUser;
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



        BtnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             CrearUsuario();
            }
        });





        lblLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
    });
    }
    // Fin de oncreate


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser usuarioactual= mAuth.getCurrentUser();
        //updateUI(usuarioactual);
    }



        public void openLoginActivity() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        }

        public void CrearUsuario(){
            String name = txtUsuario.getText().toString();
            String mail = txtMail.getText().toString();
            String password = txtPassword.getText().toString();
            if (TextUtils.isEmpty(name)){
                txtUsuario.setError("Ingrese un nombre");
                txtUsuario.requestFocus();
            }else if (TextUtils.isEmpty(mail)) {
                txtMail.setError("Ingrese un correo");
                txtMail.requestFocus();
            }else if (TextUtils.isEmpty(password)) {
                txtPassword.setError("Ingrese una contraseña");
                txtPassword.requestFocus();
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
                            usuario.put("Tutorial",false);
                            usuario.put("Tutorial2",false);


                            documentReference.set(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                Log.d("TAG","onSuccess: Datos almacenados"+userID);



                                }
                            });


                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            user.sendEmailVerification();
                            Toast.makeText(RegisterActivity.this , "Correo de Verificación enviado, míralo en tu correo eléctronico y verífica tu identidad" , Toast.LENGTH_LONG).show();
                            Toast.makeText(RegisterActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            Intent intent = (new Intent(RegisterActivity.this,LoginActivity.class));
                            startActivity(intent);





                        }else{
                            Toast.makeText(RegisterActivity.this, "Usuario no registrado"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }



}

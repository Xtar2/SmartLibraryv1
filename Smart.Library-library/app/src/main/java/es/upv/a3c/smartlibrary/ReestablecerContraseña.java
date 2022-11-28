package es.upv.a3c.smartlibrary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ReestablecerContraseña extends AppCompatActivity {

    private EditText mEditTextEmail;
    private Button mBotonReestablecer;
    private  String email = "";
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contrasenaolvidada);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        mEditTextEmail = findViewById(R.id.TextoContrasena);
        mBotonReestablecer = findViewById(R.id.Botonreestablecer);

        mBotonReestablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              email = mEditTextEmail.getText().toString();

              if (!email.isEmpty()){
                  mDialog.setMessage("Espere un momento...");
                  mDialog.setCanceledOnTouchOutside(false);
                  mDialog.show();
                  resetearcontraseña();
              } else{
                  Toast.makeText(ReestablecerContraseña.this,"Debe ingresar un email",Toast.LENGTH_SHORT).show();
              }


            }
        });

    }


    private void resetearcontraseña(){

        mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful())  {
                Toast.makeText(ReestablecerContraseña.this,"Se ha enviado un correo para reestablecer la contraseña",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(ReestablecerContraseña.this,"No se ha podido enviar el correo",Toast.LENGTH_SHORT).show();
            }
            mDialog.dismiss();
            }
        });


    }

}

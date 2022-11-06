package es.upv.a3c.smartlibrary;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class EliminarUsuarioActivity extends AppCompatActivity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_usuario);

        Button botonSi = findViewById(R.id.botonSi);
        Button botonNo = findViewById(R.id.botonNo);


        botonSi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                continuarEliminarCuenta(null);
            }
        });

        botonNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });

    }

    public void continuarEliminarCuenta(View view) {




    }
}

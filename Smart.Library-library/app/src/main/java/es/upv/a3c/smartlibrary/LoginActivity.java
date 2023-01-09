package es.upv.a3c.smartlibrary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthCredential;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    int modeNightYes = AppCompatDelegate.MODE_NIGHT_YES;
    private FirebaseAuth mAuth;
    private EditText txtMail;
    private EditText txtPassword;
    private boolean NuevoUsuario;
    private Button btnLogin;
    private TextView lblRegister;
    private FirebaseFirestore db;
    private TextView contraseñaolvidada;
    String TAG = "GoogleSignInLoginActivity";
    private GoogleSignInClient mGoogleSingInClient;
    private Button btnSignIn;
    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        NuevoUsuario = false;
        contraseñaolvidada = findViewById(R.id.olvidadacontrasena);
        txtMail = findViewById(R.id.mailLogin);
        txtPassword = findViewById(R.id.txtPassword);
        lblRegister = findViewById(R.id.lblregister);
        btnLogin = findViewById(R.id.btnLogin);
        db = FirebaseFirestore.getInstance();



        lblRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterActivity();
            }
        });

        btnLogin.setOnClickListener(view -> {
            userLogin();
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSingInClient = GoogleSignIn.getClient(this , gso);
        mAuth = FirebaseAuth.getInstance();
        btnSignIn = findViewById(R.id.GoogleButton);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });


        contraseñaolvidada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this , ReestablecerContraseña.class);
                startActivity(intent);
            }
        });

    }



//Fin del oncreate


public  void añadirFirestore(String usertype) {
        Map<String, Object> usuarioGoogle = new HashMap<>();
        usuarioGoogle.put("name", mAuth.getCurrentUser().getDisplayName());
        usuarioGoogle.put("Email", mAuth.getCurrentUser().getEmail());

        db.collection("usuarios").document(mAuth.getCurrentUser().getUid()).set(usuarioGoogle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("", "Nuevo usuario en firestore");
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("", "Error al añadir a firestore");
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()){
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    mAuth.signInWithEmailAndPassword(account.getEmail(),"123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                            }else{
                                Log.w("TAG","Error",task.getException());

                            }
                            NuevoUsuario = true;
                        }
                    });
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                }catch (ApiException e) {
                    Log.w(TAG,"Google sign in failed",e);
                }
            } else {
                Log.d(TAG,"error, login no exitoso" + task.getException().toString());
                Toast.makeText(this,"Ocurrió un error "+task.getException().toString(),Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void firebaseAuthWithGoogle (String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                        Log.d(TAG,"signInWithCredential:success");
                        if (NuevoUsuario){
                        añadirFirestore("users");


                        }
                        Intent dashboardActivity = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(dashboardActivity);
                        LoginActivity.this.finish();
                        } else{
                            Log.w(TAG,"signInWithCredential:failure");
                        }
                        }
                });

    }

    @Override
    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
        Intent dashboardActivity = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(dashboardActivity);
        }
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @SuppressWarnings("deprecation")
    private void SignIn(){
        Intent signInIntent = mGoogleSingInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
}

    public void openRegisterActivity(){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }


    public void userLogin() {
        String mail = txtMail.getText().toString();
        String password = txtPassword.getText().toString();

        FirebaseUser user = mAuth.getCurrentUser();


        if (TextUtils.isEmpty(mail)) {
            txtMail.setError("Ingrese un correo");
            txtMail.requestFocus();


        } else if
        (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this , "Ingrese una contraseña" , Toast.LENGTH_SHORT).show();
            txtPassword.requestFocus();
        } else {

                mAuth.signInWithEmailAndPassword(mail , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                Toast.makeText(LoginActivity.this , "Bienvenido" , Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this , "Autentficación fallida" , Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Check if the error is caused by an invalid email address
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // Display an error message if the email address is invalid
                                Toast.makeText(LoginActivity.this, "Dirección de correo electrónico no válida o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Revise su correo electrónico para verificarse", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        }
    }



    private void updateUI(FirebaseUser user) {
        if (user != null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);

        }else{
            Toast.makeText(LoginActivity.this,"Logueate para continuar",Toast.LENGTH_SHORT).show();
        }
    }


}



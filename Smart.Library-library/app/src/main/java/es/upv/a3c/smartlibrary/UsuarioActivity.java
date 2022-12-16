package es.upv.a3c.smartlibrary;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class UsuarioActivity extends AppCompatActivity {
    String[] requestPermission = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE};
    List<String> permissionList = new ArrayList<>();
    private NetworkImageView fotoUsuario;
    private FirebaseUser usuario;
    FirebaseAuth mAuth;
    private String idUser;
    private  FirebaseFirestore fStore;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

         usuario = FirebaseAuth.getInstance().getCurrentUser();

// Esto se hace para cargar de la base de datos el nombre y el email
        TextView nombre = findViewById(R.id.nombre);
        TextView email = findViewById(R.id.email);
        @SuppressLint({"MissingInflatedId" , "LocalSuppress"}) ImageView mapa = findViewById(R.id.mapita);
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fStore.collection("usuarios").document(idUser);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                nombre.setText(value.getString("name"));
                email.setText(value.getString("Email"));

            }
        });
mapa.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        lanzarMapa(view);
    }
});


        // Inicialización Volley (Hacer solo una vez en Singleton o Applicaction)
        RequestQueue colaPeticiones = Volley.newRequestQueue(
                getApplicationContext());
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                });
// Foto de usuario
        Uri urlImagen = usuario.getPhotoUrl();
         fotoUsuario = (NetworkImageView)
                findViewById(R.id.imagen);
        fotoUsuario.setDefaultImageResId(R.drawable.head);
        if (urlImagen != null) {

            fotoUsuario.setImageUrl(urlImagen.toString(), lectorImagenes);
        }
        loadUrl();







        Button botonPerfil = findViewById(R.id.BotonPerfil);
        Button botonReservas = findViewById(R.id.botonReservas);
        Button botonEstadisticas = findViewById(R.id.botonEstadisticas);
        TextView eliminarCuenta = findViewById(R.id.eliminarCuenta);
        fotoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionList.clear();
                for (int i = 0; i < requestPermission.length; i++) {
                    if (ContextCompat.checkSelfPermission(UsuarioActivity.this, requestPermission[i]) != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(requestPermission[i]);
                    }
                }
                if (permissionList.isEmpty()) {
                    takePhoto();
                } else {
                    String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                    ActivityCompat.requestPermissions(UsuarioActivity.this, permissions, 101);
                }
            }
        });
        botonReservas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarMisReservas(null);
            }
        });

        botonPerfil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarMiPerfil(null);
            }
        });

        botonEstadisticas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarMisEstadisticas(null);
            }
        });

        eliminarCuenta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarEliminarCuenta(null);
            }
        });

    }



    private void loadUrl() {
        if (usuario!=null&& !TextUtils.isEmpty(usuario.getUid())) {
            DatabaseReference myDf = FirebaseDatabase.getInstance().getReference().child("User").child(usuario.getUid());
            myDf.addValueEventListener(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String  img = dataSnapshot.getValue(String.class);
                    RequestQueue colaPeticiones = Volley.newRequestQueue(
                            getApplicationContext());
                    ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
                            new ImageLoader.ImageCache() {
                                private final LruCache<String, Bitmap> cache =
                                        new LruCache<String, Bitmap>(10);
                                public void putBitmap(String url, Bitmap bitmap) {
                                    cache.put(url, bitmap);
                                }
                                public Bitmap getBitmap(String url) {
                                    return cache.get(url);
                                }
                            });
                    fotoUsuario.setImageUrl(img,lectorImagenes);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void takePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 2);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case 2:
                    final Uri uri = data.getData();
                    upload (uri);
                    break;

                default:
                    break;
            }
        }

    }

    private void upload(Uri uri) {
        StorageReference mStoreReference = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStoreReference.child(System.currentTimeMillis()+".jpg");
        UploadTask uploadTask = riversRef.putFile(uri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String url = downloadUri.toString();
                    RequestQueue colaPeticiones = Volley.newRequestQueue(
                            getApplicationContext());
                    ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
                            new ImageLoader.ImageCache() {
                                private final LruCache<String, Bitmap> cache =
                                        new LruCache<String, Bitmap>(10);
                                public void putBitmap(String url, Bitmap bitmap) {
                                    cache.put(url, bitmap);
                                }
                                public Bitmap getBitmap(String url) {
                                    return cache.get(url);
                                }
                            });
                    fotoUsuario.setImageUrl(url,lectorImagenes);
                    if (usuario!=null&& !TextUtils.isEmpty(usuario.getUid())) {
                        FirebaseDatabase.getInstance().getReference().child("User").child(usuario.getUid())
                                .setValue(url);
                    }
                } else {

                }
            }
        });
    }

    public void lanzarMisReservas(View view){
        Intent i = new Intent(this, ReservasActivity.class);
        startActivity(i);
    }

    public void lanzarMisEstadisticas(View view){
        Intent i = new Intent(this, EstadisticasActivity.class);
        startActivity(i);
    }
    public void lanzarMiPerfil(View view){
        Intent i = new Intent(this,PerfilUsuario.class);
        startActivity(i);
    }
    public void lanzarMapa(View view){
        Intent i = new Intent(this,mapamaps.class);
        startActivity(i);
    }

    public void lanzarEliminarCuenta(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Seguro que quieres eliminar tu cuenta? Se perderán todos tus datos.")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            FirebaseAuth.getInstance().getCurrentUser().delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User account deleted.");
                                                AuthUI.getInstance().signOut(getApplicationContext())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Intent i = new Intent(
                                                                        getApplicationContext(), LoginActivity.class);
                                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                                        | Intent.FLAG_ACTIVITY_NEW_TASK
                                                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.show();

    }

    public void cerrarSesion(View view) {
        AuthUI.getInstance().signOut(getApplicationContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent i = new Intent(
                                getApplicationContext (),LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                });
    }

}


package es.upv.a3c.smartlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String[] nombres;
    FirebaseUser usuario;
    FloatingActionButton fab3;
    FloatingActionsMenu MenuBoton;
    SearchView Buscar;
    private NetworkImageView fotoUsuario;
    private FirebaseAuth mAuth;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        //establecer campos


        usuario = FirebaseAuth.getInstance().getCurrentUser();
        TextView nombre = findViewById(R.id.nombre4);
        nombre.setText(usuario.getDisplayName());



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
        Uri urlImagen = usuario.getPhotoUrl();
        fotoUsuario = (NetworkImageView)
                findViewById(R.id.imagen);
        fotoUsuario.setDefaultImageResId(R.drawable.head);
        if (urlImagen != null) {

            fotoUsuario.setImageUrl(urlImagen.toString(), lectorImagenes);
        }
        loadUrl();



// Esconder toolbar
getSupportActionBar().hide();


//BOTON FLOTANTE
        MenuBoton = findViewById(R.id.grupofab);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent miIntent= new Intent(MainActivity.this,AcercadeActivity.class);
            startActivity(miIntent);
MenuBoton.collapse();
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent miIntent= new Intent(MainActivity.this,UsuarioActivity.class);
                startActivity(miIntent);
                MenuBoton.collapse();
            }
        });
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuBoton.collapse();
                cerrarSesion();
            }

        });

        usuario = FirebaseAuth.getInstance().getCurrentUser();
        nombres = new String[]{"Libros", "Plazas", "Cabinas"};
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MiPagerAdapter(this));
        TabLayout tabs = findViewById(R.id.tabs);


        new TabLayoutMediator(tabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position){
                        tab.setText(nombres[position]);

                    }
                }
        ).attach();

        //ICONOS TABS
        tabs.getTabAt(0).setIcon(R.drawable.libroabierto2);
        tabs.getTabAt(2).setIcon(R.drawable.cabina_de_entradas);
        tabs.getTabAt(1).setIcon(R.drawable.sentado_en_una_silla);


// TABS SELECCIONADOS
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }
    //------------------------------
    //Fin del metodo Create
    //------------------------------


    @Override
    protected void onStart() {
        super.onStart();
       usuario = mAuth.getCurrentUser();
       if (usuario == null){
           startActivity(new Intent(MainActivity.this,LoginActivity.class));
       }
    }

    //  FOTO DE PERFIL
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





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.acerca_de) {
            Intent intent = new Intent(this, AcercadeActivity.class);
            startActivity(intent);
        }


        if (id == R.id.fab3) {
            cerrarSesion();
        }

        return super.onOptionsItemSelected(item);
    }


//Para cerrar sesion
    public void cerrarSesion() {
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

//Para ver los tabs
    public class MiPagerAdapter extends FragmentStateAdapter {
        public MiPagerAdapter(FragmentActivity activity) {
            super(activity);
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        @Override
        @NonNull
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new Libros();
                case 1:
                    return new Cabinas();
                case 2:
                    return new Plazas();
            }
            return null;
        }


    }


}



package es.upv.a3c.smartlibrary;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
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
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    String[] nombres;
    FirebaseUser usuario;
    FloatingActionButton fab3;
    FloatingActionsMenu MenuBoton;
    private FloatingActionButton fab0;
    SearchView Buscar;
    private NetworkImageView fotoUsuario;
    private FirebaseAuth mAuth;
    private String idUser;
    private  FirebaseFirestore fStore;
    boolean isOpen = true;
    TabLayout tabs;
    private boolean isFirstTime = true;
    // Iniciamos el contador en 0
    private int counter = 0;

    // Variables para almacenar el tiempo de inicio del contador
    private long startTime;
    private boolean serviceStarted = false;
    // Referencia a la base de datos
    private DatabaseReference mDatabase;
    @SuppressLint("MissingInflatedId")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser();




        // Inicialización Volley (Hacer solo una vez en Singleton o Applicaction)
        RequestQueue colaPeticiones = Volley.newRequestQueue(
                getApplicationContext());
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones ,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);

                    public void putBitmap(String url , Bitmap bitmap) {
                        cache.put(url , bitmap);
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

            fotoUsuario.setImageUrl(urlImagen.toString() , lectorImagenes);
        }
        loadUrl();

 listen_db();

        //establecer campos
        TextView nombre = findViewById(R.id.nombre4);
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fStore.collection("usuarios").document(idUser);
        documentReference.addSnapshotListener(this , new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value , @Nullable FirebaseFirestoreException error) {
                nombre.setText(value.getString("name"));
            }
        });


// Esconder toolbar
        getSupportActionBar().hide();


//BOTON FLOTANTE
        MenuBoton = findViewById(R.id.grupofab);
        fab0 = (FloatingActionButton) findViewById(R.id.fab0);
        fab0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    fab0.setTitle("deshabilitar");
                    fab0.setIcon(R.drawable.ic_close);
                    fab0.setColorNormalResId(R.color.ColorPrincipalSuave);
                    isOpen = false;
                } else {
                    isOpen = true;
                    fab0.setTitle("habilitar");
                    fab0.setIcon(R.drawable.ic_baseline_favorite_24);
                    fab0.setColorNormalResId(R.color.ColorPrincipalSuave);
                }

                MenuBoton.collapse();
            }
        });
        MenuBoton = findViewById(R.id.grupofab);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent miIntent = new Intent(MainActivity.this , AcercadeActivity.class);
                startActivity(miIntent);
                MenuBoton.collapse();
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent miIntent = new Intent(MainActivity.this , UsuarioActivity.class);
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
        nombres = new String[]{"Libros" , "Plazas" , "Cabinas"};
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MiPagerAdapter(this));
          tabs = findViewById(R.id.tabs);


        new TabLayoutMediator(tabs , viewPager ,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab , int position) {
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

                }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Obtener la ID del usuario actual
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)  {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists() && !document.getBoolean("Tutorial")) {
                        // Mostrar tutorial
                        TapTargetView.Listener listener = new TapTargetView.Listener() {
                            @Override
                            public void onTargetClick(TapTargetView view) {
                                super.onTargetClick(view);
                                // Mostrar tutorial en el siguiente tab
                                showChairReservationTutorial();
                            }
                        };
                        TapTargetView.showFor(MainActivity.this ,                 // `this` is an Activity
                                TapTarget.forView(tabs.getTabAt(0).view , "Reservar libros" , "En esta pestaña puedes reservar libros, eligiendo el libro que quieres y el dia que lo deseas" + "Ademas cuenta con un boton para que te toque un libro aleatorio")
                                        // All options below are optional
                                        .outerCircleColor(R.color.ColorPrincipalSuave)      // Specify a color for the outer circle
                                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                        .titleTextSize(20)                  // Specify the size (in sp) of the
                                        .titleTextColor(R.color.white) // Specify the color of the title text
                                        .descriptionTextSize(15) // Specify the size (in sp) of the description text
                                        .descriptionTextColor(R.color.white) // Specify the color of the description text
                                        .textColor(R.color.white) // Specify a color for both the title and description text
                                        .textTypeface(Typeface.SANS_SERIF) // Specify a typeface for the text
                                        .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
                                        .drawShadow(true) // Whether to draw a drop shadow or not
                                        .cancelable(false) // Whether tapping outside the outer circle dismisses the view
                                        .tintTarget(true) // Whether to tint the target view's color
                                        .transparentTarget(false) // Specify whether the target is transparent (displays the content underneath)
                                        .targetRadius(60) , // Specify the target radius (in dp)
                                listener);
                    }
                }
            }
                });
        initNOtification();
    }





    private void listen_db() {

        FirebaseDatabase.getInstance().getReference()
                .child("Sillas").child("Silla1").child("Ultrasonidos")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String value = snapshot.getValue().toString();
                        if (value.equals("1")) {
                         start_counter();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    // Funcion para iniciar el contador
    private void start_counter() {
        counter = 1;
        startTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (counter == 1) {
                    // Calculamos la diferencia entre el tiempo actual y el tiempo de inicio del contador
                    long time_diff = System.currentTimeMillis() - startTime;
                    // Si la diferencia es mayor o igual a 1 minuto
                    if (time_diff >= 6000) {
                        // Enviamos la notificacion y detenemos el contador
                        send_notification();
                        stop_counter();
                        break;
                    }
                }
            }
        }).start();
    }

    // Funcion para detener el contador
    private void stop_counter() {
        counter = 0;
    }


    // Funcion para enviar la notificacion
    private void send_notification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Notification", "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "Notification");
        builder.setContentTitle("Lleva demasiado tiempo sentado");
        builder.setContentText("Deberia levantarse y dar un pequeño paseo");
        builder.setSmallIcon(R.drawable.iconoapp);
        builder.setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
        managerCompat.notify(new Random().nextInt(100000), builder.build());
    }




    private void initNOtification() {
        FirebaseDatabase.getInstance().getReference()
                .child("Sillas").child("Silla1").child("Espalda")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String value = snapshot.getValue().toString();
                        if (value.equals("0")) {
                            launchPush("Mala Postura", "Mantenga una buena postura en la silla");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        Task<QuerySnapshot> querySnapshotTask = fStore.collection("usuarios").document(
                        FirebaseAuth.getInstance().getUid()).collection("Mis Libros")
                .get();
        querySnapshotTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot result = task.getResult();
                if(result.getDocuments().isEmpty())
                    return;
                String string = result.getDocuments().get(0).getData().get("fecha").toString();
                String[] split = string.split("/");
                long l1 = dateToStamp(split[2]
                        + "-" + (split[1].length() > 1 ? split[1] : ("0" + split[1]))
                        + "-" + (split[0].length() > 1 ? split[0] : ("0" + split[0])));
                long l2 = System.currentTimeMillis();
                if (l1-l2<(24L*60L*60L*1000L)&&l1-l2>=0){
                    launchPush("Devolución del libro","Por favor devuelva los libros dentro de las 24 horas.");
                }

            } else {
                Log.d("TAG", "get failed with ", task.getException());
            }
        });


        Task<QuerySnapshot> querySnapshotTask2 = fStore.collection("Cabinas").document(
                        "Cabina1")
                .collection("Reservas")
                .get();
        querySnapshotTask2.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot result = task.getResult();
                Iterator<QueryDocumentSnapshot> iterator = result.iterator();
                while (iterator.hasNext()){
                    QueryDocumentSnapshot next = iterator.next();
                    Map<String, Object> data = next.getData();
                    if (data.get("idusuario").equals(FirebaseAuth.getInstance().getUid())){
                        long fecha = (long) data.get("fecha");
                        long l2 = System.currentTimeMillis();
                        if (fecha-l2<(24L*60L*60L*1000L)&&fecha-l2>=0){
                            launchPush("Reserva de cabina","Mañana tienes una reserva  en Cabinas");
                        }
                    }
                }

            } else {
                Log.d("TAG", "get failed with ", task.getException());
            }
        });
    }

    public long dateToStamp(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        return ts;
    }

    public void launchPush(String title, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Notification", "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "Notification");
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setSmallIcon(R.drawable.iconoapp);
        builder.setColorized(true);
        builder.setColor(26614);
        builder.setAutoCancel(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
        managerCompat.notify(new Random().nextInt(100000), builder.build());
    }

    //todo  add  notification  end





    private void showChairReservationTutorial() {
        TapTargetView.showFor(this,                 // `this` es la activity actual
                TapTarget.forView(tabs.getTabAt(1).view, "Reservar sillas", "En esta pestaña puedes reservar sillas, eligiendo la silla que deseas y la hora a la que quieres estar")
                                .tintTarget(false), // No cambia el color del botón
                        new TapTargetView.Listener() { // Listener para cuando el tutorial es terminado o cancelado
                            @Override
                            public void onTargetClick(TapTargetView view) {
                                super.onTargetClick(view);
// Mostrar tutorial en el siguiente tab
                                showCabinReservationTutorial();
                            }
                            @Override
                            public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                                super.onTargetDismissed(view, userInitiated);
                            }
                        });
    }


    private void showCabinReservationTutorial() {
        TapTargetView.showFor(this,                 // `this` es la activity actual
                TapTarget.forView(tabs.getTabAt(2).view, "Reservar cabinas", "En esta pestaña puedes reservar cabinas, eligiendo la cabina, el dia y la hora que deseas")
                        .tintTarget(false), // No cambia el color del botón
                new TapTargetView.Listener() { // Listener para cuando el tutorial es terminado o cancelado
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                       BotonTutorial();

                    }
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                        super.onTargetDismissed(view, userInitiated);
                    }
                });
    }

    private void BotonTutorial() {
        TapTargetView.showFor(this,
                TapTarget.forView(findViewById(R.id.fab3), "Menú de opciones", "Presiona aquí para desplegar las opciones disponibles")
                        .tintTarget(false),
                new TapTargetView.Listener() { // listener para cuando el tutorial es terminado o cancelado
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        // Desplegar opciones y mostrar tutorial de cada botón flotante
                        MenuBoton.expand();
                        TapTargetView.showFor(MainActivity.this,
                                TapTarget.forView(findViewById(R.id.fab1), "Acerca de", "Aquí encontrarás información sobre los desarrolladores de esta aplicación")
                                        .tintTarget(false),
                                new TapTargetView.Listener() { // listener para cuando el tutorial es terminado o cancelado
                                    @Override
                                    public void onTargetClick(TapTargetView view) {
                                        super.onTargetClick(view);
                                        TapTargetView.showFor(MainActivity.this,
                                                TapTarget.forView(findViewById(R.id.fab2), "Perfil de usuario", "Aquí podrás ver y modificar tus datos personales")
                                                        .tintTarget(false),
                                                new TapTargetView.Listener() { // listener para cuando el tutorial es terminado o cancelado
                                                    @Override
                                                    public void onTargetClick(TapTargetView view) {
                                                        super.onTargetClick(view);
                                                        TapTargetView.showFor(MainActivity.this,
                                                                TapTarget.forView(findViewById(R.id.fab3), "Cerrar sesión", "Presiona aquí para cerrar tu sesión actual")
                                                                        .tintTarget(false));

                                                        Map<String, Object> map = new HashMap<>();
                                                        map.put("Tutorial", true );
                                                        fStore.collection("usuarios").document(idUser).update(map);
                                                    }
                                                });
                                    }
                                });
                    }
                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                        super.onTargetDismissed(view, userInitiated);
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

    @Override
    protected void onStop() {
        super.onStop();

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
                case 1 :

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
                    return new CabinasFragment();
                case 2:
                    return new Plazas();
            }
            return null;
        }
    }


}



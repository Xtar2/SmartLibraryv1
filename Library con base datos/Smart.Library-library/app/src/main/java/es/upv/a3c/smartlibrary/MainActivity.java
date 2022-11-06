package es.upv.a3c.smartlibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import com.firebase.ui.auth.AuthUI;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;

public class MainActivity extends AppCompatActivity {

    String[] nombres;
    FirebaseUser usuario;
    FloatingActionButton fab3;
    FloatingActionsMenu MenuBoton;
    SearchView Buscar;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




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
        tabs.getTabAt(0).setIcon(R.drawable.libroabierto2);
        tabs.getTabAt(2).setIcon(R.drawable.cabina_de_entradas);
        tabs.getTabAt(1).setIcon(R.drawable.sentado_en_una_silla);

     

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (usuario.getEmail() == null) {
            getMenuInflater().inflate(R.menu.menu_anonimo, menu);
            return true;

        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_usuario) {
            Intent intent = new Intent(this, UsuarioActivity.class);
            startActivity(intent);
        }

        if (id == R.id.menu_login_anonimo) {
            cerrarSesion();


        }

        if (id == R.id.acerca_de) {
            Intent intent = new Intent(this, AcercadeActivity.class);
            startActivity(intent);
        }



        if (id == R.id.fab3) {
            cerrarSesion();
        }

        return super.onOptionsItemSelected(item);
    }


    public void CodigoBarrasActivo () {
    new IntentIntegrator(this).initiateScan();
    }

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

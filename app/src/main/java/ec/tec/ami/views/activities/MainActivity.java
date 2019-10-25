package ec.tec.ami.views.activities;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import ec.tec.ami.R;
import ec.tec.ami.views.adapters.SectionsPagerAdapter;
import ec.tec.ami.views.fragments.AmigosFragment;
import ec.tec.ami.views.fragments.BusquedaFragment;
import ec.tec.ami.views.fragments.NotificationsFragment;
import ec.tec.ami.views.fragments.PerfilFragment;
import ec.tec.ami.views.fragments.TimeLineFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPagerAdapter sectionsPageAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.view_pager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TimeLineFragment(),"TimeLine");
        adapter.addFragment(new PerfilFragment(),"Perfil");
        adapter.addFragment(new AmigosFragment(),"Amigos");
        adapter.addFragment(new BusquedaFragment(),"Busqueda");
        adapter.addFragment(new NotificationsFragment(),"Notificaciones");
        viewPager.setAdapter(adapter);
    }
}
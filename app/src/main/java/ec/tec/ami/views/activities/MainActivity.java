package ec.tec.ami.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

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

    private Button btnLogout;

    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.view_pager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupIcons(tabLayout);

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                if(account != null){
                    signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getResources().getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
                    signInClient =  GoogleSignIn.getClient(MainActivity.this, signInOptions);
                    signInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                }else{
                    finish();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TimeLineFragment(),"");
        adapter.addFragment(new PerfilFragment(),"");
        adapter.addFragment(new AmigosFragment(),"");
        adapter.addFragment(new BusquedaFragment(),"");
        adapter.addFragment(new NotificationsFragment(),"");
        viewPager.setAdapter(adapter);
    }

    private void setupIcons(TabLayout tabLayout){
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_access_time_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_account_circle_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_group_black_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_search_black_24dp);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_notifications_black_24dp);
    }
}
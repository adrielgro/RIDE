package dev.adrielgro.ride;

import android.app.Activity;
import android.app.Dialog;
//import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity
        implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    Context context = this;

    FloatingSearchView mSearchView;
    MenuItem mActionVoice;
    TextView textViewName;
    TextView textViewEmail;


    NavigationView navigationView;
    View headerView;


    private boolean doubleBackToExitPressedOnce = false;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Remove notification bar

        setContentView(R.layout.activity_map);

        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        textViewName = (TextView) headerView.findViewById(R.id.textViewName);
        textViewEmail = (TextView) headerView.findViewById(R.id.textViewEmail);

        // Ubicar posicion en el GPS
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Buscando posición actual...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // Menu lateral
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.setOnLeftMenuClickListener(
                new FloatingSearchView.OnLeftMenuClickListener() {

                    @Override
                    public void onMenuOpened() {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }

                    @Override
                    public void onMenuClosed() {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                });
        mSearchView.attachNavigationDrawerToMenuButton(mDrawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Reconocimiento por voz
        /*mActionVoice = (MenuItem) findViewById(R.id.action_voice);
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                if (item.getItemId() == R.id.action_voice) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    //... put other settings in the Intent
                    startActivityForResult(intent, 0);
                }
            }
        });*/



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if(status == ConnectionResult.SUCCESS) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity)getApplicationContext(), 10);
            dialog.show();
        }



        textViewEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        float zoomLevel = 16;
        LatLng myPosition = new LatLng(31.820360, -116.591149);

        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(myPosition).title("Tu posición"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Presiona nuevamente atrás para salir", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_inbox) {
            Intent intent = new Intent(context, ChatActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_paymethod) {

        } else if (id == R.id.nav_driver) {

        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_report) {

        } else if (id == R.id.nav_config) {

        } else if(id == R.id.nav_manage) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void checkVoiceRecognition() {
        // Check if voice recognition is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            Toast.makeText(this, "Reconocimiento de voz no disponible",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            mSearchView.setSearchFocused(true);
            mSearchView.setSearchText(results.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

package com.app.yoursingleradio.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.yoursingleradio.AudioVisualizationFragment;
import com.cleveroad.audiovisualization.AudioVisualization;
import com.app.yoursingleradio.Config;
import com.app.yoursingleradio.R;
import com.app.yoursingleradio.fragments.FragmentRadio;
import com.app.yoursingleradio.fragments.FragmentRadioAdminPanel;
import com.app.yoursingleradio.utilities.GDPR;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final static String COLLAPSING_TOOLBAR_FRAGMENT_TAG = "collapsing_toolbar";
    private final static String SELECTED_TAG = "selected_index";
    private final static int COLLAPSING_TOOLBAR = 0;
    private static int selectedIndex;
    private boolean shouldOpenFragment;

    private static final int REQUEST_CODE = 1;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public static String FRAGMENT_DATA = "transaction_data";
    public static String FRAGMENT_CLASS = "transation_target";
    private InterstitialAd interstitialAd;
    private AdView adView;
    private static final int PERMISSION= 12;

    private AudioVisualization audioVisualization;

    private FragmentRadio fragmentRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*
        if (savedInstanceState == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                openFragment();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MODIFY_AUDIO_SETTINGS)) {
                    AlertDialog.OnClickListener onClickListener = (dialog, which) -> {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            requestPermissions();
                        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                            permissionsNotGranted();
                        }
                    };
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.title_permissions))
                            .setMessage(Html.fromHtml(getString(R.string.message_permissions)))
                            .setPositiveButton(getString(R.string.btn_next), onClickListener)
                            .setNegativeButton(getString(R.string.btn_cancel), onClickListener)
                            .show();
                } else {
                    requestPermissions();
                }
            }
        }
*/
        if (savedInstanceState == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                openFragment();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MODIFY_AUDIO_SETTINGS)) {
                    AlertDialog.OnClickListener onClickListener = (dialog, which) -> {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            requestPermissions();
                        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                            permissionsNotGranted();
                        }
                    };
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.title_permissions))
                            .setMessage(Html.fromHtml(getString(R.string.message_permissions)))
                            .setPositiveButton(getString(R.string.btn_next), onClickListener)
                            .setNegativeButton(getString(R.string.btn_cancel), onClickListener)
                            .show();
                } else {
                    requestPermissions();
                }
            }
        }


        loadInterstitialAd();

        if (savedInstanceState != null) {
//            navigationView.getMenu().getItem(savedInstanceState.getInt(SELECTED_TAG)).setChecked(true);
            return;
        }

        if (!Config.ENABLE_SOCIAL_MENU) {
            Menu navigation_menu = navigationView.getMenu();
            navigation_menu.findItem(R.id.drawer_social).setVisible(false);
        }

        selectedIndex = COLLAPSING_TOOLBAR;

        if (Config.ENABLE_ADMIN_PANEL) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FragmentRadioAdminPanel(), COLLAPSING_TOOLBAR_FRAGMENT_TAG).commit();
        } else {
            fragmentRadio = new FragmentRadio();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragmentRadio, COLLAPSING_TOOLBAR_FRAGMENT_TAG).commit();
        }

        loadAdMobBannerAd();

        GDPR.updateConsentStatus(this);

    }

    public void stopRadio(){
        if(fragmentRadio!=null){
            fragmentRadio.stopService();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAG, selectedIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //return true;
        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode ==  REQUEST_CODE){
            if(grantResults.length>0){
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]== PackageManager.PERMISSION_GRANTED)
                    openFragment();
            }
        }
    }

    private void permissionsNotGranted() {
        Toast.makeText(this, R.string.toast_permissions_not_granted, Toast.LENGTH_SHORT).show();
        finish();
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS},
                REQUEST_CODE
        );
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.drawer_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_social:
                startActivity(new Intent(getApplicationContext(), ActivitySocial.class));
                showInterstitialAd();
                return true;

            case R.id.drawer_rate:
                final String appName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                }
                showInterstitialAd();
                return true;

            case R.id.drawer_more:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
                showInterstitialAd();
                return true;

            case R.id.drawer_about:
                startActivity(new Intent(getApplicationContext(), ActivityAbout.class));
                showInterstitialAd();
                return true;

            case R.id.drawer_privacy:
                if (Config.ENABLE_ADMIN_PANEL) {
                    startActivity(new Intent(getApplicationContext(), ActivityPrivacyPolicy.class));
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url))));
                }
                showInterstitialAd();
                return true;

        }
        return false;
    }


    public interface OnBackClickListener {
        boolean onBackClick();
    }

    private OnBackClickListener onBackClickListener;

    public void setOnBackClickListener(OnBackClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
    }


    private void openFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, AudioVisualizationFragment.newInstance())
                .commit();
    }


    @Override
    protected void onResume()
    {
            super.onResume();
        if (shouldOpenFragment) {
            shouldOpenFragment = false;
            openFragment();
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (onBackClickListener != null && onBackClickListener.onBackClick()) {
                return;
            }
            super.onBackPressed();
        }
    }

    private void loadAdMobBannerAd() {
        adView = (AdView) findViewById(R.id.adView);
        adView.setVisibility(View.INVISIBLE);

        if (Config.ENABLE_ADMOB_BANNER_ADS) {
            adView.loadAd(GDPR.getAdRequest(this));
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    adView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdLoaded() {
                    adView.setVisibility(View.VISIBLE);
                }
            });

        } else {
            adView.setVisibility(View.GONE);
            Log.d("Log", "Admob Banner is Disabled");
        }
    }

    private void loadInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS_ON_DRAWER_SELECTION) {
            interstitialAd = new InterstitialAd(getApplicationContext());
            interstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_unit_id));
            interstitialAd.loadAd(GDPR.getAdRequest(this));
        } else {
            Log.d("INFO", "AdMob Interstitial is Disabled");
        }
    }


    private void showInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS_ON_DRAWER_SELECTION) {
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                interstitialAd.show();
            }
        } else {
            Log.d("INFO", "AdMob Interstitial is Disabled");
        }
    }

}

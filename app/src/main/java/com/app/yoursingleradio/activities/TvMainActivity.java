package com.app.yoursingleradio.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.app.Application;
import android.app.PictureInPictureParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.app.yoursingleradio.R;
import com.app.yoursingleradio.fragments.TvMainFragment;
import com.app.yoursingleradio.utilities.Log;


public class TvMainActivity extends AppCompatActivity {
    private boolean isFullScreen = false;
    TvMainFragment frag;
    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;
    private boolean isPIP = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_main);
        toolbar = findViewById(R.id.toolbarTvMain);
        setSupportActionBar(toolbar);
        coordinatorLayout = findViewById(R.id.coordinatorTvMain);
        frag = new TvMainFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerTvMain, frag)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.e("Menu", "menuuu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.share){
            Intent sendInt = new Intent(Intent.ACTION_SEND);
            sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendInt.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
            sendInt.setType("text/plain");
            startActivity(Intent.createChooser(sendInt, "Share"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(isFullScreen){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isFullScreen = false;
            if(frag!=null){
                frag.setFullScreen(false);
            }
        }else {
            exitDialog();
        }
    }


    public void exitDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(getResources().getString(R.string.message));
        dialog.setPositiveButton(getResources().getString(R.string.quit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(frag!=null){
                    frag.stop();
                }
                finish();
            }
        });

        dialog.setNegativeButton(getResources().getString(R.string.minimize), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                enterPIPMode();
            }
        });

        dialog.setNeutralButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void enterPIPMode(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N
                && getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PictureInPictureParams.Builder params = new PictureInPictureParams.Builder();
                this.enterPictureInPictureMode(params.build());
            } else {
                this.enterPictureInPictureMode();
            }
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        isPIP=isInPictureInPictureMode;
        if (isInPictureInPictureMode) {
            hideViews();
        } else {
            showViews();
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int newOrientation = newConfig.orientation;
        if(newOrientation==Configuration.ORIENTATION_LANDSCAPE){
            isFullScreen=true;
            setTheme(R.style.FullscreenTheme);
            hideSystemUI();
            hideViews();
            coordinatorLayout.setFitsSystemWindows(false);

        }else if(newOrientation==Configuration.ORIENTATION_PORTRAIT){
            isFullScreen=false;
            setTheme(R.style.tvTheme);
            showSystemUI();
            showViews();
            coordinatorLayout.setFitsSystemWindows(true);

        }
        frag.setFullScreen(isFullScreen);
    }

    public void hideViews(){
        toolbar.setVisibility(View.GONE);
        if(frag!=null){
            frag.hideViews(isPIP);
        }
    }
    public void showViews(){
        toolbar.setVisibility(View.VISIBLE);
        if(frag!=null) {
            frag.showViews();
        }
    }
    @Override
    public void onUserLeaveHint () {
        enterPIPMode();
    }
}
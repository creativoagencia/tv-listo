package com.app.yoursingleradio.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.app.yoursingleradio.Config;
import com.app.yoursingleradio.R;
import com.app.yoursingleradio.activities.MainActivity;
import com.app.yoursingleradio.activities.TvActivity;
import com.app.yoursingleradio.models.ItemRadio;
import com.app.yoursingleradio.services.PlaybackStatus;
import com.app.yoursingleradio.services.RadioManager;
import com.app.yoursingleradio.services.metadata.Metadata;
import com.app.yoursingleradio.utilities.CollapseControllingFragment;
import com.app.yoursingleradio.utilities.Constant;
import com.app.yoursingleradio.utilities.GDPR;
import com.app.yoursingleradio.utilities.PermissionsFragment;
import com.app.yoursingleradio.utilities.SharedPref;
import com.app.yoursingleradio.utilities.SleepTimeReceiver;
import com.app.yoursingleradio.utilities.Tools;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.makeramen.roundedimageview.RoundedImageView;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

//import es.claucookie.miniequalizerlibrary.EqualizerView;

import jp.wasabeef.blurry.Blurry;

import static android.content.Context.ALARM_SERVICE;

/**
 * This fragment is used to listen to a radio station
 */
@SuppressLint("RestrictedApi")
public class FragmentRadioAdminPanel extends Fragment implements OnClickListener, PermissionsFragment, CollapseControllingFragment, Tools.EventListener {

    private RadioManager radioManager;
    private Activity activity;


    private RoundedImageView albumArtView;
    private RoundedImageView imageBottom;

    private RoundedImageView albumArtView_bg;

    private RelativeLayout relativeLayout;
    private ProgressBar progressBar;
    private FloatingActionButton buttonPlayPause;
    private Toolbar toolbar;

    private CoordinatorLayout coordinatorLayoutMenu;
    private RelativeLayout relativeRadio;
    private LinearLayout linearBottom;

    private ImageView imagePlayPause;
    private TextView textCancion, textArtist;
    private RoundedImageView imageArrowTop;
    private ImageView imageFondoBottom;

    private MainActivity mainActivity;
    private ImageButton img_volume_bar;
    private ImageButton img_timer;
    private InterstitialAd interstitialAd;
    int counter = 1;
    private String radio_url, radio_name;
    TextView nowPlayingTitle, nowPlaying;
    Handler handler = new Handler();
    SharedPref sharedPref;
    //EqualizerView equalizerView;
    Tools tools;
    private boolean isMenu = true;

    MaterialRippleLayout mLayoutWeb, layout_facebook,layout_instagram,layout_website,layout_whatsapp;


    private RotateAnimation rotate;
    private   RotateAnimation rotate2;
    private   RotateAnimation rotate_bg;
    private eu.gsottbauer.equalizerview.EqualizerView equalizer;


    public FragmentRadioAdminPanel() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_radio, container, false);

        coordinatorLayoutMenu = relativeLayout.findViewById(R.id.coordinator_container_menu);
        relativeRadio = relativeLayout.findViewById(R.id.relative_radio);
        linearBottom = relativeLayout.findViewById(R.id.linear_bottom_radio);

        toolbar = relativeLayout.findViewById(R.id.toolbar);
        nowPlayingTitle = relativeLayout.findViewById(R.id.now_playing_title);
        nowPlaying = relativeLayout.findViewById(R.id.now_playing);


        imageArrowTop = relativeLayout.findViewById(R.id.image_arrowtop_radio);
        imagePlayPause = relativeLayout.findViewById(R.id.image_play_radio);
        textCancion = relativeLayout.findViewById(R.id.text_cancion_radio);
        textArtist = relativeLayout.findViewById(R.id.text_artista_radio);
        imageFondoBottom = relativeLayout.findViewById(R.id.image_fondo_bottom_radio);

        imageArrowTop.setCornerRadius(30f);
        imageArrowTop.setOval(true);


        equalizer = relativeLayout.findViewById(R.id.equalizer);

        setupToolbar();

        setHasOptionsMenu(true);

        sharedPref = new SharedPref(getActivity());
        sharedPref.setCheckSleepTime();
        tools = new Tools(getActivity());


        rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate2 = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );

        rotate.setDuration(3200);
        rotate.setRepeatCount(Animation.INFINITE);

        rotate2.setDuration(3200);
        rotate2.setRepeatCount(Animation.INFINITE);

        rotate_bg = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );


        rotate_bg.setDuration(3200);
        rotate_bg.setRepeatCount(Animation.INFINITE);



        initializeUIElements();

        //Initialize visualizer or imageview for album art
        if (Config.ENABLE_ALBUM_ART) {
            albumArtView.setVisibility(View.VISIBLE);
            imageBottom.setVisibility(View.VISIBLE);
        } else {
            albumArtView.setVisibility(View.GONE);
            imageBottom.setVisibility(View.GONE);
        }

        albumArtView.setImageResource(Tools.BACKGROUND_IMAGE_ID);
        imageBottom.setImageResource(Tools.BACKGROUND_IMAGE_ID);
        Blurry.with(requireContext())
                .from(BitmapFactory.decodeResource(getResources(), Tools.BACKGROUND_IMAGE_ID))
                .into(imageFondoBottom);


        loadInterstitialAd();
        onBackPressed();

        return relativeLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMenu();
    }


    private void initMenu(){
        getChildFragmentManager().beginTransaction()
                .replace(R.id.coordinator_container_menu, new FragmentMenu(), FragmentMenu.TAG)
                .commit();
        isMenu = true;
    }



    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.app_name));
        mainActivity.setSupportActionBar(toolbar);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mainActivity.setupNavigationDrawer(toolbar);
        activity = getActivity();

        if (Tools.isNetworkActive(getActivity())) {
            new MyTask().execute(Config.ADMIN_PANEL_URL + "/api.php");
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.dialog_internet_description), Toast.LENGTH_SHORT).show();
        }

        Tools.isOnlineShowDialog(activity);

        //Get the radioManager
        radioManager = RadioManager.with();

        progressBar.setVisibility(View.VISIBLE);

        //Obtain the actual radio url
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        updateButtons();
                    }
                });
            }
        });

        if (isPlaying()) {
            onAudioSessionId(RadioManager.getService().getAudioSessionId());
        }

    }

    @Override
    public void onEvent(String status) {

        switch (status) {
            case PlaybackStatus.LOADING:
                progressBar.setVisibility(View.VISIBLE);
                break;

            case PlaybackStatus.ERROR:
                makeSnackBar(R.string.error_retry);
                break;
        }

        if (!status.equals(PlaybackStatus.LOADING))
            progressBar.setVisibility(View.INVISIBLE);

        updateButtons();

    }

    @Override
    public void onAudioSessionId(Integer i) {
    }

    @Override
    public void onStart() {
        super.onStart();
        Tools.registerAsListener(this);
    }

    @Override
    public void onStop() {
        Tools.unregisterAsListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (!radioManager.isPlaying())
            radioManager.unbind(getContext());
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateButtons();
        radioManager.bind(getContext());

        if (Config.FORCE_UPDATE_METADATA_ON_RESUME) {
            if (isPlaying()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startResume();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startResume();
                            }
                        }, 10);
                    }
                }, 10);
            }
        }

    }

    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            buttonPlayPause.setVisibility(View.INVISIBLE);
            nowPlaying.setText(R.string.loading_progress);
        }

        @Override
        protected String doInBackground(String... params) {
            return Tools.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            buttonPlayPause.setVisibility(View.VISIBLE);

            if (null == result || result.length() == 0) {
                Toast.makeText(getActivity(), getResources().getString(R.string.dialog_internet_description), Toast.LENGTH_SHORT).show();

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray("result");
                    JSONObject c = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        c = jsonArray.getJSONObject(i);

                        radio_name = c.getString("radio_name");
                        radio_url = c.getString("radio_url");

                        Constant.itemRadio = new ItemRadio(radio_name, radio_url);

                    }

                    nowPlayingTitle.setText(R.string.now_playing);
                    nowPlaying.setText(radio_name);

                    if (Config.ENABLE_AUTO_PLAY) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                buttonPlayPause.performClick();
                            }
                        }, 1000);
                    } else {
                        Log.d("INFO", "Auto play is disabled");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initializeUIElements() {
        progressBar = relativeLayout.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);

      //  equalizerView = relativeLayout.findViewById(R.id.equalizer_view);

        albumArtView = relativeLayout.findViewById(R.id.albumArt);
        albumArtView_bg = relativeLayout.findViewById(R.id.albumArt_bg);
        albumArtView.setCornerRadius((float) Config.ALBUM_ART_CORNER_RADIUS);
        albumArtView_bg.setCornerRadius((float) Config.ALBUM_ART_CORNER_RADIUS);
        //albumArtView.setBorderWidth((float) Config.ALBUM_ART_BORDER_WIDTH);
        imageBottom = relativeLayout.findViewById(R.id.image_bottom_radio);
        imageBottom.setCornerRadius((float)Config.ALBUM_ART_CORNER_RADIUS);

        if (Config.ENABLE_CIRCULAR_IMAGE_ALBUM_ART) {
            albumArtView.setOval(true);
        } else {
            albumArtView.setOval(false);
        }

        layout_facebook = relativeLayout.findViewById(R.id.layout_facebook);
        layout_facebook.setOnClickListener(view -> facebook_link());

        layout_instagram = relativeLayout.findViewById(R.id.layout_instagram);
        layout_instagram.setOnClickListener(view -> instagram_link ());

        layout_website = relativeLayout.findViewById(R.id.layout_website);
        layout_website.setOnClickListener(view -> website_link ());

        layout_whatsapp = relativeLayout.findViewById(R.id.layout_whatsapp);
        layout_whatsapp.setOnClickListener(view -> whatsapp_link ());





        img_volume_bar = relativeLayout.findViewById(R.id.img_volume);
        img_volume_bar.setOnClickListener(view -> changeVolume());

        img_timer = relativeLayout.findViewById(R.id.img_timer);
        img_timer.setOnClickListener(view -> {
            if (sharedPref.getIsSleepTimeOn()) {
                openTimeDialog();
            } else {
                openTimeSelectDialog();
            }
        });


        buttonPlayPause = relativeLayout.findViewById(R.id.btn_play_pause);
        buttonPlayPause.setOnClickListener(this);
        imagePlayPause.setOnClickListener(this);
        imageArrowTop.setOnClickListener(view -> {
            closeMenu();
        });

        //   equalizerView.stopBars();
        equalizer.stopBars();
        updateButtons();

    }

    private void updateButtons() {
        if (isPlaying() || progressBar.getVisibility() == View.VISIBLE) {
            //If another stream is playing, show this in the layout
            if (RadioManager.getService() != null && radio_url != null && !radio_url.equals(RadioManager.getService().getStreamUrl())) {
                buttonPlayPause.setImageResource(R.drawable.ic_play_white);
                imagePlayPause.setImageResource(R.drawable.ic_play_white);
                relativeLayout.findViewById(R.id.already_playing_tooltip).setVisibility(View.VISIBLE);

                //If this stream is playing, adjust the buttons accordingly
            } else {
                buttonPlayPause.setImageResource(R.drawable.ic_pause_white);
                imagePlayPause.setImageResource(R.drawable.ic_pause_white);
                relativeLayout.findViewById(R.id.already_playing_tooltip).setVisibility(View.GONE);
            }
        } else {
            //If this stream is paused, adjust the buttons accordingly
            buttonPlayPause.setImageResource(R.drawable.ic_play_white);
            imagePlayPause.setImageResource(R.drawable.ic_play_white);
            relativeLayout.findViewById(R.id.already_playing_tooltip).setVisibility(View.GONE);

            updateMediaInfoFromBackground(null, null);
            updateMediaInfoFromBackgroundBottom(null,null, null);
        }

        if (isPlaying()) {
            //   equalizerView.animateBars();
            equalizer.animateBars();
            albumArtView.startAnimation(rotate);
            albumArtView_bg.startAnimation(rotate_bg);
            imageBottom.startAnimation(rotate2);

        } else {
            // equalizerView.stopBars();
            equalizer.stopBars();
            rotate.cancel();
            rotate.reset();
            rotate2.cancel();
            rotate2.reset();
            rotate_bg.cancel();
            rotate_bg.reset();

        }

    }

    @Override
    public void onClick(View v) {
        requestStoragePermission();
    }

    private void startStopPlaying() {
        //Start the radio playing
        radioManager.playOrPause(radio_url);
        //Update the UI
        updateButtons();
    }

    private void startResume() {
        //Start the radio playing
        radioManager.playResume(radio_url);
        //Update the UI
        updateButtons();
    }

    public void updateMediaInfoFromBackgroundBottom(String artist, String song, Bitmap image) {

        if ( artist != null) {
            textArtist.setText(artist);
        }
        else{
            textArtist.setText(R.string.app_name);
        }
        if ( song != null) {
            textCancion.setText(song);
        }
        else{
            textCancion.setText(R.string.now_playing);
        }

        if (image != null) {
            imageBottom.setImageBitmap(image);

        } else {
            imageBottom.setImageResource(Tools.BACKGROUND_IMAGE_ID);
        }

    }




    private void stopService() {
        radioManager.stopServices();
        Tools.unregisterAsListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent sendInt = new Intent(Intent.ACTION_SEND);
                sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sendInt.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
                sendInt.setType("text/plain");
                startActivity(Intent.createChooser(sendInt, "Share"));
                return true;
                /*DialogMain.newInstance()
                        .show(requireActivity().getSupportFragmentManager(), DialogMain.TAG);*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }


 /*   @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent sendInt = new Intent(Intent.ACTION_SEND);
                sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sendInt.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
                sendInt.setType("text/plain");
                startActivity(Intent.createChooser(sendInt, "Share"));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    //@param info - the text to be updated. Giving a null string will hide the info.
    public void updateMediaInfoFromBackground(String info, Bitmap image) {

        if (info != null)
            nowPlaying.setText(info);

        if (info != null && nowPlayingTitle.getVisibility() == View.GONE) {
            nowPlayingTitle.setVisibility(View.VISIBLE);
            nowPlaying.setVisibility(View.VISIBLE);
        } else if (info == null) {
            nowPlayingTitle.setVisibility(View.VISIBLE);
            nowPlayingTitle.setText(R.string.now_playing);
            nowPlaying.setVisibility(View.VISIBLE);
            nowPlaying.setText(radio_name);
        }

        if (image != null) {
            albumArtView.setImageBitmap(image);
        } else {
            albumArtView.setImageResource(Tools.BACKGROUND_IMAGE_ID);
        }

    }

    @Override
    public String[] requiredPermissions() {
        return new String[]{Manifest.permission.READ_PHONE_STATE};
    }

    @Override
    public void onMetaDataReceived(Metadata meta, Bitmap image) {
        //Update the mediainfo shown above the controls
        String artistAndSong = null;
        if (meta != null && meta.getArtist() != null)
            artistAndSong = meta.getArtist() + " - " + meta.getSong();
        updateMediaInfoFromBackground(artistAndSong, image);
    }

    private boolean isPlaying() {
        return (null != radioManager && null != RadioManager.getService() && RadioManager.getService().isPlaying());
    }

    @Override
    public boolean supportsCollapse() {
        return false;
    }

    private void makeSnackBar(int text) {
        Snackbar bar = Snackbar.make(buttonPlayPause, text, Snackbar.LENGTH_SHORT);
        bar.show();
        ((TextView) bar.getView().findViewById(R.id.snackbar_text)).setTextColor(getResources().getColor(R.color.white));
    }

    public void onBackPressed() {
        ((MainActivity) getActivity()).setOnBackClickListener(new MainActivity.OnBackClickListener() {
            @Override
            public boolean onBackClick() {
                exitDialog();
                return true;
            }
        });
    }



    ////////////FUNCIONES DONDE CAMBIAR EL LINK  ////////////////////////////////
    private void whatsapp_link (){
        String url ="https://wa.me/51975959016";

        startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse(url))
        );

     /*   PackageManager pm=getActivity().getPackageManager();

        if(appInstalledOrNot("com.whatsapp")) {
            String toNumber = "56941255060"; // Replace with mobile phone number without +Sign or leading zeros, but with country code.
            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "" + toNumber + "?body=" + ""));
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        }
        else
        if(appInstalledOrNot("com.whatsapp.w4b")) {

            String toNumber = "56941255060"; // Replace with mobile phone number without +Sign or leading zeros, but with country code.
            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "" + toNumber + "?body=" + ""));
            sendIntent.setPackage("com.whatsapp.w4b");
            startActivity(sendIntent);


        }
        else
        {
            Toast.makeText(activity,"No tienes instalado Whatsapp",Toast.LENGTH_SHORT).show();
        }*/

    }

    private void instagram_link (){

        //:B acá comento el código del streaming y agrego el enlace a la activity
       /* Uri uri = Uri.parse("https://www.instagram.com/streampe/");
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/streampe/")));
        }*/

        startStopPlaying();
        Intent intent = new Intent(getActivity(), TvActivity.class);
        startActivity(intent);

    }



    private void website_link (){

        getChildFragmentManager().beginTransaction()
                .replace(R.id.coordinator_container_menu, FragmentWeb.newInstance("https://creativoagencia.com"), FragmentWeb.TAG)
                .commit();
        isMenu = false;
        openMenu();
        /*try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://radiomitierra.com"));
            startActivity(intent);
        } catch(Exception e) {
            Toast.makeText(activity,"No se puede cargar el contenido",Toast.LENGTH_LONG).show();
        }*/
    }


    private void facebook_link (){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/268414683180636"));
            startActivity(intent);
        } catch(Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://web.facebook.com/creativoagencias/")));
        }

    }

    ///////////// FIN DE CAMBIO DE FUNCIONES ///////////////////////////

    private void animMenu(){
        if(coordinatorLayoutMenu.getVisibility() == View.VISIBLE){
            if(getChildFragmentManager().getBackStackEntryCount()>0){
                onBackPressed();
            }else {
                if(isMenu) {
                    closeMenu();
                }else{
                    initMenu();
                }
            }
        }else {
            openMenu();
        }
    }
    private void closeMenu(){
        coordinatorLayoutMenu.animate()
                .translationY(-coordinatorLayoutMenu.getHeight())
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        coordinatorLayoutMenu.setVisibility(View.GONE);
                    }
                });
        relativeRadio.animate()
                .translationY(0)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        relativeRadio.setVisibility(View.VISIBLE);
                        relativeRadio.setAlpha(1f);
                    }

                });
        linearBottom.animate()
                .translationY(-linearBottom.getHeight())
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        linearBottom.setVisibility(View.GONE);
                    }
                });
        initMenu();
    }



    private void openMenu(){
        coordinatorLayoutMenu.animate()
                .translationY(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        coordinatorLayoutMenu.setVisibility(View.VISIBLE);
                    }

                });
        relativeRadio.animate()
                .translationY(relativeRadio.getHeight())
                .alpha(0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        relativeRadio.setVisibility(View.GONE);
                    }
                });

        linearBottom.animate()
                .translationY(0f)
                .alpha(1.0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        linearBottom.setVisibility(View.VISIBLE);
                        linearBottom.setAlpha(0f);
                    }

                });
    }


    public void exitDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(getResources().getString(R.string.message));
        dialog.setPositiveButton(getResources().getString(R.string.quit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stopService();
                getActivity().finish();
            }
        });

        dialog.setNegativeButton(getResources().getString(R.string.minimize), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                minimizeApp();
            }
        });

        dialog.setNeutralButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    public void minimizeApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void requestStoragePermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.READ_PHONE_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (!isPlaying()) {
                                if (radio_url != null) {

                                    startStopPlaying();
                                    showInterstitialAd();

                                    //Check the sound level
                                    AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
                                    int volume_level = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    if (volume_level < 2) {
                                        makeSnackBar(R.string.volume_low);
                                    }

                                } else {
                                    //The loading of urlToPlay should happen almost instantly, so this code should never be reached
                                    makeSnackBar(R.string.error_retry_later);
                                }
                            } else {
                                startStopPlaying();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getActivity(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void changeVolume() {
        final RelativePopupWindow popupWindow = new RelativePopupWindow(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.lyt_volume, null);
        ImageView imageView1 = view.findViewById(R.id.img_volume_max);
        ImageView imageView2 = view.findViewById(R.id.img_volume_min);
        imageView1.setColorFilter(Color.BLACK);
        imageView2.setColorFilter(Color.BLACK);

        VerticalSeekBar seekBar = view.findViewById(R.id.seek_bar_volume);
        seekBar.getThumb().setColorFilter(sharedPref.getFirstColor(), PorterDuff.Mode.SRC_IN);
        seekBar.getProgressDrawable().setColorFilter(sharedPref.getSecondColor(), PorterDuff.Mode.SRC_IN);

        final AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        seekBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        int volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setProgress(volume_level);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setContentView(view);
        popupWindow.showOnAnchor(img_volume_bar, RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.CENTER);
    }

    public void openTimeSelectDialog() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(getActivity());
        alt_bld.setTitle(getString(R.string.sleep_time));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.lyt_dialog_select_time, null);
        alt_bld.setView(dialogView);

        final TextView tv_min = dialogView.findViewById(R.id.txt_minutes);
        tv_min.setText("1 " + getString(R.string.min));
        FrameLayout frameLayout = dialogView.findViewById(R.id.frameLayout);

        final IndicatorSeekBar seekbar = IndicatorSeekBar
                .with(getActivity())
                .min(1)
                .max(120)
                .progress(1)
                .thumbColor(sharedPref.getSecondColor())
                .indicatorColor(sharedPref.getFirstColor())
                .trackProgressColor(sharedPref.getFirstColor())
                .build();

        seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                tv_min.setText(seekParams.progress + " " + getString(R.string.min));
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }
        });

        frameLayout.addView(seekbar);

        alt_bld.setPositiveButton(getString(R.string.set), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String hours = String.valueOf(seekbar.getProgress() / 60);
                String minute = String.valueOf(seekbar.getProgress() % 60);

                if (hours.length() == 1) {
                    hours = "0" + hours;
                }

                if (minute.length() == 1) {
                    minute = "0" + minute;
                }

                String totalTime = hours + ":" + minute;
                long total_timer = tools.convertToMilliSeconds(totalTime) + System.currentTimeMillis();

                Random random = new Random();
                int id = random.nextInt(100);

                sharedPref.setSleepTime(true, total_timer, id);

                Intent intent = new Intent(getActivity(), SleepTimeReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), id, intent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, total_timer, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, total_timer, pendingIntent);
                }
            }
        });
        alt_bld.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    public void openTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.sleep_time));
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.lyt_dialog_time, null);
        builder.setView(dialogView);

        TextView textView = dialogView.findViewById(R.id.txt_time);

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton(getString(R.string.stop), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getActivity(), SleepTimeReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), sharedPref.getSleepID(), i, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                pendingIntent.cancel();
                alarmManager.cancel(pendingIntent);
                sharedPref.setSleepTime(false, 0, 0);
            }
        });

        updateTimer(textView, sharedPref.getSleepTime());

        builder.show();
    }

    private void updateTimer(final TextView textView, long time) {
        long timeleft = time - System.currentTimeMillis();
        if (timeleft > 0) {
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeleft),
                    TimeUnit.MILLISECONDS.toMinutes(timeleft) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(timeleft) % TimeUnit.MINUTES.toSeconds(1));

            textView.setText(hms);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sharedPref.getIsSleepTimeOn()) {
                        updateTimer(textView, sharedPref.getSleepTime());
                    }
                }
            }, 1000);
        }
    }


    private void loadInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ON_PLAY) {
            interstitialAd = new InterstitialAd(getActivity());
            interstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_unit_id));
            interstitialAd.loadAd(GDPR.getAdRequest(getActivity()));
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    interstitialAd.loadAd(GDPR.getAdRequest(getActivity()));
                }
            });
        } else {
            Log.d("INFO", "AdMob Interstitial is Disabled");
        }
    }

    private void showInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ON_PLAY) {
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                if (counter == Config.ADMOB_INTERSTITIAL_ON_PLAY_INTERVAL) {
                    interstitialAd.show();
                    counter = 1;
                } else {
                    counter++;
                }
            } else {
                Log.d("INFO", "Interstitial Ad is Disabled");
            }
        } else {
            Log.d("INFO", "AdMob Interstitial is Disabled");
        }
    }

}
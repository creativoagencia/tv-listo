package com.app.yoursingleradio.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.yoursingleradio.Config;
import com.app.yoursingleradio.R;
import com.app.yoursingleradio.activities.TvMainActivity;
import com.app.yoursingleradio.dialogs.DialogMain;
import com.app.yoursingleradio.utilities.Log;
import com.app.yoursingleradio.utilities.SharedPref;
import com.app.yoursingleradio.utilities.SleepTimeReceiver;
import com.app.yoursingleradio.utilities.Tools;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;

public class TvMainFragment extends Fragment {

    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;
    private View view;
    private FloatingActionButton fabPlayPause;
    private FloatingActionButton fabPlayPause2;
    private ImageButton img_volume_bar;
    private ImageButton img_timer;

    private ImageView imageLogo;
    private RelativeLayout relativeTools;
    SharedPref sharedPref;
    Handler handler = new Handler();
    Tools tools;
    MaterialRippleLayout mLayoutWeb, layout_facebook,layout_instagram,layout_website,layout_tv,layout_whatsapp, layout_chat;


    private boolean isPlaying = false;
    private boolean isFullScreen = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tv_main, container, false);
        sharedPref = new SharedPref(getActivity());
        sharedPref.setCheckSleepTime();
        tools = new Tools(getActivity());

        imageLogo = view.findViewById(R.id.imageTvMainLogo);
        relativeTools = view.findViewById(R.id.relativeTvMain);

        playerView = view.findViewById(R.id.playerviewTvMain);
        fabPlayPause = view.findViewById(R.id.btn_play_pause);
        fabPlayPause2 = view.findViewById(R.id.btn_play_pause2);
        img_volume_bar = view.findViewById(R.id.img_volume);
        img_timer = view.findViewById(R.id.img_timer);
        img_volume_bar.setOnClickListener(view1 -> {
            changeVolume();
        });

        layout_facebook = view.findViewById(R.id.layout_facebook);
        layout_facebook.setOnClickListener(view1 -> facebook_link());

        layout_instagram = view.findViewById(R.id.layout_instagram);
        layout_instagram.setOnClickListener(view1 -> instagram_link ());

        layout_website = view.findViewById(R.id.layout_website);
        layout_website.setOnClickListener(view1 -> website_link ());

        layout_whatsapp = view.findViewById(R.id.layout_whatsapp);
        layout_whatsapp.setOnClickListener(view1 -> whatsapp_link ());


        fabPlayPause.setOnClickListener(view1 -> {
            if(isPlaying) {
                pause();
            }else {
                play();
            }
        });
        fabPlayPause2.setOnClickListener(view1 -> {
            if(isPlaying) {
                pause();
            }else {
                play();
            }
            fabPlayPause2.hide();
            fabPlayPause2.show();
        });

        playerView.setOnClickListener(view1 -> {
            fullScreen();
        });
        img_timer.setOnClickListener(view1 -> {
            if (sharedPref.getIsSleepTimeOn()) {
                openTimeDialog();
            } else {
                openTimeSelectDialog();
            }
        });
        return view;
    }

    private void fullScreen(){
        if(!isFullScreen){
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void initializePlayer() {


        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(requireContext());
        playerView.setPlayer(simpleExoPlayer);
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(
                Util.getUserAgent(requireContext(), getString(R.string.app_name)),
                30000, 30000, false);
        MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(Config.TV_STREAM_URL));
        playerView.setUseController(false);
        playerView.requestFocus();

        simpleExoPlayer.prepare(videoSource);
        play();

    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(simpleExoPlayer!=null) {
            simpleExoPlayer.stop();
        }
    }

    private void play(){
        if(simpleExoPlayer!=null){
            Log.e("play","play");
            simpleExoPlayer.setPlayWhenReady(true);
            fabPlayPause.setImageResource(R.drawable.ic_pause_white);
            fabPlayPause2.setImageResource(R.drawable.ic_pause_white);
            isPlaying = true;
        }
    }
    private void pause(){
        if(simpleExoPlayer!=null){
            Log.e("Pause","pause");
            simpleExoPlayer.setPlayWhenReady(false);
            fabPlayPause.setImageResource(R.drawable.ic_play_white);
            fabPlayPause2.setImageResource(R.drawable.ic_play_white);
            isPlaying = false;
        }
    }
    public void stop(){
        if(simpleExoPlayer!=null){
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.stop();
            fabPlayPause.setImageResource(R.drawable.ic_play_white);
            fabPlayPause2.setImageResource(R.drawable.ic_play_white);
            isPlaying = false;
        }
    }
    public void setFullScreen(boolean full){
        isFullScreen = full;
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


    private void facebook_link (){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/113439183700172"));
            startActivity(intent);
        } catch(Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/radiobiblicaoficial/")));
        }

    }

    ////////////FUNCIONES DONDE CAMBIAR EL LINK  ////////////////////////////////
    private void whatsapp_link (){
        String url ="https://wa.me/12108355370";

        startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse(url))
        );
    }

    private void instagram_link (){

        //:B acá comento el código del streaming y agrego el enlace a la activity
        Uri uri = Uri.parse("https://instagram.com/martinmartinezgarcilazo?igshid=1j3x5k88s7q28");
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://instagram.com/martinmartinezgarcilazo?igshid=1j3x5k88s7q28")));
        }


    }


    private void website_link (){
        DialogMain.newInstance()
                .show(getActivity().getSupportFragmentManager(), DialogMain.TAG);
    }

    public void hideViews(boolean isPIP){
        imageLogo.setVisibility(View.GONE);
        relativeTools.setVisibility(View.GONE);
        if(!isPIP) {
            fabPlayPause2.show();
        }else{
            fabPlayPause2.hide();
        }
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
        //params.setMargins(dpToPx(0),dpToPx(0),dpToPx(0),dpToPx(0) );
        params.bottomMargin=0;
        params.topMargin=0;
        params.leftMargin=0;
        params.rightMargin = 0;
        playerView.setLayoutParams(params);
    }
    public void showViews(){
        imageLogo.setVisibility(View.VISIBLE);
        relativeTools.setVisibility(View.VISIBLE);
        fabPlayPause2.hide();
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
        //params.setMargins(dpToPx(15),dpToPx(25),dpToPx(15),dpToPx(10));
        params.bottomMargin=dpToPx(10);
        params.topMargin=dpToPx(25);
        params.leftMargin=dpToPx(15);
        params.rightMargin = dpToPx(15);
        playerView.setLayoutParams(params);
    }
    private int dpToPx(int dp){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return dp * displayMetrics.densityDpi / 160;
    }

}

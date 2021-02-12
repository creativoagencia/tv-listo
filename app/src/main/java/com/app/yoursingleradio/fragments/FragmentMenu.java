package com.app.yoursingleradio.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.yoursingleradio.R;
import com.app.yoursingleradio.activities.MainActivity;
import com.app.yoursingleradio.activities.TvActivity;

public class FragmentMenu extends Fragment {

    public final static String TAG = FragmentMenu.class.getName();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        TextView dj = view.findViewById(R.id.text_menu_dj);
        TextView prog = view.findViewById(R.id.text_menu_prog);
        TextView video = view.findViewById(R.id.text_menu_video);
        TextView musica = view.findViewById(R.id.text_menu_musica);
        TextView com = view.findViewById(R.id.text_menu_com);
        TextView pub = view.findViewById(R.id.text_menu_publi);
        TextView tv = view.findViewById(R.id.text_menu_tv);
        dj.setOnClickListener(view1 ->
                loadWeb("https://creativoagencia.com/aplicacion/")
        );
        prog.setOnClickListener(view1 ->
                loadWeb("https://creativoagencia.com/aplicacion/")
        );
        video.setOnClickListener(view1 ->
                loadWeb("https://creativoagencia.com/aplicacion/videos/")
        );
        musica.setOnClickListener(view1 ->
                loadWeb("https://creativoagencia.com/aplicacion/musica/")
        );
        com.setOnClickListener(view1 ->
                loadWeb("https://creativoagencia.com/aplicacion/noticias/")
        );
        pub.setOnClickListener(view1 ->
                loadWeb("https://creativoagencia.com/aplicacion/")
        );
        tv.setOnClickListener(view1 -> {
            requireActivity().onBackPressed();
            ((MainActivity)requireActivity()).stopRadio();
            startActivity(
                    new Intent(requireActivity(), TvActivity.class)
            );
        });

        return view;
    }
    private void loadWeb(String url){
        getParentFragment()
                .getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out)
                .replace(R.id.coordinator_container_menu, FragmentWeb.newInstance(url), FragmentWeb.TAG)
                .addToBackStack(FragmentWeb.TAG)
                .commit();
    }
}

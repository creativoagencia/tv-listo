package com.app.yoursingleradio.dialogs;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import com.app.yoursingleradio.R;
import com.google.android.material.card.MaterialCardView;

public class DialogMain extends DialogFragment {

    public final static String TAG = DialogMain.class.getName();

    private final static String url = "https://radiobiblica.com";

    private ProgressBar progressBar;

    public static DialogMain newInstance(){
        return new DialogMain();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
        View view = requireActivity().getLayoutInflater()
            .inflate(R.layout.dialog_main, null);
        Button cerrar = view.findViewById(R.id.button_cerrar_dialog_main);
        CoordinatorLayout coordinatorLayout = view.findViewById(R.id.coordinator_dialog_main);
        progressBar = view.findViewById(R.id.progressbar_dialog_main);
        cerrar.setOnClickListener(view1 -> dismiss());
        coordinatorLayout.setOnClickListener(view1 -> dismiss());
        WebView webView = view.findViewById(R.id.webview_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        ViewGroup.LayoutParams layoutParams = webView.getLayoutParams();
        layoutParams.height = height;
        webView.setLayoutParams(layoutParams);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new myWebClient());
        webView.loadUrl(url);

        webView.setOnKeyListener((v, keyCode, event) -> {
            if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
            return false;
        });

        alert.setView(view);
        return alert.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getDialog()!=null && getDialog().getWindow()!=null){
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public class myWebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if(progressBar!=null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(progressBar!=null) {
                progressBar.setVisibility(View.GONE);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getActivity(), R.string.no_network, Toast.LENGTH_LONG).show();
            view.loadUrl("about:blank");
        }

    }
}

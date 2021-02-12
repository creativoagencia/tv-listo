package com.app.yoursingleradio.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.yoursingleradio.R;
import com.app.yoursingleradio.dialogs.DialogMain;

public class FragmentWeb extends Fragment {

    public final static String  TAG = FragmentWeb.class.getName();

    private final static String URL = "Url";

    private String url="";

    private ProgressBar progressBar;

    public static FragmentWeb newInstance(String url){
        Bundle bn = new Bundle();
        bn.putString(URL, url);
        FragmentWeb fr = new FragmentWeb();
        fr.setArguments(bn);
        return fr;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);

        url = getArguments().getString(URL);
        progressBar = view.findViewById(R.id.progress_menu);
        WebView webView = view.findViewById(R.id.webview_menu);
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
        return view;
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

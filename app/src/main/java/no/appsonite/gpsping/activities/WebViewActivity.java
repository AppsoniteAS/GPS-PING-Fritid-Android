package no.appsonite.gpsping.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.klarna.checkout.KlarnaCheckout;

import org.json.JSONException;

import no.appsonite.gpsping.R;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class WebViewActivity extends BaseActivity {
    private static final String EXTRA_URL = "extra_url";
    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_KLARNA = "extra_klarna";

    private WebView webView;
    private ProgressBar progressBar;

    public static void open(Context context, String url, String title, boolean klarna) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_URL, url);
        intent.putExtra(WebViewActivity.EXTRA_TITLE, title);
        intent.putExtra(WebViewActivity.EXTRA_KLARNA, klarna);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        boolean isKlarna = getIntent().getBooleanExtra(EXTRA_KLARNA, false);

        progressBar = findViewById(R.id.progressBar);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

        if (isKlarna) {
            KlarnaCheckout checkout = new KlarnaCheckout(this, "no.appsonite.gpsping://checkout");
            checkout.setWebView(webView);
            createSignalListener(checkout);
        }

        String url = getIntent().getStringExtra(EXTRA_URL);
        webView.loadUrl(url);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra(EXTRA_TITLE));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void createSignalListener(KlarnaCheckout checkout) {
        checkout.setSignalListener((s, jsonObject) -> {
            if (s.equals("complete")) {
                try {
                    String url = jsonObject.getString("uri");
                    webView.loadUrl(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("TAG", "Success");
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

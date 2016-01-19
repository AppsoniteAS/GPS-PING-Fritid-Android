package no.appsonite.gpsping.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import no.appsonite.gpsping.R;

/**
 * Created: Belozerov
 * Company: APPGRANULA LLC
 * Date: 19.01.2016
 */
public class WebViewActivity extends BaseActivity {
    private static final String EXTRA_URL = "extra_url";
    private static final String EXTRA_TITLE = "extra_title";

    WebView webView;

    public static void open(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_URL, url);
        intent.putExtra(WebViewActivity.EXTRA_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

        String url = getIntent().getStringExtra(EXTRA_URL);
        webView.loadUrl(url);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra(EXTRA_TITLE));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
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

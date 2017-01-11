package androidappfromwebsite.androidappfromwebsite;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.app.Activity;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AndroidAppFromWebsite extends Activity {

    protected WebView mainWebView;
    private Context mContext;
    private WebView mWebview;
    private WebView mWebviewPop;
    private FrameLayout mContainer;

    private Activity myActivity = this;

    private String url = "https://subdomain.yourwebsite.com/homepage";
    private String target_url_prefix = "subdomain.yourwebsite.com";
    String notConnectedHtml="<html><head>Error</head><body> You need to be connected to the internet in order to make the app work</body></html>";

    private ProgressBar progress;


    public void onBackPressed(){
        Log.d("onBackPressed", "onBackPressed");
        if (mainWebView.isFocused() && mainWebView.canGoBack()) {
            mainWebView.goBack();
            Log.d("onBackPressed", "goback");
        }
        else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_app_from_website);

        //Get main webview
        mainWebView = (WebView) findViewById(R.id.webview);

        //Cookie manager for the webview
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setMax(100);

        //Get outer container
        mContainer = (FrameLayout) findViewById(R.id.webview_frame);

        //Settings
        WebSettings webSettings = mainWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);

        mainWebView.setWebViewClient(new MyCustomWebViewClient());
        mainWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mainWebView, true);
        }
        mainWebView.setWebChromeClient(new MyCustomChromeClient());
        mainWebView.loadUrl(url);

        mContext=this.getApplicationContext();

    }



    private class MyCustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();
            //Log.d("shouldOverrideUrlLoading", url);
            if (host.equals(target_url_prefix))
            {
                // This is my web site, so do not override; let my WebView load
                // the page
                if(mWebviewPop!=null)
                {
                    mWebviewPop.setVisibility(View.GONE);
                    mContainer.removeView(mWebviewPop);
                    mWebviewPop=null;
                }
                return false;
            }

            if(host.equals("m.facebook.com") || host.equals("www.facebook.com"))
            {
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch
            // another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {

            Toast.makeText(myActivity, "Oh no! " + description,
                    Toast.LENGTH_SHORT).show();

            view.loadData(notConnectedHtml, "text/html", "utf-8");

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            Log.d("onReceivedSslError", "onReceivedSslError");
            //super.onReceivedSslError(view, handler, error);
        }

    }

    private class MyCustomChromeClient extends WebChromeClient
    {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            mWebviewPop = new WebView(mContext);
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new MyCustomWebViewClient());
            mWebviewPop.getSettings().setJavaScriptEnabled(true);
            mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.addView(mWebviewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            AndroidAppFromWebsite.this.setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }

        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {

            Toast.makeText(myActivity, "Oh no! " + description,
                    Toast.LENGTH_SHORT).show();

        }



        @Override
        public void onCloseWindow(WebView window) {
            Log.d("onCloseWindow", "called");
        }

    }

    public void setValue(int progresss) {
        if (progresss != 100) {
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(progresss);
        } else{
            progress.setVisibility(View.INVISIBLE);
        }
    }
}

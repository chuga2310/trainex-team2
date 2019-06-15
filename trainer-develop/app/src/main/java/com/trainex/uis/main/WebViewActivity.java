package com.trainex.uis.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

import com.trainex.R;
import com.trainex.diaglog.CustomAlertDialog;
import com.trainex.model.Session;
import com.trainex.rest.RestClient;
import com.trainex.uis.login_signup.StartActivity;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView1;
    private String textTitle;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView1 = (WebView) findViewById(R.id.webView1);
        session = (Session) getIntent().getSerializableExtra("session");
        textTitle=getIntent().getStringExtra("title");
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.loadUrl(getIntent().getStringExtra("link"));
        webView1.setWebViewClient(new WebViewClient() {

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
////                view.loadUrl(url);
//                return false;
//            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("url", url);
                Log.e("check",url.startsWith(RestClient.BASE_API)+"");
                if (url.startsWith(RestClient.BASE_API)){
                    view.loadUrl("javascript:HtmlViewer.checkResponse" +
                            "(document.getElementsByTagName('body')[0].innerHTML)");
                }

            }
        });
        webView1.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");
    }
    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }
        @JavascriptInterface
        public void checkResponse(String response) {
            try {
                JSONObject object  = new JSONObject(response);
                if (object.getInt("code")==200){
                    Intent intent = new Intent(WebViewActivity.this, CallUsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("session", session);
                    intent.putExtra("title", textTitle);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }else{
                    Log.e("where",response);
                    CustomAlertDialog dialog = new CustomAlertDialog(WebViewActivity.this, "Payment error",object.getString("error"),"Ok") {
                        @Override
                        public void doSomethingWhenDismiss() {

                            webView1.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (webView1.canGoBack()) {
                                        webView1.goBack();
                                    }
                                }
                            });
                        }
                    };
                    dialog.show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (webView1.canGoBack()) {
            webView1.goBack();
        } else {
            finish();
        }
    }
}

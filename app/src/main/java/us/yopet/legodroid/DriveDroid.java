package us.yopet.legodroid;

import us.yopet.legodroid.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.zxing.integration.android.*;

import java.util.ArrayList;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class DriveDroid extends Activity {
    final static String TAG = "YoPet";
    protected WebView _webView;
    private final DriveDroid _that = this;


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            Log.d(TAG, "Got scan result of "+scanResult);
            ValueCallback<String> resultCallback = new ValueCallback<String>(){
                @Override
                public void onReceiveValue(String t) {
                    Log.d(TAG, "javascript eval gotFarFinger :"+t);
                }
            };
            if (_webView != null){
                _webView.evaluateJavascript("gotFarFinger(\""+scanResult.getContents()+"\")", resultCallback);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drive_droid);

        _webView = (WebView) findViewById(R.id.mainWebView);
        WebChromeClient wcc = new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest req) {
                Log.d(TAG, "Requested resource from " + req.getOrigin().toString() + " granted");
                req.grant(req.getResources());
            }

            public void onPermissionRequestCanceled(PermissionRequest req) {

            }
        };
        WebSettings webSettings = _webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        _webView.setWebViewClient(new WebViewClient());
        _webView.setWebChromeClient(wcc);
        _webView.getSettings().setDomStorageEnabled(true);
        Object qrintent = new Object() {
            @android.webkit.JavascriptInterface
            public void scanQR() {
                Log.d(TAG, "Js called scanQR");
                IntentIntegrator integrator = new IntentIntegrator(_that);
                ArrayList <String> modes= new ArrayList<String>();
                modes.add("QR_CODE");
                integrator.initiateScan(modes);
            }
        };
        _webView.addJavascriptInterface(qrintent, "qrIntent");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (this.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
        _webView.loadUrl("https://lookafar.westhawk.co.uk:9000/abrick.html");
        //mWebView.loadUrl("http://limit.westhawk.co.uk:31735/pet/?client=android");

    }


}

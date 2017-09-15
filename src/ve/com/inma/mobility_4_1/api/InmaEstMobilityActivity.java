package ve.com.inma.mobility_4_1.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
//import ve.com.inma.mobility_4_1.api.R;

public class InmaEstMobilityActivity extends Activity {
    private WebView  browser;  

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.main);
        browser = (WebView) findViewById(R.id.webview);  
        browser.getSettings().setJavaScriptEnabled(true); 
        browser.getSettings().setDomStorageEnabled(true);
        browser.loadUrl("http://192.168.0.241/final-sandbox/sandbox/mobile/main.aspx");
        final Context myApp = this; 
        browser.setWebChromeClient(new WebChromeClient() {  
            @Override  
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)  
            {  
                new AlertDialog.Builder(myApp)  
                    .setTitle("javaScript dialog")  
                    .setMessage(message)  
                    .setPositiveButton(android.R.string.ok,  
                            new AlertDialog.OnClickListener()  
                            {  
                                public void onClick(DialogInterface dialog, int which)  
                                {  
                                    result.confirm();  
                                }  
                            })  
                    .setCancelable(false)  
                    .create()  
                    .show();  
          
                return true;  
            };  
        });     
    }
  
    /* Creates the menu items */  
    public boolean onCreateOptionsMenu(Menu menu) {  
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add (0, 0, 0, "DBTools");
        return result;
    }  
  
    /* Handles item selections */  
    public boolean onOptionsItemSelected(MenuItem item) {  
        switch (item.getItemId()) {  
        case 0:  
        	startActivity(new Intent(InmaEstMobilityActivity.this, ManageData.class));
            return true;
        }  
        return false;  
    }     
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      //ignore orientation change
      super.onConfigurationChanged(newConfig);
    }   
}
package ve.com.inma.mobility_4_1.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import ve.com.inma.mobility_4_1.api.R;


public class Main extends Activity {

	public static final String LOG_TAG = "Database";

	private static final int MENU_BOOK_ADD = 0;
	private static final int MENU_MANAGE_DATA = 1;
	private static final int MENU_WEBVIEW = 2;
	private static final int MENU_NEXTVIEW = 3;

	private ViewSwitcher switcher;
	private WebView browser;
    private ProgressDialog pd; 

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);

	
		
//		switcher = (ViewSwitcher) findViewById(R.id.profileSwitcher);
//		browser = (WebView) findViewById(R.id.webview);
		browser.getSettings().setJavaScriptEnabled(true);
		browser.getSettings().setDomStorageEnabled(true);
		browser.getSettings().setAllowFileAccess(true);
		browser.getSettings().setRenderPriority(RenderPriority.HIGH);
		browser.setWebViewClient(new InsideWebViewClient());

		//String databasePath = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();  
		//browser.getSettings().setDatabasePath(databasePath); 
		browser.getSettings().setDatabaseEnabled(true);

		String path = Environment.getDataDirectory() + "/data/com.totsp.database/databases";
		browser.getSettings().setDatabasePath(path); 		

		
		final Activity activity = this;
		browser.setWebChromeClient(new WebChromeClient() {
			   @Override
			   public void onExceededDatabaseQuota(String url,
			                        String databaseIdentifier,
			                        long totalUsedQuota,
			                        long currentQuota,
			                        long estimatedSize,
			                        WebStorage.QuotaUpdater quotaUpdater)
			   {
			       quotaUpdater.updateQuota(estimatedSize * 2);
			   }

			
			@Override
			public void onProgressChanged(WebView view, int progress) {

			}
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final android.webkit.JsResult result) {
				new AlertDialog.Builder(activity)
						.setTitle("javaScript dialog")
						.setMessage(message)
						.setPositiveButton(android.R.string.ok,
								new AlertDialog.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}
								}).setCancelable(false).create().show();

				return true;
			};
			public void onConsoleMessage(String message, int lineNumber, String sourceID) {
			    Log.d("MyApplication", message + " -- From line "
			                         + lineNumber + " of "
			                         + sourceID);
		    }
		});
		browser.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(activity, "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}
			@Override  
            public void onLoadResource(WebView view, String url) {
				  //pd = ProgressDialog.show(activity, "", "sacandome los mocos", true, false);
               
            }  
			@Override  
            public void onPageStarted(WebView view, String url, Bitmap favicon)  {
				  pd = ProgressDialog.show(activity, "", "cargando InmaEstMobile", true, false);
                
            }  
			@Override  
            public void onPageFinished(WebView view, String url) {
               super.onPageFinished(browser, url);
               pd.dismiss();
        		browser.loadUrl("javascript:dojo.style(dojo.byId('content'), {left: '0px', width: '675px'});");

                
            }  
		});

		
		
		
		browser.loadUrl("file:///android_asset/sandbox/mobile/main.html");
		//browser.getSettings().setBuiltInZoomControls(true);
		//browser.getSettings().setDefaultZoom(ZoomDensity.FAR);

	}


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      //ignore orientation change
      super.onConfigurationChanged(newConfig);
    } 
	
	private class InsideWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, Main.MENU_BOOK_ADD, 0, "Add Book").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(0, Main.MENU_MANAGE_DATA, 1, "Manage DB").setIcon(
				android.R.drawable.ic_menu_manage);
		menu.add(0, Main.MENU_WEBVIEW, 2, "WEBVIEW");
		menu.add(0, Main.MENU_NEXTVIEW, 3, "NEXTVIEW");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case MENU_BOOK_ADD:
			startActivity(new Intent(Main.this, BookAdd.class));
			return true;
		case MENU_MANAGE_DATA:
			startActivity(new Intent(Main.this, ManageData.class));
			return true;
		case MENU_WEBVIEW:
			Intent myIntent = new Intent(Main.this,
					InmaEstMobilityActivity.class);
			startActivityForResult(myIntent, 0);
			return true;
		case MENU_NEXTVIEW:
			switcher.showNext();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
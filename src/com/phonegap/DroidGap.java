/*
 * PhoneGap is available under *either* the terms of the modified BSD license *or* the
 * MIT License (2008). See http://opensource.org/licenses/alphabetical for full text.
 * 
 * Copyright (c) 2005-2010, Nitobi Software Inc.
 * Copyright (c) 2010-2011, IBM Corporation
 */
package com.phonegap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ve.com.inma.mobility_4_1.api.R;
import ve.com.inma.mobility_4_1.api.Soa;
import ve.com.inma.mobility_4_1.api.WebService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.KeyEvent;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datumdroid.android.ocr.simple.EstimateActivity;
import com.datumdroid.android.ocr.simple.GlobalOcr;
import com.example.android.photobyintent.PhotoIntentActivity;
import com.phonegap.api.PhonegapActivity;
import com.phonegap.api.IPlugin;
import com.phonegap.api.PluginManager;


/**
 * This class is the main Android activity that represents the PhoneGap
 * application.  It should be extended by the user to load the specific
 * html file that contains the application.
 * 
 * As an example:
 * 
 *     package com.phonegap.examples;
 *     import android.app.Activity;
 *     import android.os.Bundle;
 *     import com.phonegap.*;
 *     
 *     public class Examples extends DroidGap {
 *       @Override
 *       public void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *                  
 *         // Set properties for activity
 *         super.setStringProperty("loadingDialog", "Title,Message"); // show loading dialog
 *         super.setStringProperty("errorUrl", "file:///android_asset/www/error.html"); // if error loading file in super.loadUrl().
 *
 *         // Initialize activity
 *         super.init();
 *         
 *         // Add your plugins here or in JavaScript
 *         super.addService("MyService", "com.phonegap.examples.MyService");
 *         
 *         // Clear cache if you want
 *         super.appView.clearCache(true);
 *         
 *         // Load your application
 *         super.setIntegerProperty("splashscreen", R.drawable.splash); // load splash.jpg image from the resource drawable directory
 *         super.loadUrl("file:///android_asset/www/index.html", 3000); // show splash screen 3 sec before loading app
 *       }
 *     }
 *
 * Properties: The application can be configured using the following properties:
 * 
 * 		// Display a native loading dialog.  Format for value = "Title,Message".  
 * 		// (String - default=null)
 * 		super.setStringProperty("loadingDialog", "Wait,Loading Demo...");
 * 
 * 		// Cause all links on web page to be loaded into existing web view, 
 * 		// instead of being loaded into new browser. (Boolean - default=false)
 * 		super.setBooleanProperty("loadInWebView", true);
 * 
 * 		// Load a splash screen image from the resource drawable directory.
 * 		// (Integer - default=0)
 * 		super.setIntegerProperty("splashscreen", R.drawable.splash);
 * 
 * 		// Time in msec to wait before triggering a timeout error when loading
 * 		// with super.loadUrl().  (Integer - default=20000)
 * 		super.setIntegerProperty("loadUrlTimeoutValue", 60000);
 * 
 * 		// URL to load if there's an error loading specified URL with loadUrl().  
 * 		// Should be a local URL starting with file://. (String - default=null)
 * 		super.setStringProperty("errorUrl", "file:///android_asset/www/error.html");
 * 
 * 		// Enable app to keep running in background. (Boolean - default=true)
 * 		super.setBooleanProperty("keepRunning", false);
 * 		
 * 		- modificación: 16 Dic. 2014 - José A. Azpurua
 * 		- Última modificación: 13 Oct. 2015 - José A. Azpurua
 */
public class DroidGap extends PhonegapActivity {

	// The webview for our app
	protected WebView appView;
	protected WebViewClient webViewClient;

	public String inputId="";
	public String sEstimateId="";
	public String placa = "";
	public String vehicleIdNumber = "";
	protected LinearLayout root;
	public boolean bound = false;
	public CallbackServer callbackServer;
	protected PluginManager pluginManager;
	protected boolean cancelLoadUrl = false;
	protected boolean clearHistory = false;
	protected ProgressDialog spinnerDialog = null;
    private ProgressDialog pd; 
	protected String json;
	protected JSONObject eObject = null;
	protected final Activity activity = this;
	public String sEstimateStatus="";
	public String sEstimator="";
	public String fechaLogD = "";
	public String fechaHoraLogD = "";
	public String versionLog = "";
	public Calendar cal1 = Calendar.getInstance();
	public boolean checkdb = false;
	//Variables of Folders
	public String FOLDER_YEAR     = "";
	public String FOLDER_MONTH    = "";
	public String FOLDER_DAY      = "";
	//public String FOLDER_PLATE    = "";
	public String FOLDER_ESTIMATE = "";
	public String FOLDER_VEHIDNUM = "";
	public String FOLDER_HORMINSEG = "";
	public String rutaAjuste = "INMAEst_Movil/";

	// The initial URL for our app
	// ie http://server/path/index.html#abc?query
	private String url;
	SQLiteDatabase myDb = null; // Database object
	SQLiteDatabase myDbCS = null; // Database object
	SQLiteDatabase myDbVS = null; // Database object
	SQLiteDatabase myDbVersion = null; // Database object
//	private static String DB_PATH = "/mnt/sdcard/databasedata/";
//	private static String DB_NAME = "carselector";
	
	// The base of the initial URL for our app.
	// Does not include file name.  Ends with /
	// ie http://server/path/
	private String baseUrl = null;

	// Plugin to call when activity result is received
	protected IPlugin activityResultCallback = null;
	protected boolean activityResultKeepRunning;

	// Flag indicates that a loadUrl timeout occurred
	private int loadUrlTimeout = 0;
	
	private String fotostat = "NO";
	
	/*
	 * The variables below are used to cache some of the activity properties.
	 */

	// Flag indicates that a URL navigated to from PhoneGap app should be loaded into same webview
	// instead of being loaded into the web browser.  
	protected boolean loadInWebView = false;

	// Draw a splash screen using an image located in the drawable resource directory.
	// This is not the same as calling super.loadSplashscreen(url)
	protected int splashscreen = 0;

	// LoadUrl timeout value in msec (default of 20 sec)
	protected int loadUrlTimeoutValue = 2000000000;
	
	// Keep app running when pause is received. (default = true)
	// If true, then the JavaScript and native code continue to run in the background
	// when another application (activity) is started.
	protected boolean keepRunning = true;
	protected static final int REQUEST_CODE = 10;
    /** 
     * Called when the activity is first created. 
     * 
     * @param savedInstanceState
     */
 
	public void javaFocus(final boolean shouldForceOpenKeyboard) {
	    Thread t = new Thread("Java focusser thread") {
	        public void run() {
	        	appView.requestFocus(View.FOCUS_DOWN);
	        	appView.setOnTouchListener(new View.OnTouchListener()
	        	{
	        	    @Override
	        	    public boolean onTouch(View v, MotionEvent event)
	        	    {
	        	        switch (event.getAction())
	        	        {
	        	            case MotionEvent.ACTION_DOWN:
	        	            case MotionEvent.ACTION_UP:
	        	                if (!v.hasFocus())
	        	                {
	        	                    v.requestFocus();
	        	                }
	        	                break;
	        	        }
	        	        return false;
	        	    }
	        	});
	        	appView.loadUrl("javascript:dojo.byId('"+inputId+"').focus();");
	        	if(shouldForceOpenKeyboard) {
	                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	                mgr.showSoftInput(appView, InputMethodManager.SHOW_IMPLICIT);
	            }
	        	appView.loadUrl("javascript:dojo.byId('"+inputId+"').click();");

	        }
	    };

	    // Run on the View Thread.
	    this.runOnUiThread(t);
	}
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		cal1.add(Calendar.MONTH, 1);
		//Date date = cal1.getInstance().getTime();
//		Toast.makeText(this, "date: "+date, Toast.LENGTH_LONG).show();	
//		Toast.makeText(this, "cal1.get(Calendar.MONTH): "+cal1.get(Calendar.MONTH), Toast.LENGTH_LONG).show();
	    fechaLogD = ""+cal1.get(Calendar.DATE)+"/"+cal1.get(Calendar.MONTH)
		+"/"+cal1.get(Calendar.YEAR)+" "+cal1.get(Calendar.HOUR)
		+":"+cal1.get(Calendar.MINUTE)+":"+cal1.get(Calendar.SECOND);
	    
	    versionLog = ""+cal1.get(Calendar.DATE)+cal1.get(Calendar.MONTH)
		+cal1.get(Calendar.YEAR);	    
	    //Toast.makeText(this, "versionLog: "+versionLog, Toast.LENGTH_LONG).show();
	    
	    FOLDER_YEAR     = ""+cal1.get(Calendar.YEAR);
	    FOLDER_MONTH    = ""+cal1.get(Calendar.MONTH);
	    if(FOLDER_MONTH.equals("0")){
	    	FOLDER_MONTH = "12";
	    }
	    FOLDER_DAY      = ""+cal1.get(Calendar.DAY_OF_MONTH);
	    int iFOLDER_DAY = cal1.get(Calendar.DAY_OF_MONTH);
	    if(iFOLDER_DAY<10){
	    	FOLDER_DAY = "0"+cal1.get(Calendar.DAY_OF_MONTH);
	    }	    
	    //Toast.makeText(this, "FOLDER_MONTH: "+FOLDER_MONTH, Toast.LENGTH_LONG).show();
	    //Toast.makeText(this, "FOLDER_DAY: "+FOLDER_DAY, Toast.LENGTH_LONG).show();
	    //int iFOLDER_MONTH = cal1.get(Calendar.MONTH);
	    int iFOLDER_MONTH = Integer.parseInt(FOLDER_MONTH);
	    if(iFOLDER_MONTH<10){
	    	FOLDER_MONTH = "0"+cal1.get(Calendar.MONTH);
	    }
	    //Toast.makeText(this, "FOLDER_MONTH: "+FOLDER_MONTH, Toast.LENGTH_LONG).show();
	    FOLDER_HORMINSEG = ""+cal1.get(Calendar.HOUR_OF_DAY);
	    FOLDER_HORMINSEG = FOLDER_HORMINSEG+"-"+cal1.get(Calendar.MINUTE);
	    FOLDER_HORMINSEG = FOLDER_HORMINSEG+"-"+cal1.get(Calendar.SECOND);
	    
	    // Se almacena la ruta completa del presente ajuste, para almacenarlo en el FileJsonInput.
		rutaAjuste = rutaAjuste + FOLDER_YEAR;
		rutaAjuste = rutaAjuste + "/" + FOLDER_MONTH;
		rutaAjuste = rutaAjuste + "/" + FOLDER_DAY;
	    
	    appendLog(" class: DroidGap.java - PROC: onCreate() - Inicio - OnCreate Ok");
	    
    	super.onCreate(savedInstanceState);
    	getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	// This builds the view.  We could probably get away with NOT having a LinearLayout, but I like having a bucket!

    	Display display = getWindowManager().getDefaultDisplay(); 
    	int width = display.getWidth();
    	int height = display.getHeight();
    	
    	root = new LinearLayoutSoftKeyboardDetect(this, width, height);
    	root.setOrientation(LinearLayout.VERTICAL);
    	root.setBackgroundColor(Color.BLACK);
    	root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 
    			ViewGroup.LayoutParams.FILL_PARENT, 0.0F));

    	// If url was passed in to intent, then init webview, which will load the url
    	Bundle bundle = this.getIntent().getExtras();
    	if (bundle != null) {
    		String url = bundle.getString("url");
    		if (url != null) {
    			this.init();
    		}
    	}
    	// Setup the hardware volume controls to handle volume control
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		try{
				File fil = new File("/mnt/sdcard/databasedata/carselector.db");
				if(fil.exists()){
				   //checkdb = true;
				   //Toast.makeText(this, "No se pudo abrir o no existe la Base de Datos: /mnt/sdcard/databasedata/carselector.db", Toast.LENGTH_LONG).show();
				  
				   //appView.loadUrl("file:///android_asset/www/NoDatabase.html");
				  //appView.stopLoading();
				}else{
				   checkdb = true;
				   Toast.makeText(this, "*** No existe la Base de Datos: /mnt/sdcard/databasedata/carselector.db. ****", Toast.LENGTH_LONG).show();
				   Toast.makeText(this, "*** SALIENDO DE LA APLICACION *** - No se puede operar sin la B.D.: carselector.db ", Toast.LENGTH_LONG).show();
				   return;
				}
	    } catch (Exception  ex) {
	        ex.printStackTrace();
			   Toast.makeText(this, "*** No existe la Base de Datos: /mnt/sdcard/databasedata/carselector.db. ****", Toast.LENGTH_LONG).show();
			   Toast.makeText(this, "*** SALIENDO DE LA APLICACION *** - No se puede operar sin la B.D.: carselector.db ", Toast.LENGTH_LONG).show();
			   return;
	    }			
    }
    
    /**
     * Create and initialize web container.
     */
	public void init() {
		
		// Create web container
		this.appView = new WebView(DroidGap.this);
		this.appView.setId(100);
		
		this.appView.setLayoutParams(new LinearLayout.LayoutParams(
        		ViewGroup.LayoutParams.FILL_PARENT,
        		ViewGroup.LayoutParams.FILL_PARENT, 
        		1.0F));

        WebViewReflect.checkCompatibility();

        if (android.os.Build.VERSION.RELEASE.startsWith("1.")) {
        	this.appView.setWebChromeClient(new GapClient(DroidGap.this));
        }
        else {
        	this.appView.setWebChromeClient(new EclairClient(DroidGap.this));        	
        }
           
        this.setWebViewClient(this.appView, new GapViewClient(this));

        this.appView.setInitialScale(100);
        this.appView.setVerticalScrollBarEnabled(false);
        this.appView.requestFocusFromTouch();
        this.appView.getSettings().setUseWideViewPort(true);
        this.appView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        
        //this.appView.clearCache(true);
        
        // Enable JavaScript
        WebSettings settings = this.appView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        settings.setAppCacheEnabled(true);
        //settings.setAppCachePath(Environment.getDataDirectory() + "/data/com.totsp.database/databases/");
        // Enable database
        settings.setDatabaseEnabled(true);
        String databasePath = "/mnt/sdcard/databasedata/";//Environment.getDataDirectory() + "/data/com.totsp.database/databases/";//this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath(); 
        settings.setDatabasePath(databasePath);
        
//        SQLiteDatabase myDb = null; // Database object
//
//        myDb = SQLiteDatabase.openDatabase(DB_PATH+DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
        
        // Enable DOM storage
        WebViewReflect.setDomStorage(settings);

        // Enable built-in geolocation
        WebViewReflect.setGeolocationEnabled(settings, false);

        // Bind PhoneGap objects to JavaScript
        this.bindBrowser(this.appView);

        // Add web view but make it invisible while loading URL
        this.appView.setVisibility(View.INVISIBLE);
        root.addView(this.appView);
        setContentView(root);
        
        // Clear cancel flag
        this.cancelLoadUrl = false;

        // If url specified, then load it
        String url = this.getStringProperty("url", null);
        if (url != null) {
        	System.out.println("Loading initial URL="+url);
        	this.loadUrl(url);        	
        }
	}
	
   	public class JavaScriptInterface {
	    Context mContext;
    	SQLiteDatabase myDb = null; 				// Database object for dealing with General Purposes and Services db´s
    	SQLiteDatabase myDbEstimates = null; 		// Database object for dealing with Estimates.sqlite Database
    	SQLiteDatabase myDbAjustes = null; 			// Database object for dealing with Ajustes.sqlite Database
    	SQLiteDatabase myDbCS = null; 				// Database object for dealing with Carselector.db Database
    	SQLiteDatabase myDbVS = null; 				// Database object for dealing with the ViewSelector.db Database
    	SQLiteDatabase myDbVersion = null; 			// Database object for dealing with version of the DataBases
    	String path = "/mnt/sdcard/databasedata/";	//Environment.getDataDirectory() + "/data/com.totsp.database/databases/";
    	//String path = "/mnt/sdcard/databasedataV/";	//Environment.getDataDirectory() + "/data/com.totsp.database/databases/";
	    /** Instantiate the interface and set the context */
	    JavaScriptInterface(Context c) {
	        mContext = c;
	    }

	    private class DownloadFilesTask extends AsyncTask<String, String, String> {

	    	@Override
	    	protected void onProgressUpdate(String... progress) {
	    		super.onProgressUpdate(progress);
	    		//Toast.makeText(mContext, "DOING...", Toast.LENGTH_SHORT).show();	
	    		mostrarToast(" EJECUTANDO.... ");
	            //setProgressPercent(progress[0]);
	        }

	    	@Override
	    	protected void onPostExecute(String result) {
	    		//Toast.makeText(mContext, "PREPARING...", Toast.LENGTH_LONG).show();			
	            //showDialog("Downloaded " + result + " bytes");
	        }

	    	@Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	    		//Toast.makeText(mContext, "PREPARING...", Toast.LENGTH_LONG).show();			

	        }

			@Override
			protected String doInBackground(String... query) {
		    	Cursor c = null;
		    	String data;
				try {
	 		    	c = myDb.rawQuery(query[0], null);
			    	c.moveToFirst();
		    		data = c.getString(2);
			    	
		    		c.close();
		    		
			    	return data;
			    }catch (CursorIndexOutOfBoundsException ex) {
			    	appendLog(" class: DroidGap.java - class DownloadFilesTask - PROC: doInBackground() - CursorIndexOutOfBoundsException: " + ex.getMessage());
			    	//Toast.makeText(mContext, "ERROR: "+query, Toast.LENGTH_LONG).show();			
			    	
			    }catch (SQLiteException ex) {
			    	appendLog(" class: DroidGap.java - class DownloadFilesTask - PROC: doInBackground() - SQLiteException: " + ex.getMessage());
			    	//Toast.makeText(mContext, "ERROR: "+query, Toast.LENGTH_LONG).show();			

				}finally{
			    	//Toast.makeText(mContext, "READY", 3).show();			
					appendLog(" class: DroidGap.java - class DownloadFilesTask - PROC: doInBackground() - DownloadFilesTask READY! ");
					
				}	        
				return "{}";			}
	    }
	    
		public void modulo_flags(String actualFlag)
		{ 
			if(actualFlag.equals("V")){
				path = "/mnt/sdcard/databasedataV/";
			}
			if(actualFlag.equals("P")){
				path = "/mnt/sdcard/databasedataP/";
			}
			if(actualFlag.equals("C")){
				path = "/mnt/sdcard/databasedataC/";
			}
			//openDatabaseEstimates();
			//Toast.makeText(mContext, "DroidGap modulo_flags actualFlag: "+actualFlag, Toast.LENGTH_LONG).show();
		}	    
	    
		public void modulo_fotos(String text)
		{ 	
			//Toast.makeText(mContext, "DroidGap modulo_fotos text: "+text, Toast.LENGTH_LONG).show();
			//Toast.makeText(mContext, "DroidGap modulo_fotos text: "+text, Toast.LENGTH_LONG).show();
	    	Intent resultIntent;
	    	
        	resultIntent = new Intent(DroidGap.this, PhotoIntentActivity.class);
        	//resultIntent.putExtra("NAME_FOLDER", "TEST_FOLDER");
        	//Toast.makeText(mContext, "Intent FOLDER_DAY: "+FOLDER_DAY, Toast.LENGTH_LONG).show();	
        	//Toast.makeText(mContext, "DroidGap FOLDER_ESTIMATE: "+text, Toast.LENGTH_LONG).show();
        	
        	if(Global.sTipoAjuste.equals("nuevo")){
        		if(text.length()==0){
        			text=Global.rutaAjusteG;
        		}
        		Global.estimateId=text;
        		if(text.length() >7){
        			text=text.substring(0, 6);
        		}
        	}else{
        		text=Global.sFOLDER_ESTIMATE;
        	}
        	
        	//Toast.makeText(mContext, "DroidGap FOLDER_ESTIMATE: "+text, Toast.LENGTH_LONG).show();
			int iautores = Global.autoRestore;
			if(iautores==1){
				resultIntent.putExtra("value", Global.rutaAjusteG);
				//resultIntent.putExtra("FOLDER_ESTIMATE", text);
				//Toast.makeText(mContext, "Intent iautores: "+iautores, Toast.LENGTH_LONG).show();
				
			}else{
				if(Global.sTipoAjuste.equals("mismo")){
				    FOLDER_HORMINSEG = ""+cal1.get(Calendar.HOUR_OF_DAY);
				    FOLDER_HORMINSEG = FOLDER_HORMINSEG+"-"+cal1.get(Calendar.MINUTE);
				    FOLDER_HORMINSEG = FOLDER_HORMINSEG+"-"+cal1.get(Calendar.SECOND);
					Global.mismoFolder = FOLDER_YEAR+FOLDER_MONTH+FOLDER_DAY+Global.sFOLDER_VEHIDNUM+FOLDER_HORMINSEG;
					//Global.rutaAjusteG = Global.rutaAjusteG + FOLDER_HORMINSEG+"/";
					Global.rutaAjusteGMismo = "/INMAEst_Movil/" + FOLDER_YEAR+"/"+FOLDER_MONTH+"/"+FOLDER_DAY+"/"+Global.sFOLDER_VEHIDNUM+"/"+FOLDER_HORMINSEG+"/";
					//Global.rutaAjusteG = Global.rutaAjusteG + FOLDER_HORMINSEG+"/";
					resultIntent.putExtra("FOLDER_HORMINSEG", FOLDER_HORMINSEG);
//					Toast.makeText(mContext, "modulo_fotos( if(Global.sTipoAjuste.equals(mismo)) Global.mismoFolder: "+Global.mismoFolder, Toast.LENGTH_LONG).show();
//					Toast.makeText(mContext, "modulo_fotos( if(Global.sTipoAjuste.equals(mismo)) Global.mismoFolder: "+Global.mismoFolder, Toast.LENGTH_LONG).show();
//					Toast.makeText(mContext, "modulo_fotos( if(Global.sTipoAjuste.equals(mismo)) Global.rutaAjusteG: "+Global.rutaAjusteG, Toast.LENGTH_LONG).show();
//					Toast.makeText(mContext, "modulo_fotos( if(Global.sTipoAjuste.equals(mismo)) Global.rutaAjusteG: "+Global.rutaAjusteG, Toast.LENGTH_LONG).show();
				}else{				
					FOLDER_VEHIDNUM=vehicleIdNumber;
					resultIntent.putExtra("FOLDER_YEAR", FOLDER_YEAR);
					resultIntent.putExtra("FOLDER_MONTH", FOLDER_MONTH);
					resultIntent.putExtra("FOLDER_DAY", FOLDER_DAY);
					//resultIntent.putExtra("FOLDER_PLATE", placa);
					resultIntent.putExtra("FOLDER_VEHIDNUM", vehicleIdNumber);
					//resultIntent.putExtra("FOLDER_ESTIMATE", text);
					Global.sFOLDER_VEHIDNUM=vehicleIdNumber;
					resultIntent.putExtra("FOLDER_HORMINSEG", FOLDER_HORMINSEG);
					Global.mismoFolder = FOLDER_YEAR+FOLDER_MONTH+FOLDER_DAY+FOLDER_VEHIDNUM+FOLDER_HORMINSEG;
					Global.rutaAjusteG = "/INMAEst_Movil/" + FOLDER_YEAR+"/"+FOLDER_MONTH+"/"+FOLDER_DAY+"/"+FOLDER_VEHIDNUM+"/"+FOLDER_HORMINSEG+"/";
					//resultIntent.putExtra("SHORT_YEAR", shortYear);
					//resultIntent.putExtra("COUNT_FOTOS", countfotos);
				}
			}
        	
			startActivityForResult(resultIntent, REQUEST_CODE);
		}	    
	    
	    private class getImage extends AsyncTask<String, String, String> {

	    	@Override
	    	protected void onProgressUpdate(String... progress) {
	    		super.onProgressUpdate(progress);
	    		//Toast.makeText(mContext, "DOING...", Toast.LENGTH_LONG).show();	
	    		mostrarToast(" EJECUTANDO.... ");
	            //setProgressPercent(progress[0]);
	        }

	    	@Override
	    	protected void onPostExecute(String result) {
	    		//Toast.makeText(mContext, "PREPARING...", Toast.LENGTH_LONG).show();			
	            //showDialog("Downloaded " + result + " bytes");
	        }

	    	@Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	    		//Toast.makeText(mContext, "PREPARING...", Toast.LENGTH_LONG).show();	
	    		mostrarToast(" PREPARANDO.... ");
	        }

			@Override
			protected String doInBackground(String... query) {
		        appView.loadUrl("javascript:getImage('"+query[0]+"','"+query[1]+"');");
				return path;
			}
	    }
	    
	    public void pasaEstatus(String sStatus){
	    	sEstimateStatus=sStatus;
	    	//Toast.makeText(mContext, "sEstimateStatus: "+sEstimateStatus, Toast.LENGTH_LONG).show();	
	    }   	    

	    // Procedimiento para guardar la Estimacion (Ajuste) en la Base de Datos / Guardar el Log en fisico
	    // y para llamar al Procedimiento que envia el json de respuesta a Peritaje, via Intent
	    public void saveEstimate(String Id, String json,String status) {
	    	
	    	//sEstimateId = Id;
	    	//Toast.makeText(mContext, "DroidGap saveEstimate - sEstimateId: "+sEstimateId, Toast.LENGTH_LONG).show();
    		ContentValues values = new ContentValues();
    		values.put("estimateId", Id);
    		values.put("Json", json);
    		values.put("status", status);
    		
    		ContentValues values2 = new ContentValues();
    		
    		//JSONObject eObjectJson = Dojo.toJson(json);
    		//JSONObject o = eObject;
    		
    		//Toast.makeText(mContext, "values "+values.toString(), Toast.LENGTH_LONG).show();
    		//Toast.makeText(mContext, "values "+eObject.toString(), Toast.LENGTH_LONG).show();
    		
   		try {
    			
    			// o = eObject.getJSONObject("eObject");
    			// o = eObject.getJSONObject("header");
    			// o = o.getJSONObject("auto");
    			// Toast.makeText(mContext, "Droidgap o.getString(Plate): "+o.getString("Plate"), Toast.LENGTH_LONG).show();
    			// String sEspacios = "";			
    			// String sIdEst   = Id;
   			// if(sIdEst.length()<20){
   				// sEspacios = space(20-sIdEst.length());
   				// sIdEst=sIdEst+sEspacios;
   			// }	    			
   			// String sFecha = o.getString("EstimateDate");	
   			// if(sFecha==null || sFecha=="null"  || sFecha.isEmpty() || sFecha.length()<8 || sFecha.length()>10|| sFecha == ""){
   				// sFecha = "NO - FECHA";
   			// }else{
   				// if(sFecha.lastIndexOf('#')>0){
   					// sFecha=sFecha.substring(1,(sFecha.length()-1));
   				// }else{
   					// //sFecha = sFecha + " ";				
   				// }
   			// }    		
   			// if(sFecha.length()<10){
   				// sEspacios = space(10-sFecha.length());
   				// sFecha=sFecha+sEspacios;
   			// }    			
   			
   			// String sPlaca = o.getString("Plate");
   			// if(sPlaca==null || sPlaca=="null"  || sPlaca.isEmpty() || sPlaca.length()<2  || sPlaca == ""){
   				// sPlaca = "NO PLACA";
   			// }else{
   				// if(sPlaca.length()<8){
   					// sEspacios = space(10-(sPlaca.length() + 1));
   					// sPlaca = " " + sPlaca + sEspacios;
   				// }
   			// } 
   			
   			// String sPerito = o.getString("Estimator");    			
   			// if(sPerito==null || sPerito=="null"  || sPerito.isEmpty() || sPerito.length()<2  || sPerito == ""){
   				// sPerito = "PERITO";
   			// }else{
   				// if(sPerito.length()<6){
   					// sEspacios = space(6-sPerito.length());
   					// sPerito = " " + sPerito + sEspacios;
   				// }
   			// }	    			
   			//Toast.makeText(this.ctx, "DroidGap placa: "+placa, Toast.LENGTH_LONG).show();
//   			Toast.makeText(mContext, "DroidGap estimateId: "+Id, Toast.LENGTH_LONG).show();
//   			Toast.makeText(mContext, "DroidGap Json: "+json, Toast.LENGTH_LONG).show();
//   			Toast.makeText(mContext, "DroidGap status: "+status, Toast.LENGTH_LONG).show();
//   			Toast.makeText(mContext, "DroidGap Global.rutaAjusteG: "+Global.rutaAjusteG, Toast.LENGTH_LONG).show();
//   			Toast.makeText(mContext, "DroidGap Global.rutaAjusteG: "+Global.rutaAjusteG, Toast.LENGTH_LONG).show();
   			values2.put("estimateId", Id);
   			// values2.put("estimateIdF", sIdEst);
   			// values2.put("Fecha", sFecha);
   			// values2.put("Perito", sPerito);
   			// values2.put("Placa", sPlaca);
       		values2.put("Json", json);			
       		values2.put("status", status); 
       		//values2.put("_id", "1"); 
       		values2.put("rutaAjuste", Global.rutaAjusteG);
   			values2.put("fechaAjuste", fechaLogD);
       		myDbAjustes.insert("ajustes", null, values2);
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " - JSONException al intentar obtener los elementos del Json "+ e.getMessage());
   			e.printStackTrace();
   		}	   		
   		appendLog(" class: DroidGap.java - PROC: saveEstimate() - myDbAjustes.insert(ajustes, , values2) Ajuste Guardado con Exito! " + values2);
    		sEstimateStatus = status;

    		//Toast.makeText(mContext, "class: DroidGap.java - PROC: saveEstimate() - values "+values2.toString(), Toast.LENGTH_LONG).show();
    		myDbEstimates.insert("estimates", "", values);
    		appendLog(" class: DroidGap.java - PROC: saveEstimate() - myDbEstimates.insert(estimates, , values) Estimacion Guardada con Exito! " + values);
    		//*** El siguiente bloque de codigo se agrego para descargar el JSon en un archivo fisico (Log) ***
    		//String sFileName = "Filejson"+Id;
    		String sFileName = "Filejson";
    		//Toast.makeText(mContext, "sFileName "+sFileName, Toast.LENGTH_LONG).show();
    		
    	    try
    	    {
    	        File root = new File(Environment.getExternalStorageDirectory(), "download");
    	        if (!root.exists()) {
    	            root.mkdirs();
    	        }
    	        File gpxfile = new File(root, sFileName);
    	        FileWriter writer = new FileWriter(gpxfile);
    	        writer.append(json);
    	        writer.flush();
    	        writer.close();
    	        appendLog(" class: DroidGap.java - PROC: saveEstimate() - Archivo Filejson, salvado con Exito! " + json);
				//delete_FileBackupjson();
    	    }
    	    catch(IOException e)
    	    {
    	    	appendLog(" class: DroidGap.java - PROC: saveEstimate() - Al Guardar el Archivo: Filejson, al escribir el el mismo o al crear el Directorio: download, IOException: " + e.getMessage());
    	         e.printStackTrace();
    	         e.getMessage();
    	    }
    	    //Toast.makeText(mContext, "class: DroidGap.java - PROC: saveEstimate() - Archivo Filejson, salvado con Exito! : "+sEstimateId, Toast.LENGTH_LONG).show();
    	  //*** Fin de bloque de codigo para descargar el JSon en un archivo fisico ***
    		processEstimate(json);
    	    //delete_FileBackupjson();
    	    
/*	    		ContentValues values = new ContentValues();
	    		values.put("estimateId", id);
	    		values.put("Json", json);
	    		values.put("status", status);
	    		myDbEstimates.insert("estimates", "", values);
	    		processEstimate(json);*/
	    }
	    
	    // Procedimiento que envia el json de respuesta a Peritaje, via Intent / Borra el Archivo de Respaldo y culmina la Aplicacion
	    public void processEstimate(String eObject)  {
    	    
	    	//Intent resultIntent;
	    	Intent resultIntent = new Intent();

        	//resultIntent = new Intent(DroidGap.this, WebService.class);
	    	//Toast.makeText(mContext, "class: DroidGap.java - PROC: processEstimate() - ANTES de ** ¡SALIENDO de INMAEst! ** Nuevoo Global.sTipoAjuste: "+Global.sTipoAjuste, Toast.LENGTH_SHORT).show();
	    	////if(Global.sTipoAjuste == "nuevo"){
	    	//resultIntent.setClassName("com.datumdroid.android.ocr.simple","com.datumdroid.android.ocr.simple.EstimateActivity");
	    	resultIntent = new Intent(DroidGap.this, EstimateActivity.class);
	    		//Toast.makeText(mContext, "** ¡SALIENDO de INMAEst! ** Nuevoo Global.sTipoAjuste: "+Global.sTipoAjuste, Toast.LENGTH_SHORT).show();
	    	//}else{
	    	////	resultIntent.setClassName("com.datumdroid.android.ocr.simple","com.datumdroid.android.ocr.simple.EstimateActivity");
	    		//Toast.makeText(mContext, "** ¡SALIENDO de INMAEst! ** MESSMO Global.sTipoAjuste: "+Global.sTipoAjuste, Toast.LENGTH_SHORT).show();
	    	////}
        	// Se prepara el Intent y se cargan los Extras a enviar al WebService (json de respuesta)
    	    try
    	    {        	
    	    	//if(Global.sTipoAjuste.equals("nuevo")){
    	    	//Toast.makeText(mContext, "** ¡SALIENDO de INMAEst! ** eObject: "+eObject, Toast.LENGTH_SHORT).show();
    	    		//resultIntent.putExtra("eObject", eObject);
    	    		GlobalOcr.eObject=eObject;
    	    		GlobalOcr.Salida="NORMAL";
//    	    		Toast.makeText(mContext, "** ¡SALIENDO de INMAEst! ** Global.rutaAjusteG: "+Global.rutaAjusteG, Toast.LENGTH_SHORT).show();
//    	    		Toast.makeText(mContext, "** ¡SALIENDO de INMAEst! ** GlobalOcr.rutaFotos: "+GlobalOcr.rutaFotos, Toast.LENGTH_SHORT).show();
    	    	//}else{
    	    		//String eObject2 = eObject;
    	    		//resultIntent.putExtra("eObject", eObject);
    	    		//resultIntent.putExtra("rutaAjuste", rutaAjuste);
//    	    		Toast.makeText(mContext, "** ¡mismoFolder! ** "+Global.mismoFolder, Toast.LENGTH_SHORT).show();	
    	    	//}
    	    		
//				resultIntent.putExtra("oPerEstatus", "NORMAL");
//				resultIntent.putExtra("mismoFolder", Global.mismoFolder);
//				resultIntent.putExtra("rutaAjuste", Global.rutaAjusteG);
//				resultIntent.putExtra("rutaFotos", GlobalOcr.rutaFotos);
				//startActivityForResult(resultIntent, REQUEST_CODE);
				startActivity(resultIntent);
    	    	//setResult(Activity.RESULT_OK, resultIntent);
				appendLog(" class: DroidGap.java - PROC: processEstimate() - Carga exitosa los Extras [eObject] con json de Salida,  y ejecucion de Intent de Activity de retorno"+eObject);
				// *** Este Mensaje no se habilita para la version Mercantil ***        	
				//Toast.makeText(mContext, "class: DroidGap.java - PROC: processEstimate() - ** ¡Estimacion comprometida! ** "+eObject, Toast.LENGTH_SHORT).show();				
				
				//startActivity(resultIntent);
				delete_FileBackupjson();
	        	appendLog(" class: DroidGap.java - PROC: processEstimate() - Finalizacion de la Activity - Salida Normal de la Aplicacion!");
	        	//activity.finish();				
    	    }
    	    catch(Exception e)
    	    {
    	    	appendLog(" class: DroidGap.java - PROC:  processEstimate() - Exception al intentar cargar los Extras [eObject] al Intent o al ejecutar el Intent del Activity de vuelta a Synergy - Mensaje de Error: " +  e.getMessage());
    	         e.printStackTrace();
    	         e.getMessage();
    	    }
    	    
    	    //Se envia el Json a traves del WebService para la Sincronizacion con INMAEst PC
    	    //Aqui se ejecuta el Proceso asyncronico de la Tablet
    	    //call(eObject);
    	    
    	    // Se procede a borrar el archivo fisico de respaldo
        	
//        	
//        	// Se invoca el final de la aplicacion!
//        	appendLog(" class: DroidGap.java - PROC: processEstimate() - Finalizacion de la Activity - Salida Normal de la Aplicacion!");
        	activity.finish();

        }  
	    
	    public void requestFocus(final String id)  {
	    	inputId = id;
	    	new Thread("After sometime Focus") {
	    	    public void run() {
	    	        try {
	    	            Thread.sleep(50);
	    	        } catch (InterruptedException e) {
	    	            e.printStackTrace();
	    	        }
	    	        
	    	        javaFocus(true);
	    	    }
	    	}.start();
       
	    }  
	    
	    public void goNormalApp() {
	    	appView.loadUrl("file:///android_asset/www/index2.html");
	        //Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
	    }

	    public String getIMEI() {
	    	TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    	appendLog(" class: DroidGap.java - PROC: getIMEI() - Obtencion del DeviceID (IMEI): " + telephonyManager.getDeviceId().toString());
	    	return telephonyManager.getDeviceId().toString();
	    }
	    
	    /** Show a toast from the web page */
	    public void showToast(String toast) {
	        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	    }
	    
	    /** Show a toast from the web page */
	    public void  showToastL(String toast) {
	        //Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
	        mostrarToast(toast);
	    }	    

		public void appendLogJS(String text)
		{
			//Toast.makeText(mContext, "appendLogJS: "+text, Toast.LENGTH_LONG).show();
			//appendLog(fechaLogD +  text);
			appendLog(text);
		}
	    
	    public void getImage(String toast, String imageId, String actualService) {
	        //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	        //loadUrl("javascript:getImage('"+imageId+"','"+actualService+"');");
	        //this.appView.loadUrl("javascript:try{PhoneGap.onPause.fire();}catch(e){};");
	        //appView.loadUrl("javascript:getImage('"+imageId+"','"+actualService+"');");
	    	
	    	getImage d = new getImage();
	    	String[] query = {imageId,actualService};
	    	d.execute(query);

	    }	    

	    public void openDatabaseEstimates(){
      		if (this.myDbEstimates != null) {
    			this.myDbEstimates.close();
    		}
    		
            try {
            	myDbEstimates = SQLiteDatabase.openDatabase(path+"estimates.sqlite", null, SQLiteDatabase.OPEN_READWRITE);

            } catch (SQLiteException sqle) {
            	appendLog(" class: DroidGap.java - PROC: openDatabaseEstimates() - No se pudo abrir o no existe la Base de Datos: estimates.sqlite - " + sqle);
            	//Toast.makeText(mContext, "No se pudo abrir o no existe la Base de Datos: estimates.sqlite - "+sqle, Toast.LENGTH_LONG).show();
            	mostrarToast("No se pudo abrir o no existe la Base de Datos: estimates.sqlite - "+sqle);
            	//throw sqle;
            }       		
      		//myDbEstimates = SQLiteDatabase.openDatabase(path+"estimates.sqlite", null, SQLiteDatabase.OPEN_READWRITE);
    		
    		//System.out.println("OPEN ESTIMATES!");
    		appendLog(" class: DroidGap.java - PROC: openDatabaseEstimates() - Apertura exitosa de la B.D. : estimates.sqlite, para escritura/lectura");
    		//Toast.makeText(mContext, "DB READY myDbEstimates", Toast.LENGTH_SHORT).show();			

	    }
	    
	    public void openDatabaseAjustes(){
      		if (this.myDbAjustes != null) {
    			this.myDbAjustes.close();
    		}
    		
            try {
            	myDbAjustes = SQLiteDatabase.openDatabase(path+"ajustes.sqlite", null, SQLiteDatabase.OPEN_READWRITE);

            } catch (SQLiteException sqle) {
            	appendLog(" class: DroidGap.java - PROC: openDatabaseAjustes() - No se pudo abrir o no existe la Base de Datos: ajustes.sqlite - " + sqle);
            	//Toast.makeText(mContext, "No se pudo abrir o no existe la Base de Datos: estimates.sqlite - "+sqle, Toast.LENGTH_LONG).show();
            	mostrarToast("No se pudo abrir o no existe la Base de Datos: ajustes.sqlite - "+sqle);
            	//throw sqle;
            }       		
    		
    		appendLog(" class: DroidGap.java - PROC: openDatabaseAjustes() - Apertura exitosa de la B.D. : ajustes.sqlite, para escritura/lectura");
    		//Toast.makeText(mContext, "DB READY myDbAjustes", Toast.LENGTH_SHORT).show();			

	    }	    
	    
	    public void openDatabase_Version(String databaseid){

      		if (this.myDbVersion != null) {
    			this.myDbVersion.close();
    		}
      		
            try {
            	myDbVersion = SQLiteDatabase.openDatabase(path+databaseid, null, SQLiteDatabase.OPEN_READWRITE);
            	//mostrarToast("myDbVersion: "+myDbVersion);
            	appendLog(" class: DroidGap.java - PROC: openDatabase_Version() - Apertura exitosa de la B.D. : myDbVersion.sqlite, para escritura/lectura: "+ path+databaseid);
            } catch (SQLiteException sqle) {
            	appendLog(" class: DroidGap.java - PROC: openDatabase_Version() - No se pudo abrir o no existe la Base de Datos: " + path+databaseid+" - "+sqle);
            	//Toast.makeText(mContext, "No se pudo abrir o no existe la Base de Datos: "+path+databaseid+ " - "+sqle, Toast.LENGTH_LONG).show();
            	mostrarToast("class: DroidGap.java - PROC: openDatabase_Version() - No se pudo abrir o no existe la Base de Datos: " + path+databaseid+" - "+sqle);
                //throw sqle;
				return;
            } 
//            finally {
//
//            	myDbVersion.close();
//            }
      		//Toast.makeText(mContext, "path+databaseid: "+path+databaseid, Toast.LENGTH_LONG).show();
    		//myDbVersion = SQLiteDatabase.openDatabase(path+databaseid, null, SQLiteDatabase.OPEN_READWRITE);
    	
    		appendLog(" class: DroidGap.java - PROC: openDatabase_Version() - Se Abrio exitosamente la Base de Datos: " + path+databaseid);
    		//Toast.makeText(mContext, "DB READY", Toast.LENGTH_SHORT).show();			
	    	
	    }	       
	    
	    public void save_Backup2(String json) {
	    	// Procedimiento de Autosave o BackUp de los Items de Estimaciones
			// Para el caso de que ocurra un error, se recupere lo ya estimado
	    	//Toast.makeText(mContext, "json: "+json, Toast.LENGTH_LONG).show();

    		ContentValues values = new ContentValues();
    		//Toast.makeText(mContext, "values antes: "+values, Toast.LENGTH_LONG).show();
    		values.put("Json", json);
    		//Toast.makeText(mContext, "values despues del push: "+values, Toast.LENGTH_SHORT).show();
			//*** Las siguientes Dos (2) lineas de codigo se comentaron para NO llamar el almacenamiento en Base de Datos ***
    		// myDbEstimates.insert("estimates", "", values);
    		// processEstimate(json);
			
    		//*** El siguiente bloque de codigo se agrego para descargar el JSon en un archivo fisico ***
    		String sFileName = "FileBackupjson";
    	    try
    	    {
    	        File root = new File(Environment.getExternalStorageDirectory(), "download");
    	        if (!root.exists()) {
    	            root.mkdirs();
    	        }
    	        File gpxfile = new File(root, sFileName);
    	       FileWriter writer = new FileWriter(gpxfile,true);
    	       //FileWriter writer = new FileWriter(gpxfile);
    	        writer.append(json);
    	        //json="";
    	        writer.flush();
    	        writer.close();
    	        //Toast.makeText(mContext, "** ¡Estimacion respaldada! **", Toast.LENGTH_SHORT).show();
    	        appendLog(" class: DroidGap.java - PROC: save_Backup2() - Se guardo exitosamente el archivo: FileBackupjson, Estimacion respaldada!");
    	    }
    	    catch(IOException e)
    	    {
    	    	appendLog(" class: DroidGap.java - PROC:  save_Backup2() - I/O Exception al intentar guardar el archivo: FileBackupjson, o al escribir en el mismo: " +  e.getMessage());
    	        e.printStackTrace();
    	        e.getMessage();
    	    }
    	  //*** Fin de bloque de codigo para descargar el JSon de BackUp en un archivo fisico ***
    	    
	    }
	    
	    public void delete_FileBackupjson() {
	    	
	    	String sFileName = "FileBackupjson";
	    	
    	    try
    	    {
    	        File root = new File(Environment.getExternalStorageDirectory(), "download");
				File fi = new File(root, sFileName);
				
				if(fi.exists()) {
					fi.delete();
					appendLog(" class: DroidGap.java - PROC: delete_FileBackupjson() - Se elimino exitosamente el archivo: FileBackupjson!");
					//Toast.makeText(mContext, "FileBackupjson Borrado", Toast.LENGTH_LONG).show();
				}   	    	
    	    }
    	    catch(Exception e)
    	    {
    	    	appendLog(" class: DroidGap.java - PROC:  delete_FileBackupjson() - Exception al intentar eliminar el archivo: FileBackupjson: " +  e.getMessage());
    	        e.printStackTrace();
    	        e.getMessage();
    	    }	    	
	    }
	    
	    public void save_BackupU(String json) {
	    	// Procedimiento de Autosave o BackUp de los Items de Estimaciones Modificados
			// Para el caso de que ocurra un error, se recupere lo ya modificado.
	    	//Toast.makeText(mContext, "json: "+json, Toast.LENGTH_LONG).show();

    		ContentValues values = new ContentValues();
    		//Toast.makeText(mContext, "values antes: "+values, Toast.LENGTH_LONG).show();
    		values.put("Json", json);
    		//Toast.makeText(mContext, "values despues: "+values, Toast.LENGTH_LONG).show();
    		//*** El siguiente bloque de codigo se agrego para descargar el JSon en un archivo fisico ***
    		String sFileName = "FileBackupjson";
			
    		//delete_FileBackupjson();
    		
    	    try
    	    {
    	        File root = new File(Environment.getExternalStorageDirectory(), "download");
    	        if (!root.exists()) {
    	            root.mkdirs();
    	        }    	        
//				File fi = new File(root, sFileName);
//				
//				if(fi.exists()) {
//					fi.delete();
//					Toast.makeText(mContext, "FileBackupjson Borrado", Toast.LENGTH_LONG).show();
//				}   	       
				
    	        File gpxfile = new File(root, sFileName);
    	        FileWriter writer = new FileWriter(gpxfile, true);

    	        writer.append(json);
    	        writer.flush();
    	        writer.close();
    	        appendLog(" class: DroidGap.java - PROC: save_BackupU() - Se guardo exitosamente el archivo: FileBackupjson, modificacion respaldada!");
    	        //Toast.makeText(mContext, "** ¡Modificacion respaldada! **", Toast.LENGTH_SHORT).show();
    	    }
    	    catch(IOException e)
    	    {
    	    	appendLog(" class: DroidGap.java - PROC:  save_BackupU() - I/O Exception al intentar guardar el archivo: FileBackupjson: " +  e.getMessage());
    	         e.printStackTrace();
    	         e.getMessage();
    	    }
    	  //*** Fin de bloque de codigo para descargar el JSon de BackUp en un archivo fisico ***
    	    
	    }	 
		
	    public void save_SubModel(String json) {

			//*** El siguiente bloque de codigo se agrego para descargar el JSon en un archivo fisico ***
			String sFileName = "FilejsonInput";
			
			try
			{
				File rootB = new File(Environment.getExternalStorageDirectory(), "download");
				File fiB = new File(rootB, sFileName);
				
				if(fiB.exists()) {
					fiB.delete();
					//Toast.makeText(mContext, "FilejsonInput Borrado", Toast.LENGTH_LONG).show();
				} 
				appendLog(" class: DroidGap.java - PROC: save_SubModel() -  En el ARCHIVO: FilejsonInput, se guardaron los valores del Vehículo que se va a ajustar");
			}
			catch(Exception e)
			{
				appendLog(" class: DroidGap.java - PROC: save_SubModel() -  Exception al intentar eliminar el archivo: FilejsonInput" + e.getMessage());
				 e.printStackTrace();
				 e.getMessage();
			}				
					
			try
			{
				File root = new File(Environment.getExternalStorageDirectory(), "download");
				if (!root.exists()) {
					root.mkdirs();
				}
				File gpxfile = new File(root, sFileName);
				FileWriter writer = new FileWriter(gpxfile);
				writer.append(json);
				writer.flush();
				writer.close();
				appendLog(" class: DroidGap.java - PROC: save_SubModel() - Se guardo exitosamente el archivo: FilejsonInput, SubModelo guardado!");
				//Toast.makeText(mContext, "save_SubModel FilejsonInput con SubModelo Guardado! "+json, Toast.LENGTH_LONG).show();
			}
			catch(IOException e)
			{
				appendLog(" class: DroidGap.java - PROC:  save_SubModel() - I/O Exception al intentar guardar el archivo: FilejsonInput: " +  e.getMessage());
				e.printStackTrace();
				e.getMessage();
			}
		}		
	    
	    private boolean checkDataBase(String DB_NAME){
	           boolean checkdb = false;
	           //Toast.makeText(mContext, "checkDataBase checkdb: "+checkdb, Toast.LENGTH_LONG).show();
	           File f = new File("/mnt/sdcard/databasedata/"+DB_NAME);
	           if(f.exists()){
	        	   checkdb = true;
	        	   //appendLog(" class: DroidGap.java - PROC: checkDataBase() - No se pudo abrir o no existe la Base de Datos: " + DB_NAME);
	        	   //Toast.makeText(mContext, "No se pudo abrir o no existe la Base de Datos: /mnt/sdcard/databasedata/"+DB_NAME, Toast.LENGTH_LONG).show();
	        	   //mostrarToast("No se pudo abrir o no existe la Base de Datos: /mnt/sdcard/databasedata/"+DB_NAME);
	        	   //appView.loadUrl("javascript:basedatos='"+DB_NAME+"';");
	               //System.out.println("File exists");
	           }else{
	        	   mostrarToast("No se pudo abrir o no existe la Base de Datos: /mnt/sdcard/databasedata/"+DB_NAME);
					appendLog(" class: DroidGap.java - PROC: checkDataBase() - No se pudo abrir o no existe la Base de Datos: " + DB_NAME);
	        	   //Toast.makeText(mContext, "No se pudo abrir o no existe la Base de Datos: /mnt/sdcard/databasedata/"+DB_NAME, Toast.LENGTH_LONG).show();
	           }
//	           try{
//	               String myPath = myContext.getFilesDir().getAbsolutePath().replace("files", "databases")+File.separator + DB_NAME;
//	               File dbfile = new File(myPath);                
//	               checkdb = dbfile.exists();
//	           }
//	           catch(SQLiteException e){
//	               System.out.println("Database doesn't exist");
//	           }
	           return checkdb;
	       }		    
	    
//	    private boolean checkDataBase(String DB_NAME){
//	           boolean checkdb = false;
//
//			File sbd = new File(Environment.getExternalStorageDirectory(), "databases/"+DB_NAME);
//	           Toast.makeText(mContext, "sbd: "+sbd, Toast.LENGTH_LONG).show();
//			if (!sbd.exists()) {
//				Toast.makeText(mContext, "No se pudo abrir o no existe la Base de Datos: "+path+"carselector.db", Toast.LENGTH_LONG).show();
//				
//			}
////	           try{
////	               String myPath = myContext.getFilesDir().getAbsolutePath().replace("files", "databases")+File.separator + DB_NAME;
////	               File dbfile = new File(myPath);                
////	               checkdb = dbfile.exists();
////	           }
////	           catch(SQLiteException e){
////	               System.out.println("Database doesn't exist");
////	           }
//	           Toast.makeText(mContext, "checkdb: "+checkdb, Toast.LENGTH_LONG).show();
//	           return checkdb;
//	       }	    
//	    
	    public void openDatabase(String databaseid){
      		if (this.myDb != null) {
    			this.myDb.close();
    		}
    		
            try {
            	myDb = SQLiteDatabase.openDatabase(path+databaseid+".db", null, SQLiteDatabase.OPEN_READWRITE);

            } catch (SQLiteException sqle) {
            	appendLog(" class: DroidGap.java - PROC: openDatabase() - No se pudo abrir o no existe la Base de Datos: " + path+databaseid+".db"+" - "+sqle);
            	Toast.makeText(mContext, "No se pudo abrir o no existe la Base de Datos: "+path+databaseid+".db"+ " - "+sqle, Toast.LENGTH_LONG).show();
            	//mostrarToast("No se pudo abrir o no existe la Base de Datos: "+path+databaseid+".db"+ " - "+sqle);
                //throw sqle;
            }	
    		
    		System.out.println("OPEN DA DB!");
    		appendLog(" class: DroidGap.java - PROC: openDatabase() - Se Abrio exitosamente la Base de Datos: " + path+databaseid+".db");
    		//Toast.makeText(mContext, "DB READY", Toast.LENGTH_SHORT).show();			
	    	
	    }

	    public void openDatabaseCarSelector(){
      		if (this.myDbCS != null) {
    			this.myDbCS.close();
    		}
//      		boolean blncheckdb = false;
//      		blncheckdb = checkDataBase("carselector.db");
//      		if(!blncheckdb){
//      			Toast.makeText(mContext, "No se pudo abrir o no existe la Base de Datos: "+path+"carselector.db", Toast.LENGTH_LONG).show();
//      		}
    		try{
    			File fil = new File("/mnt/sdcard/databasedata/carselector.db");
    			if(fil.exists()){
    			}else{
    				appendLog(" class: DroidGap.java - PROC: openDatabaseCarSelector() - No existe la Base de Datos: " + path+"carselector.db");
    				finish();
    			}		
    	    } catch (Exception  ex) {
    	        ex.printStackTrace();
				appendLog(" class: DroidGap.java - PROC: openDatabaseCarSelector() - No existe la Base de Datos: " + path+"carselector.db"+" - "+ex);
				finish();	        
    	    }      		
      		
            try {
            	myDbCS = SQLiteDatabase.openDatabase(path+"carselector.db", null, SQLiteDatabase.OPEN_READWRITE);

            } catch (SQLiteException sqle) {
            	appendLog(" class: DroidGap.java - PROC: openDatabaseCarSelector() - No se pudo abrir o no existe la Base de Datos: " + path+"carselector.db"+" - "+sqle);
            	//Toast.makeText(mContext, "No se pudo abrir o no existe la Base de Datos: "+path+"carselector.db"+ " - "+sqle, Toast.LENGTH_LONG).show();
            	//mostrarToast("No se pudo abrir o no existe la Base de Datos: "+path+"carselector.db"+ " - "+sqle);
            	Toast.makeText(mContext, "No se pudo abrir o no existe la Base de Datos: "+path+"carselector.db"+ " - "+sqle + " .", Toast.LENGTH_LONG).show();
            	return;
                //throw sqle;
            }       		
    		
    		System.out.println("DB CAR SELECTOR OPEN!");
    		appendLog(" class: DroidGap.java - PROC: openDatabaseCarSelector() - Se Abrio exitosamente la Base de Datos: " + path + "carselector.db");
    		//Toast.makeText(mContext, "DB READY", Toast.LENGTH_SHORT).show();			

	    }	    
	    
	    public void openDatabaseViewSelector(){
      		if (this.myDbVS != null) {
    			this.myDbVS.close();
    		}
    		
            try {
            	myDbVS = SQLiteDatabase.openDatabase(path+"viewselector.db", null, SQLiteDatabase.OPEN_READWRITE);

            } catch (SQLiteException sqle) {
            	appendLog(" class: DroidGap.java - PROC: openDatabaseViewSelector() - No se pudo abrir o no existe la Base de Datos: " + path+"viewselector.db"+" - "+sqle);
            	//Toast.makeText(mContext, "No se pudo abrir o no existe la Base de Datos: "+path+"viewselector.db"+ " - "+sqle, Toast.LENGTH_LONG).show();
            	//mostrarToast("No se pudo abrir o no existe la Base de Datos: "+path+"viewselector.db"+ " - "+sqle);
            	Toast.makeText(mContext, "No se pudo abrir o no existe la Base de Datos: "+path+"viewselector.db"+ " - "+sqle + " .", Toast.LENGTH_SHORT).show();
            	//throw sqle;
            }        		
    		
    		
    		System.out.println("DB VIEW SELECTOR OPEN!");
    		appendLog(" class: DroidGap.java - PROC: openDatabaseViewSelector() - Se Abrio exitosamente la Base de Datos: " + path+"viewselector.db");
    		//Toast.makeText(mContext, "DB READY", Toast.LENGTH_SHORT).show();
	    	
	    }	    	    

	    public void registerData() {

	    	Cursor cursor = null;

					cursor = myDb.rawQuery("SELECT * FROM SERVICES WHERE PATH LIKE '%IMAGE%';", null);
	 		    	if (cursor.moveToFirst()) {
	 		           do {
	 		             
	 		             appView.loadUrl("javascript:registerData('"+cursor.getString(1)+"','"+cursor.getString(2)+"');");
	 		             appendLog(" class: DroidGap.java - PROC: registerData() - Se lleno exitosamente el cursor de imagenes: " + "myDb - SELECT * FROM SERVICES WHERE PATH LIKE '%IMAGE%';");
		 		         //System.out.println("javascript:registerData: "+cursor.getString(2));

	 		           
	 		           } while (cursor.moveToNext());
	 		        }
	 		        if (cursor != null && !cursor.isClosed()) {
	 		           cursor.close();
	 		        }

	    }
	    
	    public synchronized String getDataVersion(String query) {
		//Toast.makeText(mContext, "query: "+query, Toast.LENGTH_LONG).show();	
    	Cursor c = null;
    	String data;
		try {
		    c = this.myDbVersion.rawQuery(query, null);
	    	c.moveToFirst();
	    	if(c.isFirst())
	    	{	  
				data = c.getString(0)+"-"+c.getString(1)+"-"+c.getString(2);
				//Toast.makeText(mContext, "this.myDb: "+this.myDbVersion, Toast.LENGTH_LONG).show();
				//Toast.makeText(mContext, "data: "+data, Toast.LENGTH_LONG).show();
				c.close();
				appendLog(" class: DroidGap.java - PROC: getData() - Se lleno exitosamente el cursor de data: " + "c - " + query);
				return data;
	    	}
	    }catch (CursorIndexOutOfBoundsException ex) {
	    	appendLog(" class: DroidGap.java - PROC:  getData() - CursorIndexOutOfBoundsException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
	    	c.close();
	    	//Toast.makeText(mContext, "ERROR: "+query, Toast.LENGTH_LONG).show();			
	    	
	    }catch (SQLiteException ex) {
	    	appendLog(" class: DroidGap.java - PROC:  getData() - SQLiteException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
	    	//Toast.makeText(mContext, "ERROR: "+query, Toast.LENGTH_LONG).show();			

		}finally{
	    	//Toast.makeText(mContext, "READY", 3).show();
			c.close();
		}
    	return "";
    }		
		
	    
	    public synchronized String getData(String query) {
		//Toast.makeText(mContext, "query: "+query, Toast.LENGTH_LONG).show();
	    	Cursor c = null;
	    	String data;
	    	appendLog(" class: DroidGap.java - PROC: getData() - query: " + query);
			try {
 		    	c = this.myDb.rawQuery(query, null);
		    	c.moveToFirst();
		    	if(c.isFirst())
		    	{	  
					data = c.getString(2);
//					Toast.makeText(mContext, "this.myDb: "+this.myDb, Toast.LENGTH_LONG).show();
//					Toast.makeText(mContext, "data: "+data, Toast.LENGTH_LONG).show();
					c.close();
					appendLog(" class: DroidGap.java - PROC: getData() - Se lleno exitosamente el cursor de data: " + "c - " + query);
					return data;
		    	}
		    }catch (CursorIndexOutOfBoundsException ex) {
		    	appendLog(" class: DroidGap.java - PROC:  getData() - CursorIndexOutOfBoundsException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
		    	c.close();
		    	//Toast.makeText(mContext, "ERROR: "+query, Toast.LENGTH_LONG).show();			
		    	
		    }catch (SQLiteException ex) {
		    	appendLog(" class: DroidGap.java - PROC:  getData() - SQLiteException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
		    	//Toast.makeText(mContext, "ERROR: "+query, Toast.LENGTH_LONG).show();			

			}finally{
		    	//Toast.makeText(mContext, "READY", 3).show();
				c.close();
			}
	    	return "{}";
	    	//Toast.makeText(mContext, "56784568456856...", Toast.LENGTH_LONG).show();
	    	/*DownloadFilesTask d = new DownloadFilesTask();
	    	d.execute(query);
	    	try {
				return d.get().toString();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "{}";
*/
	    }

	    
	    public String getDataViewSelector(String query) {
	    	Cursor c = null;
	    	String data;
	    	//Toast.makeText(mContext, "String query: "+query, Toast.LENGTH_LONG).show();	
			try {
 		    	c = this.myDbVS.rawQuery(query, null);
		    	c.moveToFirst();
	    		data = c.getString(2);
	    		//Toast.makeText(mContext, "data en getDataViewSelector: "+data, Toast.LENGTH_LONG).show();	
	    		c.close();
	    		//appendLog(" class: DroidGap.java - PROC: getDataViewSelector() - Se lleno exitosamente el cursor de data: " + "c - " + query);
		    	return data;
		    }catch (CursorIndexOutOfBoundsException ex) {
		    	//Toast.makeText(mContext, "ERROR: "+query, Toast.LENGTH_LONG).show();			
		    	appendLog(" class: DroidGap.java - PROC:  getDataViewSelector() - CursorIndexOutOfBoundsException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
		    }catch (SQLiteException ex) {
		    	//Toast.makeText(mContext, "ERROR: "+query, Toast.LENGTH_LONG).show();			
		    	appendLog(" class: DroidGap.java - PROC:  getDataViewSelector() - SQLiteException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
			}finally{
		    	//Toast.makeText(mContext, "READY", 3).show();	
				
			}
	    	
			return "{}";

	    }	 	    
	    
	    public String getDataCarSelector(String query) {
	    	Cursor c = null;
	    	String data;
			//Toast.makeText(mContext, "query: "+query, Toast.LENGTH_LONG).show();
			try {
 		    	c = this.myDbCS.rawQuery(query, null);
		    	c.moveToFirst();
	    		data = c.getString(2);
	    		//Toast.makeText(mContext, "data: "+data, Toast.LENGTH_LONG).show();
//	    		Toast.makeText(mContext, "data0: "+c.getString(0), Toast.LENGTH_LONG).show();
//	    		Toast.makeText(mContext, "data1: "+c.getString(1), Toast.LENGTH_LONG).show();
	    		c.close();
	    		appendLog(" class: DroidGap.java - PROC: getDataCarSelector() - Se lleno exitosamente el cursor de data: " + "c - " + query);
		    	return data;
		    }catch (CursorIndexOutOfBoundsException ex) {
		    	//Toast.makeText(mContext, "ERROR: "+query+" - "+ex, Toast.LENGTH_LONG).show();			
		    	appendLog(" class: DroidGap.java - PROC:  getDataCarSelector() - CursorIndexOutOfBoundsException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
		    }catch (SQLiteException ex) {
		    	//Toast.makeText(mContext, "ERROR: "+query+" - "+ex, Toast.LENGTH_LONG).show();			
		    	appendLog(" class: DroidGap.java - PROC:  getDataCarSelector() - SQLiteException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
			}finally{
		    	//Toast.makeText(mContext, "READY", 3).show();			
				
			}
	    	
			return "{}";

	    }	    
	}
   	
   	
	/**
	 * Set the WebViewClient.
	 * 
	 * @param appView
	 * @param client
	 */
	protected void setWebViewClient(WebView appView, WebViewClient client) {
		this.webViewClient = client;
		appView.setWebViewClient(client);

	
	}

    /**
     * Bind PhoneGap objects to JavaScript.
     * 
     * @param appView
     */
	private void bindBrowser(WebView appView) {
		this.callbackServer = new CallbackServer();
		this.pluginManager = new PluginManager(appView, this);

	}
        
	/**
	 * Look at activity parameters and process them.
	 * This must be called from the main UI thread.
	 */
	private void handleActivityParameters() {

		// Init web view if not already done
		if (this.appView == null) {
			this.init();
		}

		// If spashscreen
		this.splashscreen = this.getIntegerProperty("splashscreen", 0);
		if (this.splashscreen != 0) {
			root.setBackgroundResource(this.splashscreen);
		}

		// If loadInWebView
		this.loadInWebView = this.getBooleanProperty("loadInWebView", false);

		// If loadUrlTimeoutValue
		int timeout = this.getIntegerProperty("loadUrlTimeoutValue", 0);
		if (timeout > 0) {
			this.loadUrlTimeoutValue = timeout;
		}
		
		// If keepRunning
		this.keepRunning = this.getBooleanProperty("keepRunning", true);
	}
	
    /**
     * Load the url into the webview.
     * 
     * @param url
     */
	public void loadUrl(final String url) {
		System.out.println("loadUrl("+url+")");
		this.url = url;
		if (this.baseUrl == null) {
			int i = url.lastIndexOf('/');
			if (i > 0) {
				this.baseUrl = url.substring(0, i+1);
			}
			else {
				this.baseUrl = this.url + "/";
			}
		}
		
		System.out.println("url="+url+" baseUrl="+baseUrl);

		// Load URL on UI thread
		final DroidGap me = this;
		this.runOnUiThread(new Runnable() {
			public void run() {

				// Handle activity parameters
				me.handleActivityParameters();

				// Initialize callback server
				me.callbackServer.init(url);

				// If loadingDialog, then show the App loading dialog
				String loading = me.getStringProperty("loadingDialog", null);
				if (loading != null) {

					String title = "";
					String message = "Loading Application...";

					if (loading.length() > 0) {
						int comma = loading.indexOf(',');
						if (comma > 0) {
							title = loading.substring(0, comma);
							message = loading.substring(comma+1);
						}
						else {
							title = "";
							message = loading;
						}
					}
					me.spinnerStart(title, message);
				}

				// Create a timeout timer for loadUrl
				final int currentLoadUrlTimeout = me.loadUrlTimeout;
				Runnable runnable = new Runnable() {
					public void run() {
						try {
							synchronized(this) {
								wait(me.loadUrlTimeoutValue);
							}
						} catch (InterruptedException e) {
							appendLog(" class: DroidGap.java - PROC: loadUrl() - run() - InterruptedException al intentar ejecutar el proceso run(): " + e.getMessage());
							e.printStackTrace();
						}		

						// If timeout, then stop loading and handle error
						if (me.loadUrlTimeout == currentLoadUrlTimeout) {
							me.appView.stopLoading();
							me.webViewClient.onReceivedError(me.appView, -6, "The connection to the server was unsuccessful.", url);
							appendLog(" class: DroidGap.java - PROC:  loadUrl() - run() - No pudo establecerse la conexion al Servidor (TimeOut) ");
						}
					}
				};
				Thread thread = new Thread(runnable);
				thread.start();
				me.appView.loadUrl(url);
			}
		});
	}
	
	/**
	 * Load the url into the webview after waiting for period of time.
	 * This is used to display the splashscreen for certain amount of time.
	 * 
	 * @param url
	 * @param time				The number of ms to wait before loading webview
	 */
	public void loadUrl(final String url, final int time) {
		System.out.println("loadUrl("+url+","+time+")");
		final DroidGap me = this;

		// Handle activity parameters
		this.runOnUiThread(new Runnable() {
			public void run() {
				me.handleActivityParameters();
			}
		});

		Runnable runnable = new Runnable() {
			public void run() {
				try {
					synchronized(this) {
						this.wait(time);
					}
				} catch (InterruptedException e) {
					appendLog(" class: DroidGap.java - PROC: loadUrl(url,time) [2] - run() - InterruptedException al intentar ejecutar el proceso run(): " + e.getMessage());
					e.printStackTrace();
				}
				if (!me.cancelLoadUrl) {
					me.loadUrl(url);
				}
				else{
					me.cancelLoadUrl = false;
					System.out.println("Aborting loadUrl("+url+"): Another URL was loaded before timer expired.");
					appendLog(" class: DroidGap.java - PROC:  loadUrl(url,time) [2] - run() - Otro URL fue cargado antes de que el timer expirara! ");
				}
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}
	
	/**
	 * Cancel loadUrl before it has been loaded.
	 */
	public void cancelLoadUrl() {
		this.cancelLoadUrl = true;
	}
	
	/**
	 * Clear the resource cache.
	 */
	public void clearCache() {
		if (this.appView == null) {
			this.init();
		}
		this.appView.clearCache(true);
		appendLog(" class: DroidGap.java - PROC:  clearCache() - El cache fue limpiado exitosamente! ");
	}

    /**
     * Clear web history in this web view.
     */
    public void clearHistory() {
    	this.clearHistory = true;
    	if (this.appView != null) {
    		this.appView.clearHistory();
    	}
    }

    @Override
    /**
     * Called by the system when the device configuration changes while your activity is running. 
     * 
     * @param Configuration newConfig
     */
    public void onConfigurationChanged(Configuration newConfig) {
    	//don't reload the current page when the orientation is changed
    	super.onConfigurationChanged(newConfig);
    }
    
    /**
     * Get boolean property for activity.
     * 
     * @param name
     * @param defaultValue
     * @return
     */
    public boolean getBooleanProperty(String name, boolean defaultValue) {
    	Bundle bundle = this.getIntent().getExtras();
    	if (bundle == null) {
    		return defaultValue;
    	}
    	Boolean p = (Boolean)bundle.get(name);
    	if (p == null) {
    		return defaultValue;
    	}
    	return p.booleanValue();
    }

    /**
     * Get int property for activity.
     * 
     * @param name
     * @param defaultValue
     * @return
     */
    public int getIntegerProperty(String name, int defaultValue) {
    	Bundle bundle = this.getIntent().getExtras();
    	if (bundle == null) {
    		return defaultValue;
    	}
    	Integer p = (Integer)bundle.get(name);
    	if (p == null) {
    		return defaultValue;
    	}
    	return p.intValue();
    }

    /**
     * Get string property for activity.
     * 
     * @param name
     * @param defaultValue
     * @return
     */
    public String getStringProperty(String name, String defaultValue) {
    	Bundle bundle = this.getIntent().getExtras();
    	if (bundle == null) {
    		return defaultValue;
    	}
    	String p = bundle.getString(name);
    	if (p == null) {
    		return defaultValue;
    	}
    	return p;
    }

    /**
     * Get double property for activity.
     * 
     * @param name
     * @param defaultValue
     * @return
     */
    public double getDoubleProperty(String name, double defaultValue) {
    	Bundle bundle = this.getIntent().getExtras();
    	if (bundle == null) {
    		return defaultValue;
    	}
    	Double p = (Double)bundle.get(name);
    	if (p == null) {
    		return defaultValue;
    	}
    	return p.doubleValue();
    }

    /**
     * Set boolean property on activity.
     * 
     * @param name
     * @param value
     */
    public void setBooleanProperty(String name, boolean value) {
    	this.getIntent().putExtra(name, value);
    }
    
    /**
     * Set int property on activity.
     * 
     * @param name
     * @param value
     */
    public void setIntegerProperty(String name, int value) {
    	this.getIntent().putExtra(name, value);
    }
    
    /**
     * Set string property on activity.
     * 
     * @param name
     * @param value
     */
    public void setStringProperty(String name, String value) {
    	this.getIntent().putExtra(name, value);
    }

    /**
     * Set double property on activity.
     * 
     * @param name
     * @param value
     */
    public void setDoubleProperty(String name, double value) {
    	this.getIntent().putExtra(name, value);
    }
    
    @Override
    /**
     * Called when the system is about to start resuming a previous activity. 
     */
    protected void onPause() {
        super.onPause();
        if (this.appView == null) {
        	return;
        }
        
       	// Send pause event to JavaScript
       	this.appView.loadUrl("javascript:try{PhoneGap.onPause.fire();}catch(e){};"); 

      	// Forward to plugins
    	this.pluginManager.onPause(this.keepRunning);

        // If app doesn't want to run in background
        if (!this.keepRunning) {

        	// Pause JavaScript timers (including setInterval)
        	this.appView.pauseTimers();
        }
    }

    @Override
    /**
     * Called when the activity receives a new intent
     **/
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);

    	//Forward to plugins
    	this.pluginManager.onNewIntent(intent);
    }
    
    @Override
    /**
     * Called when the activity will start interacting with the user. 
     */
    protected void onResume() {
        super.onResume();
        if (this.appView == null) {
        	return;
        }

       	// Send resume event to JavaScript
       	this.appView.loadUrl("javascript:try{PhoneGap.onResume.fire();}catch(e){};");

      	// Forward to plugins
    	this.pluginManager.onResume(this.keepRunning || this.activityResultKeepRunning);

        // If app doesn't want to run in background
        if (!this.keepRunning || this.activityResultKeepRunning) {

   		 	// Restore multitasking state
        	if (this.activityResultKeepRunning) {
        		this.keepRunning = this.activityResultKeepRunning;
        		this.activityResultKeepRunning = false;
        	}

        	// Resume JavaScript timers (including setInterval)
        	this.appView.resumeTimers();
        }
    }
    
    @Override
    /**
     * The final call you receive before your activity is destroyed. 
     */
    public void onDestroy() {
    	super.onDestroy();
    	
        if (this.appView != null) {
    	
        	// Make sure pause event is sent if onPause hasn't been called before onDestroy
        	this.appView.loadUrl("javascript:try{PhoneGap.onPause.fire();}catch(e){};");

        	// Send destroy event to JavaScript
        	this.appView.loadUrl("javascript:try{PhoneGap.onDestroy.fire();}catch(e){};");

        	// Load blank page so that JavaScript onunload is called
        	this.appView.loadUrl("about:blank");
    	    	
        	// Forward to plugins
        	this.pluginManager.onDestroy();

        }
    }

    /**
     * Add a class that implements a service.
     * 
     * @param serviceType
     * @param className
     */
    public void addService(String serviceType, String className) {
    	this.pluginManager.addService(serviceType, className);
    }
    
    /**
     * Send JavaScript statement back to JavaScript.
     * (This is a convenience method)
     * 
     * @param message
     */
    public void sendJavascript(String statement) {
    	this.callbackServer.sendJavascript(statement);
    }
    
    
    /**
     * Display a new browser with the specified URL.
     * 
     * NOTE: If usePhoneGap is set, only trusted PhoneGap URLs should be loaded,
     *       since any PhoneGap API can be called by the loaded HTML page.
     *
     * @param url           The url to load.
     * @param usePhoneGap   Load url in PhoneGap webview.
     * @param clearPrev		Clear the activity stack, so new app becomes top of stack
     * @param params		DroidGap parameters for new app
     * @throws android.content.ActivityNotFoundException
     */
    public void showWebPage(String url, boolean usePhoneGap, boolean clearPrev, HashMap<String, Object> params) throws android.content.ActivityNotFoundException {
    	Intent intent = null;
    	if (usePhoneGap) {
    		intent = new Intent().setClass(this, com.phonegap.DroidGap.class);
    		intent.putExtra("url", url);

    		// Add parameters
    		if (params != null) {
    			java.util.Set<Entry<String,Object>> s = params.entrySet();
    			java.util.Iterator<Entry<String,Object>> it = s.iterator();
    			while(it.hasNext()) {
    				Entry<String,Object> entry = it.next();
    				String key = entry.getKey();
    				Object value = entry.getValue();
    				if (value == null) {
    				}
    				else if (value.getClass().equals(String.class)) {
    					intent.putExtra(key, (String)value);
    				}
    				else if (value.getClass().equals(Boolean.class)) {
    					intent.putExtra(key, (Boolean)value);
    				}
    				else if (value.getClass().equals(Integer.class)) {
    					intent.putExtra(key, (Integer)value);
    				}
    			}

    		}                
    	}
    	else {
    		intent = new Intent(Intent.ACTION_VIEW);
    		intent.setData(Uri.parse(url));
    	}
    	this.startActivity(intent);
    	
    	// Finish current activity
		if (clearPrev) {
			this.finish();
		}
    }
    
    /**
     * Show the spinner.  Must be called from the UI thread.
     * 
     * @param title			Title of the dialog
     * @param message		The message of the dialog
     */
    public void spinnerStart(final String title, final String message) {
    	if (this.spinnerDialog != null) {
    		this.spinnerDialog.dismiss();
    		this.spinnerDialog = null;
    	}
    	final DroidGap me = this;
    	this.spinnerDialog = ProgressDialog.show(DroidGap.this, title , message, true, true, 
    			new DialogInterface.OnCancelListener() { 
    		public void onCancel(DialogInterface dialog) {
    			me.spinnerDialog = null;
    		}
    	});
    }

    /**
     * Stop spinner.
     */
    public void spinnerStop() {
    	if (this.spinnerDialog != null) {
    		this.spinnerDialog.dismiss();
    		this.spinnerDialog = null;
		}
    }

    /**
     * Provides a hook for calling "alert" from javascript. Useful for
     * debugging your javascript.
     */
    public class GapClient extends WebChromeClient {

        private DroidGap ctx;
        
        /**
         * Constructor.
         * 
         * @param ctx
         */
        public GapClient(Context ctx) {
            this.ctx = (DroidGap)ctx;
        }

        /**
         * Tell the client to display a javascript alert dialog.
         * 
         * @param view
         * @param url
         * @param message
         * @param result
         */
  


        
/*        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        	//appView.loadUrl("javascript:dojo.style(dojo.byId('LOADER'), {display: ''});");
        	String tester = "";
        }*/
     
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this.ctx);
            dlg.setMessage(message);
            dlg.setTitle("Alert");
            dlg.setCancelable(false);
            dlg.setPositiveButton(android.R.string.ok,
            	new AlertDialog.OnClickListener() {
                	public void onClick(DialogInterface dialog, int which) {
                		result.confirm();
                	}
            	});
            dlg.create();
            dlg.show();
            return true;
        }       

        /**
         * Tell the client to display a confirm dialog to the user.
         * 
         * @param view
         * @param url
         * @param message
         * @param result
         */
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this.ctx);
            dlg.setMessage(message);
            dlg.setTitle("Confirm");
            dlg.setCancelable(false);
            dlg.setPositiveButton(android.R.string.ok, 
            	new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int which) {
                		result.confirm();
                    }
                });
            dlg.setNegativeButton(android.R.string.cancel, 
            	new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int which) {
                		result.cancel();
                    }
                });
            dlg.create();
            dlg.show();
            return true;
        }

        /**
         * Tell the client to display a prompt dialog to the user. 
         * If the client returns true, WebView will assume that the client will 
         * handle the prompt dialog and call the appropriate JsPromptResult method.
         * 
         * @param view
         * @param url
         * @param message
         * @param defaultValue
         * @param result
         */
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        	
        	// Security check to make sure any requests are coming from the page initially
        	// loaded in webview and not another loaded in an iframe.
        	boolean reqOk = false;
        	if (url.indexOf(this.ctx.baseUrl) == 0) {
        		reqOk = true;
        	}
			
        	// Calling PluginManager.exec() to call a native service using 
        	// prompt(this.stringify(args), "gap:"+this.stringify([service, action, callbackId, true]));
        	if (reqOk && defaultValue != null && defaultValue.length() > 3 && defaultValue.substring(0, 4).equals("gap:")) {
        		JSONArray array;
        		try {
        			array = new JSONArray(defaultValue.substring(4));
        			String service = array.getString(0);
        			String action = array.getString(1);
        			String callbackId = array.getString(2);
        			boolean async = array.getBoolean(3);
        			String r = pluginManager.exec(service, action, callbackId, message, async);
        			result.confirm(r);
        		} catch (JSONException e) {
        			appendLog(" class: DroidGap.java - PROC:  onJsPrompt() - JSONException al intentar crear y/o manipular el JSONArray: array - : " +  e.getMessage());
        			e.printStackTrace();
        		}
        	}
        	
        	// Polling for JavaScript messages 
        	else if (reqOk && defaultValue.equals("gap_poll:")) {
        		String r = callbackServer.getJavascript();
        		result.confirm(r);
        	}
        	
        	// Calling into CallbackServer
        	else if (reqOk && defaultValue.equals("gap_callbackServer:")) {
        		String r = "";
        		if (message.equals("usePolling")) {
        			r = ""+callbackServer.usePolling();
        		}
        		else if (message.equals("restartServer")) {
        			callbackServer.restartServer();
        		}
        		else if (message.equals("getPort")) {
        			r = Integer.toString(callbackServer.getPort());
        		}
        		else if (message.equals("getToken")) {
        			r = callbackServer.getToken();
        		}
        		result.confirm(r);
        	}
        	
        	// Show dialog
        	else {
				final JsPromptResult res = result;
				AlertDialog.Builder dlg = new AlertDialog.Builder(this.ctx);
				dlg.setMessage(message);
				final EditText input = new EditText(this.ctx);
				if (defaultValue != null) {
					input.setText(defaultValue);
				}
				dlg.setView(input);
				dlg.setCancelable(false);
				dlg.setPositiveButton(android.R.string.ok, 
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						String usertext = input.getText().toString();
						res.confirm(usertext);
					}
				});
				dlg.setNegativeButton(android.R.string.cancel, 
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						res.cancel();
					}
				});
				dlg.create();
				dlg.show();
			}
        	return true;
        }
        
    }
    
    /**
     * WebChromeClient that extends GapClient with additional support for Android 2.X
     */
    public class EclairClient extends GapClient {

    	private String TAG = "PhoneGapLog";
    	private long MAX_QUOTA = 100000 * 1024 * 1024;

    	/**
    	 * Constructor.
    	 * 
    	 * @param ctx
    	 */
    	public EclairClient(Context ctx) {
    		super(ctx);
    	}

    	/**
    	 * Handle database quota exceeded notification.
    	 *
    	 * @param url
    	 * @param databaseIdentifier
    	 * @param currentQuota
    	 * @param estimatedSize
    	 * @param totalUsedQuota
    	 * @param quotaUpdater
    	 */
    	@Override
    	public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize,
    			long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater)
    	{
    		Log.d(TAG, "event raised onExceededDatabaseQuota estimatedSize: " + Long.toString(estimatedSize) + " currentQuota: " + Long.toString(currentQuota) + " totalUsedQuota: " + Long.toString(totalUsedQuota));
    		appendLog(" class: DroidGap.java - PROC: onExceededDatabaseQuota() - event raised onExceededDatabaseQuota estimatedSize: " + Long.toString(estimatedSize) + " currentQuota: " + Long.toString(currentQuota) + " totalUsedQuota: " + Long.toString(totalUsedQuota));
    		if( estimatedSize < MAX_QUOTA)
    		{	                                        
    			//increase for 1Mb
    			long newQuota = estimatedSize;		    		
    			Log.d(TAG, "calling quotaUpdater.updateQuota newQuota: " + Long.toString(400000000) );
    			appendLog(" class: DroidGap.java - PROC: onExceededDatabaseQuota() - calling quotaUpdater.updateQuota newQuota: " + Long.toString(400000000) );
    			quotaUpdater.updateQuota(400000000);
    		}
    		else
    		{
    			// Set the quota to whatever it is and force an error
    			// TODO: get docs on how to handle this properly
    			quotaUpdater.updateQuota(400000000);
    		}		    	
    	}		

    	// console.log in api level 7: http://developer.android.com/guide/developing/debug-tasks.html
    	@Override
    	public void onConsoleMessage(String message, int lineNumber, String sourceID)
    	{       
    		// This is a kludgy hack!!!!
    		Log.d(TAG, sourceID + ": Line " + Integer.toString(lineNumber) + " : " + message);
    		//appendLog(" class: DroidGap.java - PROC: onConsoleMessage() - " + sourceID + ": Line: " + Integer.toString(lineNumber) + " : " + message );
    	}

    	@Override
    	/**
    	 * Instructs the client to show a prompt to ask the user to set the Geolocation permission state for the specified origin. 
    	 * 
    	 * @param origin
    	 * @param callback
    	 */
    	public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
    		// TODO Auto-generated method stub
    		super.onGeolocationPermissionsShowPrompt(origin, callback);
    		callback.invoke(origin, true, false);
    	}

    }

    /**
     * The webview client receives notifications about appView
     */
    public class GapViewClient extends WebViewClient {

        DroidGap ctx;

        /**
         * Constructor.
         * 
         * @param ctx
         */
        public GapViewClient(DroidGap ctx) {
            this.ctx = ctx;
        }
        
        /**
         * Give the host application a chance to take over the control when a new url 
         * is about to be loaded in the current WebView.
         * 
         * @param view			The WebView that is initiating the callback.
         * @param url			The url to be loaded.
         * @return				true to override, false for default behavior
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	// If dialing phone (tel:5551212)
        	if (url.startsWith(WebView.SCHEME_TEL)) {
        		try {
        			Intent intent = new Intent(Intent.ACTION_DIAL);
        			intent.setData(Uri.parse(url));
        			startActivity(intent);
        		} catch (android.content.ActivityNotFoundException e) {
        			System.out.println("Error dialing "+url+": "+ e.toString());
        			appendLog(" class: DroidGap.java - PROC:  shouldOverrideUrlLoading() - android.content.ActivityNotFoundException al manipular (Dial) un INTENT - : " +  e.getMessage());
        		}
        		return true;
        	}
        	
        	// If displaying map (geo:0,0?q=address)
        	else if (url.startsWith("geo:")) {
           		try {
        			Intent intent = new Intent(Intent.ACTION_VIEW);
        			intent.setData(Uri.parse(url));
        			startActivity(intent);
        		} catch (android.content.ActivityNotFoundException e) {
        			System.out.println("Error showing map "+url+": "+ e.toString());
        			appendLog(" class: DroidGap.java - PROC:  shouldOverrideUrlLoading() - android.content.ActivityNotFoundException al manipular (ACTION_VIEW) un INTENT - : Error showing map "+url+": "+ e.toString());
        		}
        		return true;        		
        	}
			
        	// If sending email (mailto:abc@corp.com)
        	else if (url.startsWith(WebView.SCHEME_MAILTO)) {
           		try {
        			Intent intent = new Intent(Intent.ACTION_VIEW);
        			intent.setData(Uri.parse(url));
        			startActivity(intent);
        		} catch (android.content.ActivityNotFoundException e) {
        			System.out.println("Error sending email "+url+": "+ e.toString());
        			appendLog(" class: DroidGap.java - PROC:  shouldOverrideUrlLoading() - android.content.ActivityNotFoundException al manipular (ACTION_VIEW / WebView.SCHEME_MAILTO) un INTENT - : Error sending email "+url+": "+ e.toString());
        		}
        		return true;        		
        	}
        	
        	// If sms:5551212?body=This is the message
        	else if (url.startsWith("sms:")) {
        		try {
        			Intent intent = new Intent(Intent.ACTION_VIEW);

        			// Get address
        			String address = null;
        			int parmIndex = url.indexOf('?');
        			if (parmIndex == -1) {
        				address = url.substring(4);
        			}
        			else {
        				address = url.substring(4, parmIndex);

        				// If body, then set sms body
        				Uri uri = Uri.parse(url);
        				String query = uri.getQuery();
        				if (query != null) {
        					if (query.startsWith("body=")) {
        						intent.putExtra("sms_body", query.substring(5));
        					}
        				}
        			}
        			intent.setData(Uri.parse("sms:"+address));
        			intent.putExtra("address", address);
        			intent.setType("vnd.android-dir/mms-sms");
        			startActivity(intent);
        		} catch (android.content.ActivityNotFoundException e) {
        			System.out.println("Error sending sms "+url+":"+ e.toString());
        			appendLog(" class: DroidGap.java - PROC:  shouldOverrideUrlLoading() - android.content.ActivityNotFoundException al manipular (address) un INTENT - : Error sending sms "+url+": "+ e.toString());
        		}
        		return true;
        	}

        	// All else
        	else {

        		// If our app or file:, then load into our webview
        		// NOTE: This replaces our app with new URL.  When BACK is pressed,
        		//       our app is reloaded and restarted.  All state is lost.
        		if (this.ctx.loadInWebView || url.startsWith("file://") || url.indexOf(this.ctx.baseUrl) == 0) {
        			try {
        				// Init parameters to new DroidGap activity and propagate existing parameters
        				HashMap<String, Object> params = new HashMap<String, Object>();
        				params.put("loadingDialog", null);
        				if (this.ctx.loadInWebView) {
        					params.put("loadInWebView", true);
        				}
        				params.put("keepRunning", this.ctx.keepRunning);
        				params.put("loadUrlTimeoutValue", this.ctx.loadUrlTimeoutValue);
        				String errorUrl = this.ctx.getStringProperty("errorUrl", null);
        				if (errorUrl != null) {
        					params.put("errorUrl", errorUrl);
        				}

        				this.ctx.showWebPage(url, true, false, params);
        			} catch (android.content.ActivityNotFoundException e) {
        				System.out.println("Error loading url into DroidGap - "+url+":"+ e.toString());
        				appendLog(" class: DroidGap.java - PROC:  shouldOverrideUrlLoading() - android.content.ActivityNotFoundException al manipular (loading url into DroidGap) un INTENT - : Error loading url into DroidGap "+url+": "+ e.toString());
        			}
        		}
  		
        		// If not our application, let default viewer handle
        		else {
        			try {
        				Intent intent = new Intent(Intent.ACTION_VIEW);
        				intent.setData(Uri.parse(url));
        				startActivity(intent);
                	} catch (android.content.ActivityNotFoundException e) {
                		appendLog(" class: DroidGap.java - PROC: shouldOverrideUrlLoading() - android.content.ActivityNotFoundException al manipular un INTENT - (ACTION_VIEW) if NOT our application - android.content.ActivityNotFoundException:  "+url+": "+ e.toString());
                		System.out.println("Error loading url "+url+":"+ e.toString());
                	}
        		}
        		return true;
        	}
        }
 
		@Override  
        public void onPageStarted(WebView view, String url, Bitmap favicon)  {
			  //pd = ProgressDialog.show(this.ctx, "", "cargando InmaEstMobile", true, false);
            
        }  
        
        
        /**
         * Notify the host application that a page has finished loading.
         * 
         * @param view			The webview initiating the callback.
         * @param url			The url of the page.
         */
        @Override
        public void onPageFinished(WebView view, String url) {
        	super.onPageFinished(view, url);

        	// Clear timeout flag
        	this.ctx.loadUrlTimeout++;

        	// Try firing the onNativeReady event in JS. If it fails because the JS is
        	// not loaded yet then just set a flag so that the onNativeReady can be fired
        	// from the JS side when the JS gets to that code.
        	if (!url.equals("about:blank")) {
        		appView.loadUrl("javascript:try{ PhoneGap.onNativeReady.fire();}catch(e){_nativeReady = true;}");
        	}

        	// Make app view visible
        	appView.setVisibility(View.VISIBLE);

        	// Stop "app loading" spinner if showing
       		this.ctx.spinnerStop();

    		// Clear history, so that previous screen isn't there when Back button is pressed
    		if (this.ctx.clearHistory) {
    			this.ctx.clearHistory = false;
    			this.ctx.appView.clearHistory();
    		}
    		
    		// Shutdown if blank loaded
    		if (url.equals("about:blank")) {
    			if (this.ctx.callbackServer != null) {
    				this.ctx.callbackServer.destroy();
    			}
    		}
            //pd.dismiss();

//            File fil = new File("/mnt/sdcard/databasedata/carselector.db");
//            if(fil.exists()){
//         	   //checkdb = true;
//         	   //Toast.makeText(this.ctx, "No se pudo abrir o no existe la Base de Datos: /mnt/sdcard/databasedata/carselector.db", Toast.LENGTH_LONG).show();
//         	  //appView.stopLoading();
//         	   //appView.loadUrl("file:///android_asset/www/NoDatabase.html");
//         	  appView.stopLoading();
//            }else{
//         	   
//         	   Toast.makeText(this.ctx, "No existe la Base de Datos: /mnt/sdcard/databasedata/carselector.db", Toast.LENGTH_LONG).show();
//            }
    //	   	   boolean blncheck = false;
    //	   	   blncheck = checkDataBase("carselector.db");
    //	        if(blncheck){
    //	        	Toast.makeText(Soa.this, "blncheck dentro if: "+blncheck, Toast.LENGTH_LONG).show();
    //	        	//setDBName("carselector.db");
    //				//super.loadUrl("file:///android_asset/www/NoDatabase.html");
    //	        }else{
    //	        	
    //	        	
    //	        }    		
    		
    		//Toast.makeText(this.ctx, "sEstimateStatus: "+sEstimateStatus, Toast.LENGTH_LONG).show();
    		if(sEstimateStatus.equals("SENT")){
    			appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  Fin de Procedimientos y Funciones! - Salida Normal de DroidGap.java -  Salida Normal de INMAEst Movilidad hacia VehiculoActivity");
    		}
    		if(sEstimateStatus.equals("ANORMAL")){
    			appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  Salida Anormal de DroidGap.java a requisicion del Usuario [Esc]-  Salida Anormal de INMAEst Movilidad hacia VehiculoActivity");
    			return;
    		}    		

    		
	if(StringUtils.isBlank(json)){
		 
		//appView.loadUrl("file:///android_asset/www/index2.html");
		 
	}else{
		 // String anio = "";
		 // String mark = "";
		 // String model = "";
		 // String submodel = "";
		  //String placa = "";
		 // //String nombre = "";
		 // String odometer = "";
		 // String vehicleIdNumber = "";	
		 // String estimator = "";
		 // //String Color="";	
		 // String estimateId="";	
		String sProfileName = "";
		String sTallerName = "";
		String sNumSiniestro = "";
		
		 JSONObject o = eObject;
		 
		try {
			o = eObject.getJSONObject("eObject");
			o = o.getJSONObject("auto");
			//Toast.makeText(this.ctx, "Droidgap o: "+o, Toast.LENGTH_LONG).show();
			// anio = o.getString("anio");
			// mark = o.getString("marca");
			// model = o.getString("modelo");	
			// estimator = o.getString("Estimator");
			// //Toast.makeText(this.ctx, "estimator: "+estimator, Toast.LENGTH_LONG).show();					
			// submodel = o.getString("SubModel");
			// //Toast.makeText(this.ctx, "model: "+model, Toast.LENGTH_LONG).show();
			// //Toast.makeText(this.ctx, "submodel: "+submodel, Toast.LENGTH_LONG).show();
			// odometer = o.getString("Odometer");
			// //Toast.makeText(this.ctx, "odometer: "+odometer, Toast.LENGTH_LONG).show();
			// vehicleIdNumber = o.getString("serialCarroceria");	
			placa = o.getString("placa");
			sProfileName = o.getString("perfil");
			sTallerName = o.getString("taller");
			sNumSiniestro = o.getString("numsiniestro");
			//Toast.makeText(this.ctx, "DroidGap placa: "+placa, Toast.LENGTH_LONG).show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " - JSONException al intentar obtener los elementos del Json "+ e.getMessage());
			e.printStackTrace();
		}
			 
		//*************************************************************************
		// Aqui se evaluaria el valor del parametro bandera que pasaria SYNERGY
		// OPCION #1 de la propuesta. Si es valor positivo, se procedera a llamar
		// al procedimiento de recuperacion de la Data de la Sesion anterior, la
		// cual esta guardada en el archivo: "/mnt/sdcard/download/filejsonbackup".
		//*************************************************************************
		 
		//****************************************************************************************
		// Aqui tambien se evaluaria si existe el archivo: "/mnt/sdcard/download/filejsonbackup".
		// OPCION #2 de la propuesta. Si existe, se procedera a llamar al procedimiento de 
		// recuperacion de la Data de la Sesion anterior, la cual esta guardada en el archivo: 
		// "/mnt/sdcard/download/filejsonbackup". La recuperacion se haria llamando a una funcion 
		// en Javascript: appView.loadUrl("javascript:getRestore('"......
		//****************************************************************************************		
		 
			//*************************************************************
			// AQUI SE EVALUARA SI EXISTE EL ARCHIVO: 
			// Si existe, entonces hubo un error que obligo
			// a cerrar la Aplicacion ANORMALMENTE.
			// y se Procederia a restaurar las Variables de Ambiente
			// con los valores existentes para ese momento:
			// partSelector_store
			//*************************************************************	
			String sFileNameIni = "/mnt/sdcard/download/FileBackupjson";
			String sFileNameInput = "/mnt/sdcard/download/FilejsonInput";
			
			File f = new File(sFileNameIni);
			//File f = new File(sFileNameIni).isFile();
			if(f.exists()) { 
			   			//Toast.makeText(this.ctx, "*** El Archivo: "+sFileNameIni + ", Existe ***", Toast.LENGTH_LONG).show(); 
			   			File fi = new File(sFileNameInput);
						// if(!fi.exists()) { 
							// Toast.makeText(this.ctx, "*** El Archivo: "+sFileNameIni + ", No Existe, o fue borrado! ***", Toast.LENGTH_LONG).show(); 
						// }
					    BufferedReader bri = null;
					    StringBuilder sbi = null;
					    BufferedReader br = null;
					    StringBuilder sb = null;		
					    
					    //JSONObject n = eObject;
						 String anio = "";
						 String mark = "";
						 String model = "";
						 String submodel = "";
						 //String placa = "";
						 String placaAnt = "";
						 String odometer = "";
						 //String vehicleIdNumber = "";	
						 String estimator = "";
						 String estimateId="";
						 String rutaAju="";
						 String mensaje="";
						 int sS = 0; 
						 boolean blnAuto=false;
						 
						    try {
									bri = new BufferedReader(new FileReader(sFileNameInput));
									sbi = new StringBuilder();
									String line = bri.readLine();

									while (line != null) {
									sbi.append(line);
									//sb.append("\n");
									line = bri.readLine();
									}

									String strinput = String.valueOf(sbi.toString());
									
									//Toast.makeText(this.ctx, "strinput: "+strinput, Toast.LENGTH_LONG).show();

									////n = eObject.getJSONObject(strinput);
									//n = eObject.getJSONObject("eObject");
									////n = n.getJSONObject("auto");
									//int len =strinput.length();
									//Toast.makeText(this.ctx, "Droidgap len: "+len, Toast.LENGTH_LONG).show();
									//Toast.makeText(this.ctx, "Droidgap strinput: "+strinput, Toast.LENGTH_LONG).show();
									int start=0;
									int send=0;
									start=strinput.indexOf("placa")+8;
									send=strinput.indexOf("serialCarroceria")-3;
									//Toast.makeText(this.ctx, "start: "+start, Toast.LENGTH_LONG).show();
									//Toast.makeText(this.ctx, "send: "+send, Toast.LENGTH_LONG).show();
									//placa = strinput.substring(start,send);
									//Toast.makeText(this.ctx, "placa: "+placa, Toast.LENGTH_LONG).show();
									placaAnt= strinput.substring(start,send);
									//Toast.makeText(this.ctx, "Droidgap placa: "+placaAnt, Toast.LENGTH_LONG).show();
								//****************************************************************************
								//*** Se evalua si son iguales las placas:                               *****
								//*** Si son diferentes; se procede regularmente                         *****
								//*** Si son iguales; se trata del mismo vehiculo de la Sesion anterior, ***** 
								//*** entonces se procede con la AutoRecuperacion.                       *****
								//****************************************************************************
									if(placa.equalsIgnoreCase(placaAnt)){								
										// *** Placas iguales: AUTORECUPERACION de la Sesion anterior  *******
										//Toast.makeText(this.ctx, "DENTRO DEL IF(PLACA==PLACANT)", Toast.LENGTH_LONG).show();
										start=strinput.indexOf("Estimator")+13;
										//Toast.makeText(this.ctx, "Droidgap Estimator start: "+start, Toast.LENGTH_LONG).show();
										send=strinput.indexOf("Odometer")-3;
										estimator = strinput.substring(start,send);
										//Toast.makeText(this.ctx, "Placas Diferentes AUTO REC. Droidgap estimator: "+estimator, Toast.LENGTH_LONG).show();
										sS = estimator.indexOf("S");
										if(sS<0){
											estimator="S"+estimator;
										}
										start=strinput.indexOf("Odometer")+12;
										send=strinput.indexOf("SubModel")-3;
										odometer = strinput.substring(start,send);
										//Toast.makeText(this.ctx, "Droidgap odometer: "+odometer, Toast.LENGTH_LONG).show();								
										start=strinput.indexOf("SubModel")+11;
										send=strinput.indexOf("anio")-3;
										submodel = strinput.substring(start,send);
										//Toast.makeText(this.ctx, "Droidgap SubModel: "+submodel, Toast.LENGTH_LONG).show();
										start=strinput.indexOf("anio")+7;
										send=strinput.indexOf("marca")-3;
										anio=strinput.substring(start,send);
//										Toast.makeText(this.ctx, "start: "+start, Toast.LENGTH_LONG).show();
//										Toast.makeText(this.ctx, "send: "+send, Toast.LENGTH_LONG).show();
//										Toast.makeText(this.ctx, "Droidgap anio: "+anio, Toast.LENGTH_LONG).show();
										start=strinput.indexOf("marca")+8;
										send=strinput.indexOf("modelo")-3;
										mark = strinput.substring(start,send);
										//Toast.makeText(this.ctx, "Droidgap mark: "+mark, Toast.LENGTH_LONG).show();
										start=strinput.indexOf("modelo")+9;
										send=strinput.indexOf("placa")-3;
										model = strinput.substring(start,send);
										//Toast.makeText(this.ctx, "Droidgap model: "+model, Toast.LENGTH_LONG).show();	
										start=strinput.indexOf("placa")+8;
										send=strinput.indexOf("serialCarroceria")-3;
										placa = strinput.substring(start,send);
										//Toast.makeText(this.ctx, "Droidgap placa: "+placa, Toast.LENGTH_LONG).show();
//										start=strinput.indexOf("serialCarroceria")+19;
//										send=strinput.indexOf("}}}")-1;
										start=strinput.indexOf("serialCarroceria")+19;
										send=strinput.indexOf("rutaAjuste")-3;
										vehicleIdNumber = strinput.substring(start,send);										
										//send=strinput.indexOf("Message03")-3;
										start=strinput.indexOf("Message03");
										//Toast.makeText(this.ctx, "Droidgap Message03 start: "+start, Toast.LENGTH_LONG).show();
										send=strinput.indexOf("}}}")-1;
										if(start>0){
											start=strinput.indexOf("Message03")+12;
											mensaje = strinput.substring(start,send);
											//Toast.makeText(this.ctx, "Droidgap mensaje : "+mensaje, Toast.LENGTH_LONG).show();
											start=strinput.indexOf("rutaAjuste")+13;
											send=strinput.indexOf("Message03")-3;
											rutaAju = strinput.substring(start,send);
//											Toast.makeText(this.ctx, "Droidgap if(start>0){ start: "+start, Toast.LENGTH_LONG).show();
//											Toast.makeText(this.ctx, "Droidgap if(start>0){ send: "+send, Toast.LENGTH_LONG).show();
//											Toast.makeText(this.ctx, "Droidgap if(start>0){ rutaAjuste: "+rutaAju, Toast.LENGTH_LONG).show();											
										}else{
											start=strinput.indexOf("rutaAjuste")+13;
											//send=strinput.indexOf("Message03")-3;
											rutaAju = strinput.substring(start,send);
											//Toast.makeText(this.ctx, "Droidgap else{ rutaAjuste: "+rutaAju, Toast.LENGTH_LONG).show();
										}										
//										send=strinput.indexOf("}}}")-1;
										rutaAjuste=rutaAju;
										
										if(rutaAjuste.length()>0 && (start-send)>0){
											start=rutaAju.indexOf(placa)+8;
											send=rutaAju.length()-1;
											estimateId=rutaAju.substring(start,send);;
											//Toast.makeText(this.ctx, "Droidgap estimateId: "+estimateId, Toast.LENGTH_LONG).show();
											
											Global.rutaAjusteG=rutaAjuste;
											//Toast.makeText(this.ctx, "Droidgap else{ rutaAjuste cambio: "+Global.rutaAjusteG, Toast.LENGTH_LONG).show();
											
											Global.autoRestore=1;											
										}
										

//										Toast.makeText(this.ctx, "Droidgap else{ Global.autoRestore: "+Global.autoRestore, Toast.LENGTH_LONG).show();
//										Toast.makeText(this.ctx, "Droidgap else{ Global.rutaAjusteG: "+Global.rutaAjusteG, Toast.LENGTH_LONG).show();
										//Toast.makeText(this.ctx, "Droidgap else{ rutaAjuste: "+rutaAjuste, Toast.LENGTH_LONG).show();
		//								start=strinput.indexOf("estimateId")+12;
		//								send=strinput.indexOf("}}}")-1;
		//								estimateId = strinput.substring(start,send);								
										//Toast.makeText(this.ctx, "Droidgap estimateId: "+estimateId, Toast.LENGTH_LONG).show();
									
										blnAuto=true;						
										appView.loadUrl("javascript:odometer='"+odometer+"';");
										appView.loadUrl("javascript:vehicleIdNumber='"+vehicleIdNumber+"';");	
										appView.loadUrl("javascript:Plate='"+placa+"';");
										appView.loadUrl("javascript:vvMessage03='"+mensaje+"';");
										//appView.loadUrl("javascript:Color='"+Color+"';");
										//Toast.makeText(this.ctx, " Estimator " + estimator, Toast.LENGTH_LONG).show();
										appView.loadUrl("javascript:jsonEstimator='"+estimator+"';");
										//Toast.makeText(this.ctx, " Estimator " + estimator, Toast.LENGTH_LONG).show();
										//appView.loadUrl("javascript:estimator='"+estimator+"';");
										//appView.loadUrl("javascript:EstimateId='"+estimateId+"';");
										//System.out.println(anio + "-"  + mark + "-"+ model);
										//appView.loadUrl("javascript:EstimateId='"+eObject.getJSONObject("eObject").getString("idEstimado")+"';");					        	
										 //appView.loadUrl("javascript:Nombre='"+nombre+"';");
										 appView.loadUrl("javascript:anio='"+anio+"';");
										 appView.loadUrl("javascript:jsonMark='"+mark+"';");
										 appView.loadUrl("javascript:jsonModel='"+model+"';");
										 appView.loadUrl("javascript:jsonSubModel='"+submodel+"';");
										 appView.loadUrl("javascript:rutaAjuste='"+rutaAjuste+"';");
										 appView.loadUrl("javascript:EstimateId='"+estimateId+"';");
										 //appView.loadUrl("javascript:getEstimator('"+estimator+"');");			 
										 //appView.loadUrl("javascript:goYear('"+anio+"');");
										 //appView.loadUrl("javascript:vColor='"+Color+"';");
										 //appView.loadUrl("javascript:getRestoreDataEstimations('"+ everything +"');");
										 //Toast.makeText(this.ctx, "estimator: "+estimator, Toast.LENGTH_LONG).show();
										 sEstimator=estimator;
										 //Toast.makeText(this.ctx, "blnAuto: "+blnAuto, Toast.LENGTH_LONG).show();
									}
						    	} catch (FileNotFoundException e) {
						    		appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " - FileNotFoundException El archivo: /mnt/sdcard/download/FileBackupjson, no existe, al intentar abrirlo! "+ e.getMessage());
						    		// TODO Auto-generated catch block
						    		e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  IOException al intentar leer/escribir o manipular el archivo: /mnt/sdcard/download/FileBackupjson! "+ e.getMessage());
								e.printStackTrace();
							} finally {
						        try {
									bri.close();
									appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  El archivo: /mnt/sdcard/download/FileBackupjson, EXISTE, y la placa es la misma. Entonces se procesa la AUTORECUPERACION de la sesión anterior!" );
								} catch (IOException e) {
									appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  IOException al intentar Cerrar el BufferedReader: bri, leyendo el archivo: /mnt/sdcard/download/FileBackupjson! "+ e.getMessage());
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						    }	
							//Toast.makeText(this.ctx, "DESPUES DEL IF - "+blnAuto, Toast.LENGTH_LONG).show();
//							if(blnAuto){
//										//Toast.makeText(this.ctx, " Recuperando la Sesion... ", Toast.LENGTH_LONG).show();  	
//										mostrarToast(" Recuperando la Sesion... ");
//										String everything = "";
//										//Toast.makeText(this.ctx, " El Archivo: "+sFileNameIni + ", Existe", Toast.LENGTH_LONG).show();
//								try {
//										br = new BufferedReader(new FileReader(sFileNameIni));
//										sb = new StringBuilder();
//										String line = br.readLine();
//
//										while (line != null) {
//											sb.append(line);
//											//sb.append("\n");
//											line = br.readLine();
//										}
//
//										everything = String.valueOf(sb.toString());
//										//Toast.makeText(this.ctx, " El Archivo: "+sFileNameIni +" : Contiene: "+ everything, Toast.LENGTH_LONG).show();
//										//*************************************************************************************************************************
//										// Aqui se llama a la funcion javascript, la cual pasa la data respaldada de las estimaciones en la variable: 'everything'
//										// al objeto: "dojo.data.ItemFileWriteStore": "partSelector_store".
//										//*************************************************************************************************************************
//										appView.loadUrl("javascript:getRestoreDataEstimations('"+ everything +"');");
//										appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  Se invoco exitosamente el procedimiento: getRestoreDataEstimations(), de DroidGap, con la data de recuperacion "+ everything);
//		//								int start1=everything.indexOf("EstimateId")+13;
//		//								int send1=everything.indexOf("EstimateDate")-3;
//		//								String sEstimateId = everything.substring(start1,send1);
//		//					        	appView.loadUrl("javascript:getIdEstimate('"+ placa +"');");
//										//String sEstimateId = "ID1234567890";
//										
//										//Toast.makeText(Âºthis.ctx, "Droidgap sEstimateId: "+sEstimateId, Toast.LENGTH_LONG).show();				        	
//										//appView.loadUrl("javascript:EstimateId='"+sEstimateId+"';");					        	
//										appView.loadUrl("javascript:goYear('"+anio+"');");
//										//appView.loadUrl("javascript:getTree('1195','1','Avant Quatro - 4Pt,Sedan - 6Cil., *i - Automatic');");
//												
//									} catch (FileNotFoundException e) {
//										// TODO Auto-generated catch block
//										appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  FileNotFoundException El archivo: " + sFileNameIni + " , no existe, al intentar abrirlo! "+ e.getMessage());
//										e.printStackTrace();
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  IOException al manipular el archivo: " + sFileNameIni + " -" + e.getMessage());
//									e.printStackTrace();
//								} finally {
//									try {
//										br.close();
//									} catch (IOException e) {
//										// TODO Auto-generated catch block
//										appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  IOException al intentar Cerrar el BufferedReader: br, leyendo el archivo: " + sFileNameIni + " -" + e.getMessage());
//										e.printStackTrace();
//									}
//								}
//								
//								//appView.loadUrl("javascript:goYear('"+anio+"');");
//		//						 appView.loadUrl("javascript:Nombre='"+nombre+"';");
//		//						 appView.loadUrl("javascript:anio='"+anio+"';");
//		//						 appView.loadUrl("javascript:jsonMark='"+mark+"';");
//		//						 appView.loadUrl("javascript:jsonModel='"+model+"';");
//		//						 appView.loadUrl("javascript:jsonSubModel='"+submodel+"';");
//		//						 appView.loadUrl("javascript:getEstimator('"+estimator+"');");			 
//
//		//						 appView.loadUrl("javascript:vColor='"+Color+"';");
//		//						 //appView.loadUrl("javascript:getRestoreDataEstimations('"+ everything +"');");
//		//						 //Toast.makeText(this.ctx, "estimator: "+estimator, Toast.LENGTH_LONG).show();
//		//						 sEstimator=estimator;
//								 //Toast.makeText(this.ctx, "sEstimator: "+sEstimator, Toast.LENGTH_LONG).show();
//								 //System.out.println("FURULO????????????");
//								 //Toast.makeText(this.ctx, "sEstimateStatus: "+sEstimateStatus, Toast.LENGTH_LONG).show();
//													
//								 //Toast.makeText(this.ctx, " Recuperando la SesiÃ³n... ", Toast.LENGTH_LONG).show();
//						 
//							}else{
//								//*************************************** blnAuto=false ******************************************************
//								//**** EL ARCHIVO: "FileBackupjson", EXISTE, PERO LAS PLACAS DIFERENTES, SE PROCEDE DE FORMA REGULAR ()  *****
//								//************************************************************************************************************
//								String sFileNameB = "FileBackupjson";
//								
//								try
//								{
//									File rootB = new File(Environment.getExternalStorageDirectory(), "download");
//									File fiB = new File(rootB, sFileNameB);
//									
//									if(fiB.exists()) {
//										fiB.delete();
//										//Toast.makeText(mContext, "FileBackupjson Borrado", Toast.LENGTH_LONG).show();
//									} 
//									appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  EL ARCHIVO: FileBackupjson, EXISTE, PERO LAS PLACAS DIFERENTES, SE PROCEDE DE FORMA REGULAR (Se elimina el archivo: FileBackupjson, pues se desecha la Autorecuperacion por desicion del Usuario!)");
//								}
//								catch(Exception e)
//								{
//									appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  Exception al intentar eliminar el archivo: FileBackupjson" + e.getMessage());
//									 e.printStackTrace();
//									 e.getMessage();
//								}	 				 
//								//*** El siguiente bloque de codigo se agrego para descargar el JSon en un archivo fisico ***
//								String sFileName = "FilejsonInput";
//								
//								try
//								{
//									File rootf = new File(Environment.getExternalStorageDirectory(), "download");
//									File fif = new File(rootf, sFileName);
//									
//									if(fif.exists()) {
//										fif.delete();
//										//Toast.makeText(this.ctx, "blnAuto=false FilejsonInput Borrado", Toast.LENGTH_LONG).show();
//									} 
//									appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  En el ARCHIVO: FilejsonInput, se guardaron los valores del Vehículo que se va a ajustar");
//								}
//								catch(Exception e)
//								{
//									appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  Exception al intentar eliminar el archivo: FilejsonInput" + e.getMessage());
//									 e.printStackTrace();
//									 e.getMessage();
//								}			
//								
//								
//								try
//								{
//									File root = new File(Environment.getExternalStorageDirectory(), "download");
//									if (!root.exists()) {
//										root.mkdirs();
//									}
//									File gpxfile = new File(root, sFileName);
//									FileWriter writer = new FileWriter(gpxfile);
//									writer.append(json);
//									writer.flush();
//									writer.close();
//									//Toast.makeText(this.ctx, "OnPage Finished blnAuto=false FilejsonInput Guardado", Toast.LENGTH_LONG).show();
//								}
//								catch(IOException e)
//								{
//									appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  IOException al intentar Crear el directorio: download, o al abrir/manipular el archivo: " + sFileName + " - " + e.getMessage());
//									 e.printStackTrace();
//									 e.getMessage();
//								}
//								 //JSONObject o = eObject;
//								 // String anio = "";
//								 // String mark = "";
//								 // String model = "";
//								 // String submodel = "";
//								 // //String placa = "";
//								 // //String nombre = "";
//								 // String odometer = "";
//								 // String vehicleIdNumber = "";	
//								 // String estimator = "";
//								 
//								try {
//									//o = eObject.getJSONObject("eObject");
//									//o = o.getJSONObject("auto");
//									//Toast.makeText(this.ctx, "Droidgap o: "+o, Toast.LENGTH_LONG).show();
//									/*n = n.getJSONObject("asegurado");*/
//									anio = o.getString("anio");
//									mark = o.getString("marca");
//									model = o.getString("modelo");	
//									estimator = o.getString("Estimator");
//									//Toast.makeText(this.ctx, "estimator: "+estimator, Toast.LENGTH_LONG).show();
//									//appView.loadUrl("javascript:estimator='"+estimator+"';");
//									//appView.loadUrl("javascript:jsonEstimator='"+estimator+"';");
//									//Toast.makeText(this.ctx, "FileBackupjson, EXISTE, PERO LAS PLACAS DIFERENTES, estimator: "+estimator, Toast.LENGTH_LONG).show();						
//									submodel = o.getString("SubModel");
//									//Toast.makeText(this.ctx, "model: "+model, Toast.LENGTH_LONG).show();
//									//Toast.makeText(this.ctx, "submodel: "+submodel, Toast.LENGTH_LONG).show();
//									odometer = o.getString("Odometer");
//									//appView.loadUrl("javascript:odometer='"+o.getString("Odometer")+"';");
//									//Toast.makeText(this.ctx, "odometer: "+odometer, Toast.LENGTH_LONG).show();
//									vehicleIdNumber = o.getString("serialCarroceria");
//									//appView.loadUrl("javascript:vehicleIdNumber='"+o.getString("serialCarroceria")+"';");	
//									placa = o.getString("placa");
//									//appView.loadUrl("javascript:Plate='"+o.getString("placa")+"';");
//									//Toast.makeText(this.ctx, "placa: "+placa, Toast.LENGTH_LONG).show();
//								} catch (JSONException e) {
//									// TODO Auto-generated catch block
//									appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  JSONException al manipular el json del eObject " + e.getMessage());
//									e.printStackTrace();
//								}
//
//								 //appView.loadUrl("javascript:Nombre='"+nombre+"';");
//								 appView.loadUrl("javascript:odometer='"+odometer+"';");
//								 appView.loadUrl("javascript:vehicleIdNumber='"+vehicleIdNumber+"';");
//								 appView.loadUrl("javascript:Plate='"+placa+"';");
//								 
//								 appView.loadUrl("javascript:anio='"+anio+"';");
//								 appView.loadUrl("javascript:jsonMark='"+mark+"';");
//								 appView.loadUrl("javascript:jsonModel='"+model+"';");
//								 appView.loadUrl("javascript:jsonSubModel='"+submodel+"';");
//								 //appView.loadUrl("javascript:getEstimator('"+estimator+"');");			 
//								 
//								 //appView.loadUrl("javascript:vColor='"+Color+"';");
//								 appView.loadUrl("javascript:jsonEstimator='"+estimator+"';");
//								 sEstimator=estimator;
//								 rutaAjuste = rutaAjuste + "/" + placa;
//								 appView.loadUrl("javascript:rutaAjuste='"+rutaAjuste+"';");
//								 //System.out.println("FURULO????????????");
//								 //Toast.makeText(this.ctx, "sEstimateStatus: "+sEstimateStatus, Toast.LENGTH_LONG).show();
//								 appView.loadUrl("javascript:goYear('"+anio+"');");
//								 appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " - LAS PLACAS DIFERENTES, SE PROCEDE DE FORMA REGULAR - Se obtuvieron los valores del vehiculo con exito, desde el archivo: FilejsonInput");
//									if(sEstimateStatus.equals("") && checkdb==false){
//										mostrarToast(" Cargando la informacion... ");
//										 //Toast.makeText(this.ctx, " Cargando la informacion... ", Toast.LENGTH_LONG).show();
//										 //Toast.makeText(this.ctx, " Cargando la informacion... ", Toast.LENGTH_LONG).show();
//										 //Toast.makeText(this.ctx, "sEstimateStatus model: "+model, Toast.LENGTH_LONG).show();
//										if(submodel.length() == 0){
//											if(model.equalsIgnoreCase("Silverado")){
//												//Toast.makeText(this.ctx, " Silverado ", Toast.LENGTH_LONG).show();
//												mostrarToast(" Cargando la informacion... ");
//												mostrarToast(" Cargando la informacion... ");
//												mostrarToast(" Cargando la informacion... ");
//												mostrarToast(" Cargando la informacion... ");
////												 Toast.makeText(this.ctx, " Cargando la informacion... ", Toast.LENGTH_LONG).show();
////												 Toast.makeText(this.ctx, " Cargando la informacion... ", Toast.LENGTH_LONG).show();
////												 Toast.makeText(this.ctx, " Cargando la informacion... ", Toast.LENGTH_LONG).show();
////												 Toast.makeText(this.ctx, " Cargando la informacion... ", Toast.LENGTH_SHORT).show();
//											}
//											if(model.equalsIgnoreCase("YARIS") || model.equalsIgnoreCase("COROLLA") || model.equalsIgnoreCase("GRAND VITARA")){
//												mostrarToast(" Cargando la informacion... "); 
//												//Toast.makeText(this.ctx, " Cargando la informacion... ", Toast.LENGTH_LONG).show();
//												 //Toast.makeText(this.ctx, " Cargando la informacion... ", Toast.LENGTH_SHORT).show();
//											}					
//											if(model.equalsIgnoreCase("Optra")){
//												mostrarToast(" Cargando la informacion... ");
//												mostrarToast(" Cargando la informacion... ");
////												 Toast.makeText(this.ctx, " Cargando la informacion... ", Toast.LENGTH_LONG).show();
////												 Toast.makeText(this.ctx, " Cargando la informacion... ", Toast.LENGTH_LONG).show();
//											}						 
//										}
//														
//									}	
//							}							
				//Toast.makeText(this.ctx, "FIN DEL BLOQUE DE CODIGO DE RESTAURACION DE SESION ANTERIOR"+blnAuto, Toast.LENGTH_LONG).show();							
			//******************************************************************************************************
			//***********    FIN DEL BLOQUE DE CODIGO DE RESTAURACION DE SESION ANTERIOR    ************************
			//******************************************************************************************************
		   	}else{
				//******************************************************************************************************
				//***********    EL ARCHIVO: "FileBackupjson", NO EXISTE. SE PROCEDE DE FORMA REGULAR   ****************
				//******************************************************************************************************
						
						//Toast.makeText(this.ctx, "*** El Archivo: "+sFileNameIni + ", NO Existe ***", Toast.LENGTH_LONG).show(); 
					//}						 
				//*** El siguiente bloque de codigo se agrego para descargar el JSon en un archivo fisico ***
				String sFileName = "FilejsonInput";
				
				try
				{
					File rootB = new File(Environment.getExternalStorageDirectory(), "download");
					File fiB = new File(rootB, sFileName);
					
					if(fiB.exists()) {
						fiB.delete();
						//Toast.makeText(mContext, "FilejsonInput Borrado", Toast.LENGTH_LONG).show();
					} 
					appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  En el ARCHIVO: FilejsonInput, se guardaron los valores del Vehículo que se va a ajustar");
				}
				catch(Exception e)
				{
					appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  Exception al intentar eliminar el archivo: FilejsonInput" + e.getMessage());
					 e.printStackTrace();
					 e.getMessage();
				}	
				
				try
				{
					File root = new File(Environment.getExternalStorageDirectory(), "download");
					if (!root.exists()) {
						root.mkdirs();
					}
					File gpxfile = new File(root, sFileName);
					FileWriter writer = new FileWriter(gpxfile);
					writer.append(json);
					writer.flush();
					writer.close();
					//Toast.makeText(this.ctx, "On Page Finished 2 EL ARCHIVO: FileBackupjson, NO EXISTE. SE PROCEDE DE FORMA REGULAR FilejsonInput guardado", Toast.LENGTH_LONG).show();
					appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  EL ARCHIVO: FileBackupjson, NO EXISTE, SE PROCEDE DE FORMA REGULAR!");
				}
				catch(IOException e)
				{
					appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  EL ARCHIVO: FileBackupjson, NO EXISTE, PROCESO REGULAR - IOException al intentar Crear el directorio: download, o al abrir/manipular el archivo: " + sFileName + " - " + e.getMessage());
					 e.printStackTrace();
					 e.getMessage();
				}
				 //JSONObject o = eObject;
				 String anio = "";
				 String mark = "";
				 String model = "";
				 String submodel = "";
				 //String placa = "";
				 //String nombre = "";
				 String odometer = "";
				 //String vehicleIdNumber = "";	
				 String estimator = "";
				 
				try {
					//o = eObject.getJSONObject("eObject");
					//o = o.getJSONObject("auto");
					//Toast.makeText(this.ctx, "Droidgap o: "+o, Toast.LENGTH_LONG).show();
					/*n = n.getJSONObject("asegurado");*/
					anio = o.getString("anio");
					mark = o.getString("marca");
					model = o.getString("modelo");	
					estimator = o.getString("Estimator");
					//Toast.makeText(this.ctx, " FileBackupjson, NO EXISTE estimator: "+estimator, Toast.LENGTH_LONG).show();
					//appView.loadUrl("javascript:estimator='"+estimator+"';");
					//appView.loadUrl("javascript:jsonEstimator='"+estimator+"';");
					//Toast.makeText(this.ctx, "estimator: "+estimator, Toast.LENGTH_LONG).show();						
					submodel = o.getString("SubModel");
					//Toast.makeText(this.ctx, "model: "+model, Toast.LENGTH_LONG).show();
					//Toast.makeText(this.ctx, "submodel: "+submodel, Toast.LENGTH_LONG).show();
					odometer = o.getString("Odometer");
					//appView.loadUrl("javascript:odometer='"+o.getString("Odometer")+"';");
					//Toast.makeText(this.ctx, "odometer: "+odometer, Toast.LENGTH_LONG).show();
					vehicleIdNumber = o.getString("serialCarroceria");
					//appView.loadUrl("javascript:vehicleIdNumber='"+o.getString("serialCarroceria")+"';");	
					placa = o.getString("placa");
					//appView.loadUrl("javascript:Plate='"+o.getString("placa")+"';");
					//Toast.makeText(this.ctx, "placa: "+placa, Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  EL ARCHIVO: FileBackupjson, NO EXISTE, PROCESO REGULAR -  JSONException al manipular el json del eObject !" + e.getMessage());
					e.printStackTrace();
				}

				 //appView.loadUrl("javascript:Nombre='"+nombre+"';");
				 appView.loadUrl("javascript:odometer='"+odometer+"';");
				 appView.loadUrl("javascript:vehicleIdNumber='"+vehicleIdNumber+"';");
				 appView.loadUrl("javascript:Plate='"+placa+"';");
				 
				 appView.loadUrl("javascript:anio='"+anio+"';");
				 appView.loadUrl("javascript:jsonMark='"+mark+"';");
				 appView.loadUrl("javascript:jsonModel='"+model+"';");
				 appView.loadUrl("javascript:jsonSubModel='"+submodel+"';");
				 appView.loadUrl("javascript:jsonProfileName='"+sProfileName+"';");
				 appView.loadUrl("javascript:jsonTallerName='"+sTallerName+"';");
				 appView.loadUrl("javascript:jsonNumSiniestro='"+sNumSiniestro+"';");
				 //appView.loadUrl("javascript:getEstimator('"+estimator+"');");			 
				 //appView.loadUrl("javascript:goYear('"+anio+"');");
				 //appView.loadUrl("javascript:vColor='"+Color+"';");
				 appView.loadUrl("javascript:jsonEstimator='"+estimator+"';");
				 sEstimator=estimator;
				 rutaAjuste = rutaAjuste + "/" + placa;
				 appView.loadUrl("javascript:rutaAjuste='"+rutaAjuste+"';");
				 //System.out.println("FURULO????????????");
				 //Toast.makeText(this.ctx, "sEstimateStatus: "+sEstimateStatus, Toast.LENGTH_LONG).show();
				 appView.loadUrl("javascript:goYear('"+anio+"');");
				 appendLog(" class: DroidGap.java - PROC: onPageFinished() PLACA: " + placa + " -  PROCEDE DE FORMA REGULAR - Se obtuvieron los valores del vehiculo con exito, desde el archivo: FilejsonInput");
				 
					if(sEstimateStatus.equals("") && checkdb==false){
						//mostrarToast(" Cargando la informacion... "); 
					}						 
				 
				}
			}
	//   		Toast.makeText(this.ctx, " voyyy... ", Toast.LENGTH_LONG).show();

    }
        
//	 public void SetEstimateStatus(){
//		 sEstimateStatus="ANORMAL";
//		 //Toast.makeText(this.ctx, "sEstimateStatus: "+sEstimateStatus, Toast.LENGTH_LONG).show();
//	 }
        
        /**
         * Report an error to the host application. These errors are unrecoverable (i.e. the main resource is unavailable). 
         * The errorCode parameter corresponds to one of the ERROR_* constants.
         *
         * @param view 			The WebView that is initiating the callback.
         * @param errorCode 	The error code corresponding to an ERROR_* value.
         * @param description 	A String describing the error.
         * @param failingUrl 	The url that failed to load. 
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        	System.out.println("onReceivedError: Error code="+errorCode+" Description="+description+" URL="+failingUrl);

        	// Clear timeout flag
        	this.ctx.loadUrlTimeout++;

       	 	// Stop "app loading" spinner if showing
       		this.ctx.spinnerStop();

        	// Handle error
        	this.ctx.onReceivedError(errorCode, description, failingUrl);
        }
        
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            
        	final String packageName = this.ctx.getPackageName();
    	    final PackageManager pm = this.ctx.getPackageManager();
    	    ApplicationInfo appInfo;
    		try {
    			appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
    	        if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
    	            // debug = true
    	        	handler.proceed();
    	            return;
    	        } else {
    	            // debug = false
    	        	super.onReceivedSslError(view, handler, error);    
    	        }
    		} catch (NameNotFoundException e) {
    			// When it doubt, lock it out!
    			appendLog(" class: DroidGap.java - PROC: onReceivedError() - NameNotFoundException al manipular el paquete!" + e.getMessage());
    			super.onReceivedSslError(view, handler, error);
    		}
        }
    }
    
    
    /**
     * Called when a key is pressed.
     * 
     * @param keyCode
     * @param event
     */
    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.appView == null) {
        	return super.onKeyDown(keyCode, event);
        }

    	// If back key
    	if (keyCode == KeyEvent.KEYCODE_BACK) {

    		// If back key is bound, then send event to JavaScript
    		if (this.bound) {
    			this.appView.loadUrl("javascript:PhoneGap.fireEvent('backbutton');");
    			return true;
    		}

    		// If not bound
    		else {

    			// Go to previous page in webview if it is possible to go back
    			if (this.appView.canGoBack()) {
    				this.appView.goBack();
    				return true;
    			}

    			// If not, then invoke behavior of super class
    			else {
    				return super.onKeyDown(keyCode, event);
    			}
    		}
    	}

    	// If menu key
    	else if (keyCode == KeyEvent.KEYCODE_MENU) {
    		this.appView.loadUrl("javascript:PhoneGap.fireEvent('menubutton');");
    		return true;
    	}

    	// If search key
    	else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
    		this.appView.loadUrl("javascript:PhoneGap.fireEvent('searchbutton');");
    		return true;
    	}

    	return false;
    }
*/
  
    /**
     * Any calls to Activity.startActivityForResult must use method below, so 
     * the result can be routed to them correctly.  
     * 
     * This is done to eliminate the need to modify DroidGap.java to receive activity results.
     * 
     * @param intent			The intent to start
     * @param requestCode		Identifies who to send the result to
     * 
     * @throws RuntimeException
     */
    @Override
    public void startActivityForResult(Intent intent, int requestCode) throws RuntimeException {
    	System.out.println("startActivityForResult(intent,"+requestCode+")");
    	super.startActivityForResult(intent, requestCode);
    	ArrayList<EstimateResult> dataList = new ArrayList<EstimateResult>();
    	Intent i = getIntent();
    	ArrayList<EstimateResult> myParcelableObject = i.getParcelableArrayListExtra("name_of_extra");

    }

    /**
     * Launch an activity for which you would like a result when it finished. When this activity exits, 
     * your onActivityResult() method will be called.
     *  
     * @param command			The command object
     * @param intent			The intent to start
     * @param requestCode		The request code that is passed to callback to identify the activity
     */
    public void startActivityForResult(IPlugin command, Intent intent, int requestCode) {
    	this.activityResultCallback = command;
    	this.activityResultKeepRunning = this.keepRunning;
    	
    	// If multitasking turned on, then disable it for activities that return results
    	if (command != null) {
    		this.keepRunning = false;
    	}
    	
    	// Start activity
    	super.startActivityForResult(intent, requestCode);
    }

     @Override
    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it. 
     * 
     * @param requestCode		The request code originally supplied to startActivityForResult(), 
     * 							allowing you to identify who this result came from.
     * @param resultCode		The integer result code returned by the child activity through its setResult().
     * @param data				An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
     protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	 //mostrarToast("onActivityResult: "+requestCode+resultCode);
    	 int icountFotos = Global.ivar1;
    	 String sFinFotos = Global.svar2;
    	 //Toast.makeText(this, "onActivityResult icountFotos: "+icountFotos, Toast.LENGTH_LONG).show();
    	 appendLog(" class: DroidGap.java - PROC: onActivityResult() - icountFotos: "+ icountFotos);
    	 if(icountFotos>=5){
    		 fotostat = "SI";
    	 }else{
    		 fotostat = "NO";
    	 }
    	 Global.autoRestore=0;
    	 //Toast.makeText(this, "onActivityResult sFinFotos: "+sFinFotos, Toast.LENGTH_LONG).show();
    	 if(sFinFotos.equals("OK")){
    		 //appView.loadUrl("javascript:dijit.byId('BUTTON_FOTOS').domNode.style.display = 'none';dijit.byId('fot').domNode.style.display = 'none';");
    	 }else{
    		 //appView.loadUrl("javascript:dijit.byId('BUTTON_FOTOS').domNode.style.display = '';dijit.byId('fot').domNode.style.display = '';");
    	 }    	 
    	 appView.loadUrl("javascript:FotosEstatus='"+fotostat+"';");
    	 super.onActivityResult(requestCode, resultCode, intent);
//    	 Toast.makeText(this, "onActivityResult requestCode: "+requestCode, Toast.LENGTH_LONG).show();
//    	 Toast.makeText(this, "onActivityResult resultCode: "+resultCode, Toast.LENGTH_LONG).show();
    	 if(resultCode==0){
    		 //appView.loadUrl("file:///android_asset/www/index2.html");
    		 appView.loadUrl("javascript:inicio();");
    	 }else{
        	 IPlugin callback = this.activityResultCallback;
        	 //Toast.makeText(this, "onActivityResult callback: "+callback, Toast.LENGTH_LONG).show();
        	 if (callback != null) {
        		 callback.onActivityResult(requestCode, resultCode, intent);
        		 appendLog(" class: DroidGap.java - PROC: onActivityResult() : " + requestCode + " - " + resultCode);
        	 }     		 
    	 }
     }

     @Override
     public void setActivityResultCallback(IPlugin plugin) {
    	 this.activityResultCallback = plugin;
     }
     
     /**
      * Report an error to the host application. These errors are unrecoverable (i.e. the main resource is unavailable). 
      * The errorCode parameter corresponds to one of the ERROR_* constants.
      *
      * @param errorCode 	The error code corresponding to an ERROR_* value.
      * @param description 	A String describing the error.
      * @param failingUrl 	The url that failed to load. 
      */
     public void onReceivedError(int errorCode, String description, String failingUrl) {
    	 final DroidGap me = this;

    	 // If errorUrl specified, then load it
    	 final String errorUrl = me.getStringProperty("errorUrl", null);
    	 if ((errorUrl != null) && errorUrl.startsWith("file://") && (!failingUrl.equals(errorUrl))) {
    		 appendLog(" class: DroidGap.java - PROC: onReceivedError() : " + errorCode + " - " + description + " - " + failingUrl);
    		 // Load URL on UI thread
    		 me.runOnUiThread(new Runnable() {
    			 public void run() {
    				 me.appView.loadUrl(errorUrl);
    			 }
    		 });
    	 }

    	 // If not, then display error dialog
    	 else {
    		 me.appView.loadUrl("about:blank");
    		 me.displayError("Application Error", description + " ("+failingUrl+")", "OK", true);
    		 appendLog(" class: DroidGap.java - PROC: onReceivedError() Application Error: " + errorCode + " - " + description + " - " + failingUrl);
    	 }
     }

     /**
      * Display an error dialog and optionally exit application.
      * 
      * @param title
      * @param message
      * @param button
      * @param exit
      */
     public void displayError(final String title, final String message, final String button, final boolean exit) {
    	 final DroidGap me = this;
    	 me.runOnUiThread(new Runnable() {
    		 public void run() {
    			 AlertDialog.Builder dlg = new AlertDialog.Builder(me);
    			 dlg.setMessage(message);
    			 dlg.setTitle(title);
    			 dlg.setCancelable(false);
    			 dlg.setPositiveButton(button,
    					 new AlertDialog.OnClickListener() {
    				 public void onClick(DialogInterface dialog, int which) {
    					 dialog.dismiss();
    					 if (exit) {
    						 me.finish();
    					 }
    				 }
    			 });
    			 dlg.create();
    			 dlg.show();
    		 }
    	 });
     }
     
     /**
      * We are providing this class to detect when the soft keyboard is shown 
      * and hidden in the web view.
      */
     class LinearLayoutSoftKeyboardDetect extends LinearLayout {

    	    private static final String LOG_TAG = "SoftKeyboardDetect";
    	    
    	    private int oldHeight = 0;	// Need to save the old height as not to send redundant events
    	    private int oldWidth = 0; // Need to save old width for orientation change    	    
    	    private int screenWidth = 0;
    	    private int screenHeight = 0;
    	        	    
			public LinearLayoutSoftKeyboardDetect(Context context, int width, int height) {
    	    	super(context);   	
    	        screenWidth = width;
    	        screenHeight = height;      	        
    	    }

    	    @Override
    	    /**
    	     * Start listening to new measurement events.  Fire events when the height 
    	     * gets smaller fire a show keyboard event and when height gets bigger fire 
    	     * a hide keyboard event.
    	     * 
    	     * Note: We are using callbackServer.sendJavascript() instead of 
    	     * this.appView.loadUrl() as changing the URL of the app would cause the 
    	     * soft keyboard to go away.
    	     * 
    	     * @param widthMeasureSpec
    	     * @param heightMeasureSpec
    	     */
    	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);       
    	        
    	    	Log.d(LOG_TAG, "We are in our onMeasure method");

    	    	// Get the current height of the visible part of the screen.
    	    	// This height will not included the status bar.
    	    	int height = MeasureSpec.getSize(heightMeasureSpec);
    	        int width = MeasureSpec.getSize(widthMeasureSpec);
    	        
    	        Log.d(LOG_TAG, "Old Height = " + oldHeight);
    	        Log.d(LOG_TAG, "Height = " + height);  	       
    	        Log.d(LOG_TAG, "Old Width = " + oldWidth);
    	        Log.d(LOG_TAG, "Width = " + width);  
    	        
    	        //appendLog(" class: DroidGap.java - PROC: onMeasure() Old Height =  " + oldHeight + " - Height = " + height + " - Old Width = " + oldWidth + " - Width = " + width);
    	        
    	        // If the oldHeight = 0 then this is the first measure event as the app starts up.
    	        // If oldHeight == height then we got a measurement change that doesn't affect us.
    	        if (oldHeight == 0 || oldHeight == height) {
    	        	Log.d(LOG_TAG, "Ignore this event");
    	        }
    	        // Account for orientation change and ignore this event/Fire orientation change
    	        else if(screenHeight == width)
    	        {
    	        	int tmp_var = screenHeight;
    	        	screenHeight = screenWidth;
    	        	screenWidth = tmp_var;
    	        	Log.d(LOG_TAG, "Orientation Change");
    	        }
    	        // If the height as gotten bigger then we will assume the soft keyboard has 
    	        // gone away.
    	        else if (height > oldHeight) {
    	        	Log.d(LOG_TAG, "Throw hide keyboard event");
    	        	callbackServer.sendJavascript("PhoneGap.fireEvent('hidekeyboard');");
    	        } 
    	        // If the height as gotten smaller then we will assume the soft keyboard has 
    	        // been displayed.
    	        else if (height < oldHeight) {
    	        	Log.d(LOG_TAG, "Throw show keyboard event");
    	        	callbackServer.sendJavascript("PhoneGap.fireEvent('showkeyboard');");
    	        }

    	        // Update the old height for the next event
    	        oldHeight = height;
    	        oldWidth = width;
    	    }
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	public void setDBName(String text)
	{ 
		appView.loadUrl("javascript:basedatos='"+text+"';");
	}
	
	public void appendLog(String text)
	{       
	   TomaFechaSistema();
	   versionLog = "" + fechaLogD;
//	    fechaLogD = ""+cal1.get(Calendar.DATE)+"/"+cal1.get(Calendar.MONTH)
//		+"/"+cal1.get(Calendar.YEAR)+" "+cal1.get(Calendar.HOUR)
//		+":"+cal1.get(Calendar.MINUTE)+":"+cal1.get(Calendar.SECOND);
	   File logFile = new File("sdcard/logINMAEst"+versionLog+".file");
	   if (!logFile.exists())
	   {
	      try
	      {
	         logFile.createNewFile();
	      } 
	      catch (IOException e)
	      {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
	   }
	   try
	   {
	      //BufferedWriter for performance, true to set append to file flag
	      BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true),10 * 1024); 
	      //buf = new char[8192];  8 * 1024     
	      buf.append(fechaHoraLogD+text);
	      buf.newLine();
	      buf.close();
	   }
	   catch (IOException e)
	   {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	   }
	}	
	
//	public void appendLogJS(String text)
//	{
//		Toast.makeText(this, "appendLogJS: "+text, Toast.LENGTH_LONG).show();
//		//appendLog(fechaLogD +  text);
//	}
	
	public class EstimateResult implements Parcelable {
	    private int id;
	    private String workType;
	    private String workforceType;
	    private String description;
	    private String pieceType;
	    private String partNumber;
	    private long partPrice;
	    private long workforceUnits;

	    public int describeContents() {
	        return 0;
	    }

	    public void writeToParcel(Parcel out, int flags) {
	        out.writeInt(id);
	        out.writeString(workType);
	        out.writeString(workforceType);
	        out.writeString(description);
	        out.writeString(pieceType);
	        out.writeString(partNumber);
	        out.writeLong(partPrice);
	        out.writeLong(workforceUnits);
	    }

	    public final Parcelable.Creator<EstimateResult> CREATOR = new Parcelable.Creator<EstimateResult>() {
	        public EstimateResult createFromParcel(Parcel in) {
	            return new EstimateResult(in);
	        }

	        public EstimateResult[] newArray(int size) {
	            return new EstimateResult[size];
	        }
	    };

	    private EstimateResult(Parcel in) {
			id = in.readInt();
			workType = in.readString();
			workforceType = in.readString();
			description = in.readString();
			pieceType = in.readString();
			partNumber = in.readString();
			partPrice = in.readLong();
			workforceUnits = in.readLong();
	    }
	}
	
    private void mostrarToast(String text){
       LayoutInflater inflater = getLayoutInflater();
       View layout = inflater.inflate(
             R.layout.toast_layout
            ,(ViewGroup) findViewById(R.id.toastLayout));
       TextView textv = (TextView) layout.findViewById(R.id.textview);
       textv.setText(text);  
       Toast toast = new Toast(getApplicationContext());
       toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
       toast.setDuration(Toast.LENGTH_LONG);
       toast.setView(layout);
       //Toast.makeText(this, text, Toast.LENGTH_LONG);
       toast.show();
    }	
    
	
    public void TomaFechaSistema() {

    	//SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss");
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
    	       Date date = new Date();
    	       //System.out.println(dateFormat.format(date));
    	fechaLogD = ""+dateFormat.format(date);
    	//Toast.makeText(this, "appendLogF - fechaLogD: "+fechaLogD, Toast.LENGTH_LONG).show();
    	SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss");
    	
    	fechaHoraLogD  = ""+dateFormat2.format(date);
    }	
    
    public void EstableceTipoAjuste(String text) {
    	
    	Global.ivar1=0;

    }
    
	public String space(int x){
	
		String spaces=" ";
		int numberOfSpaces=x;
	
		for(int i=0;i<numberOfSpaces;i++){
		spaces= spaces + " ";
		}
	
		return spaces;
	}
          
	
}

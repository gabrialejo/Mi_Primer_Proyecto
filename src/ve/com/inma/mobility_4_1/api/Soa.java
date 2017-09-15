package ve.com.inma.mobility_4_1.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import ve.com.inma.mobility_4_1.*;
import ve.com.inma.mobility_4_1.api.ManageData;
import ve.com.inma.mobility_4_1.api.util.FileUtil;
import ve.com.inma.mobility_4_1.json.AutoInmaJson;
import ve.com.inma.mobility_4_1.json.EObjectJson;
import ve.com.inma.mobility_4_1.json.JsonContainer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import ve.com.inma.mobility_4_1.api.R;
import com.phonegap.*;
import com.phonegap.DroidGap.EstimateResult;
import com.phonegap.DroidGap.GapViewClient;
import java.util.Date;
import java.util.Calendar;

import com.datumdroid.android.ocr.simple.EstimateActivity;
import com.datumdroid.android.ocr.simple.GlobalOcr;
import com.datumdroid.android.ocr.simple.VehiculoActivity;


    public class Soa extends DroidGap
    {

    	private static final int MENU_BOOK_ADD = 0;
    	private static final int MENU_MANAGE_DATA = 1;
    	private static final int MENU_WEBVIEW = 2;
    	private static final int MENU_NEXTVIEW = 3;
    	public static final String LOG_TAG = "Database";    	
    	private WebView browser;
    	private String path;
    	final Activity activity = this;
    	public String sMensaje="";
    	//private static final String TAG = "INMAEstMovil";
    	public Date fecha1 = new Date();
    	public String fechaLog = "";
    	public Calendar cal1 = Calendar.getInstance();  	
    	
    	SQLiteDatabase myDb = null; // Database object   	
    	
        /* (non-Javadoc)
         * @see com.phonegap.DroidGap#onCreate(android.os.Bundle)
         */
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
        	   
	    fechaLog = ""+cal1.get(Calendar.DATE)+"/"+cal1.get(Calendar.MONTH)
		+"/"+cal1.get(Calendar.YEAR)+" "+cal1.get(Calendar.HOUR)
		+":"+cal1.get(Calendar.MINUTE)+":"+cal1.get(Calendar.SECOND);
        	   
        	appendLog(fechaLog + " class: SOA.java - PROC: onCreate() - Inicio - OnCreate Ok");
 
            super.onCreate(savedInstanceState);
            super.requestWindowFeature(Window.FEATURE_NO_TITLE);
            super.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

            super.setContentView(R.layout.main);
    		browser = (WebView) findViewById(R.id.appView);
    		//path = Environment.getDataDirectory() + "/data/com.totsp.database/databases/";
    		//browser.getSettings().setDatabasePath(path); 		
    		//super.setStringProperty("loadingDialog", "Wait,Loading Demo...");
    		super.setIntegerProperty("splashscreen", R.drawable.splash);
    		super.setBooleanProperty("loadInWebView", true);
    		super.setBooleanProperty("keepRunning", true);

    		//super.setWebViewClient(super.appView, new DemoViewClient(this));
    		
	        /*
    		File dbFile = new File(Environment.getDataDirectory() + "/data/com.totsp.database/databases/carselector.db");
	        if (!dbFile.exists()) {
	    		//new ImportDatabaseTask().execute();
	        }else{
	    		//super.loadUrl("file:///android_asset/www/index.html");
	        }
	        */
    		
    		// Se obtiene los extras (parametros: json input) enviados por Synergy
    		//json = getIntent().getExtras().getString("eObject");
    		json = GlobalOcr.eObject;
    		//String sAjustetipo = getIntent().getExtras().getString("ajuste");
    		String sAjustetipo = GlobalOcr.ajuste;
    		//EstableceTipoAjuste(sAjustetipo);
    		//Global.estimateId=getIntent().getExtras().getString("EstimateId");
    		//Toast.makeText(Soa.this, "Global.estimateId: "+Global.estimateId, Toast.LENGTH_LONG).show();

    		if(sAjustetipo != null){
				Global.sTipoAjuste=sAjustetipo;
				//Toast.makeText(Soa.this, "sAjustetipo: "+sAjustetipo, Toast.LENGTH_LONG).show();
				if(sAjustetipo.equals("nuevo")){
					Global.ivar1=0;
				}else{
					//Global.estimateId=getIntent().getExtras().getString("EstimateId");
					String sEstimateId = Global.estimateId;
					Global.sFOLDER_ESTIMATE = sEstimateId.substring(0,6);
					//Toast.makeText(Soa.this, "Global.estimateId: "+Global.estimateId, Toast.LENGTH_LONG).show();
		    		//Global.mismoFolder=getIntent().getExtras().getString("mismoFolder");
		    		//Global.rutaAjusteG=getIntent().getExtras().getString("rutaAjuste");
		    		//GlobalOcr.rutaFotos = getIntent().getExtras().getString("rutaFotos");
		    		//Toast.makeText(Soa.this, "Global.mismoFolder: "+Global.mismoFolder, Toast.LENGTH_LONG).show();					
				}
				appendLog(fechaLog + " class: SOA.java - PROC: onCreate() -  getExtras Tipo de Ajuste: "+sAjustetipo);
				appendLog(fechaLog + " class: SOA.java - PROC: onCreate() -  getExtras mismoFolder: "+Global.mismoFolder);
    		}
    		//Log.w(TAG, json);
    		//appendLog(fecha1.toGMTString() + " - getExtras Ok json input: " + json);
    		 
    		 //Toast.makeText(Soa.this, "json: "+json, Toast.LENGTH_LONG).show();
    		//mostrarToast(" Soa.this, json: "+json);
    		 
		//*********************************************************************************
		// Procedemos a guardar en un archivo físico, el Json que envia el Aplicativo   ***
		// Peritaje de SYNERGY, en la RUTA:  "/mnt/sdcard/download/FilejsonSynergy".    ***
		//*********************************************************************************
    	   if(StringUtils.isBlank(json)){
	    		// El json esta vacio, se trata de una prueba solo de INMAEst Movil 
    		   appendLog(fechaLog + " class: SOA.java - PROC: onCreate() -  getExtras Ok json input nulo - PRUEBA INMAEst ");
	    	}else{
	    		// El json esta lleno con la data que envia Synergy
//	    		String sFileName = "FilejsonSynergy";
//				try
//				{
//					File root = new File(Environment.getExternalStorageDirectory(), "download");
//					if (!root.exists()) {
//						root.mkdirs();
//					}
//					File gpxfile = new File(root, sFileName);
//					FileWriter writer = new FileWriter(gpxfile);
//					writer.append(json);
//					writer.flush();
//					writer.close();
//				}
//				catch(IOException e)
//				{
//					 e.printStackTrace();
//					 e.getMessage();
//				}    
	    		appendLog(fechaLog + " class: SOA.java - PROC: onCreate() -  getExtras Ok json input: " + json);

    		 }
//    	   	   boolean blncheck = false;
//    	   	   blncheck = checkDataBase("carselector.db");
//		        if(blncheck){
//		        	//Toast.makeText(Soa.this, "blncheck dentro if: "+blncheck, Toast.LENGTH_LONG).show();
//		        	setDBName("carselector.db");
//					super.loadUrl("file:///android_asset/www/NoDatabase.html");
//		        }else{
       		
			//************************************************    JSONS de Prueba ESTRUCTURA VIEJA NO USAR   *************************************************************************************************************************************************************
    		//json = "{'eObject': {'auto':{'anio':'2009','marca':'AUDI','modelo':'A3','placa':'dddddd','serialCarroceria':'8y4gz48ydtv091374'}}}";
    		//json = "{'eObject': {'auto':{'anio':'1996','marca':'JEEP','modelo':'GRAND CHEROKEE','placa':'EEEEEE','serialCarroceria':'8Y4GZ48YDTV091374'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2011','marca':'CHEVROLET','modelo':'Aveo','SubModel':'Hb lt 2p - 2Pt, HatchBack - 4Cil., *i - Manual','placa':'XYZ456','serialCarroceria':'8Y4GZ48YDTV091374'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2011','marca':'CHEVROLET','modelo':'Aveo','SubModel':'','placa':'XYZ456','serialCarroceria':'8Y4GZ48YDTV091374'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2010','marca':'CHEVROLET','modelo':'Spark','SubModel':'','placa':'XYZ456','serialCarroceria':'8Y4GZ48YDTV091374', 'Odometer': '34000', 'Estimator': 'S3046','Color':'ROJO'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2001','marca':'HONDA','modelo':'Accord','SubModel':'EX - 4Pt, Sedan - 4Cil., *i - Manual','placa':'ACD4321','serialCarroceria':'8J3RS78YFGCX87653', 'Odometer': '25000', 'Estimator': 'S3046','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2012','marca':'CHEVROLET','modelo':'Silverado','placa':'ACD4321','serialCarroceria':'8J3RS78YFGCX87653', 'Odometer': '25000', 'Estimator': 'S3046','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2010','marca':'NOEXISTE','modelo':'','SubModel':'','placa':'ACD4321','serialCarroceria':'8J3RS78YFGCX87653', 'Odometer': '25000', 'Estimator': 'S3046','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2006','marca':'','modelo':'','SubModel':'','placa':'ACD4321','serialCarroceria':'8J3RS78YFGCX87653', 'Odometer': '25000', 'Estimator': 'S3046','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'1987','marca':'DODGE','modelo':'D 600','SubModel':'','placa':'XXX999','serialCarroceria':'8Y4GZ48YDTV091374'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2008','marca':'RENAULT','modelo':'Logan','SubModel':'','placa':'AA389CI','serialCarroceria':'9FBLSRAHB8M000632', 'Odometer': '25000', 'Estimator': 'S3037'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2006','marca':'FORD','modelo':'EXPLORER','SubModel':'','placa':'ACD4321','serialCarroceria':'8J3RS78YFGCX87653', 'Odometer': '25000', 'Estimator': 'S3046','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2006','marca':'FORD','modelo':'EXPEDITION','SubModel':'','placa':'ACD4321','serialCarroceria':'8J3RS78YFGCX87653', 'Odometer': '25000', 'Estimator': 'S3046','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2006','marca':'FORD','modelo':'ESCAPE','SubModel':'','placa':'ACD4321','serialCarroceria':'8J3RS78YFGCX87653', 'Odometer': '25000', 'Estimator': 'S3046','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2011','marca':'CHEVROLET','modelo':'OPTRA','SubModel':'','placa':'AD093HA','serialCarroceria':'9GAJM52307B077506', 'Odometer': '22', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2011','marca':'CHEVROLET','modelo':'LUV','SubModel':'','placa':'AD093HA','serialCarroceria':'9GAJM52307B077506', 'Odometer': '22', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2013','marca':'FORD','modelo':'EXPLORER','SubModel':'','placa':'AB716SK','serialCarroceria':'8XDEU63E6A8A33095', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2007','marca':'TOYOTA','modelo':'YARIS','SubModel':'','placa':'ACD4321','serialCarroceria':'8J3RS78YFGCX87653', 'Odometer': '25000', 'Estimator': 'S3046','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2013','marca':'TOYOTA','modelo':'Corolla','SubModel':'','placa':'ACD4321','serialCarroceria':'8J3RS78YFGCX87653', 'Odometer': '25000', 'Estimator': 'S3046','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2011','marca':'CHEVROLET','modelo':'CRUZE','SubModel':'LS-FWD-4Pt Sedan - 4Cil.*i-Automatic','placa':'AE466HM','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2012','marca':'CHEVROLET','modelo':'CRUZE','SubModel':'','placa':'AE466HM','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2008','marca':'CHEVROLET','modelo':'GRAND VITARA','SubModel':'','placa':'AB762EG','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2006','marca':'CHEVROLET','modelo':'GRAND VITARA','SubModel':'','placa':'AB762EG','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2008','marca':'RENAULT','modelo':'Clio','SubModel':'','placa':'MES38X','serialCarroceria':'8Y4GZ48YDTV091374'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2013','marca':'CHEVROLET','modelo':'Silverado','SubModel':'','placa':'AB762EG','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2006','marca':'Jeep','modelo':'Grand cherokee','SubModel':'','placa':'DDDDDD','serialCarroceria':'8Y4GZ48YDTV091374', 'Odometer': '9000', 'Estimator': 'S6030','Color':'AMARILLO'}}}";
    		//json = "{'eObject': {'auto':{'anio':'1996','marca':'Jeep','modelo':'Grand cherokee','SubModel':'Laredo 4x4 - 4Pt, Sport Wagon - 8Cil., *i - Automatic','placa':'DDDDDD','serialCarroceria':'8Y4GZ48YDTV091374', 'Odometer': '9000', 'Estimator': 'S6030'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2013','marca':'FORD','modelo':'FUSION','SubModel':'','placa':'AB716SK','serialCarroceria':'8XDEU63E6A8A33095', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2012','marca':'HYUNDAI','modelo':'GETZ','SubModel':'','placa':'AF373FA','serialCarroceria':'8XDEU63E6A8A33095', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2012','marca':'HYUNDAI','modelo':'ELANTRA','SubModel':'','placa':'AF373FA','serialCarroceria':'8XDEU63E6A8A33095', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2013','marca':'FORD','modelo':'F 250','SubModel':'','placa':'A78AZ9G','serialCarroceria':'8XDEU63E6A8A33095', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2008','marca':'CHEVROLET','modelo':'Grand vitara','SubModel':'','placa':'AB762EG','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2007','marca':'MAZDA','modelo':'Allegro 323','SubModel':'','placa':'AB762EG','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2004','marca':'HONDA','modelo':'Fit','SubModel':'','placa':'AB762EG','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2006','marca':'HONDA','modelo':'CR-V','SubModel':'','placa':'MFB71Z','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'1995','marca':'MITSUBISHI','modelo':'SPACE WAGON','SubModel':'','placa':'AB762EG','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2012','marca':'TOYOTA','modelo':'HILUX','SubModel':'','placa':'AB762EG','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2012','marca':'CHEVROLET','modelo':'Luv','SubModel':'','placa':'AB762EG','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2009','marca':'HONDA','modelo':'Civic','SubModel':'','placa':'AB762EG','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2009','marca':'TOYOTA','modelo':'Fortuner','SubModel':'','placa':'AB762EG','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    		//json = "{'eObject': {'auto':{'anio':'2008','marca':'MAZDA','modelo':'6','SubModel':'','placa':'AB762EG','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";  
    		//json = "{'eObject': {'auto':{'anio':'2006','marca':'MAZDA','modelo':'3','SubModel':'','placa':'AB762EG','serialCarroceria':'8Z1PJ5C50BG359011', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    	//************************************************  ^  JSONS de Prueba ESTRUCTURA VIEJA NO USAR ^  ****************************************************************************************************************************************************    	   
		//*********************************************************************************************************************************************************************************************************************
		// JAZPURUA - A partir de Junio 20, 2013, se utilizaran la estructura de Jsons siguiente:		
		//*********************************************************************************************************************************************************************************************************************
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '25000','SubModel':'','anio':'2010','marca':'NOEXISTE','modelo':'','placa':'AA389CI','serialCarroceria':'9FBLSRAHB8M000632'}}}";
		//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '25000','SubModel':'Convertible - 2Pt, Convertible - 4Cil., *i - Manual','anio':'2006','marca':'PEUGEOT','modelo':'206','placa':'AA389CI','serialCarroceria':'9FBLSRAHB8M000632'}}}";
		//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '25000','SubModel':'EX - 4Pt, Sedan - 4Cil., *i - Manual','anio':'2001','marca':'HONDA','modelo':'Accord','placa':'ACD4321','serialCarroceria':'8J3RS78YFGCX87653'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '25000','SubModel':'','anio':'2001','marca':'HONDA','modelo':'Accord','placa':'ACD4321','serialCarroceria':'8J3RS78YFGCX87653'}}}";
		//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '25000','SubModel':'','anio':'2010','marca':'','modelo':'','placa':'AA389CI','serialCarroceria':'9FBLSRAHB8M000632'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '90398','SubModel':'Xl 7 special edition 4x4 - 4Pt, Sport Wagon - 6Cil., *i - Automatic','anio':'2005','marca':'Chevrolet','modelo':'Grand Vitara','placa':'AB200EB','serialCarroceria':'8ZNCE13C25V339934'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '90398','SubModel':'','anio':'2006','marca':'Fiat','modelo':'Uno','placa':'MEH95R','serialCarroceria':'8ZNCE13C25V339934'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '90398','SubModel':'CLX - 4Pt, HAtchback - 4Cil., *i - Automatic','anio':'2008','marca':'Ford','modelo':'Focus','placa':'RAM41O','serialCarroceria':'8AFFZZFHA6J488350'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'LS - 2Pt, Pickup - 8Cil., *i - Automatic','anio':'2009','marca':'Chevrolet','modelo':'Silverado','placa':'A64AM5D','serialCarroceria':'8ZCEC14J99V324342'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'Prado 2p meru 4x4 - 2Pt, Sport Wagon - 4Cil., *i - Manual','anio':'2007','marca':'Toyota','modelo':'LAND CRUISER S.WAGON','placa':'AGP74N','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'Advanced - 4Pt, Sedan - 4Cil., *i - Automatic','anio':'2011','marca':'CHEVROLET','modelo':'OPTRA','placa':'AGP74N','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2009','marca':'CHEVROLET','modelo':'OPTRA','placa':'AGP74N','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'Move - 4Pt, Hatchback - 4Cil., *i - Automatic','anio':'2011','marca':'FORD','modelo':'FIESTA','placa':'AGP74N','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '123456','SubModel':'S - 4Pt, Sedan - 4Cil., *i - Automatic','anio':'2008','marca':'TOYOTA','modelo':'YARIS','placa':'AA083RV','serialCarroceria':'JTDBT923481245143'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2013','marca':'CHEVROLET','modelo':'Chevy Express','placa':'AGP74N','serialCarroceria':'9FH11UJ9079017705'}}}";
		//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2012','marca':'','modelo':'','placa':'AA652NV','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2012','marca':'MAZDA','modelo':'BT','placa':'A84CF0A','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2002','marca':'RENAULT','modelo':'TWINGO','placa':'A84CF0A','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2007','marca':'HYUNDAI','modelo':'SANTA FE','placa':'A84CF0A','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2007','marca':'TOYOTA','modelo':'Corolla','placa':'A84CF0A','serialCarroceria':'9FH11UJ9079017705'}}}";
     	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'Xlt 4x4 - 4Pt, Sport Wagon - 6Cil., *i - Automatic','anio':'2010','marca':'FORD','modelo':'EXPLORER','placa':'AGP74N','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2012','marca':'TOYOTA','modelo':'Fortuner','placa':'A84CF0A','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '1','SubModel':'','anio':'','marca':'','modelo':'','placa':'','serialCarroceria':''}}}";    	       	   
    	
		//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2010','marca':'FORD','modelo':'EXPLORER','placa':'AA196NI','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'1993','marca':'TOYOTA','modelo':'Corolla','placa':'A84CF0A','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '90398','SubModel':'','anio':'2006','marca':'Fiat','modelo':'PALIO WEEKEND','placa':'MEH95R','serialCarroceria':'8ZNCE13C25V339934'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2007','marca':'Volkswagen ','modelo':'Gol III','placa':'AGJ83S','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2007','marca':'FORD ','modelo':'','placa':'AGJ83S','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2013','marca':'MITSUBISHI','modelo':'Lancer','placa':'djdjjdkdd','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'anio':'2011','marca':'FORD','modelo':'RANGER','SubModel':'','placa':'A78AZ9G','serialCarroceria':'8XDEU63E6A8A33095', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    	//json = "{'eObject': {'auto':{'anio':'2011','marca':'MAZDA','modelo':'BT','SubModel':'','placa':'A78AZ9G','serialCarroceria':'8XDEU63E6A8A33095', 'Odometer': '33', 'Estimator': 'S3037','Color':'AZUL'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2007','marca':'CHEVROLET','modelo':'TrailBlazer','placa':'AGP74N','serialCarroceria':'9FH11UJ9079017705'}}}";
    	//json = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '35384','SubModel':'','anio':'2007','marca':'AUDI','modelo':'Q7','placa':'AGP74N','serialCarroceria':'9FH11UJ9079017705'}}}";   
    	   
    	//json = "{identifier: 'id',label:'name',items: [{id:'year_0', 'name':2015 , 'year':'2015',type:'cars',children:[{_reference:'mark_0_0'},{_reference:'mark_0_1'},{_reference:'mark_0_2'},{_reference:'mark_0_3'},{_reference:'mark_0_4'},{_reference:'mark_0_5'}]},{id:'mark_0_0', 'name':'Chery', parent:'cars', type:'stub'},{id:'mark_0_1', 'name':'Chevrolet', parent:'cars', type:'stub'},{id:'mark_0_2', 'name':'Ford', parent:'cars', type:'stub'},{id:'mark_0_3', 'name':'Jeep', parent:'cars', type:'stub'},{id:'mark_0_4', 'name':'Mitsubishi', parent:'cars', type:'stub'},{id:'mark_0_5', 'name':'Toyota', parent:'cars', type:'stub'},{id:'year_1', 'name':2014 , 'year':'2014',type:'cars',children:[{_reference:'mark_1_0'},{_reference:'mark_1_1'},{_reference:'mark_1_2'},{_reference:'mark_1_3'},{_reference:'mark_1_4'},{_reference:'mark_1_5'},{_reference:'mark_1_6'},{_reference:'mark_1_7'},{_reference:'mark_1_8'}]},{id:'mark_1_0', 'name':'Chery', parent:'cars', type:'stub'},{id:'mark_1_1', 'name':'Chevrolet', parent:'cars', type:'stub'},{id:'mark_1_2', 'name':'Fiat', parent:'cars', type:'stub'},{id:'mark_1_3', 'name':'Ford', parent:'cars', type:'stub'},{id:'mark_1_4', 'name':'Isuzu', parent:'cars', type:'stub'},{id:'mark_1_5', 'name':'Jeep', parent:'cars', type:'stub'},{id:'mark_1_6', 'name':'Mazda', parent:'cars', type:'stub'},{id:'mark_1_7', 'name':'Mitsubishi', parent:'cars', type:'stub'},{id:'mark_1_8', 'name':'Toyota', parent:'cars', type:'stub'},{id:'year_2', 'name':2013 , 'year':'2013',type:'cars',children:[{_reference:'mark_2_0'},{_reference:'mark_2_1'},{_reference:'mark_2_2'},{_reference:'mark_2_3'},{_reference:'mark_2_4'},{_reference:'mark_2_5'},{_reference:'mark_2_6'},{_reference:'mark_2_7'},{_reference:'mark_2_8'},{_reference:'mark_2_9'}]},{id:'mark_2_0', 'name':'Chery', parent:'cars', type:'stub'},{id:'mark_2_1', 'name':'Chevrolet', parent:'cars', type:'stub'},{id:'mark_2_2', 'name':'Fiat', parent:'cars', type:'stub'},{id:'mark_2_3', 'name':'Ford', parent:'cars', type:'stub'},{id:'mark_2_4', 'name':'Isuzu', parent:'cars', type:'stub'},{id:'mark_2_5', 'name':'Jeep', parent:'cars', type:'stub'},{id:'mark_2_6', 'name':'Mazda', parent:'cars', type:'stub'},{id:'mark_2_7', 'name':'Mitsubishi', parent:'cars', type:'stub'},{id:'mark_2_8', 'name':'Peugeot', parent:'cars', type:'stub'}]}"	   
    	   
    		if(StringUtils.isBlank(json)){
    			//Toast.makeText(Soa.this, "¡Solo se puede utilizar esta aplicacion, invocada a traves de Peritaje Movil Mercantil! ¡Retornando!", Toast.LENGTH_LONG).show();
    			
    			 	//super.loadUrl("file:///android_asset/www/NewFile.html");
   			 
    			 	 super.loadUrl("file:///android_asset/www/index2.html");
    		 }else{

				try {
						eObject = new JSONObject(json);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						appendLog(fechaLog + " class: SOA.java - PROC: onCreate() -  Definicion del objeto JSONObject(json) - JSONException: " +  e.getMessage());
						e.printStackTrace();
					}
 
        		 
        		 JSONObject o = eObject;
        		 /*JSONObject n = eObject;*/
    			 String anio = "";
    			 String mark = "";
    			 String model = "";
    			 String submodel = "";
    			 String placa = "";
    			 String odometer = "";
    			 String vehicleIdNumber = "";
    			 String estimator = "";
    			 String numSiniestro = "";
    			 String taller = "";
    			 //String Color="";
    			/* String nombre = "";*/
    			 try {
    				o = eObject.getJSONObject("eObject");
//    				n = eObject.getJSONObject("eObject");
    				o = o.getJSONObject("auto");
    				//anio = o.getString("anio");
    				//Toast.makeText(Soa.this, "o SOA: "+o, Toast.LENGTH_LONG).show();
    				anio = o.getString("anio");
    				//Toast.makeText(Soa.this, "anio SOA: "+anio, Toast.LENGTH_LONG).show();
    				model = o.getString("modelo");
    				//Toast.makeText(Soa.this, "model SOA: "+model, Toast.LENGTH_LONG).show();
    				submodel = o.getString("SubModel");
    				//Toast.makeText(Soa.this, "submodel SOA: "+submodel, Toast.LENGTH_LONG).show();
    				odometer = o.getString("Odometer");
    				//Toast.makeText(Soa.this, "odometer: "+odometer, Toast.LENGTH_LONG).show();
    				placa = o.getString("placa");
    				//Toast.makeText(Soa.this, "placa: "+placa, Toast.LENGTH_LONG).show();
    				vehicleIdNumber = o.getString("serialCarroceria");
    				//Toast.makeText(Soa.this, "vehicleIdNumber: "+vehicleIdNumber, Toast.LENGTH_LONG).show();
    				estimator = o.getString("Estimator");
    				//Toast.makeText(Soa.this, "estimator: "+estimator, Toast.LENGTH_LONG).show();
    				taller = o.getString("taller");
    				numSiniestro = o.getString("numsiniestro");
    				//Color=o.getString("Color");
    				//Toast.makeText(Soa.this, "o SOA: "+model+" , submodel: "+submodel+", placa: "+placa+" ,color: "+Color, Toast.LENGTH_LONG).show();
    			} catch (JSONException e) {
    				// TODO Auto-generated catch block
    				appendLog(fechaLog + " class: SOA.java - PROC: onCreate() -  Obtencion inicial de variables del Json - JSONException: " +  e.getMessage());
    				e.printStackTrace();
    			}        		 
        		 
//    			super.setIntegerProperty("splashscreen", R.drawable.splash);
//    			if(StringUtils.isBlank(anio)){
//    				 //super.loadUrl("file:///android_asset/www/NewFile.html");
//    				 super.loadUrl("file:///android_asset/www/index2.html");
//    			}else{
   				 //Toast.makeText(Soa.this, "INDEX2: ", Toast.LENGTH_LONG).show();
    				appendLog(fechaLog + " class: SOA.java - PROC: onCreate() -  Llamado a Index2.html ");
    			    super.loadUrl("file:///android_asset/www/index2.html");
//    			}
    			 
        		 
    		}
    		
	        KeyBoard keyboard = new KeyBoard(this, appView);
	        appView.addJavascriptInterface(keyboard, "KeyBoard");

    		
	        System.out.println(Environment.getDataDirectory());
	        
		   //}
        }       
        
        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
          
        	//KEYCODE_HOME  KEYCODE_BACK
        	// if (keyCode == KeyEvent.KEYCODE_HOME) {
        		// return false;
        	// } 
          //Handle the back button
          if (keyCode == KeyEvent.KEYCODE_BACK) {
        	  
        	  //Ask the user if they want to quit
//        	  if (sEstimateStatus.equals("NOT")){
//        		  sMensaje="Esta seguro de salir de InmaEstMobile?, La informacion no sera guardada!";
//        	  }else{
        		  sMensaje="Esta seguro de salir de InmaEstMobile?";
//        	  }
            new AlertDialog.Builder(this)
              .setIcon(android.R.drawable.ic_dialog_alert)
              .setTitle("Salir")
              .setMessage(sMensaje)
              .setNegativeButton(android.R.string.cancel, null)
              .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            	  
            	  
                @Override
                public void onClick(DialogInterface dialog, int which){
                	//Toast.makeText(Soa.this, "sEstimator: "+sEstimator, Toast.LENGTH_LONG).show();
                	String json  = "{'header': {'Service':'','OEM':'','PartIdManual':'false','SName':'','UName':'','PName':'','EstimateId': '','EstimateDate': '','Estimator':'"+sEstimator+"','Message03': '','MFR': '','Make': '','Year': '','Model': '','SubModel': '','Color': '','Doors': '','Odometer': '','Plate': '','VehicleIdType': '','VehicleIdNumber': '','CommittedStatus': 1,'Type': 1,'VehicleId': 0,'UserMessages': '','SentTo': '','TotalLoss': 0,'existe':0}},{'detail': [{''}]}";
                	GlobalOcr.Salida="ANORMAL";
                	sEstimateStatus="ANORMAL";
                	appendLog(fechaLog +  " class: SOA.java - PROC: onClick(DialogInterface dialog, int which) [Salida ANORMAL por ESC] - Salida del Sistema por Escape, a solicitud del Usuario");
                	Intent resultIntent;

                	resultIntent = new Intent();
                	//resultIntent.putExtra("eObject", "TEST DONE");
            	//*** El siguiente bloque de codigo se agrego para descargar el JSon en un archivo fisico ***
            		String sFileName = "Filejson";
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
            	        appendLog(fechaLog + " class: SOA.java - PROC: onClick(DialogInterface dialog, int which) [Salida por ESC] - Se guarda el archivo Filejson con las variables vacias, a solicitud del Usuario");
            	    }
            	    catch(IOException e)
            	    {
            	    	 appendLog(fechaLog +  " class: SOA.java - PROC:  onClick(DialogInterface dialog, int which) [Salida por ESC]- Exception I/O al intentar Escribir/Guardar el archivo Filejson o al Crear el Directorio: download" +  e.getMessage());
            	         e.printStackTrace();
            	         e.getMessage();
            	    }
            	    
            	    delete_FileBackupjson2();
            	    
            	 //*** Fin de bloque de codigo para descargar el JSon en un archivo fisico ***
            	    
					// Se prepara el Intent y se cargan los Extras a enviar a Synergy (json de respuesta vacio)
					try
					{ 
						//resultIntent.setClassName("com.datumdroid.android.ocr.simple","com.datumdroid.android.ocr.simple.EstimateActivity");
						resultIntent = new Intent(Soa.this, VehiculoActivity.class);
						GlobalOcr.eObject=json;
						//Toast.makeText(Soa.this, "json: "+json, Toast.LENGTH_LONG).show();
						//resultIntent.putExtra("eObject", json);
						//resultIntent.putExtra("oPerEstatus", "ANORMAL");
						GlobalOcr.Salida="ANORMAL";
//						resultIntent.putExtra("mismoFolder", Global.mismoFolder);
//						resultIntent.putExtra("rutaAjuste", Global.rutaAjusteG);
//						resultIntent.putExtra("rutaFotos", GlobalOcr.rutaFotos);						
						//setResult(Activity.RESULT_OK, resultIntent);
						startActivity(resultIntent);
						appendLog(fechaLog +  " class: SOA.java - PROC: onClick(DialogInterface dialog, int which) [Salida por ESC] - Carga exitosa de los Extras [eObject] con json vacio y ejecucion de Intent de Activity de retorno, a solicitud del Usuario");
					}
					catch(Exception e)
					{
						appendLog(fechaLog +  " class: SOA.java - PROC:  onClick(DialogInterface dialog, int which) [Salida por ESC]- Exception al intentar cargar los Extras [eObject] al Intent o al ejecutar el Intent del Activity de vuelta a Synergy" +  e.getMessage()); 
						e.printStackTrace();
						 e.getMessage();
					} 
	    	    
					appendLog(fechaLog +  " class: SOA.java - PROC: onClick(DialogInterface dialog, int which) [Salida por ESC] - Finalizacion de la Activity - Salida de la Aplicacion por [Esc], a solicitud del Usuario");
                	activity.finish();
                }
              })
              .show();

            // Say that we've consumed the event
            return true;
          }

          if (keyCode == KeyEvent.KEYCODE_SEARCH) {
        	  
 			  super.loadUrl("javascript:hideToolsShowFinder();");


        	  return true;
            }          
          
          return super.onKeyDown(keyCode, event);
        }      
        
	    public void delete_FileBackupjson2() {
	    	
	    	 String sFileName = "FileBackupjson";
	    	
    	     try
    	     {
    	         File root = new File(Environment.getExternalStorageDirectory(), "download");
				 File fi = new File(root, sFileName);
				
				 if(fi.exists()) {
					 fi.delete();
					 appendLog(fechaLog +  " class: SOA.java - PROC: delete_FileBackupjson2()- Se Elimina el archivo FileBackupjson por salida del Sistema sin terminar la Estimacion [Esc])");
				 }   	    	
    	     }
    	     catch(Exception e)
    	     {
    	    	 appendLog(fechaLog +  " class: SOA.java - PROC: delete_FileBackupjson2() - Exception al intentar Eliminar el archivo FileBackupjson" +  e.getMessage());
    	         // e.printStackTrace();
    	         // e.getMessage();
    	     }	    	
	    } 
       
        @Override
        public void onConfigurationChanged(Configuration newConfig) {
          //ignore orientation change
          super.onConfigurationChanged(newConfig);
        }

        public class ImportDatabaseTask extends AsyncTask<Void, Void, String> {
            private final ProgressDialog dialog = new ProgressDialog(Soa.this);

            @Override
            protected void onPreExecute() {
               dialog.setMessage("Importing database...");
               dialog.show();
            }

            // could pass the params used here in AsyncTask<String, Void, String> - but not being re-used
            @Override
            protected String doInBackground(final Void... args) {

          	  File[] files = new File("/mnt/sdcard/databasedata/").listFiles();
            	SQLiteDatabase myDb = null; // Database object
            	String path = Environment.getDataDirectory() + "/data/com.totsp.database/databases/";
      	     for(File file : files){

      	    	 if(file.isFile()){
      	      	  File dbBackupFile = new File("/mnt/sdcard/databasedata/"+file.getName());
      	          if (!dbBackupFile.exists()) {
      	        	appendLog(fechaLog +  " class: SOA.java - class: ImportDatabaseTask - PROC: doInBackground - No existe la Base de Datos: " + file.getName() + " , no se puede importar");
      	             return "Database backup file does not exist, cannot import.";
      	          } else if (!dbBackupFile.canRead()) {
      	        	appendLog(fechaLog +  " class: SOA.java - class: ImportDatabaseTask - PROC: doInBackground - La Base de Datos: " + file.getName() + " existe , pero no se puede leer");
      	             return "Database backup file exists, but is not readable, cannot import.";
      	          }

      	          File dbFile = new File(Environment.getDataDirectory() + "/data/com.totsp.database/databases/"+file.getName());
      	          if (dbFile.exists()) {
      	             dbFile.delete();
       	        	appendLog(fechaLog +  " class: SOA.java - class: ImportDatabaseTask - PROC: doInBackground - La Base de Datos: " + file.getName() + " fue eliminada!");      	             
      	          }

      	          try {
      	             dbFile.createNewFile();
      	             dbFile.canRead();
      	             dbFile.canWrite();
      	             
      	             FileUtil.copyFile(dbBackupFile, dbFile);
      	             
      	             Runtime.getRuntime().exec("chmod 777 " + dbFile);

       	        	appendLog(fechaLog +  " class: SOA.java - class: ImportDatabaseTask - PROC: doInBackground - La Base de Datos: " + file.getName() + " fue creada de nuevo coon exito, para lectura/escritura!");
      	          } catch (IOException e) {
      	             Log.e(Soa.LOG_TAG, e.getMessage(), e);
      	             appendLog(fechaLog +  " class: SOA.java - class: ImportDatabaseTask - PROC: doInBackground - Exception I/O al intentar crear La Base de Datos: " + file.getName() + ", o al declararla Readable/Writeable. " +  e.getMessage());
      	             return e.getMessage();
      	          }	    
      	    	 }

      	     }
          	  return null;
       
            }

            @Override
            protected void onPostExecute(final String errMsg) {
               if (dialog.isShowing()) {
                  dialog.dismiss();
               }
               if (errMsg == null) {
                  //Toast.makeText(Soa.this, "Import successful!", Toast.LENGTH_SHORT).show();
                  mostrarToast(" Import successful! ");
                  //exampleapp.super.loadUrl("file:///android_asset/www/index.html");

               } else {
            	   mostrarToast(" Import failed! ");
            	   //Toast.makeText(Soa.this, "Import failed - " + errMsg, Toast.LENGTH_SHORT).show();
               }
            }
         }     
        
        
  
    	@Override
    	public boolean onCreateOptionsMenu(final Menu menu) {
    		//menu.add(0, MENU_BOOK_ADD, 0, "InmaEst").setIcon(android.R.drawable.ic_menu_add);
    		//menu.add(0, MENU_MANAGE_DATA, 1, "Manage DB").setIcon(android.R.drawable.ic_menu_manage);
    		//menu.add(0, MENU_WEBVIEW, 2, "WEBVIEW");
    		//menu.add(0, MENU_NEXTVIEW, 3, "NEXTVIEW");
    		return super.onCreateOptionsMenu(menu);
    	}

    	@Override
    	public boolean onOptionsItemSelected(final MenuItem item) {
    		Cursor c;
    		switch (item.getItemId()) {
    		case MENU_BOOK_ADD:
    			//startActivity(new Intent(this, BookAdd.class));
    			super.loadUrl("file:///android_asset/www/index2.html");
    			return true;
    		case MENU_MANAGE_DATA:
    			startActivity(new Intent(this, ManageData.class));
 
    			
    			return true;
    		case MENU_WEBVIEW:
    			//Intent myIntent = new Intent(this, InmaEstMobilityActivity.class);
    			//startActivityForResult(myIntent, 0);
           		if (this.myDb != null) {
        			this.myDb.close();
        		}
        		
        		myDb = SQLiteDatabase.openDatabase(path+"8214.db", null, SQLiteDatabase.OPEN_READWRITE);

                /* Query for some results with Selection and Projection. */
                c = myDb.rawQuery("SELECT * FROM SERVICES;", null);

                System.out.println("ROWCOUNT: "+c.getCount());
                System.out.println("ROWCOUNT: "+c.getCount());
                System.out.println("ROWCOUNT: "+c.getCount());
                System.out.println("ROWCOUNT: "+c.getCount());
     
        		
        		c.close();
        		myDb.close();    			
    			return true;
    		case MENU_NEXTVIEW:
    			//switcher.showNext();
          		if (this.myDb != null) {
        			this.myDb.close();
        		}
        		
        		myDb = SQLiteDatabase.openDatabase(path+"692.db", null, SQLiteDatabase.OPEN_READWRITE);

                /* Query for some results with Selection and Projection. */
                c = myDb.rawQuery("SELECT * FROM SERVICES;", null);

                System.out.println("ROWCOUNT: "+c.getCount());
                System.out.println("ROWCOUNT: "+c.getCount());
                System.out.println("ROWCOUNT: "+c.getCount());
                System.out.println("ROWCOUNT: "+c.getCount());
     
        		
        		c.close();
        		//myDb.close();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    		}
    	}        
    	
//	    private boolean checkDataBase(String DB_NAME){
//	           boolean checkdb = false;
//	           Toast.makeText(this, "checkDataBase checkdb: "+checkdb, Toast.LENGTH_LONG).show();
//	           File f = new File("/mnt/sdcard/databasedata/"+DB_NAME);
//	           if(f.exists()){
//	        	   checkdb = true;
//	        	   Toast.makeText(this, "No se pudo abrir o no existe la Base de Datos: /mnt/sdcard/databasedata/"+DB_NAME, Toast.LENGTH_LONG).show();
//	        	   //appView.loadUrl("javascript:basedatos='"+DB_NAME+"';");
//	               //System.out.println("File exists");
//	           }else{
//	        	   
//	        	   Toast.makeText(this, "No se pudo abrir o no existe la Base de Datos: /mnt/sdcard/databasedata/"+DB_NAME, Toast.LENGTH_LONG).show();
//	           }
////	           try{
////	               String myPath = myContext.getFilesDir().getAbsolutePath().replace("files", "databases")+File.separator + DB_NAME;
////	               File dbfile = new File(myPath);                
////	               checkdb = dbfile.exists();
////	           }
////	           catch(SQLiteException e){
////	               System.out.println("Database doesn't exist");
////	           }
//	           return checkdb;
//	       }	    
    	
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
           toast.show();
        }	    	
    	
    }
    

package com.datumdroid.android.ocr.simple;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import ve.com.inma.mobility_4_1.api.R;

//import com.googlecode.tesseract.android.TessBaseAPI;
import com.phonegap.Global;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
//import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.content.DialogInterface;
//import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ImageView;

/*
 * Clase que representa la Pantalla Principal de acceso al Aplicativo: 
 * Permite acceder a la Pantalla de DataEntry para efectuar un nuevo Ajuste
 * Permite  acceder a la Pantalla de listado de ajustes hechos pendientes de sincronizar
 * Muestra una ventana de CopyRigth y "Derechos de Autor" y la opción de Salida normal del Sistema.
 * @author José A. Azpurua - 2013 - Última modificación: 16 Dic. 2014 - José A. Azpurua
 */
@SuppressWarnings("deprecation")
public class VehiculoActivity extends ActivityGroup   {
		//implements AdapterView.OnItemSelectedListener{
	//public VehiculoBean beanV = null;
//	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/SimpleAndroidOCR/";	
	//private LinearLayout lytitle = null;
	private TextView ttitle = null;
	private LinearLayout lylabels = null;
	//private LinearLayout lytext02 = null;
//	private RelativeLayout exvb1 = null;
	private RelativeLayout exvb2 = null;
//	private Button b1 = null;
//	private Button b2 = null;
//	private Button b3 = null;
//	private Button b4 = null;
	private Button b5 = null;
	private Button b6 = null;
//	private ImageButton ib3 = null;
	private ImageButton imb1 = null;
	private ImageButton imb2 = null;
//	private TextView tplaca = null;
	private TextView texttitle = null;
//	private EditText tserial = null;
//	private EditText todometer = null;
//	private EditText selection = null;
	private String sPlaca = "";
	private String sSerial = "";
	private String sOdometer = "";
	private String sAnnio = "";
	private String sObject = "";
	protected static final int REQUEST_CODE = 10;
	protected static LocalActivityManager mLocalActivityManager;
	public String result = null;
	Button launchButton;
	protected final Activity activity = this;
	public static final String lang = "eng";
	
	public String versionLog = "";
	public String fechaLogD = "";
	public String fechaHoraLogD = ""; 
	private String sPrimaryStorage = "/mnt/sdcard/";
	private String sSecondaryStorage = "/mnt/sdcard/";
//	protected String _path;
//	protected boolean _taken;

//	protected static final String PHOTO_TAKEN = "photo_taken";
//	
//	public String recognizedText = "";	
	
	
	@Override public void onCreate(Bundle savedInstanceState) {
		
		//String path = "/mnt/sdcard/databasedata/";	//Environment.getDataDirectory() + "/data/com.totsp.database/databases/";
		try{
			File fil = new File("/mnt/sdcard/databasedata/carselector.db");
			if(fil.exists()){
			   //checkdb = true;
				//Toast.makeText(this, "*** YA SE INSTALO LA PRIMERA VEZ. ****", Toast.LENGTH_LONG).show();
			}else{
			   //checkdb = true;
				//Toast.makeText(this, "INSTALACION POR PRIMERA VEZ", Toast.LENGTH_LONG).show();
				mostrarAlertDialogo2();
			}		
	    } catch (Exception  ex) {
	        ex.printStackTrace();
//			   mostrarToast("*** YA SE INSTALO LA PRIMERA VEZ. ****"); 
//			   mostrarToast(" *** SALIENDO DE LA APLICACION ***"); 	        
	    }
	    
		appendLog(" class: VehiculoActivity.java - PROC: onCreate() - Llegue a VehiculoActivity");
//		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };
//
//		for (String path : paths) {
//			File dir = new File(path);
//			if (!dir.exists()) {
//				if (!dir.mkdirs()) {
//					//Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
//					return;
//				} else {
//					//Log.v(TAG, "Created directory " + path + " on sdcard");
//				}
//			}
//
//		}
		
		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization
//		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
//			try {
//
//				AssetManager assetManager = getAssets();
//				InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
//				//GZIPInputStream gin = new GZIPInputStream(in);
//				OutputStream out = new FileOutputStream(DATA_PATH
//						+ "tessdata/" + lang + ".traineddata");
//
//				// Transfer bytes from in to out
//				byte[] buf = new byte[1024];
//				int len;
//				//while ((lenf = gin.read(buff)) > 0) {
//				while ((len = in.read(buf)) > 0) {
//					out.write(buf, 0, len);
//				}
//				in.close();
//				//gin.close();
//				out.close();
//				
//				//Log.v(TAG, "Copied " + lang + " traineddata");
//			} catch (IOException e) {
//				//Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
//			}
//		}		
		
		//**************************************************************************************
		// *** Validamos si existen extras del Intent que viene de vuelta desde INMAEst 4.1 ****
		//**************************************************************************************
		//Toast.makeText(VehiculoActivity.this,"Antes de Bundle recieveParams: ", Toast.LENGTH_LONG).show();
		  Bundle recieveParams = getIntent().getExtras();
		  if(recieveParams !=null){
			  //Toast.makeText(VehiculoActivity.this,"Dentro de if(recieveParams: "+recieveParams, Toast.LENGTH_LONG).show();
			  //JSONObject eObject = null;
			  ////GlobalOcr.Salida=recieveParams.getString("oPerEstatus");
			  //if(Global.Salida.equals("ANORMAL")){
				  ////result = recieveParams.getString("eObject");
	    		 //Toast.makeText(VehiculoActivity.this,"recieveParams result: "+result, Toast.LENGTH_LONG).show();
				 
//				 Global.mismoFolder=recieveParams.getString("mismoFolder");
//				 Global.rutaAjusteG=recieveParams.getString("rutaAjuste");
//				 GlobalOcr.rutaFotos=recieveParams.getString("rutaFotos");
//				 GlobalOcr.eObject=result;
	//		//*** Desviamos el control a EstimateActivity, para mostrar los resultados del Ajuste **
	//	         View v = new View(this);
				 //Intent EstIntent = new Intent(v.getContext(), EstimateActivity.class);
		         Intent EstIntent = new Intent(this, EstimateActivity.class);
	//	         EstIntent.putExtra("eObject",result);
	         
		         startActivity(EstIntent);
		         finish();
			  //}
		  }
		   		  
		//**************************************************************************************
		
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.vehicle_layout);
       
    	if(GlobalOcr.Salida.equals("ANORMAL")){
    		mostrarToast("El Usuario decidio salir del Ajuste [Esc]/[Back], antes de Finalizar el mismo - SALIDA ANORMAL DEL SISTEMA ");
    		GlobalOcr.Salida = "";
    		//return;
			//activity.finish();
    	}          
        //lytitle = (LinearLayout ) findViewById(R.id.lytitle);
        
        ttitle = (TextView) findViewById(R.id.texttitle);
        
        lylabels = (LinearLayout ) findViewById(R.id.lylabels);
        
        //lytext02 = (LinearLayout ) findViewById(R.id.lytext02);
        
        //exvb1 = (RelativeLayout) findViewById(R.id.exit_button_view);
        
        exvb2 = (RelativeLayout) findViewById(R.id.exit_button_view2);
//        
        imb1 = (ImageButton) findViewById(R.id.imageButton1);
        
        imb2 = (ImageButton) findViewById(R.id.imageButton2);
//        
//		b1 = (Button) findViewById(R.id.button1);
//		
//		b2 = (Button) findViewById(R.id.button2);
//		
//		b3 = (Button) findViewById(R.id.button3);
		
//		b4 = (Button) findViewById(R.id.button4);
		
		b5 = (Button) findViewById(R.id.button5);
		
		b6 = (Button) findViewById(R.id.button6);
		
//		ib3 = (ImageButton) findViewById(R.id.imageButtonCam);
//		
//		tplaca = (TextView) findViewById(R.id.editTextplaca);
//		
//		ttitle.setText("Módulo de Ajustes del Vehículo");
//		//ttitle.Margin.Left="280dp";
//		lylabels.setVisibility(View.INVISIBLE);
//		//lytext02.setVisibility(View.VISIBLE);
//		exvb1.setVisibility(View.INVISIBLE);
//		exvb2.setVisibility(View.VISIBLE);		
//		
//		_path = DATA_PATH + "/ocr.jpg";
//		
//		if(GlobalOcr.sPlate.length() > 0){
//			tplaca.setText(GlobalOcr.sPlate);
//		}else{
//			tplaca.setText("");
//		}
//		//Toast.makeText(VehiculoActivity.this,"Global.sPlate: "+Global.sPlate, Toast.LENGTH_LONG).show();
//		String sSalida = GlobalOcr.Salida;
//		
//		if(sSalida.equals("ANORMAL")){
//			tplaca.setText("");
//		}
//		
//		//Toast.makeText(VehiculoActivity.this,"sSalida: "+sSalida, Toast.LENGTH_LONG).show();
//		
//		tserial = (EditText) findViewById(R.id.EditTextserial);
//		
//		todometer = (EditText) findViewById(R.id.EditTextkilometros);
		
		imb1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{  	
				mostrarAlertDialogo();
			}
		});			
		
//		b1.setOnClickListener(new OnClickListener()
//		{
//		
//			public void onClick(View v)
//			{
//				//tplaca.setText(Global.sPlate);
//				sPlaca=tplaca.getText().toString().trim();
//				int lenPlaca = sPlaca.length();
//				if(lenPlaca == 0){
//					//Toast.makeText(VehiculoActivity.this,"Debe ingresar la Placa del vehiculo!", Toast.LENGTH_LONG).show();
//					mostrarToast("Debe ingresar la Placa del vehiculo!");
//					return;					
//				}
////				if(lenPlaca < 7){
////					//Toast.makeText(VehiculoActivity.this,"La Placa del vehiculo debe ser mayor o igual a siete (7) caracteres!", Toast.LENGTH_LONG).show();
////					mostrarToast("La Placa del vehiculo debe ser mayor o igual a siete (7) caracteres!");
////					return;					
////				}
//				sSerial=tserial.getText().toString().trim();
//				int lenSerial = sSerial.length();
//				if(lenSerial == 0){
//					//Toast.makeText(VehiculoActivity.this,"Debe ingresar el Serial del vehiculo!", Toast.LENGTH_LONG).show();
//					mostrarToast("Debe ingresar el Serial del vehiculo!");
//					return;					
//				}				
//				sOdometer=todometer.getText().toString().trim();
//				int lenOdometer = sOdometer.length();
//				if(lenOdometer == 0){
//					//Toast.makeText(VehiculoActivity.this,"Debe ingresar el Kilometraje del vehiculo!", Toast.LENGTH_LONG).show();
//					mostrarToast("Debe ingresar el Kilometraje del vehiculo! (odómetro)");
//					return;					
//				}	
//
//				// Confeccionamos el Objeto json a enviar.....(Parte de la data esta cableada mientras tanto)
//				sObject = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '" + sOdometer + "','SubModel': '" + "','anio':'','marca':'','modelo':'','placa':'" + sPlaca + "','serialCarroceria':'" + sSerial + "'}}}";
////				Toast msg = Toast.makeText(getBaseContext(),"EEEEEXITOOO!", Toast.LENGTH_LONG);
////				 msg.show(); 
////				if(Global.ajuste.equals("mismo")){
//					GlobalOcr.ajuste="nuevo";
////				}
//				GlobalOcr.eObject=sObject;
//				Intent EstIntent2 = new Intent();
//				EstIntent2.setClassName("ve.com.inma.mobility_4_1.api","ve.com.inma.mobility_4_1.api.Soa");
//
//				//Toast.makeText(PhotosActivity.this, "sAnnio antesdeenviar: "+sAnnio, Toast.LENGTH_LONG).show();
//		    	//myIntent.putExtra("eObject",sObject);
////		    	if(Global.ajuste.equals("nuevo")){
////		    		myIntent.putExtra("ajuste","nuevo");
////		    	}else{
////		    		myIntent.putExtra("ajuste","mismo");
////		    	}
//				//this.startActivityForResult(myIntent,1);
//				startActivity(EstIntent2);
//				//startActivityForResult(myIntent, REQUEST_CODE);
//				
//				activity.finish();			 
//				
//			}
//
//		});	  
		
//		b3.setOnClickListener(new OnClickListener()
//		{
//		
//			public void onClick(View v)
//			{		
//				Intent myIntent = new Intent(VehiculoActivity.this, Estimacion.class);
//				startActivity(myIntent);
//				finish();
//			}
//		});	 
//		
//		b2.setOnClickListener(new OnClickListener()
//		{
//		
//			public void onClick(View v)
//			{		
//				ttitle.setText("Módulo de Ajustes del Vehículo");
//				//ttitle.Margin.Left="200dp";
//				lylabels.setVisibility(View.INVISIBLE);
//				//lytext02.setVisibility(View.VISIBLE);
//				exvb1.setVisibility(View.INVISIBLE);
//				exvb2.setVisibility(View.VISIBLE);				
//				
//			}
//		});	 
		
//		ib3.setOnClickListener(new OnClickListener()
//		{
//		
//			public void onClick(View v)
//			{		
//				startCameraActivity();
//			}
//		});	  		
		
//		b4.setOnClickListener(new OnClickListener()
//		{
//		
//			public void onClick(View v)
//			{
////				//lytitle.setVisibility(View.VISIBLE);
////				ttitle.setText("           Datos del Vehículo   ");
////				//ttitle.Margin.Left="280dp";
////				lylabels.setVisibility(View.VISIBLE);
////				//lytext02.setVisibility(View.VISIBLE);
////				exvb1.setVisibility(View.VISIBLE);
////				exvb2.setVisibility(View.INVISIBLE);
//				
//				Intent myIntent = new Intent(VehiculoActivity.this, DataEntry.class);
//				startActivity(myIntent);				
//				finish();
//			}
//		});	
		
		imb1.setOnClickListener(new OnClickListener()
		{
		public void onClick(View v)
		{
			Intent myIntent = new Intent(VehiculoActivity.this, DataEntry.class);
			appendLog(" class: VehiculoActivity.java - PROC: imb1.onClick() - Antes de salir a DataEntry");
			startActivity(myIntent);				
			finish();
		}
	});
		
		imb2.setOnClickListener(new OnClickListener()
		{
		public void onClick(View v)
		{
			mostrarAlertDialogo();
		}
	});		
		appendLog(" class: VehiculoActivity.java - PROC: onCreate() - Antes de Declaracion de b5.setOnClickListener()");
		b5.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{		
				Intent myIntent = new Intent(VehiculoActivity.this, Estimacion.class);
				//myIntent.putExtra("origen","VehiculoActivity");
				appendLog(" class: VehiculoActivity.java - PROC: imb1.onClick() - Antes de salir a Estimacion");
				startActivity(myIntent);
				finish();
				appendLog(" class: VehiculoActivity.java - PROC: imb1.onClick() - Despues del finish()");
			}
		});	
		
		b6.setOnClickListener(new OnClickListener()
		{
		
			public void onClick(View v)
			{		
				finish();
			}
		});	 			
				
		
//		tplaca.addTextChangedListener(new TextWatcher() {
//
//		    public void onTextChanged(CharSequence s, int start,int before, int count) 
//		    {
//		    	int size = 7;
//		    	
////		    	Toast.makeText(VehiculoActivity.this, "CharSequence s: "+s, Toast.LENGTH_LONG).show();
//		        // TODO Auto-generated method stub
//		        if(tplaca.getText().toString().length()==size)     //size as per your requirement
//		        {
//		        	tserial.requestFocus();
//		        }
//		        
//		    	int sizes = 16;
//		        // TODO Auto-generated method stub
//		        if(tserial.getText().toString().length()==sizes)     //size as per your requirement
//		        {
//		        	todometer.requestFocus();
//		        }		        
//		    }
//		    public void beforeTextChanged(CharSequence s, int start,
//		                    int count, int after) {
//		                // TODO Auto-generated method stub
//		    	//Toast.makeText(VehiculoActivity.this, "CharSequence s: "+s, Toast.LENGTH_LONG).show();
//		    }
//
//		    public void afterTextChanged(Editable s) {
//		                // TODO Auto-generated method stub
//		    }
//
//		});	
//		
//		tserial.addTextChangedListener(new TextWatcher() {
//
//		    public void onTextChanged(CharSequence s, int start,int before, int count) 
//		    {
//		    	int size = 17;
//		        // TODO Auto-generated method stub
//		        if(tserial.getText().toString().length()==size)     //size as per your requirement
//		        {
//		        	todometer.requestFocus();
//		        }
//		        	        
//		    }
//		    public void beforeTextChanged(CharSequence s, int start,
//		                    int count, int after) {
//		                // TODO Auto-generated method stub
//		    }
//
//		    public void afterTextChanged(Editable s) {
//		                // TODO Auto-generated method stub
//		    }
//
//		});
		
    }
	
	//**************************************************************************************************
	
	//**************************************************************************************************
	
	   @Override
	   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		   //Toast.makeText(this, "Dentro de VehiculoActivity de vuelta resultCode: "+resultCode, Toast.LENGTH_LONG).show();
		   super.onActivityResult(requestCode, resultCode, data);
//	      Toast.makeText(this, "Dentro de VehiculoActivity de vuelta despues de super requestCode: "+requestCode, Toast.LENGTH_LONG).show();
////	      Toast.makeText(this, "Dentro de VehiculoActivity de vuelta: ", Toast.LENGTH_LONG).show();
			if (resultCode == -1) {
				//onPhotoTaken();
			} else {
				//Log.v(TAG, "User cancelled");
			}
//	      if (requestCode == REQUEST_CODE) {
//	         // cogemos el valor devuelto por la otra actividad
//	         result = data.getStringExtra("eObject");
//	         // enseñamos al usuario el resultado
////	         Toast.makeText(this, "json de vuelta: " + result, Toast.LENGTH_LONG).show();
//	         GlobalOcr.Salida=data.getStringExtra("oPerEstatus");
//	         Global.mismoFolder=data.getStringExtra("mismoFolder");
//	         Global.rutaAjusteG=data.getStringExtra("rutaAjuste");
//	         Global.rutaFotos=data.getStringExtra("rutaFotos");
////	         Toast.makeText(this, "Dentro de VehiculoActivity de vuelta Global.mismoFolder: "+Global.mismoFolder, Toast.LENGTH_LONG).show();
////	         Toast.makeText(this, "Dentro de VehiculoActivity de vuelta Global.rutaAjusteG: "+Global.rutaAjusteG, Toast.LENGTH_LONG).show();
////	         Toast.makeText(this, "Dentro de VehiculoActivity de vuelta Global.rutaAjusteG: "+Global.rutaAjusteG, Toast.LENGTH_LONG).show();
////	         Toast.makeText(this, "Dentro de VehiculoActivity de vuelta Global.rutaFotos: "+Global.rutaFotos, Toast.LENGTH_LONG).show();
//	         View v = new View(this);
//	         Intent EstIntent = new Intent(v.getContext(), EstimateActivity.class);
//	         EstIntent.putExtra("eObject",result);
//	         
//	         startActivity(EstIntent);	         
//	         //StringBuffer urlString = new StringBuffer();
//	         //Activity1 parentActivity = (Activity1)getParent();
//	         //replaceContentView("estimate_layout", EstIntent);	 
//	         Global.svar1="SI";
//	         
//	         activity.finish();
//	      }
	   }    
	   
	   public void replaceContentView(String id, Intent newIntent) {
		   View view = getLocalActivityManager().startActivity(id,newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) .getDecorView(); this.setContentView(view);
	   }
	   
	    private void mostrarToast(String text){
	       text=" "+text+" ";
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
    
	    private void mostrarAlertDialogo2(){
	    	
	         // custom dialog
	          final Dialog dialog = new Dialog(this);
	          dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	          dialog.setContentView(R.layout.custominstall);
	          //dialog.setTitle("                              *** GRUPO INMA; C.A.  ***  ");
	          // set the custom dialog components - text, image and button
	 //         TextView text = (TextView) dialog.findViewById(R.id.text);
	 //         text.setText("INMAEst Movil");
	          TextView text2 = (TextView) dialog.findViewById(R.id.text2);
	          text2.setText("*** INSTALACIÓN DE INMAEST MOVIL ***");
	          TextView text3 = (TextView) dialog.findViewById(R.id.text3);
	          text3.setText(" Powered by Grupo INMA C.A. Copyrigth © 2016 - 2017");            
	          ImageView image = (ImageView) dialog.findViewById(R.id.image);
	          image.setImageResource(R.drawable.logoinmaestpeq);

	          //Button venezButton = (Button) dialog.findViewById(R.id.venezuelabutton);
	          Button panamButton = (Button) dialog.findViewById(R.id.panamabutton);
	          //Button costaButton = (Button) dialog.findViewById(R.id.costaricabutton);
	          // if button is clicked, close the custom dialog
//	          venezButton.setOnClickListener(new OnClickListener() {
//	              @Override
//	              public void onClick(View v) {
//	             	 //b4.setBackgroundResource(R.drawable.placa);
//	                  dialog.dismiss();
//	              }
//	          });
	          
	          panamButton.setOnClickListener(new OnClickListener() {
	              //@Override
	              ProgressDialog pDialog;
	              public void onClick(View v) {
	             	 //b4.setBackgroundResource(R.drawable.placapanama);
	      			pDialog = new ProgressDialog(VehiculoActivity.this);
	      	        pDialog.setMessage("Cargando las Bases de Datos, espere por favor....." );
	      	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	      	        pDialog.setCancelable(true);
	      	        pDialog.show();	            	  
	                dialog.dismiss();
					Intent myIntent = new Intent(VehiculoActivity.this, WebService.class);
					startActivity(myIntent);	                
//					 Intent InstIntent = new Intent();
//					 InstIntent.setClassName("com.datumdroid.android.ocr.simple","com.datumdroid.android.ocr.simple.WebService");
//					 startActivity(InstIntent);
					pDialog.dismiss();
					 finish();	                
	              }
	          });
	          
//	          costaButton.setOnClickListener(new OnClickListener() {
//	              @Override
//	              public void onClick(View v) {
//	             	 //b4.setBackgroundResource(R.drawable.placacostarica);
//	                  dialog.dismiss();
//	              }
//	          });	          

	          dialog.show();
	        }	    
		
	    private void mostrarAlertDialogo(){
	    	
	        // custom dialog
	         final Dialog dialog = new Dialog(this);
	         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	         dialog.setContentView(R.layout.custom);
	         //dialog.setTitle("                              *** GRUPO INMA; C.A.  ***  ");
	         // set the custom dialog components - text, image and button
	//         TextView text = (TextView) dialog.findViewById(R.id.text);
	//         text.setText("INMAEst Movil");
	         TextView text2 = (TextView) dialog.findViewById(R.id.text2);
	         text2.setText("INMAEst Movil: 4.1.2 G. - Base de Datos versión: 112016");
	         TextView text3 = (TextView) dialog.findViewById(R.id.text3);
	         text3.setText(" Powered by Grupo INMA C.A. Copyrigth © 2016/2017");            
	         ImageView image = (ImageView) dialog.findViewById(R.id.image);
	         image.setImageResource(R.drawable.logoinmaestpeq);

	         Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
	         // if button is clicked, close the custom dialog
	         dialogButton.setOnClickListener(new OnClickListener() {
	             @Override
	             public void onClick(View v) {
	                 dialog.dismiss();
	             }
	         });

	         dialog.show();
	       } 
	    
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	//Toast.makeText(VehiculoActivity.this, "onKeyDown - Antes de if (keyCode == KeyEvent.KEYCODE_BACK): "+keyCode, Toast.LENGTH_LONG).show();
	    	//BACK  HOME
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				  String sMensaje="Esta seguro de salir de InmaEstMobile?";
		
			  new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Salir")
				.setMessage(sMensaje)
				.setNegativeButton(android.R.string.cancel, null)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {    		
				  @Override
				  public void onClick(DialogInterface dialog, int which){
					  activity.finish();
				  }
			  })
			  .show();
				return true;
			}
	    return super.onKeyDown(keyCode, event);
	    }	    
	    
	    @Override
	     public boolean onCreateOptionsMenu(Menu menu) {
	         MenuInflater inflater = getMenuInflater();
	         inflater.inflate(R.menu.menu, menu);
	         return true;
	     }
	    
	    @Override
	      public boolean onOptionsItemSelected(MenuItem item) {
	         switch (item.getItemId()) {
	         case R.id.about:
	            Toast.makeText(
	            		VehiculoActivity.this
	                 ,"Ejemplo Menús App"
	                 ,Toast.LENGTH_LONG)
	                 .show();
	            return true;
	    
	         case R.id.quit:
	            finish();
	            return true;
	    
	         default:
	            return super.onOptionsItemSelected(item);
	         }
	      }
	    
		// Procedimiento que llena un log (bitácora) de las acciones tomadas en la presente clase, en una archivo físico de la tableta
	    public void appendLog(String text)
	    {       
	    	   TomaFechaSistema();
	    	   versionLog = "" + fechaLogD;

	    	   File logFile = new File("/mnt/sdcard/Download/Estimate_"+versionLog+".file");
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
	    		  buf.append(fechaHoraLogD+"-"+text);
	    		  //buf.append(text);
	    		  buf.newLine();
	    		  buf.close();
	    	   }
	    	   catch (IOException e)
	    	   {
	    		  // TODO Auto-generated catch block
	    		  e.printStackTrace();
	    	   }
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
	    
}

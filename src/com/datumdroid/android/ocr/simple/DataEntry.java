package com.datumdroid.android.ocr.simple;

import java.io.*;
import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;
import java.io.InputStream;
import java.io.File;
import java.text.*;
import java.util.*;
//import com.googlecode.tesseract.android.TessBaseAPI;
import ve.com.inma.mobility_4_1.api.R;
import ve.com.inma.mobility_4_1.api.R.layout;
import ve.com.inma.mobility_4_1.api.R.menu;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.AssetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;
import android.widget.RelativeLayout;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.app.Dialog;

/*
 * Clase que representa la Pantalla de Ingreso de Data básica para el Ajuste: 
 * Placa / Serial del Vehículo (VIN) / Kilometraje (Odómetro) / Selección de Perfil y/o Taller
 * Permite  acceder al Módulo pricipal de Ajuste en HTML (Core Hibrido: HTML/JQuery/DOJO/JavaScript)
 * Permite  acceder a la Pantalla de listado de ajustes hechos pendientes de sincronizar
 * @author José A. Azpurua - 2013 - Última modificación: 16 Sep. 2016 - José A. Azpurua
 * Modificación: Inhabilitación de Control de Validación del mínimo de 17 caracteres en el Serial (VIN)
 */
public class DataEntry extends Activity {

	//public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/SimpleAndroidOCR/";	
	public static final String DATA_PATH = "/mnt/sdcard/databasedata/SimpleAndroidOCR/";
	private RelativeLayout exvb1 = null;
//	private RelativeLayout exvb2 = null;
	private Button b1 = null;
	private Button b2 = null;
	private Button b3 = null;
	private Button b4 = null;
	private Button b5 = null;
	private Button b6 = null;
	//private Button b7 = null;
	//private ImageButton ib3 = null;
	//private ImageButton imb1 = null;
	private TextView tplaca = null;
	private TextView texttitle = null;
	private EditText tserial = null;
	private EditText todometer = null;
	private EditText selection = null;
	private TextView tspinner = null;
	private TextView tspinnerT = null;
	private TextView tSiniestro = null;
	private EditText tnumSiniestro = null;// EditText que contiene el valor del Número de Siniestro para buscar por INMAEst PC (Solo Guatemala)
	private Spinner spinner = null;
	private Spinner spinnerT = null;
	private String sPlaca = "";
	private String sSerial = "";
	private String sOdometer = "";
	private String sNumSiniestro = "";
	private String sAnnio = "";
	private String sMarca = "";
	private String sModelo = "";
	private String sObject = "";
	private String spinVal = "";
	private String spinValT = "";
	protected final Activity activity = this;
	public static final String lang = "eng";
	protected String _path;
	protected boolean _taken;
	public String fechaLogD = "";
	public String fechaHoraLogD = "";	
	public String versionLog = "";
	private String sPrimaryStorage = "/mnt/sdcard/";
	private String sSecondaryStorage = "/mnt/sdcard/";
	public String path = "/mnt/sdcard/databasedata/";
	public SQLiteDatabase myDb = null; // Database object
	
	protected static final String PHOTO_TAKEN = "photo_taken";
	
	public String recognizedText = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					//Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					//Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}
		
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/" + lang + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				//while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				//gin.close();
				out.close();
				
				//Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				//Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}		
		
		super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);		
        
		setContentView(R.layout.data_entry);
		appendLog("class DataEntry - super.onCreate() - Antes de Declaracion de variables");
		
        //exvb1 = (RelativeLayout) findViewById(R.id.exit_button_view);
        
        //imb1 = (ImageButton) findViewById(R.id.imageButton1);
        
		b1 = (Button) findViewById(R.id.button1);				// Llama a INMAEst Movil 4.1 con los parametros de entrada (Placa, kilometraje, serial, etc..)
		
		b2 = (Button) findViewById(R.id.button2);				// Regresar a la pantalla Principal
		
		b3 = (Button) findViewById(R.id.button3);				// Listar Ajustes pendientes
		
//		ib3 = (ImageButton) findViewById(R.id.imageButtonCam);	// Llama al módulo de Lectura de Placas por OCR
		
		//ib4 = (ImageButton) findViewById(R.id.imageButtonPlaca);
		
		b4 = (Button) findViewById(R.id.button4);				// Habilita y hace visible / invisible los TextBox de Placa, Serial y/o Kilometraje
		
		b5 = (Button) findViewById(R.id.button5);				// Habilita y hace visible / invisible los TextBox de Placa, Serial y/o Kilometraje
		
		b6 = (Button) findViewById(R.id.button6);				// Habilita y hace visible / invisible los TextBox de Placa, Serial y/o Kilometraje
		
		//b7 = (Button) findViewById(R.id.button7);
		
		tplaca = (TextView) findViewById(R.id.editTextplaca);
		tspinner = (TextView) findViewById(R.id.txtSpinner);
		tspinnerT = (TextView) findViewById(R.id.txtSpinnerT);
		tSiniestro = (TextView) findViewById(R.id.txtSiniestro);
//		Typeface tf = Typeface.createFromAsset(getAssets(),
//		"fonts/DroidSansFallback.ttf");
//		tspinner.setTypeface(tf);
//		ttitle.setText("Módulo de Ajustes del Vehículo");
		//ttitle.Margin.Left="280dp";
//		lylabels.setVisibility(View.INVISIBLE);
		//lytext02.setVisibility(View.VISIBLE);
//		exvb1.setVisibility(View.INVISIBLE);
		//exvb1.setVisibility(View.VISIBLE);		
		
		_path = DATA_PATH + "/ocr.jpg";
		
		if(GlobalOcr.sPlate.length() > 0){
			tplaca.setText(GlobalOcr.sPlate);
		}else{
			tplaca.setText("");
		}
		//Toast.makeText(VehiculoActivity.this,"Global.sPlate: "+Global.sPlate, Toast.LENGTH_LONG).show();
		String sSalida = GlobalOcr.Salida;
		
		if(sSalida.equals("ANORMAL")){
			tplaca.setText("");
		}
		
		//Toast.makeText(VehiculoActivity.this,"sSalida: "+sSalida, Toast.LENGTH_LONG).show();
		
		tserial = (EditText) findViewById(R.id.EditTextserial);
		
		todometer = (EditText) findViewById(R.id.EditTextkilometros);
		
		tnumSiniestro = (EditText) findViewById(R.id.EditTextNumSiniestro);// EditText que contiene el valor del Número de Siniestro para buscar por INMAEst PC (Solo Guatemala)
		
		final Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		final Spinner spinnerT = (Spinner) findViewById(R.id.spinnerT);
		//spinner.setOnItemSelectedListener(this);	
		
//		imb1.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{  	
//				mostrarAlertDialogo();
//			}
//		});	
		
		b1.setOnClickListener(new OnClickListener()
		{
		
			public void onClick(View v)
			{
				//tplaca.setText(Global.sPlate);
				String sTemporal = tplaca.getText().toString().trim();
				String sTemporal2 = "";
				int lenPlaca = 0;
				//sPlaca=tplaca.getText().toString().trim();

				int indexblank = sTemporal.indexOf(" ");
				
				if(indexblank > 0){
					sPlaca = sTemporal.replaceAll("\\s+","");
					sTemporal2 = sPlaca;
				}else
				{
					sPlaca = sTemporal;
					sTemporal2 = sTemporal;
				}
				//lenPlaca = sTemporal2.length();
				indexblank = sTemporal2.indexOf(".");
				if(indexblank > 0){
					sPlaca = sTemporal2.replaceAll("\\.", "");
					sTemporal2 = sPlaca;					
				}else
				{
					sPlaca = sTemporal2;
				}
				//lenPlaca = sTemporal2.length();
				indexblank = sTemporal2.indexOf(",");
				if(indexblank > 0){
					sPlaca = sTemporal2.replaceAll("\\,", "");
				}else
				{
					sPlaca = sTemporal2;
				}
				tplaca.setText(sPlaca);
				lenPlaca = sPlaca.length();
				if(lenPlaca == 0){
					//Toast.makeText(VehiculoActivity.this,"Debe ingresar la Placa del vehiculo!", Toast.LENGTH_LONG).show();
					mostrarToast("Debe ingresar la Placa del vehiculo!");
					return;					
				}
				if(lenPlaca < 3){
					//Toast.makeText(VehiculoActivity.this,"La Placa del vehiculo debe ser mayor o igual a siete (7) caracteres!", Toast.LENGTH_LONG).show();
					mostrarToast("La Placa del vehiculo debe ser mayor o igual a tres (3) caracteres!");

					return;					
				}
				sTemporal = "";
				sTemporal2 = "";
				sTemporal = tserial.getText().toString().trim();
				sTemporal2 = "";
				//sSerial=tserial.getText().toString().trim();
				indexblank = sTemporal.indexOf(" ");
				if(indexblank > 0){
					sSerial = sTemporal.replaceAll("\\s+","");
					sTemporal2 = sSerial;
				}else
				{
					sSerial = sTemporal;
					sTemporal2 = sTemporal;
				}
				indexblank = sTemporal2.indexOf(".");
				if(indexblank > 0){
					sSerial = sTemporal2.replaceAll("\\.", "");
					sTemporal2 = sSerial;
				}				
				else
				{
					sSerial = sTemporal2;
					
				}
				indexblank = sTemporal2.indexOf(",");
				if(indexblank > 0){
					sSerial = sTemporal2.replaceAll("\\,", "");
					sTemporal2 = sSerial;
				}else
				{
					sSerial = sTemporal2;
				}
				int lenSerial = sSerial.length();
				tserial.setText(sSerial);
				if(lenSerial == 0){
					//Toast.makeText(VehiculoActivity.this,"Debe ingresar el Serial del vehiculo!", Toast.LENGTH_LONG).show();
					mostrarToast("Debe ingresar el Serial del vehiculo!");
					return;				
				}
				// Control de validación de mínimo de caracteres para serial del vehículo: Mínimo Diez y Siete (17) - Inhabilitado para Guatemala
				// if(lenSerial < 17){
					// //Toast.makeText(VehiculoActivity.this,"Debe ingresar el Serial del vehiculo!", Toast.LENGTH_LONG).show();
					// mostrarToast("El Serial del vehiculo debe tener 17 caracteres!");
					// return;				
				// }				
				sTemporal = "";
				sTemporal = todometer.getText().toString().trim();				
				//sOdometer=todometer.getText().toString().trim();
				indexblank = sTemporal.indexOf(" ");
				if(indexblank > 0){
					sOdometer = sTemporal.replaceAll("\\s+","");
				}else
				{
					sOdometer = sTemporal;
				}					
				int lenOdometer = sOdometer.length();
				if(lenOdometer == 0){
					//Toast.makeText(VehiculoActivity.this,"Debe ingresar el Kilometraje del vehiculo!", Toast.LENGTH_LONG).show();
					mostrarToast("Debe ingresar el Kilometraje del vehiculo! (odómetro)");
					return;
				}	
				
				sTemporal = "";
				sTemporal = tnumSiniestro.getText().toString().trim();				
				//sOdometer=todometer.getText().toString().trim();
				indexblank = sTemporal.indexOf(" ");
				if(indexblank > 0){
					sNumSiniestro = sTemporal.replaceAll("\\s+","");
				}else
				{
					sNumSiniestro = sTemporal;
				}					
				int lenSiniestro = sNumSiniestro.length();
				if(lenSiniestro == 0){
					//Toast.makeText(VehiculoActivity.this,"Debe ingresar el Kilometraje del vehiculo!", Toast.LENGTH_LONG).show();
					mostrarToast("Debe ingresar el número del Siniestro del vehículo!");
					return;
				}
				
				//mostrarToast("Placa del vehiculo: "+sPlaca);
//				if(sPlaca.equals("AED50X")){
//					//mostrarToast("Dentro Placa del vehiculo: "+sPlaca);
//					sOdometer="AABBCCW3OETRDUDH78";
//					sSerial="556789";
//					sObject = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '" + sOdometer + "','SubModel': '" + "','anio':'2014','marca':'CHERY','modelo':'ORINOCO','placa':'" + sPlaca + "','serialCarroceria':'" + sSerial + "'}}}";

//				}				
				//sObject = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '" + sOdometer + "','SubModel': 'SIN SIGLAS - 4Pt, Sedan - 6Cil., 8i - Manual'" + "','anio':'2014','marca':'CHERY','modelo':'ORINOCO','placa':'" + sPlaca + "','serialCarroceria':'" + sSerial + "'}}}";
				// Confeccionamos el Objeto json a enviar.....(Parte de la data esta cableada mientras tanto)
				//mostrarToast("sObject: "+sObject);
				//sObject = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '" + sOdometer + "','SubModel': '" + "','anio':'','marca':'','modelo':'','placa':'" + sPlaca + "','serialCarroceria':'" + sSerial + "'}}}";
				String sEstimator = GlobalOcr.Perito;
				sObject = "{'eObject': {'auto':{'Estimator': '" + sEstimator + "','Odometer': '" + sOdometer + "','SubModel': '" + "','anio':'" + sAnnio + "','marca':'" + sMarca + "','modelo':'" + sModelo + "','placa':'" + sPlaca + "','serialCarroceria':'" + sSerial + "','perfil':'" + spinVal  + "','taller':'" + spinValT + "','numsiniestro':'" + sNumSiniestro + "'}}}";
//				Toast msg = Toast.makeText(getBaseContext(),"EEEEEXITOOO!", Toast.LENGTH_LONG);
//				 msg.show(); 
//				if(Global.ajuste.equals("mismo")){
					GlobalOcr.ajuste="nuevo";
//				}
				GlobalOcr.eObject=sObject;
				Intent EstIntent2 = new Intent();
				EstIntent2.setClassName("ve.com.inma.mobility_4_1.api","ve.com.inma.mobility_4_1.api.Soa");

				//Toast.makeText(PhotosActivity.this, "sAnnio antesdeenviar: "+sAnnio, Toast.LENGTH_LONG).show();
		    	//myIntent.putExtra("eObject",sObject);
//		    	if(Global.ajuste.equals("nuevo")){
//		    		myIntent.putExtra("ajuste","nuevo");
//		    	}else{
//		    		myIntent.putExtra("ajuste","mismo");
//		    	}
				//this.startActivityForResult(myIntent,1);
				startActivity(EstIntent2);
				//startActivityForResult(myIntent, REQUEST_CODE);
				
				activity.finish();			 
				
			}

		});	 	  		
		
		b3.setOnClickListener(new OnClickListener()
		{
		
			public void onClick(View v)
			{		
				Intent myIntent = new Intent(DataEntry.this, Estimacion.class);
				myIntent.putExtra("origen","DataEntry");
				startActivity(myIntent);
				finish();
			}
		});	 
		
		b2.setOnClickListener(new OnClickListener()
		{
		
			public void onClick(View v)
			{		
				//ttitle.setText("Módulo de Ajustes del Vehículo");
				//ttitle.Margin.Left="200dp";
				//lylabels.setVisibility(View.INVISIBLE);
				//lytext02.setVisibility(View.VISIBLE);
				//exvb1.setVisibility(View.INVISIBLE);
				Intent myIntent = new Intent(DataEntry.this, VehiculoActivity.class);
				startActivity(myIntent);
				finish();				
				//exvb1.setVisibility(View.VISIBLE);				
				
			}
		});	
		
//		b7.setOnClickListener(new OnClickListener()
//		{
//		
//			public void onClick(View v)
//			{
//				mostrarAlertDialogo();		         
//			}
//		});
		// ib3.setOnClickListener(new OnClickListener()
		// {
			// public void onClick(View v)
			// {		
				// startCameraActivity();
			// }
		// });
		
		b4.setOnClickListener(new OnClickListener()
		{
		
			public void onClick(View v)
			{
				if(tplaca.getVisibility() == View.VISIBLE)
				{
					tplaca.setVisibility(View.INVISIBLE);
					//ib3.setVisibility(View.INVISIBLE);	
					tserial.setVisibility(View.INVISIBLE);
					todometer.setVisibility(View.INVISIBLE);
					tspinner.setVisibility(View.INVISIBLE);
					spinner.setVisibility(View.INVISIBLE);
					tspinnerT.setVisibility(View.INVISIBLE);
					spinnerT.setVisibility(View.INVISIBLE);
					tSiniestro.setVisibility(View.INVISIBLE);
					tnumSiniestro.setVisibility(View.INVISIBLE);
				}else{
					tplaca.setVisibility(View.VISIBLE);
					//ib3.setVisibility(View.VISIBLE);
					tserial.setVisibility(View.VISIBLE);
					todometer.setVisibility(View.VISIBLE);
					tspinner.setVisibility(View.VISIBLE);
					spinner.setVisibility(View.VISIBLE);
					tspinnerT.setVisibility(View.VISIBLE);
					spinnerT.setVisibility(View.VISIBLE);
					tSiniestro.setVisibility(View.VISIBLE);
					tnumSiniestro.setVisibility(View.VISIBLE);
					tplaca.requestFocus();
				}

			}
		});
		
		b5.setOnClickListener(new OnClickListener()
		{
		
			public void onClick(View v)
			{
				if(tserial.getVisibility() == View.VISIBLE)
				{
					tplaca.setVisibility(View.INVISIBLE);
					//ib3.setVisibility(View.INVISIBLE);	
					tserial.setVisibility(View.INVISIBLE);
					todometer.setVisibility(View.INVISIBLE);
					tspinner.setVisibility(View.INVISIBLE);
					spinner.setVisibility(View.INVISIBLE);
					tspinnerT.setVisibility(View.INVISIBLE);
					spinnerT.setVisibility(View.INVISIBLE);
					tSiniestro.setVisibility(View.INVISIBLE);
					tnumSiniestro.setVisibility(View.INVISIBLE);
				}else{
					tplaca.setVisibility(View.VISIBLE);
					//ib3.setVisibility(View.VISIBLE);
					tserial.setVisibility(View.VISIBLE);
					todometer.setVisibility(View.VISIBLE);
					tspinner.setVisibility(View.VISIBLE);
					spinner.setVisibility(View.VISIBLE);
					tspinnerT.setVisibility(View.VISIBLE);
					spinnerT.setVisibility(View.VISIBLE);
					tSiniestro.setVisibility(View.VISIBLE);
					tnumSiniestro.setVisibility(View.VISIBLE);
					tplaca.requestFocus();
				}
			}
		});	
		
		b6.setOnClickListener(new OnClickListener()
		{
		
			public void onClick(View v)
			{
				if(todometer.getVisibility() == View.VISIBLE)
				{
					tplaca.setVisibility(View.INVISIBLE);
					//ib3.setVisibility(View.INVISIBLE);	
					tserial.setVisibility(View.INVISIBLE);
					todometer.setVisibility(View.INVISIBLE);
					tspinner.setVisibility(View.INVISIBLE);
					spinner.setVisibility(View.INVISIBLE);
					tspinnerT.setVisibility(View.INVISIBLE);
					spinnerT.setVisibility(View.INVISIBLE);
					tSiniestro.setVisibility(View.INVISIBLE);
					tnumSiniestro.setVisibility(View.INVISIBLE);
				}else{
					tplaca.setVisibility(View.VISIBLE);
					//ib3.setVisibility(View.VISIBLE);
					tserial.setVisibility(View.VISIBLE);
					todometer.setVisibility(View.VISIBLE);
					tspinner.setVisibility(View.VISIBLE);
					spinner.setVisibility(View.VISIBLE);
					tspinnerT.setVisibility(View.VISIBLE);
					spinnerT.setVisibility(View.VISIBLE);
					tSiniestro.setVisibility(View.VISIBLE);
					tnumSiniestro.setVisibility(View.VISIBLE);
					tplaca.requestFocus();
				}
			}
		});			
		
		tplaca.addTextChangedListener(new TextWatcher() {

		    public void onTextChanged(CharSequence s, int start,int before, int count) 
		    {
		    	int size = 7;
		    	
//		    	Toast.makeText(VehiculoActivity.this, "CharSequence s: "+s, Toast.LENGTH_LONG).show();
		        // TODO Auto-generated method stub
		        if(tplaca.getText().toString().length()==size)     //size as per your requirement
		        {
		        	tserial.requestFocus();
		        }
		        
		    	int sizes = 16;
		        // TODO Auto-generated method stub
		        if(tserial.getText().toString().length()==sizes)     //size as per your requirement
		        {
		        	todometer.requestFocus();
		        }		        
		    }
		    public void beforeTextChanged(CharSequence s, int start,
		                    int count, int after) {
		                // TODO Auto-generated method stub
		    	//Toast.makeText(VehiculoActivity.this, "CharSequence s: "+s, Toast.LENGTH_LONG).show();
		    }

		    public void afterTextChanged(Editable s) {
		                // TODO Auto-generated method stub
		    }

		});	
		
		tserial.addTextChangedListener(new TextWatcher() {

		    public void onTextChanged(CharSequence s, int start,int before, int count) 
		    {
		    	int size = 17;
		        // TODO Auto-generated method stub
		        if(tserial.getText().toString().length()==size)     //size as per your requirement
		        {
		        	todometer.requestFocus();
		        }
		        	        
		    }
		    public void beforeTextChanged(CharSequence s, int start,
		                    int count, int after) {
		                // TODO Auto-generated method stub
		    }

		    public void afterTextChanged(Editable s) {
		                // TODO Auto-generated method stub
		    }

		});	
		
		todometer.addTextChangedListener(new TextWatcher() {

		    public void onTextChanged(CharSequence s, int start,int before, int count) 
		    {
		    	int size = 6;
		        // TODO Auto-generated method stub
		        if(todometer.getText().toString().length()==size)     //size as per your requirement
		        {
		        	tnumSiniestro.requestFocus();
		        }
		        	        
		    }
		    public void beforeTextChanged(CharSequence s, int start,
		                    int count, int after) {
		                // TODO Auto-generated method stub
		    }

		    public void afterTextChanged(Editable s) {
		                // TODO Auto-generated method stub
		    }
		});
		
		tnumSiniestro.addTextChangedListener(new TextWatcher() {

		    public void onTextChanged(CharSequence s, int start,int before, int count) 
		    {
		    	int size = 20;
		        // TODO Auto-generated method stub
		        if(tnumSiniestro.getText().toString().length()==size)     //size as per your requirement
		        {
		        	b1.requestFocus();
		        }
		        	        
		    }
		    public void beforeTextChanged(CharSequence s, int start,
		                    int count, int after) {
		                // TODO Auto-generated method stub
		    }

		    public void afterTextChanged(Editable s) {
		                // TODO Auto-generated method stub
		    }
		});
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.profiles_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);	
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	spinVal = String.valueOf(spinner.getSelectedItem());
		    	//mostrarToast("Valor del spinVal: "+spinVal);
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
		
		// Loading spinner data from database
        //loadSpinnerData();
        
    	if (myDb != null) {
   			myDb.close();
   		}
    	Cursor cursor = null;
          try {
       	   myDb = SQLiteDatabase.openDatabase(path+"Suscripcion_Data.db", null, SQLiteDatabase.OPEN_READWRITE);  
       	   
          } catch (SQLiteException sqle) {
            	//appendLog(fechaLogD +  " class: DroidGap.java - PROC: openDatabaseEstimates() - No se pudo abrir o no existe la Base de Datos: estimates.sqlite - " + sqle);
            	//throw sqle;
          }
          
          String query = "SELECT NameKey FROM Talleres WHERE NameKey != '' ORDER BY NameKey;";
          
 		try {
   			
      	 cursor = myDb.rawQuery(query, null);
      	
      	 int conta = cursor.getCount();
      	 if(conta==1){
      		 Toast.makeText(this, "*** No hay talleres pendientes para mostrar ***", Toast.LENGTH_LONG);
      		 return;           		 
      	 }

  	    }catch (CursorIndexOutOfBoundsException ex) {
  	    	//data = "";
  	    	//appendLog(fechaLogD +  " class: Estimacion.java - PROC:  openDatabaseEstimates() - CursorIndexOutOfBoundsException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
  	    	////cursor.close();
  	    	Toast.makeText(this, "ERROR: "+String.valueOf(ex), Toast.LENGTH_LONG).show();			
  	    }catch (SQLiteException exs) {
  	    	//appendLog(fechaLogD +  " class: Estimacion.java - PROC:  openDatabaseEstimates() - SQLiteException al intentar llenar el cursor: c - : " + query +" - " +  exs.getMessage());
  	    	Toast.makeText(this, "ERROR: "+String.valueOf(exs), Toast.LENGTH_LONG).show();			
  		}finally{
  			//cursor.close();
  			//myDb.close();
      	}
 		
 		List<String> labels = new ArrayList<String>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
 
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);
 
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
 
        // attaching data adapter to spinner
        spinnerT.setAdapter(dataAdapter);
		
//		// Create an ArrayAdapter using the string array and a default spinner layout
//		ArrayAdapter<CharSequence> adapterT = ArrayAdapter.createFromResource(this,
//		        R.array.profiles_arrayT, android.R.layout.simple_spinner_item);
//		// Specify the layout to use when the list of choices appears
//		adapterT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		// Apply the adapter to the spinner
//		spinnerT.setAdapter(adapterT);	
		
		spinnerT.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	spinValT = String.valueOf(spinnerT.getSelectedItem());
		    	//mostrarToast("Valor del spinValT: "+spinValT);
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});		
		appendLog("class DataEntry - super.onCreate() - Final de Declaracion de variables");		
	}
	
	
    /**
     * Function to load the spinner data from SQLite database
     * */
    private void loadSpinnerData() {
        // database handler
        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());
    	if (myDb != null) {
   			myDb.close();
   		}
    	Cursor cursor = null;
          try {
       	   myDb = SQLiteDatabase.openDatabase(path+"Suscripcion_Data.db", null, SQLiteDatabase.OPEN_READWRITE);  
       	   
          } catch (SQLiteException sqle) {
            	//appendLog(fechaLogD +  " class: DroidGap.java - PROC: openDatabaseEstimates() - No se pudo abrir o no existe la Base de Datos: estimates.sqlite - " + sqle);
            	//throw sqle;
          }
          
          String query = "SELECT NameKey FROM Talleres WHERE NameKey != '' ORDER BY NameKey;";
          
 		try {
   			
      	 cursor = myDb.rawQuery(query, null);
      	
      	
      	 int conta = cursor.getCount();
      	 if(conta==1){
      		 Toast.makeText(this, "*** No hay talleres pendientes para mostrar ***", Toast.LENGTH_LONG);
      		 return;           		 
      	 }
  	    }catch (CursorIndexOutOfBoundsException ex) {
  	    	//data = "";
  	    	//appendLog(fechaLogD +  " class: Estimacion.java - PROC:  openDatabaseEstimates() - CursorIndexOutOfBoundsException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
  	    	////cursor.close();
  	    	Toast.makeText(this, "ERROR: "+String.valueOf(ex), Toast.LENGTH_LONG).show();			
  	    }catch (SQLiteException exs) {
  	    	//appendLog(fechaLogD +  " class: Estimacion.java - PROC:  openDatabaseEstimates() - SQLiteException al intentar llenar el cursor: c - : " + query +" - " +  exs.getMessage());
  	    	Toast.makeText(this, "ERROR: "+String.valueOf(exs), Toast.LENGTH_LONG).show();			
  		}finally{
  			//cursor.close();
  			//myDb.close();
      	}
 		
 		List<String> labels = new ArrayList<String>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
 
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);
 
        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
 
        // attaching data adapter to spinner
        spinnerT.setAdapter(dataAdapter);
    }
 
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        // On selecting a spinner item
        String label = parent.getItemAtPosition(position).toString();
 
        // Showing selected spinner item
//        Toast.makeText(parent.getContext(), "You selected taller: " + label,
//                Toast.LENGTH_LONG).show();
 
    }
 
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
 
    }  
	
//	public class SpinnerActivity extends Activity implements OnItemSelectedListener {
//	    
//	    public void onItemSelected(AdapterView<?> parent, View view, 
//	            int pos, long id) {
//	        // An item was selected. You can retrieve the selected item using
//	        //parent.getItemAtPosition(pos)
//			final String spinVal = String.valueOf(spinner.getSelectedItem());
//			mostrarToast("Valor del spinVal: "+spinVal);
//	    }
//
//	    public void onNothingSelected(AdapterView<?> parent) {
//	        // Another interface callback
//	    }
//	}
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
	
	// Simple android photo capture:
	// http://labs.makemachine.net/2010/03/simple-android-photo-capture/

	protected void startCameraActivity() {
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);

		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, 0);

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(SimpleAndroidOCRActivity.PHOTO_TAKEN, _taken);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		//Log.i(TAG, "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(SimpleAndroidOCRActivity.PHOTO_TAKEN)) {
			//onPhotoTaken();
		}
	}

//	protected void onPhotoTaken() {
//		_taken = true;
//
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inSampleSize = 4;
//
//		Bitmap bitmap = BitmapFactory.decodeFile(_path, options);
//
//		try {
//			ExifInterface exif = new ExifInterface(_path);
//			int exifOrientation = exif.getAttributeInt(
//					ExifInterface.TAG_ORIENTATION,
//					ExifInterface.ORIENTATION_NORMAL);
//
//			//Log.v(TAG, "Orient: " + exifOrientation);
//
//			int rotate = 0;
//
//			switch (exifOrientation) {
//			case ExifInterface.ORIENTATION_ROTATE_90:
//				rotate = 90;
//				break;
//			case ExifInterface.ORIENTATION_ROTATE_180:
//				rotate = 180;
//				break;
//			case ExifInterface.ORIENTATION_ROTATE_270:
//				rotate = 270;
//				break;
//			}
//
//			//Log.v(TAG, "Rotation: " + rotate);
//
//			if (rotate != 0) {
//
//				// Getting width & height of the given image.
//				int w = bitmap.getWidth();
//				int h = bitmap.getHeight();
//
//				// Setting pre rotate
//				Matrix mtx = new Matrix();
//				mtx.preRotate(rotate);
//
//				// Rotating Bitmap
//				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
//			}
//
//			// Convert to ARGB_8888, required by tess
//			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//
//		} catch (IOException e) {
//			//Log.e(TAG, "Couldn't correct orientation: " + e.toString());
//		}
//
//		// _image.setImageBitmap( bitmap );
//		
//		//Log.v(TAG, "Before baseApi");
//
//		TessBaseAPI baseApi = new TessBaseAPI();
//		baseApi.setDebug(true);
//		baseApi.init(DATA_PATH, lang);
//		baseApi.setImage(bitmap);
//				
//		recognizedText = baseApi.getUTF8Text();
//		
//		baseApi.end();
//
//		// You now have the text in recognizedText var, you can do anything with it.
//		// We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
//		// so that garbage doesn't make it to the display.
//
//		//Log.v(TAG, "OCRED TEXT: " + recognizedText);
//
//		if ( lang.equalsIgnoreCase("eng") ) {
//			recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
//		}
//		
//		recognizedText = recognizedText.trim();
//		
////	      Toast toast = Toast.makeText(this, "Texto reconocido por OCR: "+recognizedText, Toast.LENGTH_SHORT);
////
////	      toast.show();
//
//		if ( recognizedText.length() != 0 ) {
//			tplaca.setText("");
//			tplaca.setText(tplaca.getText().toString().length() == 0 ? recognizedText : tplaca.getText() + " " + recognizedText);
//			//tplaca.setSelected(tplaca.getText().toString().length());
//			tplaca.setTextSize(26);
//			int len = tplaca.getText().toString().length();
//			
////			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
////		       lp.gravity= Gravity.CENTER_HORIZONTAL; 
////		       tplaca.setLayoutParams(lp);
//
//			//_buttonsend.setVisibility(View.VISIBLE);
//		}
//
//		// Cycle done.
//	}	
    private void mostrarAlertDialogo(){
    	
        // custom dialog
         final Dialog dialog = new Dialog(this);
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
         dialog.setContentView(R.layout.customflags);
         //dialog.setTitle("                              *** GRUPO INMA; C.A.  ***  ");
         // set the custom dialog components - text, image and button
//         TextView text = (TextView) dialog.findViewById(R.id.text);
//         text.setText("INMAEst Movil");
         TextView text2 = (TextView) dialog.findViewById(R.id.text2);
         text2.setText("INMAEst Movil: 4.1 - G. - Base de Datos versión: 122015");
         TextView text3 = (TextView) dialog.findViewById(R.id.text3);
         text3.setText(" Powered by Grupo INMA C.A. Copyrigth © 2015 - 2016");            
         ImageView image = (ImageView) dialog.findViewById(R.id.image);
         image.setImageResource(R.drawable.logoinmaestpeq);

         Button venezButton = (Button) dialog.findViewById(R.id.venezuelabutton);
         Button panamButton = (Button) dialog.findViewById(R.id.panamabutton);
         Button costaButton = (Button) dialog.findViewById(R.id.costaricabutton);
         // if button is clicked, close the custom dialog
         venezButton.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
            	 b4.setBackgroundResource(R.drawable.placa);
                 dialog.dismiss();
             }
         });
         
         panamButton.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
            	 b4.setBackgroundResource(R.drawable.placapanama);
                 dialog.dismiss();
             }
         });
         
         costaButton.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View v) {
            	 b4.setBackgroundResource(R.drawable.placacostarica);
                 dialog.dismiss();
             }
         });
         

         dialog.show();
       }
    
	   @Override
	   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		   //Toast.makeText(this, "Dentro de VehiculoActivity de vuelta resultCode: "+resultCode, Toast.LENGTH_LONG).show();
		   super.onActivityResult(requestCode, resultCode, data);
//	      Toast.makeText(this, "Dentro de VehiculoActivity de vuelta despues de super requestCode: "+requestCode, Toast.LENGTH_LONG).show();
////	      Toast.makeText(this, "Dentro de VehiculoActivity de vuelta: ", Toast.LENGTH_LONG).show();
//			if (resultCode == -1) {
//				onPhotoTaken();
//			} else {
//				//Log.v(TAG, "User cancelled");
//			}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.data_entry, menu);
		return true;
	}
	
 	public void appendLog(String text)
 	{       
 		TomaFechaSistema();
 		//Toast.makeText(this, "appendLogF - TomaFechaSistema: "+fechaLogD, Toast.LENGTH_LONG).show();
	    versionLog = "" + fechaLogD;
 	   ////File logFile = new File("sdcard/INMAEst_Movil/logINMAEstFotos"+versionLog+".file");
	   File logFile = new File(sPrimaryStorage+"/Download/logINMAEstDataEntry"+versionLog+".file");
 	  //Toast.makeText(this, " appendLogF - logFile: "+logFile, Toast.LENGTH_LONG).show();
 	   if (!logFile.exists())
 	   {
 	      try
 	      {
 	         logFile.createNewFile();
 	        //Toast.makeText(this, "Dentro de if (!logFile.exists()) - SE CREO EL LOG con Fecha/Hora: "+fechaLogD, Toast.LENGTH_LONG).show();
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
 	     //Toast.makeText(this, "appendLogF - SE ESCRIBIO EN EL BUFFER EL LOG con Fecha/Hora: "+fechaLogD, Toast.LENGTH_LONG).show();
 	   }
 	   catch (IOException e)
 	   {
 	      // TODO Auto-generated catch block
 	      e.printStackTrace();
 	   }
 	  //Toast.makeText(this, "SE GRABO EL LOG con Fecha/Hora: "+fechaLogD, Toast.LENGTH_LONG).show();
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

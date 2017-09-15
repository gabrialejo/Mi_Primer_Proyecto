package com.datumdroid.android.ocr.simple;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.BitmapFactory;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Files;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ActivityGroup;
import android.app.AlertDialog;
//import android.app.LocalActivityManager;
import android.app.ProgressDialog;

import org.apache.commons.lang3.StringUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.KeepAliveHttpsTransportSE;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.serialization.SoapObject;
//import org.apache.commons.codec.binary.Base64;
import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import org.apache.soap.encoding.soapenc.Base64; 	
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.Proxy;
//import org.xmlpull.v1.XmlPullParserException;
import ve.com.inma.mobility_4_1.api.R;

import com.datumdroid.android.ocr.simple.FotoPackage;
//import com.datumdroid.android.ocr.simple.R;
import com.phonegap.Global;

/*
 * EstimateActivity: Clase que ofrece al usuario un Resumen del Ajuste en un "Arbol de detalle"
 * Llama a la clase interna: "SincronizarEstimacion()" para enviar ajustes al Web Service de la nube
 * Llama a la clase interna: "Send_Fotos_WS_FromDirectory()" para enviar las fotos de ajustes al Web Service de la nube
 * @author José A. Azpurua - 2013 - Última modificación: 23 Ene. 2015 - José A. Azpurua

 */
public class EstimateActivity extends ActivityGroup {
	private ArrayList<String> grupos;
	private ArrayList<ArrayList<ArrayList<String>>> hijos;
//	private ArrayList<String> grupos;
//	private ArrayList<ArrayList<ArrayList<String>>> hijos;	
	//public VehiculoBean vb = null;
	private JSONObject o = null;
	private JSONArray d = null;
	private JSONObject o2 = null;
	private JSONArray d2 = null;	
	private String anio = "";
	private String mark = "";
	private String model = "";
	private String submodel = "";
	private String placa = "";
	private String odometer = "";
	private String vehicleIdNumber = "";
	private String estimator = "";
	private String estimateDate = "";
	private String estimateId = "";
	private String sObject = "";
	private File file = null;
	//private file zipFile;
	private Button b1;
	private Button b2;
	private Button b3;	
	protected static final int REQUEST_CODE = 10;
	protected final Activity activity = this;
	private File[] files;
	private File[] files2;
	private AlertDialog.Builder builder = null;
	private AlertDialog alert =  null;
	//private int displayalert = 0;
	private String json = "";
	private String json2 = "";
	private ExpandableListView l = null;
	private String sPrimaryStorage = "";
	private String sSecondaryStorage =  "";
	public String versionLog = "";
	public String fechaLogD = "";
	public String fechaHoraLogD = "";	
	private ProgressDialog pDialog1 = null;
	private FotoPackage fp = null;
	private String sFileName = "";
	private Bitmap bimage = null;
	
	private static byte[] bytefoto;
	private static Base64 base64 = null;
	private static String sBase64 = "";
    private SQLiteDatabase myDb = null; // Database object
    public String path = "/mnt/sdcard/databasedata/";	//Environment.getDataDirectory() + "/data/com.totsp.database/databases/";
	//private Map<String, FotoPackage> fotos = new HashMap<String, FotoPackage>();

    //Parametros de Producción Web Service Nube - Guatemala - GENERALI
//************************************************************************************    
	// private static final String METHOD_NAME = "inmaEst_estimateProcess";
	// // Namespace definido en el servicio web
	// private static final String NAMESPACE = "http://svrestprod.cloudapp.net/MobilityService";
	// // namespace + metodo
	// private static final String SOAP_ACTION = "http://svrestprod.cloudapp.net/MobilityService/inmaEst_estimateProcess";
	// // Fichero de definicion del servicio web para subir el Ajuste
	// private static final String URL = "http://svrestprod.cloudapp.net/MobilityService/service.asmx";	
	// // Método de subida de las imágenes
	// private String METHOD_NAME_IMAGE = "SaveImg";
	// // Namespace definido en el servicio web
	// private String NAMESPACE_IMAGE = "http://svrestprod.cloudapp.net/IEWsimage";
	// // namespace + metodo
	// private String SOAP_ACTION_IMAGE =  "http://svrestprod.cloudapp.net/IEWsimage/SaveImg";
	// // Fichero de definicion del servcio web
	// private String URL_IMAGE = "http://svrestprod.cloudapp.net/WSSite/Inmaestimglb.asmx";   
//*********************************************************************************************    
    
	  //Parametros de Demo Web Service Nube - Guatemala - GENERALI	
	private static final String METHOD_NAME = "inmaEst_estimateProcess";
	// Namespace definido en el servicio web
	private static final String NAMESPACE = "http://inmaapps1.grupoinma.com/MobilityService";
	// namespace + metodo
	private static final String SOAP_ACTION = "http://inmaapps1.grupoinma.com/MobilityService/inmaEst_estimateProcess";
	// Fichero de definicion del servicio web para subir el Ajuste
	private static final String URL = "http://inmaapps1.grupoinma.com/MobilityService/service.asmx";	
	// Método de subida de las imágenes
	private String METHOD_NAME_IMAGE = "SaveImg";
	// Namespace definido en el servicio web
	private String NAMESPACE_IMAGE = "http://inmaapps1.grupoinma.com/IEWsimage";
	// namespace + metodo
	private String SOAP_ACTION_IMAGE =  "http://inmaapps1.grupoinma.com/IEWsimage/SaveImg";
	// Fichero de definicion del servcio web
	private String URL_IMAGE = "http://inmaapps1.grupoinma.com/WSSite/Inmaestimglb.asmx"; 	
//*********************************************************************************************   
	@Override
    public void onCreate(Bundle savedInstanceState) {
//		Toast.makeText(EstimateActivity.this, "onCreate: "+GlobalOcr.Salida, Toast.LENGTH_LONG).show();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);        
        setContentView(R.layout.main_expand);

        appendLogE(" class: EstimateActivity.java - PROC: onCreate() - Llegue a Estimate Activity Path: "+path);
        //android.provider.Settings.System.putString(getContentResolver(), android.provider.Settings.System.HTTP_PROXY, "192.168.0.254:8080");
        
    	if(GlobalOcr.Salida.equals("ANORMAL")){
    		//Toast.makeText(EstimateActivity.this, "Blank anio EstimateActivity: "+GlobalOcr.Salida, Toast.LENGTH_LONG).show();
			Intent myIntent = new Intent(EstimateActivity.this, VehiculoActivity.class);				

			startActivity(myIntent);
			Toast.makeText(EstimateActivity.this, "El Usuario decidio salir del Ajuste [Esc]/[Back], antes de Finalizar: ", Toast.LENGTH_LONG).show();
			activity.finish();
    		 
    	}        
        //Toast.makeText(EstimateActivity.this, "LLEGUE A EstimateActivity - SALIDA: "+GlobalOcr.Salida, Toast.LENGTH_LONG).show();
        
        l = (ExpandableListView) findViewById(R.id.ExpandableListView01);
       
		b1 = (Button) findViewById(R.id.button1);	// Botón de SINCRONIZAR el Ajuste
		b2 = (Button) findViewById(R.id.button2);	// Botón de Nuevo Ajuste (Mismo Vehículo u otro distinto)
		b3 = (Button) findViewById(R.id.button3);	// Botón de SALIR de la Aplicación

		b1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{ 
				  boolean blnConnNet = checkConnectivity();
				  
				  if(!blnConnNet){
					  mostrarToast("*** Actualmente NO hay conectividad para sincronizar. Revise e intente cuando se reestablezca ***");
					  return;
				  }				
				//mostrarProgressDialog();
				//appendLogE(" class: EstimateActivity.java - PROC: b1.onClick - Antes de llamar a Send_Object_WS_FromDirectory() - Global.rutaFotos: "+Global.rutaFotos);
				Send_Object_WS_FromDirectory();
				Send_Fotos_WS_FromDirectory();
				//Toast msg = Toast.makeText(getBaseContext(),"Global.rutaFotos....!"+Global.rutaFotos, Toast.LENGTH_LONG);
				//msg.show(); 
				////Toast msg = Toast.makeText(getBaseContext(),"SINCRONIZANDO....!"+json, Toast.LENGTH_LONG);
				////msg.show(); 
				//CreateZipFileFromDirectory();				
				////Intent myIntent = new Intent();
				////myIntent.setClassName("com.datumdroid.android.ocr.simple","WebService.class");
				////Intent myIntent = new Intent(EstimateActivity.this, WebService.class);
		    	////myIntent.putExtra("eObject",json);
		    	//myIntent.putExtra("estimateId",zipFile);
				////startActivityForResult(myIntent, REQUEST_CODE);
				//startActivity(myIntent);
				b1.setEnabled(false);
				b2.setVisibility(View.VISIBLE);

			}
		}); 

		b2.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{   			
		        ////Intent myIntent = new Intent(v.getContext(), com.example.android.photobyintent.PhotoIntentActivity.class);
				//Intent myIntent = new Intent(); 
				//myIntent.setClassName("com.example.android.photobyintent","PhotoIntentActivity.class");
				//Intent myIntent = new Intent(this, "PhotoIntentActivity.class");
				//Intent myIntent = new Intent(EstimateActivity.this, VehiculoActivity.class);				
		    	//yIntent.putExtra("eObject",sObject);
				//this.startActivityForResult(myIntent,1);
				//startActivity(myIntent);
				//startActivityForResult(myIntent, REQUEST_CODE);
				////replaceContentView("mainfoto", myIntent);
				//activity.finish();
				
				mostrarAlertDialogo();
			}
		});
		
		b3.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent myIntent = new Intent(EstimateActivity.this, VehiculoActivity.class);
				startActivity(myIntent);
				finish();
				return;
			}
		});		
		
		//TextView text = (TextView) findViewById(R.id.textView1);
		//Toast.makeText(EstimateActivity.this, "Antes de Bundle recieveParams = getIntent().getExtras() ", Toast.LENGTH_LONG).show();
		//Bundle recieveParams = getIntent().getExtras();
		//Toast.makeText(EstimateActivity.this, "recieveParams = "+recieveParams, Toast.LENGTH_LONG).show();
		JSONObject eObject = null;
		
			//if(recieveParams !=null){
	
			   //json = recieveParams.getString("eObject");
				
				json = GlobalOcr.eObject;
				
			   //Toast.makeText(EstimateActivity.this, "EstimateActivity 1era. vez json: "+json, Toast.LENGTH_LONG).show();
					 
					 try {
						eObject = new JSONObject(json);
					 } catch (JSONException e) {
							e.printStackTrace();
							appendLogE(" class: EstimateActivity.java - PROC: onCreate() - Try del eObject = new JSONObject(json) -linea= 202 - Excepción JSON: "+e.getMessage());
					 }
					 
					 o = eObject;
					 //d = new eObject();
				 
					 try {
						o = o.getJSONObject("header");
						
					} catch (JSONException e) {
						e.printStackTrace();
						appendLogE(" class: EstimateActivity.java - PROC: onCreate() - Try del o = o.getJSONObject(header) -linea= 212 - Excepción JSON: "+e.getMessage());
					} 
					//Toast.makeText(EstimateActivity.this, "o UNO EstimateActivity: "+o, Toast.LENGTH_LONG).show();
					//Toast.makeText(EstimateActivity.this, "o DOS EstimateActivity: "+o, Toast.LENGTH_LONG).show();					
//					try {
//						anio = o.getString("Year");
//					} catch (JSONException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
					//Toast.makeText(EstimateActivity.this, "OnCreate() anio EstimateActivity anio: "+anio, Toast.LENGTH_LONG).show();						
					

					 try {
						 d = eObject.getJSONArray("detail");
						 
					} catch (JSONException e) {
						e.printStackTrace();
						appendLogE(" class: EstimateActivity.java - PROC: onCreate() - Try del d = eObject.getJSONObject(detail) -linea= 230 - Excepción JSON: "+e.getMessage());
					}
					//Toast.makeText(EstimateActivity.this, "d Detail EstimateActivity: "+d, Toast.LENGTH_LONG).show();
					
					if(GlobalOcr.Salida.equals("ANORMAL")){
						//Toast.makeText(EstimateActivity.this, "EstimateActivity - GlobalOcr.Salida: "+GlobalOcr.Salida, Toast.LENGTH_LONG).show();
						b2.setVisibility(View.VISIBLE);
					}
					
////				boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
				//Toast.makeText(this, "isSDPresent: "+isSDPresent, Toast.LENGTH_LONG).show();
//				if(isSDPresent){
//					sPrimaryStorage = System.getenv("EXTERNAL_STORAGE");
//					sSecondaryStorage = System.getenv("SECONDARY_STORAGE");
//					String[] tempS = sSecondaryStorage.split(":");
//					sSecondaryStorage=tempS[0];
//				}else{
					sPrimaryStorage = "/mnt/sdcard/";
					sSecondaryStorage = "/mnt/sdcard/";
					//sSecondaryStorage = "/mnt/extSdCard/";
//				}						
					
				cargarDatos();
				
					miExpandableAdapter adaptador = new miExpandableAdapter(this, grupos, hijos);
					l.setAdapter(adaptador);
					
					GlobalOcr.svar1="SI";

		    	//Toast.makeText(EstimateActivity.this, "***FIN*** EstimateActivity: ", Toast.LENGTH_LONG).show();
		//}
		
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
    
    public class miExpandableAdapter extends BaseExpandableListAdapter {
 
    	private ArrayList<String> groups;
 
        private ArrayList<ArrayList<ArrayList<String>>> children;
 
    	private Context context;
 
    	public miExpandableAdapter(Context context, ArrayList<String> groups, ArrayList<ArrayList<ArrayList<String>>> children) {
            if(grupos!=null){
        		this.context = context;
                this.groups = grupos;
                this.children = hijos;
            }else{
            	Toast msg = Toast.makeText(getBaseContext(),"grupos: "+grupos, Toast.LENGTH_LONG);
            	msg = Toast.makeText(getBaseContext(),"groups: "+groups, Toast.LENGTH_LONG);
            }

//			Toast msg = Toast.makeText(getBaseContext(),"groups: "+groups, Toast.LENGTH_LONG);
//			msg.show(); 
//			msg = Toast.makeText(getBaseContext(),"children: "+children, Toast.LENGTH_LONG);
//			msg.show();
        }
 
 
    	@Override
        public boolean areAllItemsEnabled()
        {
            return true;
        }
 
 
        @Override
        public ArrayList<String> getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition).get(childPosition);
        }
 
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
 
 
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,View convertView, ViewGroup parent) {
 
        	String hijo = (String) ((ArrayList<String>)getChild(groupPosition, childPosition)).get(0);
 
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.expandablelistview_hijo, null);
            }
 
            //TextView lblhijotxt = (TextView) convertView.findViewById(R.id.TextViewlblHijo01);
            TextView hijotxt = (TextView) convertView.findViewById(R.id.TextViewHijo01);
 
            hijotxt.setText(hijo);
 
            return convertView;
        }
 
        @Override
        public int getChildrenCount(int groupPosition) {
            return children.get(groupPosition).size();
        }
 
        @Override
        public String getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }
 
        @Override
        public int getGroupCount() {
        	//Toast msg = Toast.makeText(getBaseContext(),"groups.size(): "+groups.size(), Toast.LENGTH_LONG);
        	return groups.size();
        }
 
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }
 
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
 
        	String group = (String) getGroup(groupPosition);
 
        	if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.expandablelistview_grupo, null);
            }
 
            TextView grouptxt = (TextView) convertView.findViewById(R.id.TextViewGrupo);
 
            grouptxt.setText(group);
 
            return convertView;
        }
 
        @Override
        public boolean hasStableIds() {
            return true;
        }
 
        @Override
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }
 
    }
 
    private void cargarDatos(){
    	//Toast.makeText(EstimateActivity.this, "Entre a cargarDatos: ", Toast.LENGTH_LONG).show();
        try {
			anio = o.getString("Year");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			appendLogE(" class: EstimateActivity.java - PROC: cargarDatos() - Try del anio = o.getString(Year) -linea= 230 - Excepción JSON: "+e1.getMessage());
		}
		//Toast.makeText(EstimateActivity.this, "anio EstimateActivity: "+anio, Toast.LENGTH_LONG).show();
    	if(StringUtils.isBlank(anio)){
    		//Toast.makeText(EstimateActivity.this, "Blank anio EstimateActivity: ", Toast.LENGTH_LONG).show();
			Intent myIntent = new Intent(EstimateActivity.this, VehiculoActivity.class);				

			startActivity(myIntent);
			Toast.makeText(EstimateActivity.this, "El Usuario decidio salir del Ajuste [Esc]/[Back], antes de Finalizar: ", Toast.LENGTH_LONG).show();
			activity.finish();
    		 
    	}else{
//    		
			 try {
				 
			    	grupos= new ArrayList<String>();
			    	hijos= new ArrayList<ArrayList<ArrayList<String>>>();
			 
			    	grupos.add("Datos Vehículo");
			        grupos.add("Detalle Ajuste");
			        grupos.add("Fotos");		
			        
				//o = eObject.getJSONObject("eObject");
				//o = o.getJSONObject("header");
				//Toast.makeText(EstimateActivity.this, "o en CargaDatos: "+o, Toast.LENGTH_LONG).show();
				//anio = o.getString("Year");
				//Toast.makeText(EstimateActivity.this, "anio EstimateActivity: "+anio, Toast.LENGTH_LONG).show();
				mark = o.getString("Make");
				//Toast.makeText(EstimateActivity.this, "mark EstimateActivity: "+mark, Toast.LENGTH_LONG).show();
				model = o.getString("Model");
				//Toast.makeText(EstimateActivity.this, "model EstimateActivity: "+model, Toast.LENGTH_LONG).show();
				submodel = o.getString("SubModel");
				//Toast.makeText(EstimateActivity.this, "submodel EstimateActivity: "+submodel, Toast.LENGTH_LONG).show();
				odometer = o.getString("Odometer");
				//Toast.makeText(EstimateActivity.this, "odometer: "+odometer, Toast.LENGTH_LONG).show();
				placa = o.getString("Plate");
				//Toast.makeText(EstimateActivity.this, "placa: "+placa, Toast.LENGTH_LONG).show();
				vehicleIdNumber = o.getString("VehicleIdNumber");
				estimator = o.getString("Estimator");
				
				estimateDate = o.getString("EstimateDate");
				estimateId = o.getString("EstimateId");
				//Toast.makeText(EstimateActivity.this, "EstimateActivity cargarDatos estimateId: "+estimateId, Toast.LENGTH_LONG).show();
				//Toast.makeText(EstimateActivity.this, "estimateDate: "+estimateDate, Toast.LENGTH_LONG).show();
				//Toast.makeText(EstimateActivity.this, "o EstimateActivity: Marca: "+mark+"-"+model+" , submodel: "+submodel+", placa: "+placa, Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				e.printStackTrace();
				appendLogE(" class: EstimateActivity.java - PROC: cargarDatos() - Try del grupos= new ArrayList<String>() - linea= 391 - Excepción JSON: "+e.getMessage());
			}  
	 
			hijos.add(new ArrayList<ArrayList<String>>());
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(0).add("PLACA:                                         "+placa);
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(1).add("S.Carroceria:                               "+vehicleIdNumber);
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(2).add("Kilometraje:                                 "+odometer);
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(3).add("Año:                                               "+anio);
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(4).add("Marca:                                           "+mark);
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(5).add("Modelo:                                          "+model);
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(6).add("Version:                                          "+submodel);
			
			//Toast.makeText(EstimateActivity.this, "d en CargaDatos:: "+d, Toast.LENGTH_LONG).show();
			
			hijos.add(new ArrayList<ArrayList<String>>());
			try {
				for (int i = 0; i < d.length(); i++) {
					JSONObject c = d.getJSONObject(i);
					String sLineType = c.getString("LineType");
					String sDecriptor = c.getString("CEGDescription");
					//Toast.makeText(EstimateActivity.this, "d en CargaDatos CEGDescription: "+sDecriptor, Toast.LENGTH_LONG).show();
					hijos.get(1).add(new ArrayList<String>());
					hijos.get(1).get(i).add("Item "+i+"   -          "+ sDecriptor);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				appendLogE(" class: EstimateActivity.java - PROC: cargarDatos() - Try del for (int i = 0; i < d.length(); i++) - linea= 442 - Excepción: "+e.getMessage());
			}
			
//			Toast msg = Toast.makeText(getBaseContext(),"cargarDatos grupos: "+grupos, Toast.LENGTH_LONG);
//			msg.show(); 
//			msg = Toast.makeText(getBaseContext(),"cargarDatos hijos: "+hijos, Toast.LENGTH_LONG);
//			msg.show();
//			boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
//			Toast.makeText(this, "isSDPresent: "+isSDPresent, Toast.LENGTH_LONG).show();
////			if(isSDPresent){
////				sPrimaryStorage = System.getenv("EXTERNAL_STORAGE");
////				sSecondaryStorage = System.getenv("SECONDARY_STORAGE");
////				String[] tempS = sSecondaryStorage.split(":");
////				sSecondaryStorage=tempS[0];
////			}else{
////				sPrimaryStorage = "/mnt/sdcard/";
////				sSecondaryStorage = "/mnt/extSdCard/";
////			}			
////			
//			String sMes = estimateDate.substring(0, 2);		
//			String sDia = estimateDate.substring(3, 5);
//			String sAnio = estimateDate.substring(6, 10);
//			
//			//String sPath = "/mnt/sdcard/INMAEst_Movil/"+sAnio+"/"+sMes+"/"+sDia+"/"+placa+"/"+estimateId;
//			String sPath = "/mnt/sdcard/INMAEst_Movil/"+sAnio+"/"+sMes+"/"+sDia+"/"+placa+"/"+estimateId.substring(0,6);
			String sPath = GlobalOcr.rutaFotos;

			//Toast.makeText(EstimateActivity.this, "sPath en CargaDatos: "+sPath, Toast.LENGTH_LONG).show();
//			Toast.makeText(EstimateActivity.this, "sPath en CargaDatos: "+sPath, Toast.LENGTH_LONG).show();
			appendLogE(" class: EstimateActivity.java - PROC: cargarDatos() - sPath = Global.rutaFotos: "+sPath);
			appendLogE(" class: EstimateActivity.java - PROC: cargarDatos() - Global.rutaAjusteG: "+Global.rutaAjusteG);
			Global.estimateId=estimateId;
			String sFilename = "";
			try {
				hijos.add(new ArrayList<ArrayList<String>>());
				
				File f = new File(sPath);
				
				if (f.exists()) {
					files=f.listFiles();
					//Toast.makeText(EstimateActivity.this, "ENTRO EN if (f.exists()) { : ", Toast.LENGTH_LONG).show();
				}else{
					//Toast.makeText(EstimateActivity.this, "NO ENTRO EN if (f.exists()) { : ", Toast.LENGTH_LONG).show();
					appendLogE(" class: EstimateActivity.java - PROC: cargarDatos() - if (f.exists()) NO ENTRO : ");
				}

				if(files.length>0){
					GlobalOcr.cuentafotos=files.length;
					for(int i=0; i<files.length; i++){
						File filetemp = files[i];
						sFilename = filetemp.getName();
						hijos.get(2).add(new ArrayList<String>());
						hijos.get(2).get(i).add(sFilename+"   -   "+sPath + sFilename);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				appendLogE(" class: EstimateActivity.java - PROC: cargarDatos() - Try del hijos.add() - linea= 483 - Excepción: "+e.getMessage());
			}
			
			b1.setVisibility(View.VISIBLE);
//			//b2.setVisibility(View.VISIBLE);
       
    	}
        
    } 
    
    private void cargarDatos2(){
        
        try {
			anio = o2.getString("Year");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//Toast.makeText(EstimateActivity.this, "anio EstimateActivity: "+anio, Toast.LENGTH_LONG).show();
    	if(StringUtils.isBlank(anio)){
    		//Toast.makeText(EstimateActivity.this, "Blank anio EstimateActivity: ", Toast.LENGTH_LONG).show();
			Intent myIntent = new Intent(EstimateActivity.this, VehiculoActivity.class);				

			startActivity(myIntent);

			activity.finish();
    		 
    	}else{
    		
			 try {
				 
			    	grupos= new ArrayList<String>();
			    	hijos= new ArrayList<ArrayList<ArrayList<String>>>();
			    	hijos.removeAll(hijos);
			 
			    	grupos.add("Datos Vehículo");
			        grupos.add("INMAEst");
			        grupos.add("Fotos");		
			        
				//o = eObject.getJSONObject("eObject");
				//o = o.getJSONObject("header");
				//Toast.makeText(EstimateActivity.this, "o en CargaDatos: "+o, Toast.LENGTH_LONG).show();
				//anio = o.getString("Year");
				//Toast.makeText(EstimateActivity.this, "anio EstimateActivity: "+anio, Toast.LENGTH_LONG).show();
				mark = o2.getString("Make");
				//Toast.makeText(EstimateActivity.this, "mark EstimateActivity: "+mark, Toast.LENGTH_LONG).show();
				model = o2.getString("Model");
				//Toast.makeText(EstimateActivity.this, "model EstimateActivity: "+model, Toast.LENGTH_LONG).show();
				submodel = o2.getString("SubModel");
				//Toast.makeText(EstimateActivity.this, "submodel EstimateActivity: "+submodel, Toast.LENGTH_LONG).show();
				odometer = o2.getString("Odometer");
				//Toast.makeText(EstimateActivity.this, "odometer: "+odometer, Toast.LENGTH_LONG).show();
				placa = o2.getString("Plate");
				//Toast.makeText(EstimateActivity.this, "placa: "+placa, Toast.LENGTH_LONG).show();
				vehicleIdNumber = o.getString("VehicleIdNumber");
				estimator = o2.getString("Estimator");
				
				estimateDate = o2.getString("EstimateDate");
				estimateId = o2.getString("EstimateId");
				//Toast.makeText(EstimateActivity.this, "EstimateActivity cargarDatos2 - estimateId: "+estimateId, Toast.LENGTH_LONG).show();
				//Toast.makeText(EstimateActivity.this, "estimateDate: "+estimateDate, Toast.LENGTH_LONG).show();
				//Toast.makeText(EstimateActivity.this, "o EstimateActivity: Marca: "+mark+"-"+model+" , submodel: "+submodel+", placa: "+placa, Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				e.printStackTrace();
				appendLogE(" class: EstimateActivity.java - PROC: cargarDatos2() - Try del grupos= new ArrayList<String>() - linea= 541 - Excepción JSON: "+e.getMessage());
			}  
			
			//hijos.clear();
			
	 
			hijos.add(new ArrayList<ArrayList<String>>());
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(0).add("PLACA:                                         "+placa);
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(1).add("S.Carroceria:                               "+vehicleIdNumber);
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(2).add("Kilometraje:                                 "+odometer);
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(3).add("Año:                                               "+anio);
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(4).add("Marca:                                           "+mark);
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(5).add("Modelo:                                          "+model);
			hijos.get(0).add(new ArrayList<String>());
			hijos.get(0).get(6).add("Version:                                          "+submodel);
			
			//Toast.makeText(EstimateActivity.this, "d en CargaDatos:: "+d, Toast.LENGTH_LONG).show();
			
			hijos.add(new ArrayList<ArrayList<String>>());
			try {
				for (int i = 0; i < d2.length(); i++) {
					JSONObject c2 = d2.getJSONObject(i);
					String sLineType = c2.getString("LineType");
					String sDecriptor = c2.getString("CEGDescription");
					//Toast.makeText(EstimateActivity.this, "d2 en CargaDatos CEGDescription: "+sDecriptor, Toast.LENGTH_LONG).show();
					hijos.get(1).add(new ArrayList<String>());
					hijos.get(1).get(i).add("Item "+i+"   -          "+ sDecriptor);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				appendLogE(" class: EstimateActivity.java - PROC: cargarDatos2() - Try del for (int i = 0; i < d2.length(); i++) - linea= 601 - Excepción JSON: "+e.getMessage());
			}
			
//			String sMes = estimateDate.substring(0, 2);		
//			String sDia = estimateDate.substring(3, 5);
//			String sAnio = estimateDate.substring(6, 10);
			
			//String sPath = "/mnt/sdcard/INMAEst_Movil/"+sAnio+"/"+sMes+"/"+sDia+"/"+placa+"/"+estimateId;
			////String sPath = "/mnt/sdcard/INMAEst_Movil/"+sAnio+"/"+sMes+"/"+sDia+"/"+placa+"/"+estimateId.substring(0,6);
			String sPath = sPrimaryStorage + GlobalOcr.rutaFotos;
//			Toast.makeText(EstimateActivity.this, "sPath en CargaDatos2: "+sPath, Toast.LENGTH_LONG).show();
//			Toast.makeText(EstimateActivity.this, "sPath en CargaDatos2: "+sPath, Toast.LENGTH_LONG).show();
			appendLogE(" class: EstimateActivity.java - PROC: cargarDatos2() - sPath = Global.rutaFotos: "+sPath);
			appendLogE(" class: EstimateActivity.java - PROC: cargarDatos2() - sPath = Global.rutaAjusteG: "+Global.rutaAjusteG);		
			Global.estimateId=estimateId;
			String sFilename = "";
			
			hijos.add(new ArrayList<ArrayList<String>>());
			
			File f = new File(sPath);
				if (f.exists()) {
					files=f.listFiles();
				}
				GlobalOcr.cuentafotos=files.length;
				//Toast.makeText(EstimateActivity.this, "Global.cuentafotos en CargaDato2s: "+Global.cuentafotos, Toast.LENGTH_LONG).show();
				for(int i=0; i<files.length; i++){
					File filetemp = files[i];
					sFilename = filetemp.getName();
					hijos.get(2).add(new ArrayList<String>());
					hijos.get(2).get(i).add(sFilename+"   -   "+sPath+"/"+sFilename);
				}
        
    	}
        
    }     
    
	   public void replaceContentView(String id, Intent newIntent) {
		   View view = getLocalActivityManager().startActivity(id,newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) .getDecorView(); this.setContentView(view);
	   }
	   
	    private void mostrarAlertDialogo(){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("¿Desea hacer otro ajuste para el mismo vehículo?, ¿O desea hacer un nuevo ajuste?")
					.setTitle("Advertencia")
					.setCancelable(false)
					.setNegativeButton("Nuevo",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									GlobalOcr.sPlate="";
									GlobalOcr.cuentafotos=0;
									GlobalOcr.ajuste="nuevo";
									Global.sTipoAjuste="nuevo";
									//Global.mismoFolder="";
									//Aqui establecemos la condicion para un Ajuste Nuevo!!!!									
									Intent myIntent = new Intent(EstimateActivity.this, VehiculoActivity.class);
									//myIntent.putExtra("EstimateId",Global.estimateId);
									startActivity(myIntent);
																		
									dialog.cancel();
									activity.finish();
								}
							})
					.setPositiveButton("Mismo Vehículo",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									//Aqui establecemos la condicion para otro Ajuste del mismo vehiculo!!!! (Multiajuste)
									GlobalOcr.ajuste="mismo";
									Global.sTipoAjuste="mismo";
									String sObject = "{'eObject': {'auto':{'Estimator': 'S3037','Odometer': '" + odometer + "','SubModel': '" + submodel + "','anio':'" + anio + "','marca':'" + mark + "','modelo':'" + model + "','placa':'" + placa + "','serialCarroceria':'" + vehicleIdNumber + "'}}}";
									//Intent myIntent = new Intent(EstimateActivity.this, VehiculoActivity.class);
									//Toast.makeText(EstimateActivity.this, ".setPositiveButton(Mismo Vehículo: "+Global.ajuste, Toast.LENGTH_LONG).show();
									Intent myIntent = new Intent(); 
									myIntent.setClassName("ve.com.inma.mobility_4_1.api","ve.com.inma.mobility_4_1.api.Soa");
									GlobalOcr.eObject=sObject;
							    	//myIntent.putExtra("eObject",sObject);
							    	//myIntent.putExtra("ajuste","mismo");
							    	//myIntent.putExtra("EstimateId",Global.estimateId);
							    	//myIntent.putExtra("mismoFolder",Global.mismoFolder);
							    	appendLogE(" class: EstimateActivity.java - PROC: mostrarAlertDialogo() Antes de startActivityForResult 4.1- Global.rutaAjusteG: "+Global.rutaAjusteG);
							    	//myIntent.putExtra("rutaAjuste",Global.rutaAjusteG);
							    	//myIntent.putExtra("rutaFotos",GlobalOcr.rutaFotos); 
							    	startActivityForResult(myIntent, REQUEST_CODE);
									activity.finish();
								}
							});
			
				AlertDialog alert = builder.create();
				alert.show();	    	
	    }
	    
	    private void mostrarProgressDialog(){
//	       ProgressDialog.show(
//	    		   EstimateActivity.this
//	            ,"INMAEst Movil WebService"
//	            ,"SINCRONIZANDO EL AJUSTE....!"
//	            ,true
//	            ,true);
	    	//CreateZipFileFromDirectory();
	       //ProgressDialog.dismiss();
	    }
	    
		   @Override
		   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		      super.onActivityResult(requestCode, resultCode, data);

		      //Toast.makeText(EstimateActivity.this, "DENTRO DE Estimate Activity de vuelta 2da vez onActivityResult: ", Toast.LENGTH_LONG).show();
//		      Toast.makeText(EstimateActivity.this, "DENTRO DE Estimate Activity de vuelta 2da vez onActivityResult: ", Toast.LENGTH_LONG).show();
		      if (requestCode == REQUEST_CODE) {
		         // cogemos el valor devuelto por la otra actividad
		  		//Bundle recieveParams = getIntent().getExtras();
		  		
		  		//getIntent().getExtras().getString("eObject");
//		  		json2 = data.getStringExtra("eObject");
		    	json2 = GlobalOcr.eObject;
//		  		Global.mismoFolder=data.getStringExtra("mismoFolder");
//		  		Global.rutaAjusteG=data.getStringExtra("rutaAjuste");
//		  		GlobalOcr.rutaFotos=data.getStringExtra("rutaFotos");
		  		//Toast.makeText(EstimateActivity.this, "rutaAjuste onActivityResult Global.rutaFotos: "+Global.rutaFotos, Toast.LENGTH_LONG).show();
		  		JSONObject eObject2 = null;
		  		
				//if(recieveParams !=null){
		
				   //json2 = recieveParams.getString("eObject2");
//				   Toast.makeText(EstimateActivity.this, "json de vuelta onActivityResult: "+json2, Toast.LENGTH_LONG).show();
//				   Toast.makeText(EstimateActivity.this, "json de vuelta onActivityResult: "+json2, Toast.LENGTH_LONG).show();
				   //String srutaAjuste= recieveParams.getString("rutaAjuste");
				   //Toast.makeText(EstimateActivity.this, "rutaAjuste onActivityResult: "+srutaAjuste, Toast.LENGTH_LONG).show();

	//******************************************************************************************
				   File logFile = new File("sdcard/logEstimateActivity.file");
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
				         appendLogE(" class: EstimateActivity.java - PROC: onActivityResult() - Try del logFile.createNewFile() - linea= 744 - Excepción: "+e.getMessage());
				      }
				   }
				   try
				   {
				      //BufferedWriter for performance, true to set append to file flag
				      BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true),10 * 1024); 
				      //buf = new char[8192];  8 * 1024     
				      buf.append(json2);
				      buf.newLine();
				      buf.close();
				   }
				   catch (IOException e)
				   {
				      // TODO Auto-generated catch block
				      e.printStackTrace();
				      appendLogE(" class: EstimateActivity.java - PROC: onActivityResult() - Try del BufferedWriter buf  - linea= 755 - IOExcepción: "+e.getMessage());
				   }				   
				   
       //******************************************************************************************				   
				   
						 try {
							eObject2 = new JSONObject(json2);
						 } catch (JSONException e) {
								e.printStackTrace();
								appendLogE(" class: EstimateActivity.java - PROC: onActivityResult() - Try del eObject2 = new JSONObject(json2) - linea= 773 - Excepción JSON: "+e.getMessage());
						 }
						 
						 o2 = eObject2;
						 //d = new eObject();
						 //Toast.makeText(EstimateActivity.this, "o UNO EstimateActivity: "+o, Toast.LENGTH_LONG).show();
						 try {
							o2 = o2.getJSONObject("header");
						} catch (JSONException e) {
							e.printStackTrace();
							appendLogE(" class: EstimateActivity.java - PROC: onActivityResult() - Try del o2 = o2.getJSONObject(header) - linea= 783 - Excepción JSON: "+e.getMessage());
						} 
						
						 try {
							 d2 = eObject2.getJSONArray("detail");
//							 Toast.makeText(EstimateActivity.this, "d2 DOS EstimateActivity: "+d2, Toast.LENGTH_LONG).show();
//							 Toast.makeText(EstimateActivity.this, "d2 DOS EstimateActivity: "+d2, Toast.LENGTH_LONG).show();
//							 Toast.makeText(EstimateActivity.this, "d2 DOS EstimateActivity: "+d2, Toast.LENGTH_LONG).show();
						} catch (JSONException e) {
							e.printStackTrace();
							appendLogE(" class: EstimateActivity.java - PROC: onActivityResult() - Try del d2 = d2.getJSONObject(detail) - linea= 790 - Excepción JSON: "+e.getMessage());
						}		    	  
						
		         cargarDatos2();
		         
					miExpandableAdapter adaptador = new miExpandableAdapter(this, grupos, hijos);
					l.setAdapter(adaptador);
					
		  		//}
		         // enseñamos al usuario el resultado
		         //Toast.makeText(this, "json de vuelta: " + result, Toast.LENGTH_LONG).show();
		         
//		         View v = new View(this);
//		         Intent EstIntent = new Intent(v.getContext(), EstimateActivity.class);
//		         EstIntent.putExtra("eObject",result);
//		         
//		         startActivity(EstIntent);	         
		         //StringBuffer urlString = new StringBuffer();
		         //Activity1 parentActivity = (Activity1)getParent();
		         //replaceContentView("estimate_layout", EstIntent);	 
		         GlobalOcr.svar1="SI";
		         
		         //activity.finish();
		      }
		   }   
    
		   // Procedimiento que llama a la clase que consume el WebService que envia la Estimación
			public void Send_Object_WS_FromDirectory()
			{
				json=json.trim();
				SincronizarEstimacion sincronizar = new SincronizarEstimacion();	// Clase que llama/consume el WebService que envia la Estimación
				sincronizar.execute(json);				
			}

			// Procedimiento que prepara la información del archivo de imagen (foto) para ser subido a la nube (sincronización foto)
			@SuppressWarnings("unchecked")
			public void Send_Fotos_WS_FromDirectory()
			{
				appendLogE(" class: EstimateActivity.java - PROC: Send_Fotos_WS_FromDirectory() - Global.rutaFotos: "+Global.rutaAjusteG);
						
				UploaderFile nuevaTarea = new UploaderFile();	// Clase que llama/consume el WebService que envia las imágenes (fotos) de la Estimación
				nuevaTarea.execute();	            
			}			
			
			/*
			 * Clase asincrona para subir la foto
			 */
			//class UploaderFile extends AsyncTask<Map<String, FotoPackage>, Void, Void>{
			class UploaderFile extends AsyncTask<String, Void, Void>{				
				ProgressDialog pDialog;


				//appendLogE(" class: EstimateActivity.java - CLASE: UploaderFile() Al comienzo, despues de ProgressDialog pDialog ");
							    
			@SuppressWarnings("static-access")
			protected Void doInBackground(String... params) {
				//appendLogE(" class: EstimateActivity.java - PROC: UploaderFile(): ");
					boolean blnConnNet = checkConnectivity();
					
			        String srcDir = GlobalOcr.rutaFotos;
			        
			        String sEstimateId = Global.estimateId;
			        //String sCantFotos = String.valueOf(GlobalOcr.cuentafotos);
			        
			        ////String sId = "";
			        
					String sTempNamefile = ""; 
					String sNombrefile = "";			        
			        
		            // create byte buffer
		            //byte[] buffer = new byte[1023];
		            ////byte[] outbuffer = new byte[1023];

		            File dir = new File(srcDir);			        
	
					appendLogE("class: EstimateActivity.java - CLASE: UploaderFile(image) doInBackground() - Estatus de la Red - Conectada=: "+blnConnNet);
					
					files2 = dir.listFiles();
					
					////String sEstimateId = Global.estimateId;						// Id de Estimación
					////appendLogE(" class: EstimateActivity.java - CLASE: UploaderFile() Despues de  request.addProperty....estimateId: "+sEstimateId);					
					










					if (files2.length>0){
						for (int i = 0; i < files2.length; i++) {
						   
							sTempNamefile = files[i].getName();			// Nombre completo del archivo imagen (foto). Ejemplo: 'foto3.jpg'
	
							String[] tempS = sTempNamefile.split(".");
						















							sNombrefile = sTempNamefile.substring(0,(sTempNamefile.length()-4));
							
	//********************************************* COMIENZO DEL CODIGO NUEVO **************************************************************************	
							File file = new File(srcDir, sTempNamefile);
							
							if(file.exists()) {
								InputStream ios = null;			
								try {




									BufferedInputStream bufferis = null;
	
									try {
										bufferis = new BufferedInputStream(new FileInputStream(file));
	
									} catch (FileNotFoundException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									} 
									
									try {
										int bytes = (int) file.length();
										bytefoto = new byte[bytes]; 
										int readBytes = bufferis.read(bytefoto);	
	//									String encodedString = Base64.encode(bytefoto); 
										
									} catch (FileNotFoundException e) {
										// TODO Auto-generated catch block


										appendLogE(" - file jpg 1er. Catch - FileNotFoundException: "+e.getMessage());
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block


										appendLogE(" -  file jpg 2do. Catch - IOException: "+e.getMessage());
										e.printStackTrace();
									}								
									
								} finally { 
									try {
										 if ( ios != null ) 
											  ios.close();
									} catch ( IOException e) {
									}
								}								











							 }
							 else{


								 appendLogE(" else - File Not Found: "+file);
								 //return;
















































							 }							
							


	//********************************************* FIN DEL CODIGO NUEVO **************************************************************************
							


	//********************************************* COMIENZO DEL CODIGO VIEJO **************************************************************************							
						
	//						byte [] buffer = new byte[(int) files2[i].length()];
	//						InputStream ios = null;
	//						InputStream in = null;
	//						OutputStream out = null;
	//						try {
	//							in = new FileInputStream(files2[i]);
	//							out = new FileOutputStream(files2[i]);							
	//						} catch (FileNotFoundException e1) {
	//							// TODO Auto-generated catch block
	//							e1.printStackTrace();
	//						}
	////
	//						byte[] buf = new byte[(int) files2[i].length()];
	//						int len;
	//						 
	//						try {
	//							while ((len = in.read(buf)) > 0) {
	//							  out.write(buf, 0, len);
	//							}
	//							in.close();
	//							out.close();							
	//						} catch (IOException e1) {
	//							// TODO Auto-generated catch block
	//							e1.printStackTrace();
	//						}
									
	//********************************************* FIN DEL CODIGO VIEJO **************************************************************************						
						
							try { 
								SoapObject request = new SoapObject(NAMESPACE_IMAGE, METHOD_NAME_IMAGE);
								//SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
								appendLogE(" class: EstimateActivity.java - CLASE: UploaderFile() Despues de SoapObject request = new SoapObject(NAMESPACE2....: ");
								//request.addProperty("fotos", fotos);
								String sCantFotos  = String.valueOf(GlobalOcr.cuentafotos);	// Cantidad de fotos a enviar
								
								//int iSupNumber     = 0;									// Suplement Number. El SupNumber es "cero" (0) hasta nuevo aviso
								String sSupNumber     = "0";	
								String sCaption    = sNombrefile;							// Nombre del archivo imagen (foto). Ejemplo: 'foto3'
								sFileName   	   = "C:\\" + sTempNamefile;				// Nombre completo del archivo imagen (foto). Ejemplo: 'foto3.jpg'
								String sFrameNum   = String.valueOf(i+1);					// Consecutivo de cada foto (comienza en uno '1', hasta 'n')
								//base64 = new Base64();									// org.apache.commons.codec.binary.Base64;
								//bytefoto	   	   = base64.encodeBase64(outbuffer);		// Arreglo en bytes[] de la imagen (foto), convertido a Base64.			            
								
								//bytefoto = base64.encodeBase64(bytefoto);
								//sBase64 = base64.encodeBase64String(buf);
								/*Codificamos a base 64*/ 
								//String encodedString = Base64.encode(buf);
								String encodedString = Base64.encode(bytefoto);
								
								request.addProperty("estimateId", sEstimateId);
								request.addProperty("suppN", sSupNumber);
								request.addProperty("vcaption", sCaption);
								request.addProperty("filename", sFileName);
								request.addProperty("imgData", encodedString);
								request.addProperty("frameNum", sFrameNum);
								appendLogE(" class: EstimateActivity.java - CLASE: UploaderFile() Despues de  request.addProperty....vcaption: "+sCaption);
								appendLogE(" class: EstimateActivity.java - CLASE: UploaderFile() Despues de  request.addProperty....filename: "+sFileName);
								appendLogE(" class: EstimateActivity.java - CLASE: UploaderFile() Despues de  request.addProperty....imgData: "+encodedString);
								
								SoapSerializationEnvelope envelopef = new SoapSerializationEnvelope(SoapEnvelope.VER11);
								
								//appendLogE(" class: EstimateActivity.java - CLASE: UploaderFile() Despues de  SoapSerializationEnvelope envelopef....: ");
								//envelope.implicitTypes=true;
								envelopef.dotNet=true;
								envelopef.setOutputSoapObject(request);
	//							appendLogE(" class: EstimateActivity.java - CLASE: UploaderFile() Despues de  envelopef.setOutputSoapObject(request)....: ");
								HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_IMAGE);
								//appendLogE("Antes del Authenticator.setDefault");
	// ** ESTOS **						    Authenticator.setDefault(new ProxyAuthenticator("\\inma\\jazpurua", "Ja2767408"));
	// ** ESTOS **						    Proxy proxys = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.0.254", 8080));
	// ** ESTOS **							    appendLogE("Despues del Authenticator.setDefault y Proxy proxys = new Proxy(");
	// ** ESTOS **								HttpTransportSE androidHttpTransport = new HttpTransportSE(proxys, URL_IMAGE);							
								
								//HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
								appendLogE(" class: EstimateActivity.java - CLASE: UploaderFile() Despues de  HttpTransportSE androidHttpTransport = new ....: ");
								
								try {
									androidHttpTransport.call(SOAP_ACTION_IMAGE, envelopef);
									//androidHttpTransport.call(SOAP_ACTION, envelopef);
									appendLogE(" class: EstimateActivity.java - CLASE: UploaderFile() Despues de  androidHttpTransport.call....: ");
									SoapPrimitive resultado = (SoapPrimitive) envelopef.getResponse();
									//Object result = (Object)envelope.getResponse();
									appendLogE(" class: EstimateActivity.java - CLASE: UploaderFile() - Respuesta: "+resultado.toString());
									String res = resultado.toString();
									int index = res.indexOf("Infracción de la restricción PRIMARY KEY 'PK_Images'."); 
									if(index > 0)

									{
										appendLogE("index: "+String.valueOf(index));
										// No se pudo insertar en la tabla: [Estimates].[dbo].[Images], el registro de foto. Dejarla como PENDIENTE para sincronizar luego

									}
									int index2 = res.indexOf("IE-1000 Cargada la Imagen"); 
									if(index2 > 0)

									{
										appendLogE("index2: "+String.valueOf(index2));
										// Se inserto con exito en la tabla: [Estimates].[dbo].[Images], el registro de foto. 
									}								
								} catch (Exception e) {
									// TODO Auto-generated catch block
									appendLogE(" - Despues de Object result Catch interno - Exception " + " - " + String.valueOf(e));
									e.printStackTrace();
								}								
															
						 //*************************************************************************************
							   try {
								   myDb = SQLiteDatabase.openDatabase(path+"ajustes.sqlite", null, SQLiteDatabase.OPEN_READWRITE);
								}catch (SQLiteException ex) {
									appendLogE(" class: Estimacion.java - PROC:  class SincronizarEstimacion() - SQLiteException al intentar abrir la BD  ajustes.sqlite - path: " + path +" - " +  ex.getMessage());
								} 							
								String squery = "DELETE FROM ajustes WHERE estimateid='"+sEstimateId + "'";
								appendLogE("squery: " + squery);
								//Eliminar un registro
								myDb.execSQL(squery);
						//*************************************************************************************			            
								//return result;
								//Toast.makeText(UploaderFile.this, "** ¡Respuesta! **"+result.toString(), Toast.LENGTH_LONG).show();				
							} catch (Exception e) {


								e.printStackTrace();









								appendLogE(" class: EstimateActivity.java - class UploaderFile - Try del doInBackground() -linea= 1113 - Excepción: "+e.getMessage());
							}
















						
						}	
					}

































					else{
						
						appendLogE("class: EstimateActivity.java - CLASE: UploaderFile(image) doInBackground() - NO SE GUARDARON LAS FOTOS. Longitud de files2.length menor o igual a cero. Ruta de Fotos: "+GlobalOcr.rutaFotos);
					}
		            pDialog.dismiss();	
					return null;

				}
				
				protected void onPreExecute() {
					super.onPreExecute();
					pDialog = new ProgressDialog(EstimateActivity.this);
			        //pDialog.setMessage("Cargando la imagen: "+ sFileName + ", espere." );
			        pDialog.setMessage("Sincronizando el ajuste, por favor espere." );
			        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			        pDialog.setCancelable(true);
			        //pDialog.show();
				}
				@SuppressWarnings("unchecked")
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					pDialog.dismiss();
				}

			}	
			
			/*
			 * Clase asincrona para subir la Estimacion
			 */
			class SincronizarEstimacion extends AsyncTask<String, Void, Void>{

				ProgressDialog pDialog;
				
				protected Void doInBackground(String... params) {

		//			try {				
		//				Thread.sleep(1000);
		//			} catch (InterruptedException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
					
					boolean blnConnNet = checkConnectivity();

					appendLogE("class: EstimateActivity.java - CLASE: SincronizarEstimacion() doInBackground() - Estatus de la Red - Conectada=: "+blnConnNet);
					//Sincronizar
					try { 
						SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

						//appendLog("Despues de SOAP y antes de request ");
						request.addProperty("eObject", json);	            
						//appendLogE("Despues de request " + " - " + json);

						//appendLog("Antes de SoapSerializationEnvelope ");
						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
						envelope.dotNet=true;
						envelope.setOutputSoapObject(request);
						appendLogE("Despues de envelope.setOutputSoapObject y antes de HttpTransportSE "+SOAP_ACTION);
						
						//appendLogE("Antes del Authenticator.setDefault");
//					    Authenticator.setDefault(new ProxyAuthenticator("\\inma\\jazpurua", "Ja2767408"));
//					    Proxy proxys = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.0.254", 8080));
//					    appendLogE("Despues del Authenticator.setDefault y Proxy proxys = new Proxy(");
//						HttpTransportSE androidHttpTransport = new HttpTransportSE(proxys, URL);
						
						HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
						appendLogE("Despues de HttpTransportSE y antes de androidHttpTransport.call( - URL: " + URL);
						try {							
							androidHttpTransport.call(SOAP_ACTION, envelope);
							appendLogE(" - Despues de androidHttpTransport.call y antes de Object result: "+json);
							SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
							appendLogE("result: "+ result.toString());
							//Object result = (Object)envelope.getResponse();
							appendLogE(" - Despues de Object result: " + " - " + String.valueOf(result));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							appendLogE(" - Despues de Object result Catch interno - Exception " + " - " + String.valueOf(e));
							e.printStackTrace();
						}	
						
						
					} catch (Exception e) {
						appendLogE(" - Despues de Object result Catch afuera - Exception " + " - " + String.valueOf(e));
						e.printStackTrace();
					}
			
					return null;
				}
				
				protected void onPreExecute() {
					super.onPreExecute();
					pDialog = new ProgressDialog(EstimateActivity.this);
			        //pDialog.setMessage("Sincronizando la Estimación: " + estima + ", espere por favor.." );
			        pDialog.setMessage("Sincronizando la Estimación, espere por favor.." );
			        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			        pDialog.setCancelable(true);
			        pDialog.show();
				}
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					pDialog.dismiss();
				}
			}	
			
			private boolean checkConnectivity()
			{
				boolean enabled = true;
		 
				ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = connectivityManager.getActiveNetworkInfo();
				 
				if ((info == null || !info.isConnected() || !info.isAvailable()))
				{
					enabled = false;
				}
				return enabled;         
			}	
			
		 	public void appendLogE(String text)
		 	{       
		 		TomaFechaSistema();
		 		//Toast.makeText(this, "appendLogF - TomaFechaSistema: "+fechaLogD, Toast.LENGTH_LONG).show();
			    versionLog = "" + fechaLogD;
		 	   ////File logFile = new File("sdcard/INMAEst_Movil/logINMAEstFotos"+versionLog+".file");
			   File logFile = new File(sPrimaryStorage+"/INMAEst_Movil/logINMAEstEstimateAct"+versionLog+".file");
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
		        toast.setDuration(Toast.LENGTH_SHORT);
		        toast.setView(layout);
		        Toast.makeText(this, text, Toast.LENGTH_LONG);
		        toast.show();
		     }	  		    
			
}
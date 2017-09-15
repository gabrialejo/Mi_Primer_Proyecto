package com.datumdroid.android.ocr.simple;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import ve.com.inma.mobility_4_1.api.R;
//import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import org.kobjects.base64.Base64;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datumdroid.android.ocr.simple.FotoPackage;
 
/**
 * Estimacion: Clase que muestra la lista de ajustes hechos guardados en la BD: "ajustes.sqlite"
 * Llama a la clase: EstimacionesCursorAdapter para conformar el cursor de la lista. 
 * Llama a la clase interna: "SincronizarEstimacion()" para enviar ajustes al Web Service de la nube
 * @author José A. Azpurua - 2014 - Modificación: 16 Dic. 2014 - José A. Azpurua
 * Modificación para versión de Guatemala Celular: 19 Nov. 2015 - José A. Azpurua
 * Última modificación para versión de Guatemala Celular: 25 Julio, 2016 - José A. Azpurua
 * 
 */
public class Estimacion extends ListActivity {
 
   //private EstimacionesDbAdapter dbAdapter;
    private Cursor cursor;
    //private EstimacionesCursorAdapter estimacAdapter ;
    private ListView lista;
    private SQLiteDatabase myDb = null; // Database object
    //private TextView textv = null;
    private Button b1 = null;
    //private Button b2 = null;
    //private TextView tv = null;
    private String sOrigen = "";
    private int lencursor = 0;
    private int canfotos = GlobalOcr.cuentafotos;
	private int ifoto = 0;
	private String data="";
	private static byte[] bytefoto;
    
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
  
    /**
     * Definimos constante con el nombre de la tabla
     */
    public static final String C_TABLA = "ajustes" ;
    //public SQLiteDatabase myDbEstimates = null; 		// Database object for dealing with Estimates.sqlite Database
    public String path = "/mnt/sdcard/databasedata/";	//Environment.getDataDirectory() + "/data/com.totsp.database/databases/";
    /**
     * Definimos constantes con el nombre de las columnas de la tabla
     */
    public static final String C_COLUMNA_ID   = "_id";
    public static final String C_COLUMNA_ESTIMATEID  = "estimateId";
    public static final String C_COLUMNA_JSON = "json";
//    public static final String C_COLUMNA_FECHA = "Fecha";
//    public static final String C_COLUMNA_PLACA = "Placa";
//    public static final String C_COLUMNA_PERITO = "Perito";
    public static final String C_COLUMNA_ESTATUS = "status";
    private String[] columnas = new String[]{C_COLUMNA_ESTIMATEID, C_COLUMNA_JSON, C_COLUMNA_ESTATUS} ;
    //private String[] columnas = new String[]{C_COLUMNA_ESTIMATEIDF, C_COLUMNA_FECHA, C_COLUMNA_PLACA, C_COLUMNA_PERITO,C_COLUMNA_ESTATUS} ;
	private ProgressDialog pDialog1 = null;
    List<Row> rows;
    
    private String estima = "";
    private String json = "";
    
	public String versionLog = "";
	public String fechaLogD = "";
	public String fechaHoraLogD = "";
	private String sPrimaryStorage = "/mnt/sdcard/";
	private String sSecondaryStorage = "/mnt/sdcard/";
	private String sjson = "";
	
	private int exe1 = 0;
	private int exe2 = 0;
	
	private FotoPackage fp = null;
	String sFileName = "";
    
    private LayoutInflater layoutInflater;
    
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.listview);
      
	  Bundle recieveParams = getIntent().getExtras();
	  if(recieveParams !=null){
		  sOrigen = recieveParams.getString("origen");
	  }
	  appendLog(" class: Estimacion.java - PROC: onCreate() - Llegue a Estimacion sOrigen- " + sOrigen);
      b1 = (Button) findViewById(R.id.button1);
      GlobalOcr.b2 = (Button) findViewById(R.id.button2);
	  //tv  = (TextView) findViewById(R.id.textView1); 
      //lista = (ListView) findViewById(R.id.list);
		
	  b1.setOnClickListener(new OnClickListener()
	  {
	
		  public void onClick(View v)
		  {	
			Intent myIntent =null; 
			myDb.close();
			//mostrarToast("sOrigen : "+sOrigen);
			if(sOrigen.equals("DataEntry")){
				// Esta clase fue llamada desde: DataEntry.java (entrada de datos principal). Retorna hacia ella.
				 appendLog(" class: Estimacion.java - PROC: onCreate() - clase fue llamada desde: DataEntry.java (entrada de datos principal). Retorna hacia DataEntry");
				myIntent = new Intent(Estimacion.this, DataEntry.class);
			}else{
				// Esta clase fue llamada desde: VehiculoActivity.java (Pantalla principal). Retorna hacia ella.
				appendLog(" class: Estimacion.java - PROC: onCreate() - clase fue llamada desde: VehiculoActivity.java (Pantalla principal). Retorna hacia VehiculoActivity");
				myIntent = new Intent(Estimacion.this, VehiculoActivity.class);				
			}		
			appendLog(fechaLogD +  " class: Estimación.java - PROC: onCreate() - b1.setOnClickListener()- Salida con Intent hacia origen: "+sOrigen);

			startActivity(myIntent);
			appendLog(fechaLogD +  " class: Estimación.java - PROC: onCreate() - b1.setOnClickListener()- Ejecuto startActivity(myIntent) hacia Activity: " + sOrigen + ". Fin de Activity Estimacion.java");
			finish();
		  }
	  });
	  
	  GlobalOcr.b2.setOnClickListener(new OnClickListener()
	  {
		  public void onClick(View v)
		  {
			  RemoveNullValue();
			  //String sEstimaId = "";
			  String slen = String.valueOf(GlobalOcr.CheckEstimates.length);
			  int len = GlobalOcr.CheckEstimates.length;
			  //mostrarToast("slen : "+slen);
			  if(len<1){
				  mostrarToast("Debe seleccionar al menos un (1) Ajuste a sincronizar");
				  appendLog(fechaLogD +  " class: Estimación.java - PROC: onCreate() - b2.setOnClickListener()- El Usuario hizo click en b2 para Sincronizar, pero no selecciono ningún registro de Ajuste");
				  return;
			  }
			  
			  int k = 0;
			  Integer index = 0;
			  for(int i=0;i<=(GlobalOcr.CheckEstimates.length - 1);i++)
			  {
				  if(GlobalOcr.CheckEstimates[i] != null)
				  {
					  index = Integer.parseInt(GlobalOcr.CheckEstimates[i]);
					  index = i+1;
					  appendLog("GlobalOcr.CheckEstimates.length: "+GlobalOcr.CheckEstimates.length);
					  appendLog("i: "+i+" - CheckEstimates[i]: "+GlobalOcr.CheckEstimates[i]);
					  appendLog("i: "+i+" - GlobalOcr.Jsons[i]: "+GlobalOcr.Jsons[i]);
					  appendLog("index: "+index+" - GlobalOcr.Jsons[index]: "+GlobalOcr.Jsons[index]);
					  appendLog("i: "+i+" - GlobalOcr.Estimates[i]: "+GlobalOcr.Estimates[i]);
					  appendLog("index: "+index+" - GlobalOcr.Estimates[index]: "+GlobalOcr.Estimates[index]);
					  appendLog("doInBackground i: "+i+" - GlobalOcr.RutaAjusteG: "+GlobalOcr.RutaAjusteG[index]);
						
					  sjson = GlobalOcr.Jsons[index];

					  estima = GlobalOcr.Estimates[index];
					  estima = estima.trim();
					  
					  //mostrarToast("Antes de  SincronizarEstimacion");
					  boolean blnConnNet = checkConnectivity();
					  
					  if(!blnConnNet){
						  mostrarToast("*** Actualmente NO hay conectividad para sincronizar. Revise e intente cuando se reestablezca ***");
						  return;
					  }
					// Declara una instancia de la clase SincronizarEstimacion() para enviar el ajuste al Web Service de la nube
					//mostrarToast("Antes de  SincronizarEstimacion");

					SincronizarEstimacion nuevaTarea = new SincronizarEstimacion();
			  		nuevaTarea.execute("Procesar");
//					try {				
//						Thread.sleep(1000000000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}				  		
			  		//appendLog(" class: Estimacion.java - PROC: onCreate() - Regreso de la Asyncrona: SincronizarEstimacion");
//					 mostrarToast("** Termine con las imagenes **");
//					Intent myIntent =null; 
//					myDb.close();
//					//mostrarToast("sOrigen : "+sOrigen);
//					if(sOrigen.equals("DataEntry")){
//						// Esta clase fue llamada desde: DataEntry.java (entrada de datos principal). Retorna hacia ella.
//						 appendLog(" class: Estimacion.java - PROC: onCreate() - clase fue llamada desde: DataEntry.java (entrada de datos principal). Retorna hacia DataEntry");
//						myIntent = new Intent(Estimacion.this, DataEntry.class);
//					}else{
//						// Esta clase fue llamada desde: VehiculoActivity.java (Pantalla principal). Retorna hacia ella.
//						appendLog(" class: Estimacion.java - PROC: onCreate() - clase fue llamada desde: VehiculoActivity.java (Pantalla principal). Retorna hacia VehiculoActivity");
//						myIntent = new Intent(Estimacion.this, VehiculoActivity.class);				
//					}	
//					startActivity(myIntent);
//					finish();
//			  		rows2 = new ArrayList<Row>(lencursor);
//			    	setListAdapter(new CustomArrayAdapter(this, rows));
			  
				  }// End if
					 
			  }		// End For				  
		
		  }
		  
	  });
	  
	  openDatabaseEstimates();
	  // llamada al procedimiento "consultar()", el cual llama al proc. que abre la BD: "ajustes.sqlite", y a su vez instancia la clase: "EstimacionesCursorAdapter" 
	  consultar();
	  
   }

   private void RemoveNullValue() {

	   List<String> list = new ArrayList<String>();

	   for(String s : GlobalOcr.CheckEstimates) {
		  if(s != null && s.length() > 0) {
			 list.add(s);
		  }
	   }

	   GlobalOcr.CheckEstimates = list.toArray(new String[list.size()]);

   }
   
   // Procedimiento que llama a su vez al respectivo que abre la BD: "ajustes.sqlite" e instancia a EstimacionesCursorAdapter, para implementar el cursor
   private void consultar()
   {
	   lencursor = cursor.getCount();	// Número de registros en el cursor para la lista
	   GlobalOcr.b2.setEnabled(false);
	   if(lencursor==1){
		   //finish();
		   
		   mostrarToast("** No existen Ajustes pendientes por Sincronizar **");
	   }else{
		   
		//consultar();	
	   //mostrarToast("lencursor : "+lencursor);
	   rows = new ArrayList<Row>(lencursor);
		GlobalOcr.Estimates = new String[lencursor];
		GlobalOcr.Jsons = new String[lencursor];
		GlobalOcr.CheckEstimates = new String[lencursor];
		GlobalOcr.RutaAjusteG = new String[lencursor];
		
		Row row = null;
		int i = 1;
		String sEspacios = "";
		String sEstimateId = "";
		String sFecha   = "";
		String sHora   = "";
		String sPlaca   = "";			
		String sMarca = "";
		String sModelo = "";
		String sAnio = "";
		String sPerito = "";
		String sStatus = "";
		String json = "";
		//int iMark = 0;
	      JSONObject json2 = null;
	      JSONObject o = null;		
		try {
       	    cursor.moveToFirst();
       	    cursor.moveToNext();
			//mostrarToast("json2 : "+cursor.getString(1));
			json2 = new JSONObject(cursor.getString(1));
			GlobalOcr.RutaAjusteG[i] = cursor.getString(4);
			GlobalOcr.rutaFotos = cursor.getString(4);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//mostrarToast("JSONException e : "+ e);
			appendLog(fechaLogD +  " class: Estimacion.java - PROC: onCreate() - JSONException- " + e);
		}
		// Recorre el cursor para conformar el "ArrayList<Row>" con los atributos de cada registro del cursor
//	    	if (cursor.moveToFirst()) {
	           do {
	        	   //mostrarToast("cursor i : "+ i + " - " + cursor.getString(1));
	        	    row = new Row();
	        		try {
	        			json2 = new JSONObject(cursor.getString(1));
	        			json = cursor.getString(1);
//	        			if(i <= 2){
	        				//mostrarToast("json: "+ json);
//	        			}
	        			//json=json.trim();
	        			//GlobalOcr.Jsons[i] = String.valueOf(json);
	        			
	        			// Toma valores del objeto JSONObject: "o" y conforma cada linea de la lista
						o = json2.getJSONObject("header");
						int iMark = o.getString("Make").length();
						
						sEstimateId = cursor.getString(0);
						
						//mostrarToast("sEstimateId : "+sEstimateId);
		    			if(sEstimateId==null || sEstimateId=="null"  || sEstimateId.isEmpty() || sEstimateId.length()<18  || sEstimateId == ""){
		    				sEstimateId = "SIN ESTIMATE ID";
		    			}else{
		    				//sEstimateId = o.getString("EstimateId").trim();
		    				sHora = sEstimateId.substring(6,8)+":"+sEstimateId.substring(8,10)+":"+sEstimateId.substring(10,12);
		    				if(sEstimateId.length()<20){
		    					sEspacios = space(20-(sEstimateId.length()));
		    					sEstimateId = sEstimateId + sEspacios;
		    				}
		    				//else{

	//	    				}
		    			}	
		    			GlobalOcr.Estimates[i] = sEstimateId;
//						sFecha   = cursor.getString(3);
		    			if(o.getString("EstimateDate")==null || o.getString("EstimateDate")=="null"  || o.getString("EstimateDate").isEmpty() || o.getString("EstimateDate").length()<8  || o.getString("EstimateDate") == ""){
		    				GlobalOcr.sFecha = "NO FECHA";
		    			}else{
		    				sFecha = o.getString("EstimateDate").trim();
	//	    				if(sMarca.length()<20){
	//	    					sEspacios = space(44-(iMark));
	//	    					GlobalOcr.sMarca = sMarca + sEspacios;
	//	    				}else{
		    					GlobalOcr.sFecha = sFecha;
	//	    				}
		    			}
		    			//mostrarToast("sFecha : "+sFecha);
		    			if(o.getString("Plate")==null || o.getString("Plate")=="null"  || o.getString("Plate").isEmpty() || o.getString("Plate").length()<5  || o.getString("Plate") == ""){
		    				GlobalOcr.sPlaca = "NOPLACA";
		    			}else{
		    				sPlaca = o.getString("Plate").trim();
	//	    				if(sMarca.length()<20){
	//	    					sEspacios = space(44-(iMark));
	//	    					GlobalOcr.sMarca = sMarca + sEspacios;
	//	    				}else{
		    					GlobalOcr.sPlaca = sPlaca;
	//	    				}
		    			}						
		    			//mostrarToast("sPlaca : "+sPlaca);
//						sPlaca   = cursor.getString(4);								
//						if(sPlaca==null || sPlaca=="null"  || sPlaca.isEmpty() || sPlaca.length()<2  || sPlaca == ""){
//							sPlaca = "SIN PLACA ";
//						}else{
//							if(sPlaca.length()<10){
//								sEspacios = space(10-(sPlaca.length() + 1));
//								sPlaca = " " + sPlaca + sEspacios;
//							}
//						}
		    			if(o.getString("Estimator")==null || o.getString("Estimator")=="null"  || o.getString("Estimator").isEmpty() || o.getString("Estimator").length()<5  || o.getString("Estimator") == ""){
		    				sPerito = "PERITO";
		    			}else{
		    				sPerito = o.getString("Estimator").trim();
	//	    				if(sMarca.length()<20){
	//	    					sEspacios = space(44-(iMark));
	//	    					GlobalOcr.sMarca = sMarca + sEspacios;
	//	    				}else{
//		    					GlobalOcr.sPlaca = sPlaca;
	//	    				}
		    			}
		    			//mostrarToast("sPerito : "+sPerito);
		    			sStatus = cursor.getString(2);
		    			
	    			if(o.getString("Make")==null || o.getString("Make")=="null"  || o.getString("Make").isEmpty() || o.getString("Make").length()<2  || o.getString("Make") == ""){
	    				GlobalOcr.sMarca = "NO SE REGISTRO MARCA";
	    			}else{
	    				sMarca = o.getString("Make").trim();
	    				if(sMarca.length()<15){
	    					sEspacios = space(15-(sMarca.length()));
	    					GlobalOcr.sMarca = sMarca + sEspacios;
	    				}else{
	    					GlobalOcr.sMarca = sMarca;
	    				}
	    			}
	    			//mostrarToast("sMarca : "+sMarca);
	    			if(o.getString("Model")==null || o.getString("Model")=="null"  || o.getString("Model").isEmpty() || o.getString("Model").length()<2  || o.getString("Model") == ""){
	    				GlobalOcr.sModelo = "    SIN MODELO      ";
	    			}else{
	    				sModelo = o.getString("Model").trim();
	    				//GlobalOcr.sModelo = sModelo;
	    				//mostrarToast("sModelo Len : "+sModelo.length());
	    				if(sModelo.length()<10){
	    					sEspacios = space(19-sModelo.length());
	    					//mostrarToast("sEspacios Len : "+sEspacios.length());
	    					GlobalOcr.sModelo = sModelo + sEspacios;
	    					//mostrarToast("GlobalOcr.sModelo Len : "+GlobalOcr.sModelo.length());
	    				}
	    				if(sModelo.length()>20){
							int iExtra = (20-sModelo.length());
							GlobalOcr.sModelo = sModelo.substring(0,20);
	    				}else{
	    					GlobalOcr.sModelo = sModelo;
	    				}
	    			}
	    			//mostrarToast("sModelo : "+sModelo);
	    			if(o.getString("Year")==null || o.getString("Year")=="null"  || o.getString("Year").isEmpty() || o.getString("Year").length()<4  || o.getString("Year") == ""){
	    				GlobalOcr.sAño = "9999";
	    			}else{
	    				sAnio = o.getString("Year").trim();
    					GlobalOcr.sAño = sAnio;
	    			}	        	
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
//					row.setTitle(i + " - Placa: " + " - " +cursor.getString(4) + " - Fecha: " + cursor.getString(3) + " - Marca: " + GlobalOcr.sMarca + " - Modelo: " + GlobalOcr.sModelo);
//					row.setSubtitle("       Perito: " + cursor.getString(5) + " - Estatus: " + cursor.getString(6));
					String sI = String.valueOf(i);
					if(i<10){
						sI="0"+i;
					}
					String sTitle = " " + sI + " - Marca: " + GlobalOcr.sMarca + " - Modelo: " + GlobalOcr.sModelo + " - Año: " + GlobalOcr.sAño + " - Placa: " + GlobalOcr.sPlaca;
					int ilenTitle = sTitle.length();
					//mostrarToast("ilenTitle : "+ ilenTitle);
					if(ilenTitle < 110){
						sEspacios = space(110-ilenTitle);
						sTitle = sTitle + sEspacios;
						//mostrarToast("ilenTitle Mayor a 360 : "+ ilenTitle);
					}
					String sSubTitle = "             Fecha: " + GlobalOcr.sFecha + " - Hora: " + sHora + "- Perito: " + sPerito + " - Estatus: " + sStatus;
					int ilenSubTitle = sSubTitle.length();

//					mostrarToast("sEstimateId : "+sEstimateId);
//					mostrarToast("sTitle : "+sTitle);
//					mostrarToast("sSubTitle : "+sSubTitle);
					row.setTitle(sTitle);
					row.setSubtitle(sSubTitle);		
					row.setEstimateId(sEstimateId);
					row.setJson(String.valueOf(json));
					rows.add(row);
					
					i++;
					if(i == lencursor){
						break;						
					}
	           } while (cursor.moveToNext());
//	        }
//	        if (cursor != null && !cursor.isClosed()) {
//	           cursor.close();
//	        }
		 // Una vez conformado el ArrayList<Row>: "rows", se pasa al adaptador: "CustomArrayAdapter" para implementarlo.
	    	if(sEstimateId!="Vacio"){    		
	    		setListAdapter(new CustomArrayAdapter(this, rows));
	    	}
	    	
	   }	 	// Fin del else{    	
	  
   }
   
   private void ejecutarcursor()
   {  
			String query = "Select * FROM ajustes ORDER BY fechaAjuste DESC;";
			appendLog(" class: Estimacion.java - PROC: ejecutarcursor() - Se establece el Query de Consulta para el cursor: " + query);
  		try {
  			
	      	 cursor = myDb.rawQuery(query, null);
	      	 
	  	     cursor.moveToFirst();
	//      	    cursor.moveToNext();
//	  	     String sData = cursor.getString(2);
//	
//			data = cursor.getString(0)+" - "+cursor.getString(1)+" - "+cursor.getString(2) +" - "+cursor.getString(3)+" - "+cursor.getString(4)+" - "+cursor.getString(5);
//			appendLog(" class: Estimacion.java - PROC: ejecutarcursor() - cursor.isFirst()" + data);

  	    }catch (CursorIndexOutOfBoundsException ex) {
  	    	data = "";
  	    	appendLog(fechaLogD +  " class: Estimacion.java - PROC:  ejecutarcursor() - CursorIndexOutOfBoundsException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
  	    	////cursor.close();
  	    	Toast.makeText(this, "ERROR: "+String.valueOf(ex), Toast.LENGTH_LONG).show();			
  	    }catch (SQLiteException exs) {
  	    	appendLog(fechaLogD +  " class: Estimacion.java - PROC:  ejecutarcursor() - SQLiteException al intentar llenar el cursor: c - : " + query +" - " +  exs.getMessage());
  	    	Toast.makeText(this, "ERROR: "+String.valueOf(exs), Toast.LENGTH_LONG).show();			
  	    	data = "";
  		}	
  		myDb.close();
   }
 
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.estimacion, menu);
      return true;
   }
   
//   @Override
//     public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//        case R.id.about:
//           Toast.makeText(
//           		Estimacion.this
//                ,"Ejemplo Menús App"
//                ,Toast.LENGTH_LONG)
//                .show();
//           return true;
//   
//        case R.id.quit:
//           finish();
//           return true;
//   
//        default:
//           return super.onOptionsItemSelected(item);
//        }
//     } 
   
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
					  finish();
				  }
			  })
			  .show();
				return true;
			}
	    return super.onKeyDown(keyCode, event);
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

   // Procedimiento que abre la BD: "ajustes.sqlite" en la ruta: "path", para lectura y escritura. Ejecuta query basico de todos sus registros.
   // Define Cursor: "cursor". Establece a la variable string "data" con el "recorset" obtenido
   public void openDatabaseEstimates(){
    	if (myDb != null) {
   			myDb.close();
   		}
    	//cursor = null;
          try {
       	   myDb = SQLiteDatabase.openDatabase(path+"ajustes.sqlite", null, SQLiteDatabase.OPEN_READWRITE);
       	   appendLog(" class: Estimacion.java - PROC: openDatabaseEstimates() - La Base: " + path+"ajustes.sqlite. Se abrio exitosamente.");
 	
  			String query = "Select * FROM ajustes ORDER BY fechaAjuste DESC;";
  			appendLog(" class: Estimacion.java - PROC: openDatabaseEstimates() - Se establece el Query de Consulta para el cursor: " + query);
       		try {
       			
           	 cursor = myDb.rawQuery(query, null);
           	 int conta = cursor.getCount();
			 cursor.moveToLast();
           	 if(conta==1){
	       		Toast.makeText(this, "*** No hay ajustes pendientes para mostrar ***", Toast.LENGTH_LONG);
	       		return;           		 
           	 }

       	    }catch (CursorIndexOutOfBoundsException ex) {
       	    	//data = "";
       	    	appendLog(fechaLogD +  " class: Estimacion.java - PROC:  openDatabaseEstimates() - CursorIndexOutOfBoundsException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
       	    	////cursor.close();
       	    	Toast.makeText(this, "ERROR: "+String.valueOf(ex), Toast.LENGTH_LONG).show();			
       	    }catch (SQLiteException exs) {
       	    	appendLog(fechaLogD +  " class: Estimacion.java - PROC:  openDatabaseEstimates() - SQLiteException al intentar llenar el cursor: c - : " + query +" - " +  exs.getMessage());
       	    	Toast.makeText(this, "ERROR: "+String.valueOf(exs), Toast.LENGTH_LONG).show();			
       	    	//data = "";
       		}finally{
       	    	String sIdEst = cursor.getString(2);
				  if(sIdEst.equals("FIRST")){
					mostrarToast("** No existen Ajustes pendientes por Sincronizar **");
					//Toast.makeText(context, "*** NO EXISTEN AJUSTES PENDIENTES PARA SINCRONIZAR ***", Toast.LENGTH_LONG).show();
					Intent myIntent =null; 
					myDb.close();
					//mostrarToast("sOrigen : "+sOrigen);
					if(sOrigen.equals("DataEntry")){
						// Esta clase fue llamada desde: DataEntry.java (entrada de datos principal). Retorna hacia ella.
						myIntent = new Intent(Estimacion.this, DataEntry.class);
					}else{
						// Esta clase fue llamada desde: VehiculoActivity.java (Pantalla principal). Retorna hacia ella.
						myIntent = new Intent(Estimacion.this, VehiculoActivity.class);
					}
					
					startActivity(myIntent);
					finish();					
					  return;
				  }
       			//cursor.close();
       			//myDb.close();
       		}       	   
          } catch (SQLiteException sqle) {
          	//appendLog(fechaLogD +  " class: DroidGap.java - PROC: openDatabaseEstimates() - No se pudo abrir o no existe la Base de Datos: estimates.sqlite - " + sqle);
          	//throw sqle;
          }       		
      }     
   
	public String space(int x){
	
		String spaces=" ";
		int numberOfSpaces=x;
	
		for(int i=0;i<numberOfSpaces;i++){
		spaces= spaces + " ";
		}
	
		return spaces;
	} 
	
	/*
	 * Clase asincrona para subir la Estimacion/Ajuste a la nube
	 */
	class SincronizarEstimacion extends AsyncTask<String, Void, Void>{

		ProgressDialog eDialog;
		
		protected Void doInBackground(String... params) {
			appendLog("Dentro de Clase: SincronizarEstimacion - LLEGADA ");		
			appendLog("Dentro de Clase: SincronizarEstimacion -  GlobalOcr.CheckEstimates.length: " + " - " + GlobalOcr.CheckEstimates.length);
			//mostrarToast("Dentro de Clase: SincronizarEstimacion -  GlobalOcr.CheckEstimates.length: " + " - " + GlobalOcr.CheckEstimates.length);
			//Context context2 = this.getContext();
		   
//		   Toast.makeText(this, "Clase: SincronizarEstimacion GlobalOcr.CheckEstimates.length: " + " - " + GlobalOcr.CheckEstimates.length, Toast.LENGTH_LONG).show();
//		   toast.show();			
			
//			  int k = 0;
//			  Integer index = 0;
//			  for(int i=0;i<=(GlobalOcr.CheckEstimates.length - 1);i++)
//			  {
//				  if(GlobalOcr.CheckEstimates[i] != null)
//				  {
//					  index = Integer.parseInt(GlobalOcr.CheckEstimates[i]);
//					  appendLog("GlobalOcr.CheckEstimates.length: "+GlobalOcr.CheckEstimates.length);
//					  appendLog("i: "+i+" - CheckEstimates[i]: "+GlobalOcr.CheckEstimates[i]);
//					  appendLog("i: "+i+" - GlobalOcr.Jsons[i]: "+GlobalOcr.Jsons[i]);
//					  appendLog("index: "+index+" - GlobalOcr.Jsons[index]: "+GlobalOcr.Jsons[index]);
//					  appendLog("i: "+i+" - GlobalOcr.Estimates[i]: "+GlobalOcr.Estimates[i]);
//					  appendLog("index: "+index+" - GlobalOcr.Estimates[index]: "+GlobalOcr.Estimates[index]);
//					  appendLog("doInBackground i: "+i+" - GlobalOcr.RutaAjusteG: "+GlobalOcr.RutaAjusteG[index]);
//	
//						String sjson = GlobalOcr.Jsons[index];
//						//json=json.trim();
//						 //json = "{'header':{'Service':'4972','OEM':'4','PartIdManual':true,'SName':'','UName':'','PName':'','EstimateId':'140606101244aa808ab','EstimateDate':'08/03/2012','Estimator':'S6030','Message03':'355143040926353','MFR':'1','Make':'Chrysler','Year':'1998','Model':'Neon','Version':'LX - 4Pt, Sedan - 4Cil., *i - Automatic','Color':'','Doors':0,'Odometer':6700,'Plate':'aa808ab','VehicleIdType':'C','VehicleIdNumber':'8Y3HS36C5W1800388','CommittedStatus':1,'Type':1,'VehicleId':0,'UserMessages':'','SentTo':'355143040926353','TotalLoss':0},'detail':[{'LineType':'L','OverridenRepairTime':-1,'suppnum':0,'HoursOverride':-1,'LabOpOverride':-1,'LabCatOverride':-1,'DescriptionOverride':'','PartNumberOverride':'','PartTypeOverride':-1,'PriceOverride':-1,'CEGLabor':0.17,'CEGPrice':0,'CEGDataIndex':200050210,'Explanation':'','TaggedPart':0,'CEGLabOp':1,'CEGLabCat':0,'CEGDescription':'TECHO [1996-1996, TECHO CORREDIZO COUPE] ','CEGPartNumber':'4888917AC','CEGPartType':0,'PartId':0,'ARTCHours':0,'PriceOverride2':-1,'bException':false,'autoIncl':0,'Auto':0},{'LineType':'L','OverridenRepairTime':-1,'suppnum':0,'HoursOverride':-1,'LabOpOverride':-1,'LabCatOverride':-1,'DescriptionOverride':'','PartNumberOverride':'','PartTypeOverride':-1,'PriceOverride':-1,'CEGLabor':2.4,'CEGPrice':0,'CEGDataIndex':200050030,'Explanation':'','TaggedPart':0,'CEGLabOp':6,'CEGLabCat':6,'CEGDescription':'PINTURA PANEL DEL TECHO [1995-1999] ','CEGPartNumber':'','CEGPartType':1,'PartId':0,'ARTCHours':0,'PriceOverride2':-1,'bException':false,'autoIncl':-1,'Auto':1}]}";
//						estima = GlobalOcr.Estimates[index];
//						estima = estima.trim();
						//appendLog("Antes de SOAP " + " - " + estima);
						//Sincronizar la Estimacion
						try { 
							SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
							//request.addProperty("EstimateId", estima);
							//appendLog("Despues de SOAP y antes de request ");
							request.addProperty("eObject", sjson);	            
							//appendLog("Despues de request " + " - " + sjson);
	
							//appendLog("Antes de SoapSerializationEnvelope ");
							SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
							envelope.dotNet=true;
							envelope.setOutputSoapObject(request);
							//appendLog("Despues de envelope.setOutputSoapObject y antes de HttpTransportSE "+SOAP_ACTION);
							HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
							//appendLog("Despues de HttpTransportSE y antes de androidHttpTransport - URL: " + URL);
							try {							
								androidHttpTransport.call(SOAP_ACTION, envelope);
								//appendLog("Despues de androidHttpTransport.call y antes de Object result: "+"i:"+i+" - "+sjson);
								SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
								String res = result.toString();
								//Object result = (Object)envelope.getResponse();
								appendLog("Dentro del Try de androidHttpTransport.call - Despues de Object result " + " - " + String.valueOf(result));
								appendLog("Dentro del Try de androidHttpTransport.call - estima: " + estima);
								
								if(res.indexOf(":null")>0){
									exe1 = 1;
								}
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								appendLog("Try del androidHttpTransport.call - Despues de Object result Catch interno Exception " + " - " + String.valueOf(e));
								e.printStackTrace();
							}	
							
							
						} catch (Exception e) {
							appendLog("Try del SoapObject request - Despues de Object result Catch Exception " + " - " + String.valueOf(e));
							e.printStackTrace();
						}
						
//					  }// End if
//				 
//			  }		// End For	
			
						
			//********************************************************************************
			//*******************  Proceso de envio de las Fotos    **************************
			//********************************************************************************
						
						String srcDir = GlobalOcr.rutaFotos;
				        String sId = "";
				        
				        try {
				        			
				            // create byte buffer
				            byte[] buffer = new byte[1023];
				            byte[] outbuffer = new byte[1023];

				            File dir = new File(srcDir);

				            File[] files = dir.listFiles();
							//Global.estimateId;
							String sTempNamefile = ""; 
							String sNombrefile = "";
							//, sExtensionfile = "";

				            for (int i = 0; i < files.length; i++) {
				                //Toast.makeText(this, "Adding file: "+files[i].getName(), Toast.LENGTH_LONG).show();
				                //FileInputStream fis = new FileInputStream(files[i]);
				                
				            	// Instanciamos la clase: "FotoPackage()"
				                fp = new FotoPackage();
				                
				                sId = String.valueOf(i+1);
				                
				                //fp.setEstimateId(Global.estimateId);		// Id de Estimación
								fp.setEstimateId(estima);					// Id de Estimación
				                fp.setSupNumber("0");						// Suplement Number. El SupNumber es "cero" (0) hasta nuevo aviso
				                fp.setConsecutivo(i+1); 					// Numero consecutivo de cada foto tomada
								sTempNamefile = files[i].getName();			// Nombre completo del archivo imagen (foto). Ejemplo: 'foto3.jpg'

								sNombrefile = sTempNamefile.substring(0,(sTempNamefile.length()-4));

				                fp.setCaption(sNombrefile);					// Nombre del archivo imagen (foto). Ejemplo: 'foto3'
								fp.setFilename(sTempNamefile);				// Nombre completo del archivo imagen (foto). Ejemplo: 'foto3.jpg'
								
								if(files[i].exists()) {
									InputStream ios = null;			
									try {
										BufferedInputStream bufferis = null;

										try {
											bufferis = new BufferedInputStream(new FileInputStream(files[i]));

										} catch (FileNotFoundException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										} 
										
										try {
											int bytes = (int) files[i].length();
											bytefoto = new byte[bytes]; 
											int readBytes = bufferis.read(bytefoto);	
//											String encodedString = Base64.encode(bytefoto); 
											
										} catch (FileNotFoundException e) {
											// TODO Auto-generated catch block
											appendLog(" - file jpg 1er. Catch - FileNotFoundException: "+e.getMessage());
											e.printStackTrace();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											appendLog(" -  file jpg 2do. Catch - IOException: "+e.getMessage());
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
									 appendLog(" else - File Not Found: "+files[i]);
									 //return;
								 }						
									
								fp.setData(bytefoto);				
								
//								outbuffer=read(files[i]);
//				                
//				                fp.setData(outbuffer);									                
						
								//***************************************************************
								
						        String sEstimateId = fp.getEstimateId();					// Id de Estimación
							    String sSupNumber  = fp.getSupNumber();						// Suplement Number. El SupNumber es "cero" (0) hasta nuevo aviso
						        String sCaption    = fp.getCaption();						// Nombre del archivo imagen (foto). Ejemplo: 'foto3'
						        String sFileName   = fp.getFilename();						// Nombre completo del archivo imagen (foto). Ejemplo: 'foto3.jpg'
						        byte[] bytefoto	   = fp.getData();							// Arreglo en bytes[] de la imagen (foto).        
								//appendLogE(" class: EstimateActivity.java - PROC: UploaderFile(): ");
								boolean blnConnNet = checkConnectivity();
					
								appendLog(" - Estatus de la Red - Conectada=: "+blnConnNet);
								
								try { 
						            SoapObject request = new SoapObject(NAMESPACE_IMAGE, METHOD_NAME_IMAGE);
						            appendLog(" class: EstimateActivity.java - PROC: UploaderFile() Despues de SoapObject request = new SoapObject(NAMESPACE2....: ");
						            //request.addProperty("fotos", fotos);
						            
						            String sFrameNum   = String.valueOf(ifoto+1);					// Consecutivo de cada foto (comienza en uno '1', hasta 'n')
						            ifoto++;
						            String encodedString = Base64.encode(bytefoto);
						            
						            request.addProperty("estimateId", sEstimateId);
						            request.addProperty("suppN", sSupNumber);
						            request.addProperty("vcaption", sCaption);
						            request.addProperty("filename", sFileName);
						            //request.addProperty("imgData", bytefoto);
						            request.addProperty("imgData",encodedString);
						            request.addProperty("frameNum", sFrameNum);
						            
						            appendLog(" class: EstimateActivity.java - PROC: UploaderFile() Despues de  request.addProperty. estimateId: "+sEstimateId);
						            appendLog(" class: EstimateActivity.java - PROC: UploaderFile() Despues de  request.addProperty. filename: "+sFileName);
						            appendLog(" class: EstimateActivity.java - PROC: UploaderFile() Despues de  request.addProperty. sFrameNum: "+sFrameNum);
					
						            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
						            appendLog(" class: EstimateActivity.java - PROC: UploaderFile() Despues de  SoapSerializationEnvelope envelope....: ");
						            //envelope.implicitTypes=true;
						            envelope.dotNet=true;
						            envelope.setOutputSoapObject(request);
						            appendLog(" class: EstimateActivity.java - PROC: UploaderFile() Despues de  envelope.setOutputSoapObject(request)....: ");
						            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL_IMAGE);
						            appendLog(" class: EstimateActivity.java - PROC: UploaderFile() Despues de  HttpTransportSE androidHttpTransport = new ....: ");
						            androidHttpTransport.call(SOAP_ACTION_IMAGE, envelope);
						            appendLog(" class: EstimateActivity.java - PROC: UploaderFile() Despues de  androidHttpTransport.call....: ");
						            SoapPrimitive resultado = (SoapPrimitive) envelope.getResponse();
						            String res = resultado.toString();
									if(res.indexOf("IE-1000")>-1){
										exe2 = 1;
									}
						            //Object result = (Object)envelope.getResponse();
						            appendLog(" class: EstimateActivity.java SincronizarEstimacion - PROC: UploaderFile() - Respuesta: "+resultado.toString());
						            
									
								} catch (Exception e) {
									e.printStackTrace();
									appendLog(" class: EstimateActivity.java  SincronizarEstimacion- PROC: call() - Try del doInBackground() -linea= 1092 - Excepción: "+e.getMessage());
								}
							
				            }
				    		
				        }
				        catch (Exception ioe) {

				            System.out.println("Error creating image file" + ioe);
				        }
				        
				//********************************************************************************
				//*******************  FIN  Proceso de envio de las Fotos    **************************
				//********************************************************************************		
				        
						if(exe1 == 1 && exe2 == 1){
						String squery = "DELETE FROM ajustes WHERE estimateid='"+estima + "'";
	//					appendLog("squery: " + squery);
	//					//Eliminar un registro
	//					myDb.execSQL(squery);
						if (myDb != null) {
							myDb.close();
						}
						
					   try {
						   myDb = SQLiteDatabase.openDatabase(path+"ajustes.sqlite", null, SQLiteDatabase.OPEN_READWRITE);
						}catch (SQLiteException ex) {
							appendLog(" class: Estimacion.java - SincronizarEstimacion PROC:  class SincronizarEstimacion() - SQLiteException al intentar abrir la BD  ajustes.sqlite - path: " + path +" - " +  ex.getMessage());
						} 					        	  
							  
						//myDb.delete("ajustes", "estimateid =" + sRow, null);
						//*** Llama al procedure que elimina registro del ajuste ya sincronizado, de la tabla de Ajustes.sqlite ***
						//String squery = "DELETE FROM ajustes WHERE estimateid='"+estima + "'";
						appendLog("squery: " + squery);
						//Eliminar un registro
						myDb.execSQL(squery);	
						appendLog("Despues de myDb.execSQL");							
						appendLog("Elimina registro: " + estima);
						lencursor = lencursor -1;
					    if(lencursor==1){
					    	rows.clear();
					    	//setListAdapter(null);
					    	//CustomArrayAdapter.notifyDataSetChanged();
					    	//lista.setAdapter(null);
						   //mostrarToast("** No existen mas Ajustes pendientes por Sincronizar **");
						   appendLog("SincronizarEstimacion ** No existen mas Ajustes pendientes por Sincronizar **");
							myDb.close();	
							appendLog("SincronizarEstimacion ** Se cerro la Base de Datos: myDb  **");
							Intent myIntent =null; 
	
							if(sOrigen.equals("DataEntry")){
								// Esta clase fue llamada desde: DataEntry.java (entrada de datos principal). Retorna hacia ella.
								 appendLog(" class: Estimacion.java - PROC: SincronizarEstimacion() - clase fue llamada desde: DataEntry.java (entrada de datos principal). Retorna hacia DataEntry");
								myIntent = new Intent(Estimacion.this, DataEntry.class);
							}else{
								// Esta clase fue llamada desde: VehiculoActivity.java (Pantalla principal). Retorna hacia ella.
								appendLog(" class: Estimacion.java - PROC: SincronizarEstimacion() - clase fue llamada desde: VehiculoActivity.java (Pantalla principal). Retorna hacia VehiculoActivity");
								myIntent = new Intent(Estimacion.this, VehiculoActivity.class);				
							}		
							
							startActivity(myIntent);
							finish();							
					    }			
					}
					
					myDb.close();	
					appendLog("** Se cerro la Base de Datos: myDb  **");				
			
			return null;
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			eDialog = new ProgressDialog(Estimacion.this);
	        //pDialog.setMessage("Sincronizando la Estimación: " + estima + ", espere por favor.." );
	        eDialog.setMessage("Sincronizando la Estimación, espere por favor.." );
	        eDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        eDialog.setCancelable(true);
	        eDialog.show();
		}
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			appendLog("onPostExecute - result: " + " - " + String.valueOf(result));
			eDialog.dismiss();
		}
	}	
	
//}
	
	public byte[] read(File file) throws IOException {
//
//			    if ( file.length() > MAX_FILE_SIZE ) {
//			        throw new FileTooBigException(file);
//			    }

	    byte []buffer = new byte[(int) file.length()];
	    InputStream ios = null;
	    try {
	        ios = new FileInputStream(file);
	        if ( ios.read(buffer) == -1 ) {
	            throw new IOException("EOF reached while trying to read the whole file");
	        }        
	    } finally { 
	        try {
	             if ( ios != null ) 
	                  ios.close();
	        } catch ( IOException e) {
	        }
	    }
	    Toast.makeText(this, "buffer: "+buffer, Toast.LENGTH_LONG).show();
	    return buffer;
	}		

	// Procedimiento que llena un log (bitácora) de las acciones tomadas en la presente clase, en una archivo físico de la tableta
public void appendLog(String text)
{       
	   TomaFechaSistema();
	   versionLog = "" + fechaLogD;

	   File logFile = new File(sPrimaryStorage+"/Download/logEstimate_"+versionLog+".file");
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
	
}

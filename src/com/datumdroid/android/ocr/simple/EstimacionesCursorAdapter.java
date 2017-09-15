package com.datumdroid.android.ocr.simple;
 
import java.io.BufferedWriter;
import java.io.File;

import java.io.IOException;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import ve.com.inma.mobility_4_1.api.R;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.CursorIndexOutOfBoundsException;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
 
// Desarrollado por José A. Azpurua - Junio 2014 - Última modificación: 16 Dic. 2014 
// Clase para manejar el cursor que define la lista de Ajustes pendientes por sincronizar
public class EstimacionesCursorAdapter extends CursorAdapter
{
 
   //private EstimacionesDbAdapter dbAdapter = null ;
	private SQLiteDatabase myDb = null; // Database object
	private String path = "/mnt/sdcard/databasedata/";	//Environment.getDataDirectory() + "/data/com.totsp.database/databases/";
	
	public String versionLog = "";
	public String fechaLogD = "";
	public String fechaHoraLogD = "";  
 
   public EstimacionesCursorAdapter(Context context, Cursor c)
   {
      super(context, c);
      //openDatabaseEstimates();
//      dbAdapter = new EstimacionesDbAdapter(context);
//      dbAdapter.abrir();
//      Toast.makeText(context, "Llegue a EstimacionesCursorAdapter", Toast.LENGTH_LONG).show();
//      String sIdEst   = c.getString(2);	// Campo # 2 - Id de Estimación+
//      if(sIdEst.equals("FIRST")){
//    	  Toast.makeText(context, "*** NO EXISTEN AJUSTES PENDIENTES PARA SINCRONIZAR ***", Toast.LENGTH_LONG).show();
//    	  return;
//      }
      
   }
   
   // Enlace y obtención de valores de los campos definidos en el cursor. Establece estos valores en un TextView para conformar lista de ajustes
   @Override
   public void bindView(View view, Context context, Cursor cursor)
   {
	   
      TextView tv = (TextView) view ;
      
      String sTemp = cursor.getString(7);	// Campo # 7 - Json de la Estimación/Ajuste. Se guarda para otras acciones
      
      JSONObject json = null;
      JSONObject o = null;
   
		try {
			json = new JSONObject(sTemp);	// La variable JSONObject "json", toma el valor de sTemp convertido a objeto Json
	
			o = json.getJSONObject("header");
//			
//			sTemp = json.toString();
//			String sJson = "";
			String sEspacios = "";			
			//cursor.getString(2)+" - "+cursor.getString(3)+" - "+cursor.getString(4)+" - "+cursor.getString(5)+" - "+cursor.getString(6);
			String sIdEst   = cursor.getString(2);	// Campo # 2 - Id de Estimación
			String sFecha   = cursor.getString(3);	// Campo # 3 - Fecha de Estimación
			String sPlaca   = cursor.getString(4);	// Campo # 4 - Placa del Vehículo ajustado		
			String sPerito  = cursor.getString(5);	// Campo # 5 - Perito que hizo el ajuste
			String sEstatus = cursor.getString(6);	// Campo # 6 - Estatus de la Estimación (SENT/SINC)
			String sjson 	= cursor.getString(7);	// Campo # 7 - Json de la Estimación/Ajuste
			String sMarca = "";
			String sModelo = ""; 
			Toast.makeText(context, "bindView() - sIdEst: " + sIdEst, Toast.LENGTH_LONG).show();
//			int iKEY_ROWID = 0; 
//			
//			iKEY_ROWID = cursor.getInt(0);
//			Toast.makeText(context, "iKEY_ROWID: " + iKEY_ROWID, Toast.LENGTH_LONG).show();
//
////			int firstreg = 0;
//			//Toast.makeText(context, "Longitud sIdEst: "+sIdEst.length()+ " caracteres", Toast.LENGTH_LONG).show();
//			if(sIdEst.length()<20){
//				sEspacios = space(20-sIdEst.length());
//				sIdEst=sIdEst+sEspacios;
//			}			
//			//Toast.makeText(context, "Longitud sFecha: "+sFecha.length()+ " caracteres", Toast.LENGTH_LONG).show();
//			if(sFecha==null || sFecha=="null"  || sFecha.isEmpty() || sFecha.length()<8 || sFecha.length()>14|| sFecha == ""){
//				sFecha = " NO - FECHA ";
//			}else{
//				if(sFecha.lastIndexOf('#')>0){
//					sFecha=sFecha.substring(1,(sFecha.length()-1));
//				}else{
//					sFecha = sFecha + " ";				
//				}
//			}
//			
//			if(sFecha.length()<12){
//				sEspacios = space(12-sFecha.length());
//				sFecha=sFecha+sEspacios;
//			}
//			
//			if(sPlaca==null || sPlaca=="null"  || sPlaca.isEmpty() || sPlaca.length()<2  || sPlaca == ""){
//				sPlaca = "SIN PLACA ";
//			}else{
//				if(sPlaca.length()<10){
//					sEspacios = space(10-(sPlaca.length() + 1));
//					sPlaca = " " + sPlaca + sEspacios;
//				}
//			}
//			
//			if(sPerito==null || sPerito=="null"  || sPerito.isEmpty() || sPerito.length()<2  || sPerito == ""){
//				sPerito = "PERITO";
//			}else{
//				if(sPerito.length()<6){
//					sEspacios = space(6-sPerito.length());
//					sPerito = " " + sPerito + sEspacios;
//				}
//			}			
//	
//			if(o.getString("Year")==null || o.getString("Year")=="null"  || o.getString("Year").isEmpty() || o.getString("Year").length()<4  || o.getString("Year") == ""){
//				GlobalOcr.sAño = " N/A ";
//			}else{
//				GlobalOcr.sAño = o.getString("Year");
//			}
			int iMark = o.getString("Make").length();
//			Toast.makeText(context, "iMark: "+iMark+ " caracteres", Toast.LENGTH_LONG).show();
			if(o.getString("Make")==null || o.getString("Make")=="null"  || o.getString("Make").isEmpty() || o.getString("Make").length()<2  || o.getString("Make") == ""){
				GlobalOcr.sMarca = "NO SE REGISTRO MARCA  ";
			}else{
				sMarca = o.getString("Make").trim();
				if(sMarca.length()<20){
					sEspacios = space(44-iMark);
					GlobalOcr.sMarca = sMarca + sEspacios;
//					iMark = sEspacios.length();
//					Toast.makeText(context, "sEspacios iMark: "+iMark+ " espacios", Toast.LENGTH_LONG).show();
				}else{
					GlobalOcr.sMarca = sMarca;
				}
			}
////			iMark = GlobalOcr.sMarca.length();
////			Toast.makeText(context, "Final Marca Len: "+iMark+ " caracteres", Toast.LENGTH_LONG).show();
////			Toast.makeText(context, "Marca: "+GlobalOcr.sMarca, Toast.LENGTH_LONG).show();
//			
			if(o.getString("Model")==null || o.getString("Model")=="null"  || o.getString("Model").isEmpty() || o.getString("Model").length()<2  || o.getString("Model") == ""){
				GlobalOcr.sModelo = "NO SE REGISTRO MODELO      ";
			}else{
				sModelo = o.getString("Model").trim();
				if(sModelo.length()<20){
					sEspacios = space(50-sModelo.length());
					GlobalOcr.sModelo = sModelo + sEspacios;
				}else{
					GlobalOcr.sModelo = sModelo;
				}
			}
//			
    
			
			 //tv.setText("   " + GlobalOcr.sFecha +"  -     " + GlobalOcr.sAño + "       -      " + GlobalOcr.sMarca + "     -  " + GlobalOcr.sModelo + "    -   " + GlobalOcr.sPlaca + "  - " + cursor.getString(3));
			// tv.setText(" " + GlobalOcr.sFecha +"  -   " + GlobalOcr.sAño + "    -    " + GlobalOcr.sMarca + "   -  " + GlobalOcr.sModelo + " -   " + GlobalOcr.sPlaca );
			 //tv.setText(" " + cursor.getString(0) +"  -   " + cursor.getString(1) +"  -   " + cursor.getString(2) +"  -   " + cursor.getString(3) +"  -   " + cursor.getString(4) +"  -   " + cursor.getString(5));		
			//tv.setText(" " + sIdEst +  " - " + sFecha +  "  - " + sPlaca +  " - " + sPerito +  " - " + sEstatus +  " - " + sjson);
			
			// El control TextView: "tv" se establece con los valores tomados para formar una fila/registro de la lista
			tv.setText("     " + sPlaca +  "    -    " + sFecha +  "      -     " + sMarca +  "      -     " + sModelo);
			 
//		} catch (JSONException e) {
		} catch (Exception e) {			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
   }
 
   @Override
   public View newView(Context context, Cursor cursor, ViewGroup parent)
   {
      final LayoutInflater inflater = LayoutInflater.from(context);
      final View view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
 
      return view;
   }
   
   // Ejecución de query de consulta para conformar el cursor
   public synchronized String getData(String query) {
//	   Toast toast1 = new Toast(this);
//	   toast1.makeText(this, "query: "+query, Toast.LENGTH_LONG).show();
	openDatabaseEstimates();
   	Cursor c = null;
   	String data;
		try {
	    	c = this.myDb.rawQuery(query, null);
	    	c.moveToFirst();
	    	if(c.isFirst())
	    	{
				data = c.getString(2);
				Context context = null;
				   Toast toast = new Toast(context);
				   //Toast.makeText(context, "EstimacionesCursorAdapter query: "+query, Toast.LENGTH_LONG).show();
				   appendLog(" class: EstimacionesCursorAdapter.java - PROC:  getData() - query para llenar el cursor: c - : " + query +" - ");
				   //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				   //toast.setDuration(Toast.LENGTH_LONG);
				   //toast.setView(layout);
				   //Toast.makeText(context, "EstimacionesCursorAdapter this.myDb: "+this.myDb, Toast.LENGTH_LONG);
				   toast.show();
//				   Toast.makeText(context, "data: "+data, Toast.LENGTH_LONG);
//				   toast.show();				   
				   
				//Toast.makeText(cont, "this.myDb: "+this.myDb, Toast.LENGTH_LONG);
				//Toast.show();
//				Toast.makeText(this, "this.myDb: "+this.myDb, Toast.LENGTH_LONG).show();
//				Toast.makeText(context, "data: "+data, Toast.LENGTH_LONG).show();
				////c.close();
				//appendLog(fechaLogD +  " class: DroidGap.java - PROC: getData() - Se lleno exitosamente el cursor de data: " + "c - " + query);
				this.myDb.close();
				return data;
				//return c;
	    	}
	    }catch (CursorIndexOutOfBoundsException ex) {
	    	//appendLog(fechaLogD +  " class: DroidGap.java - PROC:  getData() - CursorIndexOutOfBoundsException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
	    	//c.close();
	    	//Toast.makeText(mContext, "ERROR: "+query, Toast.LENGTH_LONG).show();			
	    }catch (SQLiteException ex) {
	    	//appendLog(fechaLogD +  " class: DroidGap.java - PROC:  getData() - SQLiteException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
	    	//Toast.makeText(mContext, "ERROR: "+query, Toast.LENGTH_LONG).show();			

		}finally{
	    	//Toast.makeText(mContext, "READY", 3).show();
			////c.close();
		}
   	return "{}";
		//return c;

   } 
   
// Abrir BD: "ajustes.sqlite", la cual contiene los registros de ajustes hechos en la tableta subyacente
public void openDatabaseEstimates(){
 		if (this.myDb != null) {
			this.myDb.close();
		}
		
       try {
    	   myDb = SQLiteDatabase.openDatabase(path+"ajustes.sqlite", null, SQLiteDatabase.OPEN_READWRITE);

       } catch (SQLiteException sqle) {
       	//appendLog(fechaLogD +  " class: DroidGap.java - PROC: openDatabaseEstimates() - No se pudo abrir o no existe la Base de Datos: estimates.sqlite - " + sqle);
       	//Toast.makeText(this, "No se pudo abrir o no existe la Base de Datos: estimates.sqlite - "+sqle, Toast.LENGTH_LONG).show();
       	//mostrarToast("No se pudo abrir o no existe la Base de Datos: estimates.sqlite - "+sqle);
       	//throw sqle;
       }       		
		//System.out.println("OPEN ESTIMATES!");
		//appendLog(fechaLogD +  " class: DroidGap.java - PROC: openDatabaseEstimates() - Apertura exitosa de la B.D. : estimates.sqlite, para escritura/lectura");
		//Toast.makeText(this, "DB READY", Toast.LENGTH_SHORT).show();			
   }   
   
//private void mostrarToast(String text){
//   text=" "+text+" ";
//   LayoutInflater inflater = getLayoutInflater();
//   View layout = inflater.inflate(R.layout.toast_layout,(ViewGroup) findViewById(R.id.toastLayout));
//   TextView textv = (TextView) layout.findViewById(R.id.textview);
//   textv.setText(text);
//   Context context;
//   Toast toast = new Toast(context);
//   toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//   toast.setDuration(Toast.LENGTH_LONG);
//   toast.setView(layout);
//   Toast.makeText(context, text, Toast.LENGTH_LONG);
//   toast.show();
//}	   

	public String space(int x){
	
		String spaces=" ";
		int numberOfSpaces=x;
	
		for(int i=0;i<numberOfSpaces;i++){
		spaces= spaces + " ";
		}
	
		return spaces;
	}
	
	// Procedimiento que llena un log (bitácora) de las acciones tomadas en la presente clase, en una archivo físico de la tableta
public void appendLog(String text)
{       
	   TomaFechaSistema();
	   versionLog = "" + fechaLogD;

	   File logFile = new File("sdcard/Download/serviceCurAdapter"+versionLog+".file");
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

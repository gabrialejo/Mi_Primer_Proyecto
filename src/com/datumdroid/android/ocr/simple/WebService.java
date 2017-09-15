package com.datumdroid.android.ocr.simple;


import java.io.File;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
//import ve.com.inma.mobility_4_1.json.AutoInmaJson;
//import ve.com.inma.mobility_4_1.json.EObjectJson;
//import ve.com.inma.mobility_4_1.json.JsonContainer;

//import org.apache.http.HttpVersion;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.params.CoreProtocolPNames;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.ContentBody;
//import org.apache.http.entity.mime.content.FileBody;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.serialization.SoapObject;

import com.phonegap.Global;

import ve.com.inma.mobility_4_1.api.R;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.sql.Blob;

public class WebService extends Activity {
    //private static final String SOAP_ACTION = "http://inma.com.ve/inmaEst_estimateProcess";
    private static final String SOAP_ACTION = "http://192.168.0.45/inmaEst_estimateProcess";

    private static final String METHOD_NAME = "inmaEst_estimateProcess";

    //private static final String NAMESPACE = "http://inma.com.ve/";
    private static final String NAMESPACE = "http://192.168.0.45/";
    //private static final String URL = "http://192.168.0.74/InmaEstServiceMobile/Service.asmx";
    //private static final String URL = "http://192.168.0.39:1580/InmaEstServiceMobile/service.asmx";
    private static final String URL = "http://192.168.0.45/servicesMercantil/service.asmx";	  
    private String sEstimateId;
    private int iCantFotos;
    private String json;
    private File file = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_service);
		////json = getIntent().getExtras().getString("eObject");
		Toast.makeText(this, "*** LLEGUEEE a onCreate( en WEBService json: ***", Toast.LENGTH_LONG).show();
		//Toast.makeText(this, "*** onCreate( en WEBService json: ***"+json, Toast.LENGTH_LONG).show();
		//Toast.makeText(this, "*** onCreate( en WEBService json: ***"+Global.rutaFotos+"/archive.zip", Toast.LENGTH_LONG).show();

		//call(json);
		//CreateZipFileFromDirectory();
		String zipFile = GlobalOcr.rutaFotos+"/archive.zip";
		//call(zipFile);

		////UploaderFile nuevaTarea = new UploaderFile();
		//nuevaTarea.execute(json);
		//String zipFile = Global.rutaFotos+"/archive.zip";
		//file = new File(zipFile);
		//if (file.exists()) {
			//nuevaTarea = new UploaderFile();
			////nuevaTarea.execute(zipFile);
		//}		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web, menu);
		return true;
	}

	public void CreateZipFileFromDirectory()
	{
        String zipFile = GlobalOcr.rutaFotos+"/archive.zip";
        String srcDir = GlobalOcr.rutaFotos;
        String sCantFotos = String.valueOf(GlobalOcr.cuentafotos);

        try {

            // create byte buffer
            byte[] buffer = new byte[1024];

            FileOutputStream fos = new FileOutputStream(zipFile);

            //ZipOutputStream zos = new ZipOutputStream(fos);

            File dir = new File(srcDir);

            File[] files = dir.listFiles();
			
//			zos.putNextEntry(new ZipEntry(Global.estimateId));
//			zos.putNextEntry(new ZipEntry(sCantFotos));

            for (int i = 0; i < files.length; i++) {

                System.out.println("Adding file: " + files[i].getName());

                FileInputStream fis = new FileInputStream(files[i]);
 
                // begin writing a new ZIP entry, positions the stream to the start of the entry data
                //zos.putNextEntry(new ZipEntry(files[i].getName()));
                //Blob b = Hibernate.createBlob(fis);
                
                int length;
 
                Blob blob = null;
                
                while ((length = fis.read(buffer)) > 0) {
                    //zos.write(buffer, 0, length);
                	//blob = new Blob (buffer);
                	//Bean del Arreglo de la foto para el WebService
                	
                }

                //zos.closeEntry();

                // close the InputStream
                fis.close();
            }

            // close the ZipOutputStream

            //zos.close();

        }
        catch (IOException ioe) {

            System.out.println("Error creating zip file" + ioe);
        }
		
	}
//    public void call(String eObject)
//    {
//
//    	//final TextView textRestult = (TextView) findViewById(R.id.result);
//        try {
//        	Toast.makeText(this, "** ¡Respuesta! **"+eObject.toString(), Toast.LENGTH_LONG).show();
//            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
//
//            //request.addProperty("eObject", eObject);
//            request.addProperty("FileName", eObject);
//            request.addProperty("ToEmail", "jazpurua@inma.com.ve");
//            request.addProperty("vNombre", "Jose Azpurua");
//
//            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//            envelope.dotNet=true;
//            envelope.setOutputSoapObject(request);
//
//            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
//            androidHttpTransport.call(SOAP_ACTION, envelope);
//
//            Object result = (Object)envelope.getResponse();
//            Toast.makeText(this, "** ¡Respuesta! **"+result.toString(), Toast.LENGTH_LONG).show();
//            //textRestult.append(result + "\n");
//        } catch (Exception e) {
//            //textRestult.append(e.toString() + "\n");
//        	
//            }
//    }	
	
	/*
	 * Clase asincrona para subir la foto
	 */
	class UploaderFile extends AsyncTask<String, Void, Void>{

		ProgressDialog pDialog;
		String zipFile = GlobalOcr.rutaFotos+"/archive.zip";
        String sEstimateId = Global.estimateId;
	    String sCantFotos = String.valueOf(GlobalOcr.cuentafotos);
		protected Void doInBackground(String... params) {
			//miFoto = params[0];
			try { 
//				HttpClient httpclient = new DefaultHttpClient();
//				httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//				HttpPost httppost = new HttpPost("http://servidor.com/up.php");
//				File file = new File(miFoto);
//				MultipartEntity mpEntity = new MultipartEntity();
//				ContentBody foto = new FileBody(file, "image/jpeg");
//				mpEntity.addPart("fotoUp", foto);
//				httppost.setEntity(mpEntity);
//				httpclient.execute(httppost);
//				httpclient.getConnectionManager().shutdown();
	            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

	            //request.addProperty("eObject", eObject);
	            request.addProperty("FileName", json);
	            request.addProperty("EstimateId", sEstimateId);
	            request.addProperty("CantFotos", sCantFotos);
	    		file = new File(zipFile);
	    		if (file.exists()) {
		            request.addProperty("FileFoto", file);
	    		}		            
	            
//	            request.addProperty("ToEmail", "jazpurua@inma.com.ve");
//	            request.addProperty("vNombre", "Jose Azpurua");

	            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	            envelope.dotNet=true;
	            envelope.setOutputSoapObject(request);

	            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
	            androidHttpTransport.call(SOAP_ACTION, envelope);

	            Object result = (Object)envelope.getResponse();
	            //Toast.makeText(this, "** ¡Respuesta! **"+result.toString(), Toast.LENGTH_LONG).show();				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(WebService.this);
	        pDialog.setMessage("Subiendo la imagen, espere." );
	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pDialog.setCancelable(true);
	        pDialog.show();
		}
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pDialog.dismiss();
		}
	}	
	
}


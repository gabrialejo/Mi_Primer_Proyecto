package com.datumdroid.android.ocr.simple;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

//Crea la autenticación
class ProxyAuthenticator extends Authenticator {

        private String user, password;

          public ProxyAuthenticator(String user, String password) {
              this.user = user;
              this.password = password;
          }

          protected PasswordAuthentication getPasswordAuthentication() {
              return new PasswordAuthentication(user, password.toCharArray());
          }
      }     
      


	class WSTask extends AsyncTask<String, Void, String> {
	private static final String Metodo = "SaveImg";
	// Namespace definido en el servicio web
	private static final String snamespaces = "http://inmaapps1.grupoinma.com/InmaEstMultimedia";
	// namespace + metodo
	private static final String accionSoap = "http://inmaapps1.grupoinma.com/InmaEstMultimedia/SaveImg";
	// Fichero de definicion del servcio web
	private static final String url = "http://inmaapps1.grupoinma.com/InmaEstMultimedia/inmaestimglb.asmx";
	private byte[] bytefoto = null;
	public String fechaLogD = "";
	public String fechaHoraLogD = "";		
	public String versionLog = "";
	private String encodedString = "";	
	
    @Override
    protected String doInBackground(String ... url) {
          
        // params comes from the execute() call: params[0] is the url.
        try {
            return executeWS(url[0]);
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }
    // onPostExecute displays the results of the AsyncTask.
    protected void onPostExecute(String result) {
        //textView.setText(result);
   }
//}


private String executeWS(String myurl) throws IOException {
     String is= null;
     
	 String rutafoto = "/peritaje-movil/2014/02/05/hshs672/15-08-23/";
	 String nomimagen = "foto3.jpg";
	 File rootB = new File(Environment.getExternalStorageDirectory(), rutafoto);
	 File file = new File(rootB, nomimagen);	
	 
	 
	 if(file.exists()) {
		//byte [] buffer = new byte[(int) file.length()];
		//bytefoto = new byte[(int) file.length()];
		//bytefoto = new byte[1024];

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
				encodedString = Base64.encode(bytefoto); 

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				appendLogT(" - file jpg 1er. Catch - FileNotFoundException: "+e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				appendLogT(" -  file jpg 2do. Catch - IOException: "+e.getMessage());
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
		 appendLogT(" else - File Not Found: "+file);
		 //tv.setText("File Not Found: "+file);
		 return "NO EXISTE";
	 }     

        // Modelo el request
    SoapObject request = new SoapObject(snamespaces, Metodo);
    
    request.addProperty("estimateId", "140825024651TEST25M");
    request.addProperty("suppN", "0");
    request.addProperty("vcaption", "foto3");
    request.addProperty("filename", "foto3.jpg");
    request.addProperty("imgData", encodedString);    
    //request.addProperty("eObject" ,eObject); // Paso parametros al WS

    // Modelo el Sobre
    SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    sobre.implicitTypes=true;
    sobre.dotNet = true;
    sobre.setOutputSoapObject(request);

    
    
    // Modelo el transporte
   // URL url= new URL("http://inmaapps1.grupoinma.com/MobilityService/service.asmx");
    //URLConnection conn=url.openConnection();
    
    //HttpTransportSE transporte = new HttpTransportSE(url);
    
    Authenticator.setDefault(new ProxyAuthenticator("INMA\\jazpurua", "password"));
    Proxy proxys = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.0.254", 8080));
    
    
    //ServiceConnection conn=new ServiceConnectionSE(proxys, url);
    
    //conn.setRequestProperty("Proxy-Authorization","Basic "+encoded);
      
    
    
      HttpTransportSE transp = new HttpTransportSE(proxys, url);
      
      
	  try {
			transp.call(accionSoap,sobre);
	  } catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
	  }
		  
		try{  
			// Llamada
			
			  transp.call(accionSoap, sobre);
		
			// Resultado
			SoapPrimitive resultado = (SoapPrimitive) sobre.getResponse();
			
			
			is= resultado.toString();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is == null) {
				is=" Esta Nulo la respuesta";
			}  
		}
		return is;
	}

public void appendLogT(String text)
{       
	   TomaFechaSistema();
	   versionLog = "" + fechaLogD;		   
	   File logFile = new File("sdcard/Download/servicemaint_"+versionLog+".file");
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
	      buf.append(fechaHoraLogD+"-"+text.toString());
		  buf.newLine();
		  buf.close();
	   }
	   catch (IOException e)
	   {
		  // TODO Auto-generated catch block
		   //Toast.makeText(this, "** appendLog error IOException: "+e.getMessage(), Toast.LENGTH_LONG).show();
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



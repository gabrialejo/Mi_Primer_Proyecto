package ve.com.inma.mobility_4_1.api;

import android.app.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnClickListener;
import android.widget.*;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import android.content.DialogInterface;
import android.app.Dialog; 

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.HttpResponse;
 
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.ksoap2.*;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;
import com.datumdroid.android.ocr.simple.*;
import java.text.*;
import java.util.*;
import java.io.*;
 
import java.lang.IllegalStateException;
import java.lang.StringBuilder;
 
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Login extends Activity {

		// Método del servicio web para obtener usuario
	private static final String METHOD_NAME = "ObtieneUsuario";
		// Namespace definido en el servicio web
	private static final String NAMESPACE = "http://inmaapps1.grupoinma.com/MobilityService";
		// namespace + metodo
	private static final String SOAP_ACTION = "http://inmaapps1.grupoinma.com/MobilityService/ObtieneUsuario";
		// Archivo de definicion del servicio web
	private static final String URL = "http://inmaapps1.grupoinma.com/MobilityService/service.asmx";
	// Método del servicio web de total registros profile
	private static final String METHOD_NAMEP = "Obtiene_Total_Registros_Profile";
	// namespace + metodo de total registros profile
	private static final String SOAP_ACTIONP = "http://inmaapps1.grupoinma.com/MobilityService/Obtiene_Total_Registros_Profile";	

    //private TextView lblGotoRegister;
    private Button btnLogin;
    private Button btnEnter;
    private EditText txtUsername;
    private EditText txtPass;
    private TextView txtTitulo;
//    private TextView loginErrorMsg;
    private Cursor cursor;
    private SQLiteDatabase myDb = null; // Database object
    private int lencursor = 0;
    public String path = "/mnt/sdcard/databasedata/";		//Environment.getDataDirectory() + "/data/com.totsp.database/databases
    //public String path = Environment.getExternalStorageDirectory().toString()+"/databasedata/";
    //public String path = Environment.getRootDirectory().getAbsolutePath().toString();
    private boolean blnAutenticar = false;
    private String sNombreUsuario = "";
    private String sPerito = "";
	protected String sUser = "";
	protected String sPassword = "";
	protected String sUserName = "";
	protected String sProfile = "";
	protected String sFecha = "";
	
	protected String sResultado = "";
	protected String sResultado2 = "";
	public String fechaLogD = "";
	public String fechaHoraLogD = "";	
	public String versionLog = "";
	private String sPrimaryStorage = "/mnt/sdcard/";
	private String sSecondaryStorage = "/mnt/sdcard/";
	private boolean blnAutenticado = false;
    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login);
        txtTitulo = (TextView)this.findViewById(R.id.TextViewtitulo);
        txtUsername = (EditText)this.findViewById(R.id.txtUsername);
        txtPass = (EditText)this.findViewById(R.id.txtPass);
        btnLogin = (Button)this.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
            	
            	sUserName=txtUsername.getText().toString().trim();
            	sPassword=txtPass.getText().toString().trim();
//            	Toast.makeText(Login.this,"txtUsername: "+sUsername, Toast.LENGTH_LONG).show();
//            	Toast.makeText(Login.this,"txtPass: "+stxtPass, Toast.LENGTH_LONG).show();
            	blnAutenticar = autenticar(txtUsername.getText().toString(), txtPass.getText().toString());
                //validar(txtUsername.getText().toString(), txtPass.getText().toString());
            	if(blnAutenticar){
            		//Toast.makeText(Login.this,"*** EEEXITOOO ***"+sUsername, Toast.LENGTH_LONG).show();
            		txtTitulo.setText("Bienvenido: "+sNombreUsuario);
            		myDb.close();
            		sPerito = sUserName;
            		GlobalOcr.Perito = sPerito;

            		txtUsername.setFocusable(false);
            		txtUsername.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            		txtUsername.setClickable(false); // user nav
            		txtPass.setFocusable(false);
            		txtPass.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            		txtPass.setClickable(false); // user nav
            		btnEnter.setVisibility(View.VISIBLE);
            		appendLog("-"+fechaLogD +  " class: Login.java - PROC: onCreate() - Autenticacion exitosa del Usuario :"+sNombreUsuario);
            	}else{
            		//Toast.makeText(Login.this,"* El Usuario no existe o ingreso erroneamente el usuario o su password *"+sUsername, Toast.LENGTH_LONG).show();
            		txtTitulo.setText("* El Usuario : "+sUserName + " no existe o ingreso erroneamente el usuario o password *");
            	}
            	
            	//Evaluamos si la tabla de Profile local tiene la misma cantidad de registros que la de Producción
//				if (sResultado2.equals("MANTENIMIENTO")){
//					// La cantidad de registros es distinta a la tabla local. Por lo tanto se llama al procedimiento de Mantenimiento
//					//MantenimientoProfile();
//					Toast.makeText(Login.this,"*** sResultado2: "+sResultado2, Toast.LENGTH_LONG).show();
//				}            	
            	blnAutenticar = false;
            	
            }
        });
        
        btnEnter = (Button)this.findViewById(R.id.btnEnter);
        
        btnEnter.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
				Intent myIntent = new Intent(Login.this, VehiculoActivity.class);
				appendLog("-"+fechaLogD +  " class: Login.java - PROC: btnEnter.onClick() - Antes de salir a VehiculoActivity");
				startActivity(myIntent);
				finish();
				appendLog("-"+fechaLogD +  " class: Login.java - PROC: btnEnter.onClick() - Despues del finish()");
            }
        });        
        
        openDatabaseUsuarios();
//		startManagingCursor(cursor);
//		estimacAdapter = new EstimacionesCursorAdapter(this, cursor);
    }
    
    private boolean autenticar(String username, String pass){
		cursor = null;
    	if (!username.equals("") && !pass.equals("")){
    		try {
    			//myDb = SQLiteDatabase.openDatabase(path+"usuarios.sqlite", null, SQLiteDatabase.OPEN_READWRITE);
    			//Toast.makeText(this, "DB READY", Toast.LENGTH_SHORT).show();
    			//Cursor c = null;
    			String data;
    			String query = "Select * FROM usuarios WHERE UsuarioId = '" + username + "' ;";
    			String query2 = "Select * FROM usuarios WHERE UsuarioId = '" + username + "' and password = '" + pass + "' ;";
    			try {
//    				if (cursor != null && !cursor.isClosed()) {
//    				   cursor.close();
//    				} 
				
    				cursor = myDb.rawQuery(query, null);
    				cursor.moveToFirst();
    				int conta = cursor.getCount();
    				if(conta <=0){
//						 boolean blnConnNet = checkConnectivity();
//						
//						 if(blnConnNet==false){
//							 mostrarToast("*** ATENCIÓN *** Este Usuario NO existe en la Base de Datos local. Actualmente NO hay conectividad para Autenticar en Producción en la Nube. Revise e intente cuando se reestablezca");
//							 return false;
//						 }	    					
//    					//El usuario NO existe en la Base de Datos Usuarios.sqlite en el dispositivo. Se llama al WebService que lo busca en la Nube 
//    					ClassBuscaDatosUsuarioWeb nuevaTarea = new ClassBuscaDatosUsuarioWeb();
//    					nuevaTarea.execute(username,pass);
    					
    					
    					if(sResultado == "nodata"){
    						Toast.makeText(Login.this, "*** El Usuario. " + sUser + " no existe ***", Toast.LENGTH_LONG).show();
    					}else{
//    						Toast.makeText(Login.this, "Se encontro el Usuario. " + sUser + ". Se procede a insertarlo", Toast.LENGTH_LONG).show();
//    						// llama al procedimiento que inserta el registro del nuevo usuario encontrado a traves del WebService
//    						InsertaUsuarioBD();
    					}
    					return false;
    				}
    				if(cursor.isFirst())
    				{
    					data = cursor.getString(0)+" - "+cursor.getString(1)+" - "+cursor.getString(2);
    					sNombreUsuario = cursor.getString(1);
               			cursor = null;				
        				cursor = this.myDb.rawQuery(query2, null);
        				conta = cursor.getCount();
        				if(conta <=0){
        					Toast.makeText(Login.this, "*** El Usuario. " + sUser + " existe, pero la contraseña no es correcta ***", Toast.LENGTH_LONG).show();
        					return false;
        				}
    				}
    			} catch (CursorIndexOutOfBoundsException ex) {
    				appendLog(fechaLogD +  " class: DroidGap.java - PROC:  getData() - CursorIndexOutOfBoundsException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
    				cursor.close();
    				//Toast.makeText(mContext, "ERROR: "+query, Toast.LENGTH_LONG).show();			
    			} catch (SQLiteException ex) {
    				appendLog(fechaLogD +  " class: DroidGap.java - PROC:  getData() - SQLiteException al intentar llenar el cursor: c - : " + query +" - " +  ex.getMessage());
    				//Toast.makeText(mContext, "ERROR: "+query, Toast.LENGTH_LONG).show();
    				cursor.close();
    			}finally {
    				//Toast.makeText(mContext, "READY", 3).show();
    				cursor.close();
    			}
    		} catch (SQLiteException sqle) {
    			appendLog(fechaLogD +  " class: DroidGap.java - PROC: openDatabaseEstimates() - No se pudo abrir o no existe la Base de Datos: estimates.sqlite - " + sqle);
    			//throw sqle;
    		} 
    		return true;
    	}else{
    		Toast.makeText(Login.this,"Debe ingresar un nombre de usuario y un password para poder continuar", Toast.LENGTH_LONG).show();
    		return false;
    	}
    	
    }	
    
//    protected void InsertaUsuarioBD() {
//    	//String query = "INSERT INTO 'main'.'usuarios' ('usuarioId','nombres','password','cargo','fechaingreso') VALUES (sUser,sUserName,pass,sProfile,sFecha)";
//        try {
//    		//this.myDb.rawQuery(query, null);
//        	TomaFechaSistema();
//        	sFecha = fechaLogD;
//            ContentValues values = new ContentValues();
//
//                     values.put("usuarioId", sUser);
//                     values.put("nombres", sUserName);
//                     values.put("password", sPassword);
//                     values.put("cargo", sProfile);
//                     values.put("fechaingreso", sFecha);
//
//            myDb.insert("usuarios", null, values);    		
//    	}
//    	catch (SQLiteException ex) {
//    		appendLog(fechaLogD +  " class: DroidGap.java - PROC:  getData() - SQLiteException al intentar guardar el Usuario: " + sUser +" - " +  ex.getMessage());
//    		//Toast.makeText(mContext, "ERROR: "+query, Toast.LENGTH_LONG).show();
//    	}finally {
//    		//Toast.makeText(mContext, "READY", 3).show();
//    	}
//    }    
	
	//**********************************************************************************************
	
//	class ClassBuscaDatosUsuarioWeb extends AsyncTask<String, Void, Void> {
//		ProgressDialog pDialog;
//			
//		protected Void doInBackground(String... params) {
//
//			sUser 	  = params[0];
//			sPassword = params[1];
//			
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		
//				try {
//					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
//
//					request.addProperty("user", sUser);
//					request.addProperty("password", sPassword);
//
//					appendLog("class Login.java - Antes de SoapSerializationEnvelope ");
//					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//					envelope.dotNet=true;
//					envelope.setOutputSoapObject(request);
//					appendLog("class Login.java - Despues de envelope.setOutputSoapObject y antes de HttpTransportSE "+SOAP_ACTION);
//					HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
//					appendLog("class Login.java - Despues de HttpTransportSE y antes de androidHttpTransport - URL: " + URL);
//					try {
//						androidHttpTransport.call(SOAP_ACTION, envelope);
//						appendLog("class Login.java - Despues de androidHttpTransport.call y antes de Object result: ");
//						SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
//
//						appendLog("class Login.java - Dentro del Try de androidHttpTransport.call - Despues de Object result: " + String.valueOf(result));
//
//						sResultado = String.valueOf(result);
//						
//						String[] tempS = sResultado.split("@");
//						
//						int len = tempS.length;
//						
//						appendLog("class Login.java - Dentro del Try de androidHttpTransport.call -Longitud len: " + String.valueOf(len));
//
//						if(tempS.length > 0){
//							sUser=tempS[0];
//							sUserName=tempS[1];
//							sPassword=tempS[2];
//							sProfile=tempS[3];
//							
//							blnAutenticado = true;
//							
//							appendLog("class Login.java - Dentro del Try de androidHttpTransport.call - sUser: " + sUser);
//							appendLog("class Login.java - Dentro del Try de androidHttpTransport.call - sPassword: " + sPassword);
//							appendLog("class Login.java - Dentro del Try de androidHttpTransport.call - sUserName: " + sUserName);
//							appendLog("class Login.java - Dentro del Try de androidHttpTransport.call - sProfile: " + sProfile);
//							appendLog("class Login.java - Dentro del Try de androidHttpTransport.call - sFecha: " + sFecha);
//						}
//
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						appendLog("class Login.java - Try del androidHttpTransport.call - Despues de Object result Catch interno Exception " + " - " + String.valueOf(e));
//						e.printStackTrace();
//					}
//
//				} catch (Exception e) {
//					appendLog("class Login.java - Try del SoapObject request - Despues de Object result Catch Exception " + " - " + String.valueOf(e));
//					e.printStackTrace();
//				}
//				
//				if (blnAutenticado){
//					//Evaluamos la cantidad de registros locales de la tabla: 'Profile', llamando al WebService respectivo
//					try {
//						SoapObject requestP = new SoapObject(NAMESPACE, METHOD_NAMEP);
//						SoapSerializationEnvelope envelopeP = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//						envelopeP.dotNet=true;
//						envelopeP.setOutputSoapObject(requestP);
//						
//						HttpTransportSE androidHttpTransportP = new HttpTransportSE(URL);
//						
//						try {
//							androidHttpTransportP.call(SOAP_ACTIONP, envelopeP);
//							appendLog("class Login.java - Despues de androidHttpTransport.call y antes de Object result: ");
//							SoapPrimitive result2 = (SoapPrimitive) envelopeP.getResponse();
//							
//							sResultado2 = String.valueOf(result2);
//							
//							if (sResultado2.equals("MANTENIMIENTO")){
//								// La cantidad de registros es distinta a la tabla local. Por lo tanto se llama al procedimiento de Mantenimiento
//								//MantenimientoProfile();
//							}
//							
//							
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							appendLog("class Login.java - Try del androidHttpTransport.call - Despues de Object result Catch interno Exception " + " - " + String.valueOf(e));
//							e.printStackTrace();
//						}
//					} catch (Exception e) {
//						appendLog("class Login.java - Try del SoapObject request - Despues de Object result Catch Exception " + " - " + String.valueOf(e));
//						e.printStackTrace();
//					}						
//				}
//
//				return null;
//		}
//		
//		protected void onPreExecute() {
//				super.onPreExecute();
//				pDialog = new ProgressDialog(Login.this);
//				pDialog.setMessage("Buscando los datos del usuario nuevo en la Nube, espere por favor.." );
//				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//				pDialog.setCancelable(true);
//				pDialog.show();
//		}
//		protected void onPostExecute(Void result) {
//				super.onPostExecute(result);
//				appendLog("class Login.java - ClassBuscaDatosUsuarioWeb - Antes de tuser.setText(sUser); ");
//					txtUsername.setText(sUser);
//					txtPass.setText(sPassword);
//					// sPerito=GlobalOcr.Perito;
//				appendLog("class Login.java - ClassBuscaDatosUsuarioWeb - Despues de tuser.setText(sUser); ");				
//				pDialog.dismiss();
//		}
//	}	
	
	
	//**********************************************************************************************	    
    
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
    
    private boolean validar(String username, String pass){
            /* Comprobamos que no venga alguno en blanco. */
            if (!username.equals("") && !pass.equals("")){
                /* Creamos el objeto cliente que realiza la petición al servidor */
/*                HttpClient cliente = new DefaultHttpClient();
                 Definimos la ruta al servidor. En mi caso, es un servlet. 
                HttpPost post = new HttpPost("http://192.168.0.142:8080/marcoWeb/Login");
     
                 try{
                     Defino los parámetros que enviaré. Primero el nombre del parámetro, seguido por el valor. Es lo mismo que hacer un
                     http://192.168.0.142:8080/marcoWeb/Login?username=mario&pass=maritoPass&convertir=no 
                    List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
                    nvp.add(new BasicNameValuePair("username", username));
                     Encripto la contraseña en MD5. Definición más abajo 
                    nvp.add(new BasicNameValuePair("pass", toMd5(pass)));
                    nvp.add(new BasicNameValuePair("convertir", "no"));
                //   Agrego los parámetros a la petición 
                    post.setEntity(new UrlEncodedFormEntity(nvp));
                  // Ejecuto la petición, y guardo la respuesta 
                   HttpResponse respuesta = cliente.execute(post);
     
                  try{
                        // Traspaso la respuesta del servidor (que viene como JSON) a la instancia de JSONObject en job.
                         //Definición de inputStreamToString() más abajo. 
                        JSONObject job = new JSONObject(this.inputStreamToString(respuesta.getEntity().getContent()).toString());
                         Nuevo objeto, que contiene el valor para mensaje (definido en el JSON que crea el servidor 
                        JSONObject mensaje = job.getJSONObject("mensaje");
                         Muestro la respuesta 
                        Toast.makeText(Login.this, mensaje.getString("Estado"), Toast.LENGTH_LONG).show();
                       //  Abajo todas los exceptions que pueden ocurrir. Se imprimen en el log 
                        return true;
                    }catch(JSONException ex){
                        Log.w("Aviso", ex.toString());
                        return false;
                    }
                }catch(ClientProtocolException ex){
                    Log.w("ClientProtocolException", ex.toString());
                    return false;
                }catch(UnsupportedEncodingException ex){
                    Log.w("UnsupportedEncodingException", ex.toString());
                    return false;
                }catch(IOException ex){
                    Log.w("IOException", ex.toString());
                    return false;
                }
                catch(IllegalStateException ex){
                    Log.w("IOException", ex.toString());
                    return false;
                }*/
            	return false;
            }else{
//                Toast.makeText(Login.this, "Campo vacío !",
//                                Toast.LENGTH_LONG).show();
                return false;
            }
        }
    	
    	
    private StringBuilder inputStreamToString(InputStream is) {
            String line = "";
            StringBuilder total = new StringBuilder();
            //Guardamos la dirección en un buffer de lectura
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
     
            //Y la leemos toda hasta el final
            try{
                while ((line = rd.readLine()) != null) {
                    total.append(line);
                }
            }catch(IOException ex){
                Log.w("Aviso", ex.toString());
            }
     
            // Devolvemos todo lo leido
            return total;
        }

    	private String toMd5(String pass){
            try{
                //Creando Hash MD5
                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(pass.getBytes());
                byte messageDigest[] = digest.digest();
     
                //Creando Hex String
                StringBuffer hexString = new StringBuffer();
                for(int i=0; i<messageDigest.length; i++)
                    hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
                Log.w("Pass en MD5: ", hexString.toString());
                return hexString.toString();
            }catch(NoSuchAlgorithmException ex){
                Log.w("NoSuchAlgorithmException", ex.toString());
                return null;
            }
        }
 
    // Procedimiento que abre la BD: "usuarios.sqlite" en la ruta: "path", para lectura y escritura.
    public void openDatabaseUsuarios() {
    		if (myDb != null) {
    			myDb.close();
    		}
    		//Toast.makeText(this, "path: "+path, Toast.LENGTH_SHORT).show();
    		try {
    			myDb = SQLiteDatabase.openDatabase(path+"usuarios.sqlite", null, SQLiteDatabase.OPEN_READWRITE);
    			//Toast.makeText(this, "DB READY", Toast.LENGTH_SHORT).show();

    		} catch (SQLiteException sqle) {
    			appendLog(fechaLogD +  " class: DroidGap.java - PROC: openDatabaseEstimates() - No se pudo abrir o no existe la Base de Datos: estimates.sqlite - " + sqle);
    			//throw sqle;
    		}
    		appendLog(fechaLogD +  " class: DroidGap.java - PROC: openDatabaseEstimates() - Apertura exitosa de la B.D. : estimates.sqlite, para escritura/lectura");
    		//this.myDb.close();
    }
    	
 	public void appendLog(String text)
 	{       
 		TomaFechaSistema();
 		//Toast.makeText(this, "appendLogF - TomaFechaSistema: "+fechaLogD, Toast.LENGTH_LONG).show();
	    versionLog = "" + fechaLogD;
 	   ////File logFile = new File("sdcard/INMAEst_Movil/logINMAEstFotos"+versionLog+".file");
	   File logFile = new File(sPrimaryStorage+"/Download/logINMAEstLogin"+versionLog+".file");
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

package com.example.android.photobyintent;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.datumdroid.android.ocr.simple.GlobalOcr;
import com.phonegap.Global;

import ve.com.inma.mobility_4_1.api.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
//import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.Toast;

/*
 * Clase que maneja el módulo de fotografías y presentación de las mismas 
 * Implementa el servicio de cámara de Android modificado.
 * Recibe la ruta física de las fotos de Droidgap.java. Administra el número de fotos tomadas
 * @author José A. Azpurua - 2013 - Última modificación: 12 Sep. 2016 - José A. Azpurua
 * Modificación: Se inhabilita el control de número mínimo de fotos para poder continuar el Ajuste
 */
public class PhotoIntentActivity extends Activity {

	//private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final int ACTION_TAKE_PHOTO_S = 2;
	//private static final int ACTION_TAKE_VIDEO = 3;

	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private ImageView mImageView;
	private Bitmap mImageBitmap;
	private GridView gridview;
	private GridView gridview2;
    private String[] mFileStrings;
    private String[] mFileStrings2;
    private File[] listFile;
    private File[] files;
    private static int count =0;
    private File file = null;
    private File filefoto = null;
    private String sPath = "";
    private int countfotos = 0;

	private String mCurrentPhotoPath;
	private String imageFileName;
	private String value = "";
	private String nombrefotos = "";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private String imgAbsPos = null;
	public String sMensaje="";
	public String sPathCopy = "";
	public String versionLog = "";
	public String fechaLogD = "";
	public String fechaHoraLogD = "";
	private String sPrimaryStorage = "";
	private String sSecondaryStorage =  "";

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	
	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						appendLogF(" class: PhotoIntentActivity.java - PROC: getAlbumDir() - CameraSample - failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
			appendLogF(" class: PhotoIntentActivity.java - PROC: getAlbumDir() - External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}

	private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch(actionCode) {
			
		case ACTION_TAKE_PHOTO_S:
			File fi = null;
			
//			try {
//				//fi = setUpPhotoFile();
//				//mCurrentPhotoPath = fi.getAbsolutePath();
//
//				//takePictureIntent.putExtra(MediaStore.ACTION_IMAGE_CAPTURE, Uri.fromFile(fi));
//			} catch (IOException e) {
//				e.printStackTrace();
//				//fi = null;
//				//mCurrentPhotoPath = null;
//			}
			break;
			
		default:
			break;			
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}

//	private void dispatchTakeVideoIntent() {
//		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//		startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);
//	}

	private void handleSmallCameraPhoto(Intent intent) {
		Bundle extras = intent.getExtras();

		mImageBitmap = (Bitmap) extras.get("data");	// 
		
	//******************************************************************************************
		//String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String timeStamp = new SimpleDateFormat("mmss").format(new Date());
		//String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_" + JPEG_FILE_SUFFIX;
		//String imageFileName = timeStamp + JPEG_FILE_SUFFIX;
		String imageFileName = nombrefotos + timeStamp + JPEG_FILE_SUFFIX;
		//Toast.makeText(this, "handleSmallCameraPhoto ***nombrefotos***: "+nombrefotos, Toast.LENGTH_LONG).show();
		appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - nombrefotos: "+nombrefotos);
		//String imageFileName = nombrefotos + JPEG_FILE_SUFFIX;
		//Toast.makeText(this, "handleSmallCameraPhoto imageFileName: "+imageFileName, Toast.LENGTH_LONG).show();	
		//Toast.makeText(this, "value:"+value, Toast.LENGTH_LONG).show();	
		appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - imageFileName: "+imageFileName);
		try {
//			File path = Environment.getExternalStoragePublicDirectory(
//					 Environment.DIRECTORY_PICTURES);
			appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - sPath: "+sPath);
			File f = new File(sPath);
			//File f = new File(value);
			if (f.exists()) {
				files=f.listFiles();
				appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - if (f.exists()) -  files=f.listFiles()");
			}
			//Toast.makeText(this, "sPath:"+sPath, Toast.LENGTH_LONG).show();	
			filefoto = new File(sPath, imageFileName);
			//filefoto = new File(value, imageFileName);
			appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - filefoto = new File(sPath, imageFileName)");
			//List<String> tFileList = new ArrayList<String>();
			long flength = 0;
			
			for(int i=0; i<files.length; i++){
				File filetemp = files[i];
				flength = filetemp.length();
				//Toast.makeText(this, "flength:"+flength, Toast.LENGTH_LONG).show();
				appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - flength = filetemp.length(): "+flength);
				if(flength <= 120000){
					//tFileList.add(filetemp.getPath());
				}else{
					filetemp.delete();
					appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - flength > 12.000  = filetemp.delete();");
				}
			}				
			
			  int oldWidth = mImageBitmap.getWidth();
			  int oldHeight = mImageBitmap.getHeight();
			  int newWidth = 800; 
			  int newHeight = 600;	
			  
			  // Si se quiere reducir a la dimension original de la foto, descomentar la dos(2) lineas siguientes y comentar las dos (2) anteriores
//			  int newWidth = oldWidth; 
//			  int newHeight = oldHeight;				  
			  
			  float scaleWidth = ((float) newWidth) / oldWidth;
			  float scaleHeight = ((float) newHeight) / oldHeight;			  
	
			Matrix matrix = new Matrix();
			// resize the bit map
			matrix.postScale(scaleWidth, scaleHeight);
			
		     //Copy the center part only
		     int w = mImageBitmap.getWidth();
		     int h = mImageBitmap.getHeight();
			
			Bitmap _bitmapScaled = Bitmap.createBitmap(mImageBitmap, 0, 0,  oldWidth, oldHeight, matrix, true);
	
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			_bitmapScaled.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
			//Toast.makeText(this, "countfotos: "+countfotos, Toast.LENGTH_LONG).show();	
			
			//write the bytes in file
			FileOutputStream fo = new FileOutputStream(filefoto);
			fo.write(bytes.toByteArray());
	
			// remember close de FileOutput
			fo.close();		
			
			//callHereForEff();
			//if(Global.autoRestore==0){
			//*****************************************************			
			// Procedimiento para copiar las fotos desde el Directorio fuente hacia el destino (Carpeta de la Estimacion actual)
			File[] listFile;
			
	        //File filedir = new File("mnt/extSdCard/DCIM/Camera/");
	        //File filedir = new File(Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/");
	        File filedir = new File(sSecondaryStorage+"/DCIM/Camera/");
	        appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - Environment.getExternalStorageDirectory().getPath(): "+Environment.getExternalStorageDirectory().getPath());
	        appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - filedir: "+filedir);
			//String sPathCopy = "/mnt/sdcard/"+ value + "/";
			//sPathCopy = Environment.getExternalStorageDirectory().getPath() + "/" + value + "/";
			//sPathCopy = Environment.getExternalStorageDirectory().getPath() + "/" + value + "/" + ;
			//sPathCopy = sPrimaryStorage + "/" + value + "/";
	        sPathCopy = sPrimaryStorage + value;
			//Toast.makeText(this, "sPathCopy: "+sPathCopy, Toast.LENGTH_LONG).show();
			//sPathCopy = "/mnt/sdcard/"+ value + "/";
			appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - sPathCopy: "+sPathCopy);			

			List<String> list = new ArrayList<String>();
			if (filedir.isDirectory()) {
				listFile = filedir.listFiles();
				if (listFile != null && listFile.length > 0) {
					int i = 0;
					int j = 0;
					if(Global.sTipoAjuste.equals("mismo")){
						j = 10;
					}
					for (File filel : listFile) {
						File sourcefi = new File(listFile[i].getAbsolutePath());
						//Toast.makeText(this, "sourcefi: "+sourcefi, Toast.LENGTH_LONG).show();
						appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - for (File filel : listFile) File sourcefi: "+sourcefi);
						String namesource = filel.getName();
						//appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - for (File filel : listFile) namesource ANTES: "+namesource);
						//Toast.makeText(this, "namesource ANTES : "+namesource, Toast.LENGTH_LONG).show();
						if(namesource.length()-8>1){
							namesource = namesource.substring(0, namesource.length()-8);
						}
						appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - for (File filel : listFile) namesource: "+namesource);
//							Toast.makeText(this, "namesource DESPUES: "+namesource, Toast.LENGTH_LONG).show();
//							Toast.makeText(this, "nombrefotos: "+nombrefotos, Toast.LENGTH_LONG).show();
						if(namesource.equals(nombrefotos)){
							//Toast.makeText(this, "Exitooo: "+i, Toast.LENGTH_LONG).show();
							String name = "foto"+j+".jpg";
							File targetfi = new File(sPathCopy+name);
							appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - for (File filel : listFile) targetfi: "+targetfi);
							CopyFotos(sourcefi, targetfi);
							//sourcefi.delete();
//								if(sourcefi.exists()) {
//									sourcefi.delete();
//									appendLogF(" Class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - Se elimino exitosamente el archivo: "+sourcefi);
//									//Toast.makeText(this, "sourcefi Borrado", Toast.LENGTH_LONG).show();
//								}  
							j++;
						}
//						else{
//							if(sourcefi.exists()) {
//									sourcefi.delete();
//									appendLogF(" Class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - Se elimino exitosamente el archivo: "+sourcefi);
//							}  							
//						}

						i++;
					}
				}
				appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - Despues de CopyFotos(sourcefi, targetfi);  - Llamada a callHereForEff();");
				callHereForEff();
//				int k = 0;
//				for (File filel : listFile) {
//					File deletefi = new File(listFile[k].getAbsolutePath());
//					deletefi.delete();
//					k++;
//				}
//	            for (int i = 0; i < listFile.length; i++) {
//					File targetfi = new File(sPathCopy+list(i));
//					CopyFotos(sourcefi, targetfi);
//	            }
			}	
			//}
	        
	    //*****************************************************
		
		} catch (IOException e) {
			 Log.w("ExternalStorage", "Error writing ", e);
			 appendLogF(" class: PhotoIntentActivity.java - PROC: handleSmallCameraPhoto() - ExternalStorage - Error writing");
		}			
	//******************************************************************************************
	
	}
	
	ImageButton imageButton;
	ImageButton imageButton2;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainfoto);
		//appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - ENTRANDO Global.sTipoAjuste: "+Global.sTipoAjuste);	
		Bundle extras = getIntent().getExtras();
		
		//**boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		//Toast.makeText(this, "isSDPresent: "+isSDPresent, Toast.LENGTH_LONG).show();
		//**if(isSDPresent){
			//**	sPrimaryStorage = System.getenv("EXTERNAL_STORAGE");
			//**	sSecondaryStorage = System.getenv("SECONDARY_STORAGE");
			//**	String[] tempS = sSecondaryStorage.split(":");
			//**	sSecondaryStorage=tempS[0];
			//**}else{
			sPrimaryStorage = "/mnt/sdcard/";
			//sSecondaryStorage = "/mnt/extSdCard/";
			sSecondaryStorage = "/mnt/sdcard/";
		//**}		
		appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - sPrimaryStorage: "+sPrimaryStorage);
		appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - sSecondaryStorage: "+sSecondaryStorage);
		
		//appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - ENTRANDO DOS Global.sTipoAjuste: "+Global.sTipoAjuste);
		if (extras != null) {
		    //value = extras.getString("NAME_FOLDER");
			//appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - ENTRANDO TRES (Dentro del if) Global.sTipoAjuste: "+Global.sTipoAjuste);
			int iautores = Global.autoRestore;
			
			if(iautores==1){
				value = extras.getString("value");
				//value = Global.rutaAjusteG;
				//Toast.makeText(this, "PhotoActivity if(iautores){value: "+value, Toast.LENGTH_LONG).show();
				appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - if(iautores==1) if (extras != null) value: "+value);
				//Global.autoRestore=0;
				
			    try
			    {
			        //File root = new File(Environment.getExternalStorageDirectory(), value);
			        File root = new File(sPrimaryStorage, value);
			        if (!root.exists()) {
			            root.mkdirs();
			        }
			    }
			    catch(Exception e)
			    {
			         e.printStackTrace();
			         e.getMessage();
			    }
				
			}else{
				if(Global.sTipoAjuste.equals("mismo")){
					
					//Toast.makeText(this, "if(Global.sTipoAjuste.equals(mismo)){ : "+GlobalOcr.rutaFotos, Toast.LENGTH_LONG).show();
					appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() sTipoAjuste.equals(mismo) - GlobalOcr.rutaFotos: "+GlobalOcr.rutaFotos);					
					//Toast.makeText(this, "if(Global.sTipoAjuste.equals(mismo)){ : Global.rutaAjusteG"+Global.rutaAjusteG, Toast.LENGTH_LONG).show();
					appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() sTipoAjuste.equals(mismo) - Global.rutaAjusteG: "+Global.rutaAjusteG);							
					
					File filedirF = new File(sPrimaryStorage, Global.rutaAjusteG);
					Global.rutaAjusteG = Global.rutaAjusteGMismo;
					value = Global.rutaAjusteG;
					appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() sTipoAjuste.equals(mismo) - Despues rutaAjusteGMismo-->Global.rutaAjusteG: "+Global.rutaAjusteG);	
				    try
				    {
				        //File root = new File(sPrimaryStorage, GlobalOcr.rutaFotos);
				    	File root = new File(sPrimaryStorage, Global.rutaAjusteG);
				        if (!root.exists()) {
				            root.mkdirs();
				        }
				    }
				    catch(Exception e)
				    {
				         e.printStackTrace();
				         e.getMessage();
				    }					
					
					List<String> list = new ArrayList<String>();
					
					if (filedirF.isDirectory()) {
						listFile = filedirF.listFiles();
						if (listFile != null && listFile.length > 0) {
							int i = 0;
							int j = 0;
							for (File filel : listFile) {
								File sourcefiF = new File(listFile[i].getAbsolutePath());
								//Toast.makeText(this, "sourcefi: "+sourcefi, Toast.LENGTH_LONG).show();
								appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() sTipoAjuste.equals(mismo) - for (File filel : listFile) File sourcefi: "+sourcefiF);
								String namesource = filel.getName();
								String name = "foto"+j+".jpg";
								if(namesource.equals(name) && j<10){
									//Toast.makeText(this, "Exitooo: "+i, Toast.LENGTH_LONG).show();
									
									File targetfiF = new File(sPrimaryStorage + Global.rutaAjusteG+name);
									appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() sTipoAjuste.equals(mismo) - for (File filel : listFile) targetfi: "+targetfiF);
									try {
										CopyFotos(sourcefiF, targetfiF);
									} catch (FileNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									j++;
								}

								i++;
							}
						}
						
					}								

					nombrefotos = Global.mismoFolder;
					GlobalOcr.rutaFotos = value;
					appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - if(Global.sTipoAjuste.equals(mismo)){ value = Global.rutaAjusteG=GlobalOcr.rutaFotos: "+value);			
					
				}else{
					//	- PROC: onCreate() - *** Global.sTipoAjuste.equals("nuevo")  ****
					value = "/INMAEst_Movil/";
					//value = Environment.getExternalStorageDirectory().getPath() + "/INMAEst_Movil/" + value;
					value = value + extras.getString("FOLDER_YEAR");
					value = value + "/" + extras.getString("FOLDER_MONTH");
					value = value + "/" + extras.getString("FOLDER_DAY");
					//value = value + "/" + extras.getString("FOLDER_PLATE");
					value = value + "/" + extras.getString("FOLDER_VEHIDNUM");
					//Global.rutaAjusteG = value;
					value = value + "/" + extras.getString("FOLDER_HORMINSEG");
					value = value + "/";
					GlobalOcr.rutaFotos = sPrimaryStorage + value;
					//value = value + "/" + extras.getString("FOLDER_ESTIMATE");
					//nombrefotos = Global.estimateId;
					nombrefotos = Global.mismoFolder;
					
				    try
				    {
				        //File root = new File(Environment.getExternalStorageDirectory(), value);
				        File root = new File(sPrimaryStorage, value);
				        if (!root.exists()) {
				            root.mkdirs();
				        }
				    }
				    catch(Exception e)
				    {
				         e.printStackTrace();
				         e.getMessage();
				    }					
					
				}
			}		
			appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - value FINAL: "+value);
			
//			Toast.makeText(this, "Comienzo nombrefotos: "+nombrefotos, Toast.LENGTH_LONG).show();
//			Toast.makeText(this, "PhotoActivity value: "+value, Toast.LENGTH_LONG).show();
			appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - Comienzo nombrefotos: "+nombrefotos);
			appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - Global.mismoFolder: "+Global.mismoFolder);
			appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - GlobalOcr.rutaFotos: "+GlobalOcr.rutaFotos);
//			Toast.makeText(this, "Comienzo nombrefotos: "+nombrefotos, Toast.LENGTH_LONG).show();
			//countfotos = extras.getString("COUNT_FOTOS");
			this.countfotos = Global.ivar1;
			//Global.sTipoAjuste=sAjustetipo;
		}		
		//Toast.makeText(this, "value: "+value, Toast.LENGTH_LONG).show();
		
		appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - DESPUES sSEcondaryStorage Global.sTipoAjuste: "+Global.sTipoAjuste);
		sPath = sSecondaryStorage+"/DCIM/Camera/";
	    
	    //*********************************************************************	    
	    
	    TextView text1 = (TextView) findViewById(R.id.textView1);
	    TextView text2 = (TextView) findViewById(R.id.textView2);
	    //TextView text3 = (TextView) findViewById(R.id.textView3);
	    //TextView text4 = (TextView) findViewById(R.id.textView4);	    

		mImageBitmap = null;	
		
		imageButton = (ImageButton) findViewById(R.id.imageButton1);
 
		imageButton.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				dispatchTakePictureIntent(ACTION_TAKE_PHOTO_S);
			}
 
		});		
		
		imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
 
		imageButton2.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {			
//				if(countfotos<5){
//					TextView t1 = (TextView) findViewById(R.id.textView1);
//					t1.setText("¡El minimo de fotos para un ajuste es de cinco (5)!");
//					t1.setVisibility(View.VISIBLE);
//					return;
				//}else{
					TextView t2 = (TextView) findViewById(R.id.textView1);
					t2.setVisibility(View.INVISIBLE);
					Global.svar2="OK";
				//}
				Global.ivar1=countfotos;
				appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - *** SALIDA ***Global.ivar1=countfotos: "+countfotos);
				//DeleteIniFotos();
				File f = new File(sSecondaryStorage+"/DCIM/Camera/");

				if (f.exists()) {
    		        if (f.isDirectory()) {;
    					File[] filesD=f.listFiles();
    					for(int i=0; i<filesD.length; i++){
    						File filetemp = filesD[i];
    							filetemp.delete();
    					}    					
    		        }
				}
								
				finish();
			}
 
		});			
//		Button picSBtn = (Button) findViewById(R.id.imageButton1);
//		setBtnListenerOrDisable( 
//				picSBtn, 
//				mTakePicSOnClickListener,
//				MediaStore.ACTION_IMAGE_CAPTURE
//		);

//		Button picEBtn = (Button) findViewById(R.id.imageButton2);
//		picEBtn.setOnClickListener(new OnClickListener() {
//			public void onClick(View arg0) {
//				if(countfotos<5){
//					TextView t1 = (TextView) findViewById(R.id.textView1);
//					t1.setVisibility(View.VISIBLE);
////					Toast.makeText(this, "El mínimo de fotos es cinco(5), para culminar el ajuste! Solo ha tomado: "+countfotos, Toast.LENGTH_LONG).show();
//					return;
//				}else{
//					TextView t2 = (TextView) findViewById(R.id.textView1);
//					t2.setVisibility(View.INVISIBLE);
//				}
//				Global.ivar1=countfotos;
//				finish();
//			}
//		});
		
	    //gridview = (GridView) findViewById(R.id.gridview);
	    gridview2 = (GridView) findViewById(R.id.gridview2);
//	    final GridAdapter gridadapter = new GridAdapter(this);
//	    gridview.setAdapter(gridadapter);
//	    
//	    gridview.setOnItemClickListener(new OnItemClickListener() {
//	      public void onItemClick(AdapterView<?> parent, View v, 
//	                              int position, long id) {
//	         Intent i = new Intent(Intent.ACTION_VIEW);
//	         i.setData(Uri.parse(((ImageWithUrl) gridadapter.getItem(position)).getImageUrlString()));
//	         startActivity(i);
//	      }
//	    });	    
	    
        //file = new File("mnt/extSdCard/DCIM/Camera/");
        //file = new File("/mnt/sdcard/"+value);
        //file = new File(Environment.getExternalStorageDirectory(), value);		
       //appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - *** SALIDA ***Global.ivar1=countfotos: "+countfotos);
        
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
//		Toast.makeText(this, "Global.sTipoAjuste: "+Global.sTipoAjuste, Toast.LENGTH_LONG).show();
//		Toast.makeText(this, "Global.autoRestore: "+Global.autoRestore, Toast.LENGTH_LONG).show();
//		Toast.makeText(this, "Global.ivar1: "+Global.ivar1, Toast.LENGTH_LONG).show();
		
        if(Global.autoRestore==1){
        	file = new File(sPrimaryStorage, value);
        	//file = new File(Environment.getExternalStorageDirectory(), value);
            if (file.isDirectory()) {
                listFile = file.listFiles();
                //Arrays.sort(listFile);
                //Arrays.sort(listFile, Arrays.ASCENDING);
                if(listFile.length>0){
                	sortByNumber(listFile);
                }
                
                //Toast.makeText(this, "listFile.length: "+listFile.length, Toast.LENGTH_LONG).show();
                appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - if(Global.autoRestore==1) listFile.length: "+listFile.length);
                mFileStrings = new String[listFile.length];
                mFileStrings2 = new String[listFile.length];
                countfotos = listFile.length;
                for (int i = 0; i < listFile.length; i++) {
                	if(i>5){
                		//Toast.makeText(this, "if(i>9){ i: "+i, Toast.LENGTH_LONG).show();
                		appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - if(Global.autoRestore==1) / if(i>9) i: "+i);
                		mFileStrings2[i] = listFile[i].getAbsolutePath();
                	}else{
                		//Toast.makeText(this, "}else{ i: "+i, Toast.LENGTH_LONG).show();
                		appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - if(Global.autoRestore==1) / }else{ i: "+i);
                		mFileStrings[i] = listFile[i].getAbsolutePath();
            		}
                }
            } 
        	
        	gridview2.setAdapter(new ImageAdapter2(this, mFileStrings));
             final ImageAdapter2 im2 = new ImageAdapter2(this, mFileStrings);
             im2.notifyDataSetChanged();        	
        	if(countfotos>5){
				gridview.setAdapter(new ImageAdapter(this, mFileStrings2));
				final ImageAdapter im = new ImageAdapter(this, mFileStrings2);
				im.notifyDataSetChanged();
        	}
            

            //if(countfotos>0){
    			TextView text0 = (TextView) findViewById(R.id.textView2);
    			text0.setVisibility(View.VISIBLE);  			
            //}
//            if(countfotos>5){
//    			TextView text00 = (TextView) findViewById(R.id.textView4);
//    			text00.setVisibility(View.VISIBLE);  			
//            }             
            
            //Global.autoRestore=0;
        }else{
        	if(Global.sTipoAjuste.equals("mismo")){
        		//Toast.makeText(this, "Dentro de Global.sTipoAjuste.equals(mismo): "+value, Toast.LENGTH_LONG).show();
        		//Environment.getExternalStorageDirectory().getPath() + "/" + value + "/"
            	//file = new File(Environment.getExternalStorageDirectory(), value);
            	file = new File(sPrimaryStorage, value);
                if (file.isDirectory()) {
                    listFile = file.listFiles();
                    if(listFile.length>0){
                    	sortByNumber(listFile);
                    }
                    //Arrays.sort(listFile);
                    //Arrays.sort(listFile, Arrays.ASCENDING);
                    //Toast.makeText(this, "listFile.length: "+listFile.length, Toast.LENGTH_LONG).show();
                    appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - if(Global.sTipoAjuste.equals(mismo)) listFile.length: "+listFile.length);
                    mFileStrings = new String[listFile.length];
                    mFileStrings2 = new String[listFile.length];

                    for (int i = 0; i < listFile.length; i++) {
                    	if(i>9){
                    		//Toast.makeText(this, "if(i>4){ i: "+i, Toast.LENGTH_LONG).show();
                    		appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - if(Global.sTipoAjuste.equals(mismo)) / if(i>9) i: "+i);
                    		mFileStrings2[i] = listFile[i].getAbsolutePath();
                    	}else{
                    		//Toast.makeText(this, "}else{ i: "+i, Toast.LENGTH_LONG).show();
                    		appendLogF(" class: PhotoIntentActivity.java - PROC: onCreate() - if(Global.sTipoAjuste.equals(mismo)) / }else{ i: "+i);
                    		mFileStrings[i] = listFile[i].getAbsolutePath();
                		}
                    }
                }
              
            	gridview2.setAdapter(new ImageAdapter2(this, mFileStrings));
                 final ImageAdapter2 im2 = new ImageAdapter2(this, mFileStrings);
                 im2.notifyDataSetChanged();        	
            	if(countfotos>5){
    				gridview.setAdapter(new ImageAdapter(this, mFileStrings2));
    				final ImageAdapter im = new ImageAdapter(this, mFileStrings2);
    				im.notifyDataSetChanged();
            	}
                
                //if(countfotos>0){
        			TextView text0 = (TextView) findViewById(R.id.textView2);
        			text0.setVisibility(View.VISIBLE);  			
                //}
//                if(countfotos>5){
//        			TextView text00 = (TextView) findViewById(R.id.textView4);
//        			text00.setVisibility(View.VISIBLE);  			
//                }                 
                
        	}
        }
        
        
        
	}	
	
    protected void callHereForEff() {
    	//Toast.makeText(this, "callHereForEff value: "+value, Toast.LENGTH_LONG).show();
    	appendLogF(" class: PhotoIntentActivity.java - PROC: callHereForEff() - value: "+value);
    	//file = new File("mnt/extSdCard/DCIM/Camera/");
    	//file = new File("/mnt/sdcard/"+value);
    	//String file1 = Environment.getExternalStorageDirectory().getPath()+value;
    	//Toast.makeText(this, "file1: "+file1, Toast.LENGTH_LONG).show();
    	////file = new File(Environment.getExternalStorageDirectory(), value);
    	file = new File(sPrimaryStorage, value);
    	
        if (file.isDirectory()) {
            listFile = file.listFiles();
            //Arrays.sort(listFile);
            //Arrays.sort(listFile, Arrays.ASCENDING);
            if(listFile.length>0){
            	sortByNumber(listFile);
            }
            mFileStrings = new String[listFile.length];
            //mFileStrings2 = new String[listFile.length];

            for (int i = 0; i < listFile.length; i++) {
//            	if(i>4){
//            		mFileStrings2[i] = listFile[i].getAbsolutePath();
//            		appendLogF(" class: PhotoIntentActivity.java - PROC: callHereForEff() - mFileStrings2[i] = listFile[i].getAbsolutePath(): "+mFileStrings2[i]);
//            	}else{
            		mFileStrings[i] = listFile[i].getAbsolutePath();
            		appendLogF(" class: PhotoIntentActivity.java - PROC: callHereForEff() - mFileStrings[i] = listFile[i].getAbsolutePath(): "+mFileStrings[i]);
//        		}
            }
        }    

        countfotos = listFile.length;
       // if(countfotos>0){
			TextView text2 = (TextView) findViewById(R.id.textView2);
			text2.setVisibility(View.VISIBLE); 
			TextView text1 = (TextView) findViewById(R.id.textView1);
			text1.setText("                     Fotos tomadas:   "+countfotos+"                         ");
			text1.setVisibility(View.VISIBLE);

//			TextView text3 = (TextView) findViewById(R.id.textView3);
//			text3.setVisibility(View.VISIBLE);  			
        //}
//        if(countfotos>5){
//			TextView text4 = (TextView) findViewById(R.id.textView4);
//			text4.setVisibility(View.VISIBLE);  			
//        }        
        //Toast.makeText(this, "countfotos: "+countfotos, Toast.LENGTH_LONG).show();
        appendLogF(" class: PhotoIntentActivity.java - PROC: callHereForEff() - countfotos: "+countfotos);
        //if(count==1)
        //{
        	//Toast.makeText(this, "Que mierda: "+count, Toast.LENGTH_LONG).show();
            //ImageAdapter obj1 = new ImageAdapter(this);
            //obj1.notifyDataSetChanged();
        //Toast.makeText(this, "countfotos: "+countfotos, Toast.LENGTH_LONG).show();
        if(countfotos>0){
//            if(countfotos > 5){
//               	gridview.setAdapter(new ImageAdapter(this, mFileStrings2));
//                final ImageAdapter im = new ImageAdapter(this, mFileStrings2);
//                im.notifyDataSetChanged();
//                appendLogF(" class: PhotoIntentActivity.java - PROC: callHereForEff() - if(countfotos > 5) ");
//            }else{
            	gridview2.setAdapter(new ImageAdapter2(this, mFileStrings));
                 final ImageAdapter2 im2 = new ImageAdapter2(this, mFileStrings);
                 im2.notifyDataSetChanged();
                 appendLogF(" class: PhotoIntentActivity.java - PROC: callHereForEff() - countfotos Mayor a cero: "+countfotos + " - im2.notifyDataSetChanged()");
                 //appendLogF(" class: PhotoIntentActivity.java - PROC: callHereForEff() - if(countfotos <= 10) ");
         //   }
        }else{
            //Toast.makeText(this, "countfotos igual a cero: "+countfotos, Toast.LENGTH_LONG).show();
            appendLogF(" class: PhotoIntentActivity.java - PROC: callHereForEff() - countfotos igual a cero: "+countfotos);
        }

        //}
        
        //count=1;

//        else
//        {
//        //ImageAdapter obj = new ImageAdapter(this);
//        //obj.notifyDataSetChanged();
//
//        gridview.setAdapter(new ImageAdapter(this, mFileStrings));//Load the GridView
//        final ImageAdapter im = new ImageAdapter(this, mFileStrings);
//        im.notifyDataSetChanged();
//        }
        //grid.setAdapter(adapter);
    }	
	
	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private String[] data;
		
		public ImageAdapter(Context c, String[] d) {
			mContext = c;
			data=d;
		}

		public int getCount() {
			return data.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		// Create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
		       ImageView imageView;
		        if (convertView == null) {
		            imageView = new ImageView(mContext);
		            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		        } else {
		            imageView = (ImageView) convertView;
		            
		            imageView.setScaleType(ScaleType.MATRIX);
		        }

		        File imgFile = new File("" + data[position]);
		        //imgAbsPos = imgFile.getAbsolutePath();
		        
		        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		        //Bitmap myBitmap = BitmapFactory.decodeFile("mnt/sdcard/"+value);
		        imageView.setImageBitmap(myBitmap);
		        imageView.setLayoutParams(new GridView.LayoutParams(150,150)); //dimension in px
		        return imageView;
		}		
		
	} 	
	
	public class ImageAdapter2 extends BaseAdapter {
		private Context mContext2;
		private String[] data;	
		public ImageAdapter2(Context c, String[] d) {
			mContext2 = c;
			data=d;
		}
	
		public int getCount() {
			return data.length;
		}
	
		public Object getItem(int position) {
			return position;
		}
	
		public long getItemId(int position) {
			return position;
		}
	
		// Create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			   ImageView imageView;
				if (convertView == null) {
					imageView = new ImageView(mContext2);
					imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				} else {
					imageView = (ImageView) convertView;
					
					imageView.setScaleType(ScaleType.MATRIX);
				}
	
				File imgFile = new File("" + data[position]);
				//imgAbsPos = imgFile.getAbsolutePath();
				appendLogF(" class: PhotoIntentActivity.java - PROC: View getView() - position: "+position);
				appendLogF(" class: PhotoIntentActivity.java - PROC: View getView() - imgFile.getAbsolutePath: "+imgFile.getAbsolutePath());
				
				Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				//Bitmap myBitmap = BitmapFactory.decodeFile("mnt/sdcard/"+value);
				imageView.setImageBitmap(myBitmap);
				imageView.setLayoutParams(new GridView.LayoutParams(150,150)); //dimension in px
				return imageView;
		}		
	
}	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//requestCode=ACTION_TAKE_PHOTO_B;
		switch (requestCode) {
	//		case ACTION_TAKE_PHOTO_B: {
	//			if (resultCode == RESULT_OK) {
	//				handleBigCameraPhoto();
	//			}
	//			break;
	//		} // ACTION_TAKE_PHOTO_B
	
			case ACTION_TAKE_PHOTO_S: {
				if (resultCode == RESULT_OK) {
					handleSmallCameraPhoto(data);
					appendLogF(" class: PhotoIntentActivity.java - PROC: onActivityResult() - case ACTION_TAKE_PHOTO_S - handleSmallCameraPhoto(data); ");
				}
				break;
			} // ACTION_TAKE_PHOTO_S

		} // switch
	}
	
	
	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//Toast.makeText(this, "onSaveInstanceState", Toast.LENGTH_LONG).show();
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		//outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
		//outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null) );
		super.onSaveInstanceState(outState);
		appendLogF(" class: PhotoIntentActivity.java - PROC: onSaveInstanceState() - Despues de super.onSaveInstanceState(outState); - outState: "+outState + " - Llamada a callHereForEff();");
		 callHereForEff();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
		//mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
//		mImageView.setImageBitmap(mImageBitmap);
//		mImageView.setVisibility(
//				savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
//						ImageView.VISIBLE : ImageView.INVISIBLE
//		);
//		mVideoView.setVideoURI(mVideoUri);
//		mVideoView.setVisibility(
//				savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ? 
//						ImageView.VISIBLE : ImageView.INVISIBLE
//		);
		//Toast.makeText(this, "onRestoreInstanceState", Toast.LENGTH_LONG).show();
		appendLogF(" class: PhotoIntentActivity.java - PROC: onRestoreInstanceState() - Despues de mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);  - Llamada a callHereForEff();");
		 callHereForEff();
	}

	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
			packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	private void setBtnListenerOrDisable( 
			Button btn, 
			Button.OnClickListener onClickListener,
			String intentName
	) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);        	
		} else {
			btn.setText( 
				getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}
	
	

    public void CopyFotos(File sourceLocation, File targetLocation) 		
    throws FileNotFoundException, IOException 
    {
    	//Toast.makeText(this, "sourceLocation: "+sourceLocation, Toast.LENGTH_LONG).show();
    	//Toast.makeText(this, "targetLocation: "+targetLocation, Toast.LENGTH_LONG).show();
    	appendLogF(" class: PhotoIntentActivity.java - PROC: CopyFotos() - sourceLocation: "+sourceLocation);
    	appendLogF(" class: PhotoIntentActivity.java - PROC: CopyFotos() - targetLocation: "+targetLocation);
//		String destDirPath = targetLocation.getParent();
//		File destDir = new File(destDirPath);
//		if(!destDir.exists()){
//			destDir.mkdirs();
//      }

        InputStream in = new FileInputStream(sourceLocation);
        OutputStream out = new FileOutputStream(targetLocation);

        // Copy the bits from instream to outstream
        byte[] buf = new byte[1024*512];
        int len;
        while ((len = in.read(buf)) > 0) {
        	//Toast.makeText(this, "in.read(buf) len: "+len, Toast.LENGTH_LONG).show();
            System.out.println("papa");
            if(len>0){
				out.write(buf, 0, len);
				System.out.println(">");
            }
        }
        System.out.println(".");
        in.close();
        out.close();
    }

    
    @Override
     public boolean onKeyDown(int keyCode, KeyEvent event) {
		
       if (keyCode == KeyEvent.KEYCODE_BACK) {
     	  
     	  //Ask the user if they want to quit
//     	  if (countfotos < 5){
//     		  sMensaje="No ha tomado el minimo de fotos! Esta seguro de salir de InmaEstMobile?, La información no será guardada!";
//     		 appendLogF(" class: PhotoIntentActivity.java - PROC: onKeyDown() - if (keyCode == KeyEvent.KEYCODE_BACK) - if (countfotos < 5)");
//     	  }else{
     		  sMensaje="Salida ANORMAL del Módulo de Fotos!";
     		  Global.svar2="OK";
     		 Global.autoRestore=0;
     		appendLogF(" class: PhotoIntentActivity.java - PROC: onKeyDown() - if (keyCode == KeyEvent.KEYCODE_BACK) - if (countfotos >= 5)");
 //    	  }
         new AlertDialog.Builder(this)
           .setIcon(android.R.drawable.ic_dialog_alert)
           .setTitle("Salir del Módulo de Fotos")
           .setMessage(sMensaje)
           .setNegativeButton(android.R.string.cancel, null)
           .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {		
			  
             @Override
             public void onClick(DialogInterface dialog, int which){
             	Global.ivar1=countfotos;
             	Global.autoRestore=0;
             	appendLogF(" class: PhotoIntentActivity.java - PROC: onKeyDown() - if (keyCode == KeyEvent.KEYCODE_BACK) onClick() - Salida ANORMAL [Esc] del Módulo de Fotos!");
             	
				File f = new File(sSecondaryStorage+"/DCIM/Camera/");

				if (f.exists()) {
    		        if (f.isDirectory()) {;
    					File[] filesD=f.listFiles();
    					for(int i=0; i<filesD.length; i++){
    						File filetemp = filesD[i];
    							filetemp.delete();
    					}    					
    		        }
				}
             	finish();
             }
           })
           .show();

         // Say that we've consumed the event
         return true;
       }			  
		  
       return super.onKeyDown(keyCode, event);
     } 
    
 	public void appendLogF(String text)
 	{       
 		TomaFechaSistema();
 		
//	    versionLog = ""+cal1.get(Calendar.DATE)+cal1.get(Calendar.MONTH)
//		+cal1.get(Calendar.YEAR);
 		//Toast.makeText(this, "appendLogF - TomaFechaSistema: "+fechaLogD, Toast.LENGTH_LONG).show();
	    versionLog = "" + fechaLogD;
 	   ////File logFile = new File("sdcard/INMAEst_Movil/logINMAEstFotos"+versionLog+".file");
	   File logFile = new File(sPrimaryStorage+"/INMAEst_Movil/logINMAEstFotos"+versionLog+".file");
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
    
    public void DeleteIniFotos() {
		File filedir = new File(sSecondaryStorage+"/DCIM/Camera/");
		
		List<String> list = new ArrayList<String>();
		if (filedir.isDirectory()) {
			listFile = filedir.listFiles();					
			
			if (listFile != null && listFile.length > 0) {
				int i = 0;
				for (File filel : listFile) {
					File sourcefi = new File(listFile[i].getAbsolutePath());
					if(sourcefi.exists()) {
						sourcefi.delete();
						appendLogF(" Class: PhotoIntentActivity.java - PROC: DeleteIniFotos() - Se elimino exitosamente el archivo: "+sourcefi);
						//Toast.makeText(this, "sourcefi Borrado", Toast.LENGTH_LONG).show();
					} 							
					i++;
				}
				//Toast.makeText(this, "Fotos Originales Borradas!", Toast.LENGTH_LONG).show();
			}
		}
    }
    
    public void sortByNumber(File[] listFile2) {
        Arrays.sort(listFile2, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int n1 = extractNumber(o1.getName());
                int n2 = extractNumber(o2.getName());
                return n1 - n2;
            }

            private int extractNumber(String name) {
                int i = 0;
                try {
                    //int s = name.indexOf('_')+1;
                    //int e = name.lastIndexOf('.');
					//int e = name.length()-1;
                    String number = name.substring(3, 4);
                    i = Integer.parseInt(number);
                } catch(Exception e) {
                    i = 0; // if filename does not match the format
                           // then default to 0
                }
                return i;
            }
        });

//        for(File f : files) {
//            System.out.println(f.getName());
//        }
    }    

}	
	

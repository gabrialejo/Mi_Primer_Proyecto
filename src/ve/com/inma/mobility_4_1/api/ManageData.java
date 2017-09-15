package ve.com.inma.mobility_4_1.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.json.JSONException;
import org.json.JSONObject;

import ve.com.inma.mobility_4_1.api.util.FileUtil;

/**
 * Simplified import/export DB activity.
 * 
 * @author ccollins
 *
 */
public class ManageData extends Activity {

   private MyApplication application;

   private Button exportDbToSdButton;
   private Button importDbFromSdButton;
   private Button clearDbButton;

   @Override
   public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      application = (MyApplication) getApplication();

      setContentView(R.layout.managedata);

      exportDbToSdButton = (Button) findViewById(R.id.exportdbtosdbutton);
      exportDbToSdButton.setOnClickListener(new OnClickListener() {
         public void onClick(final View v) {
            Log.i(Main.LOG_TAG, "exporting database to external storage");
            new AlertDialog.Builder(ManageData.this).setMessage(
                     "Are you sure (this will overwrite any existing backup data)?").setPositiveButton("Yes",
                     new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                           if (isExternalStorageAvail()) {
                              Log.i(Main.LOG_TAG, "importing database from external storage, and resetting database");
                              new ExportDatabaseTask().execute();
                              ManageData.this.startActivity(new Intent(ManageData.this, Soa.class));
                           } else {
                              Toast.makeText(ManageData.this,
                                       "External storage is not available, unable to export data.", Toast.LENGTH_SHORT)
                                       .show();
                           }
                        }
                     }).setNegativeButton("No", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface arg0, int arg1) {
               }
            }).show();
         }
      });

      importDbFromSdButton = (Button) findViewById(R.id.importdbfromsdbutton);
      importDbFromSdButton.setOnClickListener(new OnClickListener() {
         public void onClick(final View v) {
            new AlertDialog.Builder(ManageData.this).setMessage(
                     "Are you sure (this will overwrite existing current data)?").setPositiveButton("Yes",
                     new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                           if (isExternalStorageAvail()) {
                              Log.i(Main.LOG_TAG, "importing database from external storage, and resetting database");
                              new ImportDatabaseTask().execute();
                              // sleep momentarily so that database reset stuff has time to take place (else Main reloads too fast)
                              SystemClock.sleep(500);
                              ManageData.this.startActivity(new Intent(ManageData.this, Soa.class));
                           } else {
                              Toast.makeText(ManageData.this,
                                       "External storage is not available, unable to export data.", Toast.LENGTH_SHORT)
                                       .show();
                           }
                        }
                     }).setNegativeButton("No", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface arg0, int arg1) {
               }
            }).show();
         }
      });

      clearDbButton = (Button) findViewById(R.id.cleardbutton);
      clearDbButton.setOnClickListener(new OnClickListener() {
         public void onClick(final View v) {
            new AlertDialog.Builder(ManageData.this).setMessage("Are you sure (this will delete all data)?")
                     .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                           Log.i(Soa.LOG_TAG, "deleting database");
                           ManageData.this.application.getDataHelper().deleteAllDataYesIAmSure();
                           ManageData.this.application.getDataHelper().resetDbConnection();
                           Toast.makeText(ManageData.this, "Data deleted", Toast.LENGTH_SHORT).show();
                           ManageData.this.startActivity(new Intent(ManageData.this, Main.class));
                        }
                     }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                     }).show();
         }
      });
   }

   private boolean isExternalStorageAvail() {
      return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
   }

   private class ExportDatabaseTask extends AsyncTask<Void, Void, Boolean> {
      private final ProgressDialog dialog = new ProgressDialog(ManageData.this);

      // can use UI thread here
      @Override
      protected void onPreExecute() {
         dialog.setMessage("Exporting database...");
         dialog.show();
      }

      // automatically done on worker thread (separate from UI thread)
      @Override
      protected Boolean doInBackground(final Void... args) {

         File dbFile = new File(Environment.getDataDirectory() + "/data/com.totsp.database/databases/inmadb.db");

         File exportDir = new File(Environment.getExternalStorageDirectory(), "databasedata");
         if (!exportDir.exists()) {
            exportDir.mkdirs();
         }
         File file = new File(exportDir, dbFile.getName());

         try {
            file.createNewFile();
            FileUtil.copyFile(dbFile, file);
            return true;
         } catch (IOException e) {
            Log.e(Soa.LOG_TAG, e.getMessage(), e);
            return false;
         }
      }

      // can use UI thread here
      @Override
      protected void onPostExecute(final Boolean success) {
         if (dialog.isShowing()) {
            dialog.dismiss();
         }
         if (success) {
            Toast.makeText(ManageData.this, "Export successful!", Toast.LENGTH_SHORT).show();
         } else {
            Toast.makeText(ManageData.this, "Export failed", Toast.LENGTH_SHORT).show();
         }
      }
   }

   public class ImportDatabaseTask extends AsyncTask<Void, Void, String> {
      private final ProgressDialog dialog = new ProgressDialog(ManageData.this);

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
	             return "Database backup file does not exist, cannot import.";
	          } else if (!dbBackupFile.canRead()) {
	             return "Database backup file exists, but is not readable, cannot import.";
	          }

	          
	          
	          File dbFile = new File(Environment.getDataDirectory() + "/data/com.totsp.database/databases/"+file.getName());
	          if (dbFile.exists()) {
	             dbFile.delete();
	          }

	          try {
	             dbFile.createNewFile();
	             dbFile.canRead();
	             dbFile.canWrite();
	             
	             FileUtil.copyFile(dbBackupFile, dbFile);
	             
	             Runtime.getRuntime().exec("chmod 777 " + dbFile);

	          } catch (IOException e) {
	             Log.e(Soa.LOG_TAG, e.getMessage(), e);
	             return e.getMessage();
	          }	    
	    	 }
/*	    	 
	    	 myDb = SQLiteDatabase.openDatabase(path+file.getName(), null, SQLiteDatabase.OPEN_READWRITE);
	    	 Cursor cursor = null;
	    	 List<String> list = new ArrayList<String>();
	    	 JSONObject array = null;
				String base64 = "";
		        String id =  "";
					cursor = myDb.rawQuery("SELECT * FROM SERVICES WHERE PATH LIKE '%IMAGE%';", null);
	 		    	if (cursor.moveToFirst()) {
	 		           do {
	 		             list.add(cursor.getString(2)); 
	 		             try {
							array = new JSONObject(cursor.getString(2));
						 } catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						 }
	 		            
						try {
							base64 = array.getString("base64");
		 		            id =  array.getString("id");
							array.remove("base64");
							array.remove("image");
							
							String query = "UPDATE SERVICES SET DATA = '"+array.toString()+"' WHERE PATH='"+cursor.getString(1)+"';";
							
							myDb.execSQL(query);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		 		         
						
						
		 		         byte[] imgBytes = (byte[]) Base64.decode(base64, 0);
		 		         FileOutputStream osf;
						try {
							 osf = new FileOutputStream(new File(Environment.getDataDirectory() + "/data/com.totsp.database/databases/"+id+".png"));
			 		         osf.write(imgBytes);
			 		         osf.flush();
			 		         
			 		         
								Bitmap bm = BitmapFactory.decodeFile(Environment.getDataDirectory() + "/data/com.totsp.database/databases/"+id+".png");
								ByteArrayOutputStream baos = new ByteArrayOutputStream();  
								bm.compress(Bitmap.CompressFormat.PNG,1, baos); //bm is the bitmap object   
								byte[] b = baos.toByteArray(); 
								
								 osf = new FileOutputStream(new File(Environment.getDataDirectory() + "/data/com.totsp.database/databases/"+id+".png"));
				 		         osf.write(b);
				 		         osf.flush();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		 		         System.out.println("SAVED TO: "+Environment.getDataDirectory() + "/data/com.totsp.database/databases/"+id+".png");

	 		           
	 		           } while (cursor.moveToNext());
	 		        }
	 		        if (cursor != null && !cursor.isClosed()) {
	 		           cursor.close();
	 		        }			    	

	 		       myDb.close();	
	    	 */
	     }
    	  return null;
 
      }

      @Override
      protected void onPostExecute(final String errMsg) {
         if (dialog.isShowing()) {
            dialog.dismiss();
         }
         if (errMsg == null) {
            Toast.makeText(ManageData.this, "Import successful!", Toast.LENGTH_SHORT).show();
         } else {
            Toast.makeText(ManageData.this, "Import failed - " + errMsg, Toast.LENGTH_SHORT).show();
         }
      }
   }
}
package ve.com.inma.mobility_4_1.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ve.com.inma.mobility_4_1.api.R;
import ve.com.inma.mobility_4_1.api.data.Book;

public class BookAdd extends Activity {

   private MyApplication application;

   private EditText title;
   private EditText authors;
   private Button insertButton;
   private WebView  browser;  
   private static final int MENU_BOOK_ADD = 0;
   private static final int MENU_MANAGE_DATA = 1;
   private static final int MENU_WEBVIEW = 2;

   @Override
   public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      //setContentView(R.layout.bookadd);
      //browser = (WebView) findViewById(R.id.webview);  
      browser.getSettings().setJavaScriptEnabled(true); 
      browser.getSettings().setDomStorageEnabled(true);
      browser.loadUrl("http://192.168.0.241/final-sandbox/sandbox/mobile/main.aspx");

      /*
      setContentView(R.layout.bookadd);

      application = (MyApplication) getApplication();

      title = (EditText) findViewById(R.id.title);
      authors = (EditText) findViewById(R.id.authors);

      insertButton = (Button) findViewById(R.id.insertbutton);
      insertButton.setOnClickListener(new OnClickListener() {
         public void onClick(final View v) {
            Toast.makeText(BookAdd.this, BookAdd.this.title.getText() + " " + BookAdd.this.authors.getText(),
                     Toast.LENGTH_LONG).show();
            Book b = new Book(BookAdd.this.title.getText().toString(), BookAdd.this.authors.getText().toString());
            BookAdd.this.application.getDataHelper().insertBook(b);
            BookAdd.this.startActivity(new Intent(BookAdd.this, Main.class));
         }
      });*/
      
   }

   @Override
   public boolean onCreateOptionsMenu(final Menu menu) {
      menu.add(0, BookAdd.MENU_BOOK_ADD, 0, "Add Book").setIcon(android.R.drawable.ic_menu_add);
      menu.add(0, BookAdd.MENU_MANAGE_DATA, 1, "Manage DB").setIcon(android.R.drawable.ic_menu_manage);
      menu.add(0, BookAdd.MENU_WEBVIEW, 2, "WEBVIEW");
      return super.onCreateOptionsMenu(menu);
   }

   
   @Override
   public boolean onOptionsItemSelected(final MenuItem item) {
      switch (item.getItemId()) {
         case MENU_BOOK_ADD:
            startActivity(new Intent(BookAdd.this, BookAdd.class));
            return true;
         case MENU_MANAGE_DATA:
            startActivity(new Intent(BookAdd.this, ManageData.class));
            return true;
         case MENU_WEBVIEW:
        	 Intent myIntent = new Intent(BookAdd.this, Main.class);  
             //startActivity(myIntent);  
             startActivityForResult(myIntent, 0);
             return true;
         default:
            return super.onOptionsItemSelected(item);
      }
   }   
   
}
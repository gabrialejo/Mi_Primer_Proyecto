package ve.com.inma.mobility_4_1.api;

import android.app.Application;

//import ve.com.inma.mobility_4_1.api.R;
import ve.com.inma.mobility_4_1.api.data.Book;
import ve.com.inma.mobility_4_1.api.data.DataHelper;

public class MyApplication extends Application {

   private DataHelper dataHelper;
   private Book selectedBook;

   @Override
   public void onCreate() {
      super.onCreate();
      //dataHelper = new DataHelper(this);
   }

   @Override
   public void onTerminate() {
      // NOTE - this method is not guaranteed to be called
      //dataHelper.cleanup();
      selectedBook = null;
      super.onTerminate();
   }

   public DataHelper getDataHelper() {
      return dataHelper;
   }

   public void setDataHelper(DataHelper dataHelper) {
      //this.dataHelper = dataHelper;
   }

   public Book getSelectedBook() {
      return selectedBook;
   }

   public void setSelectedBook(Book selectedBook) {
      this.selectedBook = selectedBook;
   }

   // so that onSaveInstanceState/onRestoreInstanceState can use with just saved title
   public void establishSelectedBook(String title) {
      //selectedBook = dataHelper.selectBook(title);
   }
}

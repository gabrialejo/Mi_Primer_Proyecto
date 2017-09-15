package ve.com.inma.mobility_4_1.api.data;

public final class Book {

   public long id;
   public String title = "";
   public String authors = "";

   public Book() {
   }

   public Book(String title, String authors) {
      this.id = 0L;
      this.title = title;
      this.authors = authors;
   }
   
   @Override
   public String toString() {
      return this.title;
   }

   public String toStringFull() {
      return this.title + " by: " + this.authors;
   }

}
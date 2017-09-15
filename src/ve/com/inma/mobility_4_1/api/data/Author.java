package ve.com.inma.mobility_4_1.api.data;

public final class Author {

   public long id;
   public String name;

   public Author() {
   }

   public Author(String name) {
      this.id = 0L;
      this.name = name;
   }
  
   @Override
   public String toString() {
      return this.name;
   }
}
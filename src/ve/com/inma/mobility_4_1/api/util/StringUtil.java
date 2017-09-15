package ve.com.inma.mobility_4_1.api.util;

public final class StringUtil {

   public static void main(String[] args) {
      String[] test = new String[] { "test1", "test2", "test3" };
      String out = StringUtil.contractComma(test);
      System.out.println("out - " + out);
   }

   public static String[] expandComma(String input) {
      return input.split(",\\s*");
   }

   public static String contractComma(String[] input) {
      String result = null;
      if (input.length == 1) {
         result = input[0];
      } else {
         int count = 0;
         for (String s : input) {
            if (count == 0) {
               result = s;
            } else {
               result += ", " + s;
            }
            count++;
         }
      }
      return result;
   }

}

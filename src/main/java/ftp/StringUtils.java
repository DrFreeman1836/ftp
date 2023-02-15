package ftp;

public class StringUtils {
  public static boolean isEmpty(String s) {
    return isEmpty(s, true);
  }

  public static boolean isEmpty(String s, Boolean doTrim) {
    return doTrim ? s == null || s.trim().length() == 0 : s == null || s.length() == 0;
  }

}

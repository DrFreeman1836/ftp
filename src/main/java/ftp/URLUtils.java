package ftp;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtils {
  public static URL constructUrl(String url, String file) throws MalformedURLException {
    return new URL(constructPath(url, file));
  }

  public static String constructPath(String url, String file) {
    return url.endsWith("/") || url.endsWith("\\") ? url + file : url + "/" + file;
  }
}

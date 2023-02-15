package ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
  private static final int BUFF_SIZE = 8192 * 16;
  /**
   * Copying maxSize bytes from src into dest.
   *
   * @param src     source stream
   * @param dest    destination stream
   * @param maxSize count of read/write bytes. If -1 then no limits
   * @return Count of readed/written bytes.
   * @throws java.io.IOException if any io error occured
   */
  public static long copy(InputStream src, OutputStream dest, long maxSize) throws IOException {
    if (maxSize == 0) return 0;
    final int packetSize = maxSize > 0 && BUFF_SIZE > maxSize ? (int) maxSize : BUFF_SIZE;
    final byte[] buf = new byte[packetSize];
    long res = 0;
    do {
      final int len = src.read(buf, 0,
          maxSize > 0 && packetSize + res > maxSize ? (int) (maxSize - res) : packetSize);
      if (len < 0)
        break;
      dest.write(buf, 0, len);
      res += len;
      if (maxSize > 0 && res >= maxSize) break;
    } while (true);
    return res;
  }

  public static void copy(String src, String dest) throws IOException {
    copy(new File(src), new File(dest));
  }

  public static void copy(File sourcefile, File destfile) throws IOException {
    copy(sourcefile, destfile, true);
  }

  public static void copy(File sourcefile, File destfile, boolean overwrite) throws IOException {
    if (destfile.exists() && !overwrite) return;

    if (!destfile.exists() && destfile.getParentFile() != null)
      destfile.getParentFile().mkdirs();

    destfile.createNewFile();

    FileOutputStream out = null;
    FileInputStream in = null;
    try {
      out = new FileOutputStream(destfile, false);
      in = new FileInputStream(sourcefile);
      copy(in, out, -1);
    } finally {
      if (out != null) out.close();
      if (in != null) in.close();
    }
  }
  public static String constructPath(String... path) {
    if (path == null || path.length == 0) return "";
    final StringBuilder fullPath = new StringBuilder();
    for (final String loc : path) {
      if (StringUtils.isEmpty(loc))
        continue;
      if (fullPath.length() == 0) {
        fullPath.append(loc);
        continue;
      }
      final char c = fullPath.charAt(fullPath.length() - 1);
      final char c1 = loc.charAt(0);
      if (c != '\\' && c != '/' && c1 != '\\' && c1 != '/')
        fullPath.append('/');
      fullPath.append(loc);
    }
    return fullPath.toString();
  }
}

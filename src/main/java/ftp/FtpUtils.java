package ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.net.URL;

public class FtpUtils {
  public static FTPFile[] listFiles(String path, final String host, String user, String password) throws IOException {
    final FTPClient c = connect(host, user, password);
    return listFiles(c, path);
  }

  public static FTPFile[] listFiles(FTPClient c, String path) throws IOException {
    c.enterLocalPassiveMode();
    final FTPFile[] files = c.listFiles(path);
    return files;
  }

  public static FTPClient connect(String ftpUrl, String user, String password) throws IOException {
    final URL url = new URL(ftpUrl);
    final String host = url.getHost();
    int port = url.getPort();
    if (port < 0) port = FTP.DEFAULT_PORT;
    final FTPClient c = new FTPClient();
    final FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
    c.setRemoteVerificationEnabled(false);

    c.configure(conf);
    c.setConnectTimeout(5 * 60 * 1000);
    c.setDataTimeout(60 * 1000);
    c.connect(host, port);
    if (!c.login(user, password))
      throw new RuntimeException("Can't login on ftp server " + host + " with user name '" + user + "'.");
    return c;
  }
}

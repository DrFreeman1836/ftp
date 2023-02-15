package ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.FileHandler;
import java.util.zip.GZIPOutputStream;

public class FtpLoader {
  private String ftpUrl;
  private FTPClient client;
  private String startingPath;
  private Logger logger;

  public FtpLoader(String ftpUrl, String user, String password) throws IOException {
    this.ftpUrl = ftpUrl.trim();
    final URL url = new URL(ftpUrl);
    startingPath = url.getPath();
    client = FtpUtils.connect(ftpUrl, user, password);
  }

  protected void closeStream(InputStream is) throws IOException {
    is.close();
    if (!client.completePendingCommand()) throw new RuntimeException("Failed loading version.properties file from ftp.");
  }

  protected InputStream open(String filePath) throws IOException {
    client.enterLocalPassiveMode();
    return client.retrieveFileStream(filePath);
  }

  protected String getStartingPath() {return startingPath;}

  protected URL getServerPath(String fileName) throws MalformedURLException {
    return StringUtils.isEmpty(fileName) ? new URL(ftpUrl) : URLUtils.constructUrl(ftpUrl, fileName);
  }

  protected FileHandler[] listFiles(String path) throws IOException {
    FTPFile[] ftpf = FtpUtils.listFiles(client, path);
    FileHandler[] files = new FileHandler[ftpf.length];
    for (int i = 0; i < ftpf.length; i++)
      files[i] = new FileHandler(ftpf[i].getName(), ftpf[i].isDirectory());
    return files;
  }

  public boolean sendReport(String reportName, String fileName) throws IOException {
    final String serverPath = FileUtils.constructPath(getStartingPath(), reportName);
    final OutputStream os = openForWrite(serverPath + ".gz", true);
    if (os == null) throw new RuntimeException("Can't write file " + serverPath + " to " + getServerPath(null));
    try {
      final InputStream is = StringUtils.isEmpty(fileName) ? new ByteArrayInputStream(reportName.getBytes()) : new FileInputStream(fileName);
      try {
        final GZIPOutputStream gzOs = new GZIPOutputStream(os);
        FileUtils.copy(is, gzOs, -1);
        gzOs.finish();
        gzOs.close();
      } finally {
        is.close();
      }
    } finally {
      os.close();
    }
    return endWriting();
  }

  private boolean isAsciiFile(String file) {return file.endsWith(".sh");}

  protected boolean endWriting() throws IOException {
    return client.completePendingCommand();
  }

  protected OutputStream openForWrite(String path, boolean binary) throws IOException {
    client.enterLocalPassiveMode();
    client.setFileType(binary ? FTP.BINARY_FILE_TYPE : FTP.ASCII_FILE_TYPE);
    OutputStream os = client.storeFileStream(path);
    if (os == null && !FTPReply.isPositiveCompletion(client.getReplyCode())) {
      printToLog("Error creating file \"" + path + "\" on ftp server. Error code: " + client.getReplyCode());
    }
    return os;
  }

  public void close() throws IOException {
    if (client != null) {
      client.logout();
      client.disconnect();
      client = null;
    }
  }

  @Override
  protected void finalize() throws Throwable {
    close();
    super.finalize();
  }

  public void setLogger(org.slf4j.Logger logger) {
    this.logger = logger;
  }

  protected void printToLog(Object o, Throwable...t) {
    if (logger != null)
      if (t.length > 0)
        logger.error("{}", o, t[0]);
      else
        logger.error("{}", o);
  }
}

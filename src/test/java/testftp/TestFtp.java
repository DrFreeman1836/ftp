package testftp;

import ftp.FtpLoader;
import ftp.FtpUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class TestFtp {

  @Test
  public void testConnect() throws InterruptedException {
    Thread.sleep(5000);
    FTPClient ftpClient = Assertions.assertDoesNotThrow(() ->
        FtpUtils.connect("ftp://10.81.2.142/report/head", "update_ppo", "123")) ;
    Assertions.assertDoesNotThrow(ftpClient::disconnect); ;
  }

  @Test
  public void testFileUpload() throws IOException, InterruptedException {
    Thread.sleep(5000);
    File tmpFile = new File("test_data.txt");
    tmpFile.createNewFile();
    tmpFile.deleteOnExit();
    List<String> data = Arrays.asList("test", "ftp", "report", "data");
    Files.write(tmpFile.toPath(), data, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
    FtpLoader loader = new FtpLoader("ftp://10.81.2.142/report/head", "update_ppo", "123");
    try {
      Assertions.assertTrue(loader.sendReport("test_report.txt", tmpFile.getAbsolutePath()));
    } finally {
      loader.close();
    }
  }
}

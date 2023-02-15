package ftp;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class FTPUploader {

  @Option(name = "-h", aliases = {"-help"}, usage = "Print help")
  protected boolean printUsage;

  @Option(name = "-url", usage = "FTP url", required = true)
  protected String ftpUrl;

  @Option(name = "-user", usage = "FTP user", required = true)
  protected String ftpUser;

  @Option(name = "-password", usage = "FTP password", required = true)
  protected String ftpPassword;

  @Option(name = "-sourceFile", usage = "Source file", required = true)
  protected String sourceFile;

  @Option(name = "-dest", usage = "Destination file", required = true)
  protected String destinationFile;

  private void uploadFile() throws IOException {
    FtpLoader loader = new FtpLoader(ftpUrl, ftpUser, ftpPassword);
    Logger logger = (Logger) LoggerFactory.getLogger(FTPUploader.class);

    File logFile = new File("ftp_uploader.log");
    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    PatternLayoutEncoder ple = new PatternLayoutEncoder();

    ple.setPattern("%d{dd.MM.yy HH:mm:ss.SSS} [%t] %p %c{0} - %m%n");
    ple.setContext(lc);
    ple.setCharset(StandardCharsets.UTF_8);
    ple.start();
    FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
    fileAppender.setFile(logFile.getCanonicalPath());
    fileAppender.setEncoder(ple);
    fileAppender.setContext(lc);
    fileAppender.start();
    logger.addAppender(fileAppender);
    logger.setLevel(Level.ALL);

    loader.setLogger(logger);
    try {
      loader.sendReport(destinationFile, sourceFile);
    } finally {
      loader.close();
    }
    logger.info("File successfully loaded to ftp");
  }

  public static void main(String[] args) throws IOException {
    JOPA
    final FTPUploader loader = new FTPUploader();
    final CmdLineParser parser = new CmdLineParser(loader);
    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.out.println(e.getLocalizedMessage());
      parser.printUsage(System.out);
      System.exit(1);
    }
    if (loader.printUsage)
      parser.printUsage(System.out);
    else {
      loader.uploadFile();
    }
  }
}

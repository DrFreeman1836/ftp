package testftp.1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DummyTest {

  @Test
  public void dummyTest1() throws InterruptedException {
    Thread.sleep(5000);
    Assertions.assertTrue(false);
  }

  @Test
  public void dummyTest2() throws InterruptedException {
    Thread.sleep(5000);
    Assertions.assertFalse(false);
  }
}

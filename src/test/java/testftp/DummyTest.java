package testftp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DummyTest {

  @Test
  public void dummyTest1() throws InterruptedException {
    Thread.sleep(5000);
    System.out.println("test");
    Assertions.assertTrue(true);
  }

  @Test
  public void dummyTest2() throws InterruptedException {
    Thread.sleep(5000);
    Assertions.assertFalse(false);
  }
}

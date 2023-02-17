package testftp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DummyTest4 {
    @Test
    public void dummyTest1() throws InterruptedException {
        Thread.sleep(2500);
        Assertions.assertTrue(true);
    }

    @Test
    public void dummyTest2() throws InterruptedException {
        Thread.sleep(2500);
        Assertions.assertFalse(false);
    }
}

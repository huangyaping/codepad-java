package util;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by huangyaping on 2017/10/12.
 */
public class GuidTest {

    private ConcurrentSkipListSet<Long> set;
    private AtomicLong callTimes;
    private long start;

    @BeforeTest
    public void beforeTest() throws Exception {
        set = new ConcurrentSkipListSet<Long>();
        callTimes = new AtomicLong(0);
        start = System.currentTimeMillis();
    }
    @AfterTest
    public void afterTest() throws Exception {
        System.out.println("cost = " + (System.currentTimeMillis()-start) + " ms");
        System.out.println("callTimes=" + callTimes);
        System.out.println("set.size=" + set.size());
        Long[] ids = new Long[set.size()];
        set.toArray(ids);
        Random random = new Random();
        for(int i = 0; i < 10 % ids.length; i++) {
            System.out.print(ids[random.nextInt(ids.length)] + ", ");
        }
        System.out.println();
    }

    @Test(threadPoolSize = 4, invocationCount = 1000, timeOut = 100000)
    public void testNextId() throws Exception {
        for(int i = 0; i < 1000; i++) {
            long nextid = Guid.nextId();
            set.add(nextid);
            callTimes.incrementAndGet();
        }
    }
}
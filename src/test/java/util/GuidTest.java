package util;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by huangyaping on 2017/10/12.
 */
public class GuidTest {

    @org.testng.annotations.Test
    public void testNextId() throws Exception {
        ConcurrentSkipListSet<Long> set = new ConcurrentSkipListSet<Long>();
        AtomicLong callTimes = new AtomicLong(0);
        for(int i = 0; i < 10; i++) {
            long nextid = Guid.nextId();
            set.add(nextid);
            callTimes.incrementAndGet();
        }
        System.out.println(set.size() + "," + callTimes.get());
    }
}
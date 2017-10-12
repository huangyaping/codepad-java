package util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存缓存的时钟。
 * 相比 System.currentTimeMillis() 的实时获取，{@code MemoryClock} 在内存中缓存了当前时间。
 *
 * @author huangyaping
 * @date 2017/9/6
 */
public class MemoryClock {

    /**
     * 获取当前时间。
     *
     * @return
     */
    public static long currentTimeMillis() {
        return CLOCK_INSTANCE.getClockTime();
    }

    // 内存时钟单例
    private static final MemoryClockImpl CLOCK_INSTANCE = new MemoryClockImpl();

    /**
     * 内存时钟实现。
     */
    private static class MemoryClockImpl {

        // 时钟时间
        private AtomicLong clockTime;

        private MemoryClockImpl() {
            clockTime = new AtomicLong(System.currentTimeMillis());
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    return thread;
                }
            });
            // 每一毫秒更新时钟
            scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    clockTime.set(System.currentTimeMillis());
                }
            }, 0, 1, TimeUnit.MILLISECONDS);
        }

        public long getClockTime() {
            return clockTime.get();
        }
    }

}

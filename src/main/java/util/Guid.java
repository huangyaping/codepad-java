package util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局唯一ID生成器。
 * 生成64位的Long类型ID。
 * ID结构 = 1位符号位 + 41位时间戳 + 10位工作机器ID + 12位自增数。
 * 41位时间戳 = 当前时间戳 - 开始时间戳，可使用年限 = 2^41 / (1000 * 3600 * 24 * 365) = 69年。
 * 10位工作机器ID，总数是1023台，可以在机房数量和机器数量之间微调，比如：4位机房ID + 6位本机房的工作机器ID，可以分布在16个机房，每个机房可以有64台机器。
 * 12位自增数，每台机器每一毫秒最多可以生成4095个唯一ID。
 *
 * @author huangyaping
 * @date 2017/9/6
 */
public class Guid {

    /**
     * 开始时间截
     */
    private static final long START_TIME = 1504698355349L;

    /**
     * GUID所占的位数
     */
    private static final int GUID_BITS = 63;

    /**
     * 时间戳所占的位数
     */
    private static final int TIMESTAMP_BITS = 41;

    /**
     * 数据标识id所占的位数
     */
    private static final int DC_ID_BITS = 4;
    private static final int DC_ID = 1;

    /**
     * 机器id所占的位数
     */
    private static final int WORKER_ID_BITS = 6;
    private static final int WORKER_ID = initWorkerId();

    /**
     * 毫秒内自增序列所占的位数
     */
    private static final int SEQUENCE_BITS = 12;
    /**
     * 最大毫秒内自增序列
     */
    private static final int MAX_SEQUENCE_ID = (1 << SEQUENCE_BITS) - 1;
    /**
     * 毫秒内自增序列
     */
    private static final AtomicInteger SEQUENCER = new AtomicInteger(0);

    /**
     * 上次生成ID的时间截
     */
    private static volatile long lastTimeMillis = MemoryClock.currentTimeMillis();

    public static synchronized long nextId() {
        long id;
        long currentTimeMillis = MemoryClock.currentTimeMillis();
        if(currentTimeMillis != lastTimeMillis) {
            // 时间戳前进，自增序列需要重置
            SEQUENCER.set(0);
            lastTimeMillis = currentTimeMillis;
        }
        id = (currentTimeMillis - START_TIME) << (GUID_BITS - TIMESTAMP_BITS);
        id |= DC_ID << (GUID_BITS - TIMESTAMP_BITS - DC_ID_BITS);
        id |= WORKER_ID << (GUID_BITS - TIMESTAMP_BITS - DC_ID_BITS - WORKER_ID_BITS);
        int sequence = SEQUENCER.incrementAndGet();
        if(SEQUENCER.get() > MAX_SEQUENCE_ID) {
            // 毫秒内自增序列达到最大，则等待，直到下一毫秒
            while(MemoryClock.currentTimeMillis() == lastTimeMillis) ;
        }
        id |= sequence;
        return id;
    }

    private static int initWorkerId() {
        return (Integer.parseInt(Networks.getSiteIp().split("\\.")[3])) % (1 << WORKER_ID_BITS);
    }

}

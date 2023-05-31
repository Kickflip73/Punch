package com.yrmjhtdjxh.punch.redis;

/**
 * @author ：GO FOR IT
 * @description:
 * @date ：2021/6/26 21:10
 */
public class RecordTimeKey extends BasePrefix{
    public RecordTimeKey(String prefix) {
        super(prefix);
    }

    public RecordTimeKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static RecordTimeKey getRecordTimeKey = new RecordTimeKey(60*60,"getRecordTimeKey");
    public static RecordTimeKey getPunchedTimeKey = new RecordTimeKey(3*60*60,"getPunchedTimeKey");
}

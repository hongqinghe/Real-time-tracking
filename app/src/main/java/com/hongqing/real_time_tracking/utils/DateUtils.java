package com.hongqing.real_time_tracking.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 * Created by 贺红清 on 2017/2/7.
 */

public class DateUtils {
    private static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String toDate(Date  date){
        return simpleDateFormat.format(date);
    }
}

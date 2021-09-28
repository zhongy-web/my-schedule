package entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {

    /***
     * 从yyyy-MM-dd HH:mm格式转成yyyyMMddHH格式
     * @param dateStr
     * @return
     */
    public static String formatStr(String dateStr){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = simpleDateFormat.parse(dateStr);
            simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
            return simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 获取指定日期的凌晨
     * @return
     */
    public static Date toDayStartHour(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date start = calendar.getTime();
        return start;
    }


    /***
     * 时间增加N分钟
     * @param date
     * @param minutes
     * @return
     */
    public static Date addDateMinutes(Date date,int minutes){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);// 24小时制
        date = calendar.getTime();
        return date;
    }

    /***
     * 时间递增N小时
     * @param hour
     * @return
     */
    public static Date addDateHour(Date date,int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hour);// 24小时制
        date = calendar.getTime();
        return date;
    }

    /***
     * 获取时间菜单
     * @return
     */
    public static List<Date> getDateMenus(){
      
        //定义一个List<Date>集合，存储所有时间段
        List<Date> dates = new ArrayList<Date>();
        
        //循环12次
        Date date = toDayStartHour(new Date()); //凌晨
        for (int i = 0; i <12 ; i++) {
            //每次递增2小时,将每次递增的时间存入到List<Date>集合中
            dates.add(addDateHour(date,i*2));
        }

        //判断当前时间属于哪个时间范围
        Date now = new Date();
        for (Date cdate : dates) {
            //开始时间<=当前时间<开始时间+2小时
            if(cdate.getTime()<=now.getTime() && now.getTime()<addDateHour(cdate,2).getTime()){
                now = cdate;
                break;
            }
        }

        //当前需要显示的时间菜单
        List<Date> dateMenus = new ArrayList<Date>();
        
        for (int i = 0; i <5 ; i++) {
            dateMenus.add(addDateHour(now,i*2));
        }
        return dateMenus;
    }

    /***
     * 时间转成yyyyMMddHH
     * @param date
     * @return
     */
    public static String date2Str(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前日期所在周的周一和周日的日期
     */
    public static Map<String, String> getMonAndSun(String tran_time, String pattern) throws ParseException {
        Map<String,String> map = new HashMap<>();
        SimpleDateFormat sdf=new SimpleDateFormat(pattern); //设置时间格式
        Calendar cal = Calendar.getInstance();
        Date time=sdf.parse(tran_time);
        cal.setTime(time);

        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if(1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一

        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        //星期一
        map.put("monday", sdf.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        //星期二
        map.put("tuesday", sdf.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        //星期三
        map.put("wednesday", sdf.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        //星期四
        map.put("thursday", sdf.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        //星期五
        map.put("friday", sdf.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        //星期六
        map.put("saturday", sdf.format(cal.getTime()));
        cal.add(Calendar.DATE, 1);
        //星期天
        map.put("sunday", sdf.format(cal.getTime()));
        return map;
    }

    /**
     * 获取这周周日的日期
     */
    public static String getSunday(String tran_time, String pattern) throws ParseException {
        Map<String,String> map = new HashMap<>();
        SimpleDateFormat sdf=new SimpleDateFormat(pattern); //设置时间格式
        Calendar cal = Calendar.getInstance();
        Date time=sdf.parse(tran_time);
        cal.setTime(time);

        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if(1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一

        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        //星期天
        return sdf.format(cal.getTime());
    }

}

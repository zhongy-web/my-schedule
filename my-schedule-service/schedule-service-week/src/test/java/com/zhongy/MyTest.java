package com.zhongy;

import entity.DateUtils;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MyTest {
    @Test
    public void test1() {
        String s1 = "ab";
        String s2 = "eidboaoo";
        Map<Character,Integer> window = new HashMap<>();
        Map<Character,Integer> need = new HashMap<>();
        //填充need
        char[] chars_s1 = s1.toCharArray();
        for(char c : chars_s1) {
            need.put(c, need.getOrDefault(c, 0) + 1);
        }
        int left = 0, right = 0;//窗口两边的指针
        int valid = 0; //子串有效个数
        char[] chars_s2 = s2.toCharArray();
        while(right < s2.length()) {
            char c = chars_s2[right];
            right++;
            if(need.containsKey(c)) {
                window.put(c, window.getOrDefault(c, 0) + 1);
                if(window.get(c).equals(need.get(c))) valid++;
            }
            System.out.println("window:" + "left:" + left + " = right:" + right);
            while(right - left >= s1.length()) {
                if(valid == need.size()) {
//                    return true;?
                    System.out.println(true);
                }
                char d = chars_s2[left];
                left--;
                if(need.containsKey(d)) {
                    if(window.get(d).equals(need.get(d))) valid--;
                    window.put(d, window.get(d) - 1);
                }
            }
        }
    }

    @Test
    public void test5() throws Exception {
        System.out.println(DateUtils.parseString2Date("2021-6-20 22:50:35").getTime());
        long x = DateUtils.parseString2Date("2021-6-20 22:50:35", "yyyy-MM-dd HH:mm:ss").getTime() - System.currentTimeMillis();
        System.out.println(System.currentTimeMillis());
        System.out.println(x);

    }

    public static void main(String[] args) throws ParseException {

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        Calendar cal = Calendar.getInstance();
        Date time=sdf.parse("2015-9-4");
        cal.setTime(time);
        System.out.println("要计算日期为:"+sdf.format(cal.getTime())); //输出要计算日期

        //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        if(1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一

        int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek()-day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        System.out.println("所在周星期一的日期："+sdf.format(cal.getTime()));
        System.out.println(cal.getFirstDayOfWeek()+"-"+day+"+6="+(cal.getFirstDayOfWeek()-day+6));

        cal.add(Calendar.DATE, 6);
        System.out.println("所在周星期日的日期："+sdf.format(cal.getTime()));

    }

    @Test
    public void test2() throws Exception {
        String s = "pwwkew";
        Map<Character,Integer> window = new HashMap<>();
        int left = 0, right = 0;
        int valid = 0;
        char[] cs = s.toCharArray();
        while(right < s.length()) {
            char c = cs[right];
            right++;
            window.put(c, window.getOrDefault(c, 0) + 1);
            System.out.println("windows:  " + " left:" + left + "right:" + right);
            while(window.get(c) > 1) {
                window.put(c, window.get(c) - 1);
                left++;
            }
            valid = Math.max(valid, right - left);
        }
        System.out.println(valid);
    }

    @Test
    public void test() {
        String str = "  hello world!  ";
        String[] split = str.trim().split(" ");
        for (String s : split) {
            System.out.println(s);
        }
    }

    @Test
    public void test12() {
    }
}

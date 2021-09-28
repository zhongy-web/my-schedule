import org.junit.Test;

import java.util.Base64;

public class MyTest {



    public static void main(String[] args) {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        String encode = encoder.encode("zhongy");
//        System.out.println(encode);
        strToInt("42");
    }
    @Test
    public void testDecodeBase64() {
        String str = "emhvbmd5Onpob25neQ==";
        byte[] decode = Base64.getDecoder().decode(str);
        String decodestr = new String(decode);
        System.out.println(decodestr);

    }

    @Test
    public void test1() {
    }

    @Test
    public void test() {
    }

    public static int strToInt(String str) {
        char[] chars = str.toCharArray();
        int res = 0, boundary = Integer.MAX_VALUE / 10;
        int i = 0, len = chars.length;
        int mark = 1;
        if(len == 0) return 0;
        while(chars[i] == ' ') {
            if(++i == len) return 0;
        }
        if(chars[i] == '-') mark = -mark;
        if(chars[i] == '-' || chars[i] == '+') i++;
        for(int j = i; j < len; j++) {
            if(chars[j] < 0 || chars[j] > 9) break;
            if(res > boundary || res == boundary && chars[j] > 7) {
                return mark == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }
            res = res * 10 + (chars[j] - '0');
        }
        return res * mark;
    }
}

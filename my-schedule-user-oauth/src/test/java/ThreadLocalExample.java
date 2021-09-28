import java.text.SimpleDateFormat;
import java.util.Random;

public class ThreadLocalExample implements Runnable{
    private static final ThreadLocal<SimpleDateFormat> formatter = new
            ThreadLocal<SimpleDateFormat>(){
                @Override
                protected SimpleDateFormat initialValue()
                {
                    return new SimpleDateFormat("yyyyMMdd HHmm");
                }
            };
    public static void main(String[] args) throws InterruptedException {
        ThreadLocalExample obj = new ThreadLocalExample();
        for(int i = 0; i < 10; i++) {
            Thread t = new Thread(obj, " "+ i);
            Thread.sleep(100);
            t.start();
        }
    }
    @Override
    public void run() {
        System.out.println("Thread Name=" + Thread.currentThread().getName() + "默认格式:" + formatter.get().toPattern());
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        formatter.set(new SimpleDateFormat());
        System.out.println("Thread Name=" + Thread.currentThread().getName() + "现在格式:" + formatter.get().toPattern());
    }
}

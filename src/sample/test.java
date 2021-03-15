package sample;

import org.junit.Test;
import sample.tool.Utils;

/**
 * @author 杨佳颖
 * @Data 2020/12/7 9:15
 **/
public class test {
    @Test
    public void test1() {
        for (float i = (float) 0.0; i < 50000.0; i++) {
            float num = (float) (i % 50000.0);
            num += 1;
            System.out.println(num / 50000);
            if (num == 50000) {
                System.out.println("结束");
            }
        }
    }

    @Test
    public void test2() {
        String url = "imgs/goods/asd/asdasd/goods_170_20191107114308862512_1.jpg";
//        System.out.println(Utils.getName(url));
        System.out.println(Utils.replaceUrl(url,2,"xxx"));
    }


    @Test
    public void test3() {
        Long startTime = System.currentTimeMillis();
        for(int i=0;i<1000;i++){
            System.out.println("tset");
        }
        Long endTime = System.currentTimeMillis();
        System.out.println(endTime-startTime);
    }
}

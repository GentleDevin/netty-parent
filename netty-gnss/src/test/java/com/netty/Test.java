package com.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @Title:
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/2/26 10:40
 */
public class Test {

    public static void main(String[] args) {
       // byteOrderConvert();
        //byteConvert();
        testGnssStatus();
    }


    public static void readFile() {
        String fileName = "C:\\Users\\gentl\\Desktop\\Todo\\WorkPlan\\GNSS\\WFSXJ9-G9-R-2020-10-23-06";
        File file = new File(fileName);
        String fileContent ;
        try {
            fileContent = FileUtils.readFileToString(file);
            /*System.out.println("fileContent=" +fileContent);*/
            int num = 0xAA;
            byte test = (byte) num;
            System.out.println(test);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void byteOrderConvert() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(4);
        buf.writeInt(87000000);
        byte[] n = new byte[4];
        System.out.println(buf.readBytes(n));
        System.out.println(Arrays.toString(n));

    /*    ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(4);
        buf2.writeIntLE(888);
        byte[] n2 = new byte[4];
        System.out.println(buf2.readBytes(n2));
        System.out.println(Arrays.toString(n2));
*/
    }

    public static void byteConvert() {
        byte[] byte1 = {59,45,93,0x1a};
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(4);
        buf.writeBytes(byte1);
        long num = buf.readUnsignedIntLE();
        System.out.println(num);
    }


    public static void testGnssStatus() {
        //方式A：
        double num1=2.247350482192265E7;
        String str1=new BigDecimal(num1+"").toString();
        System.out.println("str1= "  +str1);

        //方式B：
        Double num2=2.247350482192265E7;
        String str2=new BigDecimal(num2.toString()).toString();
        System.out.println("str2= "  +str2);

    }
}

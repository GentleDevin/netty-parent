package com.netty;

import com.netty.gnss.common.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
       // testGnssStatus();
        testBit();
        //getBit();
    }


    public static void readFile() {
        String fileName = "C:\\Users\\gentl\\Desktop\\Todo\\WorkPlan\\GNSS\\WFSXJ9-G9-R-2020-10-23-06";
        File file = new File(fileName);
        String fileContent;
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


    public static void getBit() {
        //int长度16位
        int a = 63; //111111
        int b = 15; //1111
        int c = 63; //111111

        //将a,b,c存入int中
        int res = (a << 10) + (b << 6) + c;
        System.out.println("res:"  +res);

        //按位取出a,b,c的值
        //取右移动10位的值(取前6位)
        int aa = res >> 10;
        System.out.println("aa:"  +aa);
        //0xf=1111 取中间4位
        int bb = (res >> 6) & 0xf;
        System.out.println("bb:"  +bb);
        //0x3f=111111 取后6位
        int cc = res & 0x3f;
        System.out.println("cc:"  +cc);




    }


    public static void testBit()  {
        byte[] bytes = new byte[0];
        byte[] bytes1 = new byte[1];
        try {
            bytes = ByteUtil.stringToByte("00100010");
            bytes1 = ByteUtil.getBytes("00100010");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /*for (byte aByte : bytes) {
            System.out.println(aByte);
        }
        System.out.println("==========");*/

     /*   for (byte aByte : bytes1) {
            System.out.println(aByte);
        }*/

        byte num1 = (byte) 65535;
        int leftNum = ByteUtil.getLeftNum(num1, 6);
        System.out.println("leftNum= " + leftNum);

        short num2 = 170; //二进制：00100010

        Integer result = num2 & 96; //二进制：11100000
        result = result >> 5;
        System. out.println(result);

        System. out.println(6 ^ 3);


        byte[] bytes2 = {1,2,3};
        byte[] bytes3 = Arrays.copyOfRange(bytes2, 0, 2);

        for (byte b : bytes3) {
            System.out.println("bytes3= " + b);
        }
        long aa = 2671946210L;
        float bb = (float) (1 / 128.0) * aa;

        BigDecimal a = new BigDecimal(aa);
        BigDecimal b = BigDecimal.valueOf(bb);
        float v = a.multiply(b).floatValue();
        System.out.println("v = " +  v );


        double dd = 23705199.33710812032222747802734375;
        BigDecimal usedM = new BigDecimal(dd);
        //保留两位小数且四舍五入
        usedM = usedM.setScale(2, BigDecimal.ROUND_HALF_UP);

        System.out.println(usedM);




    }
}



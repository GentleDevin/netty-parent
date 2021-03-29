package com.netty;

import com.netty.gnss.common.ByteUtil;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import io.netty.buffer.ByteBufUtil;

import java.io.*;
import java.util.Arrays;

/**
 * @Title:
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/17 14:44
 */
public class Gnss140Test {

    public static void main(String[] args) {
        byte[] bytes = initMsgData();
        System.out.println("bytes = "  + Arrays.toString(bytes));

        getUnsignedValueByBits(bytes);
    }


    /**
     * @Description: 读取二进制文件内容
     * @CreateDate: 2021/03/05 15:09:11
     * @return: void
     **/
    public static byte[]  initMsgData(){
        String filePath = "C:\\Users\\gentl\\Desktop\\Todo\\WorkPlan\\GNSS\\GNSS_FILE\\GNSS_data_1";
        InputStream in = null;
        BufferedInputStream buffer = null;
        DataInputStream dataIn = null;
        try {
            in = new FileInputStream(filePath);
            buffer = new BufferedInputStream(in);
            dataIn = new DataInputStream(buffer);
            byte[] buf = new byte[24];
            int byteReader = 0;
            int byteReaderCount = 0;
            while((byteReader = dataIn.read(buf)) != -1){
                byteReaderCount = byteReaderCount + byteReader;
                System.out.println("byteReader=" + byteReaderCount);
            }
            return buf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getUnsignedValueByBits(byte[] readBytes) {
        //通道跟踪状态
        long chTrStatus = ByteUtil.getUnsignedValueByBits(readBytes, 0, 32);
        /*int i = ByteBufUtil.swapInt((int) chTrStatus);*/
        System.out.println("chTrStatus=" +chTrStatus);

        //多普勒
        long doppOri = ByteUtil.getUnsignedValueByBits(readBytes, 32, 28);
        float dopp = doppOri * (float) (1 / 256.0);
        System.out.println("dopp ==" + dopp);

        //PSR伪距
        long psrOri = ByteUtil.getUnsignedValueByBits(readBytes, 60, 36);
        psrOri = ByteBufUtil.swapLong(psrOri);

        double psr = (double) psrOri * (1/128.0);
        System.out.println("psr=" + psr);

        //ADR载波相位
        long adrOri = ByteUtil.getUnsignedValueByBits(readBytes, 96, 32);
        float adr = adrOri * (float)(1 / 256.0);
        System.out.println("adr=" + adr);

        //卫星PRN号
        long prn = ByteUtil.getUnsignedValueByBits(readBytes, 136, 8);
        System.out.println("prn=" + prn);
        //载噪比
        long cnoOri = ByteUtil.getUnsignedValueByBits(readBytes, 165, 5);
        long cno = cnoOri + 20;
        System.out.println("cno=" + cno);
    }

    public void toBit(byte[] data) {

    }
}

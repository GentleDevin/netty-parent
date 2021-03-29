package com.netty.gnss.common;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;



public class DataFormatConvertUtil {
    /**
     * Static method, a long type data into a byte array of specified length
     *
     * @return byte[]
     */
    public static byte[] intToByteArray(long res, int length) {

        byte[] byteArray = new byte[length];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte) ((res >> ((byteArray.length - i - 1) * 8)) & 0xff);
        }
        return byteArray;
    }

    /**
     * Static method, a byte array into a signed int type data
     *
     * @return int
     */
    public static int byteArrayToSignedInt(byte[] res) {
        byte[] bytes = new byte[4];
        if ((res[0] & 0x80) == 0x80) {

            for (int i = 0; i < bytes.length - res.length; i++) {
                bytes[i] = -1;
            }
            for (int i = bytes.length - res.length; i < bytes.length; i++) {
                bytes[i] = res[i - (bytes.length - res.length)];
            }
        } else {
            for (int i = bytes.length - res.length; i < bytes.length; i++) {
                bytes[i] = res[i - (bytes.length - res.length)];
            }
        }
        int nResult = DataFormatConvertUtil.getResult(bytes);
        return nResult;
    }

    /**
     * private method,a byte array shift operations
     *
     * @return int
     */
    private static int getResult(byte[] res) {
        int nTemp, nResult;
        nResult = 0;
        for (int i = 0; i < res.length; i++) {
            nTemp = res[i] & 0xFF;
            nTemp = nTemp << ((res.length - i - 1) * 8);
            nResult = nResult | nTemp;
        }
        return nResult;
    }

    /**
     * private method,a byte array shift operations
     *
     * @return int
     */
    private static long getLongResult(byte[] res) {
        long nTemp, nResult;
        nResult = 0;
        for (int i = 0; i < res.length; i++) {
            nTemp = res[i] & 0xFF;
            nTemp = nTemp << ((res.length - i - 1) * 8);
            nResult = nResult | nTemp;
        }
        return nResult;
    }

    /**
     * Static method, a byte array into a Unsigned int type data
     *
     * @return int
     */
    public static int byteArrayToUnsignedInt(byte[] res) {
        byte[] bytes = new byte[4];
        for (int i = bytes.length - res.length; i < bytes.length; i++) {
            bytes[i] = res[i - (bytes.length - res.length)];
        }
        int nResult = DataFormatConvertUtil.getResult(bytes);
        return nResult;
    }

    /**
     * Static method, a byte array into a signed long type data
     *
     * @return long
     */
    public static long byteArrayToSignedlong(byte[] res) {

        byte[] bytes = new byte[8];
        if ((res[0] & 0x80) == 0x80) {

            for (int i = 0; i < bytes.length - res.length; i++) {
                bytes[i] = -1;
            }
            for (int i = bytes.length - res.length; i < bytes.length; i++) {
                bytes[i] = res[i - (bytes.length - res.length)];
            }
        } else {
            for (int i = bytes.length - res.length; i < bytes.length; i++) {
                bytes[i] = res[i - (bytes.length - res.length)];
            }
        }
        long nResult = DataFormatConvertUtil.getLongResult(bytes);
        return nResult;

    }

    /**
     * Static method, a byte array into a Unsigned long type data
     *
     * @return long
     */
    public static long byteArrayToUnsignedlong(byte[] res) {
        byte[] bytes = new byte[8];
        if (res.length < 8) {
            for (int i = bytes.length - res.length; i < bytes.length; i++) {
                bytes[i] = res[i - (bytes.length - res.length)];
            }
        }
        long nResult = DataFormatConvertUtil.getLongResult(bytes);
        return nResult;
    }

    /**
     * Static method, a hexString data into a bytes
     *
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    /**
     * Static method, a hexString data into a int type data
     *
     * @return int
     */
    public static int hexToInt(String str) {
        return Integer.parseInt(str, 16);
    }

    /**
     * Static method, a int type data into a hexString data
     *
     * @return String
     */
    public static String intToHexString(int value) {
        StringBuilder str = new StringBuilder("");
        String hv = Integer.toHexString(value);
        if (hv.length() < 2) {
            str.append(0);
        }
        str.append(hv);
        return str.toString();
    }

    /**
     * Static method, a int type data into a hexString data by length
     *
     * @return String
     */
    public static String intToHexString(int value, int length) {
        String v = "";
        StringBuilder str = new StringBuilder("");
        int maxInt = getMaxIntByByteSigned(length);
        int mi = maxInt/2;
        if(value < 0){
            if(value < -mi){
                value = -mi;
            }
        }if(value > 0){
            if(value > mi){
                value = mi;
            }
        }
        String hv = Integer.toHexString(value);
        int i = length * 2 - hv.length();// two hex is a byte
        for (int j = 0; j < i; j++) {
            str.append(0);
        }
        str.append(hv);
        if (value < 0) {
            v = str.substring((str.length() - length * 2), str.length());
            return v;
        } else {
            return str.toString();
        }
    }

    private static int getMaxIntByByteSigned(int length){
        StringBuffer maxHex = new StringBuffer();
        for (int i = 0; i < length; i++) {
            maxHex.append("FF");
        }
        return hexToInt(maxHex.toString());
    }

    public static void main(String[] args) {
// System.out.println(a.byteArrayToSignedInt(new byte[]{7,-48}));
        System.out.println(intToHexString(-34000, 2));
    }

    /**
     * Static method, a byte array data into hexString
     *
     * @return String
     */
    public static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder str = new StringBuilder("");
        for (int i = 0; i < byteArray.length; i++) {
            int v = byteArray[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                str.append(0);
            }
            str.append(hv);
        }
        return str.toString();
    }

    /**
     * Static method, a short type data into a hexString data
     *
     * @return String
     */
    public static String shortToHex(short in) {
        byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            b[i] = (byte) ((in >> (1 - i) * 8) & 0xFF);
        }
        String bstr = byteArrayToHexString(b);
        return bstr;
    }

    /**
     * Static method, a float type data into a hexString data
     *
     * @return String
     */
    public static String floatToHexString(float f) {
        return Float.toHexString(f);
    }

    /**
     * Static method, a byte type data into a float data
     *
     * @return float
     */
    public static float byteToFloat(byte[] by) {
        ByteBuffer buffer = ByteBuffer.wrap(by);
        FloatBuffer fb = buffer.asFloatBuffer();
        return fb.get();
    }

    /**
     * Static method, a byte type data into a double data
     *
     * @return String
     */
    public static double byteToDouble(byte[] by) {
        ByteBuffer buffer = ByteBuffer.wrap(by);
        DoubleBuffer fb = buffer.asDoubleBuffer();
        return fb.get();
    }
}
package com.netty.gnss.common;

/**
 * @Title: 判断进制数
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/2/26 14:23
 */
public class RegexNumberValidate {
    public static void main(String[] args){
        String[] values = new String[]{
                "10","32768","9999","ati","905Af","ffff"
        };
/*        for(String value:values){
            System.out.println("Validating value:\t"+value);
            if(isOctNumberRex(value)){
                System.out.println("this is a Octnumber:"+value);
            }else {
                System.out.println("this isn't a Octnumber:"+value);
            }
            if(isHexNumberRex(value)){
                System.out.println("this is a Hexnumber:"+value);
            }else {
                System.out.println("this isn't Hexnumber:"+value);
            }
        }*/
        int num  = 0xaa;
        byte b = conbertByte(num);
        System.out.println("b= " +b);



    }
    //十进制
    private static boolean isOctNumber(String str) {
        boolean flag = false;
        for(int i=0,n=str.length();i<n;i++){
            char c = str.charAt(i);
            if(c=='0'|c=='1'|c=='2'|c=='3'|c=='4'|c=='5'|c=='6'|c=='7'|c=='8'|c=='9'){
                flag =true;
            }
        }
        return flag;
    }
    //十六进制
    private static boolean isHexNumber(String str){
        boolean flag = false;
        for(int i=0;i<str.length();i++){
            char cc = str.charAt(i);
            if(cc=='0'||cc=='1'||cc=='2'||cc=='3'||cc=='4'||cc=='5'||cc=='6'||cc=='7'||cc=='8'||cc=='9'||cc=='A'||cc=='B'||cc=='C'||
                    cc=='D'||cc=='E'||cc=='F'||cc=='a'||cc=='b'||cc=='c'||cc=='c'||cc=='d'||cc=='e'||cc=='f'){
                flag = true;
            }
        }
        return flag;
    }

    private static boolean isOctNumberRex(String str){
        String validate = "\\d+";
        return str.matches(validate);
    }

    private static boolean isHexNumberRex(String str){
        String validate = "(?i)[0-9a-f]+";
        return str.matches(validate);
    }

    private static byte conbertByte(int intValue){
        byte byteValue=0;
        int temp = intValue % 256;
        if ( intValue < 0) {
           byteValue = (byte)(temp < -128 ? 256 + temp : temp);
        }else {
           byteValue =(byte)(temp > 127 ? temp - 256 : temp);
        }
        return byteValue;
    }


}

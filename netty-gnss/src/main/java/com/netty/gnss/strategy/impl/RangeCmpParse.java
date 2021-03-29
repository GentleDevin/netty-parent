package com.netty.gnss.strategy.impl;

import com.netty.gnss.common.BigDecimalUtil;
import com.netty.gnss.common.ByteUtil;
import com.netty.gnss.common.DataParseUtil;
import com.netty.gnss.common.ParseConfig;
import com.netty.gnss.protocol.IMMessage;
import com.netty.gnss.protocol.content.ChTrStatus;
import com.netty.gnss.protocol.content.Range;
import com.netty.gnss.strategy.IGnssParse;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Title: RangeCmp按位数据解析
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/5 14:14
 */
public class RangeCmpParse  implements IGnssParse {
    private int msgCount;

    @Override
    public void gnssParse(ByteBuf in, IMMessage imMessage) {
        List<Range> imContentList = new ArrayList<>();
        //CRC校验正确，开始解析数据,ridx = 32
        for (int readContentIndex = 0; readContentIndex < imMessage.getImContent().getObs(); readContentIndex++) {
            /*getRangeBit(in);*/
            /*getUnsignedValueByBits(readBytes);*/
            //每次读一组24字节数据
            byte[] readBytes = new byte[24];
            in.readBytes(readBytes);
            Range range = getRange(readBytes);
            imContentList.add(range);
         }
            //报文数据已解析完成
            imMessage.setHeaderMatch(false);
            imMessage.getImContent().setImData(imContentList);
            in.skipBytes(4);
            System.out.println("msgCount= " + ++msgCount);
    }


    //上次剩余位数
    int oldBitLength = 0;
    //上次剩余位值
    long oldBit = 0;
    /**
     * @Description:
     * @CreateDate: 2021/03/12 22:30:55
     * @param readBytes:
     * @param bitStartIndex: 字段开始位索引
     * @param bitEndIndex: 字段结束位索引
     * @return: long
     **/
    public long getRangeBitByBytesArr(byte[] readBytes,int bitStartIndex,int bitEndIndex) {
        if(null != readBytes && bitStartIndex >=0 && bitEndIndex >= 0) {
            int  byteStartIndex = bitStartIndex / 8;
            int  byteEndIndex = bitEndIndex / 8 + 1;

            //上次有剩余的字节，跳过本次重复的字节
            if(oldBitLength > 0) {
                byteStartIndex += 1;
            }

            //当前字段位长，索引从0开始多加1位
            int bitLength  = bitEndIndex - bitStartIndex + 1;
            int bitLengthBytesArr = 0;
            int bitLengthOver = 0;
            //上次剩余位长不满足当前字段位长
            if(oldBitLength < bitLength) {
                //当前从字节数组读取的位数
                bitLengthBytesArr = (byteEndIndex - byteStartIndex) * 8;
                //当前多读的位数=当前读的位数+上次剩余的位数-当前数据位长
                bitLengthOver = bitLengthBytesArr + oldBitLength - bitLength;
            }

                long rangOri = 0;
                //判断是否有新的字节需要读取
                if (bitLengthBytesArr > 0 ) {
                    byte[] bytes = Arrays.copyOfRange(readBytes, byteStartIndex, byteEndIndex);
                    //本次字段读取的字节值
                    rangOri = ByteUtil.getBytesValue(bytes);
                }

                //刚好等于位长，上个字段没有剩余位，chTrStatus,ADR
                if(bitLengthOver == 0 && oldBitLength == 0) {
                    return rangOri;
                }

                //本次字段没有多读位，上个字段有剩余位，PSR伪距
                if(bitLengthOver == 0 && oldBitLength > 0) {
                    long bit = 0;
                    //需要读取新的字节
                    if (bitLengthBytesArr > 0 ) {
                        long leftBit = rangOri << oldBitLength;
                        //重置上个字段读完的位
                        oldBitLength  = 0;
                        oldBit = 0;
                        bit = leftBit | oldBit;
                    }else{
                        //上次剩余位数读完后剩余位数 = 上次剩余的位数 - 当前数据位长
                        int oldBitLengthOver = oldBitLength - bitLength;
                        //上次剩余位数读完后还有剩余位数
                        if(oldBitLengthOver > 0 ) {
                            // 读取上个字段部分位数，通道状态载波相位
                            bit = ByteUtil.getRightBit(oldBit, bitLength);
                            // 保存本次剩余位值和长度
                            oldBit = ByteUtil.getLeftBit(oldBit,bitLength);
                            oldBitLength = oldBitLengthOver;
                        } else {
                            //读取上个字段所有位数,adrStd
                            bit = ByteUtil.getRightBit(oldBit,oldBitLength);
                            //重置上个字段读完的位
                            oldBitLength  = 0;
                            oldBit = 0;
                        }
                    }
                    return bit;
                }

                //本次多读的位长，上个字段没有剩余位 dopp,lockTime
                if(bitLengthOver > 0 && oldBitLength == 0) {
                    long rightBit = ByteUtil.getRightBit(rangOri,bitLength);
                    oldBit = ByteUtil.getLeftBit(rangOri,bitLength);
                    oldBitLength = bitLengthOver;
                    return rightBit;
                }

                // 本次字段读取位  + 上次字段剩余位 cno
                if(bitLengthOver > 0 && oldBitLength > 0) {
                    int leftLength = 0;
                    //本次字段读取长度
                    leftLength = bitLength - oldBitLength;
                    long midBit = ByteUtil.getMidBit(rangOri, leftLength, oldBit, oldBitLength);
                    oldBitLength = bitLengthOver;
                    //保存本次字段剩余位
                    oldBit = ByteUtil.getLeftBit(rangOri,leftLength);
                    return midBit;
                }
        }
            return 0;
    }

    /**
     * @Description: 获取固定的位数
     * @CreateDate: 2021/03/15 10:06:04
     * @param in:
     * @return: void
     **/
    public void getRangeBit(ByteBuf in) {
        //通道跟踪状态
        int chTrStatus = in.readIntLE();
         System.out.println("chTrStatus=" + chTrStatus);

        //多普勒
        int doppOri = in.readIntLE();
        int dopp = doppOri >> 4;
        System.out.println("dopp=" + dopp);

        //psr伪距
        long psrOri = in.readIntLE();
        long doppBack4 = doppOri & 0xf;
        long psr = (psrOri << 4) | doppBack4;
        System.out.println("psr= " + psr);

        //adr载波相位
        double adrOri = in.readIntLE();
        System.out.println("adrOri= " + adrOri);

        //psr标准差
        byte psrStdOri = in.readByte();
        byte psrStd = (byte) (psrStdOri & 0xf);
        System.out.println("psrStd=" + psrStd);

        //adr标准差
        byte adrStd = (byte) (psrStdOri >> 4);
        System.out.println("adrStd=" + adrStd);

        //卫星PRN号
        byte prnOrSlot = in.readByte();
        System.out.println("prnOrSlot=" + prnOrSlot);

        //连续跟踪时间
        byte[] readBytes1 = new byte[3];
        in.readBytes(readBytes1);
        int aa = readBytes1[2] << 13;
        int bb = readBytes1[1] << 5;
        int cc = readBytes1[0] & 0x1F;
        int lockTime = aa | bb | cc;
        System.out.println("lockTime=" + lockTime);

        //载噪比
        byte cOrNoOri = in.readByte();
        byte lockTimeBack3 = (byte) (aa & 0x7);
        int cOrNoBefore2 =  cOrNoOri >> 6;
        int cOrNo = lockTimeBack3 << 2 | cOrNoBefore2;
        System.out.println("cOrNo=" + cOrNo);
    }

    /**
     * @Description: 根据取位范围获取位值
     * @CreateDate: 2021/03/29 09:29:38
     * @param readBytes: 字节数组
     * @return: com.netty.gnss.protocol.content.Range
     **/
    public Range getRange(byte[] readBytes) {
        Range range = new Range();
        long chTrStatusTotal = getRangeBitByBytesArr(readBytes, 0, 31);
        ChTrStatus chTrStatus = parseChTrStatus(chTrStatusTotal);
        /*ChTrStatus chTrStatus = parseChTrStatus(readBytes);*/
        range.setChTrStatus(chTrStatus);
        long doppOri = getRangeBitByBytesArr(readBytes, 32, 59);
        float dopp = doppOri  / 256.0f;
        range.setDopp(dopp);
        long psrOri = getRangeBitByBytesArr(readBytes, 60, 95) ;
        double psr = psrOri / 128.0;
        range.setPsr(BigDecimalUtil.getBigDecimal(psr));
        double adr = getRangeBitByBytesArr(readBytes, 96, 127) / 256.0;
        short prn = (short) getRangeBitByBytesArr(readBytes, 136, 143);
        range.setPrnOrSlot(DataParseUtil.getSatelliteSystemPrn(prn, chTrStatus.getSateSys()));
        double calculationAdr = calculationAdr(psr, adr, prn, chTrStatus.getSateSys(), chTrStatus.getSignalTypes());
        range.setAdr(BigDecimalUtil.getBigDecimal(calculationAdr));
        long psrStd = getRangeBitByBytesArr(readBytes, 128, 131);
        range.setPsrStd(psrStd);
        float adrStd = (getRangeBitByBytesArr(readBytes, 132, 135) + 1) / 512.0f;
        range.setAdrStd(adrStd);
        float lockTime = getRangeBitByBytesArr(readBytes, 144, 164) / 32.0f;
        range.setLocktime(lockTime);
        long cno = getRangeBitByBytesArr(readBytes, 165, 169) + 20;
        range.setCOrNo(cno);
        long glofreq = getRangeBitByBytesArr(readBytes, 170, 175);
        range.setGlofreq((short) glofreq);
        return range;
    }


    /**
     * @Description: 无符号按位范围取值，按每一位取
     * @CreateDate: 2021/03/29 09:32:11
     * @param readBytes:
     * @return: void
     **/
    public void getUnsignedValueByBits(byte[] readBytes) {
        long chTrStatus = ByteUtil.getUnsignedValueByBits(readBytes, 0, 32);
        System.out.println("chTrStatus=" + chTrStatus);
        long dopp = ByteUtil.getUnsignedValueByBits(readBytes, 32, 28);
        System.out.println("dopp=" + dopp);
        long psr = ByteUtil.getUnsignedValueByBits(readBytes, 60, 36);
        System.out.println("psr=" + psr);
        long adr = ByteUtil.getUnsignedValueByBits(readBytes, 96, 32);
        System.out.println("adr=" + adr);
        long prn = ByteUtil.getUnsignedValueByBits(readBytes, 136, 8);
        System.out.println("prn=" + prn);
        long cno = ByteUtil.getUnsignedValueByBits(readBytes, 165, 5);
        System.out.println("cno=" + cno);
    }


    /**
     * @Description: 解析跟踪状态
     * @CreateDate: 2021/03/23 15:51:40
     * @return: com.netty.gnss.protocol.content.ChTrStatus
     **/
    public ChTrStatus parseChTrStatus(byte[] readBytes) {
        ChTrStatus chTrStatus = new ChTrStatus();
        long reserve0 = getRangeBitByBytesArr(readBytes, 0, 4);
        long sv = getRangeBitByBytesArr(readBytes, 5, 9);
        chTrStatus.setSv((byte) sv);
        long adrFlag = getRangeBitByBytesArr(readBytes, 10, 10);
        chTrStatus.setAdrFlag((byte) adrFlag);
        long reserve1 = getRangeBitByBytesArr(readBytes, 11, 11);
        long psrFlag = getRangeBitByBytesArr(readBytes, 12, 12);
        chTrStatus.setPsrFlag((byte) psrFlag);
        long reserve2 = getRangeBitByBytesArr(readBytes, 13, 15);
        long SatelliteName = getRangeBitByBytesArr(readBytes, 16, 18);
        chTrStatus.setSateSys((byte) SatelliteName);
        long reserve3 = getRangeBitByBytesArr(readBytes, 19, 20);
        long SignalTypes = getRangeBitByBytesArr(readBytes, 21, 25);
        chTrStatus.setSignalTypes((byte) SignalTypes);
        long reserve4 = getRangeBitByBytesArr(readBytes, 26, 31);
        return chTrStatus;
    }

    /**
     * @Description: 固定解析跟踪状态
     * @CreateDate: 2021/03/23 15:51:40
     * @return: com.netty.gnss.protocol.content.ChTrStatus
     **/
    public ChTrStatus parseChTrStatus(long chTrStatusTotal) {
        ChTrStatus chTrStatus = new ChTrStatus();
        byte sv = (byte) ((chTrStatusTotal >>> 0x1F) & 0x1F);
        chTrStatus.setSv(sv);
        byte adrFlag = (byte) ((chTrStatusTotal >>> 10) & 1);
        chTrStatus.setAdrFlag(adrFlag);
        byte psrFlag = (byte) ((chTrStatusTotal >>> 12) & 1);
        chTrStatus.setPsrFlag(psrFlag);
        byte SatelliteName = (byte) ((chTrStatusTotal >>> 16) & 7);
        chTrStatus.setSateSys(SatelliteName);
        byte SignalTypes = (byte) ((chTrStatusTotal >>> 21) & 0x1F);
        chTrStatus.setSignalTypes(SignalTypes);
        return chTrStatus;
    }


    /**
     * @Description: 计算ADR载波相位
     * @CreateDate: 2021/03/25 14:51:06
     * @param adr:  载波相位
     * @param prn:  prn号
     * @param satSys: 卫星系统
     * @param sigtype: 卫星信号
     * @return: double
     **/
    public double calculationAdr(double psr,double adr,short prn,int satSys,int sigtype) {
        int sys = getSys(satSys);
        int code = sig2code(sys, sigtype);

        if (ParseConfig.CODE_NONE >= code || (ParseConfig.MAXCODE <= code)) {
            return 0;
        }

        int sat = satno(sys, prn);
        double freq = sat2freq(sat,code);
        double adr_rolls = (psr*freq/ParseConfig.CLIGHT+adr) / ParseConfig.MAXVAL;
        adr = -adr + ParseConfig.MAXVAL * Math.floor(adr_rolls + (adr_rolls <= 0 ? -0.5 : 0.5));
        return adr;
    }


    /**
     * @Description: 根据卫星系统和PRN号得到卫星号
     * @CreateDate: 2021/03/25 15:20:31
     * @param sys:
     * @param prn:
     * @return: int
     **/
    public int satno(int sys, int prn) {
        if (prn <= 0) {
            return 0;
        }

        switch (sys) {
            case ParseConfig.SYS_GPS:
                if (prn < ParseConfig.MINPRNGPS || ParseConfig.MAXPRNGPS < prn) {
                    return 0;
                }
                return prn-ParseConfig.MINPRNGPS+1;
        }
        return 0;
    }



    /**
     * @Description: 获取卫星系统
     * @CreateDate: 2021/03/25 11:07:29
     * @param satSys:
     * @return: int
     **/
    public int getSys(int satSys) {
        switch(satSys){
            case 0:
                return ParseConfig.SYS_GPS;
            default:
                break;
        }
        return 0;
    }

    /**
     * @Description: 根据卫星系统和信号类型获取编号索引
     * @CreateDate: 2021/03/25 13:43:15
     * @param sys:
     * @param sigtype:
     * @return: int
     **/
    private int sig2code(int sys, int sigtype) {
        if (sys==ParseConfig.SYS_GPS) {
            switch(sigtype){
                case 0:
                    return ParseConfig.CODE_L1C;
                case 5:
                    return ParseConfig.CODE_L2P;
                case  9:
                    return ParseConfig.CODE_L2W;
                case 14:
                    return ParseConfig.CODE_L5Q;
                case 16:
                    return ParseConfig.CODE_L1L;
                case 17:
                    return ParseConfig.CODE_L2S;
                default:
                    break;
            }
        }
        return 0;
    }

    /**
     * @Description: 获取卫星系统频率
     * @CreateDate: 2021/03/25 14:41:14
     * @param sat:
     * @param code:
     * @return: double
     **/
    public double sat2freq(int sat, int code) {
        int sys = satsys(sat);
        double freq = code2freq(sys, code);
        return freq;
    }
    

    /**
     * @Description: 根据卫星号获取卫星系统
     * @CreateDate: 2021/03/25 13:31:14
     * @param sat: 
     * @return: int satellite system (SYS_GPS,SYS_GLO,...)
     **/
    public int satsys(int sat) {
        int sys = ParseConfig.SYS_NONE;

        if (sat<=0 || ParseConfig.MAXSAT<sat) {
            sat=0;
        }else if (sat<=ParseConfig.NSATGPS) {
            sys=ParseConfig.SYS_GPS;
        }
        return sys;
}

    /**
     * @Description: 根据卫星系统和信号类型对应的编号索引获取对应的频率
     * @CreateDate: 2021/03/25 14:03:20
     * @param sys:
     * @param code:
     * @return: double
     **/
    public double code2freq(int sys, int code) {
        double freq=0.0;
        switch(sys){
            case ParseConfig.SYS_GPS:
                freq = code2freq_GPS(code);
                break;
            default:
                break;
        }
        return freq;
    }

    /**
     * @Description: 获取GPS对应的频率
     * @CreateDate: 2021/03/25 14:28:16
     * @param code:
     * @return: double
     **/
    private double code2freq_GPS(int code) {
        String obs=code2obs(code);
        char[] obsArr = obs.toCharArray();

        switch(obsArr[0]){
            case '1':
                return ParseConfig.FREQ1;
            case '2':
                return ParseConfig.FREQ2;
            case '5':
                return ParseConfig.FREQ5;
            default:
                break;
        }
        return 0;
    }

    /**
     * @Description: 根据卫星信号类型编号获取对应值
     * @CreateDate: 2021/03/25 14:22:50
     * @param code:
     * @return: java.lang.String ("1C","1P","1P",...)
     **/
    private String code2obs(int code) {
        if (code<=ParseConfig.CODE_NONE || ParseConfig.MAXCODE<code) {
            return "";
        }
       return ParseConfig.OBS_CODES[code];
    }


}



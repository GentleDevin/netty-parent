package com.netty.gnss.common;

/**
 * @Title: 协议解析配置类
 * @Description:
 * @Author: Devin
 * CreateDate: 2021/3/25 10:32
 */
public class ParseConfig {
    /** observation code strings */
    public  static final String OBS_CODES[] = {"","1C","1P","1W","1Y", "1M","1N","1S","1L","1E", /*  0- 9 */
            "1A","1B","1X","1Z","2C", "2D","2S","2L","2X","2P", /* 10-19 */
            "2W","2Y","2M","2N","5I", "5Q","5X","7I","7Q","7X", /* 20-29 */
            "6A","6B","6C","6X","6Z", "6S","6L","8L","8Q","8X", /* 30-39 */
            "2I","2Q","6I","6Q","3I", "3Q","3X","1I","1Q","5A", /* 40-49 */
            "5B","5C","9A","9B","9C", "9X","1D","5D","5P","5Z", /* 50-59 */
            "6E","7D","7P","7Z","8D", "8P","4A","4B","4X",""    /* 60-69 */
    };

    /** speed of light (m/s) */
    public static final double CLIGHT = 299792458.0;
    public static final double MAXVAL = 8388608.0;

    /** navigation system: none */
    public static final byte SYS_NONE = 0x00;
    /** navigation system: GPS */
    public static final byte SYS_GPS = 0x01;

    /** min satellite PRN number of GPS */
    public static final byte MINPRNGPS = 1;
    /** max satellite PRN number of GPS */
    public static final byte MAXPRNGPS = 32;
    /** number of GPS satellites */
    public static final byte NSATGPS = (MAXPRNGPS - MINPRNGPS+1);

    /** ENAGLO */
    /** min satellite slot number of GLONASS */
    public static final byte MINPRNGLO = 1;
    /** max satellite slot number of GLONASS */
    public static final byte MAXPRNGLO = 27;
    /** number of GLONASS satellites */
    public static final byte NSATGLO = (MAXPRNGLO-MINPRNGLO+1);

    /** ENAGAL */
    /** min satellite PRN number of Galileo */
    public static final byte MINPRNGAL = 1;
    /** max satellite PRN number of Galileo */
    public static final byte MAXPRNGAL = 36;
    /** number of Galileo satellites */
    public static final byte NSATGAL = (MAXPRNGAL-MINPRNGAL+1);

    /** ENAQZS */
    /** min satellite PRN number of QZSS */
    public static final short MINPRNQZS = 193;
    /** max satellite PRN number of QZSS */
    public static final short MAXPRNQZS = 202;
    /** number of QZSS satellites */
    public static final short NSATQZS = (MAXPRNQZS-MINPRNQZS+1);

    /** ENACMP */
    /** min satellite sat number of BeiDou */
    public static final byte MINPRNCMP = 1;
    /** max satellite sat number of BeiDou */
    public static final byte MAXPRNCMP = 63;
    /** number of BeiDou satellites */
    public static final byte NSATCMP = (MAXPRNCMP-MINPRNCMP+1);
    public static final byte NSYSCMP = 1;

    /** ENAIRN  */
    /** min satellite sat number of IRNSS */
    public static final byte MINPRNIRN = 1;
     /** max satellite sat number of IRNSS */
    public static final byte MAXPRNIRN = 14;
     /** number of IRNSS satellites */
    public static final byte NSATIRN = (MAXPRNIRN-MINPRNIRN+1);
    public static final byte NSYSIRN  = 1;

    /** min satellite PRN number of SBAS */
    public static final byte MINPRNSBS = 120;
    /** max satellite PRN number of SBAS */
    public static final short MAXPRNSBS = 158;
     /** number of SBAS satellites */
    public static final byte NSATSBS = (MAXPRNSBS-MINPRNSBS+1);

     /** ENALEO */
    /** min satellite sat number of LEO */
    public static final byte MINPRNLEO = 1;
    /** max satellite sat number of LEO */
    public static final byte MAXPRNLEO = 10;
    /** number of LEO satellites */
    public static final byte NSATLEO = (MAXPRNLEO-MINPRNLEO+1);
    public static final byte NSYSLEO = 1;
    /** max satellite number (1 to MAXSAT) */
    public static final short MAXSAT = (NSATGPS+NSATGLO+NSATGAL+NSATQZS+NSATCMP+NSATIRN+NSATSBS+NSATLEO);


    /** obs code: none or unknown */
    public static final byte CODE_NONE = 0;
    /** obs code: L1C/A,G1C/A,E1C (GPS,GLO,GAL,QZS,SBS) */
    public static final byte CODE_L1C = 1;
    /** obs code: L2P,G2P    (GPS,GLO) */
    public static final byte CODE_L2P = 19;
    /** obs code: L2 Z-track (GPS) */
    public static final byte CODE_L2W = 20;
    /** obs code: L5Q,E5aQ (GPS,GAL,QZS,SBS) */
    public static final byte CODE_L5Q = 25;
    /** obs code: L1C(P) (GPS,QZS) */
    public static final byte CODE_L1L = 8;
    /** obs code: L2C(M) (GPS,QZS) */
    public static final byte CODE_L2S = 16;
    /** max number of obs code */
    public static final byte MAXCODE = 68;

    /** L1/E1/B1C  frequency (Hz) */
    public static final double FREQ1 = 1.57542E9;
    /** L2 frequency (Hz) */
    public static final double FREQ2 = 1.22760E9;
    /** L5/E5a/B2a frequency (Hz) */
    public static final double FREQ5 = 1.17645E9;
}




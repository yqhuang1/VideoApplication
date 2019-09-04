package com.fmscreenrecord.record;

/**
 * 录制参数设置 
 * @author lin
 * Create：2014-12
 */

public class Settings {
    public static Rotation rotation = Rotation.VERTICAL;
    public static AudioSource audioSource = AudioSource.MIC;
    public static CpuGpu cpuGpu = CpuGpu.GPU;
    public static int width = 720;
    public static int height = 1280;
    /**
     * 5.0码率
     */
    public static int BITRATE = 1280;
    public static int fps = 15;

    public static boolean colorFix = false;
    public static VideoEncoder encoder = VideoEncoder.H264;
    public static AudioQuality audioQuality = AudioQuality.KHZ_32;
    public static VideoQuality videoQuality = VideoQuality.MBPS_10;
    public static boolean showTouches = true;

    public static enum Rotation 
    {
        VERTICAL("270"),
        HORIZONTAL("0"),
        VERTICAL_UP("90"),
        HORIZONTAL_UP("180"),;

        private String command;

        private Rotation(String command)
        {
            this.command = command;
        }

        public String getCommand() 
        {
            return command;
        }
    }

    public static String getColorFixCommand() 
    {
        if (colorFix) {
            return "BGRA";
        } else {
            return "RGBA";
        }
    }

    public static enum AudioSource 
    {
        MIC("m"), MUTE("x");

        private String command;

        private AudioSource(String command) 
        {
            this.command = command;
        }

        public String getCommand()
        {
            return this.command;
        }
    }

    public  static enum CpuGpu 
    {
        CPU("CPU"), GPU("GPU");

        private String command;

        private CpuGpu(String command) 
        {
            this.command = command;
        }

        public String getCommand()
        {
            return this.command;
        }
    }

    public static enum VideoEncoder 
    {
        MPEG_4(-2),
        H264(2),
        MPEG_4_SP(3);
        private int val;

        private VideoEncoder(int val) 
        {
            this.val = val;
        }

        int getValue() {
            return val;
        }

        public String getCommand() 
        {
            return String.valueOf(val);
        }
    }

    public  static enum AudioQuality 
    {
        KHZ_8(8000),
        KHZ_16(16000),
        KHZ_32(32000),
        KHZ_48(48000),
        KHZ_96(96000);
        private int val;

        private AudioQuality(int val) 
        {
            this.val = val;
        }

        public String getCommand()
        {
            return String.valueOf(val);
        }
    }

    public   static enum VideoQuality
    {
        MBPS_1(1000000),
        MBPS_5(5000000),
        MBPS_10(10000000),
        MBPS_15(15000000),
        MBPS_30(30000000);
        private int val;

        private VideoQuality(int val) 
        {
            this.val = val;
        }

        public String getCommand() 
        {
            return String.valueOf(val);
        }
    }
}

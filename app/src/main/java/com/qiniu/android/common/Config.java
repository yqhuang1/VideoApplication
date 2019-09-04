package com.qiniu.android.common;


public final class Config {
    public static final String VERSION = "7.0.4";

    /**
     * Ĭ���ϴ�������
     */
    public static final String UP_HOST = "upload.qiniu.com";

    /**
     * �����ϴ�����������Ĭ�Ϸ�������������ʧ��ʱʹ��
     */
    public static final String UP_HOST_BACKUP = "up.qiniu.com";

    /**
     * �����ϴ�����������Ĭ�Ϸ�������������ʧ��ʱʹ��
     */
    public static final String UP_IP_BACKUP = "183.136.139.16";
    /**
     * �ϵ��ϴ�ʱ�ķ�Ƭ��С(�ɸ�����������ʵ�����)
     */
    public static final int CHUNK_SIZE = 256 * 1024;
    /**
     * �ϵ��ϴ�ʱ�ķֿ��С(Ĭ�ϵķֿ��С, ������ı�)
     */
    public static final int BLOCK_SIZE = 4 * 1024 * 1024;
    /**
     * ����ļ���С���ڴ�ֵ��ʹ�öϵ��ϴ�, ����ʹ��Form�ϴ�
     */
    public static final int PUT_THRESHOLD = 512 * 1024;
    /**
     * ���ӳ�ʱʱ��(Ĭ��10s)
     */
    public static final int CONNECT_TIMEOUT = 10 * 1000;
    /**
     * �ظ���ʱʱ��(Ĭ��30s)
     */
    public static final int RESPONSE_TIMEOUT = 30 * 1000;
    /**
     * �ϴ�ʧ�����Դ���
     */
    public static final int RETRY_MAX = 5;
    public static final String UTF_8 = "utf-8";
    /**
     * Ĭ���ϴ�������
     */
    public static String defaultUpHost = UP_HOST;

}

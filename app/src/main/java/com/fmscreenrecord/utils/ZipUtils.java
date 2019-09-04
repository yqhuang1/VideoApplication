package com.fmscreenrecord.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Ryan
 * User: Administrator
 * Date: 12-1-26
 * Time: A.M. 9:11
 */
public class ZipUtils {
    public static void unzip(String zipFile, String targetDir) throws Exception {
        FileInputStream fis = new FileInputStream(zipFile);
        unzip(fis, targetDir);
        StreamUtil.CloseInputStream(fis);
    }

    public static void unzip(String zipFile, String targetDir, UnzipListener listener) throws Exception {
        FileInputStream fis = new FileInputStream(zipFile);
        unzip(fis, targetDir, listener);
        StreamUtil.CloseInputStream(fis);
    }

    public static void unzip(InputStream zipFileName, String outputDirectory) throws IOException {
        unzip(zipFileName, outputDirectory, null);
    }

    public static void unzip(InputStream zipFileName, String outputDirectory, UnzipListener listener) throws IOException {
        ZipInputStream in = new ZipInputStream(zipFileName);
        try {
            long length = zipFileName.available();
            long progress = 0;
            ZipEntry entry = in.getNextEntry();
            if (null != listener && null != entry) {
                listener.onDeal(entry.getName(), progress, length);
            }
            while (entry != null) {
                File file = new File(outputDirectory);
                file.mkdir();
                if (entry.isDirectory()) {
                    String name = entry.getName();
                    name = name.substring(0, name.length() - 1);
                    file = new File(outputDirectory + File.separator + name);
                    file.mkdirs();
                } else {
                    long entryLength = unzipEntity(in, outputDirectory, entry.getName());
                    progress += entryLength;
                }
                entry = in.getNextEntry();
                if (null != listener && null != entry) {
                    listener.onDeal(entry.getName(), progress, length);
                }
            }
            in.close();
        } finally {
            StreamUtil.CloseInputStream(in);
        }
    }


    public static boolean isZipFile(String absolutePath) {
        try {
            FileInputStream fis = new FileInputStream(absolutePath);
            ZipInputStream in = new ZipInputStream(fis);
            ZipEntry entry = in.getNextEntry();
            fis.close();
            in.close();
            return null != entry;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

    //read the first file in zip file and return string
    public static String unzipString(InputStream is) {
        ZipInputStream in = new ZipInputStream(is);
        try {
            ZipEntry entry = in.getNextEntry();
            String s = null;
            if (null != entry) {
                s = IOUtils.toString(in, "utf-8");
            }
            in.close();
            return s;
        } catch (Exception ex) {
            return null;
        }
    }

    public static void unzip(InputStream zipIs, String targetDir, String zipFileName, String outFileName) {
        try {
            ZipInputStream in = new ZipInputStream(zipIs);
            ZipEntry entry;
            while (null != (entry = in.getNextEntry())) {
                if (entry.getName().equals(zipFileName)) {
                    unzipEntity(in, targetDir, outFileName);
                    break;
                }
            }
            in.close();
        } catch (Exception ex) {
            throw new IllegalStateException("unzip file error");
        }
    }

    public static void unzip(String zipFile, String targetDir, String zipFileName, String outFileName) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(zipFile);
            unzip(fis, targetDir, zipFileName, outFileName);
        } catch (Exception e) {
            throw new IllegalStateException("unzip file error");
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    private static long unzipEntity(ZipInputStream in, String targetDir, String outName) throws IOException {
        int count;
        byte data[] = new byte[512000];
        File file = new File(targetDir + File.separator + outName);
        if (file.exists() || file.createNewFile()) {
            FileOutputStream out = new FileOutputStream(file);
            BufferedOutputStream dest = new BufferedOutputStream(out, 512000);
            long entryLength = 0;
            while ((count = in.read(data, 0, 512000)) != -1) {
                entryLength += (int) (count * 0.91f);
                dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
            out.close();
            return entryLength;
        } else {
            throw new IllegalStateException("create entity file error!");
        }
    }

    public static interface UnzipListener {

        void onDeal(String name, long progress, long count);
    }
}
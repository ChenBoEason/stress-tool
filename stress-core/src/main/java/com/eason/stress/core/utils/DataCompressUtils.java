package com.eason.stress.core.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.*;

/**
 * @author Eason(bo.chenb)
 * @description 数据解压缩工具
 * @date 2019-12-16
 **/
public class DataCompressUtils {

    public static final String DEFAULT_CHARSET = "UTF-8";
    /**
     * 压缩数据
     *
     * @param input 压缩内容
     * @return
     */
    public static String compressByGZIP(String input) throws IOException {

        if (input == null || input.length() == 0) {
            return input;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        GZIPOutputStream gzip = null;

        try {

            gzip = new GZIPOutputStream(out);
            gzip.write(input.getBytes(DEFAULT_CHARSET));

        } catch (Exception e) {

            throw new IOException("compress data exception, please check your data");
        } finally {

            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                }
            }

            close(null, out);

        }

        /* 关闭gzip后再转字节流
        * Base64.encodeBase64String(out.toByteArray()) 和 BASE64Encoder().encode(out.toByteArray() 结果一样但前者为tomcat 后者为jdk自带工具
        *
        * */
        return new BASE64Encoder().encode(out.toByteArray());
    }

    /**
     * 解压数据
     *
     * @param compressedStr
     * @return
     * @throws Exception
     */
    public static String decompressByGZIP(String compressedStr) throws IOException {

        if (compressedStr == null || compressedStr.length() == 0) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ByteArrayInputStream in = null;

        GZIPInputStream ginzip = null;

        byte[] compressed = null;
        String decompressed = null;
        try {

            compressed = new BASE64Decoder().decodeBuffer(compressedStr);
            in = new ByteArrayInputStream(compressed);

            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();

        } catch (IOException e) {

            throw new IOException("decompress data exception, please check your data");

        } finally {

            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException e) {
                }
            }

            close(in, out);

        }

        return decompressed;
    }

    /**
     * 使用zip进行压缩
     *
     * @param str 压缩前的文本
     * @return 返回压缩后的文本
     */
    public static final String zip(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return null;
        }

        byte[] compressed;
        ByteArrayOutputStream out = null;
        ZipOutputStream zout = null;

        try {
            out = new ByteArrayOutputStream();
            zout = new ZipOutputStream(out);
            zout.putNextEntry(new ZipEntry("0"));
            zout.write(str.getBytes());
            zout.closeEntry();
            compressed = out.toByteArray();

        } catch (IOException e) {

            throw new IOException("compress data exception, please check your data");

        } finally {
            if (zout != null) {
                try {
                    zout.close();
                } catch (IOException e) {
                }
            }
            close(null, out);
        }

        return new BASE64Encoder().encodeBuffer(compressed);

    }

    /**
     * 使用zip进行解压缩
     *
     * @param compressedStr 压缩后的文本
     * @return 解压后的字符串
     */
    public static final String unzip(String compressedStr) throws IOException {
        if (compressedStr == null || compressedStr.length() == 0) {
            return null;
        }

        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        ZipInputStream zin = null;

        try {
            byte[] compressed = new BASE64Decoder().decodeBuffer(compressedStr);
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(compressed);
            zin = new ZipInputStream(in);
            zin.getNextEntry();
            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = zin.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            return out.toString();
        } catch (IOException e) {

            throw new IOException("decompress data exception, please check your data");
        } finally {
            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                }
            }
            close(in, out);
        }

    }

    public static void close(ByteArrayInputStream in, ByteArrayOutputStream out) {

        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
    }

}

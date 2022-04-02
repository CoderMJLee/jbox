package io.github.codermjlee.common.util.io;

import io.github.codermjlee.common.util.binary.Bytes;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 压缩和解压缩
 *
 * @author MJ
 */
public class Zips {
    /* 是否覆盖整个文件 */
    private boolean overwrite = true;
    /* 输入文件 */
    private File inFile;
    /* 输出文件 */
    private File outFile;
    private byte[] bytes;
    private Predicate<String> entryFilter;

    public static Zips alloc() {
        return new Zips();
    }

    public Zips overwrite(boolean overwrite) {
        this.overwrite = overwrite;
        return this;
    }

    public Zips entryFilter(Predicate<String> entryFilter) {
        this.entryFilter = entryFilter;
        return this;
    }

    public Zips bytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    public Zips inFile(File inFile) {
        this.inFile = inFile;
        return this;
    }

    public Zips inFile(String inFilePath) {
        if (inFilePath == null) return this;
        return inFile(new File(inFilePath));
    }

    public Zips outFile(File outFile) {
        this.outFile = outFile;
        return this;
    }

    public Zips outFile(String outFilePath) {
        return outFile(new File(outFilePath));
    }

    /**
     * 压缩
     * @return 压缩成字节数组
     */
    public byte[] zip() {
        if (Bytes.empty(bytes)) return null;
        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos)
        ) {
            gzip.write(bytes);
            gzip.finish();
            return baos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 解压缩
     * @return 解压缩成字节数组
     */
    public byte[] unzip() {
        if (Bytes.empty(bytes)) return null;
        try (
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            GZIPInputStream gzip = new GZIPInputStream(bais);
            ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = gzip.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            return baos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 解压文件
     */
    public void unzipFile() {
        if (inFile == null || outFile == null) return;
        if (!inFile.exists()) return;
        if (!outFile.exists()) {
            outFile.mkdirs();
        }
        Ios ios = Ios.alloc();
        try (ZipFile zip = new ZipFile(inFile, Charset.forName("GBK"))) {
            for (Enumeration<?> entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                if (entryFilter != null && !entryFilter.test(zipEntryName)) continue;

                try (InputStream in = zip.getInputStream(entry)) {
                    String outPath = (outFile.getAbsolutePath() + "/" + zipEntryName);
                    ios.inFile(outPath).mkParents();
                    if (overwrite) {
                        ios.delete();
                    }
                    try (OutputStream out = new FileOutputStream(outPath)) {
                        byte[] buf1 = new byte[1024];
                        int len;
                        while ((len = in.read(buf1)) > 0) {
                            out.write(buf1, 0, len);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

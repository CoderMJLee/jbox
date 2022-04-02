package io.github.codermjlee.common.util.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * IO操作
 *
 * @author MJ
 */
public class Ios {
    public static boolean DEBUG = false;
    /* 字符串编码 */
    private String charset = "UTF8";
    /* 是否覆盖整个文件 */
    private boolean overwrite = true;
    /* 输入文件 */
    private File inFile;
    /* 输出文件 */
    private File outFile;
    /* 文件过滤器 */
    private FileFilter filter;
    /* 输入数据 */
    private Object inData;
    /* 消费文件 */
    private Consumer<File> consumer;

    public Ios charset(String charset) {
        this.charset = charset;
        return this;
    }

    public Ios overwrite(boolean overwrite) {
        this.overwrite = overwrite;
        return this;
    }

    public Ios inFile(File inFile) {
        this.inFile = inFile;
        return this;
    }

    public File getInFile() {
        return inFile;
    }

    public File getOutFile() {
        return outFile;
    }

    public Ios inFile(String inFilePath) {
        if (inFilePath == null) return this;
        return inFile(new File(inFilePath));
    }

    public Ios outFile(File outFile) {
        this.outFile = outFile;
        return this;
    }

    public Ios outFile(String outFilePath) {
        return outFile(new File(outFilePath));
    }

    public Ios filter(FileFilter filter) {
        this.filter = filter;
        return this;
    }

    public Ios inData(Object inData) {
        this.inData = inData;
        return this;
    }

    public Ios consumer(Consumer<File> consumer) {
        this.consumer = consumer;
        return this;
    }

    public static Ios alloc() {
        return new Ios();
    }

    /**
     * 移动文件\文件夹
     */
    public void move() {
        if (inFile == null || outFile == null) return;
        if (!inFile.exists()) return;
        if (outFileOverwrite(null)) return;

        try {
            if (inFile.isFile()) {
                FileUtils.moveFile(inFile, outFile);
            } else {
                FileUtils.moveDirectory(inFile, outFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DEBUG) {
            System.out.println("move [" + inFile + "] -> [" + outFile + "]");
        }
    }

    /**
     * 拷贝文件\文件夹（符合filter的才拷贝）
     * @return 本身
     */
    public Ios copy() {
        if (inFile == null || outFile == null) return this;
        if (!inFile.exists()) return this;
        if (outFileOverwrite(null)) return this;

        try {
            if (inFile.isFile()) {
                FileUtils.copyFile(inFile, outFile);
            } else {
                FileUtils.copyDirectory(inFile, outFile, filter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DEBUG) {
            System.out.println("copy [" + inFile + "] -> [" + outFile + "]");
        }
        return this;
    }

    public boolean exists() {
        if (inFile == null) return false;
        return inFile.exists();
    }

    public Ios delete() {
        if (inFile == null) return this;
        if (!inFile.exists()) return this;

        try {
            FileUtils.forceDelete(inFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DEBUG) {
            System.out.println("delete -> " + inFile);
        }
        return this;
    }


    /**
     * 删除文件夹里面的所有文件和文件夹
     */
    public void clean() {
        if (inFile == null || inFile.isFile()) return;

        File[] files = inFile.listFiles();
        if (files == null) return;
        for (File file : files) {
            Ios fileIos = new Ios().inFile(file);
            if (filter == null || filter.accept(file)) {
                fileIos.delete();
                if (DEBUG) {
                    System.out.println("clean -> " + file);
                }
            }
            if (file.isDirectory()) {
                // 这句代码要放在if外面
                fileIos.filter(filter).clean();
            }
        }
    }

    public String absolutePath() {
        return (inFile == null) ? null : inFile.getAbsolutePath();
    }

    /**
     * 获取文件名
     * inFile "a\b\c.mp4" or "a\b.mp4\"
     * @return "c.mp4" or "b.mp4"
     */
    public String name() {
        if (inFile == null) return null;
        String path = inFile.getAbsolutePath();
        while (path.endsWith("/") || path.endsWith("\\")) {
            path = path.substring(0, path.length() - 1);
        }
        return FilenameUtils.getName(path);
    }

    /**
     * 获取文件名（不带扩展名）
     * inFile  "a\b\c.mp4" or "a\b\"
     * @return "c" or "b"
     */
    public String baseName() {
        if (inFile == null) return null;
        String path = inFile.getAbsolutePath();
        while (path.endsWith("/") || path.endsWith("\\")) {
            path = path.substring(0, path.length() - 1);
        }
        return FilenameUtils.getBaseName(path);
    }

    /**
     * 获得文件拓展名
     * @return 文件拓展名
     */
    public String extension() {
        if (inFile == null) return null;
        return FilenameUtils.getExtension(inFile.getAbsolutePath());
    }

    public void writeBase64() {
        if (outFile == null || inData == null) return;
        String base64 = inData.toString();
        if (outFileOverwrite(true)) return;

        // base64处理
        String some = "base64,";
        int index = base64.indexOf(some);
        if (index != -1) {
            base64 = base64.substring(index + some.length());
        }
        Base64.Decoder decoder = Base64.getDecoder();

        try {
            FileUtils.writeByteArrayToFile(outFile, decoder.decode(base64));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String base64() {
        return Base64.getEncoder().encodeToString(bytes());
    }

    public byte[] bytes() {
        if (inFile == null) return null;
        if (!inFile.exists()) return null;
        try (InputStream in = new FileInputStream(inFile)) {
            byte[] data = new byte[in.available()];
            in.read(data);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将toString内容写入文件
     */
    public void writeString() {
        if (outFile == null || inData == null || charset == null) return;
        if (outFileOverwrite(true)) return;
        try {
            FileUtils.writeStringToFile(outFile, inData.toString(), charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeBytes() {
        if (outFile == null || inData == null) return;
        if (outFileOverwrite(true)) return;

        try {
            FileUtils.writeByteArrayToFile(outFile, (byte[]) inData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件
     * @return 文件内容
     */
    public String readString() {
        if (inFile == null || charset == null) return null;
        if (!inFile.exists() || inFile.isDirectory()) return null;
        try {
            return FileUtils.readFileToString(inFile, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 下载文件
     * @param url 文件的url
     */
    public void writeURL(String url) {
        if (outFile == null) return;
        if (outFileOverwrite(true)) return;

        try {
            FileUtils.copyURLToFile(new URL(url), outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void list() {
        if (inFile == null || consumer == null) return;
        if (!inFile.exists() || inFile.isFile()) return;

        File[] files = inFile.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (filter == null || filter.accept(file)) {
                consumer.accept(file);
            }
            // 这句要放到if外面
            if (file.isDirectory()) {
                new Ios().inFile(file).filter(filter).consumer(consumer).list();
            }
        }
    }

    public void list(String pathRegex) {
        if (inFile == null || consumer == null) return;
        if (pathRegex == null) return;
        if (!inFile.exists() || inFile.isFile()) return;

        File[] files = inFile.listFiles();
        if (files == null) return;
        Pattern pattern = Pattern.compile(pathRegex);
        for (File file : files) {
            String path = file.getAbsolutePath();
            if (pattern.matcher(path).find()) {
                consumer.accept(file);
            }
            // 这句要放到if外面
            if (file.isDirectory()) {
                new Ios().inFile(file).consumer(consumer).list(pathRegex);
            }
        }
    }

    public Ios mkParents() {
        if (inFile == null) return this;
        File parent = inFile.getParentFile();
        if (parent == null) return this;
        parent.mkdirs();
        return this;
    }

    private Ios() {

    }

    /**
     * @param file 是否必须为文件类型
     */
    private boolean outFileOverwrite(Boolean file) {
        Ios outIos = new Ios().inFile(outFile);
        if (outFile.exists()) {
            if (overwrite) {
                if (file != null) {
                    if (file) {
                        if (outFile.isDirectory()) return true;
                    } else {
                        if (outFile.isFile()) return true;
                    }
                }
                outIos.delete();
            } else {
                return true;
            }
        }
        outIos.mkParents();
        return false;
    }
}

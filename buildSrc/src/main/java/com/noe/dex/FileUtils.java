package com.noe.dex;

import com.android.ddmlib.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huiyh on 2016/2/15.
 */
public class FileUtils {

    /**
     * 递归删除文件夹
     *
     * @param dirPath
     * @return
     */
    public static boolean deleteDir(String dirPath) {
        boolean success = false;
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDir(files[i].getAbsolutePath());
                } else {
                    success &= files[i].delete();
                }
            }
            success &= dir.delete();
            return success;
        } else {
            return success;
        }
    }

    /**
     * 得到解压到Jar的文件夹
     *
     * @param jarPath
     * @return
     */
    public static String getOutJarFileName(String jarPath) {
        String separator = File.separator;
        String[] splits = FileUtils.splitPath(jarPath);
        StringBuilder sb = new StringBuilder();
        // 适配Linux && OS X
        if (splits[0] != null && !splits[0].equals("")
                && !splits[0].equals(" ")) {
            sb.append(File.separator + splits[0]);
        }
        for (int i = 1; i < splits.length - 1; i++) {
            sb.append(File.separator + splits[i]);
        }
        sb.append(File.separator);
        String jarName = splits[splits.length - 1];
        sb.append(jarName.substring(0, jarName.length() - ".jar".length()));
        sb.append(File.separator);
        return sb.toString();
    }

    /**
     * 解压Jar文件到与Jar同目录的与Jar相同名字的文件夹中
     *
     * @param jarPath
     * @throws IOException
     */
    public static void unzipJar(String jarPath) throws IOException {
        // 1.通过Jar的地址，找出Jar的目录
        String outJarPath = getOutJarFileName(jarPath);
        // 2.创建解压的文件
        createJarDir(outJarPath);
        // 3.解压
        doUnZipJar(jarPath, outJarPath);
    }

    /**
     * 创建Jar的文件夹
     *
     * @param path
     */
    private static void createJarDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            deleteDir(dir.getAbsolutePath());
        }
        dir.mkdirs();
    }

    /**
     * 做压缩操作
     *
     * @param jarPath
     * @param outJarFile
     * @throws IOException
     */
    private static void doUnZipJar(String jarPath, String outJarFile) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPath);
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String outFileName = outJarFile + jarEntry.getName();
                File f = new File(outFileName);
                makeSupDir(outFileName);
                if (jarEntry.isDirectory()) {
                    continue;
                }
                writeFile(jarFile.getInputStream(jarEntry), f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(jarFile);
        }

    }

    private static void makeSupDir(String outFileName) {
        Pattern p = Pattern.compile("[/\\" + File.separator + "]");
        Matcher m = p.matcher(outFileName);
        while (m.find()) {
            int index = m.start();
            String subDir = outFileName.substring(0, index);
            File subDirFile = new File(subDir);
            if (!subDirFile.exists()) {
                subDirFile.mkdir();
            }
        }
    }

    private static void writeFile(InputStream ips, File outputFile) {
        FileOutputStream fileOutputStream = null;
        OutputStream ops = null;
        try {
            fileOutputStream = new FileOutputStream(outputFile);
            ops = new BufferedOutputStream(fileOutputStream);
            byte[] buffer = new byte[1024];
            int nBytes = 0;
            while ((nBytes = ips.read(buffer)) > 0) {
                ops.write(buffer, 0, nBytes);
            }
            ops.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            IOUtils.close(ops);
            IOUtils.close(ips);
            IOUtils.close(fileOutputStream);
        }
    }

    public static String readFile(String filePath) {
        if (filePath == null || filePath.length() == 0 || filePath.equals("")) {
            return null;
        }
        File file = new File(filePath);
        return readFile(file);
    }

    /**
     * 读文件
     *
     * @param file
     * @return
     */
    public static String readFile(File file) {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            StringBuilder builder = new StringBuilder();
            byte[] bytes = new byte[1024];
            int len;// 每次读取的实际长度
            while ((len = in.read(bytes)) != -1) {
                builder.append(new String(bytes, 0, len));
            }
            return builder.toString();
        } catch (FileNotFoundException e) {
            Log.e("文件: " + file.getAbsolutePath(),e);
        } catch (IOException e) {
            Log.e("文件: " + file.getAbsolutePath(),e);
        } finally {
            IOUtils.close(in);
        }
        return null;
    }

    /**
     * 复制文件
     *
     * @param fromFilePath
     * @param toFilePath
     * @return
     */
    public static boolean copyFile(String fromFilePath, String toFilePath) {
        boolean success = true;
        int byteRead = 0;
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(fromFilePath);
            out = new FileOutputStream(toFilePath);
            byte[] buffer = new byte[1024];
            while ((byteRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteRead);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } finally {
            IOUtils.close(in);
            IOUtils.close(out);
        }
        return success;
    }

    /**
     * 将文件内容按行读取放到Set里面
     *
     * @param filePath
     * @return
     */
    public static Set<String> readFile2Set(String filePath) {
        Set<String> set = new HashSet<>();
        Reader fileReader = null;
        BufferedReader br = null;
        try {
            fileReader = new FileReader(new File(filePath));
            br = new BufferedReader(fileReader);
            String line = br.readLine();
            while (line != null) {
                set.add(line.trim());
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br);
            IOUtils.close(fileReader);
        }
        return set;
    }

    /**
     * 追加写内容
     * 将Set中的内容写到file中去，每一个是一行
     *
     * @param set
     * @param filePath
     */
    public static void writeFileAppend(Set<String> set, String filePath) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(filePath, true);
            os.write(System.getProperty("line.separator").getBytes());
            for (Iterator<String> iterator = set.iterator(); iterator.hasNext(); ) {
                String string = iterator.next();
                byte[] temp = string.getBytes();
                os.write(temp);
                byte[] newLine = System.getProperty("line.separator").getBytes();
                os.write(newLine);
            }
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(os);
        }
    }

    public static void readMappingFile(String filePath, OnMappingReadingListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener 不能为空!");
        }
        Reader fileReader = null;
        BufferedReader br = null;
        try {
            fileReader = new FileReader(new File(filePath));
            br = new BufferedReader(fileReader);
            String line = br.readLine();
            while (line != null) {
                if (line.endsWith(":")) {
                    String[] mappings = line.split(" -> ");
                    if (mappings.length != 2) {
                    }
                    String first = mappings[0].trim();
                    String second = mappings[1].trim();
                    listener.onLine(line, first, second.substring(0, second.length() - 1));
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br);
            IOUtils.close(fileReader);
        }
    }

    public interface OnMappingReadingListener {
        void onLine(String totalLine, String originalPackage,
                    String proguardPackage);
    }

    public static void checkAndWriteFileAppend(String checkFile, Set<String> set, String filePath) {
        String content = readFile(checkFile);
        OutputStream os = null;
        try {
            os = new FileOutputStream(filePath, true);
            os.write(System.getProperty("line.separator").getBytes());
            for (Iterator<String> iterator = set.iterator(); iterator.hasNext(); ) {
                String string = iterator.next();
                if (content.contains(string)) {
                    // 如果文件里面已经有这一行了，就跳过
                    continue;
                }
                byte[] temp = string.getBytes();
                os.write(temp);
                byte[] newLine = System.getProperty("line.separator").getBytes();
                os.write(newLine);
            }
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(os);
        }
    }

    public static String[] splitPath(String path) {
        String os = System.getProperty("os.name");
        if (os != null && os.length() > 0) {
            String separator = File.separator;
            if (os.toLowerCase().startsWith("win")) {
                return path.split(separator + separator);
            } else {
                return path.split(separator);
            }
        }
        return new String[0];
    }
}

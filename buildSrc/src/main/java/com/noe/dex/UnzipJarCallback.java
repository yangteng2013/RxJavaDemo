package com.noe.dex;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by yuyidong on 16/4/27.
 */
public class UnzipJarCallback implements Callable<Boolean> {
    private String mJarPath;

    public UnzipJarCallback(String jarPath) {
        mJarPath = jarPath;
    }

    @Override
    public Boolean call() throws Exception {
        boolean success = true;
        String jar2classDir = FileUtils.getOutJarFileName(mJarPath);
        try {
            FileUtils.unzipJar(mJarPath);
        } catch (IOException e) {
            e.printStackTrace();
            FileUtils.deleteDir(jar2classDir);
            System.out.println("解压Jar发生异常 " + e.getMessage());
            success = false;
        }
        return success;
    }
}

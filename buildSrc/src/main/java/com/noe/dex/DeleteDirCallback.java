package com.noe.dex;

import java.util.concurrent.Callable;

/**
 * Created by yuyidong on 16/4/27.
 */
public class DeleteDirCallback implements Callable<Boolean> {
    private String mJar2classDir;

    public DeleteDirCallback(String jar2classDir) {
        mJar2classDir = jar2classDir;
    }

    @Override
    public Boolean call() throws Exception {
        FileUtils.deleteDir(mJar2classDir);
        return true;
    }
}
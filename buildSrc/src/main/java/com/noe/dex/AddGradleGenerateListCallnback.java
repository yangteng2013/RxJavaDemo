package com.noe.dex;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by yuyidong on 16/4/27.
 */
public class AddGradleGenerateListCallnback implements Callable<Boolean> {
    private String mMainDexListPath;
    private String mSupportKeepPath;
    private String mMultidexKeepPath;

    public AddGradleGenerateListCallnback(String mainDexListPath, String supportKeepPath, String multidexKeepPath) {
        mMainDexListPath = mainDexListPath;
        mSupportKeepPath = supportKeepPath;
        mMultidexKeepPath = multidexKeepPath;
    }

    @Override
    public Boolean call() throws Exception {
        boolean success = true;
        // 先将写maindexlist.txt写到multidex.keep中
        String mainDexListContent = FileUtils.readFile(mMainDexListPath);
        success = FileUtils.copyFile(mMainDexListPath, mMultidexKeepPath);
        // 再将support.keep写到multidex.keep中
        Set<String> deleteSet = new HashSet<>();
        Set<String> set = FileUtils.readFile2Set(mSupportKeepPath);
        for (Iterator<String> iterator = set.iterator(); iterator.hasNext(); ) {
            String line = iterator.next();
            if (mainDexListContent.contains(line)) {
                deleteSet.add(line);
            }
        }
        for (Iterator<String> iterator = deleteSet.iterator(); iterator
                .hasNext(); ) {
            String line = iterator.next();
            set.remove(line);
        }
        FileUtils.writeFileAppend(set, mMultidexKeepPath);
        return success;
    }
}

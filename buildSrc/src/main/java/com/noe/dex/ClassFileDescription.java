package com.noe.dex;

import java.io.File;

/**
 * Created by yuyidong on 16/4/27.
 */
public class ClassFileDescription {
    private String mAbsolutePath;// 类似/Users/yuyidong/AndroidStudioProjects/V7.4.0-GanjiLife-dexloader/GJAndroidClient15/build/intermediates/multi-dex/dev/debug/allclasses/java/util
    private String mClassName;

    public ClassFileDescription(String packageName, String outJarDir) {
        String[] p = FileUtils.splitPath(packageName);
        if (p.length == 0) {
            throw new IllegalArgumentException("没对头!!!");
        }
        StringBuilder sb = new StringBuilder(outJarDir);
        for (int i = 0; i < p.length - 1; i++) {
            sb.append(p[i]);
            if (i != p.length - 2) {
                sb.append(File.separator);
            }
        }
        mAbsolutePath = sb.toString();
        mClassName = p[p.length - 1];
    }

    public String getAbsolutePath() {
        return mAbsolutePath;
    }

    public String getClassName() {
        return mClassName;
    }
}

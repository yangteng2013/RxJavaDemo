package com.noe.dex;

import java.io.File;

/**
 * Created by yuyidong on 16/4/27.
 */
public class AnnotationFileDescription {
    private String mAbsolutePath;// 该类在项目中的地址
    private String mJavaName;// 该类的类名
    private String mJavaPackage;// javaPackage

    public AnnotationFileDescription(String absolutePath, String javaPackage) {
        this.mAbsolutePath = absolutePath;
        String[] names = FileUtils.splitPath(javaPackage.replace('.', File.separatorChar));
        mJavaName = names[names.length - 1];
        this.mJavaPackage = javaPackage;
    }

    public String getAbsolutePath() {
        return mAbsolutePath;
    }

    public String getJavaName() {
        return mJavaName;
    }

    public String getJavaPackage() {
        return mJavaPackage;
    }

    @Override
    public String toString() {
        return "AnnotationFileDescription [mAbsolutePath=" + mAbsolutePath
                + ", mJavaName=" + mJavaName + ", mJavaPackage=" + mJavaPackage
                + "]";
    }
}

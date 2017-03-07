package com.noe.dex;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;

/**
 * Created by yuyidong on 16/4/27.
 */
public abstract class AbsGetAnnotationPackage {
    /**
     * 遍历各个工程的src文件，找到所有的java文件
     *
     * @param absoluteProjectJavaCodePath
     * @param absolutePath
     * @param fileDescriptionSet
     */
    protected void getJavaPackage(String absoluteProjectJavaCodePath,
                                  String absolutePath,
                                  Set<AnnotationFileDescription> fileDescriptionSet) {
        File file = new File(absolutePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File currentFile : files) {
                if (currentFile.isDirectory()) {
                    getJavaPackage(absoluteProjectJavaCodePath, currentFile.getAbsolutePath(), fileDescriptionSet);
                } else {
                    String javaPackage = currentFile.getAbsolutePath().substring(
                            absoluteProjectJavaCodePath.length(),
                            currentFile.getAbsolutePath().length() - ".java".length()).replace(File.separatorChar, '.');
                    AnnotationFileDescription annotationFileDescription =
                            new AnnotationFileDescription(absolutePath, filterJavaPackage(javaPackage));
                    fileDescriptionSet.add(annotationFileDescription);
                }
            }
        } else {
            String javaPackage = file.getAbsolutePath().substring(
                    absoluteProjectJavaCodePath.length(),
                    file.getAbsolutePath().length() - ".java".length()).replace(File.separatorChar, '.');
            AnnotationFileDescription annotationFileDescription =
                    new AnnotationFileDescription(absolutePath, filterJavaPackage(javaPackage));
            fileDescriptionSet.add(annotationFileDescription);
        }
    }

    protected void getJavaPackage(
            String absoluteProjectJavaCodePath,
            String absolutePath,
            Map<String, AnnotationFileDescription> fileDescriptionByJavaPackageMap) {
        File file = new File(absolutePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File currentFile : files) {
                if (currentFile.isDirectory()) {
                    getJavaPackage(
                            absoluteProjectJavaCodePath,
                            currentFile.getAbsolutePath(),
                            fileDescriptionByJavaPackageMap);
                } else {
                    String javaPackage = currentFile.getAbsolutePath().substring(
                            absoluteProjectJavaCodePath.length(),
                            currentFile.getAbsolutePath().length() - ".java".length()).replace(File.separatorChar, '.');
                    AnnotationFileDescription annotationFileDescription =
                            new AnnotationFileDescription(absolutePath, filterJavaPackage(javaPackage));
                    fileDescriptionByJavaPackageMap.put(javaPackage, annotationFileDescription);
                }
            }
        } else {
            String javaPackage = file.getAbsolutePath().substring(
                    absoluteProjectJavaCodePath.length(),
                    file.getAbsolutePath().length() - ".java".length()).replace(File.separatorChar, '.');
            AnnotationFileDescription annotationFileDescription =
                    new AnnotationFileDescription(absolutePath, filterJavaPackage(javaPackage));
            fileDescriptionByJavaPackageMap.put(javaPackage, annotationFileDescription);
        }

    }

    /**
     * 因为新建的工程是AS格式的，以前的格式都是eclipse的，所以这里判断一下
     *
     * @param javaPackage
     * @return
     */
    protected String filterJavaPackage(String javaPackage) {
        if (javaPackage.startsWith("main.java.")) {
            return javaPackage.substring("main.java.".length(), javaPackage.length());
        } else {
            return javaPackage;
        }
    }

    protected static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            add.setAccessible(true);
            return add;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

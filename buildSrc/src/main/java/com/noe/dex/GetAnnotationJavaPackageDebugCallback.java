package com.noe.dex;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by yuyidong on 16/4/27.
 */
public class GetAnnotationJavaPackageDebugCallback extends AbsGetAnnotationPackage
        implements Callable<Set<AnnotationFileDescription>> {

    private String[] mAbsoluteJavaFilePaths;

    private String mJarPath;
    private String mAndroidJarPath;

    private boolean mIsDebug = false;

    public GetAnnotationJavaPackageDebugCallback(String debugJarPath,
                                                 String androidJarPath,
                                                 String[] absoluteJavaFilePaths,
                                                 boolean isDebug) {
        mJarPath = debugJarPath;
        mAndroidJarPath = androidJarPath;
        mAbsoluteJavaFilePaths = absoluteJavaFilePaths;
        mIsDebug = isDebug;
    }

    @Override
    public Set<AnnotationFileDescription> call() throws Exception {
        Set<AnnotationFileDescription> allFileDescriptionSet = new HashSet<>();
        Set<AnnotationFileDescription> annotationJavaPackage = new HashSet<>();
        // 花费时间大概30ms
        for (String absoluteProjectJavaFilePath : mAbsoluteJavaFilePaths) {
            getJavaPackage(
                    absoluteProjectJavaFilePath,
                    absoluteProjectJavaFilePath,
                    allFileDescriptionSet);
        }
        String codePath = "file:" + mJarPath;
        String androidPath = "file:" + mAndroidJarPath;
        final URLClassLoader classloader = new URLClassLoader(new URL[]{new URL(androidPath), new URL(codePath)});
//        Method method = initAddMethod();
//        URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
//        method.invoke(classloader, new URL(androidPath));
//        method.invoke(classloader, new URL(codePath));
        // 花费时间大概600ms
        for (Iterator<AnnotationFileDescription> iterator = allFileDescriptionSet.iterator(); iterator.hasNext(); ) {
            AnnotationFileDescription annotationFileDescription = iterator.next();
            try {
                Class clazz = classloader.loadClass(annotationFileDescription.getJavaPackage());
                Annotation[] annotations = clazz.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.toString().contains("MainDex")) {
                        annotationJavaPackage.add(annotationFileDescription);
                        if (mIsDebug) {
                            System.out.println("找到注解类--->" + annotationFileDescription.getJavaPackage());
                        }
                    }
                }
            } catch (Exception e) {
                if (mIsDebug) {
                    System.out.println("jar中找不到类-->" + annotationFileDescription.getJavaPackage() + "   " + e.getMessage());
                }
                e.printStackTrace();
            }
        }
        classloader.close();
        return annotationJavaPackage;
    }
}

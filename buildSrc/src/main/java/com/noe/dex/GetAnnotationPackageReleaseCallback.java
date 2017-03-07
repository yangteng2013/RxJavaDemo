package com.noe.dex;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by yuyidong on 16/4/27.
 */
public class GetAnnotationPackageReleaseCallback extends AbsGetAnnotationPackage
        implements Callable<Set<AnnotationFileDescription>> {
    private String[] mAbsoluteJavaFilePaths;

    private String mJarPath;
    private String mAndroidJarPath;
    private String mHttpClientPath;
    private String mMappingFilePath;

    private boolean mIsDebug = true;

    public GetAnnotationPackageReleaseCallback(String jarPath, String androidJarPath, String httpClientPath,
                                               String[] absoluteJavaFilePaths, String mappingFilePath,
                                               boolean isDebug) {
        this.mAbsoluteJavaFilePaths = absoluteJavaFilePaths;
        this.mJarPath = jarPath;
        this.mHttpClientPath = httpClientPath;
        this.mAndroidJarPath = androidJarPath;
        this.mMappingFilePath = mappingFilePath;
        this.mIsDebug = isDebug;
    }

    @Override
    public Set<AnnotationFileDescription> call() throws Exception {
        final Map<String, AnnotationFileDescription> allFileDescriptionByJavaPackageMap = new HashMap<>();
        final Set<AnnotationFileDescription> annotationJavaPackage = new HashSet<>();
        for (String absoluteProjectJavaFilePath : mAbsoluteJavaFilePaths) {
            getJavaPackage(absoluteProjectJavaFilePath,
                    absoluteProjectJavaFilePath,
                    allFileDescriptionByJavaPackageMap);
        }
        String codePath = "file:" + mJarPath;
        String androidPath = "file:" + mAndroidJarPath;
        String httpClientPath = "file:" + mHttpClientPath;
        final URLClassLoader classloader = new URLClassLoader(new URL[]{new URL(androidPath),
                new URL(codePath), new URL(httpClientPath)});
//        Method method = initAddMethod();
//        URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
//        method.invoke(classloader, new URL(androidPath));
//        method.invoke(classloader, new URL(codePath));
        FileUtils.readMappingFile(mMappingFilePath, new FileUtils.OnMappingReadingListener() {

            @Override
            public void onLine(String totalLine,
                               String originalPackage,
                               String proguardPackage) {
                try {
                    Class clazz = classloader.loadClass(proguardPackage);
                    Annotation[] annotations = clazz.getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation.toString().contains("MainDex")) {
                            AnnotationFileDescription annotationFileDescription = allFileDescriptionByJavaPackageMap.get(originalPackage);
                            annotationJavaPackage.add(annotationFileDescription);
                            if (mIsDebug) {
                                System.out.println("找到注解类--->" + proguardPackage + "   " + originalPackage + "   ");
                            }
                        }
                    }
                } catch (Exception e) {
                    if (mIsDebug) {
                        System.out.println("jar中找不到类-->"
                                + proguardPackage + "   "
                                + originalPackage);
                        e.printStackTrace();
                    }
                }
            }
        });
        classloader.close();
        System.out.println("  -------   " + annotationJavaPackage.size());
        return annotationJavaPackage;
    }
}

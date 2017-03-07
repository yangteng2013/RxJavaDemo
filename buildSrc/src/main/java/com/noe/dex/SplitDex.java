package com.noe.dex;

import org.xml.sax.SAXException;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by yuyidong on 16/4/26.
 */
public class SplitDex {

    public static void process(String rootPath, String variant, String buildType, boolean isDebug) {
        final Set<String> mJavaPackageSet = new HashSet<>();
        final DexProject project = new DexProject(rootPath, variant, buildType);
        ExecutorService pool = Executors.newFixedThreadPool(3);

        // 异步解压jar文件
        String jarPath = null;
        if (DexProject.TYPE_BUILD_DEBUG.equals(buildType)) {
            jarPath = project.getDebugJarFile();
        } else {
            jarPath = project.getReleaseJarFile();
        }
        String jar2classDir = FileUtils.getOutJarFileName(jarPath);
        // 异步解压
        Future<Boolean> futureUnzip = pool.submit(new UnzipJarCallback(jarPath));
        // 异步将一些系统生成的文件写入
        Future<Boolean> futureGenerateFile = pool.submit(new AddGradleGenerateListCallnback(
                project.getMainDexListPath(),
                project.getSupportKeepPath(),
                project.getMultidexKeepPath()));
        // 异步得到注解了类
        Future<Set<AnnotationFileDescription>> futureGetAnnotationJavaPackage = null;
        if (DexProject.TYPE_BUILD_DEBUG.equals(buildType)) {
            futureGetAnnotationJavaPackage = pool
                    .submit(new GetAnnotationJavaPackageDebugCallback(jarPath,
                            project.getAndroidJar(), project.getAllProjectCodePath(),
                            isDebug));
        } else {
            futureGetAnnotationJavaPackage = pool
                    .submit(new GetAnnotationPackageReleaseCallback(jarPath,
                            project.getAndroidJar(), project.getHttpClientJar(),
                            project.getAllProjectCodePath(), project.getMappingFilePath(), isDebug));
        }

        // 解析Manifest
        ManifestDescription _manifestDescription = null;
        try {
            _manifestDescription = Parser.parseManifest(project);
        } catch (ParserConfigurationException e) {
            System.out.println("解析Manifest.xml发生异常 " + e.getMessage());
            // 终止程序
            throw new RuntimeException(e);
        } catch (SAXException e) {
            System.out.println("解析Manifest.xml发生异常 " + e.getMessage());
            // 终止程序
            throw new RuntimeException(e);
        }
        final ManifestDescription manifestDescription = _manifestDescription;
        if (manifestDescription == null) {
            // 终止程序
            throw new NullPointerException("_manifestDescription is null");
        }

        // 解析四大组件、Application中import哪些类
        // Application
        JavaFileDescription javaFileDescriptionApplication = project.changeJavaPackage2JavaFileDescription(manifestDescription.getApplication());
        Parser.addJavaFileDescription(javaFileDescriptionApplication, mJavaPackageSet);
        // 把自己加进去
        Parser.addComponentJavaPackage(manifestDescription.getApplication(), mJavaPackageSet);
        // 主工程的组件
        // Service Broadcast ContentProvider
        for (int i = 0; i < manifestDescription.getComponents().length; i++) {
            JavaFileDescription javaDescription = project.changeJavaPackage2JavaFileDescription(manifestDescription.getComponents()[i], false);
            Parser.addJavaFileDescription(javaDescription, mJavaPackageSet);
            // 把自己加进去
            Parser.addComponentJavaPackage(manifestDescription.getComponents()[i], mJavaPackageSet);
        }
        // Activity 我们需要的activity
//        for (int i = 0; i < manifestDescription.getActivities().length; i++) {
//        }
        // 依赖工程的组件
        for (int i = 0; i < manifestDescription.getModuleComponents().length; i++) {
            JavaFileDescription javaDescription = project
                    .changeJavaPackage2JavaFileDescription(
                            manifestDescription.getModuleComponents()[i], true);
            Parser.addJavaFileDescription(javaDescription, mJavaPackageSet);
            // 把自己加进去
            Parser.addComponentJavaPackage(manifestDescription.getModuleComponents()[i], mJavaPackageSet);
        }

        // 注解的
        // 查看是否获取完了注解了类
        boolean isGetAnnotationFileDescriptionSuccess = true;
        Set<AnnotationFileDescription> annotationFileDescriptionSet = null;
        try {
            annotationFileDescriptionSet = futureGetAnnotationJavaPackage.get();
        } catch (InterruptedException e1) {
            System.out.println("得到注解类错误 ");
            e1.printStackTrace();
            isGetAnnotationFileDescriptionSuccess = false;
        } catch (ExecutionException e1) {
            System.out.println("得到注解类错误 ");
            e1.printStackTrace();
            isGetAnnotationFileDescriptionSuccess = false;
        }
        if (isDebug) {
            System.out.println("异步得到注解类错误是否成功--->"
                    + isGetAnnotationFileDescriptionSuccess);
        }
        if (!isGetAnnotationFileDescriptionSuccess) {
            throw new RuntimeException("");
        }

        // 对注解的进行处理
        for (AnnotationFileDescription annotationFileDescription : annotationFileDescriptionSet) {
            if (annotationFileDescription == null) {
                continue;
            }
            JavaFileDescription javaDescription = project.changeJavaPackage2JavaFileDescription(annotationFileDescription.getJavaPackage());
            Parser.addJavaFileDescription(javaDescription, mJavaPackageSet);
            Parser.addComponentJavaPackage(annotationFileDescription.getJavaPackage(), mJavaPackageSet);
        }

        // 查看是否解压完了
        boolean isUnzipSuccess = true;
        try {
            isUnzipSuccess = futureUnzip.get();
        } catch (InterruptedException e) {
            FileUtils.deleteDir(jar2classDir);
            System.out.println("解压Jar发生异常 ");
            e.printStackTrace();
            isUnzipSuccess = false;
        } catch (ExecutionException e) {
            FileUtils.deleteDir(jar2classDir);
            System.out.println("解压Jar发生异常 ");
            e.printStackTrace();
            isUnzipSuccess = false;
        }
        // 如果解压失败就退出程序
        if (isDebug) {
            System.out.println("异步压缩Jar是否成功--->" + isUnzipSuccess);
        }
        if (!isUnzipSuccess) {
            throw new RuntimeException("");
        }

        // 查看是否将系统生成的写到multidex.keep文件里面去了
        boolean isGenerateFileSuccess = true;
        try {
            isGenerateFileSuccess = futureGenerateFile.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("复制系统文件错误 ");
            e.printStackTrace();
            isGenerateFileSuccess = false;
        } catch (ExecutionException e) {
            System.out.println("复制系统文件错误 ");
            e.printStackTrace();
            isGenerateFileSuccess = false;
        }
        // 如果复制系统文件出错就退出程序
        if (isDebug) {
            System.out.println("异步复制系统文件是否成功--->" + isGenerateFileSuccess);
        }
        if (!isGenerateFileSuccess) {
            throw new RuntimeException("");
        }

        // Debug
        if (DexProject.TYPE_BUILD_DEBUG.equals(buildType)) {
            // 在解压出来的jar文件夹中找这个类以及内部类并添加到project中
            for (Iterator<String> iterator = mJavaPackageSet.iterator(); iterator.hasNext(); ) {
                String packageName = iterator.next();
                project.findClassAndAdd2Set(packageName, jar2classDir);
            }
            // 第三方的 TODO:第三方的最好找依赖的会找class文件
            for (int i = 0; i < manifestDescription.getThirdComponents().length; i++) {
                project.findClassAndAdd2Set(
                        manifestDescription.getThirdComponents()[i],
                        jar2classDir);
            }
        } else {// Release
            // 如果是release的话，先从mapping文件中得到
            FileUtils.readMappingFile(project.getMappingFilePath(), new FileUtils.OnMappingReadingListener() {

                @Override
                public void onLine(String totalLine, String originalPackage, String proguardPackage) {
                    boolean hasAdd = false;
                    for (Iterator<String> iterator = mJavaPackageSet.iterator(); iterator.hasNext(); ) {
                        String javaPackageName = iterator.next();
                        if (totalLine.startsWith(javaPackageName)) {
                            project.add2Set(proguardPackage.replace(".", File.separator) + ".class");
                            hasAdd = true;
                        }
                    }

                    if (!hasAdd) {
                        // 找找是不是第三方的
                        for (int i = 0; i < manifestDescription.getThirdComponents().length; i++) {
                            if (totalLine.startsWith(manifestDescription.getThirdComponents()[i])) {
                                project.add2Set(proguardPackage.replace(".", File.separator) + ".class");
                            }
                        }
                    }
                }
            });
            // 再去找需要写的类，如果该类混淆了的话那么那个文件夹肯定是不存在的，就跳过
            for (Iterator<String> iterator = mJavaPackageSet.iterator(); iterator.hasNext(); ) {
                String packageName = iterator.next();
                project.findClassAndAdd2Set(packageName, jar2classDir);
            }
            // 第三方的
            for (int i = 0; i < manifestDescription.getThirdComponents().length; i++) {
                project.findClassAndAdd2Set(
                        manifestDescription.getThirdComponents()[i],
                        jar2classDir);
            }
        }

        Future<Boolean> future = pool.submit(new DeleteDirCallback(
                jar2classDir));
        FileUtils.checkAndWriteFileAppend(project.getMultidexKeepPath(), project.getClasses(), project.getMultidexKeepPath());
        boolean deleteDirSuccess = true;
        try {
            deleteDirSuccess = future.get();
        } catch (InterruptedException e) {
            deleteDirSuccess = false;
            e.printStackTrace();
        } catch (ExecutionException e) {
            deleteDirSuccess = false;
            e.printStackTrace();
        }
        if (isDebug) {
            System.out.println("异步删除解压Jar的文件夹是否成功--->" + deleteDirSuccess);
        }
        if (!deleteDirSuccess) {
            throw new RuntimeException("");
        }
        if (isDebug) {
            System.out.println("成功写入multidex.keep文件");
        }
        pool.shutdown();
    }
}

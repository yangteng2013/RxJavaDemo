package com.noe.dex;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yuyidong on 16/4/26.
 */
public class DexProject {
    private Set<String> mClassSet;

    public static final String TYPE_BUILD_DEBUG = "debug";
    public static final String TYPE_BUILD_RELEASE = "release";

    public static final String PROJECT_MAIN_NAME = "GJAndroidClient15";
    public static final String PACKAGE_MAIN = "com.ganji.android";
    public static final String PROJECT_QUNZU_NAME = "GJQunZu";
    public static final String PACKAGE_QUNZU = "com.ganji.im";
    public static final String PROJECT_COMP_NAME = "GanjiCompLib";
    public static final String PACKAGE_COMP = "com.ganji.android.comp";
    public static final String PROJECT_CORE_NAME = "GanjiCoreLib";
    public static final String PACKAGE_CORE = "com.ganji.android.core";
    public static final String PROJECT_PATCH_NAME = "Patch";
    public static final String PACKAGE_PATCH = "com.ganji.android.patch";

    public static final String MANIFEST = "AndroidManifest.xml";

    public static final String SRC_CODE_ECLIPSE = "src/";
    public static final String SRC_CODE_ANDROID_STUDIO = "src/main/java/";

    private String mRootPath;
    private String mVariant;
    private String mBuildType;

    private ModuleBean[] mModuleBeans;

    public DexProject(String rootPath, String variant, String buildType) {
        if (!rootPath.endsWith(File.separator)) {
            mRootPath = rootPath + File.separator;
        } else {
            mRootPath = rootPath;
        }
        mVariant = variant;
        mBuildType = buildType;
        mModuleBeans = new ModuleBean[4];
        mModuleBeans[0] = new ModuleBean(PROJECT_QUNZU_NAME, PACKAGE_QUNZU, SRC_CODE_ANDROID_STUDIO);
        mModuleBeans[1] = new ModuleBean(PROJECT_COMP_NAME, PACKAGE_COMP, SRC_CODE_ANDROID_STUDIO);
        mModuleBeans[2] = new ModuleBean(PROJECT_CORE_NAME, PACKAGE_CORE, SRC_CODE_ANDROID_STUDIO);
        mModuleBeans[3] = new ModuleBean(PROJECT_PATCH_NAME, PACKAGE_PATCH, SRC_CODE_ANDROID_STUDIO);
        mClassSet = new HashSet<>();
    }

    public String getDebugJarFile() {
//        if (Utils.isEmpty(mVariant)) {
//            return mRootPath + PROJECT_MAIN_NAME + "/build/intermediates/multi-dex/"
//                    + mBuildType + "/allclasses.jar";
//        } else {
//            return mRootPath + PROJECT_MAIN_NAME + "/build/intermediates/multi-dex/"
//                    + mVariant + "/" + mBuildType + "/allclasses.jar";
//        }
        if (Utils.isEmpty(mVariant)) {
            return mRootPath + PROJECT_MAIN_NAME + "/build/intermediates/transforms/jarMerging/"
                    + mBuildType + "/jars/1/1f/combined.jar";
        } else {
            return mRootPath + PROJECT_MAIN_NAME + "/build/intermediates/transforms/jarMerging/"
                    + mVariant + "/" + mBuildType + "/jars/1/1f/combined.jar";
        }
    }

    public String getReleaseJarFile() {
//        if (Utils.isEmpty(mVariant)) {
//            return mRootPath + PROJECT_MAIN_NAME + "/build/intermediates/classes-proguard/"
//                    + mBuildType + "/classes.jar";
//        } else {
//            return mRootPath + PROJECT_MAIN_NAME + "/build/intermediates/classes-proguard/"
//                    + mVariant + "/" + mBuildType + "/classes.jar";
//        }
        if (Utils.isEmpty(mVariant)) {
            return mRootPath + PROJECT_MAIN_NAME + "/build/intermediates/transforms/proguard/"
                    + mBuildType + "/jars/3/1f/main.jar";
        } else {
            return mRootPath + PROJECT_MAIN_NAME + "/build/intermediates/transforms/proguard/"
                    + mVariant + "/" + mVariant + "/jars/3/1f/main.jar";
        }
    }

    public String getMappingFilePath() {

        if (Utils.isEmpty(mVariant)) {
            return mRootPath + PROJECT_MAIN_NAME + "/build/outputs/mapping/"
                    + mBuildType + "/mapping.txt";
        } else {
            return mRootPath + PROJECT_MAIN_NAME + "/build/outputs/mapping/"
                    + mVariant + "/" + mBuildType + "/mapping.txt";
        }
    }

    public String getMainDexListPath() {
        if (Utils.isEmpty(mVariant)) {
            return mRootPath + PROJECT_MAIN_NAME + "/build/intermediates/multi-dex/"
                    + mBuildType + "/maindexlist.txt";
        } else {
            return mRootPath + PROJECT_MAIN_NAME + "/build/intermediates/multi-dex/"
                    + mVariant + "/" + mBuildType + "/maindexlist.txt";
        }
    }

    public String getSupportKeepPath() {
        String supportKeepPath = mRootPath + PROJECT_MAIN_NAME + "/support.keep";
        if (!new File(supportKeepPath).exists()) {
            try {
                new File(supportKeepPath).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return supportKeepPath;
    }

    public String getMultidexKeepPath() {
        String multidexKeepPath = mRootPath + PROJECT_MAIN_NAME + "/build/multidex.keep";
        File file = new File(multidexKeepPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return multidexKeepPath;
    }

    public String[] getAllProjectCodePath() {
        String[] paths = new String[mModuleBeans.length + 1];
        for (int i = 0; i < mModuleBeans.length; i++) {
            paths[i] = mRootPath + mModuleBeans[i].getModuleName() +
                    File.separator + mModuleBeans[i].getModuleSrcCode();
        }
        paths[paths.length - 1] = getMainProjectCodePath();
        return paths;
    }

    public String getMainProjectCodePath() {
        return mRootPath + PROJECT_MAIN_NAME + File.separator + SRC_CODE_ANDROID_STUDIO;
    }

    public String getMainPackageName() {
        return PACKAGE_MAIN;
    }

    public ModuleBean[] getModuleBeans() {
        return mModuleBeans;
    }

    public String getManifestPath() {
        return mRootPath + PROJECT_MAIN_NAME + "/src/main/" + MANIFEST;
    }

    public String getRootPath() {
        return mRootPath;
    }


    /**
     * 将包名+类名转换成java文件描述类
     *
     * @param javaPackage 类似这种com.ganji.android.ClientApplication
     * @return
     */
    public JavaFileDescription changeJavaPackage2JavaFileDescription(String javaPackage) {
        boolean isModule = false;
        ModuleBean moduleBean = null;
        for (int i = 0; i < mModuleBeans.length; i++) {
            if (javaPackage.startsWith(mModuleBeans[i].getModulePackageName())) {
                isModule = true;
                moduleBean = mModuleBeans[i];
                break;
            }
        }
        return new JavaFileDescription(this, javaPackage.replace(".", File.separator), isModule, moduleBean);
    }

    /**
     * 将包名+类名转换成java文件描述类
     *
     * @param javaPackageName 类似这种com.ganji.android.ClientApplication
     * @param isModule
     * @return
     */
    public JavaFileDescription changeJavaPackage2JavaFileDescription(
            String javaPackageName, boolean isModule) {
        boolean isReallyModule = false;
        ModuleBean moduleBean = null;
        if (isModule) {
            for (int i = 0; i < mModuleBeans.length; i++) {
                if (javaPackageName.startsWith(mModuleBeans[i]
                        .getModulePackageName())) {
                    isReallyModule = true;
                    moduleBean = mModuleBeans[i];
                    break;
                }
            }
            if (isReallyModule != isModule) {
                throw new IllegalArgumentException("这个类不是module的");
            }
        }
        return new JavaFileDescription(this, javaPackageName.replace(".", File.separator), isModule, moduleBean);
    }

    /**
     * 通过 packageName 找到类已经内部类
     *
     * @param javaPackageName 类似com.ganji.android.comp.usercenter.UserCenter
     * @param jar2classDir    类似/Users/yuyidong/AndroidStudioProjects/V7.4.0-GanjiLife-
     *                        dexloader
     *                        /GJAndroidClient15/build/intermediates/multi-dex/dev/debug
     *                        /allclasses/
     */
    public void findClassAndAdd2Set(String javaPackageName, String jar2classDir) {
        ClassFileDescription classFileDescription = changeJavaPackage2ClassFileDescription(
                javaPackageName, jar2classDir);
        File dir = new File(classFileDescription.getAbsolutePath());
        // 混淆之后可能不存在
        if (!dir.exists()) {
            return;
        }
        if (dir.isDirectory()) {
            add2Set(dir, classFileDescription.getClassName(), jar2classDir);
        } else {// 可能是监听器啊之类的
            String[] names = FileUtils.splitPath(javaPackageName.replace(".", File.separator));
            // 搜上一级目录
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < names.length - 1; i++) {
                sb.append(names[i]);
                if (i != names.length - 2) {
                    sb.append(".");
                }
            }
            String newPackageName = sb.toString();
            findClassAndAdd2Set(newPackageName, jar2classDir);
        }
    }

    public ClassFileDescription changeJavaPackage2ClassFileDescription(
            String javaPackageName, String outJarDir) {
        return new ClassFileDescription(javaPackageName.replace(".",
                File.separator), outJarDir);
    }

    /**
     * 给一个目录dir和类的名字className，找出这个目录下className的类以及内部类
     *
     * @param dir
     * @param className
     * @param jar2classDir
     */
    private void add2Set(File dir, String className, String jar2classDir) {
        String[] fileNames = dir.list();
        for (String fileName : fileNames) {
            // 没有给className的情况，也是就读取下面所以文件
            if (className == null) {
                String totalPath = dir.getAbsolutePath() + File.separator
                        + fileName;
                String realRelativePath = totalPath.substring(
                        jar2classDir.length() - 1, totalPath.length());
                if (realRelativePath.startsWith(File.separator)) {
                    realRelativePath = realRelativePath.substring(1, realRelativePath.length());
                }
                if (realRelativePath.endsWith(".class")) {// 有些时候打包出来的jar里面还是有java格式的文件
                    String androidStylePath = realRelativePath.replace(File.separator, "/");
                    mClassSet.add(androidStylePath);
                } else {
                    // 可能是一个文件夹
                    File dirFile = new File(totalPath);
                    if (dirFile.isDirectory()) {
                        // 进行个递归
                        add2Set(dirFile, null, jar2classDir);
                    } else {
                        // 不是文件，那个这里就走不通了，就不管了吧
                        System.out.println("检查一下" + dirFile + "是咋回事");
                        return;
                    }
                }
            } else if (fileName.contains(className)) {// 这里就包括了内部类
                String totalPath = dir.getAbsolutePath() + File.separator
                        + fileName;
                String realRelativePath = totalPath.substring(
                        jar2classDir.length() - 1, totalPath.length());
                if (realRelativePath.startsWith(File.separator)) {
                    realRelativePath = realRelativePath.substring(1, realRelativePath.length());
                }
                if (!realRelativePath.endsWith(".class")) {
                    // 可能是一个文件夹
                    File dirFile = new File(totalPath);
                    if (dirFile.isDirectory()) {
                        // 进行个递归
                        add2Set(dirFile, null, jar2classDir);
                    } else {
                        // 不是文件，那个这里就走不通了，就不管了吧
                        System.out.println("检查一下" + dirFile + "是咋回事");
                        return;
                    }
                } else {
                    String androidStylePath = realRelativePath.replace(File.separator, "/");
                    mClassSet.add(androidStylePath);
                }
            }
        }
    }

    public void add2Set(String proguardPackageName) {
//		progoardPackageName.replace(".", File.separator);
        String androidStylePath = proguardPackageName.replace(File.separator, "/");
        mClassSet.add(androidStylePath);
    }

    public Set<String> getClasses() {
        return mClassSet;
    }

    public String getAndroidJar() {
        return mRootPath + "buildSrc/res/android-23.jar";
    }

    public String getHttpClientJar() {
        return mRootPath + "buildSrc/res/org.apache.http.legacy.jar";
    }
}

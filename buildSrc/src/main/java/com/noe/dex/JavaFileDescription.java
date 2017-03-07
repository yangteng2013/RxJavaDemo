package com.noe.dex;

import java.io.File;

/**
 * Created by yuyidong on 16/4/27.
 */
public class JavaFileDescription {
    private DexProject mDexProject;
    private String mAbsolutePath;// 该类在项目中的地址
    private String mJavaName;// 该类的类名
    private boolean mIsModule = false;
    private ModuleBean mModuleBean;

    public JavaFileDescription(DexProject dexProject, String packageName, boolean isModule,
                               ModuleBean moduleBean) {
        mDexProject = dexProject;
        mIsModule = isModule;
        if (mIsModule) {
            mModuleBean = moduleBean;
        }
        String[] p = FileUtils.splitPath(packageName);
        if (p.length == 0) {
            throw new IllegalArgumentException("没对头!");
        }
        StringBuilder sb = new StringBuilder(dexProject.getRootPath());
        if (isModule && mModuleBean != null) {
            sb.append(mModuleBean.getModuleName());
            sb.append(File.separator);
            sb.append(mModuleBean.getModuleSrcCode());
        } else {
            sb.append(DexProject.PROJECT_MAIN_NAME);
            sb.append(File.separator);
            sb.append(DexProject.SRC_CODE_ANDROID_STUDIO);
        }
        for (int i = 0; i < p.length - 1; i++) {
            sb.append(p[i]);
            if (i != p.length - 2) {
                sb.append(File.separator);
            }
        }
        mAbsolutePath = sb.toString();
        mJavaName = p[p.length - 1];
    }

    public String getAbsolutePath() {
        return mAbsolutePath;
    }

    public String getJavaName() {
        return mJavaName;
    }

    public boolean isModule() {
        return mIsModule;
    }

    public ModuleBean getModuleBean() {
        return mModuleBean;
    }

    public String getProjectPath() {
        return mDexProject.getRootPath();
    }

    public String getMainProjectName() {
        return DexProject.PROJECT_MAIN_NAME;
    }
}

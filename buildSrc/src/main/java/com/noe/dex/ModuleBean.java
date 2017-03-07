package com.noe.dex;

/**
 * Created by yuyidong on 16/4/27.
 */
public class ModuleBean {
    private final String mModuleName;
    private final String mModulePackageName;
    private final String mModuleSrcCode;

    public ModuleBean(String moduleName, String modulePackageName,
                      String moduleSrcCode) {
        this.mModuleName = moduleName;
        this.mModulePackageName = modulePackageName;
        this.mModuleSrcCode = moduleSrcCode;
    }

    public String getModuleName() {
        return mModuleName;
    }

    public String getModulePackageName() {
        return mModulePackageName;
    }

    public String getModuleSrcCode() {
        return mModuleSrcCode;
    }
}

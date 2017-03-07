package com.noe.dex;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yuyidong on 16/4/27.
 */
public class ManifestDescription {
    private static final String DOT = ".";

    private DexProject mProject;

    private String mApplication;
    // Android 四大组件
    private List<String> mComponentList;// Service Broadcast ContentProvider
    private List<String> mActivityList;
    // 第三方中的四大组件
    private List<String> mThirdComponentList;
    // 依赖工程中的四大组件
    private List<String> mModuleComponentList;

    public ManifestDescription(DexProject dexProject) {
        mProject = dexProject;
        mComponentList = new LinkedList<>();
        mActivityList = new LinkedList<>();
        mModuleComponentList = new LinkedList<>();
        mThirdComponentList = new LinkedList<>();
    }

    public void setApplication(String application) {
        if (application == null) {
            throw new IllegalArgumentException("解析到Application没有名字或者解析错误");
        }
        if (mApplication != null) {
            throw new IllegalArgumentException("已经有一个Application了，但是又解析到一个");
        }
        if (application.startsWith(DOT)) {
            mApplication = mProject.getMainPackageName() + application;
        } else {
            mApplication = application;
        }
    }

    public void addServiceOrBroadCcastOrProvider(String component) {
        if (component == null || component.contains("$")) {// 内部类也不要加入了
            return;
        }
        // 以.开头的，将包名补全
        if (component.startsWith(DOT)) {
            component = mProject.getMainPackageName() + component;
        }
        boolean isAdd = false;
        // 判断是不是module
        for (int i = 0; i < mProject.getModuleBeans().length; i++) {
            if (component.startsWith(mProject.getModuleBeans()[i]
                    .getModulePackageName())) {
                mModuleComponentList.add(component);
                isAdd = true;
                break;
            }
        }
        // 是module的
        if (isAdd) {
            return;
        }
        // 判断是不是主工程的
        if (component.startsWith(mProject.getMainPackageName())) {
            mComponentList.add(component);
        } else {
            mThirdComponentList.add(component);
        }
    }

    public void addActivity(String component) {
        if (component == null || component.contains("$")) {// 内部类也不要加入了
            return;
        }
        // 以.开头的，将包名补全
        if (component.startsWith(DOT)) {
            component = mProject.getMainPackageName() + component;
        }
        boolean isAdd = false;
        // 判断是不是module
        for (int i = 0; i < mProject.getModuleBeans().length; i++) {
            if (component.startsWith(mProject.getModuleBeans()[i]
                    .getModulePackageName())) {
                mModuleComponentList.add(component);
                isAdd = true;
                break;
            }
        }
        // 是module的
        if (isAdd) {
            return;
        }
        // 判断是不是主工程的
        if (component.startsWith(mProject.getMainPackageName())) {
            mActivityList.add(component);
        } else {
            mThirdComponentList.add(component);
        }
    }

    public String getApplication() {
        return mApplication;
    }

    public String[] getComponents() {
        return mComponentList.toArray(new String[0]);
    }

    public String[] getActivities() {
        return mActivityList.toArray(new String[0]);
    }

    public String[] getModuleComponents() {
        return mModuleComponentList.toArray(new String[0]);
    }

    public String[] getThirdComponents() {
        return mThirdComponentList.toArray(new String[0]);
    }

}

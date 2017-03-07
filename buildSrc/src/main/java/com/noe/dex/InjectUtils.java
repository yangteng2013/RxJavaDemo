package com.noe.dex;

import com.android.build.gradle.internal.transforms.DexTransform;
import com.android.builder.core.AndroidBuilder;
import com.android.builder.core.LibraryRequest;
import com.android.ide.common.process.JavaProcessExecutor;

import org.gradle.api.Project;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by yuyidong on 2016/11/10.
 */
public class InjectUtils {

    public static void inject(Project project, DexTransform transform, int maxIdxNumber) {
        try {
            Field field = getField(DexTransform.class, "androidBuilder");
            AndroidBuilder androidBuilder = (AndroidBuilder) getFieldObject(transform, field);
            InjectAndroidBuilder injectAndroidBuilder = new InjectAndroidBuilder(
                    (String) getFieldObject(androidBuilder, androidBuilder.getClass(), "mProjectId"),
                    (String) getFieldObject(androidBuilder, androidBuilder.getClass(), "mCreatedBy"),
                    androidBuilder.getProcessExecutor(),
                    (JavaProcessExecutor) getFieldObject(androidBuilder, androidBuilder.getClass(), "mJavaProcessExecutor"),
                    androidBuilder.getErrorReporter(),
                    androidBuilder.getLogger(),
                    (Boolean) getFieldObject(androidBuilder, androidBuilder.getClass(), "mVerboseExec")
            );
            injectAndroidBuilder.setSdkInfo(androidBuilder.getSdkInfo());
            injectAndroidBuilder.setTargetInfo(androidBuilder.getTargetInfo());
            injectAndroidBuilder.setLibraryRequests((List<LibraryRequest>) getFieldObject(androidBuilder, androidBuilder.getClass(), "mLibraryRequests"));
            injectAndroidBuilder.setParams(project, maxIdxNumber, androidBuilder);
            field.set(transform, injectAndroidBuilder);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("fuck you");
        }
    }

    public static Object getFieldObject(Object instance, Class clazz, String field) {
        Object o = null;
        try {
            Field f = getField(clazz, field);
            if (f != null) {
                o = f.get(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("fuck you");
        }
        return o;
    }

    public static Object getFieldObject(Object instance, Field field) {
        Object o = null;
        try {
            o = field.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("fuck you");
        }
        return o;
    }

    public static Field getField(Class clazz, String field) {
        Field f = null;
        try {
            f = clazz.getDeclaredField(field);
            f.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("fuck you");
        }
        return f;
    }

}

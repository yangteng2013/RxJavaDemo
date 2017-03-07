package com.noe.dex

import com.google.common.collect.ImmutableMap
import org.gradle.api.Project

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Created by huiyh on 2016/8/30.
 */
class BuildScripts {

    public static void indexMainDexClassList(Project project, int maxIdxNumber) {
        GradleCompat.between150And220Alpha4(project, maxIdxNumber)
    }

    public static void patchKeepSpecs(ClassLoader classLoader) {
        def taskClass = "com.android.build.gradle.internal.tasks.multidex.CreateManifestKeepList";

        Class clazz = classLoader.loadClass(taskClass)
        Field field = clazz.getDeclaredField("KEEP_SPECS")
        field.setAccessible(true)

        String DEFAULT_KEEP_SPEC = "{ <init>(); }"
        Map<String, String> KEEP_SPECS = ImmutableMap.<String, String> builder()
                .put("application", "{\n"
                + "    <init>();\n"
                + "    void attachBaseContext(android.content.Context);\n"
                + "}")
                .put("activity", DEFAULT_KEEP_SPEC)
                .put("service", DEFAULT_KEEP_SPEC)
                .put("receiver", DEFAULT_KEEP_SPEC)
                .put("provider", DEFAULT_KEEP_SPEC)
                .put("instrumentation", DEFAULT_KEEP_SPEC).build();
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, KEEP_SPECS);
    }
}
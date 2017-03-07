package com.noe.dex;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.Transform;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.ApplicationVariant;
import com.android.build.gradle.internal.pipeline.IntermediateFolderUtils;
import com.android.build.gradle.internal.pipeline.TransformTask;
import com.android.build.gradle.internal.transforms.DexTransform;

import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;

/**
 * Created by yuyidong on 2016/11/9.
 */
public class GradleCompat {

    public static void before150(Task task) {
        long millis1 = System.currentTimeMillis();
        Project project = task.getProject();
        String channelAndBuildType = task.getName().substring("create".length(), task.getName().length() - "MainDexClassList".length());
        String buildType = channelAndBuildType.endsWith("Debug") ? "debug" : "release";
        String channel = "";
        if (channelAndBuildType.length() > buildType.length()) {
            channel = channelAndBuildType.substring(0, channelAndBuildType.length() - buildType.length());
            String classDirPath = String.format("%s/build/intermediates/classes/%s/%s", project.getProjectDir(), channel, buildType.toLowerCase());
            File classDirFile = new File(classDirPath);
            if (!classDirFile.exists()) {
                channel = channel.substring(0, 1).toLowerCase() + channel.substring(1, channel.length());
            }
        }
        SplitDex.process(project.getProjectDir().getParent(), channel, buildType, true);
        long millis2 = System.currentTimeMillis();
        System.out.println("IndexMainDexClassList times: " + ((millis2 - millis1) / 1000f) + "s");
    }

    public static void between150And220Alpha4(Project project, final int maxIdxNumber) {
        AppExtension appExtension = project.getExtensions().findByType(AppExtension.class);
        for (ApplicationVariant variant : appExtension.getApplicationVariants()) {
            TransformTask dexTask = null;
            TransformTask proGuardTask = null;
            TransformTask jarMergingTask = null;
            TransformTask multidexListTask = null;

            String buildType = variant.getBuildType().getName();
            String flavorName = variant.getMergedFlavor().getName();
            boolean minifyEnabled = variant.getBuildType().isMinifyEnabled();
            for (Task task : project.getTasks()) {
                if ((task instanceof TransformTask) && task.getName().toLowerCase().endsWith(buildType)) {
                    TransformTask theTask = (TransformTask) task;
                    Transform transform = theTask.getTransform();
                    String transformName = transform.getName();
                    if (minifyEnabled && "proguard".equals(transformName)) {
                        proGuardTask = theTask;
                    } else if ("jarMerging".equalsIgnoreCase(transformName)) {
                        jarMergingTask = theTask;
                    } else if ("dex".equalsIgnoreCase(transformName)) {
                        dexTask = theTask;
                    } else if ("multidexlist".equalsIgnoreCase(transformName)) {
                        multidexListTask = theTask;
                    }
                }
            }
            if (dexTask != null) {
                final TransformTask dexTaskFinal = dexTask;
                final TransformTask proGuardTaskFinal = proGuardTask;
                final TransformTask jarMergingTaskFinal = jarMergingTask;
                final TransformTask multidexListTaskFinal = multidexListTask;
                dexTaskFinal.doFirst(task -> {
                    File mergedJar = null;
                    File proguardJar = null;
                    DexTransform dexTransform = (DexTransform) dexTaskFinal.getTransform();
                    if (minifyEnabled) {
                        if (proGuardTaskFinal != null) {
                            Transform transform = proGuardTaskFinal.getTransform();
                            proguardJar = IntermediateFolderUtils.getContentLocation(proGuardTaskFinal.getStreamOutputFolder(),
                                    "main", transform.getOutputTypes(), transform.getScopes(), Format.JAR);
                            if (proguardJar.exists()) {
                                SplitDex.process(project.getProjectDir().getParent(), "", buildType, true);
                                InjectUtils.inject(project, dexTransform, maxIdxNumber);
                            }
                        }
                    } else {
                        if (jarMergingTaskFinal != null) {
                            Transform transform = jarMergingTaskFinal.getTransform();
                            mergedJar = IntermediateFolderUtils.getContentLocation(jarMergingTaskFinal.getStreamOutputFolder(),
                                    "combined", transform.getOutputTypes(), transform.getScopes(), Format.JAR);
                            if (mergedJar.exists()) {
                                SplitDex.process(project.getProjectDir().getParent(), "", buildType, true);
                                InjectUtils.inject(project, dexTransform, maxIdxNumber);
                            }
                        }
                    }
                });
            }
        }
    }


}

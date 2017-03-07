package com.noe.dex;


import com.android.builder.core.AndroidBuilder;
import com.android.builder.core.DexOptions;
import com.android.builder.core.ErrorReporter;
import com.android.ide.common.process.JavaProcessExecutor;
import com.android.ide.common.process.ProcessException;
import com.android.ide.common.process.ProcessExecutor;
import com.android.ide.common.process.ProcessOutputHandler;
import com.android.utils.ILogger;

import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by yuyidong on 2016/11/9.
 */
public class InjectAndroidBuilder extends AndroidBuilder {
    private static final String PARAMS_MULTI_DEX = "--multi-dex";
    private static final String PARAMS_MAX_IDX_NUMBER = "--set-max-idx-number";
    private static final String PARAMS_MAIN_DEX_LIST = "--main-dex-list";
    private static final String PARAMS_MIN_MAIN_DEX = "--minimal-main-dex";

    private Project mProject;
    private int mMaxIdxNumber = 56000;
    private AndroidBuilder mAndroidBuilder;

    public void setParams(Project project, int maxIdxNumber, AndroidBuilder androidBuilder) {
        mProject = project;
        mMaxIdxNumber = maxIdxNumber;
        mAndroidBuilder = androidBuilder;
    }

    public InjectAndroidBuilder(String projectId, String createdBy, ProcessExecutor processExecutor, JavaProcessExecutor javaProcessExecutor, ErrorReporter errorReporter, ILogger logger, boolean verboseExec) {
        super(projectId, createdBy, processExecutor, javaProcessExecutor, errorReporter, logger, verboseExec);
    }

    @Override
    public void convertByteCode(Collection<File> inputs, File outDexFolder, boolean multidex, File mainDexList, DexOptions dexOptions, boolean optimize, ProcessOutputHandler processOutputHandler) throws IOException, InterruptedException, ProcessException {
        DexOptions dexOptionsProxy = dexOptions;
        List<String> additionalParameters = dexOptions.getAdditionalParameters();
        if (additionalParameters == null) {
            additionalParameters = new ArrayList<>();
        }
        additionalParameters.clear();
        additionalParameters.add(PARAMS_MULTI_DEX);
        additionalParameters.add(PARAMS_MAX_IDX_NUMBER + "=" + mMaxIdxNumber);
        additionalParameters.add(PARAMS_MAIN_DEX_LIST + "=" + mProject.getProjectDir() + "/build/multidex.keep".toString());
        mAndroidBuilder.convertByteCode(inputs, outDexFolder, multidex, mainDexList, dexOptionsProxy, optimize, processOutputHandler);
    }

    @Override
    public List<File> getBootClasspath(boolean includeOptionalLibraries) {
        return mAndroidBuilder.getBootClasspath(includeOptionalLibraries);
    }

    @Override
    public List<String> getBootClasspathAsStrings(boolean includeOptionalLibraries) {
        return mAndroidBuilder.getBootClasspathAsStrings(includeOptionalLibraries);
    }

    private void mergeParams(List<String> additionalParameters, Collection<String> addParams) {
        List<String> mergeParam = new ArrayList<>();
        for (String param : addParams) {
            if (!additionalParameters.contains(param)) {
                mergeParam.add(param);
            }
        }

        if (mergeParam.size() > 0) {
            additionalParameters.addAll(mergeParam);
        }
    }
}

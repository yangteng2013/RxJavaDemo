package com.noe.dex;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by yuyidong on 16/4/27.
 */
public class Parser {
    private static final String ANDROID_APPLICATION = "application";
    private static final String ANDROID_ACTIVITY = "activity";
    private static final String ANDROID_BROADCAST = "receiver";
    private static final String ANDROID_SERVICE = "service";
    private static final String ANDROID_PROVIDER = "provider";

    private static final String TAG_NAME = "android:name";

    public static ManifestDescription parseManifest(DexProject dexProject) throws ParserConfigurationException, SAXException {
        // 准备XML
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        // 准备Manifest封装类
        ManifestDescription manifestDescription = new ManifestDescription(dexProject);
        InputStream in = null;
        try {
            in = new FileInputStream(new File(dexProject.getManifestPath()));
            // 开始解析
            parser.parse(in, new XMLHandler(manifestDescription));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(in);
        }
        // 返回结果
        return manifestDescription;
    }

    private static class XMLHandler extends DefaultHandler {
        private ManifestDescription mManifestDescription;

        protected XMLHandler(ManifestDescription manifestDescription) {
            mManifestDescription = manifestDescription;
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            if (attributes == null) {
                return;
            }
            switch (qName) {
                case ANDROID_ACTIVITY:
                    for (int i = 0; i < attributes.getLength(); i++) {
                        if (TAG_NAME.equals(attributes.getQName(i))) {
                            mManifestDescription.addActivity(attributes.getValue(i));
                        }
                    }
                    break;
                case ANDROID_SERVICE:
                case ANDROID_BROADCAST:
                case ANDROID_PROVIDER:
                    for (int i = 0; i < attributes.getLength(); i++) {
                        if (TAG_NAME.equals(attributes.getQName(i))) {
                            mManifestDescription.addServiceOrBroadCcastOrProvider(attributes.getValue(i));
                        }
                    }
                    break;
                case ANDROID_APPLICATION:
                    for (int i = 0; i < attributes.getLength(); i++) {
                        if (TAG_NAME.equals(attributes.getQName(i))) {
                            mManifestDescription.setApplication(attributes.getValue(i));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static final String IGNORE_JAVA = "java";
    private static final String IGNORE_ANDROID = "android";
    private static final String IGNORE_JSON = "org.json";
    private static final String IGNORE_APACHE_CLIENT = "org.apache.http.client";
    private static final String IGNORE_APACHE_IMPL = "org.apache.http.impl";
    private static final String NOT_IGNORE_ANDROID_SUPPORT = "android.support";

    private static final String SUFFIX_JAVA = ".java";

    /**
     * 添加java文件描述
     *
     * @param javaDescription
     * @param javaPackageSet
     */
    public static void addJavaFileDescription(JavaFileDescription javaDescription, Set<String> javaPackageSet) {
        String javaFileAbsolutePath = javaDescription.getAbsolutePath() + File.separator + javaDescription.getJavaName() + SUFFIX_JAVA;
        String fileContent = FileUtils.readFile(javaFileAbsolutePath);
        if (fileContent == null) {
            return;
        }
        addJava(fileContent, new File(javaFileAbsolutePath).getParent(), javaDescription, javaPackageSet);
    }


    /**
     * 添加java内容
     *
     * @param fileContent
     * @param parentFilePath
     * @param javaFileDescription
     * @param javaPackageSet
     */
    public static void addJava(String fileContent, String parentFilePath, JavaFileDescription javaFileDescription, Set<String> javaPackageSet) {
        // 找到import的类
        Pattern pattern = Pattern.compile("import *");
        String[] packages = pattern.split(fileContent);
        // 第一个是package信息，不处理
        for (int i = 1; i < packages.length - 1; i++) {
            String packageString = packages[i].trim();
            if (packageString.startsWith(IGNORE_JAVA)
                    || (packageString.startsWith(IGNORE_ANDROID) && !packageString
                    .startsWith(NOT_IGNORE_ANDROID_SUPPORT))
                    || packageString.startsWith(IGNORE_JSON)
                    || packageString.startsWith(IGNORE_APACHE_CLIENT)
                    || packageString.startsWith(IGNORE_APACHE_IMPL)) {
                // 系统的包不加进来
                continue;
            }
            javaPackageSet.add(packageString.substring(0, packageString.length() - 1));
        }
        // 处理最后一个
        String lastOne = packages[packages.length - 1];
        String lastPackage = lastOne.split("\n")[0];
        javaPackageSet.add(lastPackage.trim().substring(0, lastPackage.length() - 2));

        // 遍历该文件夹，得到所有java文件，然后读取java文件内容，看内容里面是否有这个文件的对象啥的
        // 为什么这么做，是因为相同包的引用是不会加import的
        File dir = new File(parentFilePath);
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                continue;
            }
            String javaName = f.getName().substring(0, f.getName().length() - ".java".length());
            if (fileContent.contains(javaName)) {
                String path = null;
                if (javaFileDescription.isModule()) {
                    path = javaFileDescription.getProjectPath()
                            + javaFileDescription.getModuleBean()
                            .getModuleName() + File.separator;
                    javaPackageSet.add(
                            parentFilePath.substring(
                                    (path.length() + javaFileDescription.getModuleBean().getModuleSrcCode().length()),
                                    parentFilePath.length()).replace(File.separatorChar, '.') + "." + javaName);
                } else {
                    path = javaFileDescription.getProjectPath() + javaFileDescription.getMainProjectName() + File.separator;
                    javaPackageSet.add(
                            parentFilePath.substring(
                                    (path.length() + DexProject.SRC_CODE_ANDROID_STUDIO.length()),
                                    parentFilePath.length()).replace(File.separatorChar, '.') + "." + javaName);
                }
            }
        }
    }

    public static void addComponentJavaPackage(String component, Set<String> javaPackageSet) {
        javaPackageSet.add(component);
    }
}

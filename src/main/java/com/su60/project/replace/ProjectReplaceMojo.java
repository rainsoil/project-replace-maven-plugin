package com.su60.project.replace;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.SelectorUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mojo(name = "replace")
public class ProjectReplaceMojo extends AbstractMojo {

    /**
     * 包路径包含
     *
     * @since 2024/11/22
     */

    @Parameter(name = "includes", required = true)
    private List<String> includes;

    /**
     * 忽略的文件
     *
     * @since 2024/11/22
     */

    @Parameter(name = "excludes", required = false)
    private List<String> excludes;

    /**
     * 替换配置
     *
     * @since 2024/11/22
     */

    @Parameter(name = "replacements", required = true)
    private List<Replacement> replacements;


    /**
     * 来源目录
     *
     * @since 2024/11/22
     */

    @Parameter(name = "sourcedir", required = true)
    private String sourcedir;

    /**
     * 目标目录,当为空的时候取{sourcedir}
     *
     * @since 2024/11/22
     */

    @Parameter(name = "targetdir", required = false)
    private String targetdir;

    /**
     * 是否替换目录
     *
     * @since 2024/11/22
     */

    @Parameter(name = "replacePath", required = false, defaultValue = "true")
    private Boolean replacePath;

    /**
     * 是否删除旧的内容
     *
     * @since 2024/11/23
     */

    @Parameter(name = "deleteOld", required = false, defaultValue = "true")
    private Boolean deleteOld;


    public void execute() throws MojoExecutionException, MojoFailureException {

//
//        replacePath = true;
//        deleteOld = false;
//        sourcedir = "E:/tmp/20241122/quick-boot";
//        replacements = new ArrayList<>();
//        Replacement replacement1 = new Replacement();
//        replacement1.setSource("cn.t200");
//        replacement1.setTarget("com.su60");
//        Replacement replacement2 = new Replacement();
//        replacement2.setSource("cn/t200");
//        replacement2.setTarget("com/su60");
//        replacements.add(replacement1);
//        replacements.add(replacement2);
//        targetdir = "E:/tmp/20241122/target/quick-boot";
//        includes = new ArrayList<>();
//        includes.add("**/*.java");
//        includes.add("**/*.xml");
//
//        excludes = new ArrayList<>();
//        excludes.add("**/.git/**");
//        excludes.add("**/.idea/**");
//        excludes.add("**/doc/**");
//        excludes.add("**/target/**");

        System.out.println("开始替换文件");
        try {
            if (null == targetdir || targetdir.isEmpty()) {
                targetdir = sourcedir;
            }
            File sourceDir = new File(sourcedir);
            File targetDir = new File(targetdir);

            if (!sourceDir.exists() || !sourceDir.isDirectory()) {
                System.err.println("Source directory does not exist or is not a directory.");
                return;
            }

            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            // 获取所有文件
            Collection<File> allFiles = FileUtils.getFiles(sourceDir, "**", "");

            // 处理匹配的文件
            for (File file : allFiles) {


                // 目录替换
                String absolutePath = normalizePath(file.getAbsolutePath());
                String newFilePath = absolutePath.replace(sourcedir, targetdir);
                // 处理忽略的文件 不移动 不替换

                if (isMatch(absolutePath, excludes)) {
                    continue;
                }

                String content = FileUtils.fileRead(file);
                if (isMatch(absolutePath, includes)) {
                    // 内容替换
                    content = applyReplacements(content, replacements);
                }
                // 路径是否替换
                newFilePath = replacePath ? applyReplacements(normalizePath(newFilePath), replacements) : newFilePath;
                Path parent = Paths.get(newFilePath).getParent();
                // 判断 parent 是否存在,不存在则创建
                if (!Files.exists(parent)) {
                    Files.createDirectories(parent);
                }
                System.out.println("将文件从:" + absolutePath + "移动到:" + newFilePath);
                if (this.deleteOld) {
                    Files.delete(file.toPath());
                }

                // 将内容写入目标文件
                FileUtils.fileWrite(new File(newFilePath), content);


            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isMatch(String filePath, List<String> includesPattern) {
        for (String pattern : includesPattern) {
            if (SelectorUtils.matchPath(pattern, filePath)) {
                return true;
            }
        }
        return false;
    }

    private static String normalizePath(String path) {
        return path.replace("\\", "/");
    }

    private static String applyReplacements(String input, List<Replacement> replacements) {
        String result = input;
        for (Replacement replacement : replacements) {
            result = result.replace(replacement.getSource(), replacement.getTarget());
        }
        return result;
    }

    public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
        ProjectReplaceMojo projectReplaceMojo = new ProjectReplaceMojo();
        projectReplaceMojo.execute();
    }
}
package com.felink.signaturev2;

import com.felink.signaturev2.domain.KeyStore;
import com.felink.signaturev2.kitset.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignerV1 {

    public static boolean sign(String[] params, String workpath) {
        boolean result = false;
        Process process = null;
        try {
            final String jarsigner;
            String javahome = System.getenv("JAVA_HOME");
            System.out.println("JAVA_HOME: " + javahome);
            if (!StringUtil.isEmpty(javahome)) {
                jarsigner = "jarsigner";
                workpath = javahome;
            } else {
                File jarsignerFile = new File("/jarsigner.exe");
                jarsigner = SignerTool.exportMetaFile("jarsigner.exe", jarsignerFile.getAbsolutePath());
                workpath = jarsignerFile.getParent();
            }
            List<String> cmdarray = new ArrayList<>();
            String os = System.getProperty("os.name");
            if (!StringUtil.isEmpty(os)) {
                if ("Linux".equals(os)) {
                    cmdarray.add("/bin/sh");
                    cmdarray.add("-c");
                } else if (os.contains("Windows")) {
                    cmdarray.add("cmd");
                    cmdarray.add("/C");
                }
            }

            cmdarray.add(jarsigner);
            cmdarray.addAll(Arrays.asList(params));

            String[] cmds = new String[cmdarray.size()];
            for (int i = 0, len = cmdarray.size(); i < len; i++) {
                cmds[i] = cmdarray.get(i);
            }
            System.out.println(cmdarray);

            process = Runtime.getRuntime().exec(cmds, null, new File(workpath));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            //工具执行结果
            boolean zipalignResult = false;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("jar 已签名")) {
                    zipalignResult = true;
                    break;
                }
            }
            int code = process.waitFor();
//            System.out.println("Task signature v1 result : " + code);

            result = (code == 0 && zipalignResult);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    public static boolean sign(String inputPath, String outputPath, KeyStore keyStore, List<String> extras) {
        System.out.println("SignV1 : inputPath = [" + inputPath + "], outputPath = [" + outputPath + "], keyStore = [" + keyStore + "], extras = [" + extras + "]");
        List<String> cmdarray = new ArrayList<String>();
        final String[] prearray = {
                "-storepass", keyStore.pass,
                "-keypass", keyStore.keyPass,
                "-keystore", keyStore.path,
        };

        if (extras != null && extras.size() > 0) {
            cmdarray.addAll(extras);
        }

        cmdarray.addAll(Arrays.asList(prearray));
        cmdarray.add("-signedjar");
        cmdarray.add(outputPath);
        cmdarray.add(inputPath);
        cmdarray.add(keyStore.keyAlias);

        String[] cmds = new String[cmdarray.size()];
        for (int i = 0, len = cmdarray.size(); i < len; i++) {
            cmds[i] = cmdarray.get(i);
        }

        String workdir = new File(outputPath).getParent();
        System.out.println(workdir);
        boolean result = sign(cmds, workdir);
        if (result) {
            System.out.println("Apk v1 signed file path : " + outputPath);
        }
        return result;
    }
}

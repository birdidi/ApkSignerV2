package com.felink.signaturev2;

import com.android.apksigner.ApkSignerTool;
import com.felink.signaturev2.domain.KeyStore;
import com.felink.signaturev2.kitset.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SignerTool {

    public static final String TEMP_DIR = new File("/sign_tmp/").getAbsolutePath();

    private static PrintStream consoleMonitor;

    static {
        try {
            consoleMonitor = new Monitor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PrintStream sourceOut = System.out;
    private static PrintStream sourceErr = System.err;

    private static void monitor() {
        System.setOut(consoleMonitor);
        System.setErr(consoleMonitor);
    }

    private static void unmonitor() {
        System.setOut(sourceOut);
        System.setErr(sourceErr);
        consoleMonitor.close();
    }

    public static boolean sign(String[] params) {
        monitor();
        boolean result = false;
        try {
            if (params != null && params.length > 0) {
                String inputPath = null, outputPath = null;
                KeyStore keyStore = new KeyStore();
                List<String> extras = new ArrayList<>();
                boolean v1 = true, v2 = true;
                for (int i = 0, len = params.length; i < len; i++) {
                    String opt = params[i];
                    if ("-storepass".equals(opt)) {
                        keyStore.pass = params[++i];
                    } else if ("-keypass".equals(opt)) {
                        keyStore.keyPass = params[++i];
                    } else if ("-keystore".equals(opt)) {
                        keyStore.path = params[++i];
                    } else if ("-signedjar".equals(opt)) {
                        outputPath = params[++i];
                        inputPath = params[++i];
                        keyStore.keyAlias = params[++i];
                    } else if ("-v1-signing-enabled".equals(opt)) {
                        try {
                            v1 = Boolean.valueOf(params[i++]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if ("-v2-signing-enabled".equals(opt)) {
                        try {
                            v2 = Boolean.valueOf(params[i++]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        extras.add(opt);
                    }
                }
                result = sign(inputPath, outputPath, keyStore, v1, v2, false, extras);
            }
        } finally {
            unmonitor();
        }
        return result;
    }

    /**
     * 签名（v1、v2一般填true，zipalign填falase）
     *
     * @param inputpath  待签名APK文件
     * @param outputpath 已签名apk文件
     * @param keyStore   keystore对象
     * @param v1         是否v1签名
     * @param v2         是否v2签名
     * @param zipalign   是否进行zipalign
     * @param extras     额外参数
     * @return
     */
    public static boolean sign(final String inputpath, final String outputpath, KeyStore keyStore, boolean v1, boolean v2, boolean zipalign, List<String> extras) {
        boolean result = false;
        delAllTmp();

        FileUtil.createDir(TEMP_DIR);
        String finalOutputPath = new File(TEMP_DIR, FileUtil.getFileName(outputpath)).getAbsolutePath();
        String finalInputPath = new File(TEMP_DIR, FileUtil.getFileName(inputpath)).getAbsolutePath();

        if (!FileUtil.copy(inputpath, finalInputPath)) {
            finalInputPath = inputpath;
        }
        try {
            if (v2) {//v2签名直接用apksignerv2.jar
                finalOutputPath = finalInputPath + "_signed" + (v1 ? "v1v2" : "v2");
                result = SignerV2.sign(finalInputPath, finalOutputPath, keyStore, v1, v2);
                System.out.println("Sign V2 " + (result ? "Successful" : "Failure"));
            } else if (v1) {//只要v1签名的，仍用jarsigner.exe
                finalOutputPath = finalInputPath + "_signedv1";
                result = SignerV1.sign(finalInputPath, finalOutputPath, keyStore, extras);
                System.out.println("Sign V1 " + (result ? "Successful" : "Failure"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!result) {
                FileUtil.delFile(outputpath);
            } else {
                FileUtil.copy(finalOutputPath, outputpath);
            }
        }
        return result;
    }

    static void delAllTmp() {
        FileUtil.delAllFile(TEMP_DIR);
    }

//    static String getTempFile(String fileName) {
//        FileUtil.createDir(TEMP_DIR);
//        return new File(TEMP_DIR + fileName).getAbsolutePath();
//    }

    static String exportMetaFile(String path, String outputPath) throws Exception {
        if (FileUtil.isExists(outputPath)) {
            return outputPath;
        }
        InputStream is = null;
        OutputStream os = null;
        try {
            is = SignerTool.class.getResourceAsStream("/META-INF/" + path);
            os = new FileOutputStream(outputPath);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
                os.flush();
            }
            return outputPath;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void verify(String path) throws Exception {
        String[] verifyparams = {"verify", "-v", path};
        ApkSignerTool.main(verifyparams);
    }

    public static String zipalign(String inputPath) throws Exception {
        final String zipalignPath = exportMetaFile("zipalign.exe", new File("/zipalign.exe").getAbsolutePath());
        final String tempFilePath = inputPath + "_aligned";
        FileUtil.delFile(tempFilePath);

        String[] cmdarray = {"cmd.exe", "/C", zipalignPath, "-v", "-p", "4", inputPath, tempFilePath};
        boolean zipalignResult = false;
        int code = 0;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmdarray);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            //工具执行结果
            zipalignResult = false;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("Verification succesful")) {
                    zipalignResult = true;
                    break;
                }
            }
            code = process.waitFor();
            System.out.println("Task zip align result : " + code);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return (code == 0 && zipalignResult) ? tempFilePath : null;
    }
}

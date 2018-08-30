package com.felink.signaturev2;

import com.android.apksigner.ApkSignerTool;
import com.felink.signaturev2.domain.KeyStore;
import com.felink.signaturev2.kitset.FileUtil;

import java.util.Arrays;

public class SignerV2 {

    /**
     * 签名（同步）
     *
     * @param inputPath  待签名app文件路径
     * @param outputPath 已签名app文件路径
     * @param keyStore   keystore文件信息{@link KeyStore}
     * @param zipalign   是否做zipalign
     * @return v2签名是否成功
     */
    public static boolean sign(String inputPath, String outputPath, KeyStore keyStore, boolean zipalign, boolean v1, boolean v2) {
        boolean result = false;
        try {
            String tempPath = zipalign ? SignerTool.zipalign(inputPath) : inputPath;
            System.out.println(tempPath);
            if (tempPath != null) {
                String[] signparams = {"sign",
                        "--ks", keyStore.path,
                        "--ks-key-alias", keyStore.keyAlias,
                        "--ks-pass", "pass:" + keyStore.pass,
                        "--key-pass", "pass:" + keyStore.keyPass,
                        "--v1-signing-enabled", "" + v1,
                        "--v2-signing-enabled", "" + v2,
                        "--out",
                        outputPath,
                        tempPath};
                System.out.println(Arrays.asList(signparams));
                ApkSignerTool.main(signparams);
                result = FileUtil.isExists(outputPath);
                if (result) {
                    SignerTool.verify(outputPath);
                    if (v1) {
                        result = result && Monitor.contains((Monitor) System.out, "Verified using v1 scheme (JAR signing): true");
                    }
                    if (v2) {
                        result = result && Monitor.contains((Monitor) System.out, "Verified using v2 scheme (APK Signature Scheme v2): true");
                    }
                }
            }
            if (zipalign) {
                FileUtil.delFile(tempPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("V2 sign result : " + result);
            System.out.println(Monitor.getContent((Monitor) System.out));
        }
        return result;
    }

    /**
     * 签名(同步)
     *
     * @param inputPath  待签名app文件路径
     * @param outputPath 已签名app文件路径
     * @param keyStore   keystore文件信息{@link KeyStore}
     * @return v2签名是否成功
     */
    public static boolean sign(String inputPath, String outputPath, KeyStore keyStore, boolean v1, boolean v2) {
        System.out.println("SignV2 : inputPath = [" + inputPath + "], outputPath = [" + outputPath + "], keyStore = [" + keyStore + "]");
        return sign(inputPath, outputPath, keyStore, false, v1, v2);
    }
}

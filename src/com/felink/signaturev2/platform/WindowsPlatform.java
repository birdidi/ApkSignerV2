package com.felink.signaturev2.platform;

import com.felink.signaturev2.kitset.Cmd;
import com.felink.signaturev2.kitset.CommonUtil;
import com.felink.signaturev2.kitset.FileUtil;
import com.felink.signaturev2.kitset.StringUtil;

import java.io.File;
import java.io.IOException;

public class WindowsPlatform implements IPlatform {
    @Override
    public String zipalign(String inputPath) throws Exception {
        final String zipalignPath = CommonUtil.exportMetaFile("zipalign.exe", new File(File.separator + "zipalign.exe").getAbsolutePath());
        final String tempFilePath = inputPath + "_aligned";
        FileUtil.delFile(tempFilePath);

        String[] cmdarray = new String[]{/*"cmd", "/C", */zipalignPath, "-v", "-f", "4", inputPath, tempFilePath};

        boolean zipalignResult = false;
        Process process = null;
        try {
            String content = Cmd.exec(cmdarray);
            if (!StringUtil.isEmpty(content) && content.contains("Verification succesful")) {
                zipalignResult = true;
            }
            System.out.println("Task zip align result : " + zipalignResult);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return (zipalignResult) ? tempFilePath : null;
    }

    @Override
    public boolean checkZipaligned(String src) throws Exception {
        final String zipalignPath = CommonUtil.exportMetaFile("zipalign.exe", new File(File.separator + "zipalign.exe").getAbsolutePath());

        String[] cmdarray = new String[]{/*"cmd", "/C",*/ zipalignPath, "-c", "-v", "4", src};

        boolean zipalignResult = false;
        Process process = null;
        try {
            String content = Cmd.exec(cmdarray);
            if (!StringUtil.isEmpty(content) && content.contains("Verification succesful")) {
                zipalignResult = true;
            }
            System.out.println(src + " is zip aligned ? " + zipalignResult);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return (zipalignResult) ? true : false;
    }
}

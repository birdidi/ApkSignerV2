package com.felink.signaturev2.platform;

import com.felink.signaturev2.kitset.StringUtil;

public class PlatformDor {

    private static IPlatform sPlatform = null;

    public static String zipalign(String inputpath) throws Exception {
        return getOsPlatform().zipalign(inputpath);
    }

    public static boolean checkZipaligned(String inputpath) throws Exception {
        return getOsPlatform().checkZipaligned(inputpath);
    }

    private static IPlatform getOsPlatform() {
        if (sPlatform != null) {
            return sPlatform;
        }
        String os = System.getProperty("os.name");
        if (!StringUtil.isEmpty(os)) {
            if ("Linux".equals(os)) {
                sPlatform = new LinuxPlatform();
            }
        }
        if (sPlatform == null) {
            sPlatform = new WindowsPlatform();
        }
        System.out.println("Platform : " + sPlatform);
        return sPlatform;
    }
}

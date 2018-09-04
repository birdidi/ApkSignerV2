package com.felink.signaturev2.kitset;

import com.felink.signaturev2.SignerTool;

import java.io.*;

public class CommonUtil {

    public static String exportMetaFile(String path, String outputPath) throws Exception {
        System.out.println("Extract META-INF file from " + path + " to " + outputPath);
        if (FileUtil.isExists(outputPath)) {
            return outputPath;
        }

        File ofile = new File(outputPath);
        ofile.getParentFile().mkdirs();

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

    public static void slientClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

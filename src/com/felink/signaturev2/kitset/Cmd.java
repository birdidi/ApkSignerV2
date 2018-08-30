package com.felink.signaturev2.kitset;

import java.io.*;

public class Cmd {

    public static int exec(String cmd, boolean waitfor) throws Exception {
        Process process = null;
        process = Runtime.getRuntime().exec(cmd);
        return process(process, waitfor);
    }

    public static int exec(String[] cmdarray, boolean waitfor) throws Exception {
        Process process = null;
        process = Runtime.getRuntime().exec(cmdarray);
        return process(process, waitfor);
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

    private static int process(Process process, boolean waitfor) throws InterruptedException {
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        ThreadTool.execute(new LoopInputOrErrorBuffer(inputStream));
        ThreadTool.execute(new LoopInputOrErrorBuffer(errorStream));
        if (waitfor) {
            return process.waitFor();
        }
        return 0;
    }

    static class LoopInputOrErrorBuffer implements Runnable {

        private InputStream mIOStream;

        public LoopInputOrErrorBuffer(InputStream InputStream) {
            this.mIOStream = InputStream;
        }

        @Override
        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(mIOStream));
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                slientClose(mIOStream);
            }
        }
    }
}

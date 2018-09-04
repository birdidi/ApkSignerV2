package com.felink.signaturev2.kitset;

import java.io.*;

public class FileUtil {

    public static void delFile(String path) {
        if (isExists(path)) {
            File file = new File(path);
            file.delete();
        }
    }

    public static boolean isExists(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        return file != null && file.exists();
    }

    public static void writeFile(String path, String content, boolean append) {
        try {
            File f = new File(path);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }

            if (!f.exists()) {
                f.createNewFile();
                f = new File(path);
            }

            FileWriter fw = new FileWriter(f, append);
            if (content != null && !"".equals(content)) {
                fw.write(content);
                fw.flush();
            }

            fw.close();
        } catch (Exception var5) {
            var5.printStackTrace();
        }
    }

    public static void createDir(String dir) {
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }

    }

    public static boolean copy(String srcFile, String destFile) {
        FileInputStream in = null;
        FileOutputStream out = null;

        boolean var5;
        try {

            if (!isExists(destFile)) {
                File dst = new File(destFile);
                dst.getParentFile().mkdirs();
                dst.createNewFile();
            }

            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] bytes = new byte[1024];

            int c;
            while ((c = in.read(bytes)) != -1) {
                out.write(bytes, 0, c);
            }

            out.flush();
            boolean var6 = true;
            return var6;
        } catch (Exception var20) {
            System.out.println("Error!" + var20);
            var5 = false;
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException var19) {
                    var19.printStackTrace();
                }
            }

            if (null != out) {
                try {
                    out.close();
                } catch (IOException var18) {
                    var18.printStackTrace();
                }
            }

        }

        return var5;
    }

    public static void copyFolder(String oldPath, String newPath) {
        (new File(newPath)).mkdirs();
        File a = new File(oldPath);
        String[] file = a.list();
        if (null != file) {
            File temp = null;

            for(int i = 0; i < file.length; ++i) {
                try {
                    if (oldPath.endsWith(File.separator)) {
                        temp = new File(oldPath + file[i]);
                    } else {
                        temp = new File(oldPath + File.separator + file[i]);
                    }

                    if (temp.isFile()) {
                        FileInputStream input = new FileInputStream(temp);
                        FileOutputStream output = new FileOutputStream(newPath + "/" + temp.getName().toString());
                        byte[] b = new byte[5120];

                        int len;
                        while((len = input.read(b)) != -1) {
                            output.write(b, 0, len);
                        }

                        output.flush();
                        output.close();
                        input.close();
                    }

                    if (temp.isDirectory()) {
                        copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                    }
                } catch (Exception var10) {
                    var10.printStackTrace();
                }
            }

        }
    }

    public static void moveFile(String oldPath, String newPath) {
        copy(oldPath, newPath);
        delFile(oldPath);
    }

    public static boolean delAllFile(String path) {
        return delAllFile(path, (FilenameFilter) null);
    }

    public static boolean delAllFile(String path, FilenameFilter filenameFilter) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return true;
        } else if (!file.isDirectory()) {
            return flag;
        } else {
            File[] tempList = file.listFiles(filenameFilter);
            if (tempList == null) {
                return true;
            } else {
                int length = tempList.length;

                for (int i = 0; i < length; ++i) {
                    if (tempList[i].isFile()) {
                        tempList[i].delete();
                    }

                    if (tempList[i].isDirectory()) {
                        delAllFile(tempList[i].getAbsolutePath(), filenameFilter);
                        String[] ifEmptyDir = tempList[i].list();
                        if (null == ifEmptyDir || ifEmptyDir.length <= 0) {
                            tempList[i].delete();
                        }

                        flag = true;
                    }
                }

                return flag;
            }
        }
    }

    public static String getFileName(String path, boolean hasSuffix) {
        if (null != path && -1 != path.lastIndexOf("/") && -1 != path.lastIndexOf(".")) {
            return !hasSuffix ? path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")) : path.substring(path.lastIndexOf("/") + 1);
        } else {
            return null;
        }
    }

    public static String getFileName(String path) {
        return new File(path).getName();
    }

    public static String readFileContent(String path) {
        StringBuffer sb = new StringBuffer();
        if (!isExists(path)) {
            return sb.toString();
        } else {
            FileInputStream ins = null;

            try {
                ins = new FileInputStream(new File(path));
                BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (Exception var13) {
                var13.printStackTrace();
            } finally {
                if (ins != null) {
                    try {
                        ins.close();
                    } catch (IOException var12) {
                        var12.printStackTrace();
                    }
                }

            }

            return sb.toString();
        }
    }
}

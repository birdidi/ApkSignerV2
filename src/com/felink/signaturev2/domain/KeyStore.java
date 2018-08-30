package com.felink.signaturev2.domain;

public class KeyStore {

    /**
     * keystore文件地址
     */
    public String path;
    /**
     * keystore密码
     */
    public String pass;
    /**
     * key 别名
     */
    public String keyAlias;
    /**
     * key 密码
     */
    public String keyPass;

    public KeyStore() {

    }

    public KeyStore(String path, String pass, String keyAlias, String keyPass) {
        this.path = path;
        this.pass = pass;
        this.keyAlias = keyAlias;
        this.keyPass = keyPass;
    }

    public KeyStore(String path, String generateName) {
        this(path, generateName, generateName, generateName);
    }
}

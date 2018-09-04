package com.felink.signaturev2.platform;

public interface IPlatform {

    String zipalign(String inputpath) throws Exception;

    boolean checkZipaligned(String inputpath) throws Exception;
}

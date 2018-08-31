import com.felink.signaturev2.SignerTool;
import com.felink.signaturev2.domain.KeyStore;

public class Main {


    public static void main(String[] args) {
        boolean signed = false;
        try {
//            KeyStore keyStore = new KeyStore("E:/dev/ndkeystore", "ndkeystore");
//            SignerTool.sign("F:/220d8255c9a449799b72befd1e2db982.apk", "F:/vvv.apk", keyStore, true, true, false, null);
            signed = SignerTool.sign(args);
//            SignerTool.verify("F:/b48a22e46a5c457c83b2b30d9f1b42f8_sign.apk");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        System.out.println("signed : " + signed);
    }
}

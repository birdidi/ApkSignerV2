import com.felink.signaturev2.SignerTool;

public class Main {


    public static void main(String[] args) {
        boolean signed = false;
        try {
            signed = SignerTool.sign(args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        System.out.println("signed : " + signed);
    }
}

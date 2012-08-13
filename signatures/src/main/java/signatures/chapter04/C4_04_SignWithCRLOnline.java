package signatures.chapter04;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.log.LoggerFactory;
import com.itextpdf.text.log.SysoLogger;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.CrlClientOnline;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.MakeSignature;

public class C4_04_SignWithCRLOnline extends C4_01_SignWithCAcert {
	public static final String SRC = "src/main/resources/hello.pdf";
	public static final String DEST = "results/hello_cacert_crl.pdf";
	
	public static void main(String[] args) throws IOException, GeneralSecurityException, DocumentException {
		LoggerFactory.getInstance().setLogger(new SysoLogger());
		Properties properties = new Properties();
		properties.load(new FileInputStream("c:/home/blowagie/key.properties"));
    	String path = properties.getProperty("PRIVATE");
        String pass = properties.getProperty("PASSWORD");

		BouncyCastleProvider provider = new BouncyCastleProvider();
		Security.addProvider(provider);
        KeyStore ks = KeyStore.getInstance("pkcs12", provider.getName());
		ks.load(new FileInputStream(path), pass.toCharArray());
        String alias = (String)ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, pass.toCharArray());
        Certificate[] chain = ks.getCertificateChain(alias);
        CrlClient crlClient = new CrlClientOnline("http://crl.cacert.org/revoke.crl");
        List<CrlClient> crlList = new ArrayList<CrlClient>();
        crlList.add(crlClient);
        C4_04_SignWithCRLOnline app = new C4_04_SignWithCRLOnline();
		app.sign(pk, chain, SRC, DEST, provider.getName(), "Test", "Ghent", DigestAlgorithms.SHA256, MakeSignature.CMS,
				crlList, null, null, 0);
	}
  
}

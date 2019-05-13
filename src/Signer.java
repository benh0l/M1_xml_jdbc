import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Collections;

public class Signer {

    public static void creerSignature(File docXML) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, IOException, ParserConfigurationException, SAXException, MarshalException, XMLSignatureException, CertificateException, KeyException {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder builder = dbf.newDocumentBuilder();

            Document doc = builder.parse(new FileInputStream(docXML));

            DOMSignContext dsc = new DOMSignContext(kp.getPrivate(), doc.getDocumentElement());

            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

            Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA256, null), Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null, null);

            SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,(C14NMethodParameterSpec) null), fac.newSignatureMethod("http://www.w3.org/2009/xmldsig11#dsa-sha256", null), Collections.singletonList(ref));

            KeyInfoFactory kif = fac.getKeyInfoFactory();

            KeyValue kv = kif.newKeyValue(kp.getPublic());
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

            XMLSignature signature = fac.newXMLSignature(si, ki);

            signature.sign(dsc);
            System.out.println("SIGNER");

            OutputStream os;
            os = new FileOutputStream(docXML);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.transform(new DOMSource(doc), new StreamResult(os));


        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | KeyException | MarshalException | XMLSignatureException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}

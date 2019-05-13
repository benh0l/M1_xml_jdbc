import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class VerificationSignature {

    public static boolean verifier(File file,JFrame frame) throws Exception {
        boolean res = false;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(new FileInputStream(file));

        NodeList nl = doc.getElementsByTagNameNS
                (XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            JOptionPane.showMessageDialog(frame,"Signature non validée !","Echec validation signature",JOptionPane.ERROR_MESSAGE);
            throw new Exception("Cannot find "+"Signature"+" element");
        }

        DOMValidateContext valContext = new DOMValidateContext(new KeyValueKeySelector(), nl.item(0));

        XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
        XMLSignature signature = factory.unmarshalXMLSignature(valContext);

        boolean coreValidity = signature.validate(valContext);

        if(coreValidity)
            System.out.println("Validation : "+coreValidity);
        else
            System.out.println("Erreur, signature invalide : "+file.getName());
        return coreValidity;
    }

}

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
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
import java.security.cert.X509Certificate;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Rechercher {

    private String url = "";
    private ArrayList<String> champs, tables;
    private String condition;

    public Rechercher(String url){
        champs = new ArrayList<>();
        tables = new ArrayList<>();
        condition = "";
        this.url = url;
    }

    public boolean rechercher(JFrame frame,String urlRes) throws Exception {
        boolean validation = false;

        //Etape 1 : vérification signature
        validation = VerificationSignature.verifier(new File(url), frame);

        if(validation){
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            boolean v = false; //Passer à true pour afficher les contenus du fichierXML
            try {
                // Etape 2 : création d'un parseur
                final DocumentBuilder builder = factory.newDocumentBuilder();

                //Etape 3 : création d'un Document
                final Document document = builder.parse(new File(url));

                final Element racine = document.getDocumentElement();

                if (!racine.getNodeName().equals("SELECT")) {
                    System.out.println("Erreur pas racine =/= SELECT");
                }

                // Etape 5 : récupération des champs
                final NodeList racineNoeuds = racine.getChildNodes();
                final int nbRacineNoeuds = racineNoeuds.getLength();

                for(int i = 0; i < nbRacineNoeuds; i++){
                    if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE){
                        Element element = (Element) racineNoeuds.item(i);
                        String name = element.getNodeName();
                        if(name.equals("CHAMPS")){
                            NodeList nl = element.getChildNodes();
                            for(int j = 0; j < nl.getLength(); j++){
                                if(nl.item(j).getNodeType() == Node.ELEMENT_NODE){
                                    Element champ = (Element) nl.item(j);
                                    champs.add(champ.getTextContent());
                                }
                            }
                        }else if(name.equals("TABLES")){
                            NodeList nl = element.getChildNodes();
                            for(int j = 0; j < nl.getLength(); j++){
                                if(nl.item(j).getNodeType() == Node.ELEMENT_NODE){
                                    Element table = (Element) nl.item(j);
                                    tables.add(table.getTextContent());
                                }
                            }
                        }else if(name.equals("CONDITION")){
                            condition = element.getTextContent();
                        }

                    }
                }

                String query = "SELECT ";
                for(int ii = 0;ii < champs.size();ii++){
                    query += champs.get(ii)+" ";
                    if(ii != champs.size()-1)
                        query += ", ";
                }
                query += "FROM ";
                for(int ii = 0;ii < tables.size();ii++){
                    query += tables.get(ii)+" ";
                    if(ii != tables.size()-1)
                        query += ", ";
                }
                if(!condition.equals(""))
                    query += "WHERE "+condition;

                System.out.println(query);

                Statement stmt = Main.con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

            /*
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue);
                }
                System.out.println("");
            }
            */
                //System.out.println(genererXML(rs));
                File xml = new File(urlRes);
                FileWriter fileWriter = new FileWriter(xml);
                try {
                    fileWriter.write(genererXML(rs));
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                fileWriter.close();
                Signer.creerSignature(xml);

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (XMLSignatureException e) {
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (MarshalException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (KeyException e) {
                e.printStackTrace();
            }
        }

        return validation;
    }

    public String genererXML(ResultSet rs) throws SQLException, InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        ResultSetMetaData rsmd = rs.getMetaData();

        String xml = "<RESULTAT> \n <TUPLES> \n";
        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            xml += "        <TUPLE>\n";
            for (int i = 1; i <= columnsNumber; i++) {
                String columnValue = rs.getString(i);
                xml += "            <"+rsmd.getColumnName(i)+">"+columnValue+"</"+rsmd.getColumnName(i)+">\n";
            }
            xml += "        </TUPLE>\n";
        }
        xml += "    </TUPLES>\n</RESULTAT>";
        return xml;
    }


}

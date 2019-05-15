import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

public class Maj {
    private String url = "";
    private String table, champ, value, condition;

    public Maj(String url) {
        table = "";
        condition = "";
        champ = "";
        value = "";
        this.url = url;
    }

    public void maj(JFrame frame) throws Exception {
        //ETAPE 1 Verification signature
        boolean validation = VerificationSignature.verifier(new File(url),frame);

        if(validation){
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                // Etape 2 : création d'un parseur
                final DocumentBuilder builder = factory.newDocumentBuilder();

                //Etape 3 : création d'un Document
                final Document document = builder.parse(new File(url));

                final Element racine = document.getDocumentElement();

                if (!racine.getNodeName().equals("UPDATE")) {
                    System.out.println("Erreur pas racine =/= UPDATE");
                    JOptionPane.showMessageDialog(frame,"Le noeud racine n'est pas <UPDATE>","Document non valide",JOptionPane.ERROR_MESSAGE);
                }else {

                    // Etape 5 : récupération des champs
                    final NodeList racineNoeuds = racine.getChildNodes();
                    final int nbRacineNoeuds = racineNoeuds.getLength();

                    for (int i = 0; i < nbRacineNoeuds; i++) {
                        if (racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) racineNoeuds.item(i);
                            String name = element.getNodeName();
                            if (name.equals("TABLE")) {
                                table = element.getTextContent();
                            } else if (name.equals("CONDITION")) {
                                condition = element.getTextContent();
                            } else if (name.equals("CHAMP")) {
                                champ = element.getTextContent();
                            } else if (name.equals("VALUE")) {
                                value = element.getTextContent();
                            }

                        }
                    }

                    String query = "UPDATE " + table + " SET " + champ + " = " + value;

                    if (!condition.equals(""))
                        query += " WHERE " + condition;

                    System.out.println(query);

                    Statement stmt = Main.con.createStatement();
                    stmt.executeUpdate(query);
                    JOptionPane.showMessageDialog(frame,"Mise à jour effectuée avec succès","Succès",JOptionPane.INFORMATION_MESSAGE);
                }

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }

    }
}

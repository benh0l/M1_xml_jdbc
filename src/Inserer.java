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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Inserer {

    private String url = "";
    private ArrayList<String> values;
    private String table;

    public Inserer(String url){
        values = new ArrayList<>();
        table = "";
        this.url = url;
    }

    public void inserer(JFrame frame) throws Exception {
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

                if (!racine.getNodeName().equals("INSERT")) {
                    System.out.println("Erreur pas racine =/= INSERT");
                }

                // Etape 5 : récupération des champs
                final NodeList racineNoeuds = racine.getChildNodes();
                final int nbRacineNoeuds = racineNoeuds.getLength();

                String query = "";
                boolean first = true;
                for(int i = 0; i < nbRacineNoeuds; i++){
                    if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE){
                        Element element = (Element) racineNoeuds.item(i);
                        String name = element.getNodeName();
                        if(name.equals("TABLE")){
                            table = element.getTextContent();
                            query = "INSERT INTO "+table+" ";
                        }else if(name.equals("VALUES")){
                            NodeList nl = element.getChildNodes();

                            if(first) {
                                query += "VALUES(";
                                first = false;
                            }else {
                                query += ",(";
                            }

                            for(int j = 0; j < nl.getLength(); j++){
                                if(nl.item(j).getNodeType() == Node.ELEMENT_NODE){
                                    Element value = (Element) nl.item(j);
                                    values.add(value.getTextContent());
                                }
                            }
                            for(int ii = 0;ii < values.size();ii++){
                                query += values.get(ii);
                                if(ii != values.size()-1)
                                    query += ", ";
                                else
                                    query +=")";
                            }
                            values = new ArrayList<>();
                        }

                    }
                }
                query += ";";
                System.out.println(query);

                Statement stmt = Main.con.createStatement();
                stmt.executeUpdate(query);

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}

import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.*;

public class Main {


    static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/rugby_xml?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC";
    static final String user = "root";
    static final String pswd = "";

    public static Connection con;

    public static void main(String args[]){
        initDatabase();

        /*
        Signer.creerSignature(new File("src/test_delete.xml"));
        Signer.creerSignature(new File("src/test_insert.xml"));
        Signer.creerSignature(new File("src/test_select.xml"));
        Signer.creerSignature(new File("src/test_update.xml"));

        Rechercher rechercher = new Rechercher("src/test_select.xml");
        rechercher.rechercher();

        VerificationSignature.verifier(new File("resultat.xml"));

        Inserer ins = new Inserer("src/test_insert.xml");
        ins.inserer();

        Effacer eff = new Effacer("src/test_delete.xml");
        eff.effacer();

        Maj maj = new Maj("src/test_update.xml");
        maj.maj();
        */

        JFrame frame = new JFrame("XML - Coupe du monde de Rugby - Benoît HOLZER");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton fichier = new JButton("Sélectionner un fichier xml");
        fichier.setSize(new Dimension(200,50));
        fichier.setBounds(270,50,190,50);
        JTextField status = new JTextField("");
        status.setEditable(false);
        status.setSize(new Dimension(250,50));
        status.setBounds(15,50,250,50);
        fichier.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setApproveButtonText("Choix");
                //jfc.showOpenDialog(null);
                if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                    status.setText(jfc.getSelectedFile().getAbsolutePath());
            }
        });


        JButton fichierRes = new JButton("Enregistrer recherche sous");
        fichierRes.setSize(new Dimension(200,50));
        fichierRes.setBounds(270,250,200,50);
        JTextField statusRes = new JTextField("");
        statusRes.setEditable(false);
        statusRes.setSize(new Dimension(250,50));
        statusRes.setBounds(15,250,250,50);
        fichierRes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setApproveButtonText("Choix");
                //jfc.showOpenDialog(null);
                if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                    statusRes.setText(jfc.getSelectedFile().getAbsolutePath());
            }
        });


        JButton rechercher = new JButton("Rechecher");
        rechercher.setSize(150,50);
        rechercher.setBounds(15,150,110,50);
        rechercher.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!status.getText().equals("")){
                    Rechercher r = new Rechercher(status.getText());
                    try {
                        if(!statusRes.getText().equals("")){
                            r.rechercher(frame,statusRes.getText()+".xml");
                            //JOptionPane.showMessageDialog(frame,"Recherche effectuée avec succès\nFichier résultat enregistré :\n"+statusRes.getText()+".xml","Succès",JOptionPane.INFORMATION_MESSAGE);
                            status.setText("");
                            statusRes.setText("");
                        }else{
                            JOptionPane.showMessageDialog(frame,"Veuillez indiquer l'endroit ou vous souhaitez enregistrer le résultat","Erreur pas de fichier de sortie",JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(frame,e1,"Exception Error",JOptionPane.ERROR_MESSAGE);
                    }
                }else{
                    JOptionPane.showMessageDialog(frame,"Veuillez indiquer un fichier xml à utiliser","Erreur pas de fichier xml",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton inserer = new JButton("Insérer");
        inserer.setSize(150,50);
        inserer.setBounds(130,150,110,50);
        inserer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!status.equals("")){
                    Inserer i = new Inserer(status.getText());
                    try {
                        i.inserer(frame);
                        //JOptionPane.showMessageDialog(frame,"Insertion effectuée avec succès","Succès",JOptionPane.INFORMATION_MESSAGE);
                        status.setText("");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(frame,e1,"Exception Error",JOptionPane.ERROR_MESSAGE);
                    }
                }else{
                    JOptionPane.showMessageDialog(frame,"Veuillez indiquer un fichier xml à utiliser","Erreur pas de fichier xml",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton effacer = new JButton("Effacer");
        effacer.setSize(150,50);
        effacer.setBounds(245,150,110,50);
        effacer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!status.equals("")){
                    Effacer ef = new Effacer(status.getText());
                    try {
                        ef.effacer(frame);
                        //JOptionPane.showMessageDialog(frame,"Suppression effectuée avec succès","Succès",JOptionPane.INFORMATION_MESSAGE);
                        status.setText("");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(frame,e1,"Exception Error",JOptionPane.ERROR_MESSAGE);
                    }
                }else{
                    JOptionPane.showMessageDialog(frame,"Veuillez indiquer un fichier xml à utiliser","Erreur pas de fichier xml",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton maj = new JButton("Mettre à jour");
        maj.setSize(150,50);
        maj.setBounds(360,150,110,50);
        maj.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!status.equals("")){
                    Maj m = new Maj(status.getText());
                    try {
                        m.maj(frame);
                        //JOptionPane.showMessageDialog(frame,"Mise à jour effectuée avec succès","Succès",JOptionPane.INFORMATION_MESSAGE);
                        status.setText("");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(frame,e1,"Exception Error",JOptionPane.ERROR_MESSAGE);
                    }
                }else{
                    JOptionPane.showMessageDialog(frame,"Veuillez indiquer un fichier xml à utiliser","Erreur pas de fichier xml",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setLayout(null);
        frame.add(status);
        frame.add(fichier);
        frame.add(rechercher);
        frame.add(inserer);
        frame.add(effacer);
        frame.add(maj);
        frame.add(statusRes);
        frame.add(fichierRes);
        frame.pack();
        frame.setSize(new Dimension(500,400));
        frame.setVisible(true);
    }

    public static void initDatabase() {
        try {
            Class.forName(DB_DRIVER);
            con = DriverManager.getConnection(DB_URL, user, pswd);
            System.out.println("Connexion etablie a la base de donnees");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



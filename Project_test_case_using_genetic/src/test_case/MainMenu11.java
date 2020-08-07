/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test_case;

import java.io.*;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.table.DefaultTableModel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author ms
 */
public class MainMenu11 extends javax.swing.JFrame {

    private static final String START = "1";
  //  private static final String END = "12";

    /**
     * Creates new form MainMenu
     */
    public MainMenu11() {
        initComponents();
        
    }
    static Populasi generateKromosom(int jumlahIndividu,int panjangKromosom){
        Random random = new Random();
        Populasi pop = new Populasi();
        List<Integer> kromosom;
        for (int i = 0; i < jumlahIndividu; i++) {
            kromosom = new ArrayList<>();
            for (int j = 0; j < panjangKromosom; j++) {
                kromosom.add(random.nextInt(10));
            }
            pop.addKromosom(kromosom);
        }
        return pop;
    }
    
        static double dekodeKromosom(List<Integer> kromosom, int rMin, int rMax){
        double x = 0;
        double rumusDekodeBawah = 0;
        double rumusDekodeAtas = 0;
        for (int i = 0; i < kromosom.size(); i++) {
            rumusDekodeBawah = Math.pow(10, -(i+1)) + rumusDekodeBawah;
            rumusDekodeAtas = (kromosom.get(i) * Math.pow(10, -(i+1))) + rumusDekodeAtas;
        }
        rumusDekodeBawah = rumusDekodeBawah * 9;
        x = rMin +(((rMax-rMin) / rumusDekodeBawah)*rumusDekodeAtas);
        return x;
    }

    static double nilaiFitness(double x1,double x2){
        double fitness = -nilaiFungsi(x1,x2);
        return fitness;
    }
    
    static double nilaiFungsi(double x1,double x2){
        double fungsi = ((4 - (2.1 * Math.pow(x1,2)) + (Math.pow(x1,4)/3)) * Math.pow(x1,2)) + (x1 * x2) + ((-4 + (4 * Math.pow(x2,2)))*Math.pow(x2,2));
        return fungsi;
    }
    
    static List<Integer> tournamentSelection(Populasi pop, int panjangTournament){
        List<Integer> idxTemp = new ArrayList<>();
        for (int i = 0; i < pop.getListSize(); i++) {
            idxTemp.add(i);
        }
        
        Collections.shuffle(idxTemp);
        
        List<Integer> idxSample = new ArrayList<>();
        for (int i = 0; i < panjangTournament; i++) {
            idxSample.add(idxTemp.get(i));
        }
        
        int idxKromosom2;
        List<Double> fitnesses = new ArrayList<>();
        List<Integer> kromosomX1 = new ArrayList<>();
        List<Integer> kromosomX2 = new ArrayList<>();
        double x1,x2;
        for (int idxSampleKromosom = 0; idxSampleKromosom < idxSample.size(); idxSampleKromosom++){
            idxKromosom2 = pop.getKromosom(idxSampleKromosom).size()/2;
            kromosomX1.clear();
            kromosomX2.clear();
            for (int idxKromosom1 = 0; idxKromosom1 < pop.getKromosom(idxSampleKromosom).size()/2; idxKromosom1++){
                kromosomX1.add(pop.getKromosom(idxSample.get(idxSampleKromosom)).get(idxKromosom1));
                kromosomX2.add(pop.getKromosom(idxSample.get(idxSampleKromosom)).get(idxKromosom2));
                idxKromosom2++;
            }
            x1 = dekodeKromosom(kromosomX1, rMinX1, rMaxX1);
            x2 = dekodeKromosom(kromosomX2, rMinX2, rMaxX2);
            fitnesses.add(nilaiFitness(x1,x2));
        }
        
        mergedListFitness mergeList = new mergedListFitness(idxSample, fitnesses, panjangTournament);
        mergeList.sort("idxSample");
        
        List<Integer> idxParent = new ArrayList<>();
        idxParent.add(mergeList.getIdxSample(0));
        idxParent.add(mergeList.getIdxSample(1));
        return idxParent;
    }
    
    static List<List<Integer>> crossover(List<Integer> kromosom1, List<Integer> kromosom2, double pC){
        List<List<Integer>> tmpKromosom = new ArrayList<>();
        Random random = new Random();
        double prob = random.nextDouble();
        int point = 0;
        List<Integer> tmpKromosom1 = new ArrayList<>();
        List<Integer> tmpKromosom2 = new ArrayList<>();
        while(point == 0){
            point = random.nextInt(kromosom1.size()-1);
        }
        if(prob<=pC){
            for (int i = 0; i < point; i++){
                tmpKromosom1.add(kromosom1.get(i));
                tmpKromosom2.add(kromosom2.get(i));
            }
            for (int i = point; i < kromosom1.size(); i++){
                tmpKromosom1.add(kromosom2.get(i));
                tmpKromosom2.add(kromosom1.get(i));
            }
            tmpKromosom.add(tmpKromosom1);
            tmpKromosom.add(tmpKromosom2);
            return tmpKromosom;
        }
        tmpKromosom.add(kromosom1);
        tmpKromosom.add(kromosom2);
        return tmpKromosom;
    }
    
    static List<Integer> mutasi(List<Integer> kromosom, double pM, int maxNilaiKromosom){
        Random random = new Random();
        double prob;
        int tmp;
        List<Integer> kromosomBaru = new ArrayList<>();
        for (int i = 0; i < kromosom.size(); i++) {
            prob = random.nextDouble();
            if(prob <= pM){
                tmp = kromosom.get(i);
                while(tmp == kromosom.get(i)){
                    tmp = random.nextInt(maxNilaiKromosom);
                }
                kromosomBaru.add(tmp);
            }else{
                kromosomBaru.add(kromosom.get(i));
            }
        }
        return kromosomBaru;
    }
    
    static List<Integer> steadyState(int jumlahGenerasi, Populasi populasi, int jumlahIndividu, int panjangTournament){
        List<List<Integer>> gabungan = new ArrayList<>();
        List<List<Integer>> child = new ArrayList<>();
        List<List<Integer>> tmpPopulasi = new ArrayList<>();
        List<List<Integer>> tmpAnak = new ArrayList<>();
        List<Double> fitnesses = new ArrayList<>();
        List<Integer> idxParent = new ArrayList<>();
        List<Integer> tmpAnak1 = new ArrayList<>();
        List<Integer> tmpAnak2 = new ArrayList<>();
        List<Integer> anak1 = new ArrayList<>();
        List<Integer> anak2 = new ArrayList<>();
        Populasi pop = new Populasi();
        
        mergedListFitness mergeList;
        
        double x1,x2,nilaiFitness;
        int t, jumlah;
        for (int i = 0; i < jumlahGenerasi; i++) {
            child.clear();
            for (int j = 0; j < populasi.getListSize()/2; j++){
                //Seleksi parent
                idxParent.clear();
                idxParent = tournamentSelection(populasi, panjangTournament);
                anak1 = populasi.getKromosom(idxParent.get(0));
                anak2 = populasi.getKromosom(idxParent.get(1));
                
                //Crossover
                tmpAnak.clear();
                tmpAnak = crossover(anak1, anak2, pC);
                anak1 = tmpAnak.get(0);
                anak2 = tmpAnak.get(1);
                
                //Mutasi
                tmpAnak1 = mutasi(anak1, pM, maxNilaiKromosom);
                tmpAnak2 = mutasi(anak2, pM, maxNilaiKromosom);
                anak1 = tmpAnak1;
                anak2 = tmpAnak2;
                
                child.add(anak1);
                child.add(anak2);
            }
            gabungan.clear();
            gabungan.addAll(populasi.getKromosomList());
            gabungan.addAll(child);
            
            fitnesses.clear();
            for (int j = 0; j < gabungan.size(); j++) {
                x1 = dekodeKromosom(gabungan.get(j).subList(0, gabungan.get(j).size()/2), rMinX1, rMaxX1);
                x2 = dekodeKromosom(gabungan.get(j).subList(gabungan.get(j).size()/2, gabungan.get(j).size()), rMinX2, rMaxX2);
                fitnesses.add(nilaiFitness(x1, x2));
            }
            mergeList = new mergedListFitness(gabungan, fitnesses, gabungan.size(), "gabungan");
            mergeList.sort("gabungan");
            
            pop.getKromosomList().clear();
            tmpPopulasi.clear();
            t = 0;
            jumlah = 0;
            
            for (int j = 0; j < mergeList.getGabungan().size(); j++){
                if(j == 0){
                    pop.addKromosom(mergeList.getGabungan(j));
                    jumlah++;
                }else{
                    x1 = dekodeKromosom(mergeList.getGabungan(j).subList(0, mergeList.getGabungan(j).size()/2), rMinX1, rMaxX1);
                    x2 = dekodeKromosom(mergeList.getGabungan(j).subList(mergeList.getGabungan(j).size()/2, mergeList.getGabungan(j).size()), rMinX2, rMaxX2);
                    nilaiFitness = nilaiFitness(x1, x2);
                    if(nilaiFitness == mergeList.getFitness(jumlah-1)){
                        tmpPopulasi.add(gabungan.get(j));
                        t++;
                    }else{
                        pop.addKromosom(mergeList.getGabungan(j));
                        jumlah++;
                    }
                }
                if(jumlah==jumlahIndividu){
                    break;
                }
            }
            
            int z = 0;
            while(jumlah < jumlahIndividu){
                pop.addKromosom(tmpPopulasi.get(z));
                z++;
                jumlah++;
            }
            
            populasi.getKromosomList().clear();
            populasi.getKromosomList().addAll(pop.getKromosomList());
        }
        return populasi.getKromosom(0);
    }
    
    static int jumlahGenerasi = 100;
    static int jumlahIndividu = 30;
    static int panjangKromosom = 6;
    static int rMinX1 = -3;
    static int rMaxX1 = 3;
    static int rMinX2 = -2;
    static int rMaxX2 = 2;
    static int panjangTournament = jumlahIndividu/2;
    static double pC = 0.70;
    static double pM = 0.01;
    static int maxNilaiKromosom = 10;
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        BtGetFile = new javax.swing.JButton();
        BtGenPath = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        TextAreaGetFile1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        TextAreaGeneratePath = new javax.swing.JTextArea();
        TxtGetFile = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable1);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane5.setViewportView(jTextArea2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Generate Test Path From Sequence Diagram Model");

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel1.setText("Pembangkit Test Path Menggunakan Sequence Diagram");

        BtGetFile.setText("Get File");
        BtGetFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtGetFileActionPerformed(evt);
            }
        });

        BtGenPath.setText("Generate Path");
        BtGenPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtGenPathActionPerformed(evt);
            }
        });

        TextAreaGetFile1.setColumns(20);
        TextAreaGetFile1.setRows(5);
        jScrollPane1.setViewportView(TextAreaGetFile1);

        TextAreaGeneratePath.setColumns(20);
        TextAreaGeneratePath.setRows(5);
        jScrollPane2.setViewportView(TextAreaGeneratePath);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane4.setViewportView(jTextArea1);

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel2.setText("Sequence Dependency Table kromosom bawaan perent");

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel3.setText("Node Sequence Dependency kromosom yang akan di crossover");

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel4.setText("Test Path Result berdasarkan kromosom terbaik pada populasi baru");

        jButton1.setText("Clear");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(BtGetFile)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(TxtGetFile))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addGap(18, 18, 18)
                                    .addComponent(BtGenPath))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(10, 10, 10)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane4))
                                    .addGap(10, 10, 10)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(231, 231, 231)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel2)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtGetFile)
                    .addComponent(BtGenPath)
                    .addComponent(TxtGetFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtGetFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtGetFileActionPerformed
        // TODO add your handling code here:
        String text = null;
        JFileChooser OpenFile = new JFileChooser();
        FileNameExtensionFilter mdlFilter = new FileNameExtensionFilter(".xml", "xml", "XML");
        OpenFile.addChoosableFileFilter(mdlFilter);
        OpenFile.showOpenDialog(null);
        File sampleFile = OpenFile.getSelectedFile();//mengambil file
        String NamaFile = sampleFile.getAbsolutePath();
        TxtGetFile.setText(NamaFile);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(sampleFile);
            List<Relation> Relations = new ArrayList<Relation>();
            //   NodeList personList = doc.getElementsByTagName("symbol");
            NodeList personList = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < personList.getLength(); i++) {
                Node p = personList.item(i);
                if (p.getNodeType() == Node.ELEMENT_NODE) {
                    Element person = (Element) p;
                    // Get the value of the ID attribute.

                    String id = person.getAttributes().getNamedItem("id").getNodeValue();
                    // Get the value of all sub-elements.
                    String activityname = person.getElementsByTagName("objectmessage")
                            .item(0).getChildNodes().item(0).getNodeValue();
                    String sequence = person.getElementsByTagName("sequence").item(0)
                            .getChildNodes().item(0).getNodeValue();
                    String ordinal = person.getElementsByTagName("ordinal").item(0)
                            .getChildNodes().item(0).getNodeValue();
                    Relations.add(new Relation(id, activityname, sequence, ordinal));

                }
            }

            for (Relation empl : Relations) {
                TextAreaGetFile1.append(empl.toString() + "\n");
                TextAreaGetFile1.setEditable(false);
                System.out.println(empl.toString());
                jTextArea1.append(empl.toString2() + "\n");

            }

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_BtGetFileActionPerformed

    private void BtGenPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtGenPathActionPerformed
        // TODO add your handling code here:
        Genetika gen = new Genetika();
        
        Populasi pop = new Populasi();
        List<Integer> kromosomUnggul = new ArrayList<>();
        pop = generateKromosom(jumlahIndividu, panjangKromosom);
        kromosomUnggul = steadyState(jumlahGenerasi, pop, jumlahIndividu, panjangTournament);
        double x1 = dekodeKromosom(kromosomUnggul.subList(0, kromosomUnggul.size()/2), rMinX1, rMaxX1);
        double x2 = dekodeKromosom(kromosomUnggul.subList(kromosomUnggul.size()/2, kromosomUnggul.size()), rMinX2, rMaxX2);
        String fileSDT = jTextArea1.getText();
        String[] s = jTextArea1.getText().split("\n");
        String st = "";
        System.out.println("s" + s.length);

        for (int i = 1; i < s.length; i++) {
            System.out.println("generation :" + s[i]);
            TextAreaGeneratePath.setText("Hasil cross over : "+s[i]);
            st = s[i];
            String[] arrayTeksSplit = st.split("\t");
            System.out.println("Hasil cross over : "+s[i]);
            TextAreaGeneratePath.setText("\n"+"Hasil cross over : "+s[i]);
            for (int j = 0; j < arrayTeksSplit.length; j++) {
                String data1 = arrayTeksSplit[1];
                String data2 = arrayTeksSplit[2];
                String[] data1a = data1.split(" ");
                String[] data2b = data2.split("\"");
                for (int a = 0; a < data1a.length; a++) {
                    for (int b = 0; b < data2b.length; b++) {
                        String data1aa = data1a[0];
                        String data2bb = data2b[1];

                        if (data1aa.length() != 1) {
                            String[] data1aaa = data1a[0].split(",");
                            for (int c = 0; c < data1aaa.length; c++) {
                                String data1mod = data1aaa[c];
                                System.out.println("kromosom terbaik="+data1mod);
                                gen.addEdge(data1mod, data2bb);
                                TextAreaGeneratePath.setText("Hasil cross over :"+s[i]+"\n"+"Kromosom Terbaik :"+data1aa+"\n"+"Track Node"
                                        + " Populasi :"+"\n");
                            }
                        } else {
                            gen.addEdge(data1aa, data2bb);
                        }
                        break;
                    }
                }
                break;
            }

        }
        System.out.println("Nilai fitness "+nilaiFitness(x1,x2));
        System.out.println("Nilai fungsi : "+nilaiFungsi(x1,x2));
        LinkedList<String> visited = new LinkedList();
        // System.out.println(""+visited);

        visited.add(START);

        depthFirst(gen, visited);

        printPath(visited);
        gen.printnodegen();
                try{
FileWriter fileWriter = new FileWriter(sampleFile);
}
catch(IOException ex){
ex.printStackTrace();
}
    }//GEN-LAST:event_BtGenPathActionPerformed
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        jTextArea1.setText("");
        TextAreaGetFile1.setText("");
        TextAreaGeneratePath.setText("");
        TxtGetFile.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed

    public void depthFirst(Genetika gen, LinkedList<String> visited) {
        LinkedList<String> nodes = gen.adjacentNodes(visited.getLast());
         
        String fileSDT = jTextArea1.getText();
        String[] s = jTextArea1.getText().split("\n");
        //System.out.println("s" + s.length);
        String END= String.valueOf(s.length);
        
        // examine adjacent nodes
        for (String node : nodes) {
            if (visited.contains(node)) {
                continue;
            }
            if (node.equals(END)) {
                visited.add(node);
                printPath(visited);
                visited.removeLast();
                break;
            }
        }
        for (String node : nodes) {
            if (visited.contains(node) || node.equals(END)) {
                continue;
            }
            visited.addLast(node);
            depthFirst(gen, visited);
            visited.removeLast();
        }
    }

    public void printPath(LinkedList<String> visited) {
      //  Graph grap=new Graph();
        for (String node : visited) {
            System.out.print(node);
            System.out.print(" ");
            TextAreaGeneratePath.append(node+" ");
          //  jTextAreaPrintMap.append(grap.printMap);
        }
        TextAreaGeneratePath.append("\n");
        System.out.println();
    }
File sampleFile = new File("sample.txt");

String text = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;


                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainMenu11.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainMenu11.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainMenu11.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainMenu11.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainMenu11().setVisible(true);

            }
        });

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtGenPath;
    private javax.swing.JButton BtGetFile;
    private javax.swing.JTextArea TextAreaGeneratePath;
    private javax.swing.JTextArea TextAreaGetFile1;
    private javax.swing.JTextField TxtGetFile;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables
}

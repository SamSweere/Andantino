package game;

import java.io.*;

public class fileWriter {
    private final String path = "Performance/";
    private final String fileName;
    private final String filePath;
    private File fout;
    private BufferedWriter bw;

    String header= "depth,sec,nodes";

    //This class opens and closes the file after each write, such that the program can be quit anytime
    public fileWriter(String fileName){
        this.fileName = fileName;
        this.filePath = path + fileName + ".csv";
        this.fout = new File(filePath);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(fout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



        bw = new BufferedWriter(new OutputStreamWriter(fos));
        try {
            bw.write(header);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLine(int depth, float time, int nodes) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder currentContents = new StringBuilder();
        String st  = "";
        try {
            st = br.readLine();
            while (st != null){
                currentContents.append(st + "\n");
                st = br.readLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //Open new writer
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(fout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bw = new BufferedWriter(new OutputStreamWriter(fos));


        String strLine = depth + "," + time + "," + nodes;
        try {
            bw.write(currentContents.toString());
            bw.write(strLine);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

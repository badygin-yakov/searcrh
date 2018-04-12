import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.lang.*;

public class CosineSimilarity {


    public ArrayList<CosSimRate> cosineSimilarity(String searchLine) {
        ArrayList<Double> cosineSimilarity = new ArrayList<Double>();
        ArrayList<CosSimRate> sortResult = new ArrayList<CosSimRate>();

        String[] searchWords = searchLine.split(" & ");
        ArrayList<String> searchWordsList = new ArrayList<String>();
        ArrayList<Integer> searchWordsCount = new ArrayList<Integer>();

        for (int i = 0; i < searchWords.length; i++) {
            searchWords[i] = searchWords[i].trim();
            searchWordsList.add(searchWords[i].trim());
        }

        for (int i = 0; i < searchWordsList.size(); i++) {
            int count = 1;
            searchWordsCount.add(count);
            for (int k = i + 1; k < searchWordsList.size(); k++) {
                if (searchWordsList.get(i).equals(searchWordsList.get(k))) {
                    searchWordsList.remove(k);
                    searchWordsCount.remove(searchWordsCount.size() - 1);
                    count++;
                    searchWordsCount.add(count);
                }
            }
        }

        //

        ArrayList<Double> queryIdf = tfIdfCounter(searchWordsList);


        ArrayList<WordRate> wordsRateList = tfIdfCount(searchWordsList);
        double queryWeight = 0.0;
        int maxDivision = 1;
        for (int i=0;i<searchWordsCount.size();i++){
            maxDivision = maxDivision<searchWordsCount.get(i) ? searchWordsCount.get(i):maxDivision;
        }

        //веса по документам
        System.out.println(queryIdf.size());
        for (int i=1;i<queryIdf.size();i++) {
            System.out.println(queryIdf.get(i).toString());
            queryWeight += Math.pow(queryIdf.get(i), 2);
        }

        //пересчитать по TfIdf
        queryWeight = Math.sqrt(queryWeight);


        ArrayList<Double> lengthByDoc = new ArrayList<Double>();
        for (int i=0;i<wordsRateList.get(0).getrateList().size();i++) {
            double d = 0;
            for (int j=0;j<wordsRateList.size();j++){
                d += Math.pow(wordsRateList.get(j).getrateList().get(i),2);
            }
            lengthByDoc.add(Math.sqrt(d));
        }

        ArrayList<ArrayList<Double>> rateByPage = getRateByDoc();

        for (int i=0;i<lengthByDoc.size();i++){
            cosineSimilarity.add(cosSim(rateByPage.get(i),queryWeight));
        }


        sortResult = sortResult(cosineSimilarity);

        for (int i=0; i<sortResult.size();i++ ){
//            System.out.println("rate:"+sortResult.get(i).getRate()+" index:"+sortResult.get(i).getPageIndex()+" "+sortResult.get(i).getPageRef());
        }
        return sortResult;
    }


    public ArrayList<Double> tfIdfCounter(ArrayList<String> searchWordsList){
        String csvFile = "tfIdf2.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        ArrayList<Double> queryTfidf = new ArrayList<Double>();



        try {
            br = new BufferedReader(new FileReader(csvFile));
            ArrayList<String[]> value = new ArrayList<String[]>();
            String [] header = new String[1];


            line = br.readLine();
            if (line != null) {
                header = line.split(cvsSplitBy);
            }


            while((line = br.readLine())!=null){
                value.add(line.split(cvsSplitBy));
            }


            for (int i = 0; i < searchWordsList.size(); i++) {
                for (int k = 0; k < header.length; k++) {
                    if (searchWordsList.get(i).equals(header[k])) {
                        double d = 0.0;
                        try {
                            if (value.get().equals("Infinity")) {
                                queryTfidf.add(d);
                            } else {
                                d = Double.parseDouble(tfIdfValue[1][k]);
                                queryTfidf.add(d);
                            }
                        } catch (NumberFormatException e) {
                            d = 0.0;
                        }
                    }

                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return queryTfidf;
    }

    public ArrayList<CosSimRate> sortResult(ArrayList<Double> sortByIndex){
        ArrayList<CosSimRate> sort = new ArrayList<CosSimRate>();
        ArrayList<String> refPage = getPageIndex();
        for (int i=0; i<refPage.size();i++ ){
            sort.add(new CosSimRate(refPage.get(i),(i+1),sortByIndex.get(i)));
        }

        Collections.sort(sort, new Comparator<CosSimRate>() {
            public int compare(CosSimRate o1, CosSimRate o2) {
                if (o1.getRate() > o2.getRate()) return -1;
                if (o1.getRate() < o2.getRate()) return 1;
                return 0;
            }
        });
        return sort;
    }


    public ArrayList<String> getPageIndex() {
        ArrayList<String> searchResult = new ArrayList<String>();
        try {
            Scanner sc = null;
            String path = "/Users/aydar/work/GogolSearch/visitedPage/index.txt";
            sc = new Scanner(new File(path));
            while (sc.hasNextLine()) {
                String checkStr = sc.nextLine();
                checkStr = checkStr.substring(checkStr.indexOf(':') + 1);
                searchResult.add(checkStr);
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }


    public double cosSim(ArrayList<Double> docRate, double q){
        double sumRate = 0.0;
        for (int i=0; i<docRate.size();i++){
            sumRate+= Math.pow(docRate.get(i),2);
        }
        return sumRate/q;
    }


    public ArrayList<ArrayList<Double>> getRateByDoc() {
        ArrayList<ArrayList<Double>> WordsByPage = new ArrayList<ArrayList<Double>>();
        String csvFile = "tfIdf.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        Scanner sc = null;
        ArrayList<Double> bufList = new ArrayList<Double>();
        String[] bufAr = new String[0];
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line=br.readLine())!=null) {
                double d = 0.0;
                bufAr = line.split(cvsSplitBy);
                try {
                    for (int i=1;i<bufAr.length;i++) {
                        if (bufAr[i].equals("NaN")) {
                            bufList.add(d);
                        } else {
                            d = Double.parseDouble(bufAr[i]);
                            bufList.add(d);
                        }
                    }
                } catch (NumberFormatException e) {
                    d = 0.0;
                }
                WordsByPage.add(bufList);
                bufList = new ArrayList<Double>();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        WordsByPage.remove(0);
        return WordsByPage;
    }


    public ArrayList<WordRate> tfIdfCount(ArrayList<String> words) {
        ArrayList<WordRate> result = new ArrayList<WordRate>();
        String path = "tfIdf.csv";
        Scanner sc = null;
        String[] header = new String[0];
        ArrayList<Integer> rateIndex = new ArrayList<Integer>();
        String[] strIndex = new String[0];
        try {
            sc = new Scanner(new File(path));
            String buf = null;
            if (sc.hasNextLine()) {
                buf = sc.nextLine();
                header = buf.split(";");
                for (int i=0; i < header.length; i++) {
                    for (int k = 0; k < words.size(); k++){
                        header[i] = header[i].trim();
                        if(header[i].equals(words.get(k))){
                            rateIndex.add(i);
                            break;
                        }
                    }
                }
            }
            for (int i=0; i<words.size();i++) {
                result.add(new WordRate(words.get(i), new ArrayList<Integer>()));
            }
            while(sc.hasNextLine()){
                buf = sc.nextLine();
                strIndex = buf.split(";");
                ArrayList<Double> rateList = new ArrayList<Double>();

                for (int k = 0; k < rateIndex.size(); k++) {
                    double d = 0.0;
                    try {
                        if (strIndex[rateIndex.get(k)].equals("NaN")) {
                            result.get(k).getrateList().add(d);
                        } else {
                            d = Double.parseDouble(strIndex[rateIndex.get(k)]);
                            result.get(k).getrateList().add(d);
                        }
                    } catch (NumberFormatException e) {
                        d = 0.0;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}

class CosSimRate{
    private double rate;
    private int pageIndex;
    private String pageRef;
    public CosSimRate(String pageRef, int index, double rate){
        this.rate = rate;
        this.pageIndex = index;
        this.pageRef = pageRef;
    }
    public double getRate() {
        return rate;
    }
    public int getPageIndex(){
        return pageIndex;
    }

    public String getPageRef() {
        return pageRef;
    }
}


class WordRate {
    private String word;
    private ArrayList<Double> rateList = new ArrayList<Double>();

    private ArrayList<Double> lengthList = new ArrayList<Double>();

    public WordRate(String str, ArrayList list) {
        word = str;
        rateList = list;
    }

    public String getWord() {
        return word;
    }

    public ArrayList<Double> getrateList() {
        return rateList;
    }

    public void setLengthList(ArrayList<Double> lengthList) {
        this.lengthList = lengthList;
    }
}
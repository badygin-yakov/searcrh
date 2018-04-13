import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.lang.*;

public class Search {

    public ArrayList<CosSimRate> getSearchResult(String searchLine) {
        ArrayList<Double> cosineSimilarity = new ArrayList<Double>();
        ArrayList<CosSimRate> sortResult = new ArrayList<CosSimRate>();

        //Porter rtpoer = new Porter();
        String[] searchWords = searchLine.split(" ");
        ArrayList<String> searchWordsList = new ArrayList<String>();
        ArrayList<Integer> searchWordsCount = new ArrayList<Integer>();

        for (int i = 0; i < searchWords.length; i++) {
            searchWordsList.add(searchWords[i].trim());
            System.out.print(searchWords[i] + " ");
        }
        System.out.println();

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

        System.out.println(searchWordsList.size());

        ArrayList<ArrayList<Double>> queryValueRate = tfIdfCounter(searchWordsList);
        int maxDivision = 1;
        for (int i = 0; i < searchWordsCount.size(); i++) {
            maxDivision = maxDivision < searchWordsCount.get(i) ? searchWordsCount.get(i) : maxDivision;
        }

        //вес по всем документам по словам из поиска
        ArrayList<Double> queryRateByDoc = new ArrayList<>();
        for (int k = 0; k < queryValueRate.get(0).size(); k++) {
            double d = 0.0;
            for (int i = 0; i < queryValueRate.size(); i++) {
                d += Math.pow(queryValueRate.get(i).get(k), 2);
            }
            queryRateByDoc.add(Math.sqrt(d));
        }

        if (maxDivision > 1) {
            for (ArrayList<Double> divList : queryValueRate) {
                for (int i = 0; i < divList.size(); i++) {
                    double d = divList.get(i) / maxDivision;
                    divList.set(i, d);
                }
            }
        }

        //расчетные вес запроса
        ArrayList<Double> queryRate = new ArrayList<>();
        for (int k = 0; k < queryValueRate.get(0).size(); k++) {
            double d = 0.0;
            for (int i = 0; i < queryValueRate.size(); i++) {
                d += Math.pow(queryValueRate.get(i).get(k), 2);
            }
            queryRate.add(Math.sqrt(d));
        }
        //расчетные веса для всех слов по документам
        ArrayList<ArrayList<Double>> rateByPage = getTfIdf();
        ArrayList<Double> tfIdfRate = new ArrayList<>();
        for (ArrayList<Double> page : rateByPage) {
            double d = 0.0;
            for (int i = 0; i < page.size(); i++) {
                d += Math.pow(page.get(i), 2);
            }
            tfIdfRate.add(Math.sqrt(d));
        }
        for (int i = 0; i < queryRate.size(); i++) {
            double d = cosSim(rateByPage.get(i), queryRateByDoc.get(i) * tfIdfRate.get(i));
            cosineSimilarity.add(d);
        }

        sortResult = sortResult(cosineSimilarity);

        boolean flag = true;
        for (int i = 0; i < sortResult.size(); i++) {
            if (sortResult.get(i).getRate() > 0.0) {
                System.out.println("rate:" + sortResult.get(i).getRate() + " page:" + sortResult.get(i).getPageIndex() + " " + sortResult.get(i).getPageRef());
                flag = false;
            }
        }
        if (flag) {
            System.out.println("Поиск не дал результата!");
        }

        return sortResult;
    }

    public ArrayList<ArrayList<Double>> getTfIdf() {
        String csvFile = Settings.TFIDF_FILE;
        BufferedReader br;
        String line = "";
        String cvsSplitBy = ";";
        ArrayList<ArrayList<String>> value = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<Double>> doubleValue = new ArrayList<ArrayList<Double>>();

        try {
            //чтение и запись в ArrayList  tfIdf
            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                line = line.substring(line.indexOf(';') + 1);
                ArrayList<String> temp = new ArrayList(Arrays.asList(line.split(cvsSplitBy)));
                value.add(temp);
            }
            for (ArrayList<String> toDouble : value) {
                ArrayList<Double> temp = new ArrayList<Double>();
                for (int i = 0; i < toDouble.size(); i++)  {
                    if(!toDouble.get(i).equals("\"")) {
                        toDouble.set(i,toDouble.get(i).substring(1,toDouble.get(i).length()-1));
                    }
                    else {
                        toDouble.remove(i);
                    }
                }
                for (int i = 0; i < toDouble.size(); i++) {
                    double d = Double.parseDouble(toDouble.get(i));
                    d = checkDoubleValue(d);
                    temp.add(d);
                }
                doubleValue.add(temp);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doubleValue;
    }

    public double checkDoubleValue(double doubleValue){
        if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
            doubleValue = 0.0;
        }
        return doubleValue;
    }

    public ArrayList<ArrayList<Double>> tfIdfCounter(ArrayList<String> searchWordsList) {
        String csvFile = Settings.TFIDF_FILE;
        BufferedReader bufferedReader;
        String line;
        String cvsSplitBy = ";";
        ArrayList<ArrayList<String>> value = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<Double>> queryValue = new ArrayList<ArrayList<Double>>();

        try {
            //чтение и запись в ArrayList  tfIdf
            bufferedReader = new BufferedReader(new FileReader(csvFile));
            ArrayList<String> header = new ArrayList(Arrays.asList(new String[1]));
            line = bufferedReader.readLine();
            if (line != null) {
                header = new ArrayList(Arrays.asList(line.split(cvsSplitBy)));
                header.remove(0);
            }
            for (int i = 0; i < header.size(); i++)  {
                header.set(i,header.get(i).substring(2,header.get(i).length()-1));
            }
            while ((line = bufferedReader.readLine()) != null) {
                line = line.substring(line.indexOf(';') + 1);
                ArrayList<String> temp = new ArrayList(Arrays.asList(line.split(cvsSplitBy)));
                for (int i = 0; i < temp.size(); i++)  {
                    if(!temp.get(i).equals("\"")) {
                        temp.set(i,temp.get(i).substring(1,temp.get(i).length()-1));
                    }

                }
                value.add(temp);
            }

            for (int i = 0; i < searchWordsList.size(); i++) {
                //индек исковых слов

                // ТУТ КАКАЯ-ТО ПРОБЛЕМА!
                int index = header.indexOf(searchWordsList.get(i));

                //расчет tfIdf искомых слов
                ArrayList<Double> queryValueLine = new ArrayList<Double>();
                for (ArrayList<String> pageValue : value) {
                    double d = Double.parseDouble(pageValue.get(index+1));
                    d = checkDoubleValue(d);
                    queryValueLine.add(d);
                }
                queryValue.add(queryValueLine);
            }

//            for (int i = 0; i < searchWordsList.size(); i++) {
//                for (int k = 0; k < tfIdfValue[0].length; k++) {
//                    tfIdfValue[0][k] = tfIdfValue[0][k].trim();
//                    if (searchWordsList.get(i).equals(tfIdfValue[0][k])) {
//                        double d = 0.0;
//                        try {
//                            if (tfIdfValue[1][k].equals("Infinity")) {
//                                queryTfidf.add(d);
//                            } else {
//                                d = Double.parseDouble(tfIdfValue[1][k]);
//                                queryTfidf.add(d);
//                            }
//                        } catch (NumberFormatException e) {
//                            d = 0.0;
//                        }
//                    }
//
//                }
//            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queryValue;
    }

    public ArrayList<CosSimRate> sortResult(ArrayList<Double> sortByIndex) {
        ArrayList<CosSimRate> sort = new ArrayList<CosSimRate>();
        ArrayList<String> refPage = getPageIndex();
        for (int i = 0; i < refPage.size(); i++) {
            sort.add(new CosSimRate(refPage.get(i), (i + 1), sortByIndex.get(i)));
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
            String path = Settings.BASE_PATH + Settings.INDEX_FILE;
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


    public double cosSim(ArrayList<Double> docRate, double q) {
        double sumRate = 0.0;
        for (int i = 0; i < docRate.size(); i++) {
            sumRate += Math.pow(docRate.get(i), 2);
        }

        q = BigDecimal.valueOf(q).setScale(6, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        sumRate = BigDecimal.valueOf(sumRate).setScale(6, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        double d = checkDoubleValue(sumRate / q);
//        System.out.println(d +" "+sumRate+"/"+q);
        return d;
    }


    public ArrayList<ArrayList<Double>> getRateByDoc() {
        ArrayList<ArrayList<Double>> WordsByPage = new ArrayList<ArrayList<Double>>();
        String csvFile = Settings.TFIDF_FILE;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        Scanner sc = null;
        ArrayList<Double> bufList = new ArrayList<Double>();
        String[] bufAr = new String[0];
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                double d = 0.0;
                bufAr = line.split(cvsSplitBy);
                try {
                    for (int i = 1; i < bufAr.length; i++) {
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
        String path = Settings.TFIDF_FILE;
        Scanner sc;
        String[] header;
        ArrayList<Integer> rateIndex = new ArrayList<Integer>();
        String[] strIndex;
        try {
            sc = new Scanner(new File(path));
            String buf;
            if (sc.hasNextLine()) {
                buf = sc.nextLine();
                header = buf.split(";");
                for (int i = 0; i < header.length; i++) {
                    for (int k = 0; k < words.size(); k++) {
                        header[i] = header[i].trim();
                        if (header[i].equals(words.get(k))) {
                            rateIndex.add(i);
                            break;
                        }
                    }
                }
            }
            for (int i = 0; i < words.size(); i++) {
                result.add(new WordRate(words.get(i), new ArrayList<Integer>()));
            }
            while (sc.hasNextLine()) {
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

class CosSimRate {
    private double rate;
    private int pageIndex;
    private String pageRef;

    public CosSimRate(String pageRef, int index, double rate) {
        this.rate = rate;
        this.pageIndex = index;
        this.pageRef = pageRef;
    }

    public double getRate() {
        return rate;
    }

    public int getPageIndex() {
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
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import au.com.bytecode.opencsv.CSVWriter;

public class TFIDFCalculator {

    public void tfIdfInit() {
        ArrayList<ArrayList<String>> WordsByPage = new ArrayList<ArrayList<String>>();
        for (int i = 1; i < 101; i++) {
            ArrayList<String> bufList = new ArrayList<>();
            Scanner sc = null;
            String path = Settings.BASE_PATH + i + ".txt";
            try {
                sc = new Scanner(new File(path));
                while (sc.hasNextLine()) {
                    bufList.add(sc.nextLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            WordsByPage.add(bufList);
        }


        String path = Settings.BASE_PATH + Settings.UNIQUEWORDS_FILE;
        Scanner sc = null;
        ArrayList<Integer> wordIndex;
        ArrayList<String> allUniqWords = new ArrayList<String>();
        allUniqWords.add("");
        ArrayList<Reference> wordRef = new ArrayList<Reference>();
        try {
            sc = new Scanner(new File(path));
            String buf = null;
            while (sc.hasNextLine()) {
                wordIndex = new ArrayList<Integer>();
                buf = sc.nextLine();
                String word = buf.substring(0, buf.indexOf("[") - 1);
                buf = buf.substring(buf.indexOf("[") + 1, buf.length() - 1);
                String[] strIndex = buf.split(", ");
                for (int k = 0; k < strIndex.length; k++) {
                    wordIndex.add(Integer.parseInt(strIndex[k]));
                }
                wordRef.add(new Reference(word, wordIndex));
                allUniqWords.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        int pageIndex = 0;
        String tfIdf = Settings.TFIDF_FILE;
        CSVWriter writerTfIdf;

        String[] header = allUniqWords.toArray(new String[allUniqWords.size()]);
        String result = allUniqWords.toString();
        result = result.substring(1, result.length() - 1);
        System.out.println(result);
        header = result.split(",");
        try {
            Writer writerTfIdfSM = new OutputStreamWriter(new FileOutputStream(tfIdf, true), "UTF-8");
            writerTfIdf = new CSVWriter(writerTfIdfSM, ';');
            writerTfIdf.writeNext(header);
            writerTfIdf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            Writer writerTfIdfSM = new OutputStreamWriter(new FileOutputStream(tfIdf, true), "UTF-8");
            writerTfIdf = new CSVWriter(writerTfIdfSM, ';');
            for (ArrayList<String> wordsListByPage : WordsByPage) {
                pageIndex++;
                System.out.println(pageIndex);
                ArrayList<String> recordTfIdf = new ArrayList<>();
                recordTfIdf.add(String.valueOf(pageIndex));
                for (int i = 0; i < allUniqWords.size(); i++) {
                    double tfIdfValue = tfIdf(wordsListByPage, WordsByPage, allUniqWords.get(i));
                    if (tfIdfValue == Double.NaN) {
                        recordTfIdf.add("");
                    } else {
                        recordTfIdf.add(String.valueOf(tfIdfValue));
                    }
                }
                writerTfIdf.writeNext(recordTfIdf.toArray(new String[recordTfIdf.size()]));
            }
            writerTfIdf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Reference {
        private String word;
        private ArrayList<Integer> wordList = new ArrayList<Integer>();

        public Reference(String str, ArrayList list) {
            word = str;
            wordList = list;
        }

        public String getWord() {
            return word;
        }

        public ArrayList<Integer> getWordList() {
            return wordList;
        }
    }


    /**
     * @param words  list of strings
     * @param term String represents a term
     * @return term frequency of term in document
     */
    public double tf(ArrayList<String> words, String term) {
        double result = 0;
        for (String word : words) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / words.size();
    }

    /**
     * @param docs list of list of strings represents the dataset
     * @param term String represents a term
     * @return the inverse term frequency of term in documents
     */
    public double idf(ArrayList<ArrayList<String>> docs, String term) {
        double n = 0;
        for (ArrayList<String> words : docs) {
            for (String word : words) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        return Math.log(docs.size() / n);
    }

    /**
     * @param doc  a text document
     * @param docs all documents
     * @param term term
     * @return the TF-IDF of term
     */
    public double tfIdf(ArrayList<String> doc, ArrayList<ArrayList<String>> docs, String term) {
        return tf(doc, term) * idf(docs, term);
    }


}
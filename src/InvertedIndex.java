import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvertedIndex {
    class Inverted {
        private String word;
        private ArrayList<Integer> wordList = new ArrayList<Integer>();

        public Inverted(String str, ArrayList list) {
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

    public void getInvertedIndex() {
        int i = 1;
        String path;
        Scanner sc = null;
        ArrayList<String> uniqWord = new ArrayList<String>();
        ArrayList<Inverted> invertedList = new ArrayList<Inverted>();
        while (i <= 100) {
            if (i == 87) {
                i++;
                continue;
            }
            path = Settings.BASE_PATH + i + ".txt";
            i++;
            try {
                sc = new Scanner(new File(path));
                String checkStr;
                while (sc.hasNextLine()) {
                    checkStr = sc.nextLine();
                    if (uniqWord.indexOf(checkStr) == -1) {
                        uniqWord.add(checkStr);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(uniqWord.size());
        i = 1;
        ArrayList<Integer> wordIndex = new ArrayList<Integer>();
        for (int j = 0; j < uniqWord.size(); j++) {
            while (i <= 100) {
                if (i == 87) {
                    i++;
                    continue;
                }
                path = Settings.BASE_PATH + i + ".txt";
                try {
                    sc = new Scanner(new File(path));
                    String checkStr;
                    while (sc.hasNextLine()) {
                        checkStr = sc.nextLine();
                        String temp = uniqWord.get(j);
                        if (temp.equals(checkStr)) {
                            wordIndex.add(i);
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i++;
            }
            System.out.println(uniqWord.get(j) + " " + wordIndex.toString());
            System.out.println("Осталось " + (uniqWord.size() - j));
            invertedList.add(new Inverted(uniqWord.get(j), wordIndex));
            wordIndex = new ArrayList<Integer>();
            i = 1;
        }
        try (FileWriter writer = new FileWriter(Settings.BASE_PATH + "uniqWords1.txt", false)) {
            String content = "";
            for (int j = 0; j < uniqWord.size(); j++) {
                content = invertedList.get(j).getWord() + " " + invertedList.get(j).getWordList().toString();
                writer.write(content);
                writer.append('\n');
                writer.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

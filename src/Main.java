import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

//        InvertedList inverted = new InvertedList();
//        inverted.getInvertedList();
//
//        TFIDFCalculator tfIdf = new TFIDFCalculator();
//        tfIdf.tfIdfInit();

        CosineSimilarity cosineSimilarity = new CosineSimilarity();


        ArrayList<CosSimRate> cosSimRateArrayList = cosineSimilarity.getSearchResult("Экономить время");

        for (CosSimRate page : cosSimRateArrayList) {
            if (page.getRate() > 0.0) {
                System.out.println("rate:" + page.getRate() + " page:" + page.getPageIndex() + " " + page.getPageRef());
            }
        }
    }
}
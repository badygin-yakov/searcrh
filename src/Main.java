import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

//        InvertedList inverted = new InvertedList();
//        inverted.getInvertedList();
//
//        TFIDFCalculator tfIdf = new TFIDFCalculator();
//        tfIdf.tfIdfInit();

        Search search = new Search();


        ArrayList<CosSimRate> cosSimRate = search.getSearchResult("тщательный информация");

        for (CosSimRate page : cosSimRate) {
            System.out.println(page.getPageRef());
        }
    }
}
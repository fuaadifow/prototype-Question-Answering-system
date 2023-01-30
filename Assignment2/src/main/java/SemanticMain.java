import org.apache.commons.lang3.time.StopWatch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SemanticMain {
    public List<String> listVocabulary = new ArrayList<>();  //List that contains all the vocabularies loaded from the csv file.
    public List<double[]> listVectors = new ArrayList<>(); //Associated vectors from the csv file.
    public List<Glove> listGlove = new ArrayList<>();
    public final List<String> STOPWORDS;

    public SemanticMain() throws IOException {
        STOPWORDS = Toolkit.loadStopWords();
        Toolkit.loadGLOVE();
    }


    public static void main(String[] args) throws IOException {
        StopWatch mySW = new StopWatch();
        mySW.start();
        SemanticMain mySM = new SemanticMain();
        mySM.listVocabulary = Toolkit.getListVocabulary();
        mySM.listVectors = Toolkit.getlistVectors();
        mySM.listGlove = mySM.CreateGloveList();

        List<CosSimilarityPair> listWN = mySM.WordsNearest("computer");
        Toolkit.PrintSemantic(listWN, 5);

        listWN = mySM.WordsNearest("phd");
        Toolkit.PrintSemantic(listWN, 5);

        List<CosSimilarityPair> listLA = mySM.LogicalAnalogies("china", "uk", "london", 5);
        Toolkit.PrintSemantic("china", "uk", "london", listLA);

        listLA = mySM.LogicalAnalogies("woman", "man", "king", 5);
        Toolkit.PrintSemantic("woman", "man", "king", listLA);

        listLA = mySM.LogicalAnalogies("banana", "apple", "red", 3);
        Toolkit.PrintSemantic("banana", "apple", "red", listLA);
        mySW.stop();

        if (mySW.getTime() > 2000)
            System.out.println("It takes too long to execute your code!\nIt should take less than 2 second to run.");
        else
            System.out.println("Well done!\nElapsed time in milliseconds: " + mySW.getTime());
    }

    public List<Glove> CreateGloveList() {
        List<Glove> listResult = new ArrayList<>();
        //TODO Task 6.1
        List<String> listOfVocab = Toolkit.getListVocabulary();
        List<double[]> listOfVectors = Toolkit.getlistVectors();
        for (int i = 0; i < listOfVocab.size(); i++) {
            if (!STOPWORDS.contains(listOfVocab.get(i))) {
                listResult.add(new Glove(listOfVocab.get(i), new Vector(listOfVectors.get(i))));
            }
        }

        return listResult;
    }

    public List<CosSimilarityPair> WordsNearest(String _word) {
        List<CosSimilarityPair> listCosineSimilarity = new ArrayList<>();
        //TODO Task 6.2

        // getting index of query word from list
        int index = listGlove.indexOf(new Glove(_word, null));
        for (Glove glove : listGlove) {
            if (index >= 0) {
                Glove item = listGlove.get(index);

                // if list contains check if it's not itself
                if (!glove.equals(new Glove(_word, null))) {
                    CosSimilarityPair pair = new CosSimilarityPair(_word, glove.getVocabulary(),
                            item.getVector().cosineSimilarity(glove.getVector()));
                    listCosineSimilarity.add(pair);
                }
            } else {
                // if list does not contain the query word, use word error and get the vector from that Glove
                int indexError = listGlove.indexOf(new Glove("error", null));

                if(indexError >=0){
                    Glove item = listGlove.get(indexError);

                    // if list contains check if it's not itself
                    if (!glove.equals(new Glove("error", null))) {
                        CosSimilarityPair pair = new CosSimilarityPair(item.getVocabulary(),
                                glove.getVocabulary(),
                                item.getVector().cosineSimilarity(glove.getVector()));
                        listCosineSimilarity.add(pair);
                    }
                }
            }
        }

        return HeapSort.doHeapSort(listCosineSimilarity);

    }

    public List<CosSimilarityPair> WordsNearest(Vector _vector) {
        List<CosSimilarityPair> listCosineSimilarity = new ArrayList<>();
        //TODO Task 6.3
        for (Glove item : listGlove) {

            // if list contains check if it's not itself
            if (!item.getVector().equals(_vector)) {
                CosSimilarityPair pair = new CosSimilarityPair(_vector, item.getVocabulary(),
                        _vector.cosineSimilarity(item.getVector()));
                listCosineSimilarity.add(pair);
            }
        }
        return HeapSort.doHeapSort(listCosineSimilarity);
    }

    /**
     * Method to calculate the logical analogies by using references.
     * <p>
     * Example: uk is to london as china is to XXXX.
     *       _firISRef  _firTORef _secISRef
     * In the above example, "uk" is the first IS reference; "london" is the first TO reference
     * and "china" is the second IS reference. Moreover, "XXXX" is the vocabulary(ies) we'd like
     * to get from this method.
     * <p>
     * If _top <= 0, then returns an empty listResult.
     * If the vocabulary list does not include _secISRef or _firISRef or _firTORef, then returns an empty listResult.
     *
     * @param _secISRef The second IS reference
     * @param _firISRef The first IS reference
     * @param _firTORef The first TO reference
     * @param _top      How many vocabularies to include.
     */
    public List<CosSimilarityPair> LogicalAnalogies(String _secISRef, String _firISRef, String _firTORef, int _top) {
        List<CosSimilarityPair> listResult = new ArrayList<>();
        //TODO Task 6.4
        if (listGlove.contains(new Glove(_firISRef, null))) {
            Glove glove1 = listGlove.get(listGlove.indexOf(new Glove(_firISRef, null)));
            Vector vector1 = glove1.getVector();

            if (listGlove.contains(new Glove(_secISRef, null))) {
                Glove glove2 = listGlove.get(listGlove.indexOf(new Glove(_secISRef, null)));
                Vector vector2 = glove2.getVector();

                if (listGlove.contains(new Glove(_firTORef, null))) {
                    Glove glove3 = listGlove.get(listGlove.indexOf(new Glove(_firTORef, null)));
                    Vector vector3 = glove3.getVector();

                    // Main Formula to get the Vnew vector which gets us the nearest words from DSM file
                    Vector result = vector2.subtraction(vector1).add(vector3);

                    List<CosSimilarityPair> resultList = WordsNearest(result);

                    if (resultList.size() > _top) {
                        int i = 0;
                        while (listResult.size() < _top) {

                            // if list contains check if it's not itself
                            if((!resultList.get(i).getWord2().contentEquals(glove1.getVocabulary())) &&
                                    (!resultList.get(i).getWord2().contentEquals(glove2.getVocabulary())) &&
                                    (!resultList.get(i).getWord2().contentEquals(glove3.getVocabulary())))
                                listResult.add(resultList.get(i));
                            ++i;
                        }
                    }
                }
            }
        }

        return listResult;
    }
}
public class Glove {
    private String strVocabulary;
    private Vector vecVector;

    public Glove(String _vocabulary, Vector _vector) {
        //TODO Task 2.1
        this.strVocabulary = _vocabulary;
        this.vecVector = _vector;
    }

    public String getVocabulary() {
        //TODO Task 2.2
        return strVocabulary;
    }

    public Vector getVector() {
        //TODO Task 2.3
        return vecVector;
    }

    public void setVocabulary(String _vocabulary) {
        //TODO Task 2.4
        this.strVocabulary = _vocabulary;
    }

    public void setVector(Vector _vector) {
        //TODO Task 2.5
        this.vecVector = _vector;
    }
    // had to add a equals override as we are trying to fetch the index of query word from list of glove in semantic main
    // as the list generic are glove.
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Glove item) {
            return item.strVocabulary.contentEquals(this.strVocabulary);
        }
        return false;
    }
}

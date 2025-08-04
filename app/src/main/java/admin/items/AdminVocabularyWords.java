package admin.items;

public class AdminVocabularyWords {
    String wordID, wordContent, simplifiedContent, pronunciation, meaning;

    public AdminVocabularyWords(String wordID, String wordContent, String simplifiedContent, String pronunciation, String meaning) {
        this.wordID = wordID;
        this.wordContent = wordContent;
        this.simplifiedContent = simplifiedContent;
        this.pronunciation = pronunciation;
        this.meaning = meaning;
    }

    public String getWordID() {
        return wordID;
    }

    public void setWordID(String wordID) {
        this.wordID = wordID;
    }

    public String getWordContent() {
        return wordContent;
    }

    public void setWordContent(String wordContent) {
        this.wordContent = wordContent;
    }

    public String getSimplifiedContent() {
        return simplifiedContent;
    }

    public void setSimplifiedContent(String simplifiedContent) {
        this.simplifiedContent = simplifiedContent;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}

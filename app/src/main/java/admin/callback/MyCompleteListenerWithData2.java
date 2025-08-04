package admin.callback;

import java.util.ArrayList;

import admin.items.AdminVocabularyWords;

public interface MyCompleteListenerWithData2 {
    void onSuccess(ArrayList<AdminVocabularyWords> arrayList);
    void onFailure(Exception e);
}

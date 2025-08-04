package com.example.jchiiki;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;


import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Translator extends AppCompatActivity {
    TextView result_TextView;
    EditText inputWord_EditText;
    Button search_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translator);

        result_TextView = findViewById(R.id.result_TextView);
        inputWord_EditText = findViewById(R.id.inputWord_EditText);
        search_Button = findViewById(R.id.search_Button);

        search_Button.setOnClickListener(v -> {
            String wordToSearch = inputWord_EditText.getText().toString().trim();
            if (!wordToSearch.isEmpty()) {
                fetchWordFromAPI(wordToSearch);
            } else {
                result_TextView.setText("Vui lòng nhập từ cần tra.");
            }
        });
    }

    private void fetchWordFromAPI(String word) {
        // Example: 庭, 俄, にわ, niwa
        new Thread(() -> {
            try {
                String apiUrl = getString(R.string.api_base_url) + ":3000/search?q=" + word;

                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String result = response.toString();

                // Parse JSON
                JSONObject json = new JSONObject(result);
                JSONArray data = json.getJSONArray("data"); // Lấy mảng "data"

                if (data.length() == 0) {
                    runOnUiThread(() -> result_TextView.setText("Không tìm thấy kết quả."));
                    return;
                }

                // Chọn phần tử thứ 0
                JSONObject firstEntry = data.getJSONObject(0);

                JSONArray japaneseArray = firstEntry.getJSONArray("japanese");
                JSONObject japanese = japaneseArray.getJSONObject(0);
                String wordKanji = japanese.optString("word", "");
                String reading = japanese.optString("reading", "");

                StringBuilder meanings = new StringBuilder();
                JSONArray senses = firstEntry.getJSONArray("senses");
                for (int i = 0; i < senses.length(); i++) {
                    JSONObject sense = senses.getJSONObject(i);
                    JSONArray englishDefs = sense.getJSONArray("english_definitions");
                    JSONArray partsOfSpeech = sense.getJSONArray("parts_of_speech");

                    meanings.append(i + 1).append(". ");

                    // Add parts of speech
                    for (int j = 0; j < partsOfSpeech.length(); j++) {
                        meanings.append(partsOfSpeech.getString(j));
                        if (j < partsOfSpeech.length() - 1) {
                            meanings.append(", ");
                        }
                    }

                    meanings.append(": ");

                    // Add definitions
                    for (int j = 0; j < englishDefs.length(); j++) {
                        meanings.append(englishDefs.getString(j));
                        if (j < englishDefs.length() - 1) {
                            meanings.append(", ");
                        }
                    }

                    meanings.append("\n");
                }

                String display = "Từ: " + wordKanji + "\n" +
                        "Cách đọc: " + reading + "\n\n" +
                        "Nghĩa:\n" + meanings.toString();

                // Vì đang ở background thread, phải quay lại UI thread để cập nhật giao diện.
                runOnUiThread(() -> result_TextView.setText(display));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> result_TextView.setText("Lỗi: " + e.getMessage()));
            }
        }).start();
    }

    /*
        {
      "meta": { "status": 200 },
      "data": [
        {
          "slug": "庭",
          "japanese": [
            {
              "word": "庭",
              "reading": "にわ"
            }
          ],
          "senses": [
            {
              "english_definitions": ["garden", "yard", "courtyard"],
              "parts_of_speech": ["Noun"]
            },
            {
              "english_definitions": ["field (of action)", "area"],
              "parts_of_speech": ["Noun"]
            }
          ]
        }
      ]
    }



    */
}

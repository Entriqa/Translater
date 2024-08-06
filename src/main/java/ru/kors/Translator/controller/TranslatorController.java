package ru.kors.Translator.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;

@Controller
public class TranslatorController {

    @Value("${translation.api.key}")
    private String apiKey;

    @Value("${translation.folder.id}")
    private String folderId;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/")
    public String translate(@RequestParam String sourceLang,
                            @RequestParam String targetLang,
                            @RequestParam String text,
                            Model model) {

        if (sourceLang == null || targetLang == null || text == null) {
            model.addAttribute("error", "Invalid input parameters");
            return "index";
        }

        String[] words = text.split(" ");
        CompletableFuture<String>[] futures = new CompletableFuture[words.length];

        try {
            StringBuilder translatedText = new StringBuilder();

            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                futures[i] = CompletableFuture.supplyAsync(() -> {
                    String apiKey = this.apiKey;
                    String folderId = this.folderId;
                    String targetLanguage = targetLang;

                    JSONObject body = new JSONObject();
                    try {
                        body.put("targetLanguageCode", targetLanguage);
                        body.put("texts", new JSONArray().put(word));
                        body.put("folderId", folderId);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://translate.api.cloud.yandex.net/translate/v2/translate"))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Api-Key " + apiKey)
                            .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                            .build();

                    try {
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        String responseBody = response.body();

                        JSONObject jsonResponse = new JSONObject(responseBody);
                        if (jsonResponse.has("translations")) {
                            JSONArray translations = jsonResponse.getJSONArray("translations");
                            return translations.getJSONObject(0).getString("text");
                        } else {
                            System.out.println("Error: No translations field in response.");
                            return "Error: No translations field in response.";
                        }
                    } catch (Exception e) {
                        return e.getMessage();
                    }
                }, executorService);
            }

            for (CompletableFuture<String> future : futures) {
                translatedText.append(future.get()).append(" ");
            }

            model.addAttribute("translatedText", translatedText.toString().trim());

        } catch (InterruptedException | ExecutionException e) {
            model.addAttribute("error", "Error occurred while processing the translation");
        }

        return "index";
    }
}

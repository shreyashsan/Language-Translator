package com.example.languagetranxlator;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** @noinspection ALL*/
public class MainActivity extends AppCompatActivity {


    private EditText sourceLanguageEt;
    private TextView destinationLanguageTv;
    private MaterialButton sourceLanguageChooseBtn;
    private MaterialButton destinationLanguageChooseBtn;
    private MaterialButton translateBtn;

    private TranslatorOptions translatorOptions;
    private Translator translator;

    private ProgressDialog progressDialog;
    private ArrayList<ModelLanguage> languageArrayList;
    private static final String TAG = "MAIN_TAG";

    private String sourceLanguageCode = "en";
    private String sourceLanguageTitle = "English";

    private String destinationLanguageCode = "kn";

    private String destinationLanguageTitle = "kannada";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sourceLanguageEt = findViewById(R.id.sourceLanguageEt);
        destinationLanguageTv = findViewById(R.id.destinationLanguageTv);
        sourceLanguageChooseBtn = findViewById(R.id.sourceLanguageChooseBtn);
        destinationLanguageChooseBtn = findViewById(R.id.destinationLanguageChooseBtn);
        translateBtn = findViewById(R.id.translateBtn);


                    progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

                    loadAvailableLanguages();


        sourceLanguageChooseBtn.setOnClickListener(view -> sourceLanguageChoose());

        destinationLanguageChooseBtn.setOnClickListener(view -> destinationLanguageChoose());

        translateBtn.setOnClickListener(view -> validateMenu());
                }



        private String sourceLanguageText = "English";

    private void validateMenu() {

        sourceLanguageText = sourceLanguageEt.getText().toString().trim();

        Log.d(TAG, "validateData : sourceLanguageText :" + sourceLanguageText);

        if (sourceLanguageText.isEmpty()) {
            Toast.makeText(this, "Enter text to Translate...", Toast.LENGTH_SHORT).show();
        } else {
            startTranslation();
        }

    }

    private void startTranslation() {

        progressDialog.setMessage("Processing language model...");
        progressDialog.show();

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(destinationLanguageCode)
                .build();
        translator = Translation.getClient(translatorOptions);

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .build();

        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "onSuccess: model ready,Starting translating...");
                    progressDialog.setMessage("Translating....");

                    translator.translate(sourceLanguageText)
                            .addOnSuccessListener(translatedText -> {
                                Log.d(TAG, "onSuccess: translatedText: " + translatedText);
                                progressDialog.dismiss();
                                destinationLanguageTv.setText(translatedText);
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Log.e(TAG, "onFailure: ", e);
                                Toast.makeText(MainActivity.this, "Failed to translate due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.e(TAG, "onFailure: ", e);
                        Toast.makeText(MainActivity.this, "Failed to ready model due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Keyboard is open
            // Adjust layout elements (e.g., margins, padding) to accommodate the keyboard
        } else {
            // Keyboard is closed
            // Reset layout adjustments
        }
    }

    private void sourceLanguageChoose() {

        PopupMenu popupMenu = new PopupMenu(this, sourceLanguageChooseBtn);

        for (int i = 0; i < languageArrayList.size(); i++) {

            popupMenu.getMenu().add(Menu.NONE, i, i, languageArrayList.get(i).languageTitle);
        }

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem Item) {

                int position = Item.getItemId();


                sourceLanguageCode = languageArrayList.get(position).languageCode;
                sourceLanguageTitle = languageArrayList.get(position).languageTitle;

                sourceLanguageChooseBtn.setText(sourceLanguageTitle);
                sourceLanguageEt.setHint("Enter " + sourceLanguageTitle);


                Log.d(TAG, "onMenuItemClick: sourceLanguageCode: " + sourceLanguageCode);
                Log.d(TAG, "onMenuItemClick: sourceLanguageTitle: " + sourceLanguageTitle);

                return false;
            }
        });
    }

    private void destinationLanguageChoose() {

        PopupMenu popupMenu = new PopupMenu(this, destinationLanguageChooseBtn);

        for (int i = 0; i < languageArrayList.size(); i++) {

            popupMenu.getMenu().add(Menu.NONE, i, i, languageArrayList.get(i).languageTitle);
        }

        popupMenu.show();


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem Item) {

                int position = Item.getItemId();


                destinationLanguageCode = languageArrayList.get(position).languageCode;
                destinationLanguageTitle = languageArrayList.get(position).languageTitle;

                destinationLanguageChooseBtn.setText(destinationLanguageTitle);


                Log.d(TAG, "onMenuItemClick: destinationLanguageCode : " + destinationLanguageCode);
                Log.d(TAG, "onMenuItemClick: destinationLanguageTitle : " + destinationLanguageTitle);

                return false;
            }
        });
    }
    private void loadAvailableLanguages() {
            //init Language array list before starting adding data into it
            languageArrayList = new ArrayList<>();

            List<String> languageCodeList = TranslateLanguage.getAllLanguages();

            for (String languageCode : languageCodeList) {

                String languageTitle = new Locale(languageCode).getDisplayLanguage();

                Log.d(TAG, "loadAvailableLanguages: languageCode:" + languageCode);
                Log.d(TAG, "loadAvailableLanguages: languageTitle:" + languageTitle);


                ModelLanguage modelLanguage = new ModelLanguage(languageCode, languageTitle);
                languageArrayList.add(modelLanguage);


            }
        }

    }
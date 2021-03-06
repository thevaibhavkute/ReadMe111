package com.readme.textrecognization;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

import java.io.IOException;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    TextView textView;
    TextToSpeech textToSpeech;
    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView =findViewById(R.id.textView);
         textToSpeech= new TextToSpeech(this, new TextToSpeech.OnInitListener() {
             @Override
             public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS){
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
             }
         });


        ImagePicker.with(MainActivity.this)
                .cameraOnly()
                .start(100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            Uri uri = data.getData();
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(MainActivity.this, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(requestCode == 100){
            Task<Text> result =
                    recognizer.process(image)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text visionText) {
                                    if(visionText.getText().equals(""))
                                    {
                                        textView.setText("Text Not found on the image");
                                        speak("Text Not found on the image");
                                    }
                                    else {
                                        textView.setText(visionText.getText());
                                        speak(visionText.getText());
                                    }

//                                    Toast.makeText(MainActivity.this,"Text"+visionText.getText() ,Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this,"Error"+e ,Toast.LENGTH_LONG).show();

                                        }
                                    });
        }
    }
    private void speak(String text) {
        textToSpeech.setPitch(0.9f);
        textToSpeech.setSpeechRate(0.7f);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        textToSpeech.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeech.stop();
    }
}
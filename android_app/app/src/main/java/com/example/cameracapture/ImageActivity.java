package com.example.cameracapture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageActivity extends AppCompatActivity {

    private Button homeButton;
    private TextView resultBox;
    private ImageView resultView;
    private Button analysisButton;

    // URLs:
    private final String url = "http://" + "10.0.2.2" + ":" + 5001 + "/";
    private final String classificationUrl = url + "getPredictions";
    private String postBodyString;

    // describes the content type of http request or response
    private MediaType mediaType;

    private RequestBody requestBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.captured_activity_image);

        homeButton = findViewById(R.id.homeButton);
        resultView = findViewById(R.id.resultView);
//        analysisButton = findViewById(R.id.analysisButton);

        Intent intent = getIntent();

        String response = intent.getStringExtra("response_key");
        String url = intent.getStringExtra("url_key");


        // Glide code below (for cached links)
//        RequestOptions options = new RequestOptions()
//                .centerCrop()
//                        .placeholder(R.mipmap.ic_launcher_round)
//                                .error(R.mipmap.ic_launcher_round)
//                .signature(new ObjectKey(123));
//
//
//        Glide.with(this).load(url + "/getImage").apply(options).into(resultView);

        // Using picasso
        Picasso.get().load(url + "/getImage").memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(resultView);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchPage();
            }
        });

        // setting listener for analysis button
//        analysisButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                get();
//            }
//        });
    }

    // private methods

    // method: go to main page
    private void switchPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // method: to go to analysis page
    private void switchToAnalysis(String response) {
        Intent intent = new Intent(this, AnalysisActivity.class);
        intent.putExtra("response_key", String.valueOf(response));
        intent.putExtra("url_key", url);
        startActivity(intent);
    }

    // method to get the value
    private void get() {
        Request request = new Request.Builder().url(classificationUrl).build();
        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                switchToAnalysis(response.body().string());
            }
        });
    }

//    // method for the post request
//    private void postRequest(String url, RequestBody requestBody) {
//
//        // make sure to create just one instance of okHttpClient
//        OkHttpClient okHttpClient = new OkHttpClient();
//
//        Request request = new Request
//                .Builder()
//                .post(requestBody)
//                .url(url)
//                .build();
//
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            // when the response is a failed response
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(ImageActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        call.cancel();
//                    }
//                });
//            }
//
//            // response successfully returned by the server
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            switchToAnalysis(response);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    // method that returns the RequestBody of specific mediaType
//    private RequestBody requestBodyBuilderAnalysis(String message) {
//        postBodyString = message;
//        // setting the type of media
//        mediaType = MediaType.parse("text/plain");
//        requestBody = RequestBody.create(postBodyString, mediaType);
//        return requestBody;
//    }

}

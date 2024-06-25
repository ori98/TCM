package com.example.cameracapture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Storing the image view:
    private ImageView imageView;
    // Button to capture the image
    private Button captureButton;
    private Button uploadButton;
    private Button testConnectionButton;
    private Bitmap photo;

    // components for sending data to server

    // url where we send the data to
    private final String url = "http://" + "10.0.2.2" + ":" + 5001 + "/";
    private final String imagePostUrl = "http://" + "10.0.2.2" + ":" + 5001 + "/upload";
    private String postBodyString;
    // describes the content type of http request or response
    private MediaType mediaType;

    private RequestBody requestBody;

    // flag to keep track if an image is being sent
    boolean pictureTaken = false;

    private static final int REQUEST_CAMERA_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.capturedImage);

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            initializeButtons();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeButtons();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeButtons() {
        captureButton = findViewById(R.id.openCamera);
        uploadButton = findViewById(R.id.uploadButton);
        testConnectionButton = findViewById(R.id.testConnection);

        // defining the action for the image capture button
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("MyApp", "Capture Button is clicked");
                pictureTaken = true;
                Intent open_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (open_camera.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(open_camera, 100);
                } else {
                    Toast.makeText(MainActivity.this, "No camera app found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // setting the listener for the testConnection button
        testConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String testUrl = url + "test";
                // creating a method for the post request
                postRequest(testUrl, requestBodyBuilderTestConnection("Error Message"));
            }
        });

        // setting the listener for the upload button
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // post request for the image upload
                if (photo != null) {
                    postRequest(imagePostUrl, postRequestUpload(getImageToByteArray(photo)));
                } else {
                    Toast.makeText(MainActivity.this, "No image to upload", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        } else {
            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
        }
    }

    // private helper methods

    // method to get the byte array from image
    private byte[] getImageToByteArray(Bitmap bitmapPhoto) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        // RGB 565 is smaller in size and hence faster to load
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        // now storing the image in bitmap to byte array
        bitmapPhoto.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // method that returns the RequestBody of specific mediaType
    private RequestBody requestBodyBuilderTestConnection(String message) {
        postBodyString = message;
        // setting the type of media
        mediaType = MediaType.parse("text/plain");
        requestBody = RequestBody.create(postBodyString, mediaType);
        return requestBody;
    }

    // method for the post request
    private void postRequest(String url, RequestBody requestBody) {
        // make sure to create just one instance of okHttpClient
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request
                .Builder()
                .post(requestBody)
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            // when the response is a failed response
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        call.cancel();
                    }
                });
            }

            // response successfully returned by the server
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // if the response is for the image, open another page
                            // where you display the output
                            if (pictureTaken) {
                                Log.v("MyApp", "Before response");
                                goToResultPage(responseData);
                                Log.v("MyApp", "After response");
                            } else {
                                Toast.makeText(MainActivity.this, responseData, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    // post request for image
    private RequestBody postRequestUpload(byte[] byteArray) {
        RequestBody postBody = new MultipartBody
                .Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();

        Toast.makeText(MainActivity.this, "Please wait.. ", Toast.LENGTH_SHORT).show();

        return postBody;
    }

    // method to go to the results page
    private void goToResultPage(String response) {
        Log.d("MainActivity", "Switching to ImageActivity with response: " + response);
        Intent resultIntent = new Intent(MainActivity.this, ImageActivity.class);
        resultIntent.putExtra("response_key", response);
        resultIntent.putExtra("url_key", url);
        startActivity(resultIntent);
    }
}
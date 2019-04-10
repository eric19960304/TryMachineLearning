package hku.com3330.trymachinelearning;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private MyDrawView myDrawView;
    private Button submitButton;
    private Button clearButton;
    private RequestQueue requestQueue;
    private ConstraintLayout drawingContainer;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        drawingContainer = findViewById(R.id.drawingView);
        submitButton = findViewById(R.id.submitButton);
        clearButton = findViewById(R.id.clearButton);

        // add the drawing canvas to view
        myDrawView = new MyDrawView(this);
        drawingContainer.addView(myDrawView);
        drawingContainer.setDrawingCacheEnabled(true);

        // add button listeners
        clearButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myDrawView.clear();
                drawingContainer.setBackgroundColor(Color.WHITE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Bitmap bitmap = drawingContainer.getDrawingCache();
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        240,
                        240,
                        false
                );

                (new UploadDrawingToServer()).execute(resizedBitmap);
            }
        });

        // create request queue for networking
        requestQueue = Volley.newRequestQueue(this);

    } // end of onCreate

    private class UploadDrawingToServer extends AsyncTask<Bitmap, Void, Void> {

        protected Void doInBackground(Bitmap... bitmaps){

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String base64Image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP);

            Map<String, String> data = new HashMap();
            data.put("img", base64Image);
            JSONObject postData = new JSONObject(data);

            // send POST request to server
            String url = "http://10.21.4.106:8890/edge2Shoe"; // URL to call

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (
                        Request.Method.POST,
                        url,
                        postData,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    String base64Image = response.getString("result");
                                    byte[] decodedString = Base64.decode(base64Image, Base64.NO_WRAP);
                                    final Bitmap resultBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                    drawingContainer.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("myTest", resultBitmap.toString());
                                            // myDrawView.setCanvas(resultBitmap);
                                            myDrawView.clear();
                                            drawingContainer.setBackgroundColor(Color.WHITE);
                                            drawingContainer.setBackground(new BitmapDrawable(getApplicationContext().getResources(), resultBitmap));
                                        }
                                    });


                                }catch (Exception e){
                                    Log.d("myTest", e.toString());
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("myTest", "error when making POST request"+error.toString());
                            }
                        }
                    );

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(jsonObjectRequest);
            return null;
        } // end of doInBackground

    } // end of UploadDrawingToServer
}

package hku.com3330.trymachinelearning;

import android.graphics.Bitmap;
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
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import java.io.ByteArrayOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private MyDrawView myDrawView;
    private Button submitButton;
    private Button clearButton;
    private RequestQueue requestQueue;

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
        ConstraintLayout drawingView = findViewById(R.id.drawingView);
        submitButton = findViewById(R.id.submitButton);
        clearButton = findViewById(R.id.clearButton);

        // add the drawing canvas to view
        myDrawView = new MyDrawView(this);
        drawingView.addView(myDrawView);

        // add button listeners
        clearButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myDrawView.clear();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Bitmap bitmap = myDrawView.getBitmap();
                Log.d("myTest", bitmap.toString());
                (new UploadDrawingToServer()).execute(bitmap);
            }
        });

        // create request queue for networking
        requestQueue = Volley.newRequestQueue(this);

    } // end of onCreate

    private class UploadDrawingToServer extends AsyncTask<Bitmap, Void, Void> {

        protected Void doInBackground(Bitmap... bitmap){

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap[0].compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d("myTest", Integer.toString(base64Image.length()));

            // make a json string
            JSONObject data = new JSONObject();

            try{
                data.put("img", base64Image);
            }catch(Exception e){
                Log.d("myTest", "something wrong when making json string");
            }


            // send POST request to server
            String urlString = "http://localhost/edge2Shoe"; // URL to call


            return null;
        }

        protected void onPostExecute(Void v){

        }

    } // end of UploadDrawingToServer
}

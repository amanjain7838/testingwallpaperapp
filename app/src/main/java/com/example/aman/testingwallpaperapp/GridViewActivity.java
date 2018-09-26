package com.example.aman.testingwallpaperapp;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.CubeGrid;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ir.apend.slider.model.Slide;
import ir.apend.slider.ui.Slider;

public class GridViewActivity extends AppCompatActivity{



    private static final String TAG = GridViewActivity.class.getSimpleName();
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private GridViewAdapter mGridAdapter;
    private List<Slide> slideList;
    private Slider slider;
    private ArrayList<GridItem> mGridData;
    private String FEED_URL = "http://alltypecards.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gridview);
        mGridView = (GridView) findViewById(R.id.gridView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        CubeGrid doubleBounce = new CubeGrid();
        mProgressBar.setIndeterminateDrawable(doubleBounce);
        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);
        slider = findViewById(R.id.slider);

//create list of slides
        slideList = new ArrayList<>();
        slideList.add(new Slide(0,"http://cssslider.com/sliders/demo-20/data1/images/picjumbo.com_img_4635.jpg" , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        slideList.add(new Slide(1,"http://cssslider.com/sliders/demo-12/data1/images/picjumbo.com_hnck1995.jpg" , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        slideList.add(new Slide(2,"http://cssslider.com/sliders/demo-19/data1/images/picjumbo.com_hnck1588.jpg" , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        slideList.add(new Slide(3,"http://wowslider.com/sliders/demo-18/data1/images/shanghai.jpg" , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        slider.addSlides(slideList);
        slider.setVisibility(View.INVISIBLE);
        //Start download
        new AsyncHttpTask().execute(FEED_URL);
        mProgressBar.setVisibility(View.VISIBLE);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                GridItem item = (GridItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(GridViewActivity.this, DetailsActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("image", item.getImage());
                startActivity(intent);
            }
        });
        slider.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //do what you want
                Slide item = slideList.get(i);
                Intent intent = new Intent(GridViewActivity.this, DetailsActivity.class);
                intent.putExtra("title", item.getImageUrl());
                intent.putExtra("image", item.getImageUrl());
                startActivity(intent);
            }
        });
    }
    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = streamToString(httpResponse.getEntity().getContent());
                    parseResult(response);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result;
        }
        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            if (result == 1) {
                mGridAdapter.setGridData(mGridData);
            } else {
                Toast.makeText(GridViewActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setVisibility(View.GONE);
            slider.setVisibility(View.VISIBLE);
        }
    }
    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        // Close stream
        if (null != stream) {
            stream.close();
        }
        return result;
    }
    /**
     * Parsing the feed results and get the list
     * @param result
     */
    private void parseResult(String result) {
        try {
            //JSONObject response = new JSONObject(result);
            //JSONArray posts = response.optJSONArray("posts");
            JSONArray posts=new JSONArray(result);
            GridItem item;
            for (int i = 0; i < posts.length(); i++) {
                //JSONObject post = posts.optJSONObject(i);
                //String title = post.optString("title");
                String title=posts.get(i).toString();
                item = new GridItem();
                if(title !=null)
                {item.setTitle(title);
                item.setImage("http://alltypecards.com/images/"+title);
                }
                /*
                JSONArray attachments = post.getJSONArray("attachments");
                if (null != attachments && attachments.length() > 0) {
                    JSONObject attachment = attachments.getJSONObject(0);
                    if (attachment != null)
                    {
                        item.setImage(attachment.optString("url").replace("http","https"));
                    }
                    else
                        item.setImage("https://www.stacktips.com/wp-content/uploads/2018/09/Free-Java-Script-Tutorials-375x300.jpg");

                }
                else
                    item.setImage("https://www.stacktips.com/wp-content/uploads/2018/09/Free-Java-Script-Tutorials-375x300.jpg");
                */
                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

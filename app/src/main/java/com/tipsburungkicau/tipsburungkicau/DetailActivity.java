package com.tipsburungkicau.tipsburungkicau;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tipsburungkicau.tipsburungkicau.models.BlogModel;
import com.tipsburungkicau.tipsburungkicau.models.MovieModel;

public class DetailActivity extends AppCompatActivity {

    private ImageView ivMovieIcon;
    private TextView tvMovie;
    private TextView tvTagline;
    private TextView tvYear;
    private TextView tvDuration;
    private TextView tvDirector;
    private RatingBar rbMovieRating;
    private TextView tvCast;
    private TextView tvStory;
    private ProgressBar progressBar;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        // Showing and Enabling clicks on the Home/Up button
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (bundle != null) {
            String json = bundle.getString("movieModel"); //Mengambil Model dari Blogmodel Via Put Exra
            BlogModel movieModel = new Gson().fromJson(json, BlogModel.class);
            setTitle(movieModel.getTitle().substring(0,20) + "...");
        }
        // setting up text views and stuff
        setUpUIViews();

        // recovering data from MainActivity, sent via intent
        bundle = getIntent().getExtras();
        //Toast.makeText(DetailActivity.this, bundle.getString("movieModel"), Toast.LENGTH_SHORT).show();

        try {
            if (bundle != null){
                String json = bundle.getString("movieModel"); //Mengambil Model dari Blogmodel Via Put Exra
                BlogModel movieModel = new Gson().fromJson(json, BlogModel.class);

                ImageLoader.getInstance().displayImage(movieModel.getMedia$thumbnail().replaceAll("s72-c","s1600"), ivMovieIcon, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

                tvMovie.setText(movieModel.getTitle());
                //tvTagline.setText(movieModel.getTagline());
                tvYear.setText("Dipublis: " + movieModel.getPublished());
                tvDuration.setText(movieModel.getContent());
                // tvDirector.setText("Director:" + movieModel.getDirector());

                // rating bar
                // rbMovieRating.setRating(movieModel.getRating() / 2);

                StringBuffer stringBuffer = new StringBuffer();
                for(BlogModel.Categori cast : movieModel.getCategori()){
                    stringBuffer.append(cast.getTerm() + ", ");
                }

                tvDirector.setText("Categori: " + stringBuffer);
                // tvStory.setText(movieModel.getStory());
            }

        } catch (Exception e) {
            tvMovie.setText(e.toString());
            String json = bundle.getString("movieModel"); //Mengambil Model dari Blogmodel Via Put Exra
            tvDuration.setText(json);
        }



    }

    private void setUpUIViews() {
        ivMovieIcon = (ImageView)findViewById(R.id.ivIcon);
        tvMovie = (TextView)findViewById(R.id.tvMovie);
       // tvTagline = (TextView)findViewById(R.id.tvTagline);
        tvYear = (TextView)findViewById(R.id.tvYear);
        tvDuration = (TextView)findViewById(R.id.tvDuration);
        tvDirector = (TextView)findViewById(R.id.tvDirector);
        //rbMovieRating = (RatingBar)findViewById(R.id.rbMovie);
        tvCast = (TextView)findViewById(R.id.tvCast);
       // tvStory = (TextView)findViewById(R.id.tvStory);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.share) {
            if (bundle != null) {
                String json = bundle.getString("movieModel"); //Mengambil Model dari Blogmodel Via Put Exra
                BlogModel movieModel = new Gson().fromJson(json, BlogModel.class);

                Intent myintent = new Intent(Intent.ACTION_SEND);
                myintent.setType("text/plain");
                //String Sharetitle = "http://www.tipsburungkicau.com";
               // String Sharebody = "Yee Bisakah";
               // myintent.putExtra(Intent.EXTRA_SUBJECT,Sharebody);
                myintent.putExtra(Intent.EXTRA_TEXT, movieModel.getTitle() + "\n"+ movieModel.getLink());
                startActivity(Intent.createChooser(myintent, "Share Using"));
            }

        }

        return super.onOptionsItemSelected(item);
    }

}

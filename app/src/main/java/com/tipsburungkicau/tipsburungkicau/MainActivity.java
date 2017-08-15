package com.tipsburungkicau.tipsburungkicau;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.gson.Gson;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tipsburungkicau.tipsburungkicau.models.BlogModel;
//import com.tipsburungkicau.tipsburungkicau.models.MovieModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

   // private final String URL_TO_HIT = "http://www.tipsburungkicau.com/feeds/posts/default?alt=json";
    private final String URL_TO_HIT = "http://www.tipsburungkicau.com/feeds/posts/default/?alt=json&max-results=100";
    private TextView tvData;
    private ListView lvMovies;
    private ProgressDialog dialog;
    private int key = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait..."); // showing a dialog for loading the data
        // Create default options which will be used for every
        //  displayImage(...) call if no options will be passed to this method

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start

        lvMovies = (ListView)findViewById(R.id.lvMovies);


        // To start fetching the data when app start, uncomment below line to start the async task.
        new JSONTask().execute(URL_TO_HIT);
    }


    public class JSONTask extends AsyncTask<String,String, List<BlogModel>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<BlogModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line ="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONObject parentObject2 = parentObject.getJSONObject("feed");
                JSONArray parentArray = parentObject2.getJSONArray("entry");

                List<BlogModel> movieModelList = new ArrayList<>();

//                Gson gson = new Gson();
                for(key =0; key<parentArray.length(); key++) {
                    JSONObject finalObject = parentArray.getJSONObject(key);
                    BlogModel movieModel = new BlogModel();

                    JSONObject title = finalObject.getJSONObject("title");
                    //JSONObject author = finalObject.getJSONObject("author");
                    JSONObject thumbnile = finalObject.getJSONObject("media$thumbnail");
                    JSONObject published = finalObject.getJSONObject("published");
                    JSONObject content = finalObject.getJSONObject("content");

                    //Show json link
                    JSONArray urlArray = finalObject.getJSONArray("link");
                    JSONObject urlObject = urlArray.getJSONObject(4);
                    movieModel.setLink(urlObject.getString("href"));


                    movieModel.setTitle(title.getString("$t"));
                    movieModel.setPublished(published.getString("$t").substring(0, 10));
                    movieModel.setContent(content.getString("$t")
                            .replaceAll("<(.*?)\\>","\n")
                            .replaceAll("&nbsp;"," ")
                            .replaceAll("&amp;"," "));
                    //movieModel.setAuthor(author.getString("$t"));
                    movieModel.setMedia$thumbnail(thumbnile.getString("url"));


                    List<BlogModel.Categori> castList = new ArrayList<>();
                    for(int j=0; j<finalObject.getJSONArray("category").length(); j++){
                        BlogModel.Categori term = new BlogModel.Categori();
                        term.setTerm(finalObject.getJSONArray("category").getJSONObject(j).getString("term"));
                        castList.add(term);

                    }
                    movieModel.setCategori(castList);
                    movieModelList.add(movieModel);
                }
                return movieModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return  null;
        }

        @Override
        protected void onPostExecute(final List<BlogModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                MovieAdapter adapter = new MovieAdapter(getApplicationContext(), R.layout.row, result);
                lvMovies.setAdapter(adapter);
                lvMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BlogModel movieModel = result.get(position); // getting the model
                        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                        intent.putExtra("movieModel", new Gson().toJson(movieModel)); // converting model json into string type and sending it via intent
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class MovieAdapter extends ArrayAdapter {

        private List<BlogModel> movieModelList;
        private int resource;
        private LayoutInflater inflater;
        public MovieAdapter(Context context, int resource, List<BlogModel> objects) {
            super(context, resource, objects);
            movieModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.ivMovieIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
                holder.tvMovie = (TextView)convertView.findViewById(R.id.tvMovie);
                //holder.tvTagline = (TextView)convertView.findViewById(R.id.tvTagline);
                holder.tvYear = (TextView)convertView.findViewById(R.id.tvYear);
                holder.tvDuration = (TextView)convertView.findViewById(R.id.tvDuration);
                holder.tvDirector = (TextView)convertView.findViewById(R.id.tvDirector);
                //holder.rbMovieRating = (RatingBar)convertView.findViewById(R.id.rbMovie);
                holder.tvCast = (TextView)convertView.findViewById(R.id.tvCast);
               // holder.tvStory = (TextView)convertView.findViewById(R.id.tvStory);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);

            // Then later, when you want to display image
            final ViewHolder finalHolder = holder;
            ImageLoader.getInstance().displayImage(movieModelList.get(position).getMedia$thumbnail(), holder.ivMovieIcon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                    finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                    finalHolder.ivMovieIcon.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                    finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
                }
            });

            holder.tvMovie.setText(movieModelList.get(position).getTitle());
            //holder.tvTagline.setText(movieModelList.get(position).getContent());
            holder.tvYear.setText("Dipublish: " + movieModelList.get(position).getPublished());
            //holder.tvDuration.setText("Duration:" + movieModelList.get(position).getDuration());
           // holder.tvDirector.setText("Director:" + movieModelList.get(position).getAuthor());

            // rating bar
            //holder.rbMovieRating.setRating(movieModelList.get(position).getRating()/2);

            StringBuffer stringBuffer = new StringBuffer();
            for(BlogModel.Categori cast : movieModelList.get(position).getCategori()){
                stringBuffer.append(cast.getTerm() + ", ");
            }

            holder.tvCast.setText("Kategori: " + stringBuffer);
            //holder.tvStory.setText(movieModelList.get(position).getContent());
            return convertView;
        }


        class ViewHolder{
            private ImageView ivMovieIcon;
            private TextView tvMovie;
            private TextView tvTagline;
            private TextView tvYear;
            private TextView tvDuration;
            private TextView tvDirector;
            private RatingBar rbMovieRating;
            private TextView tvCast;
            private TextView tvStory;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            new JSONTask().execute(URL_TO_HIT);
            return true;
        }
        if (id == R.id.share) {
            Intent myintent = new Intent(Intent.ACTION_SEND);
            myintent.setType("text/plain");
            //String Sharetitle = "http://www.tipsburungkicau.com";
            // String Sharebody = "Yee Bisakah";
            // myintent.putExtra(Intent.EXTRA_SUBJECT,Sharebody);
            myintent.putExtra(Intent.EXTRA_TEXT, "http://www.tipsburungkicau.com");
            startActivity(Intent.createChooser(myintent, "Share Using"));

        }

        return super.onOptionsItemSelected(item);
    }


}

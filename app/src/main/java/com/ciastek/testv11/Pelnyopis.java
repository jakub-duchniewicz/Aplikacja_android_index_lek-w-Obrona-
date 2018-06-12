package com.ciastek.testv11;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class Pelnyopis extends AppCompatActivity {

    String query1;
    String Opis[] = new String[5];
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pelnyopis);
        Toolbar toolbar=findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        context = this;
    }

    @Override
    protected void onStart()
    {
        Long id = getIntent().getExtras().getLong("MY_KEY");
        getJSON("http://192.168.0.109:3000/leki_duzyopis/"+id);
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                query1=query;
                Intent intent = new Intent(searchView.getContext(),Leki.class);
                intent.putExtra("query", query1);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.cofnij_button:
                finish();
                //settings
                break;

            case R.id.kategoria:
                finish();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                break;

            case R.id.Leki:
                finish();
                long id1=-1;
                Intent intent1 = new Intent(getApplicationContext(),Leki.class);
                intent1.putExtra("MY_KEY", id1);
                startActivity(intent1);
                break;

            default:
                //unknow error
        }
        return super.onOptionsItemSelected(item);
    }

    private void getJSON(final String urlWebService)
    {
        class GetJSON extends AsyncTask<Void, Void, String>
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String content) {
                try
                {
                    loadIntolastView(content);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids)
            {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null)
                        {
                            sb.append(json + "\n");
                        }
                        return sb.toString().trim();
                }
                catch (Exception e)
                    {
                        return null;
                    }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadIntolastView(String json) throws JSONException
    {
        try
        {
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject productObject = jsonArray.getJSONObject(i);
                Opis[0] = productObject.getString("duzezdjecie_link");
                Opis[1] = productObject.getString("opis");
                Opis[2] = productObject.getString("nazwa");
                Opis[3] = productObject.getString("producent");
                Opis[4] = productObject.getString("info_opakowanie");
            }

            ImageView ivBasicImage = (ImageView) findViewById(R.id.imageViewProduct2);
            Picasso.with(context).load(Opis[0]).into(ivBasicImage);
            TextView tv1 = (TextView) findViewById(R.id.textView2);
            tv1.setText(Opis[1]);
            TextView tv2 = (TextView) findViewById(R.id.Opis_info);
            tv2.setText("Nazwa: "+Opis[2]+"\n"+"Producent: "+Opis[3]+"\n"+"Informacje: \n "+Opis[4]);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}

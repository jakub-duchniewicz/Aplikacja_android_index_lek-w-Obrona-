package com.ciastek.testv11;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Leki extends AppCompatActivity {

    ArrayList<Product> arrayList;
    ListView lv;
    String query1;
    String[] array;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leki);
        Toolbar toolbar=findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        lv = (ListView) findViewById(R.id.listView);
        lv.setOnItemClickListener (new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                lv.setAdapter(null);
                Intent intent = new Intent(view.getContext(), Pelnyopis.class);
                id=Integer.valueOf(array[position]);
                intent.putExtra("MY_KEY", id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart()
    {
        arrayList = new ArrayList<>();
        Long id = getIntent().getExtras().getLong("MY_KEY");
        if(id == -1)
            {
                getJSON("http://192.168.0.109:3000/leki/");
            }
        else
        {
            if(id==0)
                {
                    String query = getIntent().getExtras().getString("query");
                    getJSON("http://192.168.0.109:3000/leki/search/" + query);
                }
            else
                {
                    getJSON("http://192.168.0.109:3000/leki/" + id);
                }
        }
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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
                break;

            case R.id.kategoria:
                finish();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                break;

            case R.id.Leki:
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
                    loadIntoListView(content);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(Void... voids)
            {
                try
                    {
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

    private void loadIntoListView(String json) throws JSONException
    {
        try
            {
                JSONArray jsonArray = new JSONArray(json);
                array= new String[jsonArray.length()];
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject productObject = jsonArray.getJSONObject(i);
                    arrayList.add(new Product(
                        productObject.getString("malezdjecie_link"),
                        productObject.getString("nazwa"),
                        productObject.getString("krotki_opis")
                    ));
                    array[i]=productObject.getString("id");
                }
            }
        catch (JSONException e)
            {
                e.printStackTrace();
            }
        CustomListAdapter adapter = new CustomListAdapter(
                getApplicationContext(),R.layout.custom_list_layout, arrayList);
        lv.setAdapter(adapter);
    }
}





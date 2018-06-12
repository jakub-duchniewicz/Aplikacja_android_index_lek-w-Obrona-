package com.ciastek.testv11;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


//test test
public class MainActivity extends AppCompatActivity {

    public int id;
    ArrayList<Product> arrayList;
    String[] array;
    ListView lv;
    String query1;
    int cofnij;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.appbar);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onStart() {
        cofnij=0;
        arrayList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listView);
        getJSON("http://192.168.0.109:3000/kategoria/");
        lv.setOnItemClickListener (new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lv.setAdapter(null);
                Intent intent = new Intent(view.getContext(), Leki.class);
                id=Integer.valueOf(array[position]);
                intent.putExtra("MY_KEY", id);
                startActivity(intent);
            }
        });
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
        {

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {

            case R.id.cofnij_button:
                cofnij++;
                if(cofnij==2)
                    System.exit(0);//settings
                break;

            case R.id.kategoria:
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

    private void getJSON(final String urlWebService) {
        /*
        * As fetching the json string is a network operation / jako że odbiór łańcucha stringów w formacje json jest operacją internetową
        * And we cannot perform a network operation in main thread / i nie możemy przeprowadzić operacji internetowej w głównym wątku
        * so we need an AsyncTask / a więc potrzebujemy AsyncTask (Oddzielny wątek który działa w tle nie zajmująć głównego wątku
        * The constrains defined here are
        * Void -> We are not passing anything
        * Void -> Nothing at progress update as well
        * String -> After completion it should return a string and it will be the json string
        * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution // ta metoda zostanie wywołana przed jej wykonaniem
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution // ta metoda zostanie wywołana po wywołaniu metody GetJSON
            @Override
            protected void onPostExecute(String content) {
                try {
                    loadIntoListView(content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //in this method we are fetching the json string // w tej metodzie odbieramy JSON string
            @Override
            protected String doInBackground(Void... voids) {

                try {
                    //creating a URL // tworzenie URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection //Otwarcie połączenia używając HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service //Obiekt do zbudowania łańcucha danych z serwera
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service // Używam buffered reader aby odczytać dane z usługi
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line // Prosta zmienna string która odczyta dane z każdej lini
                    String json;

                    //reading until we don't find null // Odczytywanie JSON do póki linia nie jest zakończona
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder //po każdej odczytanej lini następuje przejście do nowej lini
                        sb.append(json + "\n");
                    }

                    //finally returning the read string // zwrócenie całego bloku danych
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it // stworzenie obiektu i jego wywołanie
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }




    private void loadIntoListView(String json) throws JSONException {
        //creating a json array from the json string //Stworzenie macierzy JSON
        try{

            JSONArray jsonArray = new JSONArray(json);
            array= new String[jsonArray.length()];
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject productObject = jsonArray.getJSONObject(i);
                arrayList.add(new Product(
                        productObject.getString("zdjeciepogladowe_link"),
                        productObject.getString("rodzaj"),
                        productObject.getString("krotki_opis")
                ));
                array[i]=productObject.getString("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CustomListAdapter adapter = new CustomListAdapter(
                getApplicationContext(),R.layout.custom_list_layout, arrayList
        );
        lv.setAdapter(adapter);


    }
}
/*package com.ciastek.testv11;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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



public class MainActivity extends AppCompatActivity {

    public int id;
    ArrayList<Product> arrayList;
    String[] array;
    ListView lv;
    String query1;
    int cofnij;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart()
    {
        cofnij=0;
        arrayList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listView);
        getJSON("http://192.168.0.109:3000/kategoria/");
        lv.setOnItemClickListener (new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lv.setAdapter(null);
                Intent intent = new Intent(view.getContext(), Leki.class);
                id=Integer.valueOf(array[position]);
                intent.putExtra("MY_KEY", id);
                startActivity(intent);
            }
        });
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
            public boolean onQueryTextSubmit(String query) {
                query1=query;
                Intent intent = new Intent(searchView.getContext(),Leki.class);
                intent.putExtra("query", query1);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {

            case R.id.cofnij_button:
                cofnij++;
                if(cofnij==2)
                System.exit(0);//settings
                break;

            case R.id.kategoria:
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
                try {
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
                    productObject.getString("zdjeciepogladowe_link"),
                    productObject.getString("rodzaj"),
                    productObject.getString("krotki_opis")
                    ));
                    array[i]=productObject.getString("id");
                }
            }
        catch (JSONException e)
            {
                e.printStackTrace();
            }
        CustomListAdapter adapter = new CustomListAdapter
                (
                        getApplicationContext(),R.layout.custom_list_layout, arrayList
                );
        lv.setAdapter(adapter);

    }
}
*/
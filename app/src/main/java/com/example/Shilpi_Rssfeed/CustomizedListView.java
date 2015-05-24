package com.example.Shilpi_Rssfeed;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
//REFERENCE USED
//https://github.com/thest1/LazyList/blob/master/src/com/fedorvlasov/lazylist/Utils.java
//FileCache
//LazyAdapter
//Utils




public class CustomizedListView extends Activity {
    // All static variables
    static final String URL = "https://itunes.apple.com/us/rss/topsongs/limit=100/xml";
    // XML node keys
    static final String KEY_SONG = "entry"; // parent node
    static final String KEY_ID = "id";
    static final String KEY_TITLE = "title";
    static final String KEY_ARTIST = "im:artist";
    static final String KEY_PRICE = "im:price";
    static final String KEY_THUMB_URL = "im:image";
    static final String KEY_URL = "link";
    ListView list;
    LazyAdapter adapter;
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
 Button refresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button refresh = (Button) findViewById(R.id.button);//refresh buttton


        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {    //list reloads only when refresh is clicked
                // Starting a new async task
                new loadMoreListView().execute();
            }
        });

        new loadMoreListView().execute();




    }
         //asynk task for loading of list view  asynchronously

        class loadMoreListView extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                // Showing progress dialog before sending http request
                pDialog = new ProgressDialog(
                        CustomizedListView.this);
                pDialog.setMessage("Please wait..");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            protected Void doInBackground(Void... unused) {
                runOnUiThread(new Runnable() {
                    public void run() {

               //xml parser parses the link with DOM and puts the
                        ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

                        XMLParser parser = new XMLParser();
                        String xml = parser.getXmlFromUrl(URL); // getting XML from URL
                        Document doc = parser.getDomElement(xml); // getting DOM element
                        Log.i("doc", String.valueOf(doc));
                        NodeList nl = doc.getElementsByTagName(KEY_SONG);
                        // looping through all song nodes <song>
                        Log.d("nl", String.valueOf(nl));

                        for (int i = 0; i < nl.getLength(); i++) {
                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();
                            Element e = (Element) nl.item(i);
                            Log.d("nl", (e).getTagName());


                            // adding each child node to HashMap key => value
                            map.put(KEY_ID, parser.getValue(e, KEY_ID));
                            map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
                            map.put(KEY_ARTIST, parser.getValue(e, KEY_ARTIST));
                            map.put(KEY_PRICE, parser.getValue(e, KEY_PRICE));
                            map.put(KEY_THUMB_URL, parser.getValue(e, KEY_THUMB_URL));
                            map.put(KEY_URL, parser.getValue(e, KEY_URL));


                            // adding HashList to ArrayList
                            songsList.add(map);
                        }



                        list = (ListView) findViewById(R.id.list);

                        // Getting adapter by passing xml data ArrayList
                        adapter = new LazyAdapter(CustomizedListView.this, songsList);
                        list.setAdapter(adapter);


                        // Click event for single list row
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                XMLParser parser = new XMLParser();
                                String xml = parser.getXmlFromUrl(URL); // getting XML from URL
                                Document doc = parser.getDomElement(xml); // getting DOM element
                                Node href = doc.getElementsByTagName("entry").item(position);
                                final String uri = href.getFirstChild().getNextSibling().getNextSibling().getNextSibling().getTextContent();

                                AlertDialog.Builder builder = new AlertDialog.Builder(CustomizedListView.this);
                                builder
                                        .setTitle("ALERT")
                                        .setMessage("Are you sure you want to leave the APP?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Yes button clicked, call intent and see the full link

                                                Intent webIntent = new Intent("android.intent.action.VIEW", Uri.parse(uri));
                                                startActivity(webIntent);
                                            }
                                        })
                                        .setNegativeButton("No", null)                        //Do nothing on no
                                        .show();


                            }
                        });




                    }
                });

                return (null);
            }


            protected void onPostExecute(Void unused) {
                // closing progress dialog
                pDialog.dismiss();
            }
        }


//on pressing the back button android calls fininsh(); an the activity from stack is killed and you go back to
    //previous activity resumes hence does not loads the url xml contents again but only when you click refresh










}















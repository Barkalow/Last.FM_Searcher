package edu.apsu.csci4020.finalproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import edu.apsu.csci4020.finalproject.R;


public class MainActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	Button searchBtn;
	Button tracksBtn;
	Button artistsBtn;
	EditText box;
	RadioButton songs;
	RadioButton albums;
	RadioButton similar;

    //Booleans to decide which API calls to use based on whats being searched, essentially using process of elimination to provide program flow
	boolean tracks = false;
	boolean topTracks = false;
	boolean topArtists = false;

	ArrayAdapter<Item> artistAdapter;
	ArrayList<Item> result;

	final String M_KEY = "xxxxxxxxxxxxxxxxxx"; //redacted, because private

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Session session = Authenticator.getMobileSession(user, password, M_KEY, secret);

		box = (EditText) findViewById(R.id.bandTxt);
		songs = (RadioButton) findViewById(R.id.topSongs);
		albums = (RadioButton) findViewById(R.id.topAlbums);
		similar = (RadioButton) findViewById(R.id.similarArtists);

		searchBtn = (Button) findViewById(R.id.searchBtn);
		tracksBtn = (Button) findViewById(R.id.tracksBtn);
		artistsBtn = (Button) findViewById(R.id.artistsBtn);

		searchBtn.setOnClickListener(this);
		tracksBtn.setOnClickListener(this);
		artistsBtn.setOnClickListener(this);
	}

	class GetInfo extends AsyncTask<URL, Void, List<Item>> {

		@Override
		protected List<Item> doInBackground(URL... params) {
			URL url = params[0];

			//Holy checked items and flag variables, Batman!
			//Uses various xml handlers and flags to determine how to read the xml
			if(songs.isChecked() && topArtists == false || topTracks == true){

				try {
					HttpURLConnection c = (HttpURLConnection) url.openConnection();

					InputStream contentStream = c.getInputStream();
					SAXParserFactory factory = SAXParserFactory.newInstance();

					SAXParser parser = factory.newSAXParser();
					TopSongsHandler handler = new TopSongsHandler();
					parser.parse(contentStream, handler);

					result = handler.getResults();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}else if(similar.isChecked() || topArtists == true){
				try {
					HttpURLConnection c = (HttpURLConnection) url.openConnection();

					InputStream contentStream = c.getInputStream();
					SAXParserFactory factory = SAXParserFactory.newInstance();

					SAXParser parser = factory.newSAXParser();
					SimilarArtistsHandler handler = new SimilarArtistsHandler();
					parser.parse(contentStream, handler);

					result = handler.getResults();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					HttpURLConnection c = (HttpURLConnection) url.openConnection();

					InputStream contentStream = c.getInputStream();
					SAXParserFactory factory = SAXParserFactory.newInstance();

					SAXParser parser = factory.newSAXParser();
					TopAlbumsHandler handler = new TopAlbumsHandler();
					parser.parse(contentStream, handler);

					result = handler.getResults();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


			return result;
		}


		@Override
		protected void onPostExecute(List<Item> result) {
			//Error messages just in case
			if (result == null) {
				Toast.makeText(getApplicationContext(), "Error",
						Toast.LENGTH_LONG).show();
				return;
			} else if (result.size() == 0) {
				Toast.makeText(getApplicationContext(), "No Content Found",
						Toast.LENGTH_LONG).show();
				return;
			}

			ListView lv;

			//dialog if the user is searching, otherwise its top tracks
			if(topTracks == false && topArtists == false){	
				final Dialog dialog = new Dialog(MainActivity.this);
				dialog.setContentView(R.layout.summarylayout);
				dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
						WindowManager.LayoutParams.MATCH_PARENT);

				lv = (ListView) dialog.findViewById(R.id.itemList);

				artistAdapter = new ArrayAdapter<Item>(getApplicationContext(),
						android.R.layout.simple_list_item_1,
						result.toArray(new Item[] {}));

				lv.setAdapter(artistAdapter);

				lv.setOnItemClickListener(new MyOIListener());

				Button closeButton = (Button) dialog
						.findViewById(R.id.closeBtn);
				// if button is clicked, close the custom dialog
				closeButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						tracks = false;
						dialog.dismiss();
					}
				});


				if(similar.isChecked()){
					dialog.setTitle("Similar Artists");
				}else if(songs.isChecked()){
					dialog.setTitle("Top Tracks");
				}else{
					dialog.setTitle("Top Albums");
				}
				dialog.show();
			}else{

				//If the user isnt looking at top artist/tracks, it doesn't pull up the dialog
				lv = (ListView) findViewById(R.id.topListView);

				artistAdapter = new ArrayAdapter<Item>(getApplicationContext(),
						android.R.layout.simple_list_item_1,
						result.toArray(new Item[] {}));

				lv.setAdapter(artistAdapter);

				lv.setOnItemClickListener(new MyOIListener());
			}
		}
	}

	class MyOIListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			//Band and artistUrl are only used for opening the page in top artists

			String band = box.getText().toString();
			Item item = (Item) parent.getItemAtPosition(position);
			String artistUrl = item.getUrl();
			Uri uri;

			//If the user is looking at top tracks, searches youtube instead of redirecting to the last.fm page
			if(tracks == false && topTracks == false){
				if (topArtists == false){
					uri = Uri.parse(item.getUrl());

					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}else if (topArtists == true){
					//For some reason the urls from the similar artist xml dont have "http://" at the beginning, so the handler adds it
					//Unfortunately this means theres an extra when its from the top tracks xml, so this removes it.
					//Weird, but it works.

					artistUrl = artistUrl.replace("http://", "");
					artistUrl = "http://" + artistUrl;

					uri = Uri.parse(artistUrl);

					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}
			}else if(tracks == true || topTracks == true){
				// there wouldnt be a band name in the text box if they looked at top artists, so it just searches the song name.

				if(topTracks == false){
					Intent intent = new Intent(Intent.ACTION_SEARCH);
					intent.setPackage("com.google.android.youtube");
					intent.putExtra("query", band + " " + item.getTitle());
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);
				}else if(topTracks == true){
					Intent intent = new Intent(Intent.ACTION_SEARCH);
					intent.setPackage("com.google.android.youtube");
					intent.putExtra("query", item.getTitle());
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);
				}
			}
		}

	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String searchUrl = "";
		String band = "";

		if(v.getId() == R.id.searchBtn){
			if(box.getText().toString().equals("")){
				Toast.makeText(getApplicationContext(), "Must Enter A Value",
						Toast.LENGTH_LONG).show();
			}else{
				//changes the API url depending on whats being searched for
				if(similar.isChecked()){
					band = box.getText().toString().replace(" ", "+");
					searchUrl = "http://ws.audioscrobbler.com/2.0/?method=artist.getSimilar&artist=" + band + "&api_key=" + M_KEY;
					topTracks = false;
					topArtists = false;	
				}else if(songs.isChecked()){
					band = box.getText().toString().replace(" ", "+");
					searchUrl = "http://ws.audioscrobbler.com/2.0/?method=artist.getTopTracks&artist=" + band + "&api_key=" + M_KEY;
					tracks = true;
					topTracks = false;
					topArtists = false;	
				}else{
					band = box.getText().toString().replace(" ", "+");
					searchUrl = "http://ws.audioscrobbler.com/2.0/?method=artist.getTopAlbums&artist=" + band + "&api_key=" + M_KEY;
					topTracks = false;
					topArtists = false;	
				}
			}
			//if its getting top tracks or artists charts, uses these urls instead and sets the flags for them
		}else if(v.getId() == R.id.tracksBtn){
			searchUrl = "http://ws.audioscrobbler.com/2.0/?method=chart.gettoptracks&api_key=" + M_KEY;
			topTracks = true;
			topArtists = false;	
		}else if(v.getId() == R.id.artistsBtn){
			searchUrl = "http://ws.audioscrobbler.com/2.0/?method=chart.gettopartists&api_key=" + M_KEY;
			topArtists = true;	
			topTracks = false;
		}


		//Send the url to the xml downloader
		try {
			URL url = new URL(searchUrl);
			GetInfo download = new GetInfo();
			download.execute(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

package com.jwplayer.opensourcedemo;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.cast.CastManager;
import com.longtailvideo.jwplayer.configuration.PlayerConfig;
import com.longtailvideo.jwplayer.events.listeners.VideoPlayerEvents;
import com.longtailvideo.jwplayer.media.ads.Ad;
import com.longtailvideo.jwplayer.media.ads.AdBreak;
import com.longtailvideo.jwplayer.media.ads.AdSource;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;

import java.util.ArrayList;
import java.util.List;

public class JWPlayerViewExample extends AppCompatActivity implements VideoPlayerEvents.OnFullscreenListener, View.OnClickListener {

	/**
	 * Reference to the {@link JWPlayerView}
	 */
	private JWPlayerView mPlayerView;

	/**
	 * An instance of our event handling class
	 */
	private JWEventHandler mEventHandler;

	/**
	 * Reference to the {@link CastManager}
	 */
	private CastManager mCastManager;

	/**
	 * Stored instance of CoordinatorLayout
	 * http://developer.android.com/reference/android/support/design/widget/CoordinatorLayout.html
	 */
	private CoordinatorLayout mCoordinatorLayout;

	TextView outputTextView;
	EditText mediaUrlLink;
	Button submit;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jwplayerview);
		mPlayerView = (JWPlayerView)findViewById(R.id.jwplayer);
		outputTextView = (TextView)findViewById(R.id.output);
		//mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_jwplayerview);

		mediaUrlLink = (EditText)findViewById(R.id.media_url);
		submit = (Button)findViewById(R.id.submit);

		//handle click events when submit is pressed
		submit.setOnClickListener(this);

		// Handle hiding/showing of ActionBar
		mPlayerView.addOnFullscreenListener(this);

		//setupPlayer();
	}

	@Override
	public void onClick(View view) {

		if (mPlayerView != null) {
			mPlayerView.stop();
		}

		setupPlayer();
	}

	void setupPlayer() {

		String mediaUrl;

		// Keep the screen on during playback
		new KeepScreenOnHandler(mPlayerView, getWindow());

		// Instantiate the JW Player event handler class
		mEventHandler = new JWEventHandler(mPlayerView, outputTextView);

		//mPlayerView.setup({modes:[]});


		// Construct a new Ad
		//  Ad ad = new Ad(AdSource.IMA, "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=");
		Ad ad = new Ad(AdSource.IMA, "https://googleads.g.doubleclick.net/pagead/ads?ad_type=standardvideo&client=ca-video-pub-4496925157207714&description_url=https%3A%2F%2Fshop.houseofgod.co%2F&hl=en&max_ad_duration=30000");


// Construct a new AdBreak containing the Ad
// This AdBreak will play a midroll at 10%
		AdBreak adBreak = new AdBreak("10%", ad);

// Add the AdBreak to a List
		List<AdBreak> adSchedule = new ArrayList<>();
		adSchedule.add(adBreak);


		//get the EditText
		mediaUrl = mediaUrlLink.getText().toString().trim();


		// Load a media source
		PlaylistItem pi = new PlaylistItem.Builder()
				.file(mediaUrl)
				.title("Testing")
				.description("A video player testing video.")
				.adSchedule(adSchedule)
				.build();




		// Add the PlaylistItem to a List
		List<PlaylistItem> playlist = new ArrayList<>();
		playlist.add(pi);

// Build the PlayerConfig
		PlayerConfig playerConfig = new PlayerConfig.Builder()
				.playlist(playlist)
				.build();

// Setup the player
		mPlayerView.setup(playerConfig);





		//In original code (from github) but in doc it's .setup()
		//mPlayerView.load(pi);

		// Get a reference to the CastManager TODO IMP - inside original github but not in jw player doc
		//mCastManager = CastManager.getInstance();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// Set fullscreen when the device is rotated to landscape
		mPlayerView.setFullscreen(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE, true);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		// Let JW Player know that the app has returned from the background
		super.onResume();
		mPlayerView.onResume();
	}

	@Override
	protected void onPause() {
		// Let JW Player know that the app is going to the background
		mPlayerView.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// Let JW Player know that the app is being destroyed
		mPlayerView.onDestroy();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Exit fullscreen when the user pressed the Back button
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mPlayerView.getFullscreen()) {
				mPlayerView.setFullscreen(false, true);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Handles JW Player going to and returning from fullscreen by hiding the ActionBar
	 *
	 * @param fullscreen true if the player is fullscreen
	 */
	@Override
	public void onFullscreen(boolean fullscreen) {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			if (fullscreen) {
				actionBar.hide();
			} else {
				actionBar.show();
			}
		}

		// When going to Fullscreen we want to set fitsSystemWindows="false"
		//mCoordinatorLayout.setFitsSystemWindows(!fullscreen);
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_jwplayerview, menu);
		// Register the MediaRouterButton on the JW Player SDK
//		mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.switch_to_fragment:
				Intent i = new Intent(this, JWPlayerFragmentExample.class);
				startActivity(i);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}*/
}

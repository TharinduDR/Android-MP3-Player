package com.example.mp3player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.echonest.api.v4.EchoNestException;

public class MainActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {

	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private TextView songinfo;
	private TextView location;
	private TextView tempo;
	
	private RatingBar songPopRating;
	private RatingBar artistPopRating;
	// Media Player
	private  MediaPlayer mp;
	private  SongInformation si;
	private  ArtistInfomation ai;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private SongsManager songManager;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0; 
	private ImageView image;
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		
		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		location = (TextView) findViewById(R.id.artistlocation);
		tempo = (TextView) findViewById(R.id.songtempo);
		songinfo = (TextView) findViewById(R.id.info);
		songPopRating = (RatingBar)findViewById(R.id.songpopulariti);
		artistPopRating = (RatingBar)findViewById(R.id.artistpopulariti);
		image = (ImageView) findViewById(R.id.imageView);
		
		mp = new MediaPlayer();
		songManager = new SongsManager();
		utils = new Utilities();
		
		//Songinformation
		try {
			si = new SongInformation();
			ai = new ArtistInfomation();
		} catch (EchoNestException e) {
			// TODO Auto-generated catch block
			songinfo.setText("An Error Occured");
		}
		
		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); // Important
		
		// Getting all songs list
		songsList = songManager.getPlayList();
		
		// By default play first song
		playSong(0);
				
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check for already playing
				if(mp.isPlaying()){
					if(mp!=null){
						mp.pause();
						// Changing button image to play button
						btnPlay.setImageResource(R.drawable.btn_play);
					}
				}else{
					// Resume song
					if(mp!=null){
						mp.start();
						// Changing button image to pause button
						btnPlay.setImageResource(R.drawable.btn_pause);
					}
				}
				
			}
		});
		
		/**
		 * Forward button click event
		 * Forwards song specified seconds
		 * */
		btnForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// get current song position				
				int currentPosition = mp.getCurrentPosition();
				// check if seekForward time is lesser than song duration
				if(currentPosition + seekForwardTime <= mp.getDuration()){
					// forward song
					mp.seekTo(currentPosition + seekForwardTime);
				}else{
					// forward to end position
					mp.seekTo(mp.getDuration());
				}
			}
		});
		
		/**
		 * Backward button click event
		 * Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// get current song position				
				int currentPosition = mp.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if(currentPosition - seekBackwardTime >= 0){
					// forward song
					mp.seekTo(currentPosition - seekBackwardTime);
				}else{
					// backward to starting position
					mp.seekTo(0);
				}
				
			}
		});
		
		/**
		 * Next button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check if next song is there or not
				if(currentSongIndex < (songsList.size() - 1)){
					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
				}else{
					// play first song
					playSong(0);
					currentSongIndex = 0;
				}
				
			}
		});
		
		/**
		 * Back button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(currentSongIndex > 0){
					playSong(currentSongIndex - 1);
					currentSongIndex = currentSongIndex - 1;
				}else{
					// play last song
					playSong(songsList.size() - 1);
					currentSongIndex = songsList.size() - 1;
				}
				
			}
		});
		/**
		 * Button Click event for Play list click event
		 * Launches list activity which displays list of songs
		 * */
		btnPlaylist.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
				startActivityForResult(i, 100);			
			}
		});
		
	}
	
	/**
	 * Receiving song index from playlist view
	 * and play the song
	 * */
	@Override
    protected void onActivityResult(int requestCode,
                                     int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
         	 currentSongIndex = data.getExtras().getInt("songIndex");
         	 // play selected song
             playSong(currentSongIndex);
        }
 
    }
	
	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	public void  playSong(int songIndex){
		// Play song
		try {
        	mp.reset();
			mp.setDataSource(songsList.get(songIndex).get("songPath"));
			mp.prepare();
			mp.start();
			// Displaying Song title
			String songTitle = songsList.get(songIndex).get("songTitle");
			
        	songTitleLabel.setText(songTitle);
        	songinfo.setText(songTitle + "\n");
        	//Split Title and Artist Name and get information
        	String[] temp = songTitle.split(" - ");
        	String artistname = temp[0];
        	String songName = temp[1];
        	//pass the parameters to displayinfo method
        	displayinfo(artistname,songName);
        	
			
        	// Changing Button Image to pause image
			btnPlay.setImageResource(R.drawable.btn_pause);
			
			// set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			
			// Updating progress bar
			updateProgressBar();			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (EchoNestException e) {
			songinfo.setText("An Error Occured, Please check you Internet Connection");
		} 
		
	}
	
	//showing the details of the artist/song
	public void displayinfo(String inartistname, String insongname)throws EchoNestException{
		String[] sinfo = si.getSongInfor(insongname,inartistname);
		String ainfo = ai.randomWalk(inartistname, 1);
		String URL = ai.imageprovoder(inartistname, 1);
		String similarArtist = ai.similarartistprovider(inartistname, 1);
		
		
		songinfo.setMovementMethod(new ScrollingMovementMethod());
		
		//if there is a result found
		if(sinfo != null & ainfo!= null & similarArtist != null){
			
			songPopRating.setRating(Float.parseFloat(sinfo[5]) * 5);
			artistPopRating.setRating(Float.parseFloat(sinfo[2]) * 5);
			location.setText("Artist's Location : " + sinfo[4]);
			tempo.setText("Tempo : " + sinfo[7]);
			
			if(URL != null){
				ImageDownloaderTask IDT = new ImageDownloaderTask(image,this);
				IDT.execute(URL);
			}
			
			else{
				ImageDownloaderTask IDT = new ImageDownloaderTask(image,this);
				IDT.execute("http://t0.gstatic.com/images?q=tbn:ANd9GcQBkOVuuLK2S1O5eK2MobSlqYGkiBohKRzBymutUdI2jOo0Goki0A");
			}
			
			songinfo.setTextSize(16);
			songinfo.setText("About Artist - " + ainfo + "You will also like" + similarArtist);
				
		}
		else{
			songinfo.append("Sorry, No results were found to this Song");
		}
	}
	
	
	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }	
	
	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   long totalDuration = mp.getDuration();
			   long currentDuration = mp.getCurrentPosition();
			  
			   // Displaying Total Duration time
			   songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
			   // Displaying time completed playing
			   songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
			   
			   // Updating progress bar
			   int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
			   //Log.d("Progress", ""+progress);
			   songProgressBar.setProgress(progress);
			   
			   // Running this thread after 100 milliseconds
		       mHandler.postDelayed(this, 100);
		   }
		};
		
	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		
	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
    }
	
	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
    public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
		
		// forward or backward to certain seconds
		mp.seekTo(currentPosition);
	
		
		// update timer progress again
		updateProgressBar();
    }

	/**
	 * On Song Playing completed
	 * if repeat is ON play same song again
	 * if shuffle is ON play random song
	 * */
	
	@Override
	public void onCompletion(MediaPlayer arg0) {
			if(currentSongIndex < (songsList.size() - 1)){
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			}else{
				// play first song
				playSong(0);
				currentSongIndex = 0;
			}
		
	}
	
	@Override
	 public void onDestroy(){
	 super.onDestroy();
	    mp.release();
	 }
	
	public void showFullDescription(View v) {
		TextView discription = (TextView) v;
		Layout l = discription.getLayout();
		if (l != null) {
			int lines = l.getLineCount();
			if (lines > 0)
				if (l.getEllipsisCount(lines - 1) > 0) {
					
					discription.setEllipsize(null);
					discription.setMaxLines(Integer.MAX_VALUE);
				} else {
					discription.setMaxLines(10);
					
					discription.setEllipsize(TextUtils.TruncateAt.END);
					
					}
			}
		}	
}
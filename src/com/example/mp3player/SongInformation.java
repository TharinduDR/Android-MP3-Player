package com.example.mp3player;

import java.util.List;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Song;

public class SongInformation {
	
	private EchoNestAPI en;

    public SongInformation() throws EchoNestException {
        en = new EchoNestAPI("Q09JMN3HUZT7UHQBU"); //api key
        en.setTraceSends(false);
        en.setTraceRecvs(false);
    }
    
    public String[] dumpSong(Song song) throws EchoNestException {
    	String[] info = new String[10];
    		//Information about the song & Artist
    		info[0] = song.getTitle();
    		info[1] = song.getArtistName();
    		info[2] = String.format("%.2f", song.getArtistHotttnesss());
    		info[3] = String.format("%.2f", song.getArtistFamiliarity());
    		info[4] = song.getArtistLocation().toString();
    		
    		info[5] = String.format("%.2f", song.getSongHotttnesss());
    		info[6] = String.format("%.2f", song.getDanceability());
    		info[7] = Double.toString(song.getTempo());
    		info[8] = String.format("%.2f", song.getEnergy());
    		info[9] = Double.toString(song.getMode());
    		
  	
    	return info;
    }
    
    public String[] searchSongsByTitle(String title, String artistName, int results)
            throws EchoNestException {
    	String info[] = null;
        Params p = new Params();
        p.add("title", title);
        p.add("results", results);
        List<Song> songs = en.searchSongs(p);
        for (Song song : songs) {
        	if(song.getArtistName().contains(artistName)){
        		info = dumpSong(song);
        		break;
        	}
        }
        return info;
    }
    
    public String[] getSongInfor(String intitle,String inname)throws EchoNestException{
    	String title = intitle;
    	String name = inname;
    	String[] info = null;
    	info = searchSongsByTitle(title,name,100);
    	
    	return info;
    }    
    
}

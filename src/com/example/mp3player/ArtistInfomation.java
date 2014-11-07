package com.example.mp3player;


import java.util.Collections;
import java.util.List;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.Biography;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Image;
//gives infomration abou the Artist
public class ArtistInfomation {
	private EchoNestAPI en;
    private static boolean trace = false;

    public ArtistInfomation() throws EchoNestException {
            en = new EchoNestAPI("Q09JMN3HUZT7UHQBU"); //API Key
            en.setTraceSends(trace);
            en.setTraceRecvs(trace);
            en.setMinCommandTime(0);         
   }
    //artist Information
    public String dumpArtist(Artist artist) throws EchoNestException {
		String information[] = null;
        List<Biography> bios = artist.getBiographies();
        for(int count = 0 ; count < bios.size();count++){
        	Biography bio = bios.get(count);
            if(bio.getText().length() > 100){ //if the length of the result is more than 100
            	information = bio.getText().split("\\n");
            	break; 
            }
        }
        return information[0];       
}
   //artist images 
    public String imagesinformation(Artist artist) throws EchoNestException {
		String information = null;
        List<Image> images = artist.getImages();
        for(int count = 0 ; count < images.size();count++){
        	Image image = images.get(count);
            if(image.getURL().contains("http")){ //image needs to http
            	information = image.getURL();
            	break; 
            }
        }
        return information;       
}
    
    public String similarArtist(Artist artist) throws EchoNestException {
		String similarartistinfo = null;
        List<Artist> artists = artist.getSimilar(5);
        similarartistinfo = artists.get(0).getName();
        return similarartistinfo;       
}

public String imageprovoder(String seedName, int count) throws EchoNestException {
		String image_information = null; 
        List<Artist> artists = en.searchArtists(seedName);
        if (artists.size() > 0) {
                Artist seed = artists.get(0);
                for (int i = 0; i < count; i++) {
                        image_information = imagesinformation(seed);
                        
                        List<Artist> sims = seed.getSimilar(10);
                        if (sims.size() > 0) {
                                Collections.shuffle(sims);
                                seed = sims.get(0);
                        } else {
                                break;
                        }
                }
        }
        return image_information;
}


public String randomWalk(String seedName, int count) throws EchoNestException {
	String information = null; 
    List<Artist> artists = en.searchArtists(seedName);
    if (artists.size() > 0) {
            Artist seed = artists.get(0);
            for (int i = 0; i < count; i++) {
                    information = dumpArtist(seed);
                    
                    List<Artist> sims = seed.getSimilar(10);
                    if (sims.size() > 0) {
                            Collections.shuffle(sims);
                            seed = sims.get(0);
                    } else {
                            break;
                    }
            }
    }
    return information;
}

public String similarartistprovider(String seedName, int count) throws EchoNestException {
	String similarArtist_information = null; 
    List<Artist> artists = en.searchArtists(seedName);
    if (artists.size() > 0) {
            Artist seed = artists.get(0);
            for (int i = 0; i < count; i++) {
            	similarArtist_information = similarArtist(seed);
                    
                    List<Artist> sims = seed.getSimilar(10);
                    if (sims.size() > 0) {
                            Collections.shuffle(sims);
                            seed = sims.get(0);
                    } else {
                            break;
                    }
            }
    }
    return similarArtist_information;
}



}
import java.lang.String;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class DBMusicManager {
    public static final String NOT_FOUND = "Not Found";

    private JSONObject AllData;
    private JSONArray PlaylistChanges;
    
    private JSONArray Users;
    private JSONArray Playlists;
    private JSONArray Songs;

    // Since all entities are simple (fewer than 500 records), I will use a simple for/next O(n)
    // to access them (find operation). If there were more, I would implement a more sophisticated
    // data structure than a list which must be traversed.

    public DBMusicManager(String originalData, String changes, String output) throws FileNotFoundException, IOException, org.json.simple.parser.ParseException {
    	
    	 JSONParser jp = new JSONParser();
         AllData = (JSONObject) jp.parse(new FileReader(originalData));
         JSONObject c = (JSONObject) jp.parse(new FileReader(changes));

         if (AllData == null || c == null) {
        	 throw new IOException("Json files are empty or corrupt.");
         }

         Users = (JSONArray) AllData.get("users");
         Playlists = (JSONArray) AllData.get("playlists");
         Songs = (JSONArray) AllData.get("songs");
         PlaylistChanges = (JSONArray) c.get("playlists");
         
       	 this.processChanges();
       	 this.writeToFile(output);
    } 

    protected void processChanges() {
        try {
            for (Object change: this.PlaylistChanges) {
            	this.changePlaylist((JSONObject) change);
            }
    
        } catch(Error e) {
            // Some Error
        	e.printStackTrace();
        }   	
    }
    
    public void writeToFile(String output) {
        // Permanently write new JSONObject to file
    	try {
        	FileWriter file = new FileWriter(output, false);
        	this.Playlists.writeJSONString(file);
        	file.flush();
        	file.close();
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
    	}
    }
    
    private void changePlaylist(JSONObject change) {
    	if (change.get("user_id") == null | change.get("id") == null || change.get("song_ids") == null) {
    		throw new Error("The change object doesn't have the correct format");
    	} 
    	
    	// At this point, I'd make sure user_id and id are both existing id's before proceeding
    	// for the sake of this project, I'll assume they are
    	
        for (int i = 0; i < this.Playlists.size(); i++) {
        	JSONObject playlist = (JSONObject) this.Playlists.get(i);
        	
        	if (change.get("user_id").equals(playlist.get("user_id")) && change.get("id").equals(playlist.get("id"))) {
        		JSONArray songChanges = (JSONArray) change.get("song_ids");
        		
        		if (songChanges.size() == 0) {
        			// song_ids is empty - remove playlist
        			this.Playlists.remove(i);
        		} else {        	
        			// song_ids not empty, modify current record
        			this.Playlists.set(i,  change);
        		}
        		return;
        	}
        }
        // Change id doesn't exist, add it here
        this.Playlists.add(change);        
    }

    public static void main(String[] args) {
    	    	
    	try {
    		DBMusicManager db = new DBMusicManager(args[0], args[1], args[2]);
    	} catch(FileNotFoundException f) {
    		// handle file not found
    		f.printStackTrace();
    	} catch(IOException ioe) {
    		// Handle io exception
    		ioe.printStackTrace();
    	} catch (ParseException pe) {
			// TODO Auto-generated catch block
			pe.printStackTrace();
		}

    }
}
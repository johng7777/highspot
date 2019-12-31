This is an eclipse project written in Java that takes three arguments:

     arg[0] = The JSON file containing ALL data provided by HighSpot in JSON format (mixtape-data.json)
	 arg[1] = The JSON file of the changes for the playlist array in arg[0]  (change.json)
	 arg[2] = The name of the output file containing the final playlist array after the changes have been applied
	 
	 Provided is the entire eclipse project as well as the executable jar file. Here is the commandline that will run the code:
	 
		HighspotMusicPlaylist.jar src/mixtape-data.json src/change.json output.json
		
		ALL source code is included in the file DBMusicManager.java
		
		I downloaded json-simple-1.1.jar for JSONArray and JSONObject class use in the code. It is included in src directory
		
		Also note for a Delete, I used an empty array notation for "song_ids" : [] for the program instruction to delete the playlist
		

Changes needed to scale this application:

	First of all, there are different "tiers" of scaling. Beginning with same "file input", "file output", to scale up:
	
		1) Create a hashmap of the playlist so it would be O(1) lookup and change
		2) Restructure data to have each user_id in the hashmap have their own "Playlists" so the accessed records wouldn't be the entire dataset
			This would create O(1) + O(n) where n represented only the number of playlists a user had
		3) Depending on size, possibly create multiple indexes for quick access to the records
		4) Possibly sort the change list according to user_id and if more than one change per user existed, consolidate. This would allow for streaming output as opposed to
			one giant write at the end. This would allow for clearing memory along the way.
			
	For a "system" of playlists on an enterprise level:
		1) Store the data in mongo collections behind an api gateway
		2) Turn the CRUD operations into REST api's (such as PUT and POST operations on <someurl>/api/playlists with java array input)
		3) Restructure the data to separate users, songs, and playlists into separate collection
		
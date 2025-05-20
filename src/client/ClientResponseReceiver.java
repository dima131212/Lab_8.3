package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.dataStorage.CollectionView;
import client.dataStorage.CurrentMovie;

public class ClientResponseReceiver {
	private final ObjectInputStream in;

    public ClientResponseReceiver(ObjectInputStream in) {
        this.in = in;
    }

    public synchronized void getResponce() throws ClassNotFoundException, IOException {
    	String response = (String) in.readObject();
    	if (response.startsWith("NextPage")) {
    	    response = response.replaceFirst("NextPage ", "");

    	    List<String> lines = List.of(response.split("\\n"));

    	    Map<Long, String> movieMap = new HashMap<>(); // LinkedHashMap сохраняет порядок

    	    for (String line : lines) {
    	        line = line.trim();
    	        if (line.isEmpty()) continue;

    	        int spaceIndex = line.indexOf(' ');
    	        if (spaceIndex == -1) {
    	            System.out.println("Invalid movie line: " + line);
    	            continue;
    	        }

    	        try {
    	            Long id = Long.parseLong(line.substring(0, spaceIndex));
    	            String name = line.substring(spaceIndex + 1);

    	            movieMap.put(id, name);
    	        } catch (NumberFormatException e) {
    	            System.out.println("Invalid id in line: " + line);
    	        }
    	    }
    	    movieMap.forEach((id, name) -> System.out.println(id + " -> " + name));
    	}
    	else if(response.startsWith("ThisMovie")) {
    		response = response.replaceFirst("ThisMovie\n", "");
    		System.out.println(response);
    		parseMovie(response);
    	}
    	else {
    		System.out.println(response);
    	}
    }
    
    public Object getData() throws ClassNotFoundException, IOException {
		Object data = (Object) in.readObject();
    	return data;
    }
    
    
    
    public void parseMovie(String movie) {
    	String[] values = movie.split("\n");
    	CurrentMovie.setId(values[0]);
    	CurrentMovie.setName(values[1]);
    	CurrentMovie.setCoordinatesId(values[2]);
    	CurrentMovie.setCreationDate(values[3]);
    	CurrentMovie.setOscarsCount(values[4]);
    	CurrentMovie.setTotalBoxOffice (values[5]);
    	CurrentMovie.setUsaBoxOffice (values[6]);
    	CurrentMovie.setGenre(values[7]);
    	CurrentMovie.setOperatorId(values[8]);
    	CurrentMovie.setCreatedBy(values[9]);
    	CurrentMovie.setCoordinatesX(values[11]);
    	CurrentMovie.setCoordinatesY(values[12]);
    	CurrentMovie.setOperatorName(values[14]);
    	CurrentMovie.setOperatorHeight(values[15]);
    	CurrentMovie.setOperatorEyeColor(values[16]);
    	CurrentMovie.setOperatorHairColor(values[17]);
    	CurrentMovie.setOperatorNationality(values[18]);
    	CurrentMovie.setLocationId(values[19]);
    	CurrentMovie.setLocationX(values[21]);
    	CurrentMovie.setLocationY(values[22]);
    	CurrentMovie.setLocationZ(values[23]);
    	CurrentMovie.setLocationName(values[24]);
    }
}

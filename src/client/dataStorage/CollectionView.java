package client.dataStorage;



import java.util.HashMap;
import java.util.Map;


public class CollectionView {

	static private Map<Long, String> movieView = new HashMap<Long, String>();
	
	public CollectionView(HashMap<Long, String> movieView) {
		setMovieView(movieView);
		
	}

	static public Map<Long, String> getMovieView() {
		return movieView;
	}

	static public void setMovieView(HashMap<Long, String> movieView) {
		CollectionView.movieView = movieView;
	}

	
}

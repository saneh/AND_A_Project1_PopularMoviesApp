package in.lemonco.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Custom adapter for girdview
 */
public class CustomGridViewAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = CustomGridViewAdapter.class.getSimpleName();

    private static class ViewHolder{
        ImageView moviePoster;
    }
    public CustomGridViewAdapter(Activity context,ArrayList<Movie> movies){
        super(context,0,movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get item for this position
        Movie currentMovie = getItem(position);
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.grid_item_layout,parent,false);
            viewHolder.moviePoster = (ImageView)convertView.findViewById(R.id.moviePoster);
            convertView.setTag(viewHolder);
        }else {
        viewHolder = (ViewHolder) convertView.getTag();
    }
    //Populate ImageView with movie poster

           String imagePath = currentMovie.getmPosterPath();
        Picasso.with(getContext()).load(imagePath).into(viewHolder.moviePoster);
        return convertView;
    }
}

package info.nemoworks.lily.datamodel;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import info.nemoworks.lily.R;

/**
 * Created by cshuo on 15/9/7.
 */
public class ContentAdapter extends ArrayAdapter<String>{

    private int resourceId;

    public ContentAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null); }
        else {
            view = convertView;
        }
        String s = (String) getItem(position);
        TextView content = (TextView) view.findViewById(R.id.comment_content);
        content.setText(s);
        content.setTextColor(Color.BLACK);
        return view;
    }
}

package Files;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.xml.R;

import java.util.List;

public class FileArrayAdapter  extends ArrayAdapter<Item> {
    private Context c;
    private int id;
    private List<Item> items;

    public FileArrayAdapter(Context context, int textViewResourceId, List<Item> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        items = objects;
    }
    public Item getItem(int i)
    {
        return items.get(i);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View view = inflater.inflate(R.layout.list_files, null,false);

        final Item o = items.get(position);
        if (o != null) {
            TextView t1 = view.findViewById(R.id.TextViewItem);
            TextView t2 = view.findViewById(R.id.TextView02);
            TextView t3 = view.findViewById(R.id.TextViewDate);
            /* Take the ImageView from layout and set the city's image */
            ImageView imageCity = view.findViewById(R.id.fd_Icon1);
            String uri = "drawable/" + o.getImage();
            int imageResource = c.getResources().getIdentifier(uri, null, c.getPackageName());
            Drawable image = c.getResources().getDrawable(imageResource);
            imageCity.setImageDrawable(image);

            if(t1!=null)
                t1.setText(o.getName());
            if(t2!=null)
                t2.setText(o.getData());
            if(t3!=null)
                t3.setText(o.getDate());
        }
        return view;
    }
}

package dmitriy.deomin.download_youtube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;


public class Adapter_Youtube extends SimpleAdapter {

    private ArrayList<Map<String, Object>> results;
    private Context context;

    public Adapter_Youtube(Context context, ArrayList<Map<String, Object>> data, int resource, String[] from, int[] to)
    {
        super(context, data, resource, from, to);
        this.results = data;
        this.context = context;
    }

    public View getView(int position, View view, ViewGroup parent) {

        View v = view;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.delegat_yotube, null);
        }

        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.fon_news_adapter);
        linearLayout.setBackgroundColor((position % 2 == 0) ? v.getResources().getColor(R.color.col1) : v.getResources().getColor(R.color.col2));


        TextView text1 = (TextView) v.findViewById(R.id.textView_title);
        text1.setText(results.get(position).get("title").toString());

        TextView text2 = (TextView) v.findViewById(R.id.textView_type);
        text2.setText(results.get(position).get("tipe").toString());


        TextView text3 = (TextView) v.findViewById(R.id.textView_quality);
        text3.setText(results.get(position).get("quality").toString());

        TextView text4 = (TextView) v.findViewById(R.id.textView_size);
        text4.setText(results.get(position).get("size").toString());

        return v;
    }


}

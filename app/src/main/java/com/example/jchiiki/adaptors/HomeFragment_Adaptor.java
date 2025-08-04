package com.example.jchiiki.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jchiiki.R;
import com.example.jchiiki.items.HomePracticeOptions;

import java.util.ArrayList;

public class HomeFragment_Adaptor extends BaseAdapter {
    private Context context;
    private int layout;
    ArrayList<HomePracticeOptions> practiceOptionArrayList;

    public HomeFragment_Adaptor(Context context, int layout, ArrayList<HomePracticeOptions> practiceOptionArrayList) {
        this.context = context;
        this.layout = layout;
        this.practiceOptionArrayList = practiceOptionArrayList;
    }

    @Override
    public int getCount() {
        return practiceOptionArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class ViewHolder {
        ImageView practiceOptionIcon_ImageView;
        TextView practiceOption_TextView;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if(view == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            viewHolder.practiceOptionIcon_ImageView = view.findViewById(R.id.practiceOptionIcon_ImageView);
            viewHolder.practiceOption_TextView = view.findViewById(R.id.title_TextView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        HomePracticeOptions practiceOption = practiceOptionArrayList.get(i);

        viewHolder.practiceOptionIcon_ImageView.setImageResource(practiceOption.getPracticeOptionIcon());
        viewHolder.practiceOption_TextView.setText(practiceOption.getTitle());

        return view;
    }
}

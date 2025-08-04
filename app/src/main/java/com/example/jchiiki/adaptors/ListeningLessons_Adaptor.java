package com.example.jchiiki.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jchiiki.R;
import com.example.jchiiki.items.LessonChoices;

import java.util.ArrayList;

public class ListeningLessons_Adaptor extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<LessonChoices> lessonArrayList;

    public ListeningLessons_Adaptor(Context context, int layout, ArrayList<LessonChoices> lessonArrayList) {
        this.context = context;
        this.layout = layout;
        this.lessonArrayList = lessonArrayList;
    }

    @Override
    public int getCount() {
        return lessonArrayList.size();
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
        TextView lessonTitle_TextView;
        TextView lessonName_TextView;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if(view == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);
            viewHolder.lessonName_TextView = view.findViewById(R.id.lessonName_TextView);
            viewHolder.lessonTitle_TextView = view.findViewById(R.id.lessonTitle_TextView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        LessonChoices lessonChoices = lessonArrayList.get(i);

        viewHolder.lessonTitle_TextView.setText(lessonChoices.getLessonTitle());
        viewHolder.lessonName_TextView.setText(lessonChoices.getLessonName());


        return view;
    }
}

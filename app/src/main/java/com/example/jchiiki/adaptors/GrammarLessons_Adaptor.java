package com.example.jchiiki.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jchiiki.R;
import com.example.jchiiki.items.GrammarLessonChoices;
import com.example.jchiiki.items.LessonChoices;

import java.util.ArrayList;

public class GrammarLessons_Adaptor extends BaseAdapter {
    private Context context;
    private int layout;
    ArrayList<GrammarLessonChoices> grammarLessonArrayList;

    public GrammarLessons_Adaptor(Context context, int layout, ArrayList<GrammarLessonChoices> grammarLessonArrayList) {
        this.context = context;
        this.layout = layout;
        this.grammarLessonArrayList = grammarLessonArrayList;
    }


    @Override
    public int getCount() {
        return (grammarLessonArrayList != null) ? grammarLessonArrayList.size() : 0; // Nếu getCount() trả về 0, GridView sẽ không hiển thị gì, mặc dù dữ liệu trong lessonList có thể tồn tại
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder {
        TextView lessonTitle_TextView;
        TextView lessonName_TextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

//        View myView = null;
//
//        if(convertView != null){
//            myView = convertView;
//        }
//        else{
//            myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_item, parent, false);
//        }

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            viewHolder.lessonName_TextView = convertView.findViewById(R.id.lessonName_TextView);
            viewHolder.lessonTitle_TextView = convertView.findViewById(R.id.lessonTitle_TextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        GrammarLessonChoices lessonChoices = grammarLessonArrayList.get(position);

        viewHolder.lessonTitle_TextView.setText(lessonChoices.getLessonTitle());
        viewHolder.lessonName_TextView.setText(lessonChoices.getLessonName());

        return convertView;
    }
}
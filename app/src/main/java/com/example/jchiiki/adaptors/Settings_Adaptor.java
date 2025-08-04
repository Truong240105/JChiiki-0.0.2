package com.example.jchiiki.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jchiiki.R;
import com.example.jchiiki.items.SettingChoices;

import java.util.ArrayList;

public class Settings_Adaptor extends BaseAdapter {
    private Context context;
    private int layout;
    ArrayList<SettingChoices> settingArrayList;

    public Settings_Adaptor(Context context, int layout, ArrayList<SettingChoices> settingArrayList) {
        this.context = context;
        this.layout = layout;
        this.settingArrayList = settingArrayList;
    }


    @Override
    public int getCount() {
        return settingArrayList.size(); // Nếu getCount() trả về 0, GridView sẽ không hiển thị gì, mặc dù dữ liệu trong lessonList có thể tồn tại
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
        TextView settingName_TextView;
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

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            viewHolder.settingName_TextView = convertView.findViewById(R.id.settingName_TextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.settingName_TextView.setText(settingArrayList.get(position).getSettingName());

        return convertView;
    }
}
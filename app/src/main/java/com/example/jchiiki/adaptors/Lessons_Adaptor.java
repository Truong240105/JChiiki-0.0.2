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
import com.example.jchiiki.items.LessonChoices;

import java.util.ArrayList;

public class Lessons_Adaptor extends BaseAdapter {
    private Context context;
    private int layout;
    ArrayList<LessonChoices> lessonArrayList;

    public Lessons_Adaptor(Context context, int layout,ArrayList<LessonChoices> lessonArrayList) {
        this.context = context;
        this.layout = layout;
        this.lessonArrayList = lessonArrayList;
    }


    @Override
    public int getCount() {
        Log.d("lessonArrayList.size()",lessonArrayList.size()+"");
        return (lessonArrayList != null) ? lessonArrayList.size() : 0; // Nếu getCount() trả về 0, GridView sẽ không hiển thị gì, mặc dù dữ liệu trong lessonList có thể tồn tại
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

        LessonChoices lessonChoices = lessonArrayList.get(position);

        if(lessonChoices == null) Log.d("Lessons_Adaptor: lessonChoices", "null");

        viewHolder.lessonTitle_TextView.setText(lessonChoices.getLessonTitle());
        viewHolder.lessonName_TextView.setText(lessonChoices.getLessonName());

        return convertView;
    }


    /*
    public void updateLessonNumber(int newLessonNumber) {
        this.lessonNumber = newLessonNumber;
        notifyDataSetChanged();
    }

    public void loadLessonData(final MyCompleListenerWithData callback) {
        updateLessonNumber(lessonNumber); // Cập nhật lại số lượng bài học
        Log.d("onSuccess: lessonNumber", lessonNumber + "");
        callback.onSuccess(DBQuery.g_lessonNumber); // Gọi callback khi dữ liệu đã sẵn sàng

        //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        //callback.onFailure(e); // Gọi callback nếu có lỗi
    }


    public class Lessons_ASync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("doInBackground", "accessed SUCCESSFULLY");
            loadLessonData(new MyCompleListenerWithData() {
                @Override
                public void onSuccess(long data) {
                    // Dữ liệu đã được tải xong, không cần làm gì thêm ở đây
                }

                @Override
                public void onFailure(Exception e) {
                    // Xử lý lỗi nếu cần
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("onPostExecute", "accessed SUCCESSFULLY");
            notifyDataSetChanged(); // Cập nhật Adapter sau khi dữ liệu đã tải xong
        }
    }
    */
}
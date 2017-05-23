package kr.co.appcode.teamcloud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chad on 2017-04-30.
 */

public class FileGridAdapter extends BaseAdapter {

    private HomeFragment fragment;
    private ArrayList<Content> contentList;

    ViewHolder holder;

    public FileGridAdapter(HomeFragment fragment, ArrayList<Content> contentList) {
        this.fragment = fragment;
        this.contentList = contentList;

    }

    public ArrayList<Content> getContentList() {
        return contentList;
    }

    public void setContentList(ArrayList<Content> contentList) {
        this.contentList = contentList;
    }

    public void add(Content item) {
        contentList.add(item);
    }

    @Override
    public int getCount() {
        return contentList.size();
    }

    @Override
    public Object getItem(int position) {
        return contentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_latest_file, null);

            holder = new ViewHolder();

            holder.textName = (TextView) convertView.findViewById(R.id.text_name);
            holder.textDate = (TextView) convertView.findViewById(R.id.text_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Content content = contentList.get(position);

        holder.textName.setText(content.getFileName());
        holder.textDate.setText(content.getWriteDate());

        return convertView;
    }

    static class ViewHolder {
        TextView textName;
        TextView textDate;
    }

}

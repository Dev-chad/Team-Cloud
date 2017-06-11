package kr.co.appcode.teamcloud;

import android.app.Fragment;
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

public class ContentsListAdapter extends BaseAdapter {

    private Fragment fragment;
    private ArrayList<Content> contentList;

    ViewHolder holder;

    public ContentsListAdapter(Fragment fragment, ArrayList<Content> contentLists) {
        this.fragment = fragment;
        this.contentList = contentLists;
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
            convertView = inflater.inflate(R.layout.list_contents, null);

            holder = new ViewHolder();

            holder.textContentTitle = (TextView) convertView.findViewById(R.id.text_title);
            holder.textWriter = (TextView) convertView.findViewById(R.id.text_writer);
            holder.textDate = (TextView) convertView.findViewById(R.id.text_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Content content = contentList.get(position);

        holder.textContentTitle.setText(content.getTitle());
        holder.textWriter.setText(content.getWriter());
        holder.textDate.setText(content.getWriteDate());

        return convertView;
    }

    static class ViewHolder {
        TextView textContentTitle;
        TextView textWriter;
        TextView textDate;
    }

}

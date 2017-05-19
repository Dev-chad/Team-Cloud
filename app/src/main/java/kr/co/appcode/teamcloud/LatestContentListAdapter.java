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

public class LatestContentListAdapter extends BaseAdapter {

    private HomeFragment fragment;
    private ArrayList<LatestContentItem> latestContentList;

    ViewHolder holder;

    public LatestContentListAdapter(HomeFragment fragment, ArrayList<LatestContentItem> latestContentList) {
        this.fragment = fragment;
        this.latestContentList = latestContentList;
    }

    public ArrayList<LatestContentItem> getLatestContentList() {
        return latestContentList;
    }

    public void setLatestContentList(ArrayList<LatestContentItem> latestContentList) {
        this.latestContentList = latestContentList;
    }

    public void add(LatestContentItem item) {
        latestContentList.add(item);
    }

    @Override
    public int getCount() {
        return latestContentList.size();
    }

    @Override
    public Object getItem(int position) {
        return latestContentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_latest_content, null);

            holder = new ViewHolder();

            holder.textContentTitle = (TextView) convertView.findViewById(R.id.text_title);
            holder.textWriter = (TextView) convertView.findViewById(R.id.text_writer);
            holder.textDate = (TextView) convertView.findViewById(R.id.text_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LatestContentItem currentItem = latestContentList.get(position);

        holder.textContentTitle.setText(currentItem.getTitle());
        holder.textWriter.setText(currentItem.getWriter());
        holder.textDate.setText(currentItem.getDate());

        return convertView;
    }

    static class ViewHolder {
        TextView textContentTitle;
        TextView textWriter;
        TextView textDate;
    }

}

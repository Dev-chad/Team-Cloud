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

public class LatestFileGridAdapter extends BaseAdapter {

    private HomeFragment fragment;
    private ArrayList<LatestFileItem> latestFileList;

    ViewHolder holder;

    public LatestFileGridAdapter(HomeFragment fragment, ArrayList<LatestFileItem> latestFileList) {
        this.fragment = fragment;
        this.latestFileList = latestFileList;

    }

    public ArrayList<LatestFileItem> getLatestFileList() {
        return latestFileList;
    }

    public void setLatestFileList(ArrayList<LatestFileItem> latestFileList) {
        this.latestFileList = latestFileList;
    }

    public void add(LatestFileItem item) {
        latestFileList.add(item);
    }

    @Override
    public int getCount() {
        return latestFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return latestFileList.get(position);
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

        LatestFileItem currentItem = latestFileList.get(position);

        holder.textName.setText(currentItem.getName());
        holder.textDate.setText(currentItem.getDate());

        return convertView;
    }

    static class ViewHolder {
        TextView textName;
        TextView textDate;
    }

}

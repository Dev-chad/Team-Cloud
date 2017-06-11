package kr.co.appcode.teamcloud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Chad on 2017-04-30.
 */

public class FileListAdapter extends BaseAdapter {

    private HomeFragment fragment;
    private ArrayList<Content> fileList;

    ViewHolder holder;

    public FileListAdapter(HomeFragment fragment, ArrayList<Content> fileList) {
        this.fragment = fragment;
        this.fileList = fileList;
    }

    public ArrayList<Content> getFileList() {
        return fileList;
    }

    public void setFileList(ArrayList<Content> fileList) {
        this.fileList = fileList;
    }

    public void add(Content item) {
        fileList.add(item);
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_latest_file, null);

            holder = new ViewHolder();

            holder.textName = (TextView) convertView.findViewById(R.id.text_name);
            holder.textSize = (TextView) convertView.findViewById(R.id.text_size);
            holder.textDate = (TextView) convertView.findViewById(R.id.text_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Content content = fileList.get(position);

        holder.textName.setText(content.getFileName());
        holder.textSize.setText(getCapacity(content.getFileSize()));
        holder.textDate.setText(content.getWriteDate());

        return convertView;
    }

    static class ViewHolder {
        TextView textName;
        TextView textSize;
        TextView textDate;
    }

    public String getCapacity(long fileSize) {
        double dSize = Double.parseDouble(String.valueOf(fileSize));
        String unit;
        int count = 0;

        while (dSize >= 1024 && count < 5) {
            dSize /= 1024;
            count++;
        }

        if (count == 0) {
            unit = "B";
        } else if (count == 1) {
            unit = "KB";
        } else if (count == 2) {
            unit = "MB";
        } else if (count == 3) {
            unit = "GB";
        } else {
            unit = "TB";
        }

        return String.format(Locale.KOREA, "%.2f %s", dSize, unit);
    }

}

package kr.co.appcode.teamcloud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chad on 2017-05-27.
 */

public class UploadFileListAdapter extends BaseAdapter {
    private FileBrowserActivity context;

    private ViewHolder holder;
    private ArrayList<UploadFile> fileList;

    public UploadFileListAdapter(FileBrowserActivity context, ArrayList<UploadFile> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    public void add(UploadFile file) {
        fileList.add(file);
    }

    public void setFileList(ArrayList<UploadFile> fileList) {
        this.fileList = fileList;
    }

    public ArrayList<UploadFile> getFileList() {
        return fileList;
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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_file, null);
            holder = new ViewHolder();
            holder.textFileName = (TextView) convertView.findViewById(R.id.text_file_name);
            holder.textFileSize = (TextView) convertView.findViewById(R.id.text_file_size);
            holder.textFileDate = (TextView) convertView.findViewById(R.id.text_file_date);
            holder.textInnerCount = (TextView) convertView.findViewById(R.id.text_inner_count);
            holder.imageFile = (ImageView) convertView.findViewById(R.id.image_file);
            holder.layoutFileInfo = (LinearLayout)convertView.findViewById(R.id.layout_file_info);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final UploadFile file = fileList.get(position);

        holder.textFileName.setText(file.getFileName());

        if(file.getFileType() == UploadFile.TYPE_DIRECTORY){
            holder.textInnerCount.setVisibility(View.VISIBLE);
            holder.imageFile.setImageResource(R.mipmap.ic_directory);
            holder.layoutFileInfo.setVisibility(View.GONE);
            holder.textInnerCount.setText(String.valueOf(file.getInnerCount()+"개"));
        } else {
            holder.imageFile.setImageResource(R.mipmap.ic_file);
            holder.layoutFileInfo.setVisibility(View.VISIBLE);
            holder.textFileDate.setText(file.getFileDate());
            holder.textFileSize.setText(file.getFileSize());
            holder.textInnerCount.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView textFileName;
        TextView textFileDate;
        TextView textFileSize;
        TextView textInnerCount;
        LinearLayout layoutFileInfo;
        ImageView imageFile;
    }
}

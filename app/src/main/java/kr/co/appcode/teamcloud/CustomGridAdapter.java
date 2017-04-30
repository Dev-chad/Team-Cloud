package kr.co.appcode.teamcloud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chad on 2017-04-30.
 */

public class CustomGridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> teamList;

    ViewHolder holder;

    public CustomGridAdapter(Context context, ArrayList<String> teamList){
        this.context = context;
        this.teamList= teamList;
    }

    @Override
    public int getCount() {
        return teamList.size();
    }

    @Override
    public Object getItem(int position) {
        return teamList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_team, null);
            holder = new ViewHolder();
            holder.imageTeam = (ImageView) convertView.findViewById(R.id.image_team);
            holder.textTeamName = (TextView) convertView.findViewById(R.id.text_team_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.imageTeam.setImageResource(R.mipmap.default_team);
        holder.textTeamName.setText(teamList.get(position));

        return convertView;
    }

    static class ViewHolder{
        ImageView imageTeam;
        TextView textTeamName;
    }

}

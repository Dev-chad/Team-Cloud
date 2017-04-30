package kr.co.appcode.teamcloud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chad on 2017-04-30.
 */

public class CustomTeamListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Team> teamList;

    ViewHolder holder;

    public CustomTeamListAdapter(Context context){
        this.context = context;
    }

    public CustomTeamListAdapter(Context context, ArrayList<Team> teamList){
        this.context = context;
        this.teamList= teamList;
    }

    public ArrayList<Team> getTeamList() {
        return teamList;
    }

    public void setTeamList(ArrayList<Team> teamList) {
        this.teamList = teamList;
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
            convertView = inflater.inflate(R.layout.list_team, null);
            holder = new ViewHolder();
            holder.imageTeam = (ImageView) convertView.findViewById(R.id.image_team);
            holder.textTeamName = (TextView) convertView.findViewById(R.id.text_team_name);
            holder.textMasterName = (TextView) convertView.findViewById(R.id.text_master);
            holder.btnJoin = (Button)convertView.findViewById(R.id.btn_join);
            holder.btnjoined = (Button)convertView.findViewById(R.id.btn_joined);
            holder.btnJoining = (Button)convertView.findViewById(R.id.btn_joining);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Team currentTeam = teamList.get(position);

        holder.imageTeam.setImageResource(R.mipmap.default_team);
        holder.textTeamName.setText(currentTeam.getTeamName());
        holder.textMasterName.setText(currentTeam.getMasterName());
        if(currentTeam.getLevel() == -1){
            holder.btnJoin.setVisibility(View.VISIBLE);
            holder.btnjoined.setVisibility(View.GONE);
            holder.btnJoining.setVisibility(View.GONE);
        } else if(currentTeam.getLevel() == 0){
            holder.btnJoin.setVisibility(View.GONE);
            holder.btnjoined.setVisibility(View.GONE);
            holder.btnJoining.setVisibility(View.VISIBLE);
        } else{
            holder.btnJoin.setVisibility(View.GONE);
            holder.btnjoined.setVisibility(View.VISIBLE);
            holder.btnJoining.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder{
        ImageView imageTeam;
        TextView textTeamName;
        TextView textMasterName;
        Button btnJoin;
        Button btnjoined;
        Button btnJoining;
    }

}

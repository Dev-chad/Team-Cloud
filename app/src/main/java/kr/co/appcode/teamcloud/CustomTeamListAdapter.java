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
import java.util.HashMap;

/**
 * Created by Chad on 2017-04-30.
 */

public class CustomTeamListAdapter extends BaseAdapter {

    private SearchActivity context;
    private User user;
    private ArrayList<SearchListItem> searchListItemList;
    private int max;
    private int currentPos;

    ViewHolder holder;

    public CustomTeamListAdapter(SearchActivity context, ArrayList<SearchListItem> searchListItemList, int max, User user){
        this.context = context;
        this.searchListItemList = searchListItemList;
        this.max = max;
        this.user = user;
    }

    public ArrayList<SearchListItem> getSearchListItemList() {
        return searchListItemList;
    }

    public void setSearchListItemList(ArrayList<SearchListItem> searchListItemList) {
        this.searchListItemList = searchListItemList;
    }

    public void addTeam(SearchListItem searchListItem){
        searchListItemList.add(searchListItem);
    }

    public void addTeamList(ArrayList<SearchListItem> searchListItemList){
        this.searchListItemList.addAll(searchListItemList);
    }

    public void setMax(int max){
        this.max = max;
    }

    public int getMax(){
        return max;
    }

    public void setCurrentPos(int currentPos){
        this.currentPos = currentPos;
    }

    public int getCurrentPos(){
        return currentPos;
    }

    @Override
    public int getCount() {
        return searchListItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchListItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_team, null);
            holder = new ViewHolder();
            holder.imageTeam = (ImageView) convertView.findViewById(R.id.image_team);
            holder.textTeamName = (TextView) convertView.findViewById(R.id.text_team_name);
            holder.textMasterName = (TextView) convertView.findViewById(R.id.text_master);
            holder.btnJoin = (Button)convertView.findViewById(R.id.btn_join);

            holder.btnJoined = (Button)convertView.findViewById(R.id.btn_joined);
            holder.btnJoining = (Button)convertView.findViewById(R.id.btn_joining);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        SearchListItem currentSearchListItem = searchListItemList.get(position);

        holder.imageTeam.setImageResource(R.mipmap.default_team);
        holder.textTeamName.setText(currentSearchListItem.getTeamName());
        holder.textMasterName.setText(currentSearchListItem.getMasterName());

        holder.btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> values = new HashMap<>();
                values.put("nickname", user.getNickname());
                values.put("teamName", searchListItemList.get(position).getTeamName());
                values.put("sessionInfo", user.getSessionInfo());

                currentPos = position;

                HttpConnection httpConnection = new HttpConnection(context, values, context.httpCallBack);
                httpConnection.setCheckSession(true);
                httpConnection.setMode(HttpConnection.MODE_JOIN_TEAM);
                httpConnection.execute();
            }
        });

        if(currentSearchListItem.getLevel() == -1){
            holder.btnJoin.setVisibility(View.VISIBLE);
            holder.btnJoined.setVisibility(View.GONE);
            holder.btnJoining.setVisibility(View.GONE);
        } else if(currentSearchListItem.getLevel() == 0){
            holder.btnJoin.setVisibility(View.GONE);
            holder.btnJoined.setVisibility(View.GONE);
            holder.btnJoining.setVisibility(View.VISIBLE);
        } else{
            holder.btnJoin.setVisibility(View.GONE);
            holder.btnJoined.setVisibility(View.VISIBLE);
            holder.btnJoining.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder{
        ImageView imageTeam;
        TextView textTeamName;
        TextView textMasterName;
        Button btnJoin;
        Button btnJoined;
        Button btnJoining;
    }

}

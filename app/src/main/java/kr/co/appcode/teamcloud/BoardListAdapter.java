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

public class BoardListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Board> boardList;

    ViewHolder holder;

    public BoardListAdapter(Context context, ArrayList<Board> boardList){
        this.context = context;
        this.boardList = boardList;
    }

    public void add(Board board){
        boardList.add(board);
    }


    @Override
    public int getCount() {
        return boardList.size();
    }

    @Override
    public Object getItem(int position) {
        return boardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_board, null);
            holder = new ViewHolder();
            holder.boardName = (TextView) convertView.findViewById(R.id.text_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.boardName.setText(boardList.get(position).getName());
        return convertView;
    }

    static class ViewHolder{
        TextView boardName;
    }

}

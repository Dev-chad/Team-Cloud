package kr.co.appcode.teamcloud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chad on 2017-04-30.
 */

public class UserAdapter extends BaseAdapter {

    private MemberManageActivity activity;
    private ArrayList<User> userList;

    ViewHolder holder;

    public UserAdapter(MemberManageActivity activity, ArrayList<User> userList) {
        this.activity = activity;
        this.userList = userList;
    }

    public void add(User user) {
        userList.add(user);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_latest_content, null);

            holder = new ViewHolder();

            holder.btnSetting = (Button) convertView.findViewById(R.id.btn_setting);
            holder.textNickname = (TextView) convertView.findViewById(R.id.text_nickname);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = userList.get(position);

        holder.textNickname.setText(user.getNickname());
        return convertView;
    }

    static class ViewHolder {
        TextView textNickname;
        Button btnSetting;
    }
}

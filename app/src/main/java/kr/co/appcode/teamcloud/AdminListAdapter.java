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
 * Created by Chad on 2017-05-27.
 */

public class AdminListAdapter extends BaseAdapter {
    private static final int MODE_NORMAL = 1;
    private static final int MODE_ADMIN = 2;

    private AdminManageActivity context;

    private ViewHolder holder;
    private ArrayList<Member> memberList;
    private int currentPos;
    private int mode;
    private String teamIdx;

    public AdminListAdapter(AdminManageActivity context, ArrayList<Member> memberList, String teamIdx, int mode) {
        this.context = context;
        this.memberList = memberList;
        this.mode = mode;
        this.teamIdx = teamIdx;
    }

    public void add(Member member) {
        memberList.add(member);
    }

    public void setMemberList(ArrayList<Member> memberList) {
        this.memberList = memberList;
    }

    public ArrayList<Member> getMemberList() {
        return memberList;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_member, null);
            holder = new ViewHolder();
            holder.textNickname = (TextView) convertView.findViewById(R.id.text_nickname);
            holder.textAlignTarget = (TextView) convertView.findViewById(R.id.text_const_align_target);
            holder.textTargetValue = (TextView) convertView.findViewById(R.id.text_target_value);
            holder.imgOption = (ImageView) convertView.findViewById(R.id.image_option);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Member member = memberList.get(position);

        holder.textNickname.setText(member.getNickname());

        holder.imgOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminPermDialog dialog = new AdminPermDialog(context, teamIdx, member);
                dialog.show();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView textNickname;
        TextView textAlignTarget;
        TextView textTargetValue;
        ImageView imgOption;
    }
}

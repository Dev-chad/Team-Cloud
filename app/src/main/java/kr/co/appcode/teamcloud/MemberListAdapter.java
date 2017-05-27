package kr.co.appcode.teamcloud;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chad on 2017-05-27.
 */

public class MemberListAdapter extends BaseAdapter {
    private static final int MODE_NORMAL = 1;
    private static final int MODE_ADMIN = 2;

    private MemberManageActivity context;

    private ViewHolder holder;
    private ArrayList<Member> memberList;
    private int currentPos;
    private int mode;
    private String teamIdx;

    public MemberListAdapter(MemberManageActivity context, ArrayList<Member> memberList, String teamIdx, int mode) {
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

                PopupMenu popupMenu = new PopupMenu(context, v);
                MenuInflater menuInflater = popupMenu.getMenuInflater();

                final String body = "nickname=" + member.getNickname() + "&teamIdx=" + teamIdx;

                if (mode == MODE_NORMAL) {
                    menuInflater.inflate(R.menu.menu_normal_member, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int itemId = item.getItemId();

                            AlertDialog.Builder checkDialog = new AlertDialog.Builder(context);

                            if (itemId == R.id.appoint_admin) {

                                checkDialog
                                        .setTitle("관리자 임명")
                                        .setMessage("정말로 " + member.getNickname() + " 멤버를 관리자로 임명하시겠습니까?");

                                checkDialog.setPositiveButton("임명하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentPos = position;

                                        HttpConnection httpConnection = new HttpConnection(context, body+"&mode=2", "manageMember.php", context.httpCallBack);
                                        httpConnection.execute();

                                        dialog.dismiss();
                                    }
                                });

                                checkDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                checkDialog.show();
                            } else if (itemId == R.id.banish) {
                                checkDialog
                                        .setTitle("팀원 내보내기")
                                        .setMessage("정말로 " + member.getNickname() + " 멤버를 팀에서 제외시키겠습니까?");

                                checkDialog.setPositiveButton("내보내기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentPos = position;

                                        HttpConnection httpConnection = new HttpConnection(context, body+"&mode=5", "manageMember.php", context.httpCallBack);
                                        httpConnection.execute();

                                        dialog.dismiss();
                                    }
                                });

                                checkDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                checkDialog.show();
                            } else {

                            }
                            return false;
                        }
                    });
                } else {
                    menuInflater.inflate(R.menu.menu_admin_member, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int itemId = item.getItemId();

                            AlertDialog.Builder checkDialog = new AlertDialog.Builder(context);

                            if (itemId == R.id.appoint_master) {

                                checkDialog
                                        .setTitle("마스터 위임")
                                        .setMessage("정말로 " + member.getNickname() + " 멤버에게 마스터를 위임하시겠습니까?");

                                checkDialog.setPositiveButton("위임하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentPos = position;

                                        HttpConnection httpConnection = new HttpConnection(context, body+"&mode=4", "manageMember.php", context.httpCallBack);
                                        httpConnection.execute();

                                        dialog.dismiss();
                                    }
                                });

                                checkDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                checkDialog.show();
                            } else if (itemId == R.id.banish_admin) {
                                checkDialog
                                        .setTitle("관리자에서 제외")
                                        .setMessage("정말로 " + member.getNickname() + " 멤버를 관리자에서 제외시키겠습니까?");

                                checkDialog.setPositiveButton("제외하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentPos = position;

                                        HttpConnection httpConnection = new HttpConnection(context, body+"&mode=3", "manageMember.php", context.httpCallBack);
                                        httpConnection.execute();

                                        dialog.dismiss();
                                    }
                                });

                                checkDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                checkDialog.show();
                            } else {

                            }
                            return false;
                        }
                    });
                }

                popupMenu.show();
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

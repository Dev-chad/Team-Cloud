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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Chad on 2017-04-30.
 */

public class TeamListAdapter extends BaseAdapter {


    private SearchActivity context;
    private User user;
    private ArrayList<Team> teamList;
    private int max;
    private int currentPos;

    private ViewHolder holder;

    public TeamListAdapter(SearchActivity context, ArrayList<Team> teamList, int max, User user) {
        this.context = context;
        this.teamList = teamList;
        this.max = max;
        this.user = user;
    }

    public ArrayList<Team> getTeamList() {
        return teamList;
    }

    public void setTeamList(ArrayList<Team> teamList) {
        this.teamList = teamList;
    }

    public void addTeam(Team searchListItem) {
        teamList.add(searchListItem);
    }

    public void addTeamList(ArrayList<Team> searchListItemList) {
        this.teamList.addAll(searchListItemList);
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public int getCurrentPos() {
        return currentPos;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_team, null);
            holder = new ViewHolder();
            holder.imageTeam = (ImageView) convertView.findViewById(R.id.image_team);
            holder.textTeamName = (TextView) convertView.findViewById(R.id.text_team_name);
            holder.textMasterName = (TextView) convertView.findViewById(R.id.text_master);
            holder.btnJoin = (Button) convertView.findViewById(R.id.btn_join);
            holder.btnJoined = (Button) convertView.findViewById(R.id.btn_joined);
            holder.btnJoining = (Button) convertView.findViewById(R.id.btn_joining);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Team team = teamList.get(position);

        holder.imageTeam.setImageResource(R.mipmap.default_team);
        holder.textTeamName.setText(team.getName());
        holder.textMasterName.setText(team.getMaster());

        holder.btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String teamName = teamList.get(position).getName();

                AlertDialog.Builder checkDialog = new AlertDialog.Builder(context);

                checkDialog
                        .setTitle("팀 가입 신청")
                        .setMessage(teamName + " 팀에 가입하시겠습니까?");

                checkDialog.setPositiveButton("가입하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentPos = position;
                        String body = "nickname=" + user.getNickname() + "&teamIdx=" + team.getIdx();

                        HttpConnection httpConnection = new HttpConnection(context, body, "joinTeam.php", context.httpCallBack);
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
            }
        });

        holder.btnJoining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                MenuInflater menuInflater = popupMenu.getMenuInflater();

                menuInflater.inflate(R.menu.menu_applying_join, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final String teamName = team.getName();

                        if (item.getItemId() == R.id.join_cancel) {
                            AlertDialog.Builder checkDialog = new AlertDialog.Builder(context);

                            checkDialog
                                    .setTitle("팀 가입 요청 취소")
                                    .setMessage("정말로 " + teamName + " 팀 가입 요청을 취소하시겠습니까?");

                            checkDialog.setPositiveButton("취소하기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    currentPos = position;
                                    String body = "nickname=" + user.getNickname() + "&teamIdx=" + team.getIdx() + "&mode=1";

                                    HttpConnection httpConnection = new HttpConnection(context, body, "leaveTeam.php", context.httpCallBack);
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
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        holder.btnJoined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                MenuInflater menuInflater = popupMenu.getMenuInflater();

                menuInflater.inflate(R.menu.menu_joined, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final String teamName = teamList.get(position).getName();

                        int id = item.getItemId();

                        if (id == R.id.move_team_page) {

                        } else if (id == R.id.unjoin_team) {
                            if (teamList.get(position).getMaster().equals(user.getNickname())) {
                                AlertDialog.Builder checkDialog = new AlertDialog.Builder(context);

                                checkDialog
                                        .setTitle("팀 탈퇴")
                                        .setMessage("팀의 마스터는 탈퇴가 불가능합니다.");

                                checkDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                checkDialog.show();
                            } else {
                                AlertDialog.Builder checkDialog = new AlertDialog.Builder(context);

                                checkDialog
                                        .setTitle("팀 탈퇴")
                                        .setMessage("정말로 " + teamName + " 팀을 탈퇴하시겠습니까?");

                                checkDialog.setPositiveButton("탈퇴하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentPos = position;
                                        String body = "nickname=" + user.getNickname() + "&teamIdx=" + team.getIdx() + "&mode=2";

                                        HttpConnection httpConnection = new HttpConnection(context, body, "leaveTeam.php", context.httpCallBack);
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
                            }
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        if (team.getLevel() == -1) {
            holder.btnJoin.setVisibility(View.VISIBLE);
            holder.btnJoined.setVisibility(View.GONE);
            holder.btnJoining.setVisibility(View.GONE);
        } else if (team.getLevel() == 0) {
            holder.btnJoin.setVisibility(View.GONE);
            holder.btnJoined.setVisibility(View.GONE);
            holder.btnJoining.setVisibility(View.VISIBLE);
        } else {
            holder.btnJoin.setVisibility(View.GONE);
            holder.btnJoined.setVisibility(View.VISIBLE);
            holder.btnJoining.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView imageTeam;
        TextView textTeamName;
        TextView textMasterName;
        Button btnJoin;
        Button btnJoined;
        Button btnJoining;
    }

}

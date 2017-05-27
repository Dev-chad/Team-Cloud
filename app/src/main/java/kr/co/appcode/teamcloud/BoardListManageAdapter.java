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
 * Created by Chad on 2017-04-30.
 */

public class BoardListManageAdapter extends BaseAdapter {

    private ManageBoardActivity context;
    private ArrayList<Board> boardList;
    private int currentPos;
    private String teamIdx;

    ViewHolder holder;

    public BoardListManageAdapter(ManageBoardActivity context, String teamIdx, ArrayList<Board> boardList){
        this.context = context;
        this.boardList = boardList;
        this.teamIdx = teamIdx;
    }

    public void add(Board board){
        boardList.add(board);
    }

    public ArrayList<Board> getBoardList(){
        return boardList;
    }

    public void setBoardList(ArrayList<Board> boardList){
        this.boardList = boardList;
    }

    public int getCurrentPos(){
        return currentPos;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_team_board, null);
            holder = new ViewHolder();
            holder.boardName = (TextView) convertView.findViewById(R.id.text_board_name);
            holder.imageOption = (ImageView) convertView.findViewById(R.id.image_option);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final Board board = boardList.get(position);

        holder.boardName.setText(board.getName());
        holder.imageOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                MenuInflater menuInflater = popupMenu.getMenuInflater();

                menuInflater.inflate(R.menu.menu_board, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();

                        AlertDialog.Builder checkDialog = new AlertDialog.Builder(context);

                        if (itemId == R.id.edit_board) {
                            BoardEditDialog boardEditDialog = new BoardEditDialog(context, teamIdx, board);
                            boardEditDialog.show();

                        } else if (itemId == R.id.remove_board) {
                            if(boardList.size() > 1){
                                checkDialog
                                        .setTitle("게시판 삭제")
                                        .setMessage("정말로 \"" + board.getName() + "\" 게시판을 삭제 하시겠습니까?");

                                checkDialog.setPositiveButton("삭제하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentPos = position;

                                        HttpConnection httpConnection = new HttpConnection("teamIdx="+teamIdx+"&boardName="+board.getName()+"&mode=4", "setBoard.php", context.httpCallBack);
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
                                checkDialog
                                        .setTitle("게시판 삭제")
                                        .setMessage("게시판은 한 개 이상 있어야 합니다.");

                                checkDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
        return convertView;
    }

    static class ViewHolder{
        TextView boardName;
        ImageView imageOption;
    }

}

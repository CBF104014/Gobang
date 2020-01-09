package com.example.cat.gobang;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class main extends AppCompatActivity implements View.OnTouchListener,DialogInterface.OnClickListener {
    ImageView bo,b,w,point;
    TextView tx1,tx2,tx3;;
    ConstraintLayout c1;
    AlertDialog.Builder alert;
    boolean bb,win;
    float xx[]= new float[13];
    float yy[] = new float[13];
    int size = 13,num=0,WandH = 55,PlayerToPlayer=0,whofirst=0,ai=0,player=0; //num > comput回傳棋子代碼1(黑)or2(白)獲勝  (0表示尚未連成5顆)
    int board[][]= new int[13][13]; // 0:未下棋 1:黑棋 2:白棋(AI)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bb = true;  win = false;   num=0;
        c1= (ConstraintLayout)findViewById(R.id.c1);
        tx2  = findViewById(R.id.tx2);
        tx3  = findViewById(R.id.tx3);
        bo = findViewById(R.id.board);
        bo.setOnTouchListener(this);
        tx1 = findViewById(R.id.tx1);
        init();
        Intent it = getIntent();
        PlayerToPlayer = it.getIntExtra("Player",0); //玩家對玩家 > 1
        whofirst = it.getIntExtra("priority",0); //玩家先手 > 1 > 黑棋 ,  玩家後手 > 2 > 白棋
        tx1.setText("●○●AI對弈模式●○●");
        if(whofirst==1 && PlayerToPlayer !=1){tx2.setText("黑棋進攻"); player=1; ai = 2;
        }else if(PlayerToPlayer !=1){
            tx2.setText("白棋進攻"); player = 2; ai=1;
            b = new ImageView(this);
            b.setImageResource(R.mipmap.black);
            b.setX(xx[6]);  b.setY(yy[6]);
            c1.addView(b,WandH,WandH);
            bb = true;
            board[6][6] = ai;
            c1.removeView(point);
            point = new ImageView(this);
            point.setImageResource(R.mipmap.point);
            point.setX(xx[6]);  point.setY(yy[6]);
            c1.addView(point,WandH,WandH);
            tx3.setText(String.format("  AI下在(%d , %d)",6,6)); //陣列思維
        }else{
            tx1.setText("●○●玩家對弈模式●○●");
            tx2.setText("黑棋進攻");
        }
    }

    private void init() {
        float x =15, y=260,n=83;
        for (int i = 0; i < size; i++) {
            xx[i] = x;
            yy[i] = y;
            x+=n;
            y+=n;
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if(e.getAction() == MotionEvent.ACTION_DOWN ){
            float x = e.getX()+15;
            float y = e.getY()+260;
            int rx = 0,ry=0;
            String position ="";
            float xmin=1000,ymin=1000;
            for (int i = 0; i < size; i++) {
                float tmp =Math.abs(xx[i]-x);
                float tmp2 =Math.abs(yy[i]-y);
                if(tmp < xmin){xmin =tmp; rx = i;}
                if(tmp2 < ymin){ymin =tmp2; ry = i;}
            }

            if(num==0 && PlayerToPlayer!=1){
                if(bb && board[rx][ry]==0){
                    //玩家下黑棋
                    b = new ImageView(this);
                    if(player == 1){b.setImageResource(R.mipmap.black);tx2.setText("白棋進攻");} // 黑棋
                    else{b.setImageResource(R.mipmap.white);tx2.setText("黑棋進攻");}
                    b.setX(xx[rx]);  b.setY(yy[ry]);
                    c1.addView(b,WandH,WandH);
                    bb = false;
                    board[rx][ry] = player;
                    num = comput(rx,ry,player);
                    position=String.format("你下在(%d , %d)\t",ry,rx); //陣列思維
                }else{
                    tx3.setText("你不能下那裡");
                }
            }else if(num ==0){
                //PlayerToPlayer
                if(bb && board[rx][ry]==0){
                    //玩家下黑棋
                    b = new ImageView(this);
                    b.setImageResource(R.mipmap.black);tx2.setText("白棋進攻");
                    b.setX(xx[rx]);  b.setY(yy[ry]);
                    c1.addView(b,WandH,WandH);
                    bb = false;
                    board[rx][ry] =1;
                    num = comput(rx,ry,1);
                    c1.removeView(point);
                    point = new ImageView(this);
                    point.setImageResource(R.mipmap.point);
                    point.setX(xx[rx]);  point.setY(yy[ry]);
                    c1.addView(point,WandH,WandH);
                    position=String.format("你下在(%d , %d)\t",ry,rx); //陣列思維
                }else{
                    tx3.setText("你不能下那裡");
                }
            }

            if(num==0 && PlayerToPlayer!=1){
                if(!bb){
                    //AI下白棋
                    String best[] = AI(2).split(",");
                    rx = Integer.valueOf(best[0]);
                    ry = Integer.valueOf(best[1]);
                    w = new ImageView(this);
                    if(ai == 1){w.setImageResource(R.mipmap.black);tx2.setText("白棋進攻");} // 白棋
                    else{w.setImageResource(R.mipmap.white);tx2.setText("黑棋進攻");}
                    w.setX(xx[rx]);  w.setY(yy[ry]);
                    c1.addView(w,WandH,WandH);
                    bb = true;
                    board[rx][ry] = ai;
                    num = comput(rx,ry,ai);

                    c1.removeView(point);
                    point = new ImageView(this);
                    point.setImageResource(R.mipmap.point);
                    point.setX(xx[rx]);  point.setY(yy[ry]);
                    c1.addView(point,WandH,WandH);
                    position+= String.format("  AI下在(%d , %d)",ry,rx); //陣列思維
                }
            }else if(!bb && board[rx][ry]==0){
                    //PlayerToPlayer
                    w = new ImageView(this);
                    w.setImageResource(R.mipmap.white);tx2.setText("黑棋進攻");
                    w.setX(xx[rx]);  w.setY(yy[ry]);
                    c1.addView(w,WandH,WandH);
                    bb = true;
                    board[rx][ry] = 2;
                    num = comput(rx,ry,2);
                    c1.removeView(point);
                    point = new ImageView(this);
                    point.setImageResource(R.mipmap.point);
                    point.setX(xx[rx]);  point.setY(yy[ry]);
                    c1.addView(point,WandH,WandH);
                    position=String.format("你下在(%d , %d)\t",ry,rx); //陣列思維
                }else{
                    tx3.setText("你不能下那裡");
                }
            tx3.setText(position);
        }



        if(win){
            String s = "";
            if(PlayerToPlayer!=1){
                if(whofirst == 1){
                    if(num == 1){
                        s ="玩家獲勝！";
                    }else if(num==2){
                        s="AI獲勝！";
                    }
                }else{
                    if(num == 1){
                        s ="AI獲勝！";
                    }else if(num==2){
                        s="玩家獲勝！";
                    }
                }


            }else{
                if(num == 1){
                    s ="黑棋 玩家獲勝！";
                }else if(num==2){
                    s="白棋 玩家獲勝！";
                }
            }
            alert = new AlertDialog.Builder(this)
                    .setMessage(s)
                    .setCancelable(false)
                    .setTitle("標題")
                    .setNeutralButton("查看棋局",this)
                    .setPositiveButton("再來一局",this)
                    .setNegativeButton("回主選單",this);
            alert.show();
        }
        return false;
    }

    private int comput(int y, int x, int n) {
        int cnt1=0,cnt2=0,cnt3=0,cnt4=0,cnt5=0,cnt6=0,cnt7=0,cnt8=0;
        int len = board.length;
        boolean b = false;

        for (int i = 1; i <= 4; i++) {//上
            b= false;
            if(i<=y){
                if(board[y-i][x] == n){cnt1++; b= true;//System.out.println("上+1");}
                }}
            if(b == false){break;}
        }


        for (int i = 1; i <= 4; i++) {//下
            b= false;
            if(i<len-y){
                if(board[y+i][x] == n){cnt2++; b= true;//System.out.println("下+1");}
                }}
            if(b == false){break;}
        }


        for (int i = 1; i <= 4; i++) {//右
            b= false;
            if(i<=x){
                if(board[y][x-i] == n){cnt3++; b= true;//System.out.println("右+1");}
                }}
            if(b == false){break;}
        }


        for (int i = 1; i <= 4; i++) {//左
            b= false;
            if(i<len-x){
                if(board[y][x+i] == n){cnt4++; b= true;//System.out.println("左+1");}
                }}
            if(b == false){break;}
        }


        for (int i = 1; i <= 4; i++) {//右下->左上斜
            b= false;
            if(i<=x && i<=y){
                if(board[y-i][x-i] == n){cnt5++; b= true;//System.out.println("右下->左上斜+1");}
                }}
            if(b == false){break;}
        }


        for (int i = 1; i <= 4; i++) {//左上->右下斜
            b= false;
            if(i<len-x && i<len-y){
                if(board[y+i][x+i] == n){cnt6++; b= true;//System.out.println("左上->右下斜+1");}
                }}
            if(b == false){break;}
        }


        for (int i = 1; i <= 4; i++) {//右上->左下斜
            b= false;
            if(i<=x && i<len-y){
                if(board[y+i][x-i] == n){cnt7++; b= true;//System.out.println("右上->左下斜+1");}
                }}
            if(b == false){break;}
        }


        for (int i = 1; i <= 4; i++) {//左下->右上斜
            b= false;
            if(i<len-x && i<=y){
                if(board[y-i][x+i] == n){cnt8++; b= true;//System.out.println("左下->右上斜+1");}
                }}
            if(b == false){break;}
        }


        if(cnt1 >= 4 || cnt2>=4 || cnt3>=4 || cnt4>=4|| cnt5>=4|| cnt6>=4|| cnt7>=4|| cnt8>=4 || cnt1+cnt2 >=4 || cnt3+cnt4 >=4 || cnt5+cnt6 >=4 || cnt7+cnt8 >=4){ win=true; return n;}
        else{return 0;}
    }

    private String AI(int ai) {
        int player =1;
        int len = board.length;
        int max =0,bestx=0,besty=0;
        boolean b = false;
        String tmp = "";
        for (int i = 0; i < len; i++) {
            for (int j = 0;  j< len; j++) {
                if(board[i][j]==0){
                    int Player[] = new int[8];
                    int Ai[] = new int[8];
                    int Player2[] = new int[8];
                    int Ai2[] = new int[8];
                    int serch =4;
                    //下 -> 上 (白棋Ai) 2
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=i){ //邊界內
                            if(board[i-k][j] == ai){Ai[1]++; b=true;}
                            else if(board[i-k][j] == player){Player[1]++;}
                        }
                        if(!b){break;}
                    }
                    //下 -> 上 (黑棋Player) 1
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=i){ //邊界內
                            if(board[i-k][j] == player){Player2[1]++; b=true;}
                            else if(board[i-k][j] == ai){Ai2[1]++;}
                        }
                        if(!b){break;}
                    }



                    //上 -> 下 (白棋Ai) 2
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=len-i-1){ //邊界內
                            if(board[i+k][j] == ai){Ai[6]++; b=true;}
                            else if(board[i+k][j] == player){Player[6]++;}
                        }
                        if(!b){break;}
                    }
                    //上 -> 下 (黑棋Player) 1
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=len-i-1){ //邊界內
                            if(board[i+k][j] == player){Player2[6]++; b=true;}
                            else if(board[i+k][j] == ai){Ai2[6]++;}
                        }
                        if(!b){break;}
                    }




                    //右 -> 左 (白棋Ai) 2
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=j){ //邊界內
                            if(board[i][j-k] == ai){Ai[3]++; b=true;}
                            else if(board[i][j-k] == player){Player[3]++;}
                        }
                        if(!b){break;}
                    }
                    //右 -> 左 (黑棋Player) 1
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=j){ //邊界內
                            if(board[i][j-k] == player){Player2[3]++; b=true;}
                            else if(board[i][j-k] == ai){Ai2[3]++;}
                        }
                        if(!b){break;}
                    }



                    //左 -> 右 (白棋Ai) 2
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=len-j-1){ //邊界內
                            if(board[i][j+k] == ai){Ai[4]++; b=true;}
                            else if(board[i][j+k] == player){Player[4]++;}
                        }
                        if(!b){break;}
                    }
                    //左 -> 右 (黑棋Player) 1
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=len-j-1){ //邊界內
                            if(board[i][j+k] == player){Player2[4]++; b=true;}
                            else if(board[i][j+k] == ai){Ai2[4]++;}
                        }
                        if(!b){break;}
                    }



                    //右下 -> 左上 (白棋Ai) 2
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=i && k<=j){ //邊界內
                            if(board[i-k][j-k] == ai){Ai[0]++; b=true;}
                            else if(board[i-k][j-k] == player){Player[0]++;}
                        }
                        if(!b){break;}
                    }
                    //下 -> 上 (黑棋Player) 1
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=i && k<=j){ //邊界內
                            if(board[i-k][j-k] == player){Player2[0]++; b=true;}
                            else if(board[i-k][j-k] == ai){Ai2[0]++;}
                        }
                        if(!b){break;}
                    }





                    //左上 -> 右下 (白棋Ai) 2
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=len-i-1 && k<=len-j-1){ //邊界內
                            if(board[i+k][j+k] == ai){Ai[7]++; b=true;}
                            else if(board[i+k][j+k] == player){Player[7]++;}
                        }
                        if(!b){break;}
                    }
                    //左上 -> 右下 (黑棋Player) 1
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=len-i-1 && k<=len-j-1){ //邊界內
                            if(board[i+k][j+k] == player){Player2[7]++; b=true;}
                            else if(board[i+k][j+k] == ai){Ai2[7]++;}
                        }
                        if(!b){break;}
                    }




                    //左下 -> 右上 (白棋Ai) 2
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=i && k<=len-j-1){ //邊界內
                            if(board[i-k][j+k] == ai){Ai[2]++; b=true;}
                            else if(board[i-k][j+k] == player){Player[2]++;}
                        }
                        if(!b){break;}
                    }
                    //左下 -> 右上 (黑棋Player) 1
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=i && k<=len-j-1){ //邊界內
                            if(board[i-k][j+k] == player){Player2[2]++; b=true;}
                            else if(board[i-k][j+k] == ai){Ai2[2]++;}
                        }
                        if(!b){break;}
                    }





                    //右上 -> 左下 (白棋Ai) 2
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=len-i-1 && k<=j){ //邊界內
                            if(board[i+k][j-k] == ai){Ai[5]++; b=true;}
                            else if(board[i+k][j-k] == player){Player[5]++;}
                        }
                        if(!b){break;}
                    }
                    //右上 -> 左下 (黑棋Player) 1
                    for (int k = 1; k <= serch; k++) {
                        b = false;
                        if(k<=len-i-1 && k<=j){ //邊界內
                            if(board[i+k][j-k] == player){Player2[5]++; b=true;}
                            else if(board[i+k][j-k] == ai){Ai2[5]++;}
                        }
                        if(!b){break;}
                    }

                    int score = type(Ai,Player,Ai2,Player2);
                    if(score > max){max = score; bestx=i; besty =j; tmp=""; tmp+=i+"-"+j+",";}
                    else if(score !=0 && score == max){tmp+=i+"-"+j+",";}
                }
            }
        }

        String arr[] = tmp.split(",");
        if(arr.length > 2){
            do{
                int r= (int) (Math.random()*(arr.length-1));
                if(!arr[r].isEmpty()){
                    String arr2[] = arr[r].split("-");
                    bestx = Integer.valueOf(arr2[0]);
                    besty = Integer.valueOf(arr2[1]);
                    break;
                }
            }while (true);
        }
        tmp ="";
        return bestx+","+besty;
    }

    private int type(int[] ai1, int[] player1, int[] ai2, int[] player2) {
        int score = 0;
        for (int i = 0; i < ai1.length; i++) {
            if(ai1[i]+ai1[ai1.length-1-i] >=4 && player1[i]<=1 && player1[ai1.length-1-i]<=1){score+=9999999;} //攻雙活2 || 活1+活3 || 雙死2 || 死1+死3 || 混和... > 1
            else if(ai1[i] >= 4 && player1[i]==0){score+=9999999;} //攻活4 > 1
            else if(ai1[i] == 4 && player1[i]==1){score+=9999999;} //攻死4 > 1
            else if(ai1[i]+ai1[ai1.length-1-i] ==3&& player1[i]==0 && player1[ai1.length-1-i]==0){score+=100000;} //攻活2+活1 > 3
            else if(ai1[i] == 3 && player1[i]==0){score+=100000;} //攻活3 > 3
            else if(ai1[i]+ai1[ai1.length-1-i] ==3&& player1[i]==1 && player1[ai1.length-1-i]==0){score+=10000;} //攻死2+活1 > 4
            else if(ai1[i]+ai1[ai1.length-1-i] ==3&& player1[i]==0 && player1[ai1.length-1-i]==1){score+=10000;} //攻活2+死1 > 4
            else if(ai1[i] == 3 && player1[i]==1){score+=10000;} //攻死3 > 4
            else if(ai1[i] == 2 && player1[i]==0){score+=100;} //攻活2 > 6
            else if(ai1[i] == 1 && player1[i]==0){score+=10;} //攻活1 > 7
            else if(ai1[i] == 2 && player1[i]==1){score+=10;} //攻死2 > 7
            else if(ai1[i] == 1 && player1[i]==1){score+=1;} //攻死1 > 8


            if(ai2[i]<= 1 && player2[i]>=4){score+=1000000;} //守死4- > 2
            else if(player2[i]+player2[player2.length-1-i] >=4 && ai2[i]<=1 && ai2[ai1.length-1-i]<=1 ){score+=1000000;} //守雙活2 || 1+3 || 雙死2  ||.... > 2
            else if(player2[i]+player2[player2.length-1-i] ==3 && ai2[i]==0 && ai2[ai1.length-1-i]==0 ){score+=1000;} //守活2+活1 > 5
            else if(ai2[i] == 0 && player2[i]==3){score+=1000;} //守活3- > 5
            else if(player2[i]+player2[player2.length-1-i] ==3 && ai2[i] + ai2[ai1.length-1-i]==1 ){score+=10;} //守死2+活1 > 7
            else if(ai2[i] == 1 && player2[i]==3){score+=10;} //守死3- > 7
            else if(ai2[i] == 0 && player2[i]==2){score+=1;} //守活2- > 8
            else if(ai2[i] == 0 && player2[i]==1){score+=1;} //守活1- > 8
            else if(ai2[i] == 1 && player2[i]==2){score+=1;} //守死2- > 8
            else if(ai2[i] == 1 && player2[i]==1){score+=1;} //守死1- > 8
        }
        return score;
    }

    public void onClick(DialogInterface dialog, int witch) {
        if(witch == DialogInterface.BUTTON_POSITIVE){
            //再來一盤

            Intent it = new Intent(this,main.class);
            it.putExtra("Player",PlayerToPlayer);
            it.putExtra("priority",whofirst);
            startActivity(it);
            finish();
        }else if(witch == DialogInterface.BUTTON_NEGATIVE){
            //回主選單

            Intent it = new Intent(this,Menu.class);
            startActivity(it);
            finish();
        }else{

        }
    }
}

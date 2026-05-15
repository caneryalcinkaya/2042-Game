import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.KeyEvent;

public class Main {

    static final int canvaWidth = 600;
    static final int canvasHeight = 800;

    public static void main(String[] args) {

        StdDraw.setCanvasSize(canvaWidth,canvasHeight);
        StdDraw.setXscale(0, canvaWidth);
        StdDraw.setYscale(0, canvasHeight);
        StdDraw.setTitle("2042: Interceptor");
        StdDraw.enableDoubleBuffering();

        int positionInterceptorX=300;
        int positionInterceptorY=150;
        int interceptorWidth=60;
        int interceptorHeight=80;
        int pauseDuration=30;
        int speed = 15;

        int[] bulletX = new int[20];
        int[] bulletY = new int[20];
        boolean[] bulletAlive = new boolean[20];
        int bulletSpeed = 15;
        boolean spaceWasPressed = false;
        boolean stackRight=false;
        boolean stackLeft=false;
        boolean stackUp=false;
        boolean stackDown=false;
        int gameMode=0;
        int score = 0;
        int[] enemyX = {120,240,360,480,120,240,360,480};
        int[] enemyY = {750,750,750,750,650,650,650,650};
        boolean[] isEnemyAlive = {true,true,true,true,true,true,true,true};
        boolean isEnenmyMovingRight = true;
        int enemySpeed= 10;

        int[] enemyBulletX = new int[20];
        int[] enemyBulletY = new int[20];
        boolean[] enemyBulletAlive = new boolean[20];
        int enemyBulletSpeed = 8;
        Random random = new Random();
        boolean[] isExplosionActive = new boolean[8];
        int[] explosionTimer = new int[8];


        int playerLives = 3;
        int heartSizeDistance = 40;
        int firstHeartX = 570;
        int firstHeartY = 780;


        while (true) {

            StdDraw.clear(StdDraw.WHITE);
            StdDraw.picture(300,400,"assets/background.png",600,800,0.0);
            int fps = 1000/pauseDuration;

            // Movement of interceptor
            if (positionInterceptorX>canvaWidth-(interceptorWidth)) {
                stackRight=true;
            } else {
                stackRight=false;
            }
            if (positionInterceptorX<(interceptorWidth/2)) {
                stackLeft=true;
            } else {
                stackLeft=false;
            }
            if (positionInterceptorY>canvasHeight-(interceptorHeight)) {
                stackUp=true;
            } else {
                stackUp=false;
            }
            if (positionInterceptorY<interceptorHeight/2) {
                stackDown=true;
            } else {
                stackDown=false;
            }

            if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
                if (!stackLeft) {
                    positionInterceptorX-=speed;
                }
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                if (!stackRight) {
                    positionInterceptorX+=speed;
                }
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
                if (!stackUp) {
                    positionInterceptorY+=speed;
                }
            }
            if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
                if (!stackDown) {
                    positionInterceptorY-=speed;
                }
            }
            if (gameMode==0) { // Menu

                if (StdDraw.isKeyPressed(KeyEvent.VK_A)) {
                    if (pauseDuration>0 && fps<1000) {
                        pauseDuration-=1;
                    }
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_D)) {
                    if(pauseDuration>0) {
                        pauseDuration+=1;
                    }
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_Q)) {
                    speed+=1;
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_E)) {
                    speed-=1;
                }
                if (StdDraw.isKeyPressed(KeyEvent.VK_ENTER)) {
                    gameMode=1; // switch game play
                }

                StdDraw.picture(300,500,"assets/title.png",400,150);
                Font font = new Font("Arial", Font.BOLD, 32);
                StdDraw.setFont(font);
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.text(300,350,"> Start Game <");

                Font font2 = new Font("Arial", Font.PLAIN, 16);
                StdDraw.setFont(font2);
                StdDraw.text(300,300,"FPS: ~"+fps+" | Speed: "+speed);
                StdDraw.text(300,100,"Move: [←] [↑] [→] [↓]");
                StdDraw.text(300,75,"Shoot: [Space]");
                StdDraw.text(300,50,"Press: [ENTER] to start");
                StdDraw.text(300,25,"FPS: A/D  Speed: Q/E");


            } else if (gameMode==1) { // GamePlay
                for (int i=0; i<8 ;i++) {
                    if (isEnemyAlive[i]) {
                        if (isEnenmyMovingRight) {
                            enemyX[i]+=enemySpeed;
                        } else {
                            enemyX[i]-=enemySpeed;
                        }
                    }
                }
                boolean hitWall = false;
                for (int i=0; i<8;i++) {
                    if (isEnemyAlive[i]) {
                        if (enemyX[i]>canvaWidth-50 || enemyX[i]<50) {
                            hitWall=true;
                        }
                    }
                }
                if (hitWall) {
                    isEnenmyMovingRight=!isEnenmyMovingRight;
                }
                for (int i=0;i<8;i++) {
                    if (isEnemyAlive[i]) {
                        StdDraw.picture(enemyX[i],enemyY[i],"assets/enemyFighter.png",100,70,0.0);
                    }
                }

                // COLLISION
                for (int i=0;i<20;i++) { // for bullet
                    for (int j=0;j<8;j++) { // for enemy
                        if (bulletAlive[i] && isEnemyAlive[j]) {
                            if (bulletX[i]>(enemyX[j]-49) && bulletX[i]<(enemyX[j]+49) && bulletY[i]>(enemyY[j]-34) && bulletY[i]<(enemyY[j]+34)) {
                                isEnemyAlive[j]=false;
                                bulletAlive[i]=false;
                                score+=30;
                                isExplosionActive[j]=true;
                                explosionTimer[j]=0;
                            }
                        }
                    }
                }
                for (int i=0;i<8;i++) {
                    if(isExplosionActive[i]) {
                        explosionTimer[i]++;
                        if (explosionTimer[i]<10){
                            StdDraw.picture(enemyX[i],enemyY[i],"assets/explosionSmall.png",40,30,0.0);
                        } else if(explosionTimer[i]<20) {
                            StdDraw.picture(enemyX[i],enemyY[i],"assets/explosionBig.png",60,40,0.0);
                        } else {
                            isExplosionActive[i]=false;
                        }
                    }
                }



                // SETTING SCORE
                Font font = new Font("Arial", Font.BOLD, 30);
                StdDraw.setFont(font);
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.textLeft(15,775,"Score: "+score);

                //SETTING PLAYER LIVES
                for (int i=0;i<playerLives;i++) {
                    StdDraw.picture(firstHeartX,firstHeartY,"assets/heart.png",40,30,0.0);

                }


                // ENEMY BULLET RANDOMIZATION


            }





            // Bullet Movements
            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE) && !spaceWasPressed) {

                for (int i = 0; i < 20; i++) {
                    if (!bulletAlive[i]) {
                        bulletX[i] = positionInterceptorX;
                        bulletY[i] = positionInterceptorY + interceptorHeight/2;
                        bulletAlive[i] = true;
                        break;
                    }
                }
                spaceWasPressed = true;
            } else if (!StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                spaceWasPressed = false;
            }
            for (int i = 0; i < 20; i++) {
                if (bulletAlive[i]) {
                    bulletY[i] += bulletSpeed;
                    if (bulletY[i] > canvasHeight) {
                        bulletAlive[i] = false;
                    }
                    StdDraw.picture(bulletX[i],bulletY[i],"assets/bullet.png",10,40);
                }
            }



            StdDraw.picture(positionInterceptorX,positionInterceptorY,"assets/interceptor.png",60,80,0.0);
            StdDraw.show();
            StdDraw.pause(pauseDuration);

        }
    }
}


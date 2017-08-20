package com.fly;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *  飞机大战
 * 1.游戏共四个基本对象类：敌机类(Airplane)、蜜蜂类(Bee)、子弹类(Bullet)、英雄类(Hero)
 * 2.由蜜蜂类实现奖励接口(Award),添加火力和生命值；敌机类实现积分接口(Enemy)。共同继承飞行物抽象类(FlyingObject)
 *
 * 3.main方法中创建游戏窗体，添加面板属性，调用action方法启动游戏
 * 4.通过监听鼠标点击与移动，切换游戏状态，调节英雄飞机坐标，进行游戏
 * 5.在游戏线程内进行：飞行物的生成、子弹与飞行物的碰撞、奖励与积分的计算、越界与发生碰撞的飞行物的清除
 * 6.调用定时器，周期性执行线程内代码，持续刷新游戏界面，更新游戏进度
 */
public class ShootGame extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    static final int WIDTH = 400; // 面板宽
    static final int HEIGHT = 654; // 面板高
    /**
     * 游戏的当前状态 ：START RUNNING PAUSE GAME_OOVER
     */
    private int state;    //游戏状态 开始、运行中、暂停、游戏结束
    private static final int START = 0;
    private static final int RUNNING = 1;
    private static final int PAUSE = 2;
    private static final int GAME_OVER = 3;

    private int score = 0; // 得分
    /**
     * 图片组
     */
    private static BufferedImage background;    //游戏背景图
    private static BufferedImage start;        //飞机大战预开始图片
    static BufferedImage airplane;        //敌机图片
    static BufferedImage bee;        //小蜜蜂
    static BufferedImage bullet;    //子弹
    static BufferedImage hero0;        //英雄图片，喷火小尾巴远离
    static BufferedImage hero1;        //英雄图片，小尾巴靠近
    private static BufferedImage pause;        //暂停图片，鼠标移至游戏边框时触发
    private static BufferedImage gameover;        //游戏结束图片

    private FlyingObject[] flyings = {};// 敌机数量
    private Bullet[] bullets = {};// 子弹数量
    private Hero hero = new Hero();// 英雄机

    static {    //加载对应图片资源
        try {
            background = ImageIO.read(ShootGame.class.getResource("background.png"));
            start = ImageIO.read(ShootGame.class.getResource("start.png"));
            airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
            bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
            bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
            hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
            hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
            pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
            gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 绘画游戏界面
     */
    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, null); // 画背景图
        paintHero(g); // 画英雄机
        paintBullets(g); // 画子弹
        paintFlyingObjects(g); // 画飞行物
        paintScore(g); // 画分数
        paintState(g); // 画游戏状态图
    }

    /**
     * 画英雄机
     */
    private void paintHero(Graphics g) {
        g.drawImage(hero.getImage(), hero.getX(), hero.getY(), null);
    }

    /**
     * 画子弹
     */
    private void paintBullets(Graphics g) {
        for (Bullet b : bullets) {        //子弹x横坐标为子弹图片x坐标-子弹宽度的一半
            g.drawImage(b.getImage(), b.getX() - b.getWidth() / 2, b.getY(), null);
        }
    }

    /**
     * 画飞敌机
     */
    private void paintFlyingObjects(Graphics g) {
        for (FlyingObject f : flyings) {
            g.drawImage(f.getImage(), f.getX(), f.getY(), null);
        }
    }

    /**
     * 画分数
     */
    private void paintScore(Graphics g) {
        int x = 10; // x坐标
        int y = 25; // y坐标
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 22); // 字体
        g.setColor(new Color(0xFF0000));    //颜色
        g.setFont(font); // 设置字体
        g.drawString("SCORE:" + score, x, y); // 画分数
        y = y + 20; // 命的y坐标增20，下移20个像素的
        g.drawString("LIFE:" + hero.getLife(), x, y); // 画命
    }

    /**
     * 画游戏状态
     */
    private void paintState(Graphics g) {
        switch (state) {
            case START: // 启动状态
                g.drawImage(start, 0, 0, null);
                break;
            case PAUSE: // 暂停状态
                g.drawImage(pause, 0, 0, null);
                break;
            case GAME_OVER: // 游戏终止状态
                g.drawImage(gameover, 0, 0, null);
                break;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fly");//设置游戏标题
        ShootGame game = new ShootGame(); // 创建面板
        frame.add(game); // 将面板添加到JFrame窗体中
        frame.setSize(WIDTH, HEIGHT); // 设置大小
        frame.setAlwaysOnTop(true); // 设置其总显示在最上层
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // 关闭窗体，默认退出游戏
        frame.setLocationRelativeTo(null); // 窗体默认居中
        frame.setVisible(true); // 显示窗体

        game.action(); // 启动游戏
    }


    /**
     * 启动执行代码
     */
    private void action() {
        // 鼠标监听事件
        MouseAdapter l = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) { // 鼠标移动
                if (state == RUNNING) { // 游戏运行状态下
                    int x = e.getX();
                    int y = e.getY();
                    hero.moveTo(x, y);//跟随鼠标移动英雄机
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) { // 鼠标进入游戏界面
                if (state == PAUSE) { // 暂停状态切换到运行
                    state = RUNNING;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) { // 鼠标退出游戏界面
                if (state == RUNNING) { // 游戏未结束，则设置其为暂停
                    state = PAUSE;
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) { // 鼠标点击
                switch (state) {
                    case START:     //游戏开始界面点击，进入运行界面
                        state = RUNNING;
                        break;
                    case GAME_OVER: // 游戏结束，清理现场，重新开始游戏
                        flyings = new FlyingObject[0]; // 清空飞行物
                        bullets = new Bullet[0]; // 清空子弹
                        hero = new Hero(); // 重新创建英雄机
                        score = 0; // 清空成绩
                        state = START; // 状态设置为启动
                        break;
                }
            }
        };
        this.addMouseListener(l); // 处理鼠标点击操作，把鼠标监听添加到游戏中
        this.addMouseMotionListener(l); // 处理鼠标滑动操作

        Timer timer = new Timer();    //定时器
        int interval = 10;    //时间间隔
        timer.schedule(new TimerTask() {
            @Override
            public void run() {    //调用 线程
                if (state == RUNNING) {
                    enterAction();
                    stepAction();
                    shootAction();
                    bangAction();
                    outOfBoundsAction();
                    checkGameOverAction();
                }
                repaint(); // 重绘，调用paint()方法
            }

        }, interval, interval);
    }

    private int flyEnteredIndex = 0; // 飞行物入场计数

    /**
     * 飞行物入场
     */
    private void enterAction() {
        flyEnteredIndex++;
        if (flyEnteredIndex % 60 == 0) { // 600毫秒生成一个飞行物--10*60
            FlyingObject obj = nextOne(); // 随机生成一个飞行物
            flyings = Arrays.copyOf(flyings, flyings.length + 1);
            flyings[flyings.length - 1] = obj;
        }
    }

    /**
     * 按概率随机生成飞行物
     */
    private static FlyingObject nextOne() {
        Random random = new Random();
        int type = random.nextInt(20); // [0,20)
        if (type < 4) {
            return new Bee();
        } else {
            return new Airplane();
        }
    }

    /**
     * 飞行物移动
     */
    private void stepAction() {
        for (FlyingObject f : flyings) { // 飞行物移动
            f.step();
        }

        for (Bullet b : bullets) { // 子弹移动
            b.step();
        }
        hero.step(); // 英雄机切图片
    }

    private int shootIndex = 0; // 射击计数
    /**
     * 射击
     */
    private void shootAction() {
        shootIndex++;
        if (shootIndex % 30 == 0) { // 300毫秒发一颗
            Bullet[] bs = hero.shoot(); // 英雄打出子弹
            bullets = Arrays.copyOf(bullets, bullets.length + bs.length); // 扩容
            System.arraycopy(bs, 0, bullets, bullets.length - bs.length,
                    bs.length); // 追加数组
        }
    }

    /**
     * 子弹与飞行物碰撞检测
     */
    private void bangAction() {
        for (Bullet b : bullets) { // 遍历所有子弹
            bang(b); // 子弹和飞行物之间的碰撞检查
        }
    }

    /**
     * 子弹和飞行物之间的碰撞检查
     */
    private void bang(Bullet bullet) {
        int index = -1; // 击中的飞行物索引
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject obj = flyings[i];
            if (obj.shootBy(bullet)) { // 判断是否击中
                index = i; // 记录被击中的飞行物的索引
                break;
            }
        }
        if (index != -1) { // 有击中的飞行物
            FlyingObject one = flyings[index]; // 记录被击中的飞行物

            FlyingObject temp = flyings[index]; // 被击中的飞行物与最后一个飞行物交换
            flyings[index] = flyings[flyings.length - 1];
            flyings[flyings.length - 1] = temp;

            flyings = Arrays.copyOf(flyings, flyings.length - 1); // 删除最后一个飞行物(即被击中的)

            // 检查one的类型(敌人加分，奖励获取)
            if (one instanceof Enemy) { // 检查类型，是敌人，则加分
                Enemy e = (Enemy) one; // 强制类型转换
                score += e.getScore(); // 加分
            } else { // 若为奖励，设置奖励
                Award a = (Award) one;
                int type = a.getType(); // 获取奖励类型
                switch (type) {
                    case Award.DOUBLE_FIRE:
                        hero.addDoubleFire(); // 设置双倍火力
                        break;
                    case Award.LIFE:
                        hero.addLife(); // 设置加命
                        break;
                }
            }
        }
    }


    /**
     * 删除越界飞行物及子弹，防止溢出
     */
    private void outOfBoundsAction() {
        int index = 0; // 索引
        FlyingObject[] flyingLives = new FlyingObject[flyings.length]; // 活着的飞行物
        for (FlyingObject f : flyings) {
            if (!f.outOfBounds()) {
                flyingLives[index++] = f; // 不越界的留着
            }
        }
        flyings = Arrays.copyOf(flyingLives, index); // 将不越界的飞行物都留着

        index = 0; // 索引重置为0
        Bullet[] bulletLives = new Bullet[bullets.length];
        for (Bullet b : bullets) {
            if (!b.outOfBounds()) {
                bulletLives[index++] = b;
            }
        }
        bullets = Arrays.copyOf(bulletLives, index); // 将不越界的子弹留着
    }

    /**
     * 结束游戏
     */
    private void checkGameOverAction() {
        if (isGameOver()) {
            state = GAME_OVER; // 改变状态
        }
    }

    /**
     * 检查游戏是否结束
     */
    private boolean isGameOver() {

        for (int i = 0; i < flyings.length; i++) {
            int index = -1;
            FlyingObject obj = flyings[i];
            if (hero.hit(obj)) { // 检查英雄机与飞行物是否碰撞
                hero.subtractLife(); // 减命
                hero.setDoubleFire(); // 双倍火力解除
                index = i; // 记录碰上的飞行物索引
            }
            if (index != -1) {
                FlyingObject t = flyings[index];
                flyings[index] = flyings[flyings.length - 1];
                flyings[flyings.length - 1] = t; // 碰上的飞行物与飞行物集合中最后一个飞行物交换，方便删除

                flyings = Arrays.copyOf(flyings, flyings.length - 1); // 删除碰上的飞行物
            }
        }

        return hero.getLife() <= 0;     //没命了判定游戏结束
    }


}

package com.fly;

import java.util.Random;

/** 蜜蜂 */
public class Bee extends FlyingObject implements Award{
	private int xSpeed = 1;   //x坐标移动速度
	private int ySpeed = 2;   //y坐标移动速度
	private int awardType;    //奖励类型
	
	/** 初始化数据 */
	Bee(){
		this.image = ShootGame.bee;	//加载蜜蜂图片，将图片长宽设定为蜜蜂长宽
		width = image.getWidth();
		height = image.getHeight();
		y = -height;				//出场机制，于上方游戏框随机出现
		Random rand = new Random();
		x = rand.nextInt(ShootGame.WIDTH - width);
		awardType = rand.nextInt(2);   //打下蜜蜂随机给奖励类型
	}
	
	/** 获得奖励类型 */
	public int getType(){
		return awardType;
	}

	/** 越界处理 */
	@Override
	public boolean outOfBounds() {
		return y>ShootGame.HEIGHT;
	}

	/** 移动，可斜着飞 */
	@Override
	public void step() {      
		x += xSpeed;
		y += ySpeed;
		if(x > ShootGame.WIDTH-width){  
			xSpeed = -1;	//左移
		}
		if(x < 0){
			xSpeed = 1;		//右移
		}
	}
}
package com.fly;

import java.util.Random;

/**
 *	敌机
 */
public class Airplane extends FlyingObject implements Enemy{

	/** 初始化敌机信息 **/

	Airplane() {
		this.image = ShootGame.airplane;
		width = image.getWidth();
		height = image.getHeight();
		y = -height;
		x = new Random().nextInt(ShootGame.WIDTH - width);

	}

	/** 获取分数 */
	public int getScore() {
		return 5;
	}

	/** 越界处理 */
	@Override
	public boolean outOfBounds() {
		return y > ShootGame.HEIGHT;
	}

	/** 移动 */
	@Override
	public void step() {
		int speed = 3;
		y += speed;
	}

}

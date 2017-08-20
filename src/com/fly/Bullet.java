package com.fly;

/**
 * 子弹类
 */
public class Bullet extends FlyingObject {

	/**
	 * 初始化数据
	 */
	Bullet(int x, int y) {
		this.x = x;
		this.y = y;
		this.image=ShootGame.bullet;
	}
	
	/**越界处理*/
	@Override
	public boolean outOfBounds() {
		return y<-height;
	}

	/**移动*/
	@Override
	public void step() {
		int speed = 5;
		y-= speed;
	}

}

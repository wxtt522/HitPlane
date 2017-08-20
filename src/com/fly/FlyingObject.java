package com.fly;

import java.awt.image.BufferedImage;
/**
 * 飞行物(敌机、蜜蜂、子弹、英雄机)
 */
public abstract class FlyingObject {
	int x;	//x坐标
	int y;	//y坐标
	int width;	//宽
	int height;	//高
	BufferedImage image;	//图片
	int getX() {
		return x;
	}

	int getY() {
		return y;
	}

	int getWidth() {
		return width;
	}

	BufferedImage getImage() {
		return image;
	}

	/**
	 * 检查是否出界
	 */
	public abstract boolean outOfBounds();
	
	/**
	 * 飞行物移动一步
	 */
	public abstract void step();
	
	/**
	 * 检查当前飞行物体是否被子弹(x,y)击(shoot)中
	 */
    boolean shootBy(Bullet bullet){
		int x=bullet.x;
		int y=bullet.y;
		return this.x<x && x<this.x+width && this.y<y && y<this.y+height;
	}
	
	
	
	
	
}

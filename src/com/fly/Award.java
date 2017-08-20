package com.fly;
/**
 * 奖励接口，由小蜜蜂实现
 */
public interface Award {
	int DOUBLE_FIRE = 0; //双倍火力
	int LIFE = 1; //1条命
	/**获得奖励类型 (上面的0或者1 奖励火力或者命)*/
	int getType();
}

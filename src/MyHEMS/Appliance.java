package MyHEMS;
/**
 * 时间：2013年11月28日
 * 电器父类
 */
public class Appliance 
{
	public int power;						//功率
	public int priority;					//优先级
	public int setStartTime;				//设定开始时间
	public boolean isOn = false;			//是否打开
	public boolean beToOn = false;			//是否正要打开
	public boolean isGood = false;			//是否满足舒适度
	public boolean isDelayStart = false;	//是否处于延时开启
	public int delayTime;					//延时时间
	
}

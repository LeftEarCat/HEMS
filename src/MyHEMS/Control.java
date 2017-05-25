package MyHEMS;
/**
 * 时间：2013年11月28日
 * 改进：增加了界面，当热水器和汽车充电完成之后，增加了再次充电的按钮
 * 注意事项：为了便于测试，时间一块做了更改，实际应用中只需删去测试用的时间代码，还原实际的时间的代码即可
 */
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;

public class Control
{
	static final int fiveMinutes = 1000;	//理应是300,000，测试用
	static int currentHour;					//当前时间，11点到12点[11,12）之间是11
	static int countfiveMinutes = 0;			//计数，经过了多少个5分钟
	//电器
	static AirCondition airCondition;	
	static ClothesDry clothesDry;
	static ElectronicVehicle electronicVehicle;
	static WaterHeater waterHeater;
	//电器父类数组
	static Appliance[] appliance;

	static int totalPower = 0;		//总功率
	static java.util.Date date;		//时间，实际中要用到，关于用于测试的时间代码要全部删去
	static final int delay_max = 5;		//电器延时开启的最大可延时时间
	static int delayTime;			
	static final int demandLimit = 5000;		//电力限制
	
	static int calEVTime = 0;		//汽车充电时间计数，12为一个小时
	
	static Boolean isStart = false;		//系统是否开启
	static boolean isInit = false;		//系统是否进行了初始化
	
	public static void main(String[] args)	//主线程
	{
		System.out.println("进入主线程...................................");
		MyFrame myFrame = new MyFrame("家居智能控制系统");	//实例一个人机交互界面
		
		//测试用
		Control.currentHour = 10;
		TimeTest timeTestThread = new TimeTest();
		timeTestThread.start();
		//测试用	

		System.out.println("等待启动 .......................................................");
		//存储能源不足时
		while(true)
		{
			while (isStart)
			{
	/*			//实际中时间的取值，若是实际应用，时间用下面两行，将"TimeTest.java"及"Control.currentHour = 10;"删去。
				date = new java.util.Date();
				currentHour = date.getHours();*/				
				appliance = init();		//初始化
				try
				{
					Thread.sleep(1000);	//1秒钟监测一次
				} catch (Exception e)
				{
					// TODO: handle exception
				}
				for (int i = 0; i < 4; i++) 	//从高优先级开始判断哪一个电器到达延时并先打开
				{	
					if ((appliance[i].delayTime == currentHour) && appliance[i].isDelayStart)	//延时时间到达，并且电器处于延时开启状态
					{
						System.out.println("当前时间.currentHour:"+Control.currentHour+"点钟@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
						System.out.println("到达延时时间，正在尝试打开........");
						appliance[i].isDelayStart = false;
						appliance[i].beToOn = false;
						totalPower = getToStartTotalPower(i);
												
						if (totalPower < demandLimit) //总功率小于可用最大功率
						{
							System.out.println("打开电器后的总功率totalPower为: "+totalPower+"瓦特");		
							System.out.println("打开电器......");
							appliance[i].isOn = true;
							showAppOn(i);				//显示这次打开的电器
						}
						else //总功率大于可用最大功率
						{										
							System.out.println("打开电器后的总功率totalPower为: "+totalPower+"瓦特");	
							System.out.println("正在从低优先级关闭电器......");
							System.out.println("打开电器......");
							openApp(appliance);			//关闭低优先级电器，打开延时准备开启的电器
						}
					}
				}
			}	
		}			
	}	
	static int delayStartOnPeriod(int i)	//延时在某个时刻开启，返回开启的时间点
	{					
		System.out.println("-----------------------------------------------------------------");
		totalPower = getToStartTotalPower(i);
		System.out.println("当前时间.currentHour:"+currentHour+"点钟@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		//时间是高峰期时。满足舒适度，继续；不满足舒适度，打开appliance
		if ((currentHour >= 6 && currentHour <12) || (currentHour >= 18 && currentHour < 24)) 
		{
			System.out.println("时间段: on_peak");
			delayTime = 24 - currentHour; //延时到低峰时间的延时时间
			System.out.println("延时到低峰时间的延时时间是:"+delayTime+"小时");
			if (delayTime <= delay_max) //延时到低峰时间小于最大可延时时间
			{		
				System.out.println("可以延时到off_peak,等待到晚上零点开启！");
				appliance[i].isDelayStart = true;
				return 0;					
			}
			else //延时到低峰时间大于最大可延时时间
			{
				System.out.println("无法延时到off_peak，尝试延时到mid_peak");
				if (currentHour < 12)  //时间在中峰时间之前的那段
				{
					System.out.println("现在时间处于上午on_peak时段");
					delayTime = 12 - currentHour;
					System.out.println("延时到mid_peak时间的延时时间是: "+delayTime+"小时");
					if (delayTime < delay_max)    //延时到中峰时间小于最大可延时时间
					{		
						System.out.println("可以延时到mid_peak时段，等待到中午12点开启！");
						appliance[i].isDelayStart = true;
						return 12;
					}
					else 
					{
						System.out.println("无法延时到mid_peak时段，尝试直接打开........");
						if (totalPower < demandLimit) //总功率小于可用最大功率
						{
							System.out.println("打开电器后的总功率totalPower为: "+totalPower+"瓦特");		
							System.out.println("打开电器......");
							openApp(appliance);
						}
						else //总功率大于可用最大功率
						{										
							System.out.println("打开电器后的总功率totalPower为: "+totalPower+"瓦特");	
							System.out.println("正在从低优先级关闭电器......");
							System.out.println("打开电器......");
							openApp(appliance);
						}
						return 25;
					}
				}
				else  //时间在中峰时间之后低峰期之前的那段
				{
					System.out.println("无法延时到mid_peak，尝试直接打开.........");
					if (totalPower < demandLimit) //总功率小于可用最大功率
					{
						System.out.println("打开电器后的总功率totalPower为: "+totalPower+"瓦特");		
						System.out.println("打开电器......");
						openApp(appliance);
					}
					else //总功率大于可用最大功率
					{										
						System.out.println("打开电器后的总功率totalPower为: "+totalPower+"瓦特");	
						System.out.println("正在从低优先级关闭电器......");
						System.out.println("打开电器......");
						openApp(appliance);
					}
					appliance[i].isDelayStart = false;
					return 25;
				}
			}
		}
		//时间是中峰期时。
		else if (currentHour >= 12 && currentHour <18) //是否要考虑demandLimit？？？？
		{
			System.out.println("时间段: mid_peak");
			delayTime = 24 - currentHour;	//延时至off_peak所需延时
			if (delayTime > delay_max)
			{
				System.out.println("无法延时到off_peak，尝试打开电器.......");
				openApp(appliance);
				appliance[i].isDelayStart = false;
				return 25;
			}	
			else 
			{
				System.out.println("可以延时到off_peak，等待到晚上零点打开电器.......");
				appliance[i].isDelayStart = true;
				return 0;						
			}
		}
		//时间是低峰期时。
		else if (currentHour >= 0 && currentHour < 6)  //是否要考虑demandLimit？？？？
		{
			System.out.println("时间段: off_peak");
			if (totalPower < demandLimit) //总功率小于可用最大功率
			{
				System.out.println("打开电器后的总功率totalPower为: "+totalPower+"瓦特");		
				System.out.println("打开电器......");
				openApp(appliance);
				appliance[i].isDelayStart = false;
				return 25;
			}
			else //总功率大于可用最大功率
			{										
				System.out.println("打开电器后的总功率totalPower为: "+totalPower+"瓦特");	
				System.out.println("正在从低优先级关闭电器......");
				System.out.println("打开电器......");
				openApp(appliance);
				appliance[i].isDelayStart = false;
				return 25;
			}
		}
		return 25;					
	}	
	
	static Appliance[] init()					//电器初始化
	{
		if (!isInit)
		{
			isInit = true;
			System.out.println("初始化.....");
			Appliance[] app = new Appliance[4];
			airCondition = new AirCondition();
			clothesDry = new ClothesDry();
			electronicVehicle = new ElectronicVehicle();
			waterHeater = new WaterHeater();
			app[0] = waterHeater;
			app[1] = airCondition;
			app[2] = clothesDry;
			app[3] = electronicVehicle;
			waterHeater.priority = 4;
			waterHeater.roomWaterTempature = 30;
			waterHeater.highWaterTempature = 90;
			waterHeater.currentWaterTempature = waterHeater.roomWaterTempature;
			waterHeater.reRunInOnTempature = 60;
			waterHeater.power = 900;
			
			airCondition.priority = 3;
			airCondition.roomTempature = 32;
			airCondition.setTempature = 22;
			airCondition.currentTempature = airCondition.roomTempature;
			airCondition.reRunInOnTempature = 26;
			airCondition.power = 1500;
			airCondition.beToOn =false;
			
			clothesDry.priority = 2;
			clothesDry.needDryTime = 50;	//五十分钟
			clothesDry.currentDryTime = 0;
			clothesDry.power = 1000;

			electronicVehicle.priority = 1;	
			electronicVehicle.currentChargeTime = 0;
			electronicVehicle.needChargeTime = 13;	//13个小时
			electronicVehicle.power = 2000;
			
			AppThread myThread = new AppThread();		
			Thread thread = new Thread(myThread);
			thread.start();	
			TimeThread timeThread = new TimeThread();
			Thread thread2 = new Thread(timeThread);
			thread2.start();
			return app;
		}
		else {
			return appliance;
		}
	}
	static int getToStartTotalPower(int i)					//得到当前电器（包括将要打开的电器）的总功率
	{
		int total = getTotalPower(appliance);
		total += appliance[i].power;
		return total;
	}
	static int getTotalPower(Appliance[] app)				//得到当前已经打开的电器的总功率
	{
		int total = 0;
		for (int i = 0; i < app.length; i++)
		{
			if (app[i].isOn)
			{
				total += app[i].power;
			}
		}
		return total;
	}
	static boolean allGood()		//电器舒适度均满足
	{
		if (airCondition.isGood == true && clothesDry.isGood == true && waterHeater.isGood == true && electronicVehicle.isGood == true)
		{
			return true;	
		}
		else {
			return false;
		}
	}
	static void openApp(Appliance[] app)	//满足电力限制的条件下按照优先级打开电器
	{
		for (int i = 0; i < 4; i++) 
		{
			if (app[i].beToOn && !app[i].isOn) //电器处于关闭准备打开
			{	
				System.out.println("正在打开中##################################");
				if (app[i].isGood) 
				{
					System.out.println("已经满足舒适度，无需打开！");
				}
				else 
				{
					if (totalPower > demandLimit) 
					{
						System.out.println("打开后将超过电力限制，正在尝试关闭其他电器后再次打开..........");
						while (totalPower > demandLimit)
						{
							closeFromLower(app);
						}
						if (totalPower <= demandLimit) 
						{
							app[i].isOn = true;
							app[i].beToOn = false;
							showAppOn(i);
						}
						break;
					}
					else 
					{
						app[i].isOn = true;
						app[i].beToOn = false;
						showAppOn(i);
					}
				}	
			}			
		}
	}
	static void showStartFailed(int i)		//显示打开失败的电器
	{
		switch (i)
		{
		case 0:
			System.out.println("热水器重新打开失败!");
			break;
		case 1:
			System.out.println("空调重新打开失败！");
			break;
		case 2:
			System.out.println("干洗机重新打开失败!");
			break;
		case 3:
			System.out.println("汽车重新充电失败！");
			break;
		default:
			break;
		}
	}
	static void closeFromLower(Appliance[] app)//先关闭已经打开且满足舒适度的，如果功率依旧高于电力限制，再关闭已经发开为满足的电器，均按照优先级
	{
		System.out.println("正在尝试关闭较低优先级的电器........");
		for (int i = 3; i >= 0; i--) 
		{
			if (app[i].isOn && app[i].isGood) 
			{
				app[i].isOn = false;
				showAppOff(i);
				totalPower -= app[i].power;
				if (totalPower < demandLimit) 
				{			
					System.out.println("关闭结束,欲打开的电器即将打开........");
					return;
				}
			}			
		}
		for (int i = 3; i >= 0; i--) 
		{
			if (app[i].isOn)
			{
				app[i].isOn = false;
				app[i].beToOn =false;
				showAppOff(i);
				totalPower -= app[i].power;
				if (totalPower < demandLimit) 
				{
					System.out.println("关闭结束,欲打开的电器即将打开........");
					return;
				}
			}
		}
		System.out.println("从低优先级关闭电器失败！");
	}
	static void showAppOn(int i)				//显示电器重新开启动作
	{
		switch (i)
		{
		case 0:
			System.out.println("热水器.waterHeater is turned on，开始工作!");
			break;
		case 1:
			System.out.println("空调.airCondition is turned on，开始工作!");
			break;
		case 2:
			System.out.println("干洗机.clothesDry is turned on，开始工作!");
			break;
		case 3:
			System.out.println("汽车.electronicVehicle is turned on，开始工作!");
			break;
		default:
			break;
		}
	}
	static void showAppOff(int i)				//显示电器重新关闭动作
	{
		switch (i)
		{
		case 0:
			System.out.println("热水器.waterHeater is turned off，停止工作!");
			break;
		case 1:
			System.out.println("空调.airCondition is turned off，停止工作!");
			break;
		case 2:
			System.out.println("干洗机.clothesDry is turned off，停止工作!");
			break;
		case 3:
			System.out.println("汽车.electronicVehicle is turned off，停止工作!");
			break;
		default:
			break;
		}
	}

}

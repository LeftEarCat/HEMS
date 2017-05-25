package MyHEMS;

public class AppThread implements Runnable  //电器工作线程
{
	public synchronized void run()
	{
		while(Control.isStart) 
		{
			System.out.println("进入电器线程.....................");
			try {
				Thread.sleep(Control.fiveMinutes);	//假设5分钟一次
				//空调
				if (Control.airCondition.isOn == true) //空调处于打开状态
				{
					if (!Control.airCondition.isSleep)	//空调处于工作状态
					{
						Control.airCondition.currentTempature -= 1;	//每十分钟室温降低2度		
						//满足舒适度
						System.out.println("空调.当前室温(工作中): "+Control.airCondition.currentTempature+"度");
						if (Control.airCondition.currentTempature <= Control.airCondition.setTempature)  
						{
							Control.airCondition.isGood = true;
							Control.airCondition.currentTempature = Control.airCondition.setTempature;
							Control.airCondition.isSleep = true;
							System.out.println("空调.休眠中********************************************");
						}										
					}
					else 				//空调处于休眠状态
					{
						Control.airCondition.currentTempature += 1;	//每十分钟室温升高2度	
						System.out.println("空调.当前室温(休眠中): "+Control.airCondition.currentTempature+"度");	
						if (Control.airCondition.currentTempature >= Control.airCondition.reRunInOnTempature) 
						{
							Control.airCondition.isGood = false;
							Control.airCondition.isSleep = false;
							System.out.println("空调.准备唤醒休眠****************************************");
						}
						if (Control.airCondition.currentTempature >= Control.airCondition.roomTempature)
						{
							Control.airCondition.currentTempature = Control.airCondition.roomTempature;	
						}
					}
				}
				else		//空调处于关闭状态
				{
					Control.airCondition.currentTempature += 1;	//每十分钟室温升高2度	
					if (Control.airCondition.currentTempature >= Control.airCondition.roomTempature)
					{
						Control.airCondition.currentTempature = Control.airCondition.roomTempature;	
					}
				}
				//衣服干洗机器
				if (Control.clothesDry.isOn == true) //干洗机器处于工作状态
				{
					//空调热水器跟干洗机汽车不一样，干洗机汽车是计时的
					Control.clothesDry.currentDryTime += 10;	//十分钟，干洗共需50分钟
					System.out.println("干洗机.当前干洗时间(工作中): "+Control.clothesDry.currentDryTime+"分钟");												
					if (Control.clothesDry.currentDryTime >= Control.clothesDry.needDryTime)
					{
						Control.clothesDry.isOn = false;
						Control.clothesDry.isGood = true;
						System.out.println("Work finished, 干洗机.clothesDry is turned off!");
					}
					else {
						Control.clothesDry.isGood = false;
					}
				}
				//汽车
				if (Control.electronicVehicle.isOn == true) 
				{
					Control.calEVTime++;
					if (Control.calEVTime == 6)	//一个小时，充电共需13个小时
					{
						Control.calEVTime = 0;
						Control.electronicVehicle.currentChargeTime++;
					}
					System.out.println("汽车.当前充电时间(工作中)***************************************:"+Control.electronicVehicle.currentChargeTime+"小时");									
					if (Control.electronicVehicle.currentChargeTime >= Control.electronicVehicle.needChargeTime)
					{
						Control.electronicVehicle.isOn = false;
						Control.electronicVehicle.isGood = true;
						Control.totalPower -= Control.electronicVehicle.power;
						System.out.println("Work finished, 汽车.electronicVehicle is turned off!");
					}	
					else {
						Control.electronicVehicle.isGood = false;
					}
				}
				//热水器
				if (Control.waterHeater.isOn == true) 
				{
					if (!Control.waterHeater.isSleep)
					{
						Control.waterHeater.currentWaterTempature += 10;	//每10分钟水温升高20度
						if(Control.waterHeater.currentWaterTempature >= 100)
						{
							Control.waterHeater.currentWaterTempature = 100;
							Control.waterHeater.isGood = true;
						}
						System.out.println("热水器.当前水温(工作中): "+Control.waterHeater.currentWaterTempature+"度");
						//舒适度判断
						if (Control.waterHeater.currentWaterTempature > Control.waterHeater.reRunInOnTempature && Control.waterHeater.currentWaterTempature < Control.waterHeater.highWaterTempature)
						{
							Control.waterHeater.isGood = true;
						}
						else {
							if (Control.waterHeater.currentWaterTempature >= Control.waterHeater.highWaterTempature)
							{
								Control.waterHeater.isSleep = true;
								Control.waterHeater.isGood = true;
								System.out.println("热水器.进入休眠中********************************************");
							}
						}
					}
					else 
					{
						if (Control.waterHeater.currentWaterTempature > Control.waterHeater.roomWaterTempature) 
						{
							Control.waterHeater.currentWaterTempature -= 3;	//每5分钟水温降低3度
							System.out.println("热水器.当前水温(休眠中): "+Control.waterHeater.currentWaterTempature+"度");
							if (Control.waterHeater.currentWaterTempature <= Control.waterHeater.reRunInOnTempature) {
								Control.waterHeater.isSleep = false;
								Control.waterHeater.isGood = false;
								System.out.println("热水器.准备唤醒休眠****************************************");
							}
						}
						else {
							Control.waterHeater.currentWaterTempature = Control.waterHeater.roomWaterTempature;
						}
					}
				}		
				else     //停机
				{
					if (Control.waterHeater.currentWaterTempature > Control.waterHeater.roomWaterTempature) 
					{
						Control.waterHeater.currentWaterTempature -= 3;	//每5分钟水温降低3度
						if (Control.waterHeater.currentWaterTempature <= Control.waterHeater.roomWaterTempature) 
						{
							Control.waterHeater.currentWaterTempature = Control.waterHeater.roomWaterTempature;
						}
					}
				}								
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}
}

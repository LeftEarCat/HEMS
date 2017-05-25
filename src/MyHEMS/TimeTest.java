package MyHEMS;

public class TimeTest extends Thread
{
	public void run()
	{
	//测试时间用*************************************	
		while (true)
		{
			while (Control.isStart)
			{
				try
				{
					Thread.sleep(Control.fiveMinutes);			//5分钟执行一次
					Control.countfiveMinutes++;
					if (Control.countfiveMinutes == 12)//5分钟
					{
						Control.countfiveMinutes = 0;
						Control.currentHour++;
					 	if (Control.currentHour == 24)
					 	{
					 		Control.currentHour = 0;
					 	}
					}
				//	System.out.println("当前时间.currentHour:"+Control.currentHour+"点钟@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}	
		}		
	//测试时间用*************************************	
	}
	
}

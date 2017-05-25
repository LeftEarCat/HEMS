package MyHEMS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
/**
 * 电器正处于延时阶段，关闭电器是否停止延时，直接关闭呢？如果需要，将beToOn置为false即可。
 *
 */

public class MyFrame extends JFrame
{
	static TextField textField;
	MyFrame(String title)		//绘出一个人机交互面板
	{
		super(title);
		setBounds(200, 100, 300, 300);
		setVisible(true);
		setLayout(new BorderLayout());

		textField = new TextField();
		textField.setEditable(false);
		add(textField, BorderLayout.NORTH);
		
		Panel panel_1 = new Panel();
		panel_1.setLayout(new GridLayout(4, 2, 5, 5));
		

		JButton buttonCD = new JButton("启动干洗机");
		MonitotButtonCD monitotButtonCD = new MonitotButtonCD();
		buttonCD.addActionListener(monitotButtonCD);
		JButton buttonCDKill = new JButton("关闭干洗机");
		MonitotButtonCDKill monitotButtonCDKill = new MonitotButtonCDKill();
		buttonCDKill.addActionListener(monitotButtonCDKill);
		
		JButton buttonEV = new JButton("启动汽车充电");
		MonitorButtonEV monitorButtonEV = new MonitorButtonEV();
		buttonEV.addActionListener(monitorButtonEV);
		JButton buttonEVKill = new JButton("关闭汽车充电");
		MonitorButtonEVKill monitorButtonEVKill = new MonitorButtonEVKill();
		buttonEVKill.addActionListener(monitorButtonEVKill);
		
		JButton buttonAC = new JButton("启动空调");
		MonitorButtonAC monitorButtonAC = new MonitorButtonAC();
		buttonAC.addActionListener(monitorButtonAC);
		JButton buttonACKill = new JButton("关闭空调");
		MonitorButtonACKill monitorButtonACKill = new MonitorButtonACKill();
		buttonACKill.addActionListener(monitorButtonACKill);
		
		JButton buttonWH = new JButton("启动热水器");
		MonitorButtonWH monitorButtonWH = new MonitorButtonWH();
		buttonWH.addActionListener(monitorButtonWH);
		JButton buttonWHKill = new JButton("关闭热水器");
		MonitorButtonWHKill monitorButtonWHKill = new MonitorButtonWHKill();
		buttonWHKill.addActionListener(monitorButtonWHKill);
				
		JButton buttonStart = new JButton("启动系统");
		MonitorButtonStart monitorButtonStart = new MonitorButtonStart();
		buttonStart.addActionListener(monitorButtonStart);
		JButton buttonStop = new JButton("停止系统");
		MonitorButtonStop monitorButtonStop = new MonitorButtonStop();
		buttonStop.addActionListener(monitorButtonStop);
		panel_1.add(buttonWH);
		panel_1.add(buttonWHKill);
		panel_1.add(buttonAC);
		panel_1.add(buttonACKill);	
		panel_1.add(buttonCD);
		panel_1.add(buttonCDKill);
		panel_1.add(buttonEV);
		panel_1.add(buttonEVKill);
		add(panel_1, BorderLayout.CENTER);
		Panel panel_2 = new Panel();
		panel_2.add(buttonStart);
		panel_2.add(buttonStop);
		add(panel_2, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	class MonitotButtonCD implements ActionListener			//开启干洗机，若可以延时则延时开启。其他电器类同
	{
		public void actionPerformed(ActionEvent e)
		{
			if (Control.isStart && !Control.clothesDry.isOn)
			{
				Control.clothesDry.beToOn = true;
				Control.clothesDry.isDelayStart = false;
				Control.clothesDry.isGood = false;
				System.out.println("干洗机.准备打开中###############################");
				if (Control.clothesDry.currentDryTime >= 50)
				{
					Control.clothesDry.currentDryTime = 0;
					Control.clothesDry.isGood = false;
				}
				tryToOpenApp(2);
			}			
		}
		
	}
	class MonitotButtonCDKill implements ActionListener			//关闭干洗机。其他电器类同
	{
		public void actionPerformed(ActionEvent e)
		{
			if (Control.isStart)
			{
				if (Control.clothesDry.isOn)
				{
					Control.clothesDry.isOn = false;					
					System.out.println("干洗机.clothesDry is turned off，停止工作!");
				}
			}			
		}
		
	}
	class MonitorButtonEV implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (Control.isStart && !Control.electronicVehicle.isOn)
			{
				Control.electronicVehicle.beToOn = true;
				Control.electronicVehicle.isDelayStart = false;
				Control.electronicVehicle.isGood = false;
				System.out.println("汽车充电.准备打开中###############################");				
				if (Control.electronicVehicle.currentChargeTime >= 13)
				{
					Control.electronicVehicle.isGood = false;
					Control.electronicVehicle.currentChargeTime = 0;
				}
				tryToOpenApp(3);
			}			
		}
	}
	class MonitorButtonEVKill implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (Control.isStart && Control.electronicVehicle.isOn)
			{
				Control.electronicVehicle.isOn = false;
				System.out.println("汽车.electronicVehicle is turned off，停止工作!");
			}				
		}
	}
	class MonitorButtonAC implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (Control.isStart && !Control.airCondition.isOn)
			{
				Control.airCondition.beToOn =true;
				Control.airCondition.isGood = false;
				Control.airCondition.isDelayStart = false;
				System.out.println("空调.准备打开中###############################");
				tryToOpenApp(1);
			}				
		}
	}
	class MonitorButtonACKill implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (Control.isStart && Control.airCondition.isOn)
			{
				Control.airCondition.isOn = false;
				System.out.println("空调.airCondition is turned off，停止工作!");
			}
		}
	}
	class MonitorButtonWH implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (Control.isStart && ! Control.waterHeater.isOn)
			{
				Control.waterHeater.beToOn = true;
				Control.waterHeater.isGood = false;
				Control.waterHeater.isDelayStart = false;
				System.out.println("热水器.准备打开中###############################");
				tryToOpenApp(0);
			}					
		}
	}
	class MonitorButtonWHKill implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (Control.isStart && Control.waterHeater.isOn)
			{
				Control.waterHeater.isOn = false;
				Control.totalPower -= Control.waterHeater.power;
				System.out.println("热水器.waterHeater is turned off，停止工作!");
			}
		}
	}
	class MonitorButtonStart implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{				
			if (!Control.isStart)
			{
				System.out.println("系统正在启动...");
				Control.isStart = true;
				System.out.println("系统已经启动！");
			}				
		}
	}
	class MonitorButtonStop implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (Control.isStart)
			{
				Control.isStart = false;
				System.out.println("系统正在关闭...");
				try
				{
					Thread.sleep(5000);
				} catch (InterruptedException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("系统已经关闭！");			
				Control.isInit = false;
			}			
		}
	}
	
	static void tryToOpenApp(int i)		//尝试打开电器，如果可以延时打开，则延时打开
	{
		if (Control.appliance[i].beToOn && !Control.appliance[i].isDelayStart)
		{
			Control.appliance[i].delayTime = Control.delayStartOnPeriod(i);
		}		
	}
}






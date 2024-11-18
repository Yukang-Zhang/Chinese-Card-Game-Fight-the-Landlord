package Landlord;
import java.net.*;
import java.beans.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.*;
class EventSource
{
	String name;
	private PropertyChangeSupport listeners=new PropertyChangeSupport(this);
	public void addListener(PropertyChangeListener listener)
	{
		listeners.addPropertyChangeListener(listener);
	}
	
	public String getName()
	{
		return name;
	}
	
	public synchronized void setName(String name1)
	{
		name=name1;
		listeners.firePropertyChange(null,null,getName());
	}
}

class Monitor implements PropertyChangeListener
{
	public int f(char x)
	{
		if(x>='0'&&x<='9')
			return Integer.parseInt(""+x);
		else if(x=='A')
			return 10;
		else if(x=='B')
			return 11;
		else if(x=='C')
			return 12;
		else if(x=='D')
			return 13;
		else if(x=='E')
			return 14;
		else if(x=='F')
			return 15;
		return 0;
	}
	
	public synchronized void propertyChange(PropertyChangeEvent e)
	{
		String message=Game.eventSource.getName();
		System.out.println(message);
		try
		{
			if(message.charAt(0)=='1')
	    	{
				int[] w=new int[55],c=new int[55];
				int len=0;
				for(int i=1;i<message.length();i+=2)
				{
					if(message.charAt(i)=='0')
						break;
					len++;
					w[len]=f(message.charAt(i));
					c[len]=f(message.charAt(i+1));
				}
				
				for(int i=1;i<=len;i++)
				{
					Card card=new Card(w[i],c[i]);
					Game.player[0].allMyCard.addCard(card);
				}
				
				Game.player[0].displayMyCard();
				if(len==3)
					Game.firstPlayer=0;
				else
					Game.firstPlayer=1;
			}
			
			else if(message.charAt(0)=='2')
	    	{
				int[] w=new int[55],c=new int[55];
				int len=0;
				for(int i=1;i<message.length();i+=2)
				{
					if(message.charAt(i)=='0')
						break;
					len++;
					w[len]=f(message.charAt(i));
					c[len]=f(message.charAt(i+1));
				}
				
				for(int i=1;i<=len;i++)
				{
					Card card=new Card(w[i],c[i]);
					Game.player[0].allMyCard.removeCard(card);
				}
			}
			
			else if(message.charAt(0)=='3')
	    	{
				Game.lastCard=new CardSequence();
				int[] w=new int[55],c=new int[55];
				int len=0;
				for(int i=1;i<message.length();i+=2)
				{
					if(message.charAt(i)=='0')
						break;
					len++;
					w[len]=f(message.charAt(i));
					c[len]=f(message.charAt(i+1));
				}
				
				for(int i=1;i<=len;i++)
				{
					Card card=new Card(w[i],c[i]);
					Game.lastCard.addCard(card);
				}
			}
			
			else if(message.charAt(0)=='G')
	    	{
				int who=Integer.parseInt(""+message.charAt(1));
				Game.player[0].readyCard=new CardSequence();
				for(int i=2;i<message.length();i+=2)
				{
					if(message.charAt(i)=='0')
						break;
					Card card=new Card(f(message.charAt(i)),f(message.charAt(i+1)));
					Game.player[0].readyCard.addCard(card);
				}
				
				Game.player[0].displayOtherCard(who);
			}
			
			else if(message.charAt(0)=='5')
	    	{
				Game.noPoint.setVisible(true);
				Game.onePoint.setVisible(true);
				Game.twoPoint.setVisible(true);
				Game.threePoint.setVisible(true);
			}
			
			else if(message.charAt(0)=='6')
	    	{
				Game.failProduce=f(message.charAt(1));
				if(Game.failProduce<2)
					Game.pass.setVisible(true);
				Game.playCard.setVisible(true);
			}
			
			else if(message.charAt(0)=='7')
	    	{
				int i=f(message.charAt(1));
				String name="";
				Game.gameRole[i]=0;
				int j;
				for(j=2;j<message.length();j++)
				{
					if(message.charAt(j)=='_')
						break;
					Game.gameRole[i]=Game.gameRole[i]*10+Integer.parseInt(""+message.charAt(j));
				}
				
				
				for(j++;j<message.length();j++)
					name+=message.charAt(j);
				Game.roleName[i]=name;
				Game.nameFinal[i]=new JTextField(Game.roleName[i]);
				Game.headFinal[i]=new JButton(Game.roleHead[Game.gameRole[i]]);
				Game.displayDetail(i);
				//从服务器端接收另外两个玩家的信息并打印
			}
			
			else if(message.charAt(0)=='8')
	    	{
				String word="";
				for(int i=1;i<message.length();i++)
					word+=message.charAt(i);
				Game.speakString(word);
			}
			
			else if(message.charAt(0)=='9')
	    	{
				int[] w=new int[55],c=new int[55];
				int len=0,who=f(message.charAt(1));
				for(int i=2;i<message.length();i+=2)
				{
					if(message.charAt(i)=='0')
						break;
					len++;
					w[len]=f(message.charAt(i));
					c[len]=f(message.charAt(i+1));
				}
				
				if(len!=0)
					Game.lastCard=new CardSequence();
				Game.player[who].readyCard=new CardSequence();
				for(int i=1;i<=len;i++)
				{
					Card card=new Card(w[i],c[i]);
					Game.lastCard.addCard(card);
					Game.player[who].readyCard.addCard(card);
				}
				
				Game.player[who].displayOtherCard(who);
				String word=("G"+(who==1?"2":"1"));
				for(int i=1;i<=Game.player[who].readyCard.getLen();i++)
					word+=(""+Game.g(Game.player[who].readyCard.findCard(i).getW())+Game.g(Game.player[who].readyCard.findCard(i).getC()));
				word+="00";
				if(who==1)
					Game.player[2].out.writeUTF(word);
				else if(who==2)
					Game.player[1].out.writeUTF(word);
			}
			
			else if(message.charAt(0)=='A')
	    	{
				int who=f(message.charAt(1));
				Game.failProduce=f(message.charAt(2));
				if(Game.failProduce==2)
					Game.lastCard=new CardSequence();
				if(who==2)
				{
					Game.playCard.setVisible(true);
					if(Game.failProduce<2)
						Game.pass.setVisible(true);
				}
				
				else if(who==1)
				{
					String word1="3",word2="6";
					for(int i=1;i<=Game.lastCard.getLen();i++)
						word1+=(""+Game.g(Game.lastCard.findCard(i).getW())+Game.g(Game.lastCard.findCard(i).getC()));
					word1+="00";
					Game.player[2].out.writeUTF(word1);
					word2+=(""+Game.failProduce);
					Game.player[2].out.writeUTF(word2);
				}
			}
			
			else if(message.charAt(0)=='B')
	    	{
				int who=f(message.charAt(1));
				Game.point[who]=f(message.charAt(2));
				if(Game.firstPlayer==0&&who==1)
					Game.player[2].out.writeUTF("5");
				else if(Game.firstPlayer==0&&who==2)
					Game.startGame();
				else if(Game.firstPlayer==1&&who==1)
					Game.player[2].out.writeUTF("5");
				else if(Game.firstPlayer==1&&who==2)
				{
					Game.noPoint.setVisible(true);
					Game.onePoint.setVisible(true);
					Game.twoPoint.setVisible(true);
					Game.threePoint.setVisible(true);
				}
				
				else if(Game.firstPlayer==2&&who==1)
					Game.startGame();
				else if(Game.firstPlayer==2&&who==2)
				{
					Game.noPoint.setVisible(true);
					Game.onePoint.setVisible(true);
					Game.twoPoint.setVisible(true);
					Game.threePoint.setVisible(true);
				}
			}
			
			else if(message.charAt(0)=='C')
	    	{
				int who=f(message.charAt(1));
				String name="";
				Game.gameRole[who]=0;
				int j;
				for(j=2;j<message.length();j++)
				{
					if(message.charAt(j)=='_')
						break;
					Game.gameRole[who]=Game.gameRole[who]*10+Integer.parseInt(""+message.charAt(j));
				}

				for(j++;j<message.length();j++)
					name+=message.charAt(j);
				Game.roleName[who]=name;
				Game.nameFinal[who]=new JTextField(Game.roleName[who]);
				Game.headFinal[who]=new JButton(Game.roleHead[Game.gameRole[who]]);
				Game.displayDetail(who);
				Game.enterFrame.setTitle("玩家"+Game.roleName[who]+"（玩家"+who+"）连接成功！");
				Game.numOfConnected++;
				if(Game.numOfConnected==3)
					Game.giveDetail();
			}
			
			else if(message.charAt(0)=='D')
	    	{
				String word="";
				int who=f(message.charAt(1));
				for(int i=2;i<message.length();i++)
					word+=message.charAt(i);
				Game.speakString(word);
				if(who==1)
					Game.player[2].out.writeUTF("8"+word);
				else
					Game.player[1].out.writeUTF("8"+word);
			}
			
			else if(message.charAt(0)=='E')
			{
				int landlord=Integer.parseInt(""+message.charAt(1));
				Game.headFinal[landlord].setBorder(BorderFactory.createLineBorder(Color.RED));
				Game.nameFinal[landlord].setText(Game.roleName[landlord]+"（地主）");
				//Game.displayDetail(landlord);
				//Game.jFrame.repaint();
			}
			
			else if(message.charAt(0)=='W')
			{
				int winner=Integer.parseInt(""+message.charAt(1));
				if(winner==Game.firstPlayer)
				{
					Game.speakString("地主胜利！");
					Game.player[1].out.writeUTF("8地主胜利！");
					Game.player[2].out.writeUTF("8地主胜利！");
					if(Game.firstPlayer==0)
					{
						Game.speakString("你赢了！");
						Game.player[1].out.writeUTF("8你输了！");
						Game.player[2].out.writeUTF("8你输了！");
					}
					
					else if(Game.firstPlayer==1)
					{
						Game.speakString("你输了！");
						Game.player[1].out.writeUTF("8你赢了！");
						Game.player[2].out.writeUTF("8你输了！");
					}
					
					else if(Game.firstPlayer==2)
					{
						Game.speakString("你输了！");
						Game.player[1].out.writeUTF("8你输了！");
						Game.player[2].out.writeUTF("8你赢了！");
					}
				}
				
				else if(winner!=Game.firstPlayer)
				{
					Game.speakString("农民胜利！");
					Game.player[1].out.writeUTF("8农民胜利！");
					Game.player[2].out.writeUTF("8农民胜利！");
					if(Game.firstPlayer==0)
					{
						Game.speakString("你输了！");
						Game.player[1].out.writeUTF("8你赢了！");
						Game.player[2].out.writeUTF("8你赢了！");
					}
					
					else if(Game.firstPlayer==1)
					{
						Game.speakString("你赢了！");
						Game.player[1].out.writeUTF("8你输了！");
						Game.player[2].out.writeUTF("8你赢了！");
					}
					
					else if(Game.firstPlayer==2)
					{
						Game.speakString("你赢了！");
						Game.player[1].out.writeUTF("8你赢了！");
						Game.player[2].out.writeUTF("8你输了！");
					}
				}
			}
		}
		
		catch(IOException e1)
		{
			e1.printStackTrace();
		}
	}
}

public class Game
{
	static EventSource eventSource=new EventSource();
	static Monitor monitor=new Monitor();
	//变量监听，当接收到网络消息时可以响应
	
	//以下定义一些参数：
	static String serverName="localhost";
	//需连接到的服务器名称，若创建服务器则忽略
	static int gameMode=-1;
	//游戏模式，-1为未选择，0为连接至服务器，1234分别对应下面的四种模式
	static int[] gameRole=new int[3];
	//决定玩家的头像
	static String[] roleName=new String[3];
	//最终确定的玩家昵称
	static JButton[] headFinal=new JButton[3];
	//显示玩家头像
	static JTextField[] nameFinal=new JTextField[3];
	//显示玩家昵称
	
	//以下为游戏主窗口的一些元件
	static JFrame jFrame=new JFrame("斗地主");
	static JPanel playerUnderButton=new JPanel();
	static JButton playCard=new JButton("出牌"),pass=new JButton("不出");
	static JButton noPoint=new JButton("不叫"),onePoint=new JButton("1分"),twoPoint=new JButton("2分"),threePoint=new JButton("3分");
	//以上在playerUnderButton上放置一些游戏中的按钮，默认隐藏
	
	//下面添加一个多行文本框，用来显示游戏中的通知，提示及让玩家输入一些必要信息。
	static JTextArea textArea=new JTextArea("Welcome, ");
	static JScrollPane jScrollPane=new JScrollPane(textArea);
	static JScrollBar jScrollBar=jScrollPane.getVerticalScrollBar();
	//上面是一个通知栏
	
	//创建扑克牌图片对象，到时候根据需要来显示。
	static ImageIcon[] imageIcon=new ImageIcon[21];//牌的图片对象
	static JButton[][] cardPi=new JButton[21][1001];//显示图片的按钮对象
	static int playTimes=0;
	//因为某种原因已经放上去的牌remove不了，所以每次更新加一个playTimes变量，用一组新的cardPi来显示
	//下面一个道理
	
	//上面是玩家面前手牌的图片，下面创建的图片对象用来显示三个玩家面前打出的牌
	static ImageIcon[][] imageIconOut=new ImageIcon[21][3];
	static JButton[][][] cardPiOut=new JButton[21][3][1001];
	static int[] playTimesOut=new int[3];
	
	static int cntLine=0;//统计通知栏的行数
	
	static SecureRandom sr=new SecureRandom();//创建随机变量
	static ImageIcon[] roleHead=new ImageIcon[200];//存储头像图片
	static JButton[] dpHead=new JButton[200];//用来显示头像图片的标签/按钮
	
	static JTextField name[]=new JTextField[200];
	static String[] nameString=new String[]{"Star","Alice","Cat","Zombie","Tom","Zackary","Nothing","Nobody","Flower","Exception","Syschronized","Butterfly","Death","Summer","Winter","Fall","Spring","Enemy","Europa","Io","Mercury","Bella","Future","Million","Weep","Puppet","Roman","Advertisement","Business","Wake","Rachel","Diane","Isolation","Downward","Spiritualized","Penman","Red","Yellow","Purple","Blue","Green","Brown","Black","White","Pink","Grey","One","Two","Three","Four","Five","Six","Seven","Nine","Thought","Would","Marcus","Corinna","Libertine","Katie","Anthrax"};
	//存放玩家昵称
	
	static int numOfPlayer=3;//三个玩家
	static Player[] player=new Player[numOfPlayer];
	
	static JFrame enterFrame=new JFrame("欢迎来到斗地主!请选择一个游戏模式，点击头像可更换");
	//登录窗口
	
	static int[] point=new int[]{-1,-1,-1};//存储玩家叫分
	static ServerSocket serverSocket;
	
	static int runned=0,mainRun=1;
	//如果当前要运行一个线程，就用这两个变量做标记。runned表示当前线程是否完成运行，mainRun表示主线程是否继续运行。
	//这样可以确保线程运行的先后顺序
	static int firstPlayer;
	//第一个操作的玩家
	
	static CardSequence allCard=new CardSequence();
	static int failProduce=0;
	
	static int numOfConnected=1;
	public static CardSequence lastCard=new CardSequence();
	public static String g(int x)
	{
		if(x<=9)
			return ""+x;
		else if(x==10)
			return "A";
		else if(x==11)
			return "B";
		else if(x==12)
			return "C";
		else if(x==13)
			return "D";
		else if(x==14)
			return "E";
		else if(x==15)
			return "F";
		return "FUCK";
	}
	
	public static void main(String[] args) throws InterruptedException
	{
		eventSource.addListener(monitor);
		//设置游戏窗口的大小和位置
		jFrame.setSize(1200,600);
		jFrame.setLocation(200,150);
		//设置窗口布局和不可调整大小
		jFrame.setLayout(null);
		jFrame.setResizable(false);
		playerUnderButton.setBounds(200,360,700,40);
		//playerUnderButton.setBackground(Color.BLUE);
		jFrame.add(playerUnderButton);
		playerUnderButton.add(pass);
		playerUnderButton.add(playCard);
		playerUnderButton.add(noPoint);
		playerUnderButton.add(onePoint);
		playerUnderButton.add(twoPoint);
		playerUnderButton.add(threePoint);
		//在playerUnderButton上添加按钮
		
		noPoint.setVisible(false);
		onePoint.setVisible(false);
		twoPoint.setVisible(false);
		threePoint.setVisible(false);
		playCard.setVisible(false);
		pass.setVisible(false);
		//playerUnderButton.setVisible(false);
		//开始时全部设为不可见，当需要操作时再显示。
		
		setButton();//设置以上按钮的功能
		textArea.setLineWrap(true);//自动换行
		jScrollPane.setBounds(990,300,300,258);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jFrame.add(jScrollPane);
		//通知栏
		
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//上面是主游戏窗口，现在先做一个登录界面，登录之后再显示主窗口。
		
		enterFrame.setSize(480,200);
		enterFrame.setLocation(600,300);
		enterFrame.setResizable(false);
		enterFrame.setLayout(null);
		enterFrame.setVisible(true);
		
		for(int i=0;i<68;i++)
		{
			roleHead[i]=new ImageIcon("head\\"+(i+1)+".png");
		}
		
		//玩家头像列表
		for(int i=0;i<61;i++)
		{
			dpHead[i]=new JButton(roleHead[i]);
			dpHead[i].setBounds(330,10,100,100);
			name[i]=new JTextField(nameString[i]);
			name[i].setBounds(330,120,100,28);
			//设置一下点击头像随机切换
			dpHead[i].addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					int lstGameRole=gameRole[0];
					enterFrame.remove(dpHead[gameRole[0]]);
					enterFrame.remove(name[gameRole[0]]);
					//移除旧的
					while(gameRole[0]==lstGameRole)
						gameRole[0]=(sr.nextInt()%68+68)%68;//随机改一个
					name[gameRole[0]].setText(nameString[(sr.nextInt()%68+68)%68]);
					enterFrame.add(dpHead[gameRole[0]]);
					enterFrame.add(name[gameRole[0]]);
					//添加新的
					enterFrame.repaint();
				}
			});		
		}
		
		gameRole[0]=(sr.nextInt()%68+68)%68;
		enterFrame.add(dpHead[gameRole[0]]);
		//显示头像的按钮
		enterFrame.add(name[gameRole[0]]);
		//输入服务器地址以连接
		JTextField getServerName=new JTextField("localhost");
		getServerName.setBounds(20,10,130,30);
		enterFrame.add(getServerName);
		
		//或创建一个服务器，四种游戏模式
		JLabel createServer=new JLabel("或创建一个游戏:");
		createServer.setBounds(20,50,150,28);
		createServer.setFont(new Font("仿宋",1,15));
		enterFrame.add(createServer);
		JButton[] mod=new JButton[5];
		mod[0]=new JButton("连接到此服务器");
		mod[1]=new JButton("斗地主");
		mod[2]=new JButton("跑得快");
		mod[3]=new JButton("斗地主不洗牌");
		mod[4]=new JButton("跑得快不洗牌");
		mod[0].setBounds(155,10,130,28);
		mod[1].setBounds(20,80,130,28);
		mod[2].setBounds(155,80,130,28);
		mod[3].setBounds(20,120,130,28);
		mod[4].setBounds(155,120,130,28);
		for(int i=0;i<5;i++)
		{
			enterFrame.add(mod[i]);
			final int i1=i;
			mod[i].addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					gameMode=i1;
					roleName[0]=name[gameRole[0]].getText();
					serverName=getServerName.getText();//获取输入的服务器名称，创建服务器则忽略
					headFinal[0]=new JButton(roleHead[gameRole[0]]);
					speakString(roleName[0]+"!");//显示一条欢迎信息
					headFinal[0].setBounds(50,425,100,100);
					jFrame.add(headFinal[0]);//显示自己的头像
					nameFinal[0]=new JTextField(roleName[0]);
					nameFinal[0].setBounds(50,530,100,28);
					nameFinal[0].setEditable(false);
					jFrame.add(nameFinal[0]);//显示自己的昵称
					//jFrame.repaint();
					for(int j=0;j<5;j++)
						mod[j].setVisible(false);
					try
					{
						switch(gameMode)
						{
							case 0:
								runClient();//客户端
								break;
							case 1:
								//Thread.sleep(1000);
								runServer1();//斗地主
								break;
							case 2:
								runServer2();//跑得快
								break;
							case 3:
								runServer3();//斗地主不洗牌
								break;
							case 4:
								runServer4();//跑得快不洗牌
								break;
						}
					}
					
					catch(IOException | InterruptedException e1)
					{
						e1.printStackTrace();
					}
							
				}
			});
		}
		
		enterFrame.repaint();
	}
	
	public static void runClient() throws IOException,InterruptedException
	{
		enterFrame.setTitle("正在连接至服务器...");
		player[0]=new Player(0,0);
		//创建本地玩家对象
		Thread.sleep(1);
		enterFrame.setTitle("连接成功！等待游戏开始...");
		//speakString("游戏开始！");
		enterFrame.setVisible(false);//隐藏登录界面
		jFrame.setVisible(true);//显示游戏主窗口
	}
	
	public static void runServer1() throws IOException,InterruptedException
	{
		speakString("我是服务器");
		InetAddress addr=InetAddress.getLocalHost();
		enterFrame.setTitle(addr.getHostAddress()+"等待连接中...");
		serverSocket=new ServerSocket(1437);
		serverSocket.setSoTimeout(300000);
		for(int i=0;i<3;i++)
			player[i]=new Player(i,1);
		enterFrame.setVisible(false);//隐藏登录界面
		jFrame.setVisible(true);//显示游戏主窗口
	}
	
	public static void runServer2()
	{
		//jScrollBar.setValue(cntLine*18);
		//textArea.append("Server2");
		speakString("等待其他玩家连接中");
	}
	
	public static void runServer3()
	{
		//jScrollBar.setValue(cntLine*18);
		//textArea.append("Server3");
		speakString("等待其他玩家连接中");
	}
	
	public static void runServer4()
	{
		//jScrollBar.setValue(cntLine*18);
		//textArea.append("Server4");
		speakString("等待其他玩家连接中");
	}
	
	public static void speakString(String words)
	{
		jScrollBar.setValue(cntLine*18);
		textArea.append(words+"\n");
		cntLine++;
	}
	
	public static void displayDetail(int i)
	{
		if(i==1)
		{
			headFinal[i].setBounds(1050,125,100,100);
			jFrame.add(headFinal[i]);
			nameFinal[i].setBounds(1050,230,100,28);
			nameFinal[i].setEditable(false);
			jFrame.add(nameFinal[i]);
		}
		
		else if(i==2)
		{
			headFinal[i].setBounds(50,125,100,100);
			jFrame.add(headFinal[i]);
			nameFinal[i].setBounds(50,230,100,28);
			nameFinal[i].setEditable(false);
			jFrame.add(nameFinal[i]);
		}
		
		else if(i==0)
		{
			headFinal[i].setBounds(50,425,100,100);
			jFrame.add(headFinal[i]);
			nameFinal[i].setBounds(50,530,100,28);
			nameFinal[i].setEditable(false);
			jFrame.add(nameFinal[i]);
		}
		
		jFrame.repaint();
	}
	
	public static void dealCard1() throws IOException, InterruptedException
	{
		
	}
	
	public static void dealCard2()
	{
		
	}
	
	public static void dealCard3()
	{
		
	}
	
	public static void dealCard4()
	{
		
	}
	
	public static void giveDetail() throws IOException
	{
		//player[i].speakToClient("Start");
		//发送开始信号，客户端接收信号后再分别接收两个玩家的信息
		//对于每个玩家来说，他自己的编号为0，下家为1，上家为2
		String word1="72";
		word1+=(""+gameRole[0]+"_"+roleName[0]);
		player[1].out.writeUTF(word1);
		String word2="71";
		word2+=(""+gameRole[2]+"_"+roleName[2]);
		player[1].out.writeUTF(word2);
		
		word1="71";
		word1+=(""+gameRole[0]+"_"+roleName[0]);
		player[2].out.writeUTF(word1);
		word2="72";
		word2+=(""+gameRole[1]+"_"+roleName[1]);
		player[2].out.writeUTF(word2);
		
		speakString("游戏开始！");
		player[1].out.writeUTF("8游戏开始！");
		player[2].out.writeUTF("8游戏开始！");
		
		//发牌
		int[] w=new int[55],c=new int[55];
		int cnt=0;
		for(int i=1;i<=13;i++)
			for(int j=1;j<=4;j++)
			{
				w[++cnt]=i;
				c[cnt]=j;
			}
			
		w[53]=14;
		w[54]=15;
		c[53]=1;
		c[54]=1;
		cnt+=2;
		allCard=new CardSequence(w,c,cnt);
		CardSequence[] givePlayerCard=new CardSequence[3];
		for(int i=0;i<3;i++)
		{
			givePlayerCard[i]=new CardSequence();
			for(int j=1+17*i;j<=17+17*i;j++)
				givePlayerCard[i].addCard(allCard.findCardRand(j));
		}
		
		for(int i=0;i<3;i++)
		{
			if(i==0)
			{
				player[0].allMyCard=givePlayerCard[i];
				player[0].displayMyCard();
			}
			
			else
			{
				String word="1";
				for(int j=1;j<=givePlayerCard[i].getLen();j++)
					word+=(""+g(givePlayerCard[i].findCard(j).getW())+g(givePlayerCard[i].findCard(j).getC()));
				word+="00";
				player[i].out.writeUTF(word);
			}
		}
		
		firstPlayer=(sr.nextInt()%3+3)%3;
		if(firstPlayer==0)
		{
			noPoint.setVisible(true);
			onePoint.setVisible(true);
			twoPoint.setVisible(true);
			threePoint.setVisible(true);
		}
		
		else
		{
			player[firstPlayer].out.writeUTF("5");
		}
	}
	
	public static void startGame() throws IOException
	{
		int maxPoint=-1,landlord=0;
		for(int i=0;i<3;i++)
			if(point[i]>maxPoint)
			{
				maxPoint=point[i];
				landlord=i;
			}
			
		headFinal[landlord].setBorder(BorderFactory.createLineBorder(Color.RED));
		nameFinal[landlord].setText(Game.roleName[landlord]+"（地主）");
		firstPlayer=landlord;
		//jFrame.repaint();
		//Game.displayDetail(landlord);
		if(landlord==0)
		{
			for(int i=52;i<=54;i++)
				player[0].allMyCard.addCard(allCard.findCardRand(i));
			player[0].displayMyCard();
			playCard.setVisible(true);
			player[1].out.writeUTF("E2");
			player[2].out.writeUTF("E1");
			
		}
		
		else
		{
			if(landlord==1)
			{
				player[1].out.writeUTF("E0");
				player[2].out.writeUTF("E2");
			}
			
			else
			{
				player[1].out.writeUTF("E1");
				player[2].out.writeUTF("E0");
			}
			
			String word="1";
			for(int i=52;i<=54;i++)
				word+=(""+g(allCard.findCardRand(i).getW())+g(allCard.findCardRand(i).getC()));
			word+="00";
			player[landlord].out.writeUTF(word);
			/*给客户端发送出牌信息
			 * 先发送上一手出的牌，再发送failProduce
			 */
			word="300";//这里没有上一手
			player[landlord].out.writeUTF(word);
			player[landlord].out.writeUTF("6"+failProduce);
		}
		
		
	}
	
	public static void setButton()
	{
		noPoint.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				point[0]=0;
				noPoint.setVisible(false);
				onePoint.setVisible(false);
				twoPoint.setVisible(false);
				threePoint.setVisible(false);
				try
				{
					if(gameMode==0)
						player[0].out.writeUTF("B"+point[0]);
					else
					{
						if(firstPlayer==0||firstPlayer==2)
							player[1].out.writeUTF("5");
						else if(firstPlayer==1)
							startGame();
					}
				}
				
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		onePoint.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				point[0]=1;
				noPoint.setVisible(false);
				onePoint.setVisible(false);
				twoPoint.setVisible(false);
				threePoint.setVisible(false);
				try
				{
					if(gameMode==0)
						player[0].out.writeUTF("B"+point[0]);
					else
					{
						if(firstPlayer==0||firstPlayer==2)
							player[1].out.writeUTF("5");
						else if(firstPlayer==1)
							startGame();
					}
				}
				
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		twoPoint.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				point[0]=2;
				noPoint.setVisible(false);
				onePoint.setVisible(false);
				twoPoint.setVisible(false);
				threePoint.setVisible(false);
				try
				{
					if(gameMode==0)
						player[0].out.writeUTF("B"+point[0]);
					else
					{
						if(firstPlayer==0||firstPlayer==2)
							player[1].out.writeUTF("5");
						else if(firstPlayer==1)
							startGame();
					}
				}
				
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		threePoint.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				point[0]=3;
				noPoint.setVisible(false);
				onePoint.setVisible(false);
				twoPoint.setVisible(false);
				threePoint.setVisible(false);
				try
				{
					if(gameMode==0)
						player[0].out.writeUTF("B"+point[0]);
					else
					{
						if(firstPlayer==0||firstPlayer==2)
							player[1].out.writeUTF("5");
						else if(firstPlayer==1)
							startGame();
					}
				}
				
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		pass.addActionListener(new ActionListener()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if(gameMode==0)
					{
						player[0].out.writeUTF("900");
						failProduce++;
						player[0].out.writeUTF("A"+failProduce);
					}
					
					else
					{
						failProduce++;
						if(failProduce==2)
							player[1].out.writeUTF("300");
						else
						{
							String word="3";
							for(int i=1;i<=lastCard.getLen();i++)
								word+=(""+g(lastCard.findCard(i).getW())+g(lastCard.findCard(i).getC()));
							player[1].out.writeUTF(word);
						}
						
						player[1].out.writeUTF("6"+failProduce);
					}
					
					playCard.setVisible(false);
					pass.setVisible(false);
				}
				
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
		playCard.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					player[0].readyCard=new CardSequence();
					for(int i=1;i<=player[0].allMyCard.getLen();i++)
						if(player[0].chosen[i]==1)
						{
							player[0].readyCard.addCard(player[0].allMyCard.findCard(i));
							
						}
					
					if(!player[0].readyCard.kill(lastCard))
					{
						speakString("你选择的牌不符合游戏规则！");
						for(int i=1;i<=player[0].allMyCard.getLen();i++)
						{
							player[0].chosen[i]=0;
							Game.cardPi[i][Game.playTimes].setBounds(540+35*(i-player[0].allMyCard.getLen()/2-1),408,i<player[0].allMyCard.getLen()?35:105,150);
							Game.jFrame.repaint();
						}
						
						return;
					}
					
					for(int i=1;i<=player[0].readyCard.getLen();i++)
						player[0].allMyCard.removeCard(player[0].readyCard.findCard(i));
					player[0].displayMyCard();
					player[0].displayOtherCard(0);
					lastCard=new CardSequence();
					lastCard.len=player[0].readyCard.getLen();
					for(int i=1;i<=player[0].readyCard.getLen();i++)
					{
						lastCard.card[i].setW(player[0].readyCard.findCard(i).getW());
						lastCard.card[i].setC(player[0].readyCard.findCard(i).getC());
					}
					
					if(gameMode==0)
					{
						String word="9";
						for(int i=1;i<=player[0].readyCard.getLen();i++)
						{
							word+=(""+g(player[0].readyCard.findCard(i).getW())+g(player[0].readyCard.findCard(i).getC()));
							//System.out.println(word);
						}
						
						word+="00";
						player[0].out.writeUTF(word);
						if(player[0].allMyCard.getLen()==0)
							player[0].out.writeUTF("W"+firstPlayer);
						else
							player[0].out.writeUTF("A0");
					}
					
					else
					{
						String word="G2";
						for(int i=1;i<=player[0].readyCard.getLen();i++)
							word+=(""+g(player[0].readyCard.findCard(i).getW())+g(player[0].readyCard.findCard(i).getC()));
						word+="00";
						player[1].out.writeUTF(word);
						word="G1";
						for(int i=1;i<=player[0].readyCard.getLen();i++)
							word+=(""+g(player[0].readyCard.findCard(i).getW())+g(player[0].readyCard.findCard(i).getC()));
						word+="00";
						player[2].out.writeUTF(word);
						word="3";
						for(int i=1;i<=player[0].readyCard.getLen();i++)
							word+=(""+g(player[0].readyCard.findCard(i).getW())+g(player[0].readyCard.findCard(i).getC()));
						word+="00";
						player[1].out.writeUTF(word);
						if(player[0].allMyCard.getLen()==0)
						{
							speakString(firstPlayer==0?"地主胜利！":"农民胜利！");
							player[1].out.writeUTF("8"+(firstPlayer==0?"地主胜利！":"农民胜利！"));
							player[2].out.writeUTF("8"+(firstPlayer==0?"地主胜利！":"农民胜利！"));
							speakString("你赢了!");
							if(firstPlayer==0)
							{
								player[1].out.writeUTF("8你输了！");
								player[2].out.writeUTF("8你输了！");
							}
							
							else if(firstPlayer==1)
							{
								player[1].out.writeUTF("8你输了！");
								player[2].out.writeUTF("8你赢了！");
							}
							
							else if(firstPlayer==2)
							{
								player[1].out.writeUTF("8你赢了！");
								player[2].out.writeUTF("8你输了！");
							}
							
							player[1].out.writeUTF("P");
							player[2].out.writeUTF("P");
						}
						
						else
							player[1].out.writeUTF("60");
					}
					
					playCard.setVisible(false);
					pass.setVisible(false);
				}
				
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});
	}
	
}













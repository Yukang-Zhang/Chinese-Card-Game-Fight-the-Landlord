package Landlord;

import java.net.*;
import java.net.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.*;
public class Player
{
	public Socket server;
	public DataInputStream in;
	public DataOutputStream out;
	public int who=0;
	public CardSequence allMyCard=new CardSequence();//玩家手牌
	public CardSequence readyCard=new CardSequence();
	public int len=0;//手牌长度
	public int[] chosen=new int[50];//选中的牌
	public int[] readyLen=new int[]{0,0,0};
	public ActionListener[][] al=new ActionListener[21][1001];
	public ActionListener[][][] alOther=new ActionListener[21][3][1001];
	
	public Player(int i,int j) throws IOException, InterruptedException
	{
		//i表示玩家身份，0为自己。
		//j表示是否是服务器，如果i不为0肯定是服务器，如果i为0，j为0，则为客户端，需要考虑创建与服务器的连接
		//如果i为0,j为1就是服务器本地，不用处理
		if(i==0&&j==0)//客户端本地玩家
		{
			server=new Socket(Game.serverName,1437);
			in=new DataInputStream(server.getInputStream());
			out=new DataOutputStream(server.getOutputStream());
			String word="C";
			word+=(""+Game.gameRole[0]+"_"+Game.roleName[0]);
			out.writeUTF(word);
			new Thread(new Runnable()
			{
				/*
				 * 接收消息
				 * 消息格式：
				 * 
				 * 添加牌："111121314E1F100"，其中开头一个1表示指令种类
				 * 之后跟着一堆数字和字母，表示牌的大小和花色，1-9表示3-J，A-F表示Q-2和大小王
				 * 1-4表示四种花色，读到0 0则结束
				 * 
				 * 减少牌：同上，开头1改成2
				 * （好像用不到）
				 * 
				 * 告诉客户端上一手出的牌：同上，开头3
				 * 
				 * 开头4，后接1表示相对于该玩家的玩家1面前的牌，需要显示
				 * 后接2同理
				 * 
				 * 开头5，要玩家叫分
				 * 
				 * 开头6，要玩家出牌，后接failProduce=“目前要不起上一手牌的玩家数量”
				 * 如果这个值为2，不显示不出的按钮。如果玩家选择出牌，把这个值改为0
				 * 
				 * 开头7，后接1或二表示相对的玩家1和2，后接该玩家头像编号,玩家昵称
				 * 如："710_Alice ","721_Bell "
				 * 
				 * 开头8，客户端显示字符串
				 * 
				 * 开头E，后接地主编号，显示地主是谁
				 * 
				 * 开头G，后接1，2表示玩家编号，后接该玩家面前的牌
				 * 
				 * 开头P，游戏结束
				 * 
				 * 更改Game.eventSource以作出响应
				 * 
				 * 
				 */
				public void run()
				{
					try
					{
						while(true)
						{
							String receive=in.readUTF();
							Game.eventSource.setName(receive);
						}
					}
					
					catch(IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}).start();
		}
		
		else if(i!=0)//服务器处理远程玩家
		{
			who=i;
			server=Game.serverSocket.accept();
			in=new DataInputStream(server.getInputStream());
			out=new DataOutputStream(server.getOutputStream());
			final int i1=i;
			new Thread(new Runnable()
			{
				/*
				 * 接收消息
				 * 开头9，接收该玩家出的牌，格式同上
				 * 
				 * 开头A，后接failProduce值
				 * 
				 * 开头B，后跟玩家叫分结果
				 * 
				 * 开头C，接收玩家信息，同上7
				 * 
				 * 开头D，后接需要输出的字符串
				 * 
				 * 开头F，后接这个玩家面前打出的牌
				 * 
				 * 开头W，后接胜利者的身份，地主0，农民1
				 * 
				 * 更改Game.eventSource以作出响应
				 * 
				 * 在开头后加一个数字i，不然不知道消息是哪来的
				 */
				public void run()
				{
					try
					{
						while(true)
						{
							String receive=in.readUTF();
							String word=""+receive.charAt(0);
							word+=(""+i1);
							for(int i=1;i<receive.length();i++)
								word+=receive.charAt(i);
							Game.eventSource.setName(word);
						}
					}
					
					catch(IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	public void displayMyCard()
	{
		for(int i=1;i<=len;i++)
			Game.jFrame.remove(Game.cardPi[i][Game.playTimes]);
		len=allMyCard.getLen();
		Game.playTimes++;
		for(int i=1;i<=len;i++)
		{
			final int i1=i;
			chosen[i]=0;
			al[i][Game.playTimes]=new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if(chosen[i1]==0)
					{
						chosen[i1]=1;
						Game.cardPi[i1][Game.playTimes].setBounds(540+35*(i1-len/2-1),398,i1<len?35:105,150);
						Game.jFrame.repaint();
					}
					
					else
					{
						chosen[i1]=0;
						Game.cardPi[i1][Game.playTimes].setBounds(540+35*(i1-len/2-1),408,i1<len?35:105,150);
						Game.jFrame.repaint();
					}
				}
			};
			String piName=(i<len?"cardcut\\":"card\\")+allMyCard.findCard(i).getW()+"_"+allMyCard.findCard(i).getC()+".jpg";
			//System.out.println(piName);
			Game.imageIcon[i]=new ImageIcon(piName);
			Game.cardPi[i][Game.playTimes]=new JButton(Game.imageIcon[i]);
			Game.cardPi[i][Game.playTimes].setBounds(540+35*(i-len/2-1),408,i<len?35:105,150);
			Game.cardPi[i][Game.playTimes].addActionListener(al[i][Game.playTimes]);
			Game.jFrame.add(Game.cardPi[i][Game.playTimes]);
			Game.jFrame.repaint();
		}
		
		Game.jFrame.repaint();
	}
	
	public void displayOtherCard(int p)
	{
		if(p==0)
		{
			for(int i=1;i<=readyLen[0];i++)
				Game.jFrame.remove(Game.cardPiOut[i][0][Game.playTimesOut[0]]);
			readyLen[0]=readyCard.getLen();
			Game.playTimesOut[0]++;
			for(int i=1;i<=readyLen[0];i++)
			{
				String piName=(i<readyLen[0]?"cardcutsmall\\":"cardsmall\\")+readyCard.findCard(i).getW()+"_"+readyCard.findCard(i).getC()+".jpg";
				Game.imageIconOut[i][0]=new ImageIcon(piName);
				Game.cardPiOut[i][0][Game.playTimesOut[0]]=new JButton(Game.imageIconOut[i][0]);
				Game.cardPiOut[i][0][Game.playTimesOut[0]].setBounds(540+23*(i-readyLen[0]/2-1),260,i<readyLen[0]?23:70,100);
				Game.jFrame.add(Game.cardPiOut[i][0][Game.playTimesOut[0]]);
				Game.jFrame.repaint();
			}
			
			Game.jFrame.repaint();
		}
		
		else if(p==1)
		{
			for(int i=1;i<=readyLen[1];i++)
				Game.jFrame.remove(Game.cardPiOut[i][1][Game.playTimesOut[1]]);
			readyLen[1]=readyCard.getLen();
			Game.playTimesOut[1]++;
			for(int i=1;i<=readyLen[1];i++)
			{
				String piName=(i<readyLen[1]?"cardcutsmall\\":"cardsmall\\")+readyCard.findCard(i).getW()+"_"+readyCard.findCard(i).getC()+".jpg";
				Game.imageIconOut[i][1]=new ImageIcon(piName);
				Game.cardPiOut[i][1][Game.playTimesOut[1]]=new JButton(Game.imageIconOut[i][1]);
				Game.cardPiOut[i][1][Game.playTimesOut[1]].setBounds(940-23*readyLen[1]+23*i,125,i<readyLen[1]?23:70,100);
				Game.jFrame.add(Game.cardPiOut[i][1][Game.playTimesOut[1]]);
				Game.jFrame.repaint();
			}
			
			Game.jFrame.repaint();
		}
		
		else if(p==2)
		{
			for(int i=1;i<=readyLen[2];i++)
				Game.jFrame.remove(Game.cardPiOut[i][2][Game.playTimesOut[2]]);
			readyLen[2]=readyCard.getLen();
			Game.playTimesOut[2]++;
			for(int i=1;i<=readyLen[2];i++)
			{
				String piName=(i<readyLen[2]?"cardcutsmall\\":"cardsmall\\")+readyCard.findCard(i).getW()+"_"+readyCard.findCard(i).getC()+".jpg";
				Game.imageIconOut[i][2]=new ImageIcon(piName);
				Game.cardPiOut[i][2][Game.playTimesOut[2]]=new JButton(Game.imageIconOut[i][2]);
				Game.cardPiOut[i][2][Game.playTimesOut[2]].setBounds(140+23*i,125,i<readyLen[2]?23:70,100);
				Game.jFrame.add(Game.cardPiOut[i][2][Game.playTimesOut[2]]);
				Game.jFrame.repaint();
			}
			
			Game.jFrame.repaint();
		}
	}
}


















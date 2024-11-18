package Landlord;
import java.net.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.*;
class Card
{
	public int weight,color;
	public Card(int w,int c)
	{
		weight=w;
		color=c;
	}
	
	public int getW()
	{
		return weight;
	}
	
	public int getC()
	{
		return color;
	}
	
	public void setW(int x)
	{
		weight=x;
	}
	
	public void setC(int c)
	{
		color=c;
	}
}

public class CardSequence
{
	public Card[] card=new Card[200],cardRand=new Card[200];
	public int[] cardAbstract=new int[200];
	public int len=0;
	public CardSequence(int[] w,int[] c,int n)
	{
		for(int i=1;i<=n;i++)
			card[i]=new Card(w[i],c[i]);
		len=n;
		for(int i=1;i<=n;i++)
			for(int j=i+1;j<=n;j++)
				if(card[i].getW()>card[j].getW()||card[i].getW()==card[j].getW()&&card[i].getC()>card[j].getC())
				{
					Card p0=card[i];
					card[i]=card[j];
					card[j]=p0;
				}
				
		randSequence();
		abstractSequence();
	}
	
	public CardSequence()
	{
		len=0;
		for(int i=1;i<=54;i++)
			card[i]=cardRand[i]=new Card(0,0);
		for(int i=1;i<=15;i++)
			cardAbstract[i]=0;
	}
	
	/*public void change(CardSequence newCard,int l,int r)
	{
		len=r-l+1;
		for(int i=l;i<=r;i++)
			card[i-l+1]=newCard.findCard(i);
		for(int i=1;i<=len;i++)
			for(int j=i+1;j<=len;j++)
				if(card[i].getW()>card[j].getW()||card[i].getW()==card[j].getW()&&card[i].getC()>card[j].getC())
				{
					Card p0=card[i];
					card[i]=card[j];
					card[j]=p0;
				}
				
		randSequence();
		abstractSequence();
	}*/
	
	/*public void changeRand(CardSequence newCard,int l,int r)
	{
		len=r-l+1;
		for(int i=l;i<=r;i++)
		{
			card[i-l+1]=newCard.findCardRand(i);
		}
		
		for(int i=1;i<=len;i++)
			for(int j=i+1;j<=len;j++)
				try
				{
					if(card[i].getW()>card[j].getW()||card[i].getW()==card[j].getW()&&card[i].getC()>card[j].getC())
					{
						Card p0=card[i];
						card[i]=card[j];
						card[j]=p0;
					}
				}
				
				catch(NullPointerException e)
				{
					e.printStackTrace();
				}
		
		randSequence();
		abstractSequence();
	}*/
	
	public void randSequence()
	{
		for(int i=1;i<=len;i++)
			cardRand[i]=card[i];
		SecureRandom sr=new SecureRandom();
		for(int i=1;i<=len;i++)
		{
			int p=(sr.nextInt()%(len+1-i)+(len+1-i))%(len+1-i)+1;
			Card p0=cardRand[len+1-i];
			cardRand[len+1-i]=cardRand[p];
			cardRand[p]=p0;
		}
	}
	
	public void abstractSequence()
	{
		for(int i=1;i<=15;i++)
			cardAbstract[i]=0;
		for(int i=1;i<=len;i++)
			cardAbstract[card[i].getW()]++;
	}
	
	public int type()
	{
		/* 这里返回该组牌的类型，规定：
		 * 单牌：1
		 * 对子：2
		 * 三张：3
		 * 三带一：4
		 * 三带一对：5
		 * 单顺子：顺子的长度+1，如3 4 5 6 7返回6
		 * 双顺子（连对）：顺子的长度*20，如3 3 4 4 5 5返回60
		 * 三顺子（飞机）不带牌：顺子的长度*200，如333444返回400
		 * 三顺子带单张：顺子的长度*200+1，如333444+56返回401
		 * 三顺子带对子：顺子的长度*200+2，如333444+5566返回402
		 * 四带二：10002
		 * 四带两对：10004
		 * 炸弹：100000（遇到炸弹就不需要考虑类型了）
		 * 王炸：100000
		 * 如果不属于以上情况返回0，表示输入错误或空
		 */
		 
		int t=0;
		if(len==0)
			return 0;
		else if(len==1)
			return 1;//单
		else if(len==2&&card[1].getW()==card[2].getW())
			return 2;//对
		else if(len==2&&card[1].getW()==14&&card[2].getW()==15||len==4&&cardAbstract[card[1].getW()]==4)
			return 100000;//炸
		else if(len==3&&cardAbstract[card[1].getW()]==3)
			return 3;//三张
		else if(len==4&&(cardAbstract[card[1].getW()]==3||cardAbstract[card[2].getW()]==3))
			return 4;//三带一
		else if(len==5&&(card[1].getW()==card[2].getW()&&card[1].getW()==card[3].getW()&&card[4].getW()==card[5].getW()||card[3].getW()==card[4].getW()&&card[3].getW()==card[5].getW()&&card[1].getW()==card[2].getW()))
			return 5;//三带二
		else if(len==6&&(card[1].getW()==card[2].getW()&&card[1].getW()==card[3].getW()&&card[1].getW()==card[4].getW()||card[2].getW()==card[3].getW()&&card[2].getW()==card[4].getW()&&card[2].getW()==card[5].getW()||card[3].getW()==card[4].getW()&&card[3].getW()==card[5].getW()&&card[3].getW()==card[6].getW()))
			return 10002;//四带二
		else if(len==8&&(card[1].getW()==card[2].getW()&&card[1].getW()==card[3].getW()&&card[1].getW()==card[4].getW()&&card[5].getW()==card[6].getW()&&card[7].getW()==card[8].getW()||card[1].getW()==card[2].getW()&&card[3].getW()==card[4].getW()&&card[3].getW()==card[5].getW()&&card[3].getW()==card[6].getW()&&card[7].getW()==card[8].getW()||card[1].getW()==card[2].getW()&&card[3].getW()==card[4].getW()&&card[5].getW()==card[6].getW()&&card[5].getW()==card[7].getW()&&card[5].getW()==card[8].getW()))
			return 10004;
		else if((t=this.isSingleStraight())!=0)
			return t+1;//单顺子
		else if((t=this.isDoubleStraight())!=0)
			return t*20;//连对
		else if((t=this.isAeroplane())!=0)
			return t*200;//飞机
		else if((t=this.isAeroplaneAndOne(0))!=0)
			return t*200+1;//飞机带单
		else if((t=this.isAeroplaneAndDouble(0))!=0)
			return t*200+2;//飞机带对
		else
			return 0;
	}
	
	private int isSingleStraight()
	{
		if(len<5)
			return 0;
		for(int i=2;i<=len;i++)
			if(card[i].getW()!=card[i-1].getW()+1||card[i].getW()>12)
				return 0;
		return len;
	}
	
	private int isDoubleStraight()
	{
		if(len<6||card[2].getW()!=card[1].getW()||len%2!=0)
			return 0;
		for(int i=4;i<=len;i+=2)
			if(card[i].getW()!=card[i-1].getW()||card[i].getW()!=card[i-2].getW()+1||card[i].getW()>12)
				return 0;
		return len>>1;
	}
	
	private int isAeroplane()
	{
		if(len<6||card[1].getW()!=card[2].getW()||card[1].getW()!=card[3].getW()||card[2].getW()!=card[3].getW()||len%3!=0)
			return 0;
		for(int i=6;i<=len;i+=3)
			if(card[i].getW()!=card[i-1].getW()||card[i].getW()!=card[i-2].getW()||card[i-1].getW()!=card[i-2].getW()||card[i].getW()!=card[i-3].getW()+1)
				return 0;
		return len/3;
	}
	
	private int isAeroplaneAndOne(int op)
	{
		//巨麻烦
		if(len<8||len%4!=0)
			return 0;
		int res=0;
		for(int i=13-len/4;i>=1;i--)
		{
			int p=1;
			for(int j=i;j<=i-1+len/4;j++)
				if(cardAbstract[j]<3)
				{
					p=0;
					break;
				}
				
			if(p==1)
			{
				res=i;
				break;
			}
		}
			
		if(op==0&&res!=0)
			return len/4;
		else
			return res;//这里有两种返回值，如果这个函数被value函数调用就会返回这个飞机的起点，否则还是返回长度
	}
	
	//根据我知道的斗地主规则，飞机中带的牌类型必须相同，必须都带单张或者都带对子，不能多带或者少带。
	//代码就按这个规则来写了，没有考虑别的情况。如果允许少带其实还好写一些。
	//考虑到飞机可能出现的奇葩组合（比如333444555666这种长度算3还是4）
	//太麻烦了
	//还要考虑前一手牌的所有情况与这一手牌的所有情况是否存在一对符合的，还要考虑前一手为0...干脆不处理了，打了那么久斗地主都没见过几次长度3的飞机，哪有那么多离谱的牌
	//全都按这个来：先保证飞机长度尽可能长，再保证飞机起点尽可能大（第二点感觉好像没用）
	//其实就是按顺序把验证不带牌，带单，带对的函数跑一遍就满足条件了，这样简单多了
	private int isAeroplaneAndDouble(int op)
	{
		//跟上面差不多
		if(len<10||len%5!=0)
			return 0;
		int[] cardByNum=new int[21];
		int res=0;
		for(int i=13-len/5;i>=1;i--)
		{
			int p=1;
			for(int j=1;j<=15;j++)
				cardByNum[j]=cardAbstract[j];
			for(int j=i;j<=i-1+len/4;j++)
				if(cardByNum[j]!=3)
				{
					p=0;
					break;
				}
				
				else
					cardByNum[j]-=3;
			for(int j=1;j<=15;j++)
				if(cardByNum[j]%2!=0)
				{
					p=0;
					break;
				}
				
			if(p==1)
			{
				res=i;
				break;
			}
		}
		
		if(op==0&&res!=0)
			return len/5;
		else
			return res;
	}
	
	public int value()
	{
		/* 这里返回该组牌的权值，规定：
		 * 单牌，对子，三张，三带一，三带一对，四带二，四带两对：牌面数值
		 * 各种顺子：顺子起点的牌面数值，如333444+5566返回1，56789返回3
		 * 炸弹：牌面数值*100，如5555返回500，KKKK返回1100，2222返回1300
		 * 王炸：10000
		 */
		int res=0;
		if(len==1||len==2&&card[1].getW()==card[2].getW()||len==3&&card[1].getW()==card[2].getW()&&card[2].getW()==card[3].getW())
			return card[1].getW();//单对三
		else if(len==4&&card[1].getW()==card[2].getW()&&card[1].getW()==card[3].getW()&&card[1].getW()==card[4].getW())
			return card[1].getW()*100;//炸
		else if(len==2&&card[1].getW()==14&&card[2].getW()==15)
			return 10000;//王炸
		else if(len==4&&card[1].getW()==card[2].getW()&&card[1].getW()==card[3].getW())
			return card[1].getW();//三带一，三在前
		else if(len==4&&card[2].getW()==card[3].getW()&&card[2].getW()==card[4].getW())
			return card[2].getW();//三带一，三在后
		else if(len==5&&card[1].getW()==card[2].getW()&&card[1].getW()==card[3].getW()&&card[4].getW()==card[5].getW())
			return card[1].getW();//三带对，三在前
		else if(len==5&&card[1].getW()==card[2].getW()&&card[3].getW()==card[4].getW()&&card[3].getW()==card[5].getW())
			return card[3].getW();//三带对，三在后
		else if(len==6&&card[1].getW()==card[2].getW()&&card[1].getW()==card[3].getW()&&card[1].getW()==card[4].getW())
			return card[1].getW();//四带二，四在前
		else if(len==6&&card[2].getW()==card[3].getW()&&card[2].getW()==card[4].getW()&&card[2].getW()==card[5].getW())
			return card[2].getW();//四带二，四在中
		else if(len==6&&card[3].getW()==card[4].getW()&&card[3].getW()==card[5].getW()&&card[3].getW()==card[6].getW())
			return card[3].getW();//四带二，四在后
		else if(len==8&&card[1].getW()==card[2].getW()&&card[1].getW()==card[3].getW()&&card[1].getW()==card[4].getW()&&card[5].getW()==card[6].getW()&&card[7].getW()==card[8].getW())
			return card[1].getW();
		else if(len==8&&card[1].getW()==card[2].getW()&&card[3].getW()==card[4].getW()&&card[3].getW()==card[5].getW()&&card[3].getW()==card[6].getW()&&card[7].getW()==card[8].getW())
			return card[3].getW();
		else if(len==8&&card[1].getW()==card[2].getW()&&card[3].getW()==card[4].getW()&&card[5].getW()==card[6].getW()&&card[5].getW()==card[7].getW()&&card[5].getW()==card[8].getW())
			return card[5].getW();//以上四带两对，下面是各种各样的顺子
		else if(this.isSingleStraight()!=0||this.isDoubleStraight()!=0||this.isAeroplane()!=0)
			return card[1].getW();
		else if(((res=this.isAeroplaneAndOne(1))!=0)||((res=this.isAeroplaneAndDouble(1))!=0))
			return res;
		return 1;
	}
	
	//返回牌组长度
	public int getLen()
	{
		return len;
	}
	
	//判断点数为i的牌的数量
	public int getNum(int i)
	{
		return cardAbstract[i];
	}
	
	//判断两组牌的包含关系
	public boolean exist(CardSequence cardSequence)
	{
		for(int i=1;i<=15;i++)
			if(cardSequence.getNum(i)>cardAbstract[i])
				return false;
		return true;
	}
	
	public Card findCard(int i)
	{
		return card[i];
	}
	
	public Card findCardRand(int i)
	{
		return cardRand[i];
	}
	
	public void addCard(Card oneCard)
	{
		card[++len].setW(oneCard.getW());
		card[len].setC(oneCard.getC());
		for(int i=1;i<=len;i++)
			for(int j=i+1;j<=len;j++)
				if(card[i].getW()>card[j].getW()||card[i].getW()==card[j].getW()&&card[i].getC()>card[j].getC())
				{
					Card p0=card[i];
					card[i]=card[j];
					card[j]=p0;
				}
		
		abstractSequence();
	}
	
	public boolean kill(CardSequence anotherCard)
	{
		int t1=this.type(),t2=anotherCard.type();
		int v1=this.value(),v2=anotherCard.type();
		if((this.type()==anotherCard.type()&&this.type()!=0||this.type()==100000)&&this.value()>anotherCard.value()||anotherCard.type()==0&&this.type()!=0)
			return true;
		return false;
	}
	
	public void removeCard(Card oneCard)
	{
		for(int i=1;i<=len;i++)
			if(card[i].getW()==oneCard.getW()&&card[i].getC()==oneCard.getC())
			{
				card[i].setW(100);
				break;
			}
			
		for(int i=1;i<=len;i++)
			for(int j=i+1;j<=len;j++)
				if(card[i].getW()>card[j].getW()||card[i].getW()==card[j].getW()&&card[i].getC()>card[j].getC())
				{
					Card p0=card[i];
					card[i]=card[j];
					card[j]=p0;
				}
				
		len--;
	}
	
	public void printCard()
	{
		System.out.println("---");
		for(int i=1;i<=len;i++)
			System.out.println(card[i].getW()+" "+card[i].getC());
		System.out.println("---");
	}
}















package main;

//本程序是eclipse开发的
//本程序的作用是用于测试JVM虚拟机的GC回收机制
//JDK1.7以后JVM的GC用于判断对象已经死亡的算法称为 可达性分析算法
//这个算法的基本思路就是通过一系列的被称为"GC ROOTS"的对象作为起始点，从这些节点开始向下搜索，搜索所走的路径称之为引用链
//当一个对象到GC ROOTS没有任何引用链相连那么就说明对象是不可用的，在下一次垃圾回收时，系统就会将该对象放入一个被宣判死刑的
//对象列表，GC算法有专门的线程来轮训这个列表，每次轮训的时候这个列表里的每一个对象的finalize方法就会被调用，列表里的对象
//就可以在finalize进行自救（即给自己赋予一个新的引用，即让自己与GC ROOTS重新连上），当GC算法第二次来轮训这个列表的时候发现，
//唉这个对象的又有 引用了，于是就赦免这个对象，而其他该列表里的对象没有再自己的finalize方法里自救，那么他们就会被GC干掉
//一个方法的finalize方法在GC时会且只会执行一次。所以对象是没办法进行二次自救的
//但是必须注意的是，在日常撸码的时候要坚决杜绝使用finalize，因为它有很多不确定性，其实要达到它的效果可以用trycatchfinally
//在fainlly去拯救自己吧。
//这里可以做一下调试设置，在Run->Run Configurations -> Java Application
//新增一个在Arguments的VM arguments里面设置如下内容
//-verbose:gc   用于打印GC日志到控制台
//
public class FinalizeEscapeGC {
	public static FinalizeEscapeGC SAVE_HOOK = null;
	public void isAlive() {
		System.out.println("yes, i am still alive...");
	}
	@Override
	protected void finalize() throws Throwable{
		super.finalize();
		System.out.println("finalize method executed...");
		//在对象的finalize方法里自救
		FinalizeEscapeGC.SAVE_HOOK = this;
	}
	
	public static void main(String[] args) throws Throwable {
		SAVE_HOOK = new FinalizeEscapeGC();
		//对象第一次成功拯救自己
		SAVE_HOOK = null;
		System.gc();
		//因为finalize方法的优先级很低，所以暂停程序0.5秒等到它被执行
		Thread.sleep(500);
		if(SAVE_HOOK != null) {
			SAVE_HOOK.isAlive();
		}else {
			System.out.println("no, i am already dead...");
		}
		//下面这段代码和上面的一样，但是这次却自救失败了
		SAVE_HOOK = null;
		System.gc();
		Thread.sleep(500);
		if(SAVE_HOOK != null) {
			SAVE_HOOK.isAlive();
		}else {
			System.out.println("no, i am already dead ...");
		}
	}
}

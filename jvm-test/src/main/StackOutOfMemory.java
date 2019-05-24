package main;


//本程序未eclipse项目
//本程序用于测试在JAVA虚拟机栈是确定的情况下，创建了太多线程的情况下导致的内存溢出抛出OutOfMemoryError异常
//注意OutOfMemoryError并不一定就是堆内存不足导致的异常，但是StackOverFlow一定是栈内存不足导致的异常
//测试堆内存溢出
//这里可以做一下调试设置，在Run->Run Configurations -> Java Application
//新增一个在Arguments的VM arguments里面设置如下内容来设置本应用程序的内存大小，
//-verbose:gc -Xms20M -Xmx20M -Xmn10M -Xss2M -XX:+PrintGCDetails
//注意最好不要执行这个程序，执行这个程序会导致电脑假死
//因为JVM是可以访问全部计算机内存的，虽然指定了-Xmx，但是这只是固定了堆内存的大小，而栈内存是可以访问整个计算机物理内存的
//当栈内存访问过多，就意味着要占用很多计算机物理内存，导致计算机假死
public class StackOutOfMemory {
	static class SOOMObject{
		private void dontStop() {
			while(true) {
				
			}
		}
		public void stackLeakByThread() {
			while(true) {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						dontStop();
					}
				});
				thread.start();
			}
		}
	}
	public static void main(String[] args) {
		SOOMObject soom = new SOOMObject();
		soom.stackLeakByThread();
	}
}

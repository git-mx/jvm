package main;

//本程序未eclipse项目
//本程序用于测试当栈一个方法的栈帧太大而导致JAVA虚拟机栈不够用的情况抛出StackOverFlow异常
//测试堆内存溢出
//这里可以做一下调试设置，在Run->Run Configurations -> Java Application
//新增一个在Arguments的VM arguments里面设置如下内容来设置本应用程序的内存大小，
//-verbose:gc -Xss128K -XX:+PrintGCDetails

public class StackOverFlow {
	static class SOFObject {
		private int stackLength = 1;
		public void stackLeak() {
			this.stackLength ++;
			stackLeak();
		}
	}
	
	public static void main(String[] args){
		SOFObject sof = new SOFObject();
		try {
			sof.stackLeak();
		}catch(Exception e) {
			System.out.println("stack length:" + sof.stackLength);
			throw e;
		}
	}
}

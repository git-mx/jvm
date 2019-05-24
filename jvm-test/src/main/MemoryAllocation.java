package main;
//本程序未eclipse项目
//本程序用于测试JVM的内存分配机制，
//这里可以做一下调试设置，在Run->Run Configurations -> Java Application
//新增一个在Arguments的VM arguments里面设置如下内容来设置本应用程序的内存大小，
//-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails
//PSYoungGen: total 9216K = eden(8192k) + from(1024K), to(1024K)不计算在新生代的total里面，因为to区是不可用的
//JVM调优

public class MemoryAllocation {
	public static void main(String[] args) {
		byte[] allocation1 = new byte[2097152];
		System.gc();
//		byte[] allocation2 = new byte[2097152];
//		System.gc();
//		byte[] allocation3 = new byte[2097152];
//		System.gc();
//		byte[] allocation4 = new byte[4194304];
//		System.gc();
	}
}

package main;
//������δeclipse��Ŀ
//���������ڲ���JVM���ڴ������ƣ�
//���������һ�µ������ã���Run->Run Configurations -> Java Application
//����һ����Arguments��VM arguments���������������������ñ�Ӧ�ó�����ڴ��С��
//-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails
//PSYoungGen: total 9216K = eden(8192k) + from(1024K), to(1024K)����������������total���棬��Ϊto���ǲ����õ�
//JVM����

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

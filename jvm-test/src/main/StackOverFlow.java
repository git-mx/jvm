package main;

//������δeclipse��Ŀ
//���������ڲ��Ե�ջһ��������ջ̫֡�������JAVA�����ջ�����õ�����׳�StackOverFlow�쳣
//���Զ��ڴ����
//���������һ�µ������ã���Run->Run Configurations -> Java Application
//����һ����Arguments��VM arguments���������������������ñ�Ӧ�ó�����ڴ��С��
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

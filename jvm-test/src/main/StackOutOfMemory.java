package main;


//������δeclipse��Ŀ
//���������ڲ�����JAVA�����ջ��ȷ��������£�������̫���̵߳�����µ��µ��ڴ�����׳�OutOfMemoryError�쳣
//ע��OutOfMemoryError����һ�����Ƕ��ڴ治�㵼�µ��쳣������StackOverFlowһ����ջ�ڴ治�㵼�µ��쳣
//���Զ��ڴ����
//���������һ�µ������ã���Run->Run Configurations -> Java Application
//����һ����Arguments��VM arguments���������������������ñ�Ӧ�ó�����ڴ��С��
//-verbose:gc -Xms20M -Xmx20M -Xmn10M -Xss2M -XX:+PrintGCDetails
//ע����ò�Ҫִ���������ִ���������ᵼ�µ��Լ���
//��ΪJVM�ǿ��Է���ȫ��������ڴ�ģ���Ȼָ����-Xmx��������ֻ�ǹ̶��˶��ڴ�Ĵ�С����ջ�ڴ��ǿ��Է�����������������ڴ��
//��ջ�ڴ���ʹ��࣬����ζ��Ҫռ�úܶ����������ڴ棬���¼��������
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

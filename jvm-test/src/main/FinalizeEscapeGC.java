package main;

//��������eclipse������
//����������������ڲ���JVM�������GC���ջ���
//JDK1.7�Ժ�JVM��GC�����ж϶����Ѿ��������㷨��Ϊ �ɴ��Է����㷨
//����㷨�Ļ���˼·����ͨ��һϵ�еı���Ϊ"GC ROOTS"�Ķ�����Ϊ��ʼ�㣬����Щ�ڵ㿪ʼ�����������������ߵ�·����֮Ϊ������
//��һ������GC ROOTSû���κ�������������ô��˵�������ǲ����õģ�����һ����������ʱ��ϵͳ�ͻὫ�ö������һ�����������̵�
//�����б�GC�㷨��ר�ŵ��߳�����ѵ����б�ÿ����ѵ��ʱ������б����ÿһ�������finalize�����ͻᱻ���ã��б���Ķ���
//�Ϳ�����finalize�����Ծȣ������Լ�����һ���µ����ã������Լ���GC ROOTS�������ϣ�����GC�㷨�ڶ�������ѵ����б��ʱ���֣�
//�������������� �����ˣ����Ǿ�����������󣬶��������б���Ķ���û�����Լ���finalize�������Ծȣ���ô���ǾͻᱻGC�ɵ�
//һ��������finalize������GCʱ����ֻ��ִ��һ�Ρ����Զ�����û�취���ж����Ծȵ�
//���Ǳ���ע����ǣ����ճ�ߣ���ʱ��Ҫ����ž�ʹ��finalize����Ϊ���кܶ಻ȷ���ԣ���ʵҪ�ﵽ����Ч��������trycatchfinally
//��fainllyȥ�����Լ��ɡ�
//���������һ�µ������ã���Run->Run Configurations -> Java Application
//����һ����Arguments��VM arguments����������������
//-verbose:gc   ���ڴ�ӡGC��־������̨
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
		//�ڶ����finalize�������Ծ�
		FinalizeEscapeGC.SAVE_HOOK = this;
	}
	
	public static void main(String[] args) throws Throwable {
		SAVE_HOOK = new FinalizeEscapeGC();
		//�����һ�γɹ������Լ�
		SAVE_HOOK = null;
		System.gc();
		//��Ϊfinalize���������ȼ��ܵͣ�������ͣ����0.5��ȵ�����ִ��
		Thread.sleep(500);
		if(SAVE_HOOK != null) {
			SAVE_HOOK.isAlive();
		}else {
			System.out.println("no, i am already dead...");
		}
		//������δ���������һ�����������ȴ�Ծ�ʧ����
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

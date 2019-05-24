package main;

import java.util.ArrayList;
import java.util.List;

//本程序为eclipse项目
//本程序用于测试当JVM堆内存太小，而new出来的对象导致堆（heap）内存不够用时抛出的OutOfMemoryError异常
//测试堆内存溢出
//这里可以做一下调试设置，在Run->Run Configurations -> Java Application
//新增一个在Arguments的VM arguments里面设置如下内容来设置本应用程序的内存大小，
//-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails
//-verbose的作用是在输出设备上显示虚拟机的运行信息
//-Xms表示JVM启动是分配的内存（最小内存），-Xmx表示JVM运行时可获得的最大堆内存，
//只要把 -Xms和-Xmx这两个值设置成一样大，就能避免JVM堆内存的动态扩展
//JAVA堆内存：新生代 + 老年代 （-Xmn表示新生代的大小，所以在-Xmx确定（最大内存确定）的情况下，新生代越大，代表着老年代越小）
//再细致一点JAVA堆内存分为eden区、survivor区、from区、to区等。
//这些区域的划分实际上都是针对GC算法来说的
//新生代对应的就是1个eden区+2个survivor区，老年代又叫做Tenured Gen
//HotSpot虚拟机GC算法采用分代收集算法
//一个对象刚被new出来之后是放在eden区的，
//分带收集算法的做法是，定期去轮训每一个区域，轮训的频次是从eden、survivor、Tenured Gen依次递减，
//GC每一次轮训eden的每一个对象都去检查这个对象在方法区有没有引用，如果有那么GC宣告对象安全，将其放入survivor区，
//否则GC说，你已经在方法区没有引用了，you die。这样以更低的频率去轮训survivor和老年区，去清理内存，从而实现内存回收机制
//总的来说JVM的总内存 = 新生代 + 老年代 + 永久代（永久代的大小总是固定的64M）
//在JAVA8以前是有永久代的说法的，从JAVA8开始就不再有永久代，而是成为元空间。
//更细一点说：新生代和老年代的垃圾回收算法是不同的，在现在的虚拟机中把新生代分成一个eden区一个两个survivor区（from和to），
//新生代采用的垃圾回收算法称之为复制算法，它的具体做法是：eden区和其中一个survivor区（from）存放的所有对象里绝大部分就是
//即生即死，即存活不了多久，GC总是以更高的频次来轮训这个eden去和其中一个survivor区（from），轮训完一遍之后，哦发现绝大部分对象
//都已经入GC ROOTS没有引用链连接了，好那我就把有的引用链的那一部分对象移动到另一块survivor区（to）去，下一次GC清理的时候
//就直接将eden和survivor（from）区清理干净，然后去轮训to区，把to区那些已经死亡的对象KILL掉，然后将剩下还活着的那些对象移动到
//老年代，因此老年代里面存着的都是那些元老。老年代采取的垃圾回收算法是标记清除算法，因为老年代只有一个，没有额外的空间拿来专门存放
//那些活着的对象，标记清除算法的做法是：第一次轮训的时候就将那些没有引用的对象打上标记，下一次GC的时候将这些被标记的对象KILL掉
//复制算法和标记清除算法互相比较：
//复制算法需要一块额外的空间来存储活着的对象，而且移动对象到额外空间也需要CPU开销，但是它的好处就是可以清理一整块大的连续的空间出来
//标记清除算法的优点就是不需要额外的内存空间，也不需要额外的CPU开销，但是缺点就是清理过后的内存是碎片化的，不连续性的，这就导致
//在有新的比较大的对象需要大的连续内存空间时，不得不提前触发下一次GC去回收内存以创造出适合大小的内存空间。
//以下是分带收集算法的实现：
//现在要实现分带收集算法，要确定两件事情，一件事确定哪些"节点"能够成为GC ROOTS，还有就是在轮训GC ROOTS的时候报保证GC ROOTS
//列表的一致性，一致性是指在GC轮训的时候要确保GC ROOTS在某一个时刻是确定不变的，这就要求在GC是JMV里所有的线程都处于暂停状态。
//首先来说明第一件是：就是如何去确定GC ROOTS
//GC ROOTS包含 虚拟机栈中引用的对象、方法区类静态属性引用的对象、方法区常量池引用的对象、本地方法栈JNI引用的对象
//在类加载完成的时候，HotSpot就把对象内什么偏移量上是什么类型的数据计算出来，在JIT编译过程中，也会在特定的位置记录下栈和寄存器中
//的哪些位置是引用.这样GC在扫描的时候就能直接得到这GC ROOTS了。
//GC又分为Minor GC 和 Full GC两种
//在大多数情况下：直接new的对象会在新生代的eden区开辟空间，来存储，在new对象的时候如果发现eden区的内存已经不够用就会触发Minor GC
//在GC的过程中，有些对象需要从新生到移动到老年代，在移动的时候发现老年代的内存不足了，会触发Full GC。在我们的JAVA代码里直接执行
//System.gc()实际上执行的是Full GC。
//这只是一般情况，但是还要考虑空间分配的担保问题，在发生Minor GC之前，JVM会先检查老年代的最大连续可用空间是否大于新生代所有的对象的总空间，
//如果大于则进行Minor GC。如果小于则看HandlePromotionFailure设置是否允许担保失败（不允许的话就直接Full GC）。如果允许，那会
//尝试计算老年代最大连续可用空间是否大于历次晋升到老年代对象的平均大小，如果大于则Minor GC，如果尝试计算失败或者小于进行Full GC。
//以上整个算法就成为分带收集算法
//而GC线程是怎么运行的呢，这就要扯到垃圾收集器了，所谓垃圾搜集器分带收集算法的实现
//目前主流的垃圾收集器是CMS收集器（Concurrent Mark Sweep）字面意思是：并行的-标记-清理
//它包含如下四个步骤：初始标记、并发标记、重新标记、并发清除
//其中初始标记个重新标记都是轮训GC ROOTS，这两个步骤是需要Stop The World（暂停所有用户线程）的
//GC日志
//GC日志开头的GC和Full GC 说明了这次垃圾收集的停顿类型，如果是Full GC则说明本次GC是发生了Stop The World
//各个JVM打印出来的GC日志是不相同的，这主要是由于各个JVM采用的垃圾收集器不同
//如果打印的是DefNew则说明当前的JVM采用的是Serial垃圾收集器
//如果打印的是ParNew则说明当前的JVM采用的是ParNew垃圾收集器
//如果打印的是PSYoungGen则说明当前的JVM采用的是Parallel Scavenge垃圾收集器
//例如当前本程序打印的日志中的某一行如下
//[GC (Allocation Failure) [PSYoungGen: 8192K->1000K(9216K)] 8192K->3726K(19456K), 0.0032263 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
//1.GC表示本次GC并没有发生Stop The World 
//2.PSYoungGen代表的是当前的JVM采用的是Parallel Scavenge垃圾收集器，并且PSYoungGen指的是新生代
//3.[PSYoungGen: 8192K->1000K(9216K)] 表示新生代的内存可用量在本次GC之前是8192K，在本次GC之后是1000K，（9216K）是指新生代的总容量
//4.方括号外的8192K->3726K(19456K)表示JAVA堆内存在本次GC之前的可用容量是8192K，在本次GC之后是3726K，（19456K）表示JAVA堆内存的总容量
//0.0032263 secs表示本次GC针对该内存区域（新生代）的执行时间
// [Times: user=0.00 sys=0.00, real=0.00 secs]这里面的user、sys、real与Linux的time命令所输出的时间含义一致，
//分别代表用户态消耗的CPU时间、内核态消耗的CPU时间、和从操作开始到结束所经过的墙钟时间。CPU时间与墙钟时间的区别是，墙钟时间包括各种
//非运算的等待耗时，例如等待磁盘I/O，等待线程阻塞，而CPU时间不包括这些耗时，单当系统有多个CPU，多线程操作会叠加这些CPU时间，所以在
//多核CPU系统中，看到的user和sys时间大于墙钟时间是正常的。




public class HeapOutOfMemory {
	static class OOMObject {
		private Long id;
		private String name;
		OOMObject(){}
		OOMObject(Long id, String name){
			this.id = id;
			this.name = name;
		}
	}
	
	public static void main(String[] args) {
		List<OOMObject> list = new ArrayList<OOMObject>();
		while(true) {
			list.add(new OOMObject(1L, "张三"));
		}
	}
}

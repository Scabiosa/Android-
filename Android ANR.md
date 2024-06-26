## Android ANR



ANR：应用无响应，是Application Not Responsing的简称

造成ANR的原因，都是因为在主线程执行任务太久阻塞了界面更新导致的，主要分为以下几类

```
Broadcast Timeout: 前台广播执行超过10s, 后台广播执行超过60s (注意：只有串行广播才有超时机制，并行广播并不会超时，也就是说，如果广播是动态注册的，直接调用sendBroadcast触发，如果主线程Looper中排在后面的Message不会触发超时机制，那么即使这个广播是前台广播，系统也永远不会弹出框提示用户超时了)
Service Timeout: 前台服务执行超过20s, 后台服务执行超过200s
Provider Timeout: 内容提供者，publish超过10s
Input Timeout: 按键触摸事件分发超过5s
```

常见原因：

```
执行耗时任务过久，如文件读取或存储，网络访问获取文件太耗时
线程被阻塞过久，或者出现了死锁
线程饥饿，如Binder线程总共16个，Binder主线程占有一个，剩余的15个工作线程都被占满
CPU饥饿，负载值过大，虽然代码正常但任务一直没有来得及执行
```



#### 一、获取准确的ANR时间

##### 1.日志关键字

- 日志中检索[**am_anr**]，提取ANR时间、进程号、包名及原因
- 如无[**am_anr**]日志再通过[**WindowManager:ANR in Window**]获取时间及包名

```
10-26 05:19:49.156  2399 14899 I am_anr  : [0,14369,com.miui.gallery,819576325,Input dispatching timed out (970f2ff com.miui.gallery/com.miui.gallery.activity.HomePageActivity (server) is not responding. Waited 5000ms for FocusEvent(hasFocus=true))]

10-26 05:19:48.925  2399  3167 I WindowManager: ANR in Window{970f2ff u0 com.miui.gallery/com.miui.gallery.activity.HomePageActivity}. Reason:970f2ff com.miui.gallery/com.miui.gallery.activity.HomePageActivity (server) is not responding. Waited 5000ms for FocusEvent(hasFocus=true)
```



#### 二、查看ANR trace文件

```xml
"main" prio=5 tid=1 Native
  | group="main" sCount=1 ucsCount=0 flags=1 obj=0x7315b308 self=0xb400007f02842c00
  | sysTid=26290 nice=0 cgrp=background sched=0/0 handle=0x7fafadf4f8
  | state=S schedstat=( 29572958499 51558931800 131607 ) utm=1779 stm=1177 core=0 HZ=100
  | stack=0x7fd88d5000-0x7fd88d7000 stackSize=8188KB
  | held mutexes=

"Signal Catcher" daemon prio=10 tid=3 Runnable
  | group="system" sCount=0 ucsCount=0 flags=0 obj=0x12c02d80 self=0xb400007f02844800
  | sysTid=26296 nice=-20 cgrp=background sched=0/0 handle=0x7ecb60dcb0
  | state=R schedstat=( 480095772 47544465 297 ) utm=29 stm=18 core=2 HZ=100
  | stack=0x7ecb516000-0x7ecb518000 stackSize=991KB
  | held mutexes= "mutator lock"(shared held)
  
  "binder:26290_3" prio=10 (not attached)
  | sysTid=26762 nice=-20 cgrp=background
  | state=S schedstat=( 1967074 20649693 42 ) utm=0 stm=0 core=3 HZ=100
  
第0行
Signal Catcher：线程名称，main代表主线程
daemon：是否是守护线程(如果不是，则不打印daemon)
prio：线程优先级，一般默认是5
tid：线程内部id
Runnable/Native：线程状态，Native ：表示正在调用JNI，如果当前线程没有attach，则第一行会显示： "name" prio=num (not attached)

第1行
group：线程所属的线程组
sCount：线程挂起次数
obj：当前线程关联的java线程对象
self：该线程native的地址

第二行
sysTid：线程真正意义上的tid
nice：线程的调度优先级，nice值越小，则优先级越高。主线程一般nice=-10
cgrp：所属的进程调度组
sched：调度策略
handle：函数处理地址

第三行
state：线程状态
schedstat：括号中的三个数字依次是（Running，Runnable，Switch），分别代表CPU运行的时间，单位ns、RQ队列的等待时间，单位ns、CPU调度切换次数
utm：该线程在用户态所执行的时间，单位是jiffies，jiffies定义为sysconf(_SC_CLK_TCK)，默认等于10ms
stm：该线程在内核态所执行的时间，单位是jiffies，默认等于10ms
core：执行该线程CPU核的序号
HZ: 时钟频率

第四行
stack：线程栈的地址区间
stackSize：栈的大小

第五行
mutex: 所持有mutex类型，有独占锁exclusive和共享锁shared两类
```

##### 线程状态

```c++
// State stored in our C++ class Thread.
// When we refer to "a suspended state", or when function names mention "ToSuspended" or
// "FromSuspended", we mean any state other than kRunnable, i.e. any state in which the thread is
// guaranteed not to access the Java heap. The kSuspended state is merely one of these.
enum ThreadState {
  //                                   Java
  //                                   Thread.State   JDWP state
  kTerminated = 66,                 // TERMINATED     TS_ZOMBIE    Thread.run has returned, but Thread* still around
  kRunnable,                        // RUNNABLE       TS_RUNNING   runnable
  kTimedWaiting,                    // TIMED_WAITING  TS_WAIT      in Object.wait() with a timeout
  kSleeping,                        // TIMED_WAITING  TS_SLEEPING  in Thread.sleep()
  kBlocked,                         // BLOCKED        TS_MONITOR   blocked on a monitor
  kWaiting,                         // WAITING        TS_WAIT      in Object.wait()
  kWaitingForLockInflation,         // WAITING        TS_WAIT      blocked inflating a thin-lock
  kWaitingForTaskProcessor,         // WAITING        TS_WAIT      blocked waiting for taskProcessor
  kWaitingForGcToComplete,          // WAITING        TS_WAIT      blocked waiting for GC
  kWaitingForCheckPointsToRun,      // WAITING        TS_WAIT      GC waiting for checkpoints to run
  kWaitingPerformingGc,             // WAITING        TS_WAIT      performing GC
  kWaitingForDebuggerSend,          // WAITING        TS_WAIT      blocked waiting for events to be sent
  kWaitingForDebuggerToAttach,      // WAITING        TS_WAIT      blocked waiting for debugger to attach
  kWaitingInMainDebuggerLoop,       // WAITING        TS_WAIT      blocking/reading/processing debugger events
  kWaitingForDebuggerSuspension,    // WAITING        TS_WAIT      waiting for debugger suspend all
  kWaitingForJniOnLoad,             // WAITING        TS_WAIT      waiting for execution of dlopen and JNI on load code
  kWaitingForSignalCatcherOutput,   // WAITING        TS_WAIT      waiting for signal catcher IO to complete
  kWaitingInMainSignalCatcherLoop,  // WAITING        TS_WAIT      blocking/reading/processing signals
  kWaitingForDeoptimization,        // WAITING        TS_WAIT      waiting for deoptimization suspend all
  kWaitingForMethodTracingStart,    // WAITING        TS_WAIT      waiting for method tracing to start
  kWaitingForVisitObjects,          // WAITING        TS_WAIT      waiting for visiting objects
  kWaitingForGetObjectsAllocated,   // WAITING        TS_WAIT      waiting for getting the number of allocated objects
  kWaitingWeakGcRootRead,           // WAITING        TS_WAIT      waiting on the GC to read a weak root
  kWaitingForGcThreadFlip,          // WAITING        TS_WAIT      waiting on the GC thread flip (CC collector) to finish
  kNativeForAbort,                  // WAITING        TS_WAIT      checking other threads are not run on abort.
  kStarting,                        // NEW            TS_WAIT      native thread started, not yet ready to run managed code
  kNative,                          // RUNNABLE       TS_RUNNING   running in a JNI native method
  kSuspended,                       // RUNNABLE       TS_RUNNING   suspended by GC or debugger
};
```


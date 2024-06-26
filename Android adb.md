## Android adb



adb：Android Debug Bridge，简称adb，是一种用于在计算机和Android设备之间进行通信的工具。它是Android开发工具包（SDK）中的一部分，用于调试和测试Android应用程序。



#### 1、adb shell ps

```shell
adb shell ps

	  USER			PID		   PPID				  VSZ				RSS		WCHAN		ADDR	S								NAME                       
u0_a283		18989		1674	19714404		287240					0				  0    S		com.miui.gallery
```

ps 是 "process status" 的缩写，它可以显示正在运行的进程的详细信息，包括进程 ID (PID)、进程的用户 ID (UID)、进程的状态、CPU 使用率等



#### 2、adb shell kill

```
adb shell kill
```

用于终止正在运行的进程。它的作用是向指定的进程发送一个终止信号，使进程立即停止运行。

## android日志

#### 1、Android event日志详解

```
# See system/core/logcat/event.logtags for a description of the format of this file.

option java_package com.android.server.wm

# Do not change these names without updating the checkin_events setting in
# google3/googledata/wireless/android/provisioning/gservices.config !!
#
# An activity is being finished:
30001 wm_finish_activity (User|1|5),(Token|1|5),(Task ID|1|5),(Component Name|3),(Reason|3)
# A task is being brought to the front of the screen:
30002 wm_task_to_front (User|1|5),(Task|1|5)
# An existing activity is being given a new intent:
30003 wm_new_intent (User|1|5),(Token|1|5),(Task ID|1|5),(Component Name|3),(Action|3),(MIME Type|3),(URI|3),(Flags|1|5)
# A new task is being created:
30004 wm_create_task (User|1|5),(Task ID|1|5)
# A new activity is being created in an existing task:
30005 wm_create_activity (User|1|5),(Token|1|5),(Task ID|1|5),(Component Name|3),(Action|3),(MIME Type|3),(URI|3),(Flags|1|5)
# An activity has been resumed into the foreground but was not already running:
30006 wm_restart_activity (User|1|5),(Token|1|5),(Task ID|1|5),(Component Name|3)
# An activity has been resumed and is now in the foreground:
30007 wm_resume_activity (User|1|5),(Token|1|5),(Task ID|1|5),(Component Name|3)

# Activity launch time
30009 wm_activity_launch_time (User|1|5),(Token|1|5),(Component Name|3),(time|2|3)

# The Activity Manager failed to pause the given activity.
30012 wm_failed_to_pause (User|1|5),(Token|1|5),(Wanting to pause|3),(Currently pausing|3)
# Attempting to pause the current activity
30013 wm_pause_activity (User|1|5),(Token|1|5),(Component Name|3),(User Leaving|3),(Reason|3)
# Application process has been started

# An activity is being destroyed:
30018 wm_destroy_activity (User|1|5),(Token|1|5),(Task ID|1|5),(Component Name|3),(Reason|3)
# An activity has been relaunched, resumed, and is now in the foreground:
30019 wm_relaunch_resume_activity (User|1|5),(Token|1|5),(Task ID|1|5),(Component Name|3)
# An activity has been relaunched:
30020 wm_relaunch_activity (User|1|5),(Token|1|5),(Task ID|1|5),(Component Name|3)

# Activity set to resumed
30043 wm_set_resumed_activity (User|1|5),(Component Name|3),(Reason|3)

# Root task focus
30044 wm_focused_root_task (User|1|5),(Display Id|1|5),(Focused Root Task Id|1|5),(Last Focused Root Task Id|1|5),(Reason|3)

# Attempting to stop an activity
30048 wm_stop_activity (User|1|5),(Token|1|5),(Component Name|3)

# The task is being removed from its parent task
30061 wm_remove_task (Task ID|1|5), (Root Task ID|1|5)

# An activity been add into stopping list
30066 wm_add_to_stopping (User|1|5),(Token|1|5),(Component Name|3),(Reason|3)

# Keyguard status changed
30067 wm_set_keyguard_shown (Display Id|1|5),(keyguardShowing|1),(aodShowing|1),(keyguardGoingAway|1),(Reason|3)

# Out of memory for surfaces.
31000 wm_no_surface_memory (Window|3),(PID|1|5),(Operation|3)
# Task created.
31001 wm_task_created (TaskId|1|5),(RootTaskId|1|5)
# Task moved to top (1) or bottom (0).
31002 wm_task_moved (TaskId|1|5),(ToTop|1),(Index|1)
# Task removed with source explanation.
31003 wm_task_removed (TaskId|1|5),(Reason|3)
# bootanim finished:
31007 wm_boot_animation_done (time|2|3)

# Request surface flinger to show / hide the wallpaper surface.
33001 wm_wallpaper_surface (Display Id|1|5),(visible|1)
```

网址链接：

http://aospxref.com/android-13.0.0_r3/xref/frameworks/base/services/core/java/com/android/server/wm/EventLogTags.logtags

#### 2、应用启动完整 Log

```
// 创建 Task
1810  5473 I wm_task_created: [100,-1]
1810  5473 I wm_stack_created: 100
1810  5473 I wm_create_task: [0,100]

// 创建 Activity
1810  5473 I wm_create_activity: [0,231348670,100,com.androidperformance.memoryfix/.MainActivity,android.intent.action.MAIN,NULL,NULL,270532608]

// 桌面走 pause 流程
1810  5473 I wm_pause_activity: [0,93093883,com.xxx.launcher/.Launcher,userLeaving=true]
0615 20615 I wm_on_top_resumed_lost_called: [93093883,com.xxx.launcher.Launcher,topStateChangedWhenResumed]
0615 20615 I wm_on_paused_called: [93093883,com.xxx.launcher.Launcher,performPause]
1810  3720 I wm_add_to_stopping: [0,93093883,com.xxx.launcher/.Launcher,makeInvisible]

//启动 Activity
1810  2045 I am_proc_start: [0,18803,10263,com.androidperformance.memoryfix,pre-top-activity,{com.androidperformance.memoryfix/com.androidperformance.memoryfix.MainActivity}]
1810  3428 I am_proc_bound: [0,18803,com.androidperformance.memoryfix]
1810  3428 I wm_restart_activity: [0,231348670,100,com.androidperformance.memoryfix/.MainActivity]
1810  3428 I wm_set_resumed_activity: [0,com.androidperformance.memoryfix/.MainActivity,minimalResumeActivityLocked]
8803 18803 I wm_on_create_called: [231348670,com.androidperformance.memoryfix.MainActivity,performCreate]
8803 18803 I wm_on_start_called: [231348670,com.androidperformance.memoryfix.MainActivity,handleStartActivity]
8803 18803 I wm_on_resume_called: [231348670,com.androidperformance.memoryfix.MainActivity,RESUME_ACTIVITY]
8803 18803 I wm_on_top_resumed_gained_called: [231348670,com.androidperformance.memoryfix.MainActivity,topStateChangedWhenResumed]
1810  2034 I wm_activity_launch_time: [0,231348670,com.androidperformance.memoryfix/.MainActivity,471]

// 桌面走 stop 流程
1810  1978 I wm_stop_activity: [0,93093883,com.xxx.launcher/.Launcher]
0615 20615 I wm_on_stop_called: [93093883,com.xxx.launcher.Launcher,STOP_ACTIVITY_ITEM]

```

#### 3、Window Focus 相关的流程

````
// 从桌面启动 App，focus 变化 ：Launcher => null => App
WindowManager: Changing focus from Window{b0416d7 u0 com.xxx.launcher/com.xxx.launcher.Launcher} to null,diplayid=0
WindowManager: Changing focus from null to Window{10f5145 u0 com.android.settings/com.android.settings.Settings},diplayid=0

// 从 App 返回桌面，focus 变化 ：App => null => Launcher
WindowManager: Changing focus from Window{10f5145 u0 com.android.settings/com.android.settings.Settings} to null,diplayid=0
WindowManager: Changing focus from null to Window{b0416d7 u0 com.xxx.launcher/com.xxx.launcher.Launcher},diplayid=0

// 从 App 界面进入锁屏：focus 变化 ：App => null => 锁屏
WindowManager: Changing focus from Window{10f5145 u0 com.android.settings/com.android.settings.Settings} to null,diplayid=0
WindowManager: Changing focus from null to Window{82e5f30 u0 NotificationShade},diplayid=0

// 从锁屏界面解锁进入 App，focus 变化 ：锁屏 => App
WindowManager: Changing focus from Window{82e5f30 u0 NotificationShade} to Window{10f5145 u0 com.android.settings/com.android.settings.Settings},diplayid=0
````



#### 4、EventLog

https://juejin.cn/post/7231081485318537274#heading-17



#### 5、内存信息MEMINFO

```txt
** MEMINFO in pid 517 [com.miui.gallery] **
                   Pss      Pss   Shared  Private   Shared  Private  SwapPss      Rss     Heap     Heap     Heap
                 Total    Clean    Dirty    Dirty    Clean    Clean    Dirty    Total     Size    Alloc     Free
                ------   ------   ------   ------   ------   ------   ------   ------   ------   ------   ------
  Native Heap    66399        0     1276    65112      280     1260   154190    67928   245100   221714    23385
  Dalvik Heap   132662        0     1740   132588     4412        0     1533   138740   233590   135286    98304
 Dalvik Other    11074        0     6564     7236       44      772      228    14616                           
        Stack     2384        0       12     2296        0       88     3132     2396                           
       Ashmem       10        0      360        0       48        0        0      408                           
      Gfx dev      532        0        0      532        0        0        0      532                           
    Other dev       17        0      520        0        0       16        0      536                           
     .so mmap     2636     1192     1676      328    35340     1192     2676    38536                           
    .jar mmap      957        0        0        0    27928        0        0    27928                           
    .apk mmap     1762     1292        0        0     1900     1292        0     3192                           
    .ttf mmap     1321        0        0        0    11372        0        0    11372                           
    .dex mmap    13758    13544        4       24     1576    13544       32    15148                           
    .oat mmap      268        0       40        0    13228        0        0    13268                           
    .art mmap     3974       28    16408     3672     2612       28       89    22720                           
   Other mmap       72        0       24        8     1040       32        0     1104                           
      Unknown      934        0      324      660      216      272      913     1472                           
        TOTAL   401553    16056    28948   212456    99996    18496   162793   359896   478690   357000   121689

单位KB
```


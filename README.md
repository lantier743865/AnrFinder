# AnrFinder
要获得一个Git项目到你的构建：




步骤1.将JitPack存储库添加到构建文件中
将其添加到存储库末尾的根build.gradle中：

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
	
	
	
步骤2.添加依赖关系

  	dependencies {
	        compile 'com.github.lantier743865:AnrFinder:v1.0'
	}
	
	
	
	
	
步骤3.Application中初始化，并注册在Manifest.xml

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        // Do it on main process
        BlockCanary.install(this, new AppBlockCanaryContext()).start();
    }
}




步骤4.如果需要改阈值或其他可以继承BlockCanaryContext
Implement your application BlockCanaryContext context (strongly recommend you to check all these configs)：


public class AppBlockCanaryContext extends BlockCanaryContext {
    /**
     * Config monitor duration, after this time BlockCanary will stop, use
     * with {@code BlockCanary}'s isMonitorDurationEnd
     *
     * @return monitor last duration (in hour)
     */
    public int provideMonitorDuration() {
        return -1;
    }

    /**
     * Config block threshold (in millis), dispatch over this duration is regarded as a BLOCK. You may set it
     * from performance of device.
     *
     * @return threshold in mills
     */
    public int provideBlockThreshold() {
        return 1000;
    }

    /**
     * Block interceptor, developer may provide their own actions.
     */
    public void onBlock(Context context, BlockInfo blockInfo) {

    }
}

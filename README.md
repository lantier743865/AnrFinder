# AnrFinder
要获得一个Git项目到你的构建：
步骤1.将JitPack存储库添加到构建文件中
gradle这个
行家
SBT
leiningen
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

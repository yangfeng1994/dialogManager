# dialogManager
对DialogFragment与dialog进行队列的管理，一个关闭后，另一个自动弹出，监听生命周期，还可设置优先级

# 使用方法
第一步
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
第二步

```
dependencies {
	        implementation 'com.github.yangfeng1994:dialogManager:1.0.0'
	}
```
第三步
将您的DialogFragment 或者dialog 实现 DialogController 接口

## 使用时 ，在您的 Activity 或者Fragment中创建 DialogManager 单例

```
val dialogManager = DialogManager.getInstance()
dialogManager.addLifecycle(this) //实现生命周期监听
```

## 对activity或者fragment回调接口中
```
 //添加lifecycle 设置生命周期监听
 override fun getControllerLifecycle(): Lifecycle {
        return lifecycle
    }
    //添加类名，对不同fragment或者activity设置唯一性
    override fun getControllerClass(): Class<*> {
        return this.javaClass
    }
    //设置fragmentManager
    override fun getControllerFragmentManager(): FragmentManager {
        return supportFragmentManager
    }
    //移除监听
    override fun onDestroy() {
        super.onDestroy()
        dialogManager.removeLifecycle(this)
    }

```


addQueue 添加您的弹窗
```
dialogManager.addQueue(0,false, mFistDialogFragment, this)
```

下载demo更能更好的帮助您使用

## 感谢
- [DialogChainHelper](https://github.com/NByida/DialogChainHelper )
提供的反射思想，让项目调用更方便



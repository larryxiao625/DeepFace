# 人脸识别第一阶段说明文档  
## 一、项目目录结构及架构说明
  
涉及到的java代码都在app->java->com.iustu.identification包下：  


* api: 项目遗留问题由于涉及到的引用太多所有没有删掉，但是也没有使用  
* bean: 各种实体类  
	* ParameterConfig: 参数配置界面中保存各种配置的Java类   
	* PreviewSizeConfig： 
	* 其他：项目遗留问题，牵扯到的太多，所以没有删除，但是也没有使用  

* config：该包整个属于项目遗留问题，牵扯到的太多，所以没有删除，但是也没有使用  
* entity：该包下的几乎所有Java类都是数据库中数据表对应的Entity  
	* Account：代表登录账户  
	* BatchSuccess：不是数据库Table对应的Entity，在批量导入时用来记录当前操作是否成功，并记录该位置的图片的信息  
	* CompareRecord：比对记录的Table对应的Java类  
	* Library：人脸库的Table对应的Java类  
	* PersionInfo：人脸库中人员的Table对应的Java类  
	* TakeRecord：人脸库中抓拍信息对应的Java类  

* ui：各个界面所在的包  
	* base：界面中Fragment和Activity等的父类所在的包  
	* login：负责登录界面实现的包  
	* main：负责其他界面实现的包  
	* widget：界面中使用到的View所在的包  
	* SplashActivity：打开APP至登录界面中间显示图片过度的Activity  

* util：各种工具所在的包  
* App：自定义的Application，内部进行一些必要的初始化操作 

项目整体使用MVP+Retrofit+Rxjava模式四开发

#### 使用到的第三方库  

比较常用的、重要的只有RxJava和Glide：  

* RxJava：并发操作使用
* Glide：显示图片使用
* EventBus：跨线程调用时进行线程间通信

## 二、重要的工具类  
### 1. RxUtil    

里面各种方法**都是有关数据库的操作**，当*创建人脸库、修改人脸库信息、添加新成员等*动作在响应的界面产生时，都会调用RxUtil中响应的方法实现数据库的相应操作  

### 2. SqliteUtil  
  
由于比对记录和抓怕都是发生在主界面的Service中，比较特殊，所以比对记录和抓拍的插入操作都放到了SqliteUtil中；其中还有一些方法也是为数据库操作服务的  

### 3. SDKUtil  

该工具类是操作SDK的工具类，包含初始化句柄，使用句柄等一系列操作  

### 4. DataCache  

该工具类用来保存一些配置信息：当前登录的账户信息、管理员账户信息、SharePreference中的参数配置信息等等  

### 5. FileUtil  

该工具类用来实现一些文件相关的操作，包含创建文件夹、处理图片等操作  

## 三、自定义Application--App  

该类的作用是在启动App的时候进行一些必要的初始化操作：  

* 加载人脸识别sdk  
* CrashReport.initCrashReport(getApplicationContext())：添加bug上传  
* SqliteHelper.init(getApplicationContext())：首次打开App时创建数据库  
* Stetho.initializeWithDefaults(this)：
* keepAlive:添加保活  

同样也有一些App退出时候资源的释放或者关闭：  

* SDKUtil.destory():释放所有句柄  
* AlarmUtil.destory():释放比对成功时的警报资源  

## 三、参数配置界面  

参数配置界面的实现位于ui->main->config包下。  

ConfigFragment这个类是管理*参数配置、系统配置、人脸库选择*三个界面的Fragment，在其生命周期中会执行这三个界面中**配置变动的保存操作**  

### 1. ParameterConfigFragment  

该界面负责**参数配置界面**，主要的功能有：  

* initView：初始化控件，添加相应的点击事件  
* initData：获取保存的配置数据  
* 各种生命周期：保存配置  

### 2. SystemManagerFragment  

系统管理界面的实现，主要功能有：  

* initView：初始化控件  
* 点击修改密码：调用SqliteUtil.modifyAccountPassword(account.name, content)进行数据库中数据的修改  

### 3. LibManagerFragment  

人脸库选择界面的实现，**其主要功能在其mvp包的LibManageView中都有注释**  

## 四、人脸库管理界面  

该界面的相关实现位于ui->main->library下  
  
LibraryFragment：该类作用是管理*添加单个成员、人脸库管理、人脸库所有人员管理*三个Fragment，其主要功能除了初始化操作之外，如果当前登录的账户不是管理员账户的话会弹出对话框要求输入管理员密码。  

* 构造器：进行三个子Fragment的初始化操作、各自Presenter的初始化操作和绑定Presenter操作  
* initView：初始化控件，还会比对当前账户是否为管理员账户  
* onShow：比对当前账户是否为管理员账户  
* onTitleButtonClick：导航栏点击事件的管理，不同按钮会调用不同的方法  
* onBackPressed：导航栏按钮管理，不同界面显示不同的按钮、调用不同的点击事件  

### 1. LibrariesManageFragment  

该界面的作用是人脸库的管理，主要功能是获取人脸库信息、添加新的人脸库等。  
  
主要功能有：  

* initView：初始化控件  
	* mAdapter.setOnLibrariesItemButtonClickedListener：每一个人脸库后各种操作(*进入添加单个人员界面、人员管理界面、批量导入界面等，以及删除人脸库*)的点击事件的添加；其**最终实现是LibPresenter实现的，参数的详情请在LibPresenter的注释中查看**    
		* onImportMany：弹出批量导入对话框点击事件  
		* onNewMember：进入新增人员界面实现  
		* onManagePeople：切换到人员管理界面实现  
		* onDelete：删除人脸库的点击事件
			* deleteLib：删除人脸库的实现
		* onNewLib：添加新的人脸库的点击事件  
			* createNewLib：添加新的人脸库的实现
		
	* mAdapter.setOnPageItemClickListener：点击列表中每一项时，弹出修改人脸库信息对话框的点击事件
		* modifyLibName：修改人脸库名称的实现  

以下方法继承自位于mvp包下的LibView接口，其参数说明在LibView中有详情说明：  

* serPresenter：绑定Presenter  
* bindData：保存从数据库中获取的所有人脸库数据  
* showWaitDialog：从数据库获取人脸库时显示等待对话框  
* dismissDialog：从数据库获取人脸库时成功或者失败时调用，关闭等待对话框  
* onError：从数据库获取人脸库失败时调用，弹出Toast提示错误信息  
* onSuccess：从数据库获取人脸库成功时调用，根据不同的操作，进行不同的响应。  


### 2. AddPersonFragment  

添加单个人员信息的Fragment：  

* initView：初始化控件  
* onSubmit：点击事件，进行添加人员的提交；其最终实现是在AddPersionPresesnter的onAddPersion实现的，具体的参数信息请查看AddPersionPresenter的注释  

以下方法继承自位于mvp包下的AddPersionView接口，其参数说明在AddPersionView中有详情说明：  

* serPresenter：绑定Presenter    
* showWaitDialog：往人脸库中添加人员信息时显示等待对话框  
* dismissDialog：往人脸库中添加人员信息成功或者失败时调用，关闭等待对话框  
* onAddError：往人脸库中添加人员信息失败时调用，弹出Toast提示错误信息  
* onAddSuccess：往人脸库中添加人员信息时调用。  

### 3. PeopleManageFragment  

该Fragment是人员信息管理的界面实现：  

* initView:初始化控件  
* searchPersion：弹出搜索人员的对话框
* Event:EventBus的响应方法，搜索成功后跳转到对应的界面

以下方法继承自位于mvp包下的PersionView接口，其参数说明在PersionView中有详情说明：  

* setPresenter：绑定Presenter    
* bindData：保存从数据库中获取的所有人脸库数据 
* onInitData：初始加载数据调用
* onDeletePhoto：点击“删除照片”的时候调用
* onDeletePer：点击“删除”的时候调用
* onSaveChange：点击“保存”的时候调用
* onAddPhoto：点击"添加照片"的时候调用
* showWaitDialog：在获取人脸库所有人员信息的时候显示等待框
* dismissDialog：在在获取人脸库所有人员信息成功或者失败时  
* onFailed：在获取人脸库所有人员信息失败时
* onSuccess：当数据库操作成功时调用 

## 五、人脸实时对比抓拍界面

该界面相关实现位于ui->main->camera下
该界面主要作用实现摄像头实时抓拍及人脸对比结果的展示

为了应用保活，所有的逻辑均在service中实现，该界面主要与Service进行交互

### adapter

* CatchFaceAdapter:人脸捕捉展示RecyclerView关联adapter
* CompareItemAdapter:人脸比重展示RecyclerView展示关联adapter

### 1.CameraFragment

该Fragment是实时抓拍的界面实现

* initView:除初始化控件及adapter等
* onHiddenCahnged:当界面切换时进行相关操作

以下方法继承自IVew接口：

* showShortMsg:展示相关失败信息通用方法
* updateSingleResult:匹配到人脸信息后更新相关RecyclerView的方法
* updateResult:抓拍到人脸后更新相关RecyclerView的方法
* onPreviewResult:摄像头连接后获取到摄像头支持分辨率并更新到配置文件中

以下方法服务于Service和View进行交互

* onServiceConnected:当Service被绑定到当前View时进行调用
* onServiceDisconnected:当Service被解绑时调用

### 2.CameraPrenster
该prenster承担Camera开源库相关，以下方法继承自UVCCameraHelper（Github地址:[https://github.com/jiangdongguo/AndroidUSBCamera](https://github.com/jiangdongguo/AndroidUSBCamera)）

* onAttchDev:摄像头插入手机时调用
* onDettachDev:摄像头拔出连接时调用
* onConnectDev:摄像头成功连接时调用 **（当摄像头插入手机且权限正确时才会进行调用，注意与onAttchDev区别）**
* getSupportPreviewSize:获取支持的分辨率列表并更新到配置文件中
* onDisconnectDev:摄像头断开连接时调用 **(注意与onDettachDev区分，前者为物理上的拔插，此为切换界面时等资源释放时调用)**
* attchView:绑定View内部的类，实现与View交互

### 3.CapturePicService

该service为人脸抓拍及检测核心类

完整核心检测流程为 人脸抓拍(capturePic)->挑选五张人脸中最好的一张(Event)->人脸检测->根据算法返回人脸坐标裁剪人脸(getCutPicture)->提取人脸特征(getVerify)->人脸库中搜索人脸(searchFace)

* capturePic:通过RXjava实现一秒四张照片
	- 当设置为去重时此方法会将一秒内的照片同时通过EventBus推送到相关检测流程
	- 当设置为不去重时此方法会将每张捕捉到的照片都通过EventBus推送到相关检测流程

* Event:此方法绑定EventBus
	- 当设置为去重时此方法将会将四张照片的瞳距进行比较，只保留瞳距最大的一张进行人脸检测流程
	- 当设置为不去重时此方法会将post过来的所有照片进行人脸检测流程
	- 此方法为了降低人脸检测耗时，故使用Bitmap相关方法将突破压缩后进行检测，**但数据库保存原图记录以便读取时调用**

* getCutPicture：此方法当人脸检测返回人脸不为0时进行调用，根据算法返回坐标，扩大1.5倍裁剪出照片中的人脸，并通过SQliteUtil保存到数据库中以备历史查询

* getVerify:当检测到人脸数不为0时调用，提取照片中所有人脸的特征并根据数量存到List中

* searchFace:提取完人脸特征后调用，在人脸库中进行人脸搜索，若匹配到结果，则响起警报并同时将结果插入数据库中以备查询

## 六、登录界面

该界面只要实现登录功能

### 1、LoginActivity

* initView:初始化View界面
* start:静态公共方法，供外部启动LoginActivity

以下方法继承自view包中的IVew接口，主要实现prenster和主界面弹窗交互

* showLoginFail: 展示登录失败Dialog
* showWaitDialog: 展示登录等待Dialog
* dismissDialog：消除所有Dialog

### 2、LoginPrenster

* normalLogin:登录按钮点击时调用的方法
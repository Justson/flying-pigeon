# Flying-Pigeon
Flying-Pigeon 内部提供两种跨进程通信方式，来应对各种跨进程场景

## 引入

* Gradle


```gradle
implementation 'com.github.Justson:flying-pigeon:v1.0.5'
```

## 方式一

### Server

```java
private Api mApi = new Api() {
        @Override
        public int createPoster(Poster poster) {
            Log.e(TAG, "poster:" + GsonUtils.toJson(poster));
            return 11;
        }
    };
```

#### 对外发布服务
```java
ServiceManager.getInstance().publish(mApi);
```

### Client
``` java
final Pigeon pigeon = Pigeon.newBuilder(this).setAuthority(ServiceApiImpl.class).build();
Api api = pigeon.create(Api.class);
api.createPoster(new Poster("Justson", "just", 119, 11111000L, (short) 23, 1.15646F, 'h', (byte) 4, 123456.415D));
```

## 方式二

### Server

```java
@MainThread
@route("/query/username")
public void queryUsername(final Bundle in, final Bundle out) {
    ipcLabel.setText("received other app message,\n message:" + in.getString("userid"));
    out.putString("username", "ipc-sample");
}
```
#### 对外发布服务
```java
ServiceManager.getInstance().publish(this);
```


### Client
```java
Pigeon flyPigeon = Pigeon.newBuilder(MainActivity.this).setAuthority("com.flyingpigeon.ipc_sample").build();
Bundle bundle = flyPigeon.route("/query/username").withString("userid", UUID.randomUUID().toString()).fly();
```

## 混淆
```
-keep class com.flyingpigeon.library.*
-dontwarn com.flyingpigeon.library.*
```

## 建议
*  建议App内使用方式一，App与其他App通信使用方式二
*  返回的类型中，尽可能使用基本数据类型的包装类、如Integer,Double,Long,Short,Float,Byte,Boolean,Character
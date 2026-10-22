# Flying-Pigeon
Flying-Pigeon 是一个IPC跨进程通信组件，把跨进程通信简化到极致，并且突破binder 1MB内存大小限制。

## 引入

* Gradle


```gradle
implementation 'com.github.Justson:flying-pigeon:v1.0.7'
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

## Provider 连接复用与释放

同一个 `Pigeon` 实例的接口调用、路由调用和大响应查询会复用一个
`ContentProviderClient`，首次请求时才获取连接。请复用 `Pigeon` 实例，
不要在每次请求时重新创建。

```java
Pigeon pigeon = Pigeon.newBuilder(context)
        .setAuthority("com.flyingpigeon.sample.api")
        .build();
Api api = pigeon.create(Api.class);
// 在业务生命周期内复用 pigeon / api。

// 不再使用时（例如持有它的 Activity / Service 销毁时）：
pigeon.close();
```

`close()` 可重复调用；关闭后不要再使用该实例或它创建的代理。
关闭不会中断已经发起的调用，正在读取的 Cursor 关闭后才释放对应连接。
内部仅在获取、失效和释放连接时同步，不持有连接锁执行远程业务方法。

连接使用 unstable 引用，避免目标进程死亡时因 stable Provider 依赖连带终止调用方。
遇到 `DeadObjectException` 会清除失效连接，下一次请求重新获取；当前失败请求
不会自动重发，避免写操作被重复执行。原有调用失败时返回 null / 默认值的行为保持不变。

## 混淆
```
-keep class com.flyingpigeon.library.*
-dontwarn com.flyingpigeon.library.*
```

## 建议
*  建议App内使用方式一，App与其他App通信使用方式二
*  返回的类型中，尽可能使用基本数据类型的包装类、如Integer,Double,Long,Short,Float,Byte,Boolean,Character

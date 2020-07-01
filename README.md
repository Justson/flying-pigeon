# Flying-Pigeon
Flying-Pigeon 内部提供两种跨进程通信方式，来应对各种夸进程场景

## 引入

* Gradle


```gradle
implementation 'com.github.Justson:flying-pigeon:v1.0.0'
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
 Poster poster = new Poster("Justson", "just", 119, 11111000L, (short) 23, 1.15646F, 'h', (byte) 4, 123456.415D);
 api.createPoster(poster);
```

## 方式二

### Server

```java
@route("/query/username")
public void queryUsername(final Bundle in, final Bundle out) {
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            ipcLabel.setText("received other app message,\n message:" + in.getString("userid"));
        }
    });
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

## 建议
> 建议App内使用方式一，App与其他App通信使用方式二
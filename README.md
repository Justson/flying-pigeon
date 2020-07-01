# Flying-Pigeon
Flying-Pigeon 是一个IPC 跨进程通信组件，底层是匿名内存+Binder ， 突破1MB大小限制，无需写AIDL文件，让实现跨进程通信就像写一个接口一样简单

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

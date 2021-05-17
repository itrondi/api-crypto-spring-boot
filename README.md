# api-crypto-spring-boot

[![](https://img.shields.io/badge/release-1.0.0.RELEASE-red.svg)](http://hermesdi.cn)  [![](https://img.shields.io/badge/JDK-1.8+-8cba05.svg)](http://hermesdi.cn)  [![](https://img.shields.io/badge/author-hermes·di-ff69b4.svg)](http://hermesdi.cn)

## 介绍

`api-crypto-spring-boot`  是基于 `Spring Boot` 开发的控制器统一注解方式自动加解密 `请求体、响应体` 的启动器，该组件能够提供在 `接口`交互过程中数据的安全保护能力。支持常见的 加解密算法、编码、签名 等模式; 

## 特性

- Spring Boot 启动器组件方式，轻量级、只需要简单配置和注解即可使用；
- 支持 **摘要加密**、**签名验签**、**对称性加密解密**、**非对称性加密解密**、**内容编码** 等模式；
- 可以扩展 **自定义实现模式**、、**自定义注解**、**自定义格式等**；

## 支持实现

#### 摘要（DigestApiCrypto）: 

- MD2、MD4、MD5
- SHA1、SHA3、SHA224、SHA256、SHA384、SHA512
- SHAKE

#### 对称性（SymmetricApiCrypto）:

| 算法   | 模式                | 填充                      |
| ------ | ------------------- | ------------------------- |
| AES    | ECB/CBC/CTR/OFB/CFB | PKCS7Padding/PKCS5Padding |
| DES    | ECB/CBC/CTR/OFB/CFB | PKCS7Padding/PKCS5Padding |
| DESede | ECB/CBC/CTR/OFB/CFB | PKCS7Padding/PKCS5Padding |

#### 非对称性（AsymmetryApiCrypto）:

| 算法 | 模式 | 填充                                                         |
| ---- | ---- | ------------------------------------------------------------ |
| RSA  | ECB  | NoPadding/PKCS1Padding/OAEPWithSHA-1AndMGF1Padding/OAEPWithSHA-256AndMGF1Padding |
| RSA  | None | NoPadding/PKCS1Padding                                       |

#### 签名（SignatureApiCrypto）:

- 数据 + 随机字符串 + 时间戳 + 秘钥

#### 编码（EncodingApiCrypto）:

- base64、UrlBase64、hex

## 快速上手

#### 1.maven 引入 api-crypto-spring-boot-starter。

```xml
<dependency>
    <groupId>cn.hermesdi</groupId>
    <artifactId>api-crypto-spring-boot-starter</artifactId>
    <version>1.0.0.RELEASE</version>
</dependency>
```
#### 2.参数配置

​	需要使用什么模式配置什么参数，可同时使用多个模式；

- yml 方式：

```yml
api:
  crypto:
    #默认字符集
    charset: utf-8
    #默认base64编码
    encoding-type: base64

    #1. 使用签名注解 (@Signature) 时，这里全局配置 或 注解中配置
    signature:
      #签名秘钥
      secret-key: 13245678
      #配置验签超时时间，默认不超时。单位（秒）
      timeout: 0

    #2. 使用对称性加密解密 (@Symmetric) 注解时，这里全局配置（根据使用的算法选择配置） 或 注解中配置
    symmetric:
      #AES 密钥长度可以是 128(bit)、192(bit)、256(bit)
      AES: 1324567899999999

      #DESede(3DES) 密钥长度可以是 128(bit)、192(bit)
      DESede: 1324567899999999

      #DES 密钥长度是 64(bit)
      DES: 13245678


    #3. 使用非对称性加密解密 (@Asymmetry) 注解时，这里全局配置（根据使用的算法选择配置） 或 注解中配置
    asymmetry:
      RAS:
        #公钥
        public-key: xxxx
        #私钥
        private-key: xxxx
```

- properties 方式：

```properties
#默认字符集
api.crypto.charset=utf-8
#默认base64编码
api.crypto.encoding-type=base64

#1. 使用签名注解 (@Signature) 时，这里全局配置 或 注解中配置
#签名秘钥
api.crypto.signature.secret-key=13245678
#配置验签超时时间，默认不超时。单位（秒）
api.crypto.signature.timeout=0

#2. 使用对称性加密解密 (@Symmetric) 注解时，这里全局配置（根据使用的算法选择配置） 或 注解中配置
#AES 密钥长度可以是 128(bit)、192(bit)、256(bit)
api.crypto.symmetric.AES=xx245678999999xx
#DESede(3DES) 密钥长度可以是 128(bit)、192(bit)
api.crypto.symmetric.DESede=xx245678999999xx
#DES 密钥长度是 64(bit)
api.crypto.symmetric.DES=xx2456xx

#3. 使用非对称性加密解密 (@Asymmetry) 注解时，这里全局配置（根据使用的算法选择配置） 或 注解中配置
# 公钥
api.crypto.asymmetry.RSA.private-key=xxxx
# 私钥
api.crypto.asymmetry.RSA.public-key=xxxx
```

#### 3.启动类配置开启注解

```java
// 开启 ApiCrypto 注解
@EnableApiCrypto
@SpringBootApplication
public class ApiCryptoExampleApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ApiCryptoExampleApplication.class, args);
    }
}
```

#### 4.注册 实现模式(算法) Bean

​	为了减少不必要的消耗，用户可根据自身需求注册不同的模式到容器以使用。目前所有的已实现模式都实现于 `ApiCryptoAlgorithm` 接口，需要什么模式将该接口其对应的实现类 @Bean 注入即可。

```java
@Configuration
public class ApiCryptoConfiguration {
    /**
     * 摘要加密 Bean
     */
    @Bean
    public ApiCryptoAlgorithm digestApiCrypto() {
        return new DigestApiCrypto();
    }

    /**
     * 对称性加密解密 Bean
     */
    @Bean
    public ApiCryptoAlgorithm symmetricApiCrypto() {
        return new SymmetricApiCrypto();
    }

    /**
     * 非对称性加密解密 Bean
     */
    @Bean
    public ApiCryptoAlgorithm asymmetryApiCrypto() {
        return new AsymmetryApiCrypto();
    }

    /**
     * 签名 Bean
     */
    @Bean
    public ApiCryptoAlgorithm signatureApiCrypto() {
        return new SignatureApiCrypto();
    }

    /**
     * 内容 编码、解码 Bean
     */
    @Bean
    public ApiCryptoAlgorithm encodingApiCrypto() {
        return new EncodingApiCrypto();
    }
}
```

#### 5.实践使用

##### 模式注解使用：

- **注解放在类上**：该类下所有接口方法将开启实现。
- **注解放在方法上**：只有该方法开启实现。

| 模式（算法）          | 注解             | 作用范围               |
| --------------------- | ---------------- | ---------------------- |
| [摘要](#支持实现)     | @DigestsCrypto   | 响应体加密             |
| [对称性](#支持实现)   | @SymmetricCrypto | 响应体加密、请求体解密 |
| [非对称性](#支持实现) | @AsymmetryCrypto | 响应体加密、请求体解密 |
| [签名](#支持实现)     | @SignatureCrypto | 响应体签名、请求体验签 |
| [编码](#支持实现)     | @EncodingCrypto  | 响应体编码、请求体解码 |

**注意：**

​	当同时存在 **两个注解作用于一个方法或者类上和方法上都有注解**  的情况下，不同注解按照注册实现Bean的顺序，相同注解则按照方法优先于类的原则。



##### 其他注解：

​	使用于方法上，结合注解可以自由选择加密还是解密，还可以当整个类开启加密时忽略个别接口加密或解密。

- `@NotDecrypt` 忽略 **解密**，就是不对请求体进行处理
- `@NotEncrypt` 忽略 **加密**，就是不对响应体进行处理
- `@NotCrypto ` 忽略 **加密、解密**（上面两个注解合并）



例子：[完整dome](https://gitee.com/hermes-di/api-crypto-example)

```java
@RestController
public class SymmetricController {

    // 使用对称性加密、解密注解
    @SymmetricCrypto(type = SymmetricType.AES_CFB_PKCS7_PADDING)
    @PostMapping("/symmetric1")
    public Object symmetric() {
        
        return "对称性加密测试";
    
    }
    
    
    // 忽略解密（就是不对请求体进行处理）
    @NotDecrypt
    // 使用对称性加密、解密注解
    @SymmetricCrypto(type = SymmetricType.AES_CFB_PKCS7_PADDING)
    @PostMapping("/symmetric2")
    public Object symmetric() {
        
        return "对称性加密测试";
    
    }
    
     
    // 忽略加密（就是不对响应体进行处理）
    @NotEncrypt
    // 使用对称性加密、解密注解
    @SymmetricCrypto(type = SymmetricType.AES_CFB_PKCS7_PADDING)
    @PostMapping("/symmetric3")
    public Object symmetric() {
        
        return "对称性加密测试";
    
    }
}

```



## 高级特性

​	组件支持扩展如 自定义请求体解析、自定义请求体响应、自定义实现模式等；

### 自定义解析

​	可以自定义解析请求响应 body 的格式，以满足不同的场景需求。下面以注册对称性[算法Bean](#4.注册 实现模式(算法) Bean)为例：

```java
@Configuration
public class ApiCryptoConfiguration {
    
    /**
     * 对称性加密解密 Bean
     */
    @Bean
    public ApiCryptoAlgorithm symmetricApiCrypto() {
        SymmetricApiCrypto symmetricApiCrypto = new SymmetricApiCrypto();

        // 自定义请求内容解析，格式例如：数据&偏移量
        symmetricApiCrypto.setiApiRequestBody((annotation, inputStream) -> {
            try {
                // 转为字符串
                byte[] byteArr = new byte[inputStream.available()];
                inputStream.read(byteArr);
                String str = new String(byteArr);
                // 按照'&'符号分割
                String[] strings = str.split("&");
                // 封装数据
                return new ApiCryptoBody().setData(strings[0]).setIv(strings[1]);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });


        // 自定义响应内容格式，格式例如：数据&偏移量
        symmetricApiCrypto.setiApiResponseBody((annotation, cryptoBody) -> {

            String data = cryptoBody.getData();
            String iv = cryptoBody.getIv();

            return data + "&" + iv;

        });

        return symmetricApiCrypto;
    }
}
```

```java
@RestController
public class SymmetricController {
    @Resource
    ObjectMapper objectMapper;

    // 非对称加密、解密
    @SymmetricCrypto(type = SymmetricType.AES_CFB_PKCS7_PADDING)
    @PostMapping("/symmetric")
    public String symmetric(@RequestBody TestBean req) throws JsonProcessingException {
        System.out.println(req.toString());

        TestBean testBean = new TestBean();
        testBean.setAnInt(0);
        testBean.setInteger(1);
        testBean.setString("test string");
        testBean.setStringList(Collections.singletonList("list"));
        testBean.setObjectMap(Collections.singletonMap("test", "map"));

        return objectMapper.writeValueAsString(testBean);
    }
}
```

请求body为：数据&偏移量格式

```
1KFHb7m5f0VHCupyZmNBSmV0PpgDAmrMvmiM9bwylB1BfoXHptXbZVvtZhtuPWFdJATI1YLxxRl/102DhhO1tcKGpcnIxvpXQaXDsTz+HgU9lY2zEqfv2pvx0qieY8WN&L46gdVx6X934cpm6
```

响应body为：数据&偏移量格式

```
a9joYmcuIPEiIlGdTMhDEE+7LxPzo1DNMBlmDXGeBcG+I8qFRgdoQtuM7i+QHeNdYtz8CBUIMiC6f3nW10xW8Ok16GlBrO7ubt7cLIi/dkzhlD8Ita46ywyxJB2CeCxu&1Ed3g5Z9v6B3tpZ2
```



### 自定义实现模式

自定义实现一种模式，交互数据&md5(交互数据+盐)

例如：

要传输的数据是 ：abc123abc

盐(salt)是  : 123456

最后传输数据是 : abc123abc&3bdb146d4faea7d09c0d27ec9d36cbee

这样子就可以保证数据传输安全不被篡改了。

#### 自定义一个执行注解

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomCrypto {
    /**
     * 是否请求体解密
     */
    boolean isDecryption();

    /**
     * 是否响应体加密
     */
    boolean isEncryption();
    /**
     * 加密的盐
     */
    String salt();
}
```

#### 自定义一个实现算法

```java
public class CustomApiCrypto implements ApiCryptoAlgorithm {
    @Autowired
    private ObjectMapper objectMapper;

    private static final Log logger = LogFactory.getLog(CustomApiCrypto.class);

    /**
     * 处理自定义 加密/解密注解 或 其他方式
     *
     * @param methodParameter:   方法参数
     * @param requestOrResponse: 请求还是响应，true请求，false响应
     * @return boolean 返回 true 将执行下面两个对应的方法，false则忽略
     **/
    @Override
    public boolean isCanRealize(MethodParameter methodParameter, boolean requestOrResponse) {
        // 获取执行 方法或类上的指定注解（注解在类上并且是被继承的无效）
        CustomCrypto annotation = this.getAnnotation(methodParameter, CustomCrypto.class);
        // 是否请求解密
        if (annotation.isDecryption() && !requestOrResponse) {
            return true;
        }
        // 是否响应加密
        if (annotation.isEncryption() && !requestOrResponse) {
            return true;
        }
        return false;
    }

    /**
     * 当 isCanRealize 返回true，并且为请求时执行
     *
     * @param httpInputMessage: 请求内容
     * @param methodParameter:  执行方法参数
     * @param type:             目标类型
     * @param aClass:           消息转换器
     * @return org.springframework.http.HttpInputMessage 请求的数据输入流
     **/
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        // 请求内容
        byte[] byteArr = new byte[httpInputMessage.getBody().available()];
        httpInputMessage.getBody().read(byteArr);
        String str = new String(byteArr);
        // 拆分出数据和签名
        String[] strings = str.split("&");
        String data = strings[0];
        String md5 = strings[1];
        // 获取盐
        CustomCrypto annotation = this.getAnnotation(methodParameter, CustomCrypto.class);
        String salt = annotation.salt();
        // md5加密一下
        String test = DigestUtils.md5DigestAsHex((data + salt).getBytes(StandardCharsets.UTF_8));
        // 对比原来的md5加密是否一致，不一致说明被修改过请求体了
        if (!test.equals(md5)) {
            throw new ApiDecodeException("验证失败");
        }

        return this.stringToInputStream(data.getBytes(StandardCharsets.UTF_8), httpInputMessage.getHeaders(), logger);
    }

    /**
     * 当 isCanRealize 返回 true，并且为响应时执行
     *
     * @param body:               方法执行后返回的参数
     * @param methodParameter:    方法执行参数
     * @param mediaType:          数据类型，json，text，html等等
     * @param aClass:             消息转换器
     * @param serverHttpRequest:  请求
     * @param serverHttpResponse: 响应
     * @return java.lang.Object   最终响应内容
     **/
    @Override
    public Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        CustomCrypto annotation = this.getAnnotation(methodParameter, CustomCrypto.class);

        // 如果方法返回内容是字符串
        if (body instanceof String) {
            // md5加密一下
            String md5 = DigestUtils.md5DigestAsHex((body + annotation.salt()).getBytes(StandardCharsets.UTF_8));
            return body + "&" + md5;
        }

        // 如果方法返回内容是对象
        String json = null;
        try {
            json = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new ApiEncryptException("转json字符串失败");
        }

        // md5加密一下
        String md5 = DigestUtils.md5DigestAsHex((json + annotation.salt()).getBytes(StandardCharsets.UTF_8));
        return body + "&" + md5;
    }
}
```

#### 注册自定义实现模式

```java
@Configuration
public class ApiCryptoConfiguration {
    /**
     * 自定义模式 Bean
     */
    @Bean
    public ApiCryptoAlgorithm customApiCrypto() {
        return new CustomApiCrypto();
    }
}
```

#### 在Controller使用

```java
@RestController
public class CustomController {

    @CustomCrypto(isDecryption = true, isEncryption = true, salt = "123456")
    @PostMapping("/custom")
    public String custom(@RequestBody String text) {
        System.out.println(text); // abc123abc

        return "ABC123456";
    }
}
```

请求body为：数据&md5

```
abc123abc&3bdb146d4faea7d09c0d27ec9d36cbee
```

响应body为：数据&md5

```
ABC123456&f929eb0889e53b3893600ba29f039ced
```



## 常见问题

- 无法自动解密请求体加密内容
  - 加上 `@RequestBody` 即可；
- 多个算法注解同时使用时顺序问题
  - 当同时存在 **两个注解作用于一个方法或者类上和方法上都有注解**  的情况下，不同注解按照注册实现Bean的顺序，相同注解则按照方法优先于类的原则；



## 开源协议

[Apache 2.0](/LICENSE)
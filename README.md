简体中文 | [English](./README_EN.md)
# api-crypto-spring-boot

[![](https://img.shields.io/badge/release-1.0.0.RELEASE-red.svg)](http://hermesdi.cn)[![](https://img.shields.io/badge/JDK-1.8+-8cba05.svg)](http://hermesdi.cn)[![](https://img.shields.io/badge/author-hermes·di-ff69b4.svg)](http://hermesdi.cn)

## 介绍

`api-crypto-spring-boot`  是基于 `Spring Boot` 开发的控制器统一注解方式自动加解密 `请求体、响应体` 的启动器，该组件能够提供在 `接口`交互过程中数据的安全保护能力。支持常见的 加解密算法、编码、签名 等模式; 

## 特性

- Spring Boot 启动器组件方式，轻量级、只需要简单配置和注解即可使用；
- 支持 **摘要加密**、**签名验签**、**对称性加密解密**、**非对称性加密解密**、**内容编码** 等模式；
- 可以扩展 **自定义实现模式**、**自定义注解**；

## 使用方法

1. maven 引入 api-crypto-spring-boot-starter。
```xml
<dependency>
    <groupId>cn.hermesdi</groupId>
    <artifactId>api-crypto-spring-boot-starter</artifactId>
    <version>1.0.0.RELEASE</version>
</dependency>
```
2. 参数配置，需要使用什么模式配置什么参数。
```yml

```

3. 启动类配置开启注解  `@EnableApiCrypto` 。

```java
// 开启注解
@EnableApiCrypto
@SpringBootApplication
public class Application { 
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
或者使用`@RestController`对整个控制器的方法响应体都进行加密：
```java
@RestController
@EncryptBody
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String test(){
        return "hello world";
    }

}https://github.com/Licoy/encrypt-body-spring-boot-starter/wiki/解密注解一览表)
```
## 提供模式

- 摘要: 
  - MD2、MD4、MD5
  - SHA1、SHA3、SHA224、SHA256、SHA384、SHA512
  - SHAKE




- 对称性:

  | 算法   | 模式                | 填充                      |
  | ------ | ------------------- | ------------------------- |
  | AES    | ECB/CBC/CTR/OFB/CFB | PKCS7Padding/PKCS5Padding |
  | DES    | ECB/CBC/CTR/OFB/CFB | PKCS7Padding/PKCS5Padding |
  | DESede | ECB/CBC/CTR/OFB/CFB | PKCS7Padding/PKCS5Padding |




- 非对称性:

  | 算法 | 模式     | 填充                   |
  | ---- | -------- | ---------------------- |
  | RSA  | ECB/None | NoPadding/PKCS1Padding |




- 签名:

  - 数据 + 随机字符串 + 时间戳 + 秘钥




- 编码:

  - base64、UrlBase64、hex

## 开源协议

[Apache 2.0](/LICENSE)
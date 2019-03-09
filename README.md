### git地址
https://github.com/ChenBoEason/stress-tool.git

### 项目发展阶段




### 保证接口安全说明
所有加密算法使用 AES 加密  

1. header头中须有sign参数,sign = aesEncrypt({"deviceNum":"刷脸机设备号","signTime":"签名时间戳,毫秒级","token":"63df2bd5f4e94479ad46934c886b3fbd"})  
2. token 信息放入 header传输,进行验证
3. 接收与响应数据都使用 AES 加密  

关于 header 的检验在 SignAuthFilter 此过滤器中  



### 全局异常处理
获取全局异常处理,异常具体信息不做返回,只告诉 api 调用方 加密( {"code":"50001","msg":"服务端异常"}  )


### 接收数据处理
示例代码:  
```
@RequestMapping(value = "/getAccessToken")
public String getAccessToken(@RequestBody String params){
    /* 数据加密后的处理 */
    Map<String,Object> map = JSON.parseObject(params);
    
}
```

因为实现 RequestBodyAdvice 接口,只能获取 RequestBody 中的数据, post 表单提交的数据无法获取.提交的数据必须为 json 格式！



### token 信息存储
此信息用于获取商户授权时进行MySQL存储,表结构如下  
```
CREATE TABLE `app_auth_token`  (
     `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
     `app_auth_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商户授权token',
     `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '授权者的PID',
     `auth_app_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '授权商户的AppId（如果有服务窗，则为服务窗的AppId）',
     `gmt_create` datetime(0) NOT NULL COMMENT '第一次token创建时间',
     `gmt_modified` datetime(0) NOT NULL COMMENT '刷新token时间',
     PRIMARY KEY (`id`) USING BTREE
   ) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '此表数据为刷脸应用应用授权token信息表' ROW_FORMAT = Dynamic;
   
   ```

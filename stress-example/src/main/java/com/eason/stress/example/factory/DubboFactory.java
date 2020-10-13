package com.eason.stress.example.factory;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Eason(bo.chenb)
 * @description
 * @email chenboeason@gmail.com
 * @date 2020-10-06
 **/
@Slf4j
public class DubboFactory {

    private static Map<String, RegistryConfig> registryConfigMap = new HashMap<>();

    private static Map<String, ApplicationConfig> dubboApplicationMap = new HashMap<>();


    /**
     * 泛化调用dubbo服务
     * @param registryAddr    服务列表
     * @param namespace       命名空间
     * @param group           dubbo服务组别
     * @param version         dubbo服务版本号
     * @param apiClazz        dubbo接口全限定名称
     * @param apiMethod       dubbo接口方法
     * @param parameterType   dubbo服务方法参数类型, 当前实现只允许单参数类型
     * @param parameterArg    dubbo服务方法参数值, 默认为JSONObject
     * @return                对象为map, 空值和简单类型返回值不变
     */
    public static Object invoke(String registryAddr, String namespace, String group, String version, String apiClazz, String apiMethod,
                         String timeout, String parameterType, JSONObject parameterArg, Map<String, Object> attachments){


        return invoke(registryAddr, namespace, group, version, apiClazz, apiMethod, timeout, new String[]{parameterType}, new Object[]{parameterArg}, attachments);
    }


    /**
     * 泛化调用dubbo服务
     * @param registryAddr    服务列表
     * @param namespace       命名空间
     * @param group           dubbo服务组别
     * @param version         dubbo服务版本号
     * @param apiClazz        dubbo接口全限定名称
     * @param apiMethod       dubbo接口方法
     * @param parameterTypes  dubbo服务方法参数类型
     * @param params          dubbo服务方法参数值
     * @param attachments     dubbo RpcContext.getContext()参数
     * @return                对象为map, 空值和简单类型返回值不变
     */
    public static Object invoke(String registryAddr, String namespace, String group, String version,
                         String apiClazz, String apiMethod, String apiTimeout, String[] parameterTypes, Object[] params, Map<String, Object> attachments){

        ApplicationConfig application = buildApplicationConfig(registryAddr, namespace);

        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        // 弱类型接口名
        reference.setInterface(apiClazz);
        reference.setGroup(group);
        reference.setVersion(version);
        // 声明为泛化接口
        reference.setGeneric("true");
        reference.setApplication(application);
        //todo 后面改为可配置
        reference.setRetries(0);

        Integer timeout = 3000;

        if(apiTimeout != null){
            try {
                timeout = Integer.valueOf(apiTimeout);
            }catch (Exception e){

            }
        }

        reference.setTimeout(timeout);
        GenericService genericService = null;
        try {
            genericService = ReferenceConfigCache.getCache().get(reference);
        } catch (Exception e) {
            log.error("ReferenceConfigCache.getCache().get() is error,{}/{}.{}:{}", group, apiClazz, apiMethod, version,e);
        }

        if (genericService == null) {
            genericService = reference.get();
        }

        if(attachments != null) {
            attachments.forEach((k,v) -> {
                RpcContext.getContext().setAttachment(k, v);
            });
        }

        return genericService.$invoke(apiMethod, parameterTypes, params);
    }


    /**
     * 构建dubbo application config
     *
     * @param registryAddr 注册中心地址
     * @param namespace    命名空间
     * @return
     */
    private static synchronized ApplicationConfig buildApplicationConfig(String registryAddr, String namespace){
        String registryKey = registryAddr + namespace;
        RegistryConfig registry = registryConfigMap.get(registryKey);

        ApplicationConfig dubboApplication = dubboApplicationMap.get(registryKey);

        if(registry == null) {
            registry = new RegistryConfig();
            registry.setAddress(registryAddr);
            if(StringUtils.isNotEmpty(namespace)) {
                Map<String, String> registryParameters = new HashMap<>();
                registryParameters.put("namespace", namespace);
                registry.setParameters(registryParameters);
            }
            registryConfigMap.put(registryKey, registry);

            return buildDubboApplication(dubboApplication, registry, registryKey);
        }

        return buildDubboApplication(dubboApplication, registry, registryKey);
    }

    private static ApplicationConfig buildDubboApplication(ApplicationConfig dubboApplication, RegistryConfig registry, String registryKey) {

        if(dubboApplication != null){
            return dubboApplication;
        }

        Optional<ApplicationConfig> applicationConfigOptional = ApplicationModel.getConfigManager().getApplication();
        dubboApplication = new ApplicationConfig();
        dubboApplication.setName("gateway");
        dubboApplication.setRegistry(registry);

        dubboApplicationMap.put(registryKey, dubboApplication);

        return dubboApplication;
    }
}

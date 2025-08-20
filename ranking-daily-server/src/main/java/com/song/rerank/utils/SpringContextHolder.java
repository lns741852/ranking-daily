package com.song.rerank.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

    private static ApplicationContext applicationContext = null;

    /**
     * 從靜態變量applicationContext中取得Bean, 自動轉型為所賦值對象的類型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 從靜態變量applicationContext中取得Bean, 自動轉型為所賦值對象的類型.
     */
    public static <T> T getBean(Class<T> requiredType) {
        assertContextInjected();
        return applicationContext.getBean(requiredType);
    }

    /**
     * 獲取SpringBoot 配置信息
     *
     * @param property     屬性key
     * @param defaultValue 默認值
     * @param requiredType 返回類型
     * @return /
     */
    public static <T> T getProperties(String property, T defaultValue, Class<T> requiredType) {
        T result = defaultValue;
        try {
            result = getBean(Environment.class).getProperty(property, requiredType);
        } catch (Exception ignored) {}
        return result;
    }

    /**
     * 獲取SpringBoot 配置信息
     *
     * @param property 屬性key
     * @return /
     */
    public static String getProperties(String property) {
        return getProperties(property, null, String.class);
    }

    /**
     * 獲取SpringBoot 配置訊息
     *
     * @param property     屬性key
     * @param requiredType 返回類型
     * @return /
     */
    public static <T> T getProperties(String property, Class<T> requiredType) {
        return getProperties(property, null, requiredType);
    }

    /**
     * 檢查ApplicationContext不為空.
     */
    private static void assertContextInjected() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicaitonContext屬性未註入, 請在applicationContext" +
                    ".xml中定義SpringContextHolder或在SpringBoot啟動類中註冊SpringContextHolder.");
        }
    }

    /**
     * 清除SpringContextHolder中的ApplicationContext為Null.
     */
    private static void clearHolder() {
        log.debug("清除SpringContextHolder中的ApplicationContext:"
                + applicationContext);
        applicationContext = null;
    }

    @Override
    public void destroy() {
        SpringContextHolder.clearHolder();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if (SpringContextHolder.applicationContext != null) {
            log.warn("SpringContextHolder 中的 ApplicationContext 被覆蓋，原有 ApplicationContext 為: " + SpringContextHolder.applicationContext);
        }
        SpringContextHolder.applicationContext = context;
    }
    /**
     * 獲取 @Service 的所有 bean 名稱
     * @return /
     */
    public static List<String> getAllServiceBeanName() {
        return new ArrayList<>(Arrays.asList(applicationContext
                .getBeanNamesForAnnotation(Service.class)));
    }
}
/*
 * *
 *  * @author guomw
 *  *
 *
 */

package com.huotu.loanmarket.webapi.config;

import com.huotu.loanmarket.common.utils.ViewResolverUtils;
import com.huotu.loanmarket.webapi.interceptor.AppInterceptor;
import com.huotu.loanmarket.webapi.interceptor.AppLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import java.util.Arrays;
import java.util.List;

/**
 * @author guomw
 * @date 29/01/2018
 */
@Configuration
@EnableWebMvc
@ComponentScan({
        "com.huotu.loanmarket.webapi.controller",
        "com.huotu.loanmarket.service.service",
        "com.huotu.loanmarket.webapi.service"
})
public class MvcConfig extends WebMvcConfigurerAdapter {

    /**
     * 静态资源处理,加在这里
     */
    private static String[] STATIC_RESOURCE_PATH = {
            "resource",
            "script"
    };

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private Environment environment;


    /**
     * 禁止拦截静态资源
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        for (String resourcePath : MvcConfig.STATIC_RESOURCE_PATH) {
            registry.addResourceHandler("/" + resourcePath + "/**").addResourceLocations("/" + resourcePath + "/");
        }
    }

    /**
     * for upload
     */
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));

        converters.add(converter);
    }

    /**
     * 默认拦截器
     *
     * @return
     */
    @Bean
    public AppInterceptor appInterceptor() {
        return new AppInterceptor();
    }

    /**
     * 登录拦截器
     *
     * @return
     */
    @Bean
    public AppLoginInterceptor appLoginInterceptor() {
        return new AppLoginInterceptor();
    }


    /**
     * 判断拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录拦截
        this.addLoginAuthInterceptor(registry);

        //签名授权拦截
        this.addSignatureInterceptor(registry);

    }

    /**
     * 登录拦截器
     *
     * @param registry
     */
    private void addLoginAuthInterceptor(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(appLoginInterceptor());

        /**
         * 以下规则接口，必须进行登录验证
         */
        registration.addPathPatterns("/api/user/**")
                .addPathPatterns("/api/order/**")
                .addPathPatterns("/api/project/applyLog")
                .addPathPatterns("/api/sesame/**")
        ;


        /**
         * 基于上面规则接口，不需要进行登录验证的接口配置
         */
        registration.excludePathPatterns("/api/sys/sendVerifyCode")
                .excludePathPatterns("/api/user/login")
                .excludePathPatterns("/api/user/register")
                .excludePathPatterns("/api/user/updatePassword")
                .excludePathPatterns("/api/sesame/rollBack/**")
                .excludePathPatterns("/api/carrier/**")
                .excludePathPatterns("/api/ds/**")
                .excludePathPatterns("/api/sesameReport/**")
                .excludePathPatterns("/api/projectView/**")
                .excludePathPatterns("/api/other/**")
                .excludePathPatterns("/api/order/return/**")
                .excludePathPatterns("/api/user/reg/**");
    }

    /**
     * 签名授权拦截
     *
     * @param registry
     */
    private void addSignatureInterceptor(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(appInterceptor());
        /**
         * 以下规则接口，必须进行签名验证
         */
        registration.addPathPatterns("/api/**");

        /**
         * 以下规则接口，不进行签名验证
         */
        registration.excludePathPatterns("/api/sys/test")
                .excludePathPatterns("/api/user/register")
                //通用支付返回页面
                .excludePathPatterns("/api/order/return/**")
                //第三方支付支付宝通知、返回、去支付
                .excludePathPatterns("/api/alipay/**")
                .excludePathPatterns("/api/sys/checkUpdate")
                // 数据魔盒回调
                .excludePathPatterns("/api/carrier/**")
                //芝麻信用回调
                .excludePathPatterns("/api/sesame/rollBack/**")
                //内嵌页面接口请求
                .excludePathPatterns("/api/ds/**")
                .excludePathPatterns("/api/sesameReport/**")
                .excludePathPatterns("/api/projectView/**")
                //协议等页面
                .excludePathPatterns("/api/other/**")
                .excludePathPatterns("/api/user/reg/**")
                .excludePathPatterns("/api/sys/sendVerifyCode");
    }


    /**
     * 视图显示Resolver
     *
     * @param registry
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        super.configureViewResolvers(registry);
        registry.viewResolver(viewResolver());
    }

    /**
     * thymeleaf解析
     *
     * @return
     */
    @Bean
    public ThymeleafViewResolver viewResolver() {
        return new ViewResolverUtils().getThymeleafViewResolver(webApplicationContext.getServletContext(), "/views/", environment);
    }
}

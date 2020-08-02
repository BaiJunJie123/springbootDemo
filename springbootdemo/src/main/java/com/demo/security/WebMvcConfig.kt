package com.demo.security

import com.demo.config.JwtInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.charset.Charset
import java.util.*

/**
 * @author suzhen
 * @create 2018/10/4
 */
@Configuration
open class WebMvcConfig : WebMvcConfigurer {
    /**
     * 解决ie responsebody返回json的时候提示下载问题
     *
     * @return
     */
    fun customJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter {
        val jsonConverter = MappingJackson2HttpMessageConverter()
        val supportedMediaTypes: MutableList<MediaType> = ArrayList()
        supportedMediaTypes.add(MediaType.TEXT_HTML)
        supportedMediaTypes.add(MediaType.TEXT_PLAIN)
        jsonConverter.defaultCharset = Charset.forName("UTF-8")
        jsonConverter.supportedMediaTypes = supportedMediaTypes
        return jsonConverter
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>?>) {
        //去掉测试ie7以上都能正常处理json的返回值
        converters.add(customJackson2HttpMessageConverter())
    }

    /**
     * https://www.cnblogs.com/yuansc/p/9076604.html
     *
     * @param registry
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        //设置允许跨域的路径
        registry.addMapping("/**") //设置允许跨域请求的域名
                .allowedOrigins("*") //是否允许证书 不再默认开启
                .allowCredentials(true)
                .allowedHeaders("*") //设置允许的方法
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE") //跨域允许时间
                .maxAge(3600)
    }

    // 拦截器
    override fun addInterceptors(registry: InterceptorRegistry){
        // 可添加多个
        registry.addInterceptor(JwtInterceptor()).addPathPatterns("/**")
    }
}

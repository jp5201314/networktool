package com.jp.networkarch.converter

/**
 * @ProjectName : NetWorkArchTools
 * @Author : Jason
 * @Time : 2020/8/22 14:13
 * @Description : 文件描述
 */
@Target(
        AnnotationTarget.FUNCTION
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RequestConverter(val format: ConverterFormat = ConverterFormat.JSON)
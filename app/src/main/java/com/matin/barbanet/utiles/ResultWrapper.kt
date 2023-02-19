package com.matin.barbanet.utiles

sealed class ResultWrapper<T>(val data: T?=null, val message:String?=""){
    class Success<T>(data: T): ResultWrapper<T>(data = data)
    class Error<T>(message: String ,data: T?= null): ResultWrapper<T>(message = message, data = data)
    class Loading<T>(data: T? = null): ResultWrapper<T>(data)
}

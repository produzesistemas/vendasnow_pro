package com.produze.sistemas.vendasnow.vendasnowpremium.services.authentication

import com.produze.sistemas.vendasnow.vendasnowpremium.model.Status

sealed class ApiResult <out T> (val status: Status, val data: T?, val message:String?) {

    data class Success<out R>(val _data: R?): ApiResult<R>(
        status = Status.SUCCESS,
        data = _data,
        message = null
    )

    data class Error(val exception: String): ApiResult<Nothing>(
        status = Status.ERROR,
        data = null,
        message = exception
    )

    data class Loading<out R>(val _data: R?, val isLoading: Boolean): ApiResult<R>(
        status = Status.LOADING,
        data = _data,
        message = null
    )
}
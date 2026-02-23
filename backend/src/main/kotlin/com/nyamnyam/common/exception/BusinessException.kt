package com.nyamnyam.common.exception

class BusinessException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)

package com.example.util;


import com.example.common.BaseResult;
import com.example.common.enums.BaseEnumI;
import com.example.common.enums.CommonCodeEnum;

import java.util.Optional;

public class BaseResultUtils {

    public static <T> BaseResult<T> buildBaseResult(T t){
        BaseResult<T> baseResult = new BaseResult<>();
        baseResult.setErrorCode(CommonCodeEnum.SUCCESS.getCode());
        baseResult.setData(t);
        baseResult.setErrorMsg(CommonCodeEnum.SUCCESS.getValue());
        return baseResult;
    }

    /**
     * 创建基础返回对象
     * @return
     */
    public static BaseResult buildEmptyBaseResult(){
        BaseResult baseResult = new BaseResult();
        baseResult.setErrorCode(CommonCodeEnum.SUCCESS.getCode());
        baseResult.setData(Optional.empty());
        baseResult.setErrorMsg(CommonCodeEnum.SUCCESS.getValue());
        return baseResult;
    }


    /**
     * 返回提示性，结果返回
     * @param baseEnumI
     * @return
     */
    public static BaseResult<String> buildBaseResult(BaseEnumI baseEnumI){
        BaseResult<String> baseResult = new BaseResult<>();
        baseResult.setErrorCode(baseEnumI.getCode());
        baseResult.setErrorMsg(baseEnumI.getValue());
        return baseResult;
    }


    /**
     * 返回提示性，结果返回
     * @param baseEnum
     * @return
     */
    public static <T> BaseResult<T> buildBaseResultT(BaseEnumI baseEnum){
        BaseResult<T> baseResult = new BaseResult<>();
        baseResult.setErrorCode(baseEnum.getCode());
        baseResult.setErrorMsg(baseEnum.getValue());
        return baseResult;
    }
}

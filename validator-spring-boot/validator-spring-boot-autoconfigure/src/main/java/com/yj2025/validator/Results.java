package com.yj2025.validator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by serv on 2016/12/3.
 */
public class Results {

    private List<Result> results;

    Results(List<Result> results) {
        this.results = results;
    }

    public List<Result> getResults() {
        return results;
    }

    /**
     * 是否全部验证通过
     * @return true 全部验证成功,没有失败 false 至少包含一个失败
     */
    public boolean validAll(){
        Optional<Result> any = results.stream().filter(result -> !result.isValid()).findAny();
        if(any.isPresent()){
            return false;
        }
        return true;
    }

    /**
     * 如果有验证不通过则抛出错误
     */
    public void error(){
        Optional<Result> first = results.stream().filter(result -> !result.isValid()).findFirst();
        if(first.isPresent()){
            throw new ValidatorException(first.get().getMsg());
        }
    }

    @Override
    public String toString() {
        return results.toString();
    }

    /**
     * 获取所有失败的结果
     * @return
     */
    public List<Result> getErrors(){
        return results.stream().filter(result -> !result.isValid()).collect(Collectors.toList());
    }

    /**
     * 获取第一个失败的结果
     * @return
     */
    public Result getFirstError(){
        List<Result> errors = getErrors();
        if(errors!=null){
            return errors.get(0);
        }
        return null;
    }

    /**
     * 获取所有失败的message信息
     * @return
     */
    public List<String> getErrorMsgs(){
        return getErrors().stream().map(Result::getMsg).collect(Collectors.toList());
    }

    /**
     * 获取第一个失败的message信息
     * @return
     */
    public String getFirstErrorMsg(){
        List<String> messages = getErrorMsgs();
        if(messages!=null){
            return messages.get(0);
        }
        return null;
    }

}

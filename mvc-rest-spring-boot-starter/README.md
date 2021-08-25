# spring mvc 和 feign 异常封装

* 全局json请求进行了包装
    * 如果是feign请求原生返回 所有返回不需要使用respVo包裹一层
    * 如果是其他请求统一使用{success:true,data:[],errCode:'',errMsg:''} 包装起来
* 全局异常进行了控制
    * 如果是feign请求会包含base64的序列化异常堆栈,方便调用端捕获到
    * 其他请求只有在dev环境下才会输出json格式的异常堆栈,方便问题定位
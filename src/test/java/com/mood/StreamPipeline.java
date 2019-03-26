package com.mood;


import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class StreamPipeline implements Pipeline {
    private final ReentrantLock lock =new ReentrantLock();
    private volatile Set<LinkedHashMap<String,Object>> stream;
    private volatile Set<String> setUrl;
    public StreamPipeline(){}
    public StreamPipeline(Set<LinkedHashMap<String,Object>> stream,Set<String> setUrl){
        this.stream=stream;
        this.setUrl=setUrl;
    }
    @Override
    public void process(ResultItems resultItems, Task task) {
        try{
            lock.lock();
            LinkedHashMap<String,Object> treeMap=new LinkedHashMap<>();

            if (!setUrl.contains(resultItems.getRequest().getUrl())){
                setUrl.add(resultItems.getRequest().getUrl());
                treeMap.put("自如链接",resultItems.getRequest().getUrl());
                Iterator var3 = resultItems.getAll().entrySet().iterator();
                while(var3.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry) var3.next();
                    if (entry.getValue() != null&& entry.getValue()!=null) {
                        String value = entry.getValue().toString();
                        if (value.length()>5){
                            switch (entry.getKey()) {
                                case "面积大小":
                                    value = entry.getValue().toString().
                                            substring(entry.getValue().toString().indexOf("面积： ")+3,
                                                    entry.getValue().toString().indexOf(" ㎡"));
                                    break;
                                case "房屋朝向":
                                    value = entry.getValue().toString().
                                            substring(entry.getValue().toString().indexOf("朝向： ")+3,
                                                    entry.getValue().toString().indexOf("</li>"));
                                    break;
                                case "地址":
                                    value = entry.getValue().toString().
                                            substring(entry.getValue().toString().indexOf("ellipsis")+11,
                                                    entry.getValue().toString().indexOf("</span>"));
                                    break;
                                case "房子名称":
                                    value = entry.getValue().toString().
                                            substring(entry.getValue().toString().indexOf("<h2> ")+5,
                                                    entry.getValue().toString().indexOf(" <span"));
                                    break;
                            }
                        }
                        treeMap.put(entry.getKey(), value);
                        stream.add(treeMap);
                    }
                }
            }
        }finally {
            lock.unlock();
        }

    }
}

package com.mood.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author: by Mood
 * @date: 2011-01-14 11:11:11
 * @Description: 操作ES工具包
 * @version: 1.0
 */
public class ElasticSearchHandle {
    private final static Logger logger = LoggerFactory.getLogger(ElasticSearchHandle.class);
    /**
     *
     * @param mustLIst  //must参数
     * @param mustNotLIst //must not参数
     * @param shouldLIst //should参数
     * @param orderByList //sort参数
     * @description: 构建查询串
     * @return
     * @throws IOException
     */
    public static String queryOrderInfo(List mustLIst, List mustNotLIst, List shouldLIst, List orderByList) throws IOException {
        StringBuffer sb=new StringBuffer().append("ES查询");
        XContentBuilder builder =
                XContentFactory.jsonBuilder()
                        .startObject()
                        .field("sort", orderByList)
                        .startObject("query")
                        .startObject("bool")
                        .field("must_not", mustNotLIst)
                        .startArray("must")
                        .startObject()
                        .startObject("bool")
                        .field("must", mustLIst)
                        .endObject()
                        .endObject()
                        .startObject()
                        .startObject("bool")
                        .field("should", shouldLIst)
                        .endObject()
                        .endObject()
                        .endArray()
                        .endObject()
                        .endObject()
                        .endObject();
        String ret=builder.toString();
        sb.append(ret);
        logger.info(sb.toString());
        return ret;
    }
    /**
     *
     * @param mustLIst  //must参数
     * @param mustNotLIst //must not参数
     * @param shouldLIst //should参数
     * @param orderByList //sort参数
     * @description: 构建分页查询查询串
     * @return
     * @throws IOException
     */
    public static String queryOrderInfoLimit( List mustLIst,List mustNotLIst,List shouldLIst, List orderByList,int offset,int pagesize) throws IOException {
        StringBuffer sb=new StringBuffer().append("ES查询");
        XContentBuilder builder =
                XContentFactory.jsonBuilder()
                        .startObject()
                        .field("from", offset)
                        .field("size", pagesize)
                        .field("sort", orderByList)
                        .startObject("query")
                        .startObject("bool")
                        .field("must_not", mustNotLIst)
                        .startArray("must")
                        .startObject()
                        .startObject("bool")
                        .field("must", mustLIst)
                        .endObject()
                        .endObject()
                        .startObject()
                        .startObject("bool")
                        .field("should", shouldLIst)
                        .endObject()
                        .endObject()
                        .endObject()
                        .endArray()
                        .endObject()
                        .endObject()
                        .endObject();
        String ret=builder.toString();
        sb.append(ret);
        logger.info(sb.toString());
        return ret;
    }
    /**
     *
     * @param ret  ES返回结果
     * @description: 解析结果
     * @return
     * @throws IOException
     */
    public static Map<String,Object> analysisResultMap(JSONObject ret){
        Map<String,Object> map= Maps.newHashMap();
        List<JSONObject> list= Lists.newLinkedList();
        JSONObject hits= ret.getJSONObject("hits");
        if(hits!=null){
            int l=hits.getIntValue("total");
            if(l>0){
                JSONArray hitArr=hits.getJSONArray("hits");
                for(int i=0;i<hitArr.size();i++){
                    JSONObject hit=hitArr.getJSONObject(i).getJSONObject("_source");
                    JSONObject result=function.apply(hit);
                    list.add(result);
                }
                map.put("count",l);
                map.put("data",list);
            }
        }
        return map;
    }
    static Function<JSONObject,JSONObject> function= x->{
        JSONObject result=x;
        x.forEach((k,v)->{
            //转换时间
            if (k.contains("Time")){
                if (v!=null&&!v.equals("")&&v.toString().length()==14){
                    try {
                        Date time= DateUtil.convertDate(v.toString(), DateUtil.TIME_FORMAT_LONG);
                        result.put(k,DateUtil.dateFormat(time,DateUtil.defaultDateTimeFormatter));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    result.put(k,"");
                }
            }else {
                result.put(k,(v!=null&&!v.equals(""))?v:"");
            }
        });
        return result;
    };
    /**
     *
     * @param ret  ES返回结果
     * @description: 解析结果
     * @return
     * @throws IOException
     */
    public static Map<String,JSONObject> analysisResultType(JSONObject ret){
        Map<String,JSONObject> map= Maps.newHashMap();
        JSONObject hits= ret.getJSONObject("hits");
        if(hits!=null){
            int l=hits.getIntValue("total");
            if(l>0){
                JSONArray hitArr=hits.getJSONArray("hits");
                for(int i=0;i<hitArr.size();i++){
                    JSONObject hit=hitArr.getJSONObject(i).getJSONObject("_source");
                    JSONObject result=function.apply(hit);
                    map.put("data",result);
                }
            }
        }
        return map;
    }
    public static void main(String args[])throws Exception{
        //must参数
        List mustLIst= Lists.newArrayList();
        //must not参数
        List mustNotLIst=Lists.newArrayList();
        //should参数
        List shouldLIst=Lists.newArrayList();

        List sortLIst=Lists.newArrayList();
        System.out.println(ElasticSearchHandle.queryOrderInfo(mustLIst,mustNotLIst,shouldLIst,sortLIst));
    }
}

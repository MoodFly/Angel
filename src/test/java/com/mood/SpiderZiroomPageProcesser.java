package com.mood;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SpiderZiroomPageProcesser implements PageProcessor {

    private Site site = Site.me().setDomain("http://www.ziroom.com/z/nl/z2.html?qwd=%E9%9C%8D%E8%90%A5");
    @Override
    public void process(Page page) {
//        List<String> links = page.getHtml().links().regex("http://my\\.oschina\\.net/flashsword/blog/\\d+").all();
        List<String> links = page.getHtml().links().regex("www.ziroom.com/z/vr/-?[1-9]\\d*.html").all().stream().map(x->x.substring(14,x.length())).collect(Collectors.toList());
        page.addTargetRequests(links);
        page.putField("房子名称", page.getHtml().xpath("/html/body/div[3]/div[2]/div[1]/h2").toString());
        page.putField("面积大小", page.getHtml().xpath("/html/body/div[3]/div[2]/ul/li[1]").toString());
        page.putField("距离",page.getHtml().xpath("//*[@id=\"lineList\"]").all());
        page.putField("房屋朝向",page.getHtml().xpath("/html/body/div[3]/div[2]/ul/li[2]").all());
        page.putField("地址",page.getHtml().xpath("/html/body/div[3]/div[2]/div[1]/p/span[1]").all());
        page.putField("价格",page.getHtml().xpath("/html/body/div[3]/div[2]/div[1]/p/span[2]").all());
    }

    @Override
    public Site getSite() {
        return site;

    }

    public static void main(String[] args) {
        Set<LinkedHashMap<String,Object>> stream=new HashSet<>(64);
        Set<String> setUrl=new HashSet<>(64);
        StreamPipeline streamPipeline=new StreamPipeline(stream,setUrl);
        ExecutorService executor=Executors.newFixedThreadPool(36);
        CountDownLatch countDownLatch=new CountDownLatch(36);
        for (int i=1;i<=36;i++){
            int finalI = i;
            executor.execute(()->{
                Spider.create(new SpiderZiroomPageProcesser()).addUrl("http://www.ziroom.com/z/nl/z2.html?qwd=%E9%9C%8D%E8%90%A5&p="+ finalI)
                        .addPipeline(streamPipeline).run();
                countDownLatch.countDown();
            });

        }
        try {
            countDownLatch.await();
            stream.forEach(x->{
                x.forEach((k,v)->{
                    System.out.println(k + ":\t" + v);
                });
            });
            executor.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
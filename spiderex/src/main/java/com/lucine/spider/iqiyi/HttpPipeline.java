package com.lucine.spider.iqiyi;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lucine.spider.entity.OttMedia;
import com.lucine.spider.utils.Utils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class HttpPipeline implements Pipeline {
	
	private final Logger log = LoggerFactory.getLogger(HttpPipeline.class);
	
	final String url = "http://127.0.0.1:8080/spider/ottmedia";

	public void process(ResultItems resultItems, Task task) {

		Map<String, Object> all = resultItems.getAll();
		if (all == null) {
			return;
		}

		for(Map.Entry<String, Object> entry:all.entrySet()) {
			
			OttMedia om = (OttMedia) entry.getValue();
			try {
				log.info(om.toString());
				byte[] bytesOM = om.toString().getBytes("utf-8");
				
				Utils.httpPost(bytesOM);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

	}

}

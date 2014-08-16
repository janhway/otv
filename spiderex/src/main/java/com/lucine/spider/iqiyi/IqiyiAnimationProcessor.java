package com.lucine.spider.iqiyi;

import com.lucine.spider.entity.MediaType;

import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;

public class IqiyiAnimationProcessor  {
	
	//private final Logger log = LoggerFactory.getLogger(IqiyiAnimationProcessor.class);

	private AnimationOrTVProcessor processor;
	
	IqiyiAnimationProcessor(String startUrl, int threadNum, Pipeline pipeline, Downloader downLoader) {
		this.processor = new AnimationOrTVProcessor(MediaType.ANIMATION, startUrl,threadNum, pipeline,downLoader);
	}
	
	public void run() {
		processor.run();
	}
	
	public static void main(String[] args) {
		String startUrl = "http://list.iqiyi.com/www/4/------------------.html";
		IqiyiAnimationProcessor pp = new IqiyiAnimationProcessor(startUrl,1, new FilePipeline("D:\\logs"),new HttpClientDownloader());
		pp.run();
	}
}

package com.lucine.spider.iqiyi;

import com.lucine.spider.entity.MediaType;

import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;


public class IqiyiTvProcessor  {
	
	//private final Logger log = LoggerFactory.getLogger(IqiyiTvProcessor.class);

	private AnimationOrTVProcessor processor;
	
	IqiyiTvProcessor(String startUrl, int threadNum, Pipeline pipeline, Downloader downLoader) {
		this.processor = new AnimationOrTVProcessor(MediaType.TV, startUrl,threadNum, pipeline,downLoader);
	}
	
	public void run() {
		processor.run();
	}
	
	public static void main(String[] args) {
		String startUrl = "http://list.iqiyi.com/www/2/-------------10-1-1-iqiyi--.html";
		IqiyiTvProcessor pp = new IqiyiTvProcessor(startUrl,1, new HttpPipeline(),new HttpClientDownloader());
		pp.run();
	}
}

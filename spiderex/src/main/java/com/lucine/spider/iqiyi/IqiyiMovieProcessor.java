package com.lucine.spider.iqiyi;

import java.util.ArrayList;
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lucine.spider.entity.Episode;
import com.lucine.spider.entity.MediaType;
import com.lucine.spider.entity.Program;

public class IqiyiMovieProcessor implements PageProcessor {

	private final Logger log = LoggerFactory.getLogger(IqiyiMovieProcessor.class);
	private final Site site = Site.me().setDomain("www.iqiyi.com").setCharset("utf-8");

	private String startUrl;
	private int threadNum;
	private Pipeline pipeline;
	private Downloader downLoader;
	
	IqiyiMovieProcessor(String startUrl, int threadNum, Pipeline pipeline, Downloader downLoader) {
	    this.startUrl = startUrl;
		this.threadNum = threadNum;
	    this.pipeline = pipeline;
		this.downLoader = downLoader;	
	}	
	
	public void process(Page page) {
		String curPageUrl = page.getUrl().toString();

		if (curPageUrl.indexOf("list.iqiyi.com") >= 0) {
			parseMediaListInfo(page);
		} else {
			parseMediaDetailInfo(page);
		}

	}

	private void parseMediaListInfo(Page page) {
		try {
			log.info(" Movie parseMediaListInfo One page begin:");

			List<String> detailsRequests = new ArrayList<String>();

			Document doc = page.getHtml().getDocument();
			Elements pgEles = doc.select("p.site-piclist_info_title");

			for (int i = 0; i < pgEles.size(); i++) {
				Element pgE = pgEles.get(i).select("a").get(0);
				String pgUrl = pgE.attr("href");
				String pgTitle = pgE.attr("title");
				log.info("title=" + pgTitle + ",href=" + pgUrl);
				detailsRequests.add(pgUrl);
			}

			String nextPage = doc.select("a[data-key=down]").first()
					.attr("href");

			log.info("nextPage=" + nextPage);

			if (nextPage != null && nextPage.length() > 0) {
				page.addTargetRequest(nextPage);
			}

			if (detailsRequests.size() > 0) {
				page.addTargetRequests(detailsRequests);
			}

			page.setSkip(true);

			log.info("Movie parseMediaListInfo One page finished....");
		} catch (Exception e) {
			log.error("", e);
		}

	}

	private void parseMediaDetailInfo(Page page) {

		try {
			Program prgm = new Program();
			prgm.setCpName("iqiyi");
			prgm.setMediaType(MediaType.MOVIES);
			
			StringBuilder bld = new StringBuilder();

			Document doc = page.getHtml().getDocument();

			String title = doc.select("meta[itemprop=name]").first().attr("content");
			prgm.setTitle(title);

			Elements eles = doc.select("div[data-widget-moviepaybtn=btn]");
			if (eles != null && eles.first() != null) {
				log.info("Igore program which title=" + title);
				page.setSkip(true);
				return;
			}

			String datePublished = doc.select("meta[itemprop=datePublished]").first().attr("content");
			prgm.setReleaseYear(datePublished);

			// 导演
			Elements directorEles = doc.select("span[itemprop=director]");
			for (int i = 0; i < directorEles.size(); i++) {
				bld.append(directorEles.get(i).select("meta[itemprop=name]")
						.first().attr("content"));
				bld.append("/");
			}
			if (bld.length() > 0) {
				bld.deleteCharAt(bld.length() - 1);
			}
			String directors = bld.toString();
			prgm.setDirectors(directors);

			// 主演
			bld.setLength(0);
			Elements actorEles = doc.select("item[itemprop=actor]");
			for (int i = 0; i < actorEles.size(); i++) {
				bld.append(actorEles.get(i).select("meta[itemprop=name]")
						.first().attr("content"));
				bld.append("/");
			}
			if (bld.length() > 0) {
				bld.deleteCharAt(bld.length() - 1);
			}
			String actors = bld.toString();
			prgm.setActors(actors);

			// 类型
			bld.setLength(0);
			Elements genreEles = doc.select("meta[itemprop=genre]");
			for (int i = 0; i < genreEles.size(); i++) {
				bld.append(genreEles.get(i).attr("content"));
				bld.append("/");
			}
			if (bld.length() > 0) {
				bld.deleteCharAt(bld.length() - 1);
			}
			String genre = bld.toString();
			prgm.setGenre(genre);

			// 简介
			String description = doc.select("meta[itemprop=description]")
					.first().attr("content");
			prgm.setDescription(description);

			// 海报URL
			String picUrl = doc.select("item meta[itemprop=image]").first()
					.attr("content");
			prgm.setPicUrl(picUrl);

			// 地区
			String origCountry = doc
					.select("item meta[itemprop=contentLocation]").first()
					.attr("content");
			prgm.setOriginCountry(origCountry);

			// 评分
			String ratingValue = doc
					.select("item div meta[itemprop=ratingValue]").first()
					.attr("content");
			prgm.setScore(ratingValue);

			// 播放次数  fix it later.

			
			// 子集信息，电影固定只有一个子集
			Episode ep = new Episode();
			ep.setTitle(title);
			ep.setPlayUrl(page.getUrl().toString());
			
			// 播放时长
			String duration = doc.select("item meta[itemprop=duration]")
					.first().attr("content");
			ep.setDuration(duration);
			
			List<Episode> episodes = new ArrayList<Episode>();

			episodes.add(ep);
			prgm.setEpisodeList(episodes);
			prgm.setEpisodeTotalNum("1");
			prgm.setEpisodeUpdNum("1");
			
			page.putField(title, prgm);

		} catch (Exception e) {
			page.setSkip(true);
			log.error(page.getUrl().toString());
			log.error("", e);
		}
	}	

	public Site getSite() {
		return site;
	}
	
	public void run() {
		// when thread can't stop correctly,JVM will not terminate. fix it later. 
		Spider.create(this).thread(threadNum).addUrl(startUrl).addPipeline(pipeline).run();
	}
	
	public static void main(String[] args) {
		String startUrl = "http://list.iqiyi.com/www/1/-------------10-1-1-iqiyi--.html";
		IqiyiMovieProcessor pp = new IqiyiMovieProcessor(startUrl,1, new FilePipeline("D:\\logs"),new HttpClientDownloader());
		pp.run();
	}
}

// http://www.iqiyi.com/dianying


package com.lucine.example.spiderex;

import java.util.ArrayList;
import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IqiyiMovieProcessor implements PageProcessor {

	private Logger log = LoggerFactory.getLogger(IqiyiMovieProcessor.class);
	
	private Site site = Site.me().setDomain("www.iqiyi.com").setCharset("utf-8");

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
			StringBuilder logSB = new StringBuilder();
			StringBuilder bld = new StringBuilder();

			Document doc = page.getHtml().getDocument();

			String title = doc.select("meta[itemprop=name]").first().attr("content");
			page.putField("title", title);
			logSB.append("title=" + title);

			Elements eles = doc.select("div[data-widget-moviepaybtn=btn]");
			if (eles != null && eles.first() != null) {
				log.info("Igore program which title=" + title);
				page.setSkip(true);
				return;
			}
			
			page.putField("playUrl", page.getUrl());
			logSB.append(",playUrl=" + page.getUrl());

			String datePublished = doc.select("meta[itemprop=datePublished]").first().attr("content");
			page.putField("shangYing", datePublished);
			logSB.append(",shangYing=" + datePublished);

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
			String director = bld.toString();
			page.putField("director", director);
			logSB.append(",director=" + director);

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
			String actor = bld.toString();
			page.putField("actor", actor);
			logSB.append(",actor=" + actor);

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
			page.putField("genre", genre);
			logSB.append(",genre=" + genre);

			// 简介
			String description = doc.select("meta[itemprop=description]")
					.first().attr("content");
			page.putField("description", description);
			logSB.append(",description=" + description);

			// 海报URL
			String picUrl = doc.select("item meta[itemprop=image]").first()
					.attr("content");
			page.putField("picUrl", picUrl);
			logSB.append(",picUrl=" + picUrl);

			// 播放时长
			String duration = doc.select("item meta[itemprop=duration]")
					.first().attr("content");
			page.putField("duration", duration);
			logSB.append(",duration=" + duration);

			// 播放时长
			String origCountry = doc
					.select("item meta[itemprop=contentLocation]").first()
					.attr("content");
			page.putField("origCountry", origCountry);
			logSB.append(",origCountry=" + origCountry);

			// 评分
			String ratingValue = doc
					.select("item div meta[itemprop=ratingValue]").first()
					.attr("content");
			page.putField("ratingValue", ratingValue);
			logSB.append(",ratingValue=" + ratingValue);

			// 播放次数

			//log.info("MediaDetailInfo:" + logSB.toString());

		} catch (Exception e) {
			log.error("", e);
		}
	}	

	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		Spider.create(new IqiyiMovieProcessor())
				.thread(5)
				.addUrl("http://list.iqiyi.com/www/1/-------------10-1-1-iqiyi--.html").run();
				//.addPipeline(new SolrPipeline()).run();
	}
}

// http://www.iqiyi.com/dianying


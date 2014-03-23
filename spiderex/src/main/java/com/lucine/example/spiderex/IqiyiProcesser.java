package com.lucine.example.spiderex;

import java.util.ArrayList;
import java.util.List;

import us.codecraft.webmagic.OttMedia;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.LucenePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IqiyiProcesser implements PageProcessor {

	private Site site = Site.me().setDomain("www.iqiyi.com")
			.setCharset("utf-8");

	public void process(Page page) {
		String curPageUrl = page.getUrl().toString();

		if (curPageUrl.indexOf("list.iqiyi.com") >= 0) {
			parseMediaListInfo(page);
		} else {
			// 详细ye
			parseMediaDetailInfo(page);
		}

	}

	private void parseMediaListInfo(Page page) {
		System.out.println("aaaaaaaaaaaaaaaaaaa-begin:");

		List<String> detailsRequests = new ArrayList<String>();

		Document doc = page.getHtml().getDocument();
		Elements pgEles = doc.select("div.list0").first()
				.select("li.j-listanim");

		for (int i = 0; i < pgEles.size(); i++) {
			Element pgE = pgEles.get(i);
			String pgUrl = pgE.select("a").get(1).attr("href");
			String pgTitle = pgE.select("a").get(1).text();
			System.out.println("pgUrl=" + pgUrl);
			System.out.println("pgTitle=" + pgTitle);
			detailsRequests.add(pgUrl);
		}

		String nextPage = doc.select("div.page").first()
				.select("a:contains(下一页)").first().attr("href");

		System.out.println("nextPage=" + nextPage);
		if (nextPage != null && nextPage.length() > 0) {
			page.addTargetRequest(nextPage);
		}

		if (detailsRequests.size() > 0) {
			page.addTargetRequests(detailsRequests);
		}

		page.setSkip(true);

		System.out.println("aaaaaaaaaaaaaaaaaaa-end.");
	}

	private void parseMediaDetailInfo(Page page) {

		//OttMedia om = new OttMedia();

		Document doc = page.getHtml().getDocument();

		String title = doc.select("meta[itemprop=name]").first()
				.attr("content");
		page.putField("id", title);
		System.out.println("id=" + title);

		String descrip = doc.select("meta[itemprop=description]").first()
				.attr("content");
		page.putField("descrip", descrip);
		System.out.println("descrip=" + descrip);

		// String shangYing =
		// doc.select("meta[itemprop=datePublished]").first().attr("content");
		// System.out.println("shangYing=" + shangYing);

		/*
		 * Elements directorEles = doc.select("span[itemprop=director]"); for
		 * (int i = 0; i < directorEles.size(); i++) {
		 * bld.append(directorEles.get
		 * (i).select("meta[itemprop=name]").first().attr("content"));
		 * bld.append("/"); } if (bld.length() > 0) {
		 * bld.deleteCharAt(bld.length() - 1); } String director =
		 * bld.toString(); System.out.println("director=" + director);
		 */

		// 主演
		/*
		 * bld.delete(0, bld.length()); Elements actorEles =
		 * doc.select("item[itemprop=actor]"); for (int i = 0; i <
		 * actorEles.size(); i++) {
		 * bld.append(actorEles.get(i).select("meta[itemprop=name]"
		 * ).first().attr("content"));; bld.append("/"); } if (bld.length() > 0)
		 * { bld.deleteCharAt(bld.length() - 1); } String actor =
		 * bld.toString(); System.out.println("actor=" + actor);
		 */

		// 类型
		/*
		 * bld.delete(0, bld.length()); Elements genreEles =
		 * doc.select("meta[itemprop=genre]"); for (int i = 0; i <
		 * genreEles.size(); i++) {
		 * bld.append(genreEles.get(i).attr("content")); bld.append("/"); } if
		 * (bld.length() > 0) { bld.deleteCharAt(bld.length() - 1); } String
		 * genre = bld.toString(); System.out.println("genre=" + genre);
		 */

		// 播放次数
		// 评分
		
		Element dt = doc.select(
				"div[class=clearfix border_gray film_information margin_b20]")
				.first();

		if (dt != null) {
			System.out.println("parseMediaDetailInfoByForm1");
			parseMediaDetailInfoByForm1(page);
		} else {
			System.out.println("parseMediaDetailInfoByForm2");
			parseMediaDetailInfoByForm2(page);
		}
	}

	private void parseMediaDetailInfoByForm1(Page page) {

		Document doc = page.getHtml().getDocument();

		// 播放次数
		// 评分

		StringBuilder bld = new StringBuilder(200);

		Element dt = doc.select(
				"div[class=clearfix border_gray film_information margin_b20]")
				.first();

		String shangYing = dt.select("span:contains(首映)").first()
				.nextElementSibling().text();
		page.putField("shangYing", shangYing);
		System.out.println("shangYing=" + shangYing);

		// 导演
		String director = "";
		Elements directorEles = dt.select("span:contains(导演)").first().parent()
				.select("a");
		for (int i = 0; i < directorEles.size(); i++) {
			bld.append(directorEles.get(i).text());
			bld.append("/");
		}
		if (bld.length() > 0) {
			bld.deleteCharAt(bld.length() - 1);
			director = bld.toString();
		}
		page.putField("director", director);
		System.out.println("director=" + director);

		// 类型
		String genre = "";
		bld.delete(0, bld.length());
		Elements genreEles = dt.select("span:contains(类型)").first().parent()
				.select("a");
		for (int i = 0; i < genreEles.size(); i++) {
			bld.append(genreEles.get(i).text());
			bld.append("/");
		}
		if (bld.length() > 0) {
			bld.deleteCharAt(bld.length() - 1);
			genre = bld.toString();
		}
		page.putField("genre", genre);
		System.out.println("genre=" + genre);

		// 主演
		String actor = "";
		bld.delete(0, bld.length());
		Elements actorEles = dt.select("span:contains(主演)").first().parent()
				.select("a");
		for (int i = 0; i < actorEles.size(); i++) {
			bld.append(actorEles.get(i).text());
			bld.append("/");
		}
		if (bld.length() > 0) {
			bld.deleteCharAt(bld.length() - 1);
			actor = bld.toString();
		}
		page.putField("actor", actor);
		System.out.println("actor=" + actor);

		return;
	}

	private void parseMediaDetailInfoByForm2(Page page) {

		Document doc = page.getHtml().getDocument();

		// 播放次数
		// 评分

		StringBuilder bld = new StringBuilder(200);

		Element dt = doc.select("div.movieMsg").first();

		//首映
		String shangYing = dt.select("p[rseat=time]").first().text();
		shangYing = shangYing.substring("发布时间： ".length());
		page.putField("shangYing", shangYing);
		System.out.println("shangYing=" + shangYing);

		// 导演
		String director = "";
		Elements directorEles = dt.select("p[rseat=导演]").first().select("a");
		for (int i = 0; i < directorEles.size(); i++) {
			bld.append(directorEles.get(i).text());
			bld.append("/");
		}
		if (bld.length() > 0) {
			bld.deleteCharAt(bld.length() - 1);
			director = bld.toString();
		}
		page.putField("director", director);
		System.out.println("director=" + director);

		// 类型
		String genre = "";
		bld.delete(0, bld.length());
		Elements genreEles = dt.select("p[rseat=类型]").first().select("a");
		for (int i = 0; i < genreEles.size(); i++) {
			bld.append(genreEles.get(i).text());
			bld.append("/");
		}
		if (bld.length() > 0) {
			bld.deleteCharAt(bld.length() - 1);
			genre = bld.toString();
		}
		page.putField("genre", genre);
		System.out.println("genre=" + genre);

		// 主演
		String actor = "";
		bld.delete(0, bld.length());
		Elements actorEles = dt.select("div.peos-info").first().select("a");
		for (int i = 0; i < actorEles.size(); i++) {
			bld.append(actorEles.get(i).text());
			bld.append("/");
		}
		if (bld.length() > 0) {
			bld.deleteCharAt(bld.length() - 1);
			actor = bld.toString();
		}
		page.putField("actor", actor);
		System.out.println("actor=" + actor);

		return;
	}

	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		Spider.create(new IqiyiProcesser())
				.thread(5)
				.addUrl("http://list.iqiyi.com/www/1/------------3-1-1-1---.html")
				.addPipeline(new SolrPipeline()).run();
	}
}

// http://www.iqiyi.com/dianying


package com.lucine.spider.iqiyi;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
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

import com.lucine.spider.entity.SubStoryInfo;
import com.lucine.spider.entity.MediaType;
import com.lucine.spider.entity.OttMedia;

public class IqiyiMovieProcessor implements PageProcessor, Task {

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
				//page.addTargetRequest(nextPage);
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
			OttMedia prgm = new OttMedia();
			prgm.setCpName("iqiyi");
			prgm.setMediaType(MediaType.MOVIES);

			Document doc = page.getHtml().getDocument();

			String title = getItem(doc, "meta[itemprop=name]");
			prgm.setTitle(title);

			Elements eles = doc.select("div[data-widget-moviepaybtn=btn]");
			if (eles != null && eles.first() != null) {
				log.info("Igore payed program which title=" + title);
				page.setSkip(true);
				return;
			}

			// 年代 格式 20120102 fix it later
			String datePublished = getItem(doc, "meta[itemprop=datePublished]");
			prgm.setReleaseYear(transformDate(datePublished));

			// 导演
			String directors = getDirectorOrActor(doc,
					"span[itemprop=director]");
			prgm.setDirectors(directors);

			// 主演
			String actors = getDirectorOrActor(doc, "item[itemprop=actor]");
			prgm.setActors(actors);

			// 类型
			prgm.setGenre(getGenres(doc));

			// 简介
			String description = getItem(doc, "meta[itemprop=description]");
			prgm.setDescription(description);

			// 海报URL
			String picUrl = getItem(doc, "item meta[itemprop=image]");
			prgm.setPicUrl(picUrl);

			// 播放URL
			prgm.setPlayUrl(page.getUrl().toString());

			// 地区
			String origCountry = getItem(doc,
					"item meta[itemprop=contentLocation]");
			prgm.setOriginCountry(origCountry);

			// 评分
			String ratingValue = getItem(doc,
					"item div meta[itemprop=ratingValue]");
			prgm.setScore(ratingValue);

			// 播放次数
			String albumId = getAlbumId(doc);
			if (albumId != null) {
				String playNum = getPlayNumByAlbumid(albumId);
				prgm.setPlayNum(playNum);
			}

			// 子集信息，电影固定只有一个子集
			SubStoryInfo ep = new SubStoryInfo();
			ep.setTitle(title);
			ep.setPlayUrl(page.getUrl().toString());
			ep.setPicUrl(picUrl);
			// 播放时长
			String duration = getItem(doc, "item meta[itemprop=duration]");
			ep.setDuration(transformDuration(duration));

			List<SubStoryInfo> episodes = new ArrayList<SubStoryInfo>();

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
	
	private String getItem(Document doc, String cssSelector) {
		return doc.select(cssSelector).first().attr("content");
	}

	private String getGenres(Document doc) {
		StringBuilder bld = new StringBuilder(100);
		Elements genreEles = doc.select("meta[itemprop=genre]");
		for (int i = 0; i < genreEles.size(); i++) {
			bld.append(genreEles.get(i).attr("content"));
			bld.append("/");
		}
		if (bld.length() > 0) {
			bld.deleteCharAt(bld.length() - 1);
		}
		return bld.toString();
	}

	private String getDirectorOrActor(Document doc, String cssSelector) {
		
		//String selV = String.format("span[itemprop=%s]", cssSelector);
		
		StringBuilder bld = new StringBuilder(100);
		
		Elements directorEles = doc.select(cssSelector);
		for (int i = 0; i < directorEles.size(); i++) {
			bld.append(directorEles.get(i).select("meta[itemprop=name]")
					.first().attr("content"));
			bld.append("/");
		}
		if (bld.length() > 0) {
			bld.deleteCharAt(bld.length() - 1);
		}

		return bld.toString();
	}
	
	//2010年03月26日 转成20100326
	private String transformDate(String date) {
		Pattern p = Pattern.compile("([0-9]{4}).*([0-9]{2}).*([0-9]{2})");
		Matcher m = p.matcher(date);
		if (m.find()) {
			return m.group(1)+m.group(2)+m.group(3);
		}
		return null;
	}
	
	// P5872S 转成5872
	private String transformDuration(String duration) {
		Pattern p = Pattern.compile("\\s*P(\\d+)S\\s*");
		Matcher m = p.matcher(duration);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
	
	private String getScoreByAlbumid(String albumId) {
		String url = "http://score.video.qiyi.com/ud/"+albumId+"/";
		Page page = downLoader.download(new Request(url), this);
		String data = page.getRawText();
		log.info("data="+data);
		Pattern p = Pattern.compile(".*,\"score\":\\s*(\\d+\\.\\d).*");
		Matcher m = p.matcher(data);
		if(m.find()) {
			return m.group(1);
		}
		return null;
	}

	private String getPlayNumByAlbumid(String albumId) {
		//http://cache.video.qiyi.com/jp/pc/167419/
		String url = "http://cache.video.qiyi.com/jp/pc/"+albumId+"/";
		Page page = downLoader.download(new Request(url), this);
		String data = page.getRawText();
		log.info("data="+data);
		Pattern p = Pattern.compile(".*:\\s*(\\d+)}]");
		Matcher m = p.matcher(data);
		if(m.find()) {
			return m.group(1);
		}
		return null;
	}

	private String getAlbumId(Document doc) {
		String albumId = null;
		try {
			Element albumIdEle = doc.select("div.videoArea").first()
					.select("div#flashbox").first();
			albumId = albumIdEle.attr("data-player-albumid");
			log.info("albumId=" + albumId);
		} catch (Exception e) {
			log.error("", e);
		}
		return albumId;
	}

	public Site getSite() {
		return site;
	}

	public String getUUID() {
		if (site != null) {
            return site.getDomain();
        }
		return "no UUID-fix it later";
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


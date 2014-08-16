package com.lucine.spider.iqiyi;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lucine.spider.entity.Episode;
import com.lucine.spider.entity.MediaType;
import com.lucine.spider.entity.Program;

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

public class IqiyiAnimationProcessor implements PageProcessor,Task {
	
	private final Logger log = LoggerFactory.getLogger(IqiyiAnimationProcessor.class);
	private final Site site = Site.me().setDomain("www.iqiyi.com").setCharset("utf-8");

	private String startUrl;
	private int threadNum;
	private Pipeline pipeline;
	private Downloader downLoader;
	
	IqiyiAnimationProcessor(String startUrl, int threadNum, Pipeline pipeline, Downloader downLoader) {
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
			log.info(" Animation parseMediaListInfo One page begin:");

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

			String nextPage = doc.select("a[data-key=down]").first().attr("href");

			log.info("nextPage=" + nextPage);

			if (nextPage != null && nextPage.length() > 0) {
				//page.addTargetRequest(nextPage);
			}

			if (detailsRequests.size() > 0) {
				page.addTargetRequests(detailsRequests);
			}

			page.setSkip(true);

			log.info("Animation parseMediaListInfo One page finished....");
		} catch (Exception e) {
			log.error(page.getUrl().toString());
			log.error("", e);
		}

	}

	private void parseMediaDetailInfo(Page page) {

		try {
			StringBuilder logSB = new StringBuilder();
			StringBuilder bld = new StringBuilder();
			Document doc = page.getHtml().getDocument();
			
			//div class="rtxt introZone"
			Elements introZZ = null;
			introZZ = doc.select("div[class=rtxt introZone]");
			if (introZZ == null || introZZ.size() == 0)
			{
				introZZ = doc.select("div[class=rtxt]");
			}
			Element introZone = introZZ.first();

			// 获取节目名称
			Element titleEle = introZone.select("a[rseat=tvName]").first();
			if (titleEle == null)
			{
				titleEle = introZone.select("h2 strong a").first();
			}					
			String title = 	titleEle.text().trim();
			if(title == null || title.length() == 0)
			{
				log.error("cant get title.fix it later.url="+page.getUrl().toString());
				page.setSkip(true);
				return;
			}
			
			Program prgm = new Program();
			prgm.setCpName("iqiyi");
			prgm.setMediaType(MediaType.ANIMATION);
			
			// 获取节目名称
			prgm.setTitle(title);
			logSB.append("title=" + title);
			
			String albumId = getAlbumId(introZone);
			if (albumId != null) {
				// 获取评分
				String score = getScoreByAlbumid(albumId);
				prgm.setScore(score);
				logSB.append(",score=" + score);
				
				// 获取播放次数
				String playNum = getPlayNumByAlbumid(albumId);
				prgm.setPlayNum(playNum);
				logSB.append(",playNum=" + playNum);
			}

//			String datePublished =introZone.select("a[rseat=issueTime]").first().text().trim();
//			prgm.setReleaseYear(datePublished);
//			logSB.append(",releaseyear=" + datePublished);

			// 类型
			bld.setLength(0);
			Elements eles = introZone.select("p:contains(标签)").first().select("a");
			for (int i = 0; i < eles.size(); i++) {
				String text = eles.get(i).text().trim();
				if (text == null || text.length() == 0) {
					continue;
				}
				bld.append(eles.get(i).text().trim());
				bld.append("/");
			}
			if (bld.length() > 0) {
				bld.deleteCharAt(bld.length() - 1);
			}
			String genre = bld.toString();
			prgm.setGenre(genre);
			logSB.append(",genre=" + genre);

			// 简介   id="j-album-more"
			String description = introZone.select("div[id=j-album-more]").first().text().trim();
			prgm.setDescription(description);
			logSB.append(",description=" + description);
			
			// 多少集
			//span class="upALL"
			try{
			String totalCount = doc.select("span.upAll").first().text();
			prgm.setEpisodeTotalNum(totalCount);
			}catch(Exception ee){
				prgm.setEpisodeTotalNum("fix it later");
			}
					
			List<Episode> episodes = getEpisodeList(doc);
			prgm.setEpisodeList(episodes);
			
			page.putField(prgm.getTitle(), prgm);

		} catch (Exception e) {
			page.setSkip(true);
			log.error(page.getUrl().toString());
			log.error("", e);
		}
	}
	
	
	private void getInfoByScriptArea(Document doc, Program prgm) {
		//<script type="text/javascript">
		Element ele = doc.select("script[type=text/javascript]:contains(var albumInfo=)").first();
		if (ele == null) {
			return;
		}
		Pattern p = Pattern.compile("var albumInfo=(.*)");
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

	private String getAlbumId(Element introZone) {
		//span id="widget-playcount"
		Element albumIdEle = introZone.select("span#widget-playcount").first();
		String albumId = albumIdEle.attr("data-dynamic-albumid");
		log.info("albumId="+albumId);
		return albumId;
	}

	private List<Episode> parseEpisodeListInfo(Page page) {

		page.setSkip(true);

		List<Episode> episodes = null;
		try {
			episodes = new ArrayList<Episode>();

			// <li><a data-widget-qidanadd="qidanadd"
			Document doc = page.getHtml().getDocument();
			Elements pgEles = doc.select("li");
			for (Element e : pgEles) {
				
				Element subE = e.select("p a").first();
				String title = subE.attr("title");

				Episode episode = new Episode();
				episode.setTitle(title);

				String playUrl = subE.attr("href");
				episode.setPlayUrl(playUrl);

				String picUrl = e.select("a img").first().attr("data-lazy");
				episode.setPicUrl(picUrl);

				String dur = e.select("a span.s2").first().text();
				episode.setDuration(dur);

				episodes.add(episode);
			}
		} catch (Exception e) {
			log.error(page.getUrl().toString());
			log.error("", e);
		}

		return episodes;
	}
	
	private List<Episode> getEpisodeList(Document doc)
	{
		List<Episode> episodes = new ArrayList<Episode>();
		
		//div id="j-album-1" style="display: none;">/common/topicinc/294633_26361/playlist_1.inc</div>
        int i=0;
		while(true)
        {
			i++;
        	String id = "div#j-album-"+i;
        	Element ele = doc.select(id).first();
        	if (ele == null)
        	{
        		break;
        	}
        	
        	String url = "http://www.iqiyi.com"+ele.text().trim();
        	log.info("EpisodeUrl="+url);
        	
        	Page page00 = this.downLoader.download(new Request(url), this);
        	
        	episodes.addAll(parseEpisodeListInfo(page00));
        	
        }
		
		return episodes;
	}

	public void run() {
		// when thread can't stop correctly,JVM will not terminate. fix it later. 
		Spider.create(this).thread(threadNum).addUrl(startUrl).addPipeline(pipeline).run();
	}
	
	public static void main(String[] args) {
		String startUrl = "http://list.iqiyi.com/www/4/------------------.html";
		IqiyiAnimationProcessor pp = new IqiyiAnimationProcessor(startUrl,1, new FilePipeline("D:\\logs"),new HttpClientDownloader());
		pp.run();
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

}

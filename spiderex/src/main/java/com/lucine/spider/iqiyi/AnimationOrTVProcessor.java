package com.lucine.spider.iqiyi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lucine.spider.entity.Episode;
import com.lucine.spider.entity.MediaType;
import com.lucine.spider.entity.Program;

public class AnimationOrTVProcessor implements PageProcessor,Task {
	
	private final Logger log = LoggerFactory.getLogger(AnimationOrTVProcessor.class);
	private final Site site = Site.me().setDomain("www.iqiyi.com").setCharset("utf-8");

	private MediaType mt;
	private String startUrl;
	private int threadNum;
	private Pipeline pipeline;
	private Downloader downLoader;
	
	AnimationOrTVProcessor(MediaType mt, String startUrl, int threadNum, Pipeline pipeline, Downloader downLoader) {
	    this.mt = mt;
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
			log.info("parseMediaListInfo---One page begin.MediaType="+mt.toString());

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

			log.info("parseMediaListInfo---One page finished. MediaType="+mt.toString());

		} catch (Exception e) {
			log.error(page.getUrl().toString());
			log.error("", e);
		}

	}

	private void parseMediaDetailInfo(Page page) {

		String curUrl = page.getUrl().toString();
		
		try {
			Document doc = page.getHtml().getDocument();
			
			Program prgm = new Program();
			prgm.setCpName("iqiyi");
			prgm.setMediaType(MediaType.ANIMATION);
			
			//getTitleByScriptArea(doc,prgm);
			
			getInfoByScriptArea(doc,prgm);
			
			if(prgm.getTitle() == null)
			{
				log.error("fail to get title.curUrl="+curUrl);
				page.setSkip(true);
				return;
			}
			
			List<Episode> episodes = getEpisodeList(doc);
			prgm.setEpisodeList(episodes);
			
			page.putField(prgm.getTitle(), prgm);
			
			log.info(prgm.toString());

		} catch (Exception e) {
			page.setSkip(true);
			log.error("Exception:curUrl="+ curUrl);
			log.error("", e);
		}
	}
	
	private void getInfoByScriptArea(Document doc, Program prgm) {
		
		Pattern p = Pattern.compile(".*var\\s*albumInfo\\s*=\\s*(\\{.*\\}).*");
		
		String albumInfoJson = null;
		
		Elements eles = doc.select("script[type=text/javascript]");
		for (Element ele : eles) {
			String data = ele.outerHtml();
			Matcher m = p.matcher(data);
			if (m.find()) {
				albumInfoJson = m.group(1);
				log.info("albumInfoJson=" + albumInfoJson);
				break;
			}
		}
		
		if (albumInfoJson == null) {
			return;
		}
		
		JSONObject jo = JSONObject.parseObject(albumInfoJson);

		Object ob = null;
		JSONObject subJo = null;
		
		// 节目名称
		ob = jo.get("tvName");
		prgm.setTitle(ob.toString());

		// 海报
		ob = jo.get("tvPictureUrl");
		prgm.setPicUrl(ob.toString());

		// 播放url
		ob = jo.get("tvPurl");
		prgm.setPlayUrl(ob.toString());
		
		// 导演   "directors":["蒋家骏"]
		String strValue  = getDirectorOrActor(jo, "directors");
		prgm.setDirectors(strValue);
		
		// 演员 
		strValue  = getDirectorOrActor(jo, "mainActors");
		prgm.setActors(strValue);
		
		// 年代
		ob = jo.get("issueTime");
		prgm.setReleaseYear(ob.toString());

		strValue = getAreaOrGenre(jo, "types");
		prgm.setGenre(strValue);
		
		// 地区
		strValue = getAreaOrGenre(jo, "areas");
		prgm.setOriginCountry(strValue);
		
		// 评分
		subJo = (JSONObject)jo.get("albumUpDown");
		if (subJo != null) {
			ob = subJo.get("score");
			prgm.setScore(ob.toString());
		}

		// 播放次数
		ob = jo.get("playCounts");
		prgm.setPlayNum(String.valueOf(ob));
		
		// 更新到第几集
		ob = jo.get("currentMaxEpisode");
		prgm.setEpisodeUpdNum(ob.toString());

		// 总集数
		ob = jo.get("episodeCounts");
		prgm.setEpisodeTotalNum(ob.toString());

		// 描述
		ob = jo.get("tvDesc");
		prgm.setDescription(ob.toString());
		
		return;
	}

	private String getDirectorOrActor(JSONObject jo, String key) {
		Object ob;
		StringBuilder sb = new StringBuilder(100);
		ob = jo.get(key);
		JSONArray joa = (JSONArray)ob;
		Iterator<Object> it = joa.iterator();
		while(it.hasNext()) {
            sb.append(it.next().toString()).append("/");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	private String getAreaOrGenre(JSONObject jo, String keyName) {
		
		StringBuilder sb = new StringBuilder(100);
		
		JSONObject subJo;
		// 类型
		subJo = JSONObject.parseObject((String)jo.get(keyName));
		Set<String> keySet = subJo.keySet();
		for (String str : keySet) {
			sb.append(subJo.get(str).toString()).append("/");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		
		return sb.toString();
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
				String title = subE.text();

				Episode episode = new Episode();
				episode.setTitle(title);

				String playUrl = subE.attr("href");
				episode.setPlayUrl(playUrl);

				String picUrl = e.select("a img").first().attr("data-lazy");
				if (picUrl == null) {
					picUrl = e.select("a img").first().attr("src");
				}
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
		AnimationOrTVProcessor pp = new AnimationOrTVProcessor(MediaType.ANIMATION, startUrl,1, new FilePipeline("D:\\logs"),new HttpClientDownloader());
		pp.run();
		
		startUrl = "http://list.iqiyi.com/www/2/-------------10-1-1-iqiyi--.html";
		AnimationOrTVProcessor ppa = new AnimationOrTVProcessor(MediaType.TV, startUrl,1, new FilePipeline("D:\\logs"),new HttpClientDownloader());
		ppa.run();
	}
	
	public Site getSite() {
		return site;
	}

	public String getUUID() {
		if (site != null) {
            return site.getDomain();
        }
		return "no UUID-fix it later";
	}}

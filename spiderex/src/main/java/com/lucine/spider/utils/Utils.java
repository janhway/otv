package com.lucine.spider.utils;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.FilePipeline;

import com.lucine.spider.entity.MediaType;
import com.lucine.spider.iqiyi.AnimationOrTVProcessor;

public class Utils {

	private final static Logger log = LoggerFactory.getLogger(Utils.class);

	public static int httpPost(byte[] body) throws Exception {
		int retCode = -1;

		HttpPost httpPost = new HttpPost("http://127.0.0.1:8080/spider/ottmedia");
		httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
		HttpEntity entity = new ByteArrayEntity(body); 
		httpPost.setEntity(entity);
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(5000).setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).build();
		httpPost.setConfig(requestConfig);

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = HttpClients.custom().build();
			response = httpClient.execute(httpPost);

			StatusLine sl = response.getStatusLine();
			System.out.println(sl);
			retCode = sl.getStatusCode();

			HttpEntity entity2 = response.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			EntityUtils.consume(entity2);
		} catch (Exception e) {
			log.error("",e);
		} finally {
			httpClient.close();
			response.close();
		}

		return retCode;
	}
	
	public static void main(String[] args) {
		try {
			httpPost("aaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbcvcccccccccc".getBytes("utf-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}

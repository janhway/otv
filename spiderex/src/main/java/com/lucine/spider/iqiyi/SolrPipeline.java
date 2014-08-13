package com.lucine.spider.iqiyi;

import java.net.MalformedURLException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
//import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class SolrPipeline implements Pipeline {

	private SolrServer getSolrServer() throws MalformedURLException {
		// the instance can be reused
		return new HttpSolrServer("http://localhost:8983/solr/collection1/");
	}

	public void process(ResultItems resultItems, Task task) {

		Map<String, Object> all = resultItems.getAll();
		if (all == null) {
			return;
		}

		try {

			SolrServer server = getSolrServer();
			SolrInputDocument document = new SolrInputDocument();

			for (String key : all.keySet()) {
				String value = (String) all.get(key);
				document.addField(key, value);
			}

			server.add(document);
			server.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

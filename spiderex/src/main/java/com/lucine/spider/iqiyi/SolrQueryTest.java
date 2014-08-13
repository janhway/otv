package com.lucine.spider.iqiyi;

import java.net.MalformedURLException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrQueryTest {
	public static SolrServer getSolrServer() throws MalformedURLException {
		// the instance can be reused
		return new HttpSolrServer("http://localhost:8983/solr/collection1/");
	}
	
	public static void main(String[] args) {
		try {
			SolrServer server = getSolrServer();
			SolrQuery solrQuery = new SolrQuery().setQuery("私人");
			QueryResponse rsp = server.query(solrQuery);

			SolrDocumentList docs = rsp.getResults();
			for (SolrDocument doc : docs) {
				System.out.println(doc.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

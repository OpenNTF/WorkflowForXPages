/*
 * © Copyright IBM Corp. 2013
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package com.ibm.xsp.xflow.activiti.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

public class HttpClientUtil {  
	private static Logger logger = Logger.getLogger(HttpClientUtil.class.getName());
	
	public static String post(String url, Cookie[] cookies,String content) {
    	String body = null;
    	try{
    		StringBuffer buffer = new StringBuffer();
    		DefaultHttpClient httpClient = new DefaultHttpClient();
    		HttpPost postRequest = new HttpPost(url);
     
    		StringEntity input = new StringEntity(content);
    		input.setContentType("application/json");
    		postRequest.setEntity(input);
    		
    		httpClient.setCookieStore(new BasicCookieStore());
    		String hostName = new URL(url).getHost();
    		String domain = hostName.substring(hostName.indexOf("."));
    		for(int i=0;i<cookies.length;i++){
    			if(logger.isLoggable(Level.FINEST)){
    				logger.finest("Cookie:"+cookies[i].getName()+":"+ cookies[i].getValue()+":"+cookies[i].getDomain()+":"+cookies[i].getPath());
    			}
	    		BasicClientCookie cookie = new BasicClientCookie(cookies[i].getName(), cookies[i].getValue());
	            cookie.setVersion(0);
	            cookie.setDomain(domain);
	            cookie.setPath("/");
	
	            httpClient.getCookieStore().addCookie(cookie);
    		}    		
    		HttpResponse response = httpClient.execute(postRequest);
    		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			if(logger.isLoggable(Level.FINEST)){
				logger.finest("Output from Server .... \n");
			}
			while ((output = br.readLine()) != null) {
				if(logger.isLoggable(Level.FINEST)){
					logger.finest(output);
				}
				buffer.append(output);
			}

			httpClient.getConnectionManager().shutdown();
			
			body = buffer.toString();
     
    	}catch(Exception e){
    		e.printStackTrace();
    	}
          
          
        return body;  
    }
      
	public static String get(String url, Cookie[] cookies) {
    	String body = null;
    	try{
    		StringBuffer buffer = new StringBuffer();
    		DefaultHttpClient httpClient = new DefaultHttpClient();
    		HttpGet getRequest = new HttpGet(url);
     
    		httpClient.setCookieStore(new BasicCookieStore());
    		for(int i=0;i<cookies.length;i++){
    			logger.finest("Cookie:"+cookies[i].getName()+":"+ cookies[i].getValue()+":"+cookies[i].getDomain()+":"+cookies[i].getPath());
	    		BasicClientCookie cookie = new BasicClientCookie(cookies[i].getName(), cookies[i].getValue());
	            cookie.setVersion(0);
	            URL urlParse = new URL(url);
	            String host = urlParse.getHost();
	            String domain = null;
	            if(host!=null && host.indexOf('.')>0){
	            	domain = host.substring(host.indexOf('.')+1);
	            }
	            logger.finest("Domain:"+domain);
	            cookie.setDomain(domain);
	            cookie.setPath("/");
	
	            httpClient.getCookieStore().addCookie(cookie);
    		}    		
    		HttpResponse response = httpClient.execute(getRequest);
    		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			logger.finest("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				logger.finest(output);
				buffer.append(output);
			}

			httpClient.getConnectionManager().shutdown();
			
			body = buffer.toString();
     
    	}catch(Exception e){
    		e.printStackTrace();
    	}
          
          
        return body;  
    }
          
      
}  
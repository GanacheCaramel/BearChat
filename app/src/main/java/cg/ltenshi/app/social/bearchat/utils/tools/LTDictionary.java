package cg.ltenshi.app.social.bearchat.utils.tools;

import java.util.ArrayList;
import java.util.List;

public class LTDictionary{
	private List<String> keys;
	private List<String> contents;
	
	public void LTDictionary(){}
	
	public void add(String key, String content){
		keys.add(key);
		contents.add(content);
	}
	
	public String getByKey(String key){
		
		for (String k : keys){
			if (key.equals(k)){
				return keys.get( keys.indexOf(k) );
			}
		}
		return null;
	}
	
	public Boolean haveThis(String key){
		for(String k : keys){
			if(k.equals(key)){ return true; }
		}
		return false;
	}
}
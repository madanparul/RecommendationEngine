package com.pb.am.recommendation.algo;

import java.io.*;
import java.util.*;

import com.pb.analytics.dto.AMRecommendationCommodityDto;
import com.pb.analytics.dto.AMRecommendationDto;
import com.pb.analytics.dto.AMRecommendationFrequentSetDto;
import com.pb.utils.Utils;

public class DICAlgo {
	private final int DC = 1;
	private final int DS = 2;
	private final int SC = 3;
	private final int SS = 4;
	int N;
	int M;
	int stepm;
	int tid;
	int k;
	int setnum;
	int minsup;
	hashtreenode root;
	String DSset, DCset, SCset, SSset;
	
	public static void main(String[] args) throws Exception{
		
		DICAlgo obj= new DICAlgo();
		obj.getAssosicationRules(null);
	}

	public AMRecommendationDto getAssosicationRules(Map<String,String> productMap) throws Exception{
		AMRecommendationDto retObj = new AMRecommendationDto();
   	 
		String fullitemset = new String();
		String transa = new String();
		String str0 = new String();
		String oneline = new String();
		StringTokenizer st;
		DataInputStream file_in;
		BufferedReader data_in;
		Date d = new Date();
		int j;
		int lineprocessed = 0;
		int numRead = 0;
		boolean qiaole = false;
		long s1, s2;

		getconfig();

		root = new hashtreenode(SS, null, 0, 1, 0);
		for (int i = 1; i <= N; i++) {
			String str = new String(Integer.toString(i));
			hashtreenode htn = new hashtreenode(DC, str, 0, 1, 0);
			if (root.ht == null) {
				Hashtable<String, hashtreenode> ht = new Hashtable<String, hashtreenode>();
				root.ht = ht;
			}
			root.ht.put(str, htn);
		}

		BufferedInputStream fstream=(BufferedInputStream)DICAlgo.class.getResourceAsStream("/TransactionFile");
		file_in = new DataInputStream(fstream);
		data_in = new BufferedReader(new InputStreamReader(file_in));

		for (int i = 1; i <= N; i++) {
			fullitemset = fullitemset.concat(" " + Integer.toString(i));
		}
		fullitemset = fullitemset.trim();

		k = 0;
		tid = 1;

		d = new Date();
		s1 = d.getTime();
		while (dashfound(root)) {
			k++;
			lineprocessed = 0;
			while (lineprocessed < stepm) {
				oneline = data_in.readLine();
				numRead++;
				if ((oneline == null) || (numRead > M)) {
					numRead = 0;

					fstream=(BufferedInputStream)DICAlgo.class.getResourceAsStream("/TransactionFile");
					file_in = new DataInputStream(fstream);
					data_in = new BufferedReader(new InputStreamReader(file_in));

					tid = 1;
					if (qiaole) {
						oneline = data_in.readLine();
					} else {
						break;
					}
				}

				st = new StringTokenizer(oneline.trim());
				j = 0;
				transa = new String();
				while (st.hasMoreTokens()) {
					j++;
					str0 = st.nextToken();
					if (Integer.valueOf(str0).intValue() != 0) {
						transa = transa.concat(" " + Integer.toString(j));
					}
				}
				transa = transa.trim();
				transatrahashtree(transa, root, 0);

				lineprocessed++;
				tid++;

				qiaole = (lineprocessed >= stepm && tid > M);

				if (tid > M) {
					tid = 1;
				}
			}
			checkcounter(root, fullitemset, 0);
			checkhashtree(root, fullitemset, 0);
			checkcountedall(root, fullitemset, 0);
		}

		DSset = new String();
		DCset = new String();
		SCset = new String();
		SSset = new String();
		printhashtree(root, fullitemset, 0);

		StringTokenizer sss, tokenizedItemset;
		int i = 1;
		boolean found = false, first = true;
		do {
			first = true;
			sss = new StringTokenizer(SSset, ",");
			found = false;
			while (sss.hasMoreTokens()) {
				String superset1 = new String(sss.nextToken());
				tokenizedItemset = new StringTokenizer(superset1);
				if (tokenizedItemset.countTokens() == i) {
					found = true;
					String [] dArr = superset1.split(" ");
					AMRecommendationFrequentSetDto fDto = new AMRecommendationFrequentSetDto();
	                for(int ji=0; ji < dArr.length; ji++) {
	                  
					   AMRecommendationCommodityDto cDto = new AMRecommendationCommodityDto();
           		       cDto.setId(dArr[ji]);
           		       cDto.setName(productMap.get(dArr[ji]));
           		       fDto.getCommodityList().add(cDto);
	                }
	                fDto.setSize(i);
	            	retObj.getFrequentSetList().add(fDto);
					
				}
			}
			
			i++;
		} while (found);
		data_in.close();
		
		return retObj;
	}

	class hashtreenode {

		int state;
		String itemset;
		int counter;
		int starting;
		int startingk;
		boolean needcounting;
		Hashtable<String, hashtreenode> ht;

		public hashtreenode(int state, String itemset, int counter, int starting, int startingk) {
			this.state = state;
			if (itemset == null) {
				this.itemset = new String();
			} else {
				this.itemset = new String(itemset);
			}
			this.counter = counter;
			this.starting = starting;
			this.startingk = startingk;
			needcounting = true;
			ht = new Hashtable<String, hashtreenode>();
		}

		public hashtreenode() {
			this.state = DC;
			this.itemset = new String();
			this.counter = 0;
			this.starting = 0;
			this.startingk = 0;
			needcounting = true;
			ht = new Hashtable<String, hashtreenode>();
		}
	}

	public void getconfig() throws IOException {
		String oneline = new String();
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		try {
			oneline = Utils.getResourceProperty("2.noOfProducts");
			N = Integer.valueOf(oneline).intValue();

			oneline =  Utils.getResourceProperty("2.noOfTxns");
			M = Integer.valueOf(oneline).intValue();

			oneline =  Utils.getResourceProperty("2.minSup");
			minsup = Integer.valueOf(oneline).intValue();

			oneline = Utils.getResourceProperty("2.dic.step.size");
			stepm = Integer.valueOf(oneline).intValue();

			String user = "";
			while (stepm < 5) {
				try {
					user = reader.readLine();
				} catch (Exception e) {
					System.out.println(e);
				}

				stepm = Integer.valueOf(user).intValue();
			}

		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public String getitemat(int i, String itemset) {
		StringTokenizer st = new StringTokenizer(itemset);
		String str1 = new String(itemset);
		for (int a = 0; a < i; a++) {
			str1 = st.nextToken();
		}
		return (str1);
	}

	public int itemsetsize(String itemset) {
		StringTokenizer st = new StringTokenizer(itemset);
		return st.countTokens();
	}

	boolean dashfound(hashtreenode htn) {
		if (htn.state == DS || htn.state == DC) {
			return true;
		}
		for (Enumeration e = htn.ht.elements(); e.hasMoreElements();) {
			if (dashfound((hashtreenode) e.nextElement())) {
				return true;
			}
		}
		return false;
	}

	public void printhashtree(hashtreenode htn, String transa, int a) {
		String state = new String();
		switch (htn.state) {
		case DC:
			state = "DC";
			DCset = DCset.concat(htn.itemset);
			DCset = DCset.concat(",");
			break;
		case DS:
			state = "DS";
			DSset = DSset.concat(htn.itemset);
			DSset = DSset.concat(",");
			break;
		case SC:
			state = "SC";
			SCset = SCset.concat(htn.itemset);
			SCset = SCset.concat(",");
			break;
		case SS:
			state = "SS";
			SSset = SSset.concat(htn.itemset);
			SSset = SSset.concat(",");
			break;
		}
		if (htn.ht == null) {
			return;
		}
		for (int b = a + 1; b <= itemsetsize(transa); b++) {
			if (htn.ht.containsKey(getitemat(b, transa))) {
				printhashtree((hashtreenode) htn.ht.get(getitemat(b, transa)), transa, b);
			}
		}
	}

	public void transatrahashtree(String transa, hashtreenode htn, int a) {
		if (htn.needcounting) {
			htn.counter++;
		}
		if (htn.ht == null) {
			return;
		} else {
			for (int b = a + 1; b <= itemsetsize(transa); b++) {
				if (htn.ht.containsKey(getitemat(b, transa))) {
					transatrahashtree(transa, (hashtreenode) htn.ht.get(getitemat(b, transa)), b);
				}
			}
		}
	}

	public void checkcountedall(hashtreenode htn, String transa, int startfrom) {
		if (htn.starting == tid && k != htn.startingk) {
			if (htn.state == DS) {
				htn.state = SS;
			} else if (htn.state == DC) {
				htn.state = SC;
			}

			htn.needcounting = false;
		}

		if (htn.ht == null || htn.ht.isEmpty()) {
			return;
		}

		for (int c = startfrom + 1; c <= N; c++) {
			if (htn.ht.containsKey(getitemat(c, transa))) {
				checkcountedall((hashtreenode) htn.ht.get(getitemat(c, transa)), transa, c);
			}
		}
	}

	public void checkcounter(hashtreenode htn, String transa, int startfrom) {
		//if its a dashed circle and frequent, change its state to a dashed square
		if (htn.state == DC && htn.counter >= ((minsup * M) / 100)) {
			htn.state = DS;
		}

		if (htn.ht.isEmpty()) {
			return;
		}

		for (int c = startfrom + 1; c <= N; c++) {
			if (htn.ht.containsKey(getitemat(c, transa))) {
				checkcounter((hashtreenode) htn.ht.get(getitemat(c, transa)), transa, c);
			}
		}
	}

	public void checkhashtree(hashtreenode htn, String transa, int startfrom) {
		String superset = new String();
		String subset = new String();
		StringTokenizer stsuperset, stsubset;
		boolean dcfound;

		if (htn.state == DS) {
			superset = gensuperset(htn.itemset);
			if (superset != null) {
				stsuperset = new StringTokenizer(superset, ",");
				while (stsuperset.hasMoreTokens()) {
					String superset1 = new String(stsuperset.nextToken());
					if (htn.ht.containsKey(getitemat(itemsetsize(superset1), superset1))) {
						continue;
					}
					subset = gensubset(superset1);
					stsubset = new StringTokenizer(subset, ",");
					dcfound = false;
					while (stsubset.hasMoreTokens()) {
						if (circlefound(root, stsubset.nextToken(), 0)) {
							dcfound = true;
							break;
						}
					}
					if (!dcfound) {
						hashtreenode tmphtn = new hashtreenode(DC, superset1, 0, tid, k);
						htn.ht.put(getitemat(itemsetsize(superset1), superset1), tmphtn);
					}
				}
			}
		}

		if (htn.ht == null || htn.ht.isEmpty()) {
			return;
		}

		for (int c = startfrom + 1; c <= N; c++) {
			if (htn.ht.containsKey(getitemat(c, transa))) {
				checkhashtree((hashtreenode) htn.ht.get(getitemat(c, transa)), transa, c);
			}
		}
	}

	public boolean circlefound(hashtreenode htn, String itemset, int startfrom) {
		if (htn.state == DC || htn.state == SC) {
			return true;
		}

		for (int c = startfrom + 1; c <= itemsetsize(itemset); c++) {
			if (htn.ht.containsKey(getitemat(c, itemset))) {
				return circlefound((hashtreenode) htn.ht.get(getitemat(c, itemset)), itemset, c);
			}
		}

		return false;
	}

	public String gensubset(String itemset) {
		int len = itemsetsize(itemset);
		int j;
		String str1;
		String str2;
		String str3 = new String();

		if (len == 1) {
			return null;
		}
		for (int i = 1; i <= len; i++) {
			StringTokenizer st = new StringTokenizer(itemset);
			str1 = new String();
			for (j = 1; j < i; j++) {
				str1 = str1.concat(st.nextToken());
				str1 = str1.concat(" ");
			}
			str2 = st.nextToken();
			for (j = i + 1; j <= len; j++) {
				str1 = str1.concat(st.nextToken());
				str1 = str1.concat(" ");
			}
			if (i != 1) {
				str3 = str3.concat(",");
			}
			str3 = str3.concat(str1.trim());
		}

		return str3;
	}

	public String gensuperset(String itemset) {
		String str1, str2;
		int c;
		int i1 = itemset.lastIndexOf(" ");

		if (i1 == -1) {
			str1 = new String(itemset);
		} else {
			str1 = new String(itemset.substring(i1 + 1));
		}

		c = Integer.valueOf(str1).intValue();
		if (c == N) {
			return null;
		} else {
			str2 = new String();
			for (int b = c + 1; b < N; b++) {
				str2 = str2.concat(itemset);
				str2 = str2.concat(" ");
				str2 = str2.concat(Integer.toString(b));
				str2 = str2.concat(",");
			}
			str2 = str2.concat(itemset);
			str2 = str2.concat(" ");
			str2 = str2.concat(Integer.toString(N));
		}
		return str2;
	}
}

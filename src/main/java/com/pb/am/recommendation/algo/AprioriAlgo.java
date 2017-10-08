package com.pb.am.recommendation.algo;

import java.io.*;
import java.util.*;

import org.apache.lucene.util.FrequencyTrackingRingBuffer;

import com.pb.analytics.dto.AMRecommendationCommodityDto;
import com.pb.analytics.dto.AMRecommendationDto;
import com.pb.analytics.dto.AMRecommendationFrequentSetDto;
import com.pb.utils.Utils;

public class AprioriAlgo {

	public static void main(String [] args) throws Exception {
		AprioriAlgo o = new AprioriAlgo();
		o.getAssosicationRules(null);
	}
    private final int HT = 1;
    private final int IL = 2;
    int N;
    int M;
    Vector<Vector<String>> largeitemset = new Vector<Vector<String>>();
    Vector<candidateelement> candidate = new Vector<candidateelement>();
    int minsup;
    String fullitemset;
    
    public AMRecommendationDto getAssosicationRules(Map<String,String> productMap) throws Exception{
    	 AMRecommendationDto retObj = new AMRecommendationDto();
    	 
    	 candidateelement cande;
         int k = 0;
         Vector large = new Vector();
         Date d = new Date();
         long s1, s2;
         getconfig();

         fullitemset = new String();
         fullitemset = fullitemset.concat("1");
         for (int i = 2; i <= N; i++) {
             fullitemset = fullitemset.concat(" ");
             fullitemset = fullitemset.concat(Integer.toString(i));
         }

         //start time
         d = new Date();
         s1 = d.getTime();

         while (true) {
             k++;
             cande = new candidateelement();
             cande.candlist = createcandidate(k);
             if (cande.candlist.isEmpty()) {
                 break;
             }
             cande.htroot = null;
             candidate.addElement(cande);
             ((candidateelement) candidate.elementAt(k - 1)).htroot = createcandidatehashtree(k);
             transatraverse(k);
             createlargeitemset(k);
             Vector vec = (Vector) (largeitemset.elementAt(k - 1));
             for(int i=0; i<vec.size(); i++) {
            	 AMRecommendationFrequentSetDto fDto = new AMRecommendationFrequentSetDto();
            	 String [] dArr = ((String)vec.get(i)).split(" ");
            	 for(int j=0; j < dArr.length; j++) {
            		 AMRecommendationCommodityDto cDto = new AMRecommendationCommodityDto();
            		 cDto.setId(dArr[j]);
            		 cDto.setName(productMap.get(dArr[j]));
            		 fDto.getCommodityList().add(cDto);
            	 }
            	 fDto.setSize(k);
            	 retObj.getFrequentSetList().add(fDto);
             }
         }
         return retObj;
    }
    
    class candidateelement {

        hashtreenode htroot;
        Vector candlist;
    }

    class hashtreenode {

        int nodeattr;
        int depth;
        Hashtable<String, hashtreenode> ht;
        Vector<itemsetnode> itemsetlist;

        public void hashtreenode() {
            nodeattr = HT;
            ht = new Hashtable<String, hashtreenode>();
            itemsetlist = new Vector<itemsetnode>();
            depth = 0;
        }

        public void hashtreenode(int i) {
            nodeattr = i;
            ht = new Hashtable<String, hashtreenode>();
            itemsetlist = new Vector<itemsetnode>();
            depth = 0;
        }
    }

    class itemsetnode {

        String itemset;
        int counter;

        public itemsetnode(String s1, int i1) {
            itemset = new String(s1);
            counter = i1;
        }

        public itemsetnode() {
            itemset = new String();
            counter = 0;
        }

        public String toString() {
            String tmp = new String();
            tmp = tmp.concat("<\"");
            tmp = tmp.concat(itemset);
            tmp = tmp.concat("\",");
            tmp = tmp.concat(Integer.toString(counter));
            tmp = tmp.concat(">");
            return tmp;
        }
    }

    public void printhashtree(hashtreenode htn, String transa, int a) {
        if (htn.nodeattr == IL) {
        } else { // HT
            if (htn.ht == null) {
                return;
            }
            for (int b = a + 1; b <= N; b++) {
                if (htn.ht.containsKey((getitemat(b, transa)))) {
                    printhashtree((hashtreenode) htn.ht.get((getitemat(b, transa))), transa, b);
                }
            }
        }
    }

    public void getconfig() throws IOException {
        String oneline = new String();
        int i = 0;

        try {
            oneline = Utils.getResourceProperty("2.noOfProducts");
            N = Integer.valueOf(oneline).intValue();

            oneline =  Utils.getResourceProperty("2.noOfTxns");
            M = Integer.valueOf(oneline).intValue();

            oneline =  Utils.getResourceProperty("2.minSup");
            minsup = Integer.valueOf(oneline).intValue();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String getitemat(int i, String itemset) {
        String str1 = new String(itemset); //copy the itemset into a new string
        StringTokenizer st = new StringTokenizer(itemset); //create a tokenizer
        int j;

        if (i > st.countTokens()) {
        }

        for (j = 1; j <= i; j++) {
            str1 = st.nextToken();
        }

        return (str1);
    }

    public int itemsetsize(String itemset) {
        StringTokenizer st = new StringTokenizer(itemset);
        return st.countTokens();
    }

    public String gensubset(String itemset) {
        int len = itemsetsize(itemset);
        int i, j;
        String str1;
        String str2 = new String();
        String str3 = new String();

        if (len == 1) {
            return null;
        }
        for (i = 1; i <= len; i++) {
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

    public Vector createcandidate(int n) {
        Vector<String> tempcandlist = new Vector<String>();
        Vector ln_1 = new Vector();
        int i, j, length1;
        String cand1 = new String();
        String cand2 = new String();
        String newcand = new String();
        if (n == 1) {
            for (i = 1; i <= N; i++) {
                tempcandlist.addElement(Integer.toString(i));
            }
        } else {
            ln_1 = (Vector) largeitemset.elementAt(n - 2);
            length1 = ln_1.size();
            for (i = 0; i < length1; i++) {
                cand1 = (String) ln_1.elementAt(i);
                for (j = i + 1; j < length1; j++) {
                    cand2 = (String) ln_1.elementAt(j);
                    newcand = new String();
                    if (n == 2) {
                        newcand = cand1.concat(" ");
                        newcand = newcand.concat(cand2);
                        tempcandlist.addElement(newcand.trim());
                    } else {
                        int c;
                        String i1, i2;
                        boolean same = true;

                        for (c = 1; c <= n - 2; c++) {
                            i1 = getitemat(c, cand1);
                            i2 = getitemat(c, cand2);
                            if (i1.compareToIgnoreCase(i2) != 0) {
                                same = false;
                                break;
                            } else {
                                newcand = newcand.concat(" ");
                                newcand = newcand.concat(i1);
                            }
                        }
                        if (same) {
                            i1 = getitemat(n - 1, cand1);
                            i2 = getitemat(n - 1, cand2);
                            newcand = newcand.concat(" ");
                            newcand = newcand.concat(i1);
                            newcand = newcand.concat(" ");
                            newcand = newcand.concat(i2);
                            tempcandlist.addElement(newcand.trim());
                        }
                    }
                }
            }
        }
        if (n <= 2) {
            return tempcandlist;
        }
        Vector<String> newcandlist = new Vector<String>();
        for (int c = 0; c < tempcandlist.size(); c++) {
            String c1 = (String) tempcandlist.elementAt(c);
            String subset = gensubset(c1);
            StringTokenizer stsubset = new StringTokenizer(subset, ",");
            boolean fake = false;
            while (stsubset.hasMoreTokens()) {
                if (!ln_1.contains(stsubset.nextToken())) {
                    fake = true;
                    break;
                }
            }
            if (!fake) {
                newcandlist.addElement(c1);
            }
        }
        return newcandlist;
    }

    public hashtreenode createcandidatehashtree(int n) {
        int i, len1;
        hashtreenode htn = new hashtreenode();
        if (n == 1) {
            htn.nodeattr = IL;
        } else {
            htn.nodeattr = HT;
        }

        len1 = ((candidateelement) candidate.elementAt(n - 1)).candlist.size();
        for (i = 1; i <= len1; i++) {
            String cand1 = new String();
            cand1 = (String) ((candidateelement) candidate.elementAt(n - 1)).candlist.elementAt(i - 1);
            genhash(1, htn, cand1);
        }
        return htn;
    }

    public void genhash(int i, hashtreenode htnf, String cand) {

        int n = itemsetsize(cand);
        if (i == n) {
            htnf.nodeattr = IL;
            htnf.depth = n;
            itemsetnode isn = new itemsetnode(cand, 0);
            if (htnf.itemsetlist == null) {
                htnf.itemsetlist = new Vector<itemsetnode>();
            }
            htnf.itemsetlist.addElement(isn);
        } else {
            if (htnf.ht == null) {
                htnf.ht = new Hashtable<String, hashtreenode>(HT);
            }
            if (htnf.ht.containsKey((getitemat(i, cand)))) {
                htnf = (hashtreenode) htnf.ht.get((getitemat(i, cand)));
                genhash(i + 1, htnf, cand);
            } else {
                hashtreenode htn = new hashtreenode();
                htnf.ht.put((getitemat(i, cand)), htn);
                if (i == n - 1) {
                    htn.nodeattr = IL;
                    //Vector isl=new Vector();
                    //htn.itemsetlist=isl;
                    genhash(i + 1, htn, cand);
                } else {
                    htn.nodeattr = HT;
                    //Hashtable ht=new Hashtable();
                    //htn.ht=ht;
                    genhash(i + 1, htn, cand);
                }
            }
        }
    }

    public void createlargeitemset(int n) {
        Vector candlist = new Vector();
        Vector<String> lis = new Vector<String>();
        hashtreenode htn = new hashtreenode();
        int i;

        candlist = ((candidateelement) candidate.elementAt(n - 1)).candlist;
        htn = ((candidateelement) candidate.elementAt(n - 1)).htroot;
        getlargehash(0, htn, fullitemset, lis);
        largeitemset.addElement(lis);
    }

    public void getlargehash(int i, hashtreenode htnf, String transa, Vector<String> lis) {
        Vector tempvec = new Vector();
        int j;

        if (htnf.nodeattr == IL) {
            tempvec = htnf.itemsetlist;
            for (j = 1; j <= tempvec.size(); j++) {
                if (((itemsetnode) tempvec.elementAt(j - 1)).counter >= ((minsup * M) / 100)) {
                    lis.addElement(((itemsetnode) tempvec.elementAt(j - 1)).itemset);
                }
            }
        } else {
            if (htnf.ht == null) {
                return;
            }
            for (int b = i + 1; b <= N; b++) {
                if (htnf.ht.containsKey((getitemat(b, transa)))) {
                    getlargehash(b, (hashtreenode) htnf.ht.get((getitemat(b, transa))), transa, lis);
                }
            }
        }
    }

    public void transatraverse(int n) {
    	BufferedInputStream fstream;
    	DataInputStream file_in;
        BufferedReader data_in;
        String oneline = new String();
        int i = 0, j = 0;
        String transa;
        hashtreenode htn = new hashtreenode();
        StringTokenizer st;
        String str0;
        int numRead = 0;

        htn = ((candidateelement) candidate.elementAt(n - 1)).htroot;
        try {

        	fstream=(BufferedInputStream)DICAlgo.class.getResourceAsStream("/TransactionFile");
			file_in = new DataInputStream(fstream);
			data_in = new BufferedReader(new InputStreamReader(file_in));

            while (true) {
                transa = new String();
                oneline = data_in.readLine();
                numRead++;

                if ((oneline == null) || (numRead > M)) {
                    break;
                }

                st = new StringTokenizer(oneline.trim());
                j = 0;

                while ((st.hasMoreTokens()) && j < N) {
                    j++;
                    str0 = st.nextToken();
                    i = Integer.valueOf(str0).intValue();
                    if (i != 0) {
                        transa = transa.concat(" ");
                        transa = transa.concat(Integer.toString(j));
                    }
                }
                transa = transa.trim();
                transatrahash(0, htn, transa);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void transatrahash(int i, hashtreenode htnf, String transa) {
        Vector itemsetlist = new Vector();
        int j, lastpos, len;
        String d;
        itemsetnode tmpnode = new itemsetnode();
        StringTokenizer st;

        if (htnf.nodeattr == IL) {
            itemsetlist = (Vector) htnf.itemsetlist;
            len = itemsetlist.size();
            for (j = 0; j < len; j++) {
                st = new StringTokenizer(transa);
                tmpnode = (itemsetnode) itemsetlist.elementAt(j);
                d = getitemat(htnf.depth, tmpnode.itemset);

                while (st.hasMoreTokens()) {
                    if (st.nextToken().compareToIgnoreCase(d) == 0) {
                        ((itemsetnode) (itemsetlist.elementAt(j))).counter++;
                    }
                }
            }
            return;
        } else {
            for (int b = i + 1; b <= itemsetsize(transa); b++) {
                if (htnf.ht.containsKey((getitemat(b, transa)))) {
                    transatrahash(i, (hashtreenode) htnf.ht.get((getitemat(b, transa))), transa);
                }
            }
        }

    }
}

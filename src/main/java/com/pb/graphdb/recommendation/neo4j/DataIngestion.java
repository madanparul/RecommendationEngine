package com.pb.graphdb.recommendation.neo4j;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class DataIngestion {
	public enum nodeLabels implements Label {PRODUCT, USER}
	public enum relLabels implements RelationshipType {BOUGHT, SIMILARTO}

	public void addNode(String nodeType, String name,Map<String,String> attrMap) {
		GraphDatabaseService di = DatabaseInit.getInstance();
		Transaction txn = di.beginTx();
		Node n = di.createNode(nodeLabels.valueOf(nodeType));
		n.setProperty("id", name);
		Iterator<String> it = attrMap.keySet().iterator();
		while(it.hasNext()) {
			String attrKey = it.next();
			String attrVal = attrMap.get(attrKey);
			n.setProperty(attrKey, attrVal);
		}
		txn.success();
		txn.close();
	}    

	public void addRelation(String fileName) throws Exception {
		GraphDatabaseService di = DatabaseInit.getInstance();
		Transaction txn = di.beginTx();
		FileInputStream fstream = new FileInputStream(fileName);
		DataInputStream keywordStream = new DataInputStream(fstream);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(keywordStream));
		String strLine = null;
		int count = 0;
		Map<Integer,String> propIdxMap = new HashMap<Integer,String>();
		while ((strLine = br2.readLine()) != null) {
			String [] dArr =strLine.split(",");
			Result r = di.execute("MATCH(n:"+dArr[0]+") where n.id=\""+dArr[1]+"\" return n");
			Node node1 = (Node)r.next().get("n");
			r = di.execute("MATCH(n:"+dArr[3]+") where n.id=\""+dArr[4]+"\" return n");
			Node node2 = (Node)r.next().get("n");
			node1.createRelationshipTo(node2, relLabels.valueOf(dArr[2]));
		}
		txn.success();
		txn.close();
	}
	public void addNodes(String fileName,String type) throws Exception {
		DataIngestion di = new DataIngestion();
		FileInputStream fstream = new FileInputStream(fileName);
		String nodeType = type;
		DataInputStream keywordStream = new DataInputStream(fstream);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(keywordStream));
		String strLine = null;
		int count = 0;
		Map<Integer,String> propIdxMap = new HashMap<Integer,String>();
		while ((strLine = br2.readLine()) != null) {
			Map<String,String> propMap = new HashMap<String,String>(); 
			String [] dArr = strLine.split(",");
			if(count == 0) {
				for(int i=1;i<dArr.length;i++) {
					propIdxMap.put(i, dArr[i]);
				}
				count++;
				continue;
			}
			String name = dArr[0];
			for(int i=1;i<dArr.length;i++) {
				if(!dArr[i].isEmpty()) {
					propMap.put(propIdxMap.get(i), dArr[i]);
				}
			}
			di.addNode(nodeType,name,propMap);
		}
	}
	private Relationship getRelationshipBetween(Node n1, Node n2){
		for(Relationship rel :n1.getRelationships()){
			if(rel.getOtherNode(n1).equals(n2)) return rel;
		}
		return null;
	}
	public void userRelation(){
		GraphDatabaseService di = DatabaseInit.getInstance();
		Transaction txn = di.beginTx();
		Result r = di.execute("MATCH(n:USER) Return n");
		while(r.hasNext()){
			Node firstnode = (Node)r.next().get("n");
			Result r1 = di.execute("MATCH(n:USER) Where NOT (id(n)="+firstnode.getId()+") Return n");
			while(r1.hasNext()){
				Node secondnode = (Node)r1.next().get("n");
				if(getRelationshipBetween(firstnode,secondnode)!=null){
					continue;
				}
			//	System.out.println(firstnode.getProperty("Name")+"----"+secondnode.getProperty("Name"));
				String firstnodecity = (String) firstnode.getProperty("City");
				String secondnodecity = (String) secondnode.getProperty("City");
				int firstnodeage = Integer.parseInt((String) firstnode.getProperty("Age"));
				int secondnodeage =Integer.parseInt((String) secondnode.getProperty("Age"));
				String firstnodegender = (String) firstnode.getProperty("Gender");
				String secondnodegender = (String) secondnode.getProperty("Gender");
				int agediff = Math.abs(firstnodeage-secondnodeage);
				double score = 0;
				if(firstnodecity.equalsIgnoreCase(secondnodecity)){
					score+=0.3;
				}
				if(firstnodegender.equalsIgnoreCase(secondnodegender)){
					score+=0.2;
				}
				if(agediff >2 && agediff <= 5){
					score+=0.1;
				}else if(agediff<=2){
					score+=0.5;
				}
				if(score >=0.3){
					Relationship rel1 = firstnode.createRelationshipTo(secondnode, relLabels.valueOf("SIMILARTO"));
					rel1.setProperty("Score", score);
					Relationship rel2 = secondnode.createRelationshipTo(firstnode, relLabels.valueOf("SIMILARTO"));
					rel2.setProperty("Score", score);
				}
			}
		}

		txn.success();
		txn.close();
	}    
	public void prodRelation(){
		GraphDatabaseService di = DatabaseInit.getInstance();
		Transaction txn = di.beginTx();
		Result r = di.execute("MATCH(p:PRODUCT) Return p");
		while(r.hasNext()){
			Node firstnode = (Node)r.next().get("p");
			Result r1 = di.execute("MATCH(p:PRODUCT) Where NOT (id(p)="+firstnode.getId()+") Return p");
			while(r1.hasNext()){
				Node secondnode = (Node)r1.next().get("p");
				if(getRelationshipBetween(firstnode,secondnode)!=null){
					continue;
				}
				String firstnodesubcat1 = (String) firstnode.getProperty("SubCategory1");
				String secondnodesubcat1 = (String) secondnode.getProperty("SubCategory1");

				String firstnodesubcat2 = (String) firstnode.getProperty("SubCategory2");
				String secondnodesubcat2 = (String) secondnode.getProperty("SubCategory2");

				String firstnodesubcat3 = (String) firstnode.getProperty("SubCategory3");
				String secondnodesubcat3 = (String) secondnode.getProperty("SubCategory3");

				double score = 0;

				if(firstnodesubcat1.equalsIgnoreCase(secondnodesubcat1)){
					score+=0.1;
					if(firstnodesubcat2.equalsIgnoreCase(secondnodesubcat2)){
						score+=0.1;
						if(firstnodesubcat3.equalsIgnoreCase(secondnodesubcat3)){
							score+=0.1;
						}
					}
				}

				if(score >=0.2){

					HashMap<String,String> firstnodePropertyMap= (HashMap) firstnode.getAllProperties();
					HashMap<String,String> secondnodePropertyMap= (HashMap) secondnode.getAllProperties();

					Iterator<String> it = firstnodePropertyMap.keySet().iterator();

					while(it.hasNext()) {
						String key = it.next();

						if(key.equalsIgnoreCase("SubCategory1") || key.equalsIgnoreCase("SubCategory2") || key.equalsIgnoreCase("SubCategory3"))
						{
							continue;
						}
						if(secondnodePropertyMap.containsKey(key)){
							String valueNode1 = firstnodePropertyMap.get(key);
							String valueNode2 = secondnodePropertyMap.get(key);

							if(valueNode1.equalsIgnoreCase(valueNode2)){  
								score+=0.1;
							}
						}
					}

					if(score >=0.4){
						Relationship rel1 = firstnode.createRelationshipTo(secondnode, relLabels.valueOf("SIMILARTO"));
						rel1.setProperty("Score", score);
						Relationship rel2 = secondnode.createRelationshipTo(firstnode, relLabels.valueOf("SIMILARTO"));
						rel2.setProperty("Score", score);
					}
				}
			}
		}
		txn.success();
		txn.close();
	}

}

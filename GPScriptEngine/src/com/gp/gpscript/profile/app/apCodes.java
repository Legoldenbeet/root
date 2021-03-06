package com.gp.gpscript.profile.app;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gp.gpscript.profile.ProfileNode;
import com.gp.gpscript.profile.xPathNode;

public class apCodes extends ProfileNode {
	private Logger log = Logger.getLogger(apCodes.class);
	public apCode Code[];

	public apCodes(Node node) {
		super(node);
		String xpString = "code";
		try {
			Document document = node.getOwnerDocument();
			NodeList nl = xPathNode.getNodeList(xpString, node);
			Code = new apCode[nl.getLength()];
			for (int i = 0; i < nl.getLength(); i++) {
				Code[i] = new apCode(nl.item(i));
			}
		} catch (Exception e) {
			// e.printStackTrace();
			log.error(e.getMessage());
		}
	}
}
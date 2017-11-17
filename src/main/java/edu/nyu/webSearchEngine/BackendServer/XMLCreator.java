package edu.nyu.webSearchEngine.BackendServer;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;

public class XMLCreator {
    public String getDocumentString(Document document) {
        return document.asXML();
    }

    public Document createDocument(List<QueryResponse> responseList) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("root");


        for (QueryResponse response:responseList) {
//            Element element = root.addElement("Result")
//                    .addAttribute("docId", String.valueOf(response.docId))
//                    .addAttribute("bmValue", String.valueOf(response.bmValue))
//                    .addAttribute("freq", response.freq)
//                    .addAttribute("url", response.url)
//                    .addAttribute("snippets", StringUtils.join(response.snippets, " "));

            Element docId = root.addElement("docId")
                    .addText(String.valueOf(response.docId));
            Element bmValue = docId.addElement("bmValue")
                    .addText(String.valueOf(response.bmValue));
            Element freq = docId.addElement("freq")
                    .addText(String.valueOf(response.freq));
            Element url = docId.addElement("url")
                    .addText(String.valueOf(response.url));
            Element snippets = docId.addElement("snippets")
                    .addText(StringUtils.join(response.snippets, "\n"));

        }
        return document;
    }
}

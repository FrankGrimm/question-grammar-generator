/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grammar.read.questions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class SparqlQuery {
    //https://www.w3.org/TR/rdf-sparql-query/

    private static String endpoint = "https://dbpedia.org/sparql";
    private String objectOfProperty;
    public static String FIND_ANY_ANSWER = "FIND_ANY_ANSWER";
    public static String FIND_LABEL = "FIND_LABEL";
    public String sparqlQuery = null;
    public static String RETURN_TYPE_OBJECT = "objOfProp";
    public static String RETURN_TYPE_SUBJECT = "subjOfProp";
    private String resultSparql=null;

    public SparqlQuery(String entityUrl, String property, String type, String returnType) {
        if (type.contains(FIND_ANY_ANSWER)) {
            if (returnType.contains("objOfProp")) {
                sparqlQuery = this.setSparqlQueryPropertyObject(entityUrl, property);
            } else if (returnType.contains("subjOfProp")) {
                sparqlQuery = this.setSparqlQueryPropertyWithSubject(entityUrl, property);
            }

        } else if (type.contains(FIND_LABEL)) {
            sparqlQuery = this.setSparqlQueryForLabel(entityUrl);
        }
        this.resultSparql = executeSparqlQuery(sparqlQuery);
        parseResult(resultSparql);
    }

    public SparqlQuery(String sparqlQuery) {
        this.resultSparql = executeSparqlQuery(sparqlQuery);
        parseResult(resultSparql);
    }

    private String executeSparqlQuery(String query) {
        String result = null, resultUnicode = null, command = null;
        Process process = null;
        try {
            resultUnicode = this.stringToUrlUnicode(query);
            command = "curl " + endpoint + "?query=" + resultUnicode;
            process = Runtime.getRuntime().exec(command);
            //System.out.print(command);
        } catch (Exception ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in unicode in sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            result = builder.toString();
        } catch (IOException ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in reading sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    public void parseResult(String xmlStr) {
        Document doc = convertStringToXMLDocument(xmlStr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            this.parseResult(builder, xmlStr);
        } catch (Exception ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in parsing sparql in XML!" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Document convertStringToXMLDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseResult(DocumentBuilder builder, String xmlStr) {

        try {
            Document document = builder.parse(new InputSource(new StringReader(
                    xmlStr)));
            NodeList results = document.getElementsByTagName("results");
            for (int i = 0; i < results.getLength(); i++) {
                NodeList childList = results.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("result".equals(childNode.getNodeName())) {
                        //System.out.println("label!!!!!!!!!!!!!" + childList.item(j).getTextContent().trim());
                        this.objectOfProperty = childList.item(j).getTextContent().trim();
                    }
                }

            }
        } catch (SAXException ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("no result after sparql query!" + ex.getMessage());
            return;
        } catch (IOException ex) {
            Logger.getLogger(SparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("no result after sparql query!" + ex.getMessage());
            return;
        }

        //System.out.println("xmlStr!!!!!!!!!!!!!" + xmlStr);

        //System.out.println("xmlStr!!!!!!!!!!!!!" + xmlStr);
    }

    public String setSparqlQueryPropertyObject(String entityUrl, String property) {
        return "select  ?o\n"
                + "    {\n"
                + "    " + "<" + entityUrl + ">" + " " + "<" + property + ">" + "  " + "?o" + "\n"
                + "    }";

    }

    /*public  String setSparqlQueryPropertyWithSubject(String entityUrl, String property) {
        return "select  ?s\n"
                + "    {\n"
                 + "   " + "?s" + " " + "<" + property + ">" + "  " + "<" +  "http://www.w3.org/2001/XMLSchema#"+entityUrl + ">" + "\n"
                + "    }";

    }*/
    public String setSparqlQueryPropertyWithSubject(String entityUrl, String property) {
        String sparql = null;
        if (entityUrl.contains("http:")) {
            sparql = "select  ?s\n"
                    + "    {\n"
                    + "   " + "?s" + " " + "<" + property + ">" + "  " + "<" + entityUrl + ">" + "\n"
                    + "    }";
        } else {
            sparql = "select  ?s\n"
                    + "    {\n"
                    + "   " + "?s" + " " + "<" + property + ">" + "  " + entityUrl + "\n"
                    + "    }";
        }
        return sparql;

    }

    public static String setSparqlQueryPropertyWithSubjectFilter(String entityUrl, String property) {
        String sparql = null;
        if (entityUrl.contains("http:")) {
            sparql = "select  ?s\n"
                    + "    {\n"
                    + "   " + "?s" + " " + "<" + property + ">" + "  " + "<" + entityUrl + ">" + "\n"
                    + "    }";
        } else {
            sparql = "select  ?s\n"
                    + "    {\n"
                    + "   " + "?s" + " " + "<" + property + ">" + "  " + entityUrl + "\n"
                    + "    }";
        }
        return sparql;

    }

    public static String setSparqlQueryForLabel(String entityUrl) {
        String sparql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "   PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "   PREFIX dbpedia: <http://dbpedia.org/resource/>\n"
                + "\n"
                + "   SELECT DISTINCT ?label \n"
                + "   WHERE {  \n"
                + "       <" + entityUrl + "> rdfs:label ?label .     \n"
                + "       filter(langMatches(lang(?label),\"EN\"))         \n"
                + "   }";

        return sparql;

    }

    public static String setSparqlQueryForTypes(String propertyUrl, String objectUrl) {
        String sparql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "   PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "   PREFIX dbpedia: <http://dbpedia.org/resource/>\n"
                + "\n"
                + "   SELECT DISTINCT ?label \n"
                + "   WHERE {  \n"
                + "   " + "?label" + " " + "<" + propertyUrl + ">" + " " + "<" + objectUrl + ">" + " .     \n"
                + "       filter(langMatches(lang(?label),\"EN\"))         \n"
                + "   }";

        return sparql;

    }

    /*public static String setSparqlQueryForTypes(String classUrl) {
        String sparql = 
                  "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "   PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "   PREFIX res: <http://dbpedia.org/resource/>\n"
                + "\n"
                + "   SELECT DISTINCT ?label \n"
                + "   WHERE {  \n"
                + "       ?label"+" rdf:type "+" dbo:"+classUrl+" "+" .     \n"
                + "       filter(langMatches(lang(?label),\"EN\"))         \n"
                + "   }";

        return sparql;

    }*/
    public String stringToUrlUnicode(String string) throws UnsupportedEncodingException {
        String encodedString = URLEncoder.encode(string, "UTF-8");
        return encodedString;
    }

    public String getObject() {
        return this.objectOfProperty;
    }

    public String getSparqlQuery() {
        return sparqlQuery;
    }

    public String getResultSparql() {
        return resultSparql;
    }

    @Override
    public String toString() {
        return "SparqlQuery{" + "objectOfProperty=" + objectOfProperty + ", sparqlQuery=" + sparqlQuery + '}';
    }

    public SparqlQuery() {

    }

    public static void main(String[] args) {
        String objectUrl = "http://dbpedia.org/ontology/largestCity";
        String propertyUrl = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
        String subject = "http://dbpedia.org/resource/Province_of_Saxony";
        String object = "http://dbpedia.org/resource/Russia";

        SparqlQuery sparqlQuery = new SparqlQuery(subject, objectUrl, FIND_ANY_ANSWER, RETURN_TYPE_OBJECT);
        System.out.println(sparqlQuery.getSparqlQuery());
        
        String sparql=sparqlQuery.setSparqlQueryForLabel(object);
        //System.out.println(sparqlQuery.getResultSparql());
         System.out.println(sparql);

        /*String entitieSparql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
               + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
               + "SELECT ?subject ?label\n"
               + "WHERE {\n"
               + "    ?subject rdf:type <http://dbpedia.org/ontology/Country> .\n"
               + "    ?subject rdfs:label ?label .\n"
               + "       filter(langMatches(lang(?label),\"EN\"))         \n"
               + "} LIMIT 20000";*/
        ///<http://dbpedia.org/resource/Algeria> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Country> .
        /*SparqlQuery sparql=new SparqlQuery();
       String sparqlStr=SparqlQuery.setSparqlQueryPropertyWithSubjectFilter(objectUrl, propertyUrl);
       String resultSparql = sparql.executeSparqlQuery(entitieSparql);
       System.out.println("sparql:"+resultSparql);
       System.out.println("sparql:"+sparql.getObject());
         */
    }

}

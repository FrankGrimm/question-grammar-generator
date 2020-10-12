/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grammar.question.answer.gui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GrammarEntryUnit {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("language")
    private String language;
    @JsonProperty("type")
    private String type;
    @JsonProperty("bindingType")
    private String bindingType;
    @JsonProperty("returnType")
    private String returnType;
    @JsonProperty("frameType")
    private String frameType;
    @JsonProperty("sentences")
    private List<String> sentences = new ArrayList<String>();
    @JsonProperty("queryType")
    private String queryType;
    @JsonProperty("sparqlQuery")
    private String sparqlQuery;
    @JsonIgnore
    private SentenceToSparqlParameterMapping sentenceToSparqlParameterMapping = null;
    @JsonProperty("returnVariable")
    private String returnVariable;
    @JsonProperty("sentenceBindings")
    private SentenceBindings sentenceBindings;
    @JsonProperty("combination")
    private Boolean combination;

    public GrammarEntryUnit() {
    }

    public Integer getId() {
        return id;
    }

    public String getLanguage() {
        return language;
    }

    public String getType() {
        return type;
    }

    public String getBindingType() {
        return bindingType;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getFrameType() {
        return frameType;
    }

    public List<String> getSentences() {
        return sentences;
    }

    public String getQueryType() {
        return queryType;
    }

    public String getSparqlQuery() {
        return sparqlQuery;
    }

    public SentenceToSparqlParameterMapping getSentenceToSparqlParameterMapping() {
        return sentenceToSparqlParameterMapping;
    }

    public String getReturnVariable() {
        return returnVariable;
    }

    public SentenceBindings getSentenceBindings() {
        return sentenceBindings;
    }

    public Boolean getCombination() {
        return combination;
    }

}
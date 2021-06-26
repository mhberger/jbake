package org.jbake.domain

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.stream.Stream

public class DocumentTest {
    private static Stream<Arguments> patternAndDatesData() {

        return Stream.of(
            Arguments.of("yyyy-MM-dd",       "2020-02-29"),
            Arguments.of("yyyy-MM-dd hh:mm", "2020-02-29 14:30"),
        );
    }

    @ParameterizedTest
    @MethodSource("patternAndDatesData")
    void checkDatesAndPatternsFromContentAreProcessed(String pattern, String dateString) throws ParseException {

        // from content to DocumentModel
        DateFormat df = new SimpleDateFormat(pattern);
        Date date = df.parse(dateString);

        // from DocumentModel to jsonString
        JsonBuilder json = new JsonBuilder();
        json("k": date);
        def result = new JsonSlurper().parseText(JsonOutput.toJson(json.content))

        // from jsonString back to DocumentModel
        Assertions.assertEquals(date.toString(), Document.convertJsonDateToJavaUtilDate(result.get("k")).toString())
    }
}

package org.jbake.domain

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import spock.lang.Specification
import spock.lang.Unroll

import java.text.DateFormat
import java.text.SimpleDateFormat

class DocumentSpec extends Specification {

    @Unroll
    def "check that #a == #b"() {
        expect:
        (a == b) == result

        where:
        a    | b    || result
        1    | 1    || true
        1    | 2    || false
        -1   | -1   || true
        2    | 1    || false
        null | null || true
    }

    @Unroll
    def "check date String and pattern -> DocumentModel -> Json -> DocumentModel for #pattern and #dateString"() {
        when:
            // from content to DocumentModel
            DateFormat df = new SimpleDateFormat(pattern);
            Date date = df.parse(dateString);

            // from DocumentModel to jsonString
            def json = new JsonBuilder()
            json("k": date)
            def result = new JsonSlurper().parseText(JsonOutput.toJson(json.content))

        then:

        // from jsonString back to DocumentModel
        date.toString() == Document.convertJsonDateToJavaUtilDate(result.get("k")).toString()

        where:
        pattern            | dateString
        'yyyy-MM-dd'       | '2020-02-29'
        'yyyy-MM-dd hh:mm' | '2020-02-29 14:30'
    }

}

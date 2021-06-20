package org.jbake.domain

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.Canonical
import org.jbake.app.configuration.JBakeConfiguration
import org.jbake.model.DocumentModel
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Canonical
class Document {

    private static final Logger LOGGER = LoggerFactory.getLogger(Document.class);

    Long id
    String name
    String title
    String status
    String type
    String root_path
    String file
    String uri
    String uri_no_extension
    String source_uri
    String document_date
    String sha1
    Boolean rendered
    Boolean cached
    String tag_string
    String body
    String json_data

    List<String> tags() {
      List<String> tags = tag_string.split(/,/)
      return tags
    }

    Date documentDate(JBakeConfiguration config) {
        Date date = Date.parse(config.getDateFormat(), document_date)
        LOGGER.info("MHB documentDate document_date {}, date parsed {}", document_date, date);
        date
    }

    static String formatDate(Date d, JBakeConfiguration config) {
        // Review whether we should be using this?
        // configuration.getDateFormat()
        String formattedDate = new SimpleDateFormat(config.getDateFormat()).format(d)
        LOGGER.info("MHB formatDate date value {}, date formatted {}", d, formattedDate);
        formattedDate
    }

    // Convert to ModelClass
    DocumentModel toDocumentModel() {
        DocumentModel d = new DocumentModel()

        // Document Model is a HashMap<String, Object> so we can
        // just extract from json_data
        def slurper = new JsonSlurper()
        def result = slurper.parseText(json_data)
        result.each {k, v ->
            if (k == "date") {
                Date documentDate = convertJsonDateToJavaUtilDate(v)
                d.setDate(documentDate)

            }
            else {
                d[k] = v
            }
        }
        d
    }

    static Date convertJsonDateToJavaUtilDate(String v) {
        LocalDateTime parsedDate = LocalDateTime.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+0000"));
        Date documentDate = Date.from(parsedDate.atZone(TimeZone.getTimeZone("Universal").toZoneId()).toInstant())
        LOGGER.info("MHB toDocumentModel date value {}, date parsed {}, documentDate {}", v, parsedDate, documentDate);
        documentDate
    }

    // Convert from ModelClass
    static Document fromDocumentModel(DocumentModel documentModel, JBakeConfiguration config) {
        Document document = new Document()

        // Keep them as separate fields so that we can query them via SQL
        document.name               =  documentModel.name
        document.title              =  documentModel.title
        document.status             =  documentModel.status
        document.type               =  documentModel.type
        document.root_path          =  documentModel.rootpath
        document.file               =  documentModel.file
        document.uri                =  documentModel.uri
        document.uri_no_extension   =  documentModel.noExtensionUri
        document.source_uri         =  documentModel.sourceuri
        document.document_date      =  formatDate(documentModel.getDate(), config)
        document.sha1               =  documentModel.sha1
        document.rendered           =  documentModel.rendered
        document.cached             =  documentModel.cached
        document.tag_string         =  documentModel.getTags().join(/,/).toString()+","
        document.body               =  documentModel.body

        // Serialise the entire documentModel as a JSON string.
        // This lets us catch any additional fields used in DocumentModel.
        document.json_data          = new JsonBuilder(documentModel).toString()

        document
    }
}

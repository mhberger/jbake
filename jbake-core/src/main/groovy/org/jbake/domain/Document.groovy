package org.jbake.domain

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.Canonical
import org.jbake.model.DocumentModel

import java.text.SimpleDateFormat

@Canonical
class Document {
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

    Date documentDate() {
        // Review whether we should be using this?
        // configuration.getDateFormat()
        Date.parse('yyyy-MM-dd', document_date)
    }

    static String formatDate(Date d) {
        // Review whether we should be using this?
        // configuration.getDateFormat()
        new SimpleDateFormat('yyyy-MM-dd').format(d)
    }

    // Convert to ModelClass
    DocumentModel toDocumentModel() {
        DocumentModel d = new DocumentModel()

        // Document Model is a HashMap<String, Object> so we can
        // just extract from json_datah
        def slurper = new JsonSlurper()
        def result = slurper.parseText(json_data)
        result.each {k, v ->
            if (k == "date") {
                // Review whether we should be using this?
                // configuration.getDateFormat()
                d.setDate(Date.parse('yyyy-MM-dd', v))
            }
            else {
                d[k] = v
            }
        }
        d
    }

    // Convert from ModelClass
    static Document fromDocumentModel(DocumentModel documentModel) {
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
        document.document_date      =  formatDate(documentModel.getDate())
        document.sha1               =  documentModel.sha1
        document.rendered           =  documentModel.rendered
        document.cached             =  documentModel.cached
        document.tag_string         =  documentModel.getTags().join(/,/).toString()+","
        document.body               =  documentModel.body

        // Serialise the entire documentModel as a JSON string.
        // This lets us catch any additiional fields used in DocumentModel.
        document.json_data          = new JsonBuilder(documentModel).toString()

        document
    }
}

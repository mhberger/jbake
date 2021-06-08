package org.jbake.domain


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
    String uri_no_extensions
    String source_uri
    String document_date
    String sha1
    Boolean rendered
    Boolean cached
    String tag_string
    String body

    List<String> tags() {
      List<String> tags = tag_string.split(/,/)
      return tags
    }

    Date documentDate() {
       Date.parse('yyyy-MM-dd', document_date)
    }

    static String formatDate(Date d) {
        new SimpleDateFormat('yyyy-MM-dd').format(d)
    }

    // Convert to ModelClass
    DocumentModel toDocumentModel() {
        DocumentModel d = new DocumentModel()
        d.name = name
        d.title = title
        d.status = status
        d.type = type
        d.rootPath = root_path
        d.file = file
        d.uri = uri
        d.noExtensionsUri = uri_no_extensions
        d.sourceUrl = source_uri
        d.date = documentDate()
        d.sha1 = sha1
        d.rendered = rendered
        d.cached = cached
        d.tags = tags()
        d.body = body

        d
    }

    // Convert from ModelClass
    static Document fromDocumentModel(DocumentModel documentModel) {
        Document document = new Document()

        document.name               =  documentModel.name
        document.title              =  documentModel.title
        document.status             =  documentModel.status
        document.type               =  documentModel.type
        document.root_path          =  documentModel.rootPath
        document.file               =  documentModel.file
        document.uri                =  documentModel.uri
        document.uri_no_extensions  =  documentModel.noExtensionsUri
        document.source_uri         =  documentModel.sourceUrl
        document.document_date      =  formatDate(documentModel.getDate())
        document.sha1               =  documentModel.sha1
        document.rendered           =  documentModel.rendered
        document.cached             =  documentModel.cached
        document.tag_string         =  documentModel.getTags().join(/,/).toString()
        document.body               =  documentModel.body

        document
    }

}

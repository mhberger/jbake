package org.jbake.domain


import groovy.transform.Canonical
import org.jbake.model.DocumentModel

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

}

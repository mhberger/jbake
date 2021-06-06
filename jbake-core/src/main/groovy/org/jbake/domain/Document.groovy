package org.jbake.domain


import groovy.transform.Canonical

@Canonical
class Document {
    String uri
    String name
    String status
    String type
    String source_uri
    Date document_date
    String sha1
    Boolean rendered
    Boolean cached
    Set<String> tags
    String body

    // Convert to ModelClass

    // Convert from ModelClass

}

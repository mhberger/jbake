package org.jbake.domain


import groovy.transform.Canonical

@Canonical
class Document {
    Long id
    String uri
    String name
    String status
    String type
    String source_uri
    String document_date
    String sha1
    Boolean rendered
    Boolean cached
    String tag_string
    String body

    Set<String> tags() {
      tags = tag_string.split(/,/).sort()
      return tags as Set<String>
    }
    // Convert to ModelClass

    // Convert from ModelClass

}

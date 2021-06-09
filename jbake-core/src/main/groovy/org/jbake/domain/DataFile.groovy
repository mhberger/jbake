package org.jbake.domain

import groovy.transform.Canonical
import org.jbake.model.DocumentModel

@Canonical
class DataFile {
    Long id
    String sha1
    Boolean rendered
    String file
    String source_uri
    String type
    String data

    // Convert to ModelClass
    DocumentModel toDocumentModel() {
        DocumentModel d = new DocumentModel()
        d.sha1 = sha1
        d.rendered = rendered
        d.file = file
        d.sourceUrl = source_uri
        d.type = type
        d.data = data

        d
    }

    // Convert from ModelClass
    static DataFile fromDocumentModel(DocumentModel documentModel) {
        DataFile dataFile = new DataFile()

        dataFile.sha1               =  documentModel.sha1
        dataFile.rendered           =  documentModel.rendered
        dataFile.file               =  documentModel.file
        dataFile.source_uri         =  documentModel.sourceuri
        dataFile.type               =  documentModel.type
        dataFile.data               =  documentModel.data

        dataFile
    }
}

package org.jbake.domain

import groovy.transform.Canonical

@Canonical
class Signature {
    String key
    String sha1
}

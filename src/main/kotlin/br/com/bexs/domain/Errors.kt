package br.com.bexs.domain

import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
data class RestErrorInfo(val detail: Exception, val message: String)
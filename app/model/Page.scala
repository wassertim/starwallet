package model

import org.jsoup.nodes.Document
import com.ning.http.client.Cookie

case class Page(document: Document, cookies: Seq[Cookie])

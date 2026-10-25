package com.gamaflix.tv

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class EpgEntry(val channel: String, val title: String, val start: Long, val stop: Long,
                    val description: String, val category: String, val year: String,
                    val rating: String, val subtitle: String)

private class EpgDb(ctx: Context): SQLiteOpenHelper(ctx.applicationContext,"gamaflix_epg.db",null,1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE epg (id INTEGER PRIMARY KEY,channel TEXT,title TEXT,start INTEGER,stop INTEGER,description TEXT,category TEXT,year TEXT,rating TEXT,subtitle TEXT)")
        db.execSQL("CREATE INDEX epg_channel_start ON epg(channel,start)")
    }
    override fun onUpgrade(db:SQLiteDatabase,oldVersion:Int,newVersion:Int) = Unit
}
object EPGRepository {
    fun parseXmltvDate(value: String): Long {
        val m = Regex("^(\\d{14})(?:\\s*([+-]\\d{4}))?").find(value.trim()) ?: return 0
        return try {
            val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
            val tz = m.groupValues[2].ifBlank { "+0000" }
            sdf.timeZone = TimeZone.getTimeZone("GMT$tz")
            sdf.isLenient = false
            sdf.parse(m.groupValues[1])?.time ?: 0L
        } catch (_: Exception) { 0L }
    }
    suspend fun importUrl(ctx: Context, address: String, progress: suspend (Int)->Unit): Int = withContext(Dispatchers.IO) {
        require(address.startsWith("http://",true) || address.startsWith("https://",true)) { "Informe um endereço HTTP ou HTTPS." }
        val helper = EpgDb(ctx)
        val connection = URL(address).openConnection() as HttpURLConnection
        connection.connectTimeout=20000;connection.readTimeout=90000
        connection.setRequestProperty("Accept-Encoding","gzip, identity")
        var number=0
        try {
            if(connection.responseCode !in 200..299) error("HTTP ${connection.responseCode}")
            val db=helper.writableDatabase
            connection.inputStream.use { raw ->
                val source: InputStream = if (connection.contentEncoding.equals("gzip",true)) java.util.zip.GZIPInputStream(raw) else raw
                source.use { stream ->
                    val parser=Xml.newPullParser();parser.setInput(stream,"UTF-8")
                    db.beginTransaction()
                    try {
                        db.execSQL("DELETE FROM epg")
                        val stmt=db.compileStatement("INSERT INTO epg(channel,title,start,stop,description,category,year,rating,subtitle) VALUES(?,?,?,?,?,?,?,?,?)")
                        var channel="";var begin=0L;var end=0L
                        var title="";var desc="";var category="";var year="";var rating="";var subtitle=""
                        var inside=false
                        var event=parser.eventType
                        while(event != XmlPullParser.END_DOCUMENT) {
                            if(event==XmlPullParser.START_TAG) {
                                val name=parser.name
                                if(name=="programme") {
                                    inside=true;channel=parser.getAttributeValue(null,"channel").orEmpty()
                                    begin=parseXmltvDate(parser.getAttributeValue(null,"start").orEmpty())
                                    end=parseXmltvDate(parser.getAttributeValue(null,"stop").orEmpty())
                                    title="";desc="";category="";year="";rating="";subtitle=""
                                } else if(inside) {
                                    when(name) {
                                        "title"-> title=parser.nextText().take(400)
                                        "sub-title"-> subtitle=parser.nextText().take(300)
                                        "desc"->desc=parser.nextText().take(2500)
                                        "category"-> category=parser.nextText().take(160)
                                        "date"->year=parser.nextText().take(30)
                                        "value"-> if(rating.isEmpty())rating=parser.nextText().take(60)
                                    }
                                }
                            } else if(event==XmlPullParser.END_TAG && parser.name=="programme" && inside) {
                                inside=false
                                if(channel.isNotBlank() && begin>0L && end>begin) {
                                    stmt.clearBindings();stmt.bindString(1,channel);stmt.bindString(2,title)
                                    stmt.bindLong(3,begin);stmt.bindLong(4,end)
                                    stmt.bindString(5,desc);stmt.bindString(6,category);stmt.bindString(7,year)
                                    stmt.bindString(8,rating);stmt.bindString(9,subtitle)
                                    stmt.executeInsert();number++
                                    if(number%3000==0)progress(number)
                                }
                            }
                            event=parser.next()
                        }
                        stmt.close();db.setTransactionSuccessful()
                    } finally {db.endTransaction()}
                }
            }
            progress(number)
            number
        } finally {connection.disconnect();helper.close()}
    }
    fun schedule(ctx: Context, channelId: String, from: Long = System.currentTimeMillis(), limit: Int=25):List<EpgEntry> {
        if(channelId.isBlank()) return emptyList()
        val db=EpgDb(ctx)
        return try {
            val result=mutableListOf<EpgEntry>()
            db.readableDatabase.rawQuery("SELECT channel,title,start,stop,description,category,year,rating,subtitle FROM epg WHERE channel=? AND stop>? ORDER BY start LIMIT ?", arrayOf(channelId,from.toString(),limit.toString())).use { c ->
                while(c.moveToNext())result.add(EpgEntry(c.getString(0).orEmpty(),c.getString(1).orEmpty(),c.getLong(2),c.getLong(3),c.getString(4).orEmpty(),c.getString(5).orEmpty(),c.getString(6).orEmpty(),c.getString(7).orEmpty(),c.getString(8).orEmpty()))
            }
            result
        } finally {db.close()}
    }
}

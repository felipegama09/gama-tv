package com.gamaflix.tv

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/** IDs live in SQLite, not in a massive in-memory playlist. */
data class IPTVItem(
    val name: String, val url: String, val logo: String = "", val group: String = "",
    val tvgId: String = "", val type: IPTVType = IPTVType.LIVE, val id: Long = 0
)
enum class IPTVType { LIVE, MOVIE, SERIES }

private class CatalogDb(context: Context) : SQLiteOpenHelper(context.applicationContext, "gamaflix_catalog.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS media (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, url TEXT NOT NULL, logo TEXT, grp TEXT, tvgid TEXT, type TEXT NOT NULL)")
        db.execSQL("CREATE INDEX IF NOT EXISTS media_type_id ON media(type,id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS media_url ON media(url)")
        db.execSQL("CREATE INDEX IF NOT EXISTS media_group_type_id ON media(type,grp,id)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
}

object IPTVRepository {
    private const val PAGE_SIZE = 75
    // These are limits for malformed single lines, NOT limits on the total playlist size.
    private const val MAX_LINE_LENGTH = 65536
    private val attributes = Regex("([a-zA-Z0-9_-]+)=\"([^\"]*)\"")

    private fun attr(meta: String, key: String): String = attributes.findAll(meta)
        .firstOrNull { it.groupValues[1].equals(key, ignoreCase = true) }?.groupValues?.get(2).orEmpty()

    private fun category(group: String, link: String): IPTVType {
        val g = group.lowercase()
        val u = link.lowercase()
        return when {
            "séries" in g || "series" in g || "/series/" in u -> IPTVType.SERIES
            "filme" in g || "movie" in g || "vod" in g || "/movie/" in u -> IPTVType.MOVIE
            else -> IPTVType.LIVE
        }
    }

    /** Streams directly to SQLite: no artificial 10,000-item cutoff or readText().
     * One transaction keeps the previous catalog if importing a new list fails.
     */
    suspend fun importUrl(context: Context, address: String, progress: suspend (Int) -> Unit): Int = withContext(Dispatchers.IO) {
        require(address.startsWith("https://", true) || address.startsWith("http://", true)) {
            "Informe uma URL HTTP ou HTTPS válida."
        }
        val helper = CatalogDb(context)
        val conn = URL(address).openConnection() as HttpURLConnection
        conn.connectTimeout = 20000
        conn.readTimeout = 60000
        conn.instanceFollowRedirects = true
        conn.setRequestProperty("Accept-Encoding", "identity")
        conn.setRequestProperty("User-Agent", "GamaFlix/1.4 AndroidTV")
        var count = 0
        try {
            val code = conn.responseCode
            if (code !in 200..299) throw IllegalStateException("Servidor respondeu HTTP $code")
            val db = helper.writableDatabase
            conn.inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
                db.beginTransaction()
                try {
                    db.execSQL("DELETE FROM media")
                    val statement = db.compileStatement("INSERT INTO media(name,url,logo,grp,tvgid,type) VALUES(?,?,?,?,?,?)")
                    var meta: String? = null
                    var groupHint = ""
                    while (true) {
                        val line = reader.readLine() ?: break
                        if (line.length > MAX_LINE_LENGTH) { meta = null; continue }
                        val trimmed = line.trim()
                        when {
                            trimmed.startsWith("#EXTINF:", true) -> meta = trimmed
                            trimmed.startsWith("#EXTGRP:", true) -> groupHint = trimmed.substringAfter(':').trim()
                            trimmed.startsWith("#") || trimmed.isEmpty() -> Unit
                            meta != null -> {
                                val m = meta!!
                                meta = null
                                val grp = attr(m, "group-title").ifBlank { groupHint }
                                groupHint = ""
                                if (!trimmed.startsWith("https://", true) && !trimmed.startsWith("http://", true)) continue
                                val name = m.substringAfter(',', "Sem nome").trim().ifBlank { "Sem nome" }
                                statement.clearBindings()
                                statement.bindString(1, name)
                                statement.bindString(2, trimmed)
                                statement.bindString(3, attr(m, "tvg-logo"))
                                statement.bindString(4, grp)
                                statement.bindString(5, attr(m, "tvg-id"))
                                statement.bindString(6, category(grp, trimmed).name)
                                statement.executeInsert()
                                count++
                                if (count % 2000 == 0) progress(count)
                            }
                        }
                    }
                    statement.close()
                    db.setTransactionSuccessful()
                } finally { db.endTransaction() }
            }
            progress(count)
            count
        } finally {
            conn.disconnect()
            helper.close()
        }
    }

    fun count(context: Context, type: IPTVType? = null): Int {
        val helper = CatalogDb(context)
        return try {
            val filter = if (type == null) "" else " WHERE type=?"
            helper.readableDatabase.rawQuery("SELECT COUNT(*) FROM media$filter", if (type == null) null else arrayOf(type.name)).use {
                if (it.moveToFirst()) it.getInt(0) else 0
            }
        } finally { helper.close() }
    }

    fun page(context: Context, type: IPTVType?, afterId: Long = 0L, limit: Int = PAGE_SIZE): List<IPTVItem> {
        val helper = CatalogDb(context)
        return try {
            val sql = if (type == null)
                "SELECT id,name,url,logo,grp,tvgid,type FROM media WHERE id>? ORDER BY id LIMIT ?"
            else "SELECT id,name,url,logo,grp,tvgid,type FROM media WHERE type=? AND id>? ORDER BY id LIMIT ?"
            val args = if (type == null) arrayOf(afterId.toString(), limit.toString())
                else arrayOf(type.name, afterId.toString(), limit.toString())
            val result = ArrayList<IPTVItem>(limit)
            helper.readableDatabase.rawQuery(sql, args).use { c ->
                while (c.moveToNext()) result.add(
                    IPTVItem(c.getString(1), c.getString(2), c.getString(3).orEmpty(),
                        c.getString(4).orEmpty(), c.getString(5).orEmpty(),
                        IPTVType.valueOf(c.getString(6)), c.getLong(0))
                )
            }
            result
        } finally { helper.close() }
    }

    fun findUrl(context: Context, url: String): IPTVItem? {
        val helper = CatalogDb(context)
        return try {
            helper.readableDatabase.rawQuery("SELECT id,name,url,logo,grp,tvgid,type FROM media WHERE url=? LIMIT 1", arrayOf(url)).use { c ->
                if (c.moveToFirst()) IPTVItem(c.getString(1), c.getString(2), c.getString(3).orEmpty(),
                    c.getString(4).orEmpty(), c.getString(5).orEmpty(), IPTVType.valueOf(c.getString(6)), c.getLong(0)) else null
            }
        } finally { helper.close() }
    }

    /** Categories are queried in SQLite; the 392K+ item catalog never enters Compose state. */
    fun groups(context: Context, type: IPTVType): List<Pair<String, Int>> {
        val helper = CatalogDb(context)
        return try {
            val result = mutableListOf<Pair<String, Int>>()
            helper.readableDatabase.rawQuery(
                "SELECT CASE WHEN TRIM(COALESCE(grp,''))='' THEN 'Sem categoria' ELSE grp END AS category, COUNT(*) AS n FROM media WHERE type=? GROUP BY category ORDER BY category COLLATE NOCASE",
                arrayOf(type.name)
            ).use { c -> while (c.moveToNext()) result.add(c.getString(0) to c.getInt(1)) }
            result
        } finally { helper.close() }
    }

    fun pageGroup(context: Context, type: IPTVType, group: String, afterId: Long = 0L, limit: Int = 60): List<IPTVItem> {
        val helper = CatalogDb(context)
        return try {
            val statement = "SELECT id,name,url,logo,grp,tvgid,type FROM media WHERE type=? AND " +
                    (if (group == "Sem categoria") "TRIM(COALESCE(grp,''))=''" else "grp=?") +
                    " AND id>? ORDER BY id LIMIT ?"
            val args = if (group == "Sem categoria") arrayOf(type.name,afterId.toString(),limit.toString())
                else arrayOf(type.name,group,afterId.toString(),limit.toString())
            val out = ArrayList<IPTVItem>(limit)
            helper.readableDatabase.rawQuery(statement,args).use { c ->
                while (c.moveToNext()) out.add(IPTVItem(c.getString(1),c.getString(2),c.getString(3).orEmpty(),c.getString(4).orEmpty(),c.getString(5).orEmpty(),IPTVType.valueOf(c.getString(6)),c.getLong(0)))
            }
            out
        } finally { helper.close() }
    }

}

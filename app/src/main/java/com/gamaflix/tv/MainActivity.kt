package com.gamaflix.tv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ink = Color(0xFF000000)
private val panel = Color(0xFF141414)
private val red = Color(0xFFE50914)
private val muted = Color(0xFFB6B6B6)
private val white = Color.White
private val nav = listOf("Início", "TV", "Filmes", "Séries", "Categorias", "EPG", "Recentes", "Minha Lista", "Configurações")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = 5894 or 1024 or 512
        setContent { MaterialTheme { GamaRoot() } }
    }
}

@Composable
private fun GamaRoot() {
    var intro by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(2500); intro = false }
    if (intro) {
        val wordAlpha = remember { Animatable(0f) }
        val wordScale = remember { Animatable(.82f) }
        val gAlpha = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            launch { wordScale.animateTo(1f, tween(750)) }
            wordAlpha.animateTo(1f, tween(600))
            delay(500)
            wordAlpha.animateTo(0f, tween(500))
            gAlpha.animateTo(1f, tween(650))
        }
        Box(Modifier.fillMaxSize().background(ink), contentAlignment = Alignment.Center) {
            Image(painterResource(R.drawable.gama_word), "GAMA", Modifier.fillMaxWidth(.75f).height(250.dp).alpha(wordAlpha.value).scale(wordScale.value), contentScale = ContentScale.Fit)
            Image(painterResource(R.drawable.gama_mark), "Símbolo G", Modifier.fillMaxWidth(.27f).height(280.dp).alpha(gAlpha.value), contentScale = ContentScale.Fit)
        }
    } else GamaApp()
}

@Composable
private fun FocusAction(text: String, selected: Boolean = false, modifier: Modifier = Modifier, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val zoom by animateFloatAsState(if (focused) 1.065f else 1f, label = "focusZoom")
    val bg by animateColorAsState(if (focused) Color(0xFF3E0A0D) else if (selected) Color(0xFF242424) else Color(0xFF121212), label = "focusBg")
    Box(
        modifier.padding(3.dp).scale(zoom)
            .onFocusChanged { focused = it.isFocused }
            .border(if (focused || selected) 1.5.dp else .5.dp, if (focused || selected) red else Color(0xFF292929), RoundedCornerShape(8.dp))
            .background(bg, RoundedCornerShape(8.dp)).clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 13.sp, color = white, fontWeight = if (focused || selected) FontWeight.Bold else FontWeight.Medium, maxLines = 1)
    }
}

@Composable
private fun GamaHeader(active: String, onSwitch: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().height(74.dp).background(ink).padding(horizontal = 22.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painterResource(R.drawable.gama_logo), "GAMA", Modifier.width(128.dp).height(62.dp), contentScale = ContentScale.Fit)
        Spacer(Modifier.width(16.dp))
        LazyRow(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            items(nav) { item -> FocusAction(item, selected = item == active) { onSwitch(item) } }
        }
        Text("GAMA TV", color = muted, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun GamaApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("gamaflix", 0) }
    val scope = rememberCoroutineScope()
    var tab by remember { mutableStateOf("Início") }
    var mediaType by remember { mutableStateOf(IPTVType.LIVE) }
    var listUrl by remember { mutableStateOf(prefs.getString("playlist", DEFAULT_PLAYLIST_URL) ?: DEFAULT_PLAYLIST_URL) }
    var epgUrl by remember { mutableStateOf(prefs.getString("epg", DEFAULT_EPG_URL) ?: DEFAULT_EPG_URL) }
    var importing by remember { mutableStateOf(false) }
    var importingEpg by remember { mutableStateOf(false) }
    var importedCount by remember { mutableIntStateOf(0) }
    var epgCount by remember { mutableIntStateOf(0) }
    var total by remember { mutableIntStateOf(0) }
    var status by remember { mutableStateOf("") }
    var revision by remember { mutableIntStateOf(0) }
    var groups by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
    var chosenGroup by remember { mutableStateOf<String?>(null) }
    var entries by remember { mutableStateOf<List<IPTVItem>>(emptyList()) }
    var loadingMore by remember { mutableStateOf(false) }
    var more by remember { mutableStateOf(false) }
    var detail by remember { mutableStateOf<IPTVItem?>(null) }
    var playing by remember { mutableStateOf<IPTVItem?>(null) }
    var recent by remember { mutableStateOf(prefs.getString("recent", "").orEmpty()) }
    var favorites by remember { mutableStateOf(prefs.getString("favorites", "").orEmpty()) }
    var epgSchedule by remember { mutableStateOf<List<EpgEntry>>(emptyList()) }

    val currentType = when (tab) {
        "TV", "EPG" -> IPTVType.LIVE
        "Filmes" -> IPTVType.MOVIE
        "Séries" -> IPTVType.SERIES
        else -> mediaType
    }

    LaunchedEffect(Unit) { total = withContext(Dispatchers.IO) { IPTVRepository.count(context) } }
    LaunchedEffect(tab, currentType, revision) {
        chosenGroup = null
        if (tab in listOf("TV", "Filmes", "Séries", "Categorias", "EPG")) {
            groups = withContext(Dispatchers.IO) { IPTVRepository.groups(context, currentType) }
        } else groups = emptyList()
    }
    LaunchedEffect(tab, currentType, chosenGroup, revision, recent, favorites) {
        entries = emptyList(); more = false
        if (chosenGroup != null) {
            entries = withContext(Dispatchers.IO) { IPTVRepository.pageGroup(context, currentType, chosenGroup!!) }
            more = entries.size == 60
        } else if (tab == "Recentes" || tab == "Minha Lista") {
            val links = (if (tab == "Recentes") recent else favorites).lines().filter { it.isNotBlank() }.take(50)
            entries = withContext(Dispatchers.IO) { links.mapNotNull { IPTVRepository.findUrl(context, it) } }
        }
    }
    fun showDetail(item: IPTVItem) {
        detail = item
        epgSchedule = emptyList()
        if (item.type == IPTVType.LIVE) {
            scope.launch { epgSchedule = withContext(Dispatchers.IO) { EPGRepository.schedule(context, item.tvgId) } }
        }
    }
    fun play(item: IPTVItem) {
        recent = (listOf(item.url) + recent.lines().filter { it.isNotBlank() && it != item.url }).take(50).joinToString("\n")
        prefs.edit().putString("recent", recent).apply()
        playing = item
    }
    fun nextPage() {
        val category = chosenGroup ?: return
        if (loadingMore || !more) return
        loadingMore = true
        val cursor = entries.lastOrNull()?.id ?: 0L
        val typ = currentType
        scope.launch {
            try {
                val next = withContext(Dispatchers.IO) { IPTVRepository.pageGroup(context, typ, category, cursor) }
                if (chosenGroup == category) { entries = entries + next; more = next.size == 60 }
            } catch (e: Exception) { status = "Falha ao carregar: ${e.message}" }
            finally { loadingMore = false }
        }
    }
    fun importPlaylist() {
        if (importing) return
        importing = true; importedCount = 0; status = "Importando catálogo..."
        scope.launch {
            try {
                val result = IPTVRepository.importUrl(context, listUrl.trim()) { importedCount = it }
                prefs.edit().putString("playlist", listUrl.trim()).apply()
                total = result; revision++
                status = "$result conteúdos importados com sucesso."
            } catch (e: Exception) { status = "Falha na lista: ${e.message}" }
            finally { importing = false }
        }
    }
    fun importGuide() {
        if (importingEpg) return
        importingEpg = true; epgCount = 0; status = "Importando EPG..."
        scope.launch {
            try {
                val result = EPGRepository.importUrl(context, epgUrl.trim()) { epgCount = it }
                prefs.edit().putString("epg", epgUrl.trim()).apply()
                status = "$result programas importados para o EPG."
            } catch (e: Exception) { status = "Falha no EPG: ${e.message}" }
            finally { importingEpg = false }
        }
    }
    BackHandler(playing != null || detail != null || chosenGroup != null) {
        when { playing != null -> playing = null; detail != null -> detail = null; else -> chosenGroup = null }
    }
    if (playing != null) { GamaPlayer(playing!!) { playing = null }; return }

    Column(Modifier.fillMaxSize().background(ink)) {
        GamaHeader(tab) { tab = it; detail = null; chosenGroup = null }
        HorizontalDivider(color = Color(0xFF222222), thickness = 1.dp)
        when {
            detail != null -> {
                val item = detail!!
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(25.dp)) {
                    FocusAction("← Voltar") { detail = null }
                    Spacer(Modifier.height(18.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MediaArtwork(item, Modifier.size(width = 220.dp, height = 128.dp))
                        Spacer(Modifier.width(24.dp))
                        Column {
                            Text(item.name, color = white, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Text(item.group.ifBlank { "Sem categoria" }, color = muted, fontSize = 15.sp)
                            Spacer(Modifier.height(10.dp))
                            Row {
                                FocusAction("▶ Reproduzir") { play(item) }
                                FocusAction("Player externo") {
                                    try { context.startActivity(Intent(Intent.ACTION_VIEW).apply { setDataAndType(Uri.parse(item.url), "video/*"); addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }) }
                                    catch (_: Exception) { status = "Nenhum player externo disponível" }
                                }
                                FocusAction(if (favorites.lines().contains(item.url)) "♥ Remover da lista" else "+ Minha Lista") {
                                    favorites = if (favorites.lines().contains(item.url)) favorites.lines().filter { it != item.url }.joinToString("\n")
                                    else (favorites.lines().filter { it.isNotBlank() } + item.url).joinToString("\n")
                                    prefs.edit().putString("favorites", favorites).apply()
                                }
                            }
                        }
                    }
                    if (item.type == IPTVType.LIVE) {
                        Spacer(Modifier.height(24.dp))
                        Text("Guia de programação", color = white, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                        if (epgSchedule.isEmpty()) Text("Programação indisponível para este canal. Confira o tvg-id e atualize o EPG nas configurações.", color = muted)
                        epgSchedule.forEach { EpgRow(it) }
                    }
                    if (status.isNotBlank()) Text(status, color = muted)
                }
            }
            tab == "Configurações" -> {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(28.dp)) {
                    Text("Configurações", color = white, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(18.dp))
                    Text("Endereço da lista IPTV M3U", color = muted)
                    AddressField(listUrl) { listUrl = it }
                    Row {
                        FocusAction(if (importing) "Importando: $importedCount" else "Importar lista M3U") { importPlaylist() }
                        FocusAction("Salvar endereço") { prefs.edit().putString("playlist", listUrl.trim()).apply(); status = "Endereço M3U salvo" }
                    }
                    Spacer(Modifier.height(28.dp))
                    Text("Guia de programação XMLTV (EPG)", color = muted)
                    AddressField(epgUrl) { epgUrl = it }
                    Row {
                        FocusAction(if (importingEpg) "Importando: $epgCount" else "Atualizar EPG") { importGuide() }
                        FocusAction("Salvar EPG") { prefs.edit().putString("epg", epgUrl.trim()).apply(); status = "Endereço EPG salvo" }
                    }
                    Spacer(Modifier.height(24.dp))
                    FocusAction("Limpar histórico") { recent = ""; prefs.edit().remove("recent").apply(); status = "Histórico apagado" }
                    Text("$total conteúdos no catálogo local • Use listas e transmissões autorizadas.", color = muted, fontSize = 13.sp)
                    if (status.isNotBlank()) Text(status, color = white, modifier = Modifier.padding(top = 14.dp))
                }
            }
            tab == "Início" -> {
                HomePage(context, total, revision, openType = { tab = it }, onDetail = ::showDetail)
            }
            tab == "Recentes" || tab == "Minha Lista" -> {
                Column(Modifier.fillMaxSize().padding(20.dp)) {
                    SectionTitle(tab, "${entries.size} itens salvos")
                    if (entries.isEmpty()) Text("Ainda não há conteúdos nesta seção.", color = muted)
                    LazyVerticalGrid(GridCells.Adaptive(190.dp), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)) {
                        items(entries, key = { it.id }) { item -> MediaTile(item, onClick = { showDetail(item) }) }
                    }
                }
            }
            else -> {
                Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SectionTitle(tab, if (chosenGroup == null) "Selecione uma categoria" else chosenGroup.orEmpty())
                        Spacer(Modifier.weight(1f))
                        Text("${String.format(Locale("pt", "BR"), "%,d", total)} títulos no catálogo", color = muted, fontSize = 12.sp)
                    }
                    if (tab == "Categorias") {
                        Row { FocusAction("TV", selected = currentType == IPTVType.LIVE) { mediaType = IPTVType.LIVE }; FocusAction("Filmes", selected = currentType == IPTVType.MOVIE) { mediaType = IPTVType.MOVIE }; FocusAction("Séries", selected = currentType == IPTVType.SERIES) { mediaType = IPTVType.SERIES } }
                    }
                    if (tab == "EPG") Text("Abra um canal para consultar a programação atual, os próximos horários e sinopses quando disponíveis.", color = muted, fontSize = 13.sp)
                    if (total == 0) { Text("Importe sua lista em Configurações.", color = muted); FocusAction("Abrir Configurações") { tab = "Configurações" } }
                    Spacer(Modifier.height(10.dp))
                    if (chosenGroup == null) {
                        if (groups.isEmpty() && total > 0) Text("Nenhuma categoria identificada nesta seção.", color = muted)
                        LazyVerticalGrid(GridCells.Adaptive(240.dp), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 15.dp)) {
                            items(groups, key = { it.first }) { (category, n) -> CategoryTile(category, n) { chosenGroup = category } }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FocusAction("← Categorias") { chosenGroup = null }
                            Spacer(Modifier.width(10.dp))
                            Text(chosenGroup.orEmpty(), color = white, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        LazyVerticalGrid(GridCells.Adaptive(180.dp), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 15.dp)) {
                            items(entries, key = { it.id }) { item -> MediaTile(item) { showDetail(item) } }
                            if (more) item { FocusAction(if (loadingMore) "Carregando..." else "Carregar mais") { nextPage() } }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomePage(context: android.content.Context, total: Int, revision: Int, openType: (String) -> Unit, onDetail: (IPTVItem) -> Unit) {
    var hero by remember { mutableStateOf<IPTVItem?>(null) }
    LaunchedEffect(revision) { hero = withContext(Dispatchers.IO) { IPTVRepository.page(context, IPTVType.MOVIE, limit = 1).firstOrNull() ?: IPTVRepository.page(context, IPTVType.LIVE, limit = 1).firstOrNull() } }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item {
            Box(Modifier.fillMaxWidth().height(224.dp).background(Brush.horizontalGradient(listOf(Color(0xFF230608), Color(0xFF0B0B0B), ink)))) {
                hero?.takeIf { it.logo.isNotBlank() }?.let { featured ->
                    AsyncImage(model = featured.logo, contentDescription = null, modifier = Modifier.align(Alignment.CenterEnd).fillMaxWidth(.38f).fillMaxHeight().alpha(.42f), contentScale = ContentScale.Fit)
                }
                Column(Modifier.align(Alignment.CenterStart).padding(start = 34.dp)) {
                    Text("GAMA ORIGINAL", color = red, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 3.sp)
                    Spacer(Modifier.height(9.dp))
                    Text(hero?.name ?: "O melhor da sua TV", color = white, fontSize = 32.sp, fontWeight = FontWeight.Black, maxLines = 2)
                    Spacer(Modifier.height(8.dp))
                    Text(if (total == 0) "Importe sua playlist para começar" else "Canais, filmes e séries organizados para você", color = muted, fontSize = 15.sp)
                    Spacer(Modifier.height(15.dp))
                    Row {
                        FocusAction(if (hero == null) "Configurar lista" else "▶ Ver detalhes") { if (hero != null) onDetail(hero!!) else openType("Configurações") }
                        FocusAction("Explorar categorias") { openType("Categorias") }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item { Shelf(context, IPTVType.LIVE, revision, "TV ao vivo", "TV", onDetail, openType) }
        item { Shelf(context, IPTVType.MOVIE, revision, "Filmes em destaque", "Filmes", onDetail, openType) }
        item { Shelf(context, IPTVType.SERIES, revision, "Séries disponíveis", "Séries", onDetail, openType) }
    }
}

@Composable
private fun Shelf(context: android.content.Context, type: IPTVType, revision: Int, heading: String, tab: String, onDetail: (IPTVItem) -> Unit, openType: (String) -> Unit) {
    var content by remember(type) { mutableStateOf<List<IPTVItem>>(emptyList()) }
    LaunchedEffect(type, revision) { content = withContext(Dispatchers.IO) { IPTVRepository.page(context, type, limit = 12) } }
    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(heading, color = white, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            FocusAction("Ver categorias  ›") { openType(tab) }
        }
        if (content.isEmpty()) Text("Nenhum conteúdo encontrado nesta seção.", color = muted, fontSize = 12.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
            items(content, key = { it.id }) { item -> MediaTile(item, Modifier.width(210.dp)) { onDetail(item) } }
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column {
        Text(title, color = white, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = muted, fontSize = 13.sp, maxLines = 1)
    }
}

@Composable
private fun CategoryTile(title: String, count: Int, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.03f else 1f, label = "categoryFocus")
    Column(Modifier.padding(7.dp).scale(scale).onFocusChanged { focused = it.isFocused }
        .clip(RoundedCornerShape(12.dp))
        .border(if (focused) 2.dp else 1.dp, if (focused) red else Color(0xFF323232), RoundedCornerShape(12.dp))
        .background(Brush.horizontalGradient(listOf(Color(0xFF301015), Color(0xFF151515))))
        .clickable(onClick = onClick).padding(18.dp)) {
        Text("▣", color = red, fontSize = 22.sp)
        Spacer(Modifier.height(10.dp))
        Text(title, color = white, fontSize = 17.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(4.dp))
        Text("${String.format(Locale("pt", "BR"), "%,d", count)} conteúdos", color = muted, fontSize = 12.sp)
    }
}

@Composable
private fun MediaArtwork(item: IPTVItem, modifier: Modifier = Modifier) {
    Box(modifier.background(Color(0xFF1C1C1C), RoundedCornerShape(9.dp)).clip(RoundedCornerShape(9.dp)), contentAlignment = Alignment.Center) {
        if (item.logo.isNotBlank()) AsyncImage(model = item.logo, contentDescription = item.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        else Text("G", fontSize = 48.sp, fontWeight = FontWeight.Black, color = red)
    }
}

@Composable
private fun MediaTile(item: IPTVItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.04f else 1f, label = "mediaFocus")
    Column(modifier.padding(6.dp).scale(scale).onFocusChanged { focused = it.isFocused }
        .border(if (focused) 2.dp else 1.dp, if (focused) red else Color(0xFF292929), RoundedCornerShape(9.dp))
        .clip(RoundedCornerShape(9.dp)).background(panel).clickable(onClick = onClick)) {
        MediaArtwork(item, Modifier.fillMaxWidth().height(112.dp))
        Text(item.name, color = white, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
            maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 8.dp))
        Text(item.group.ifBlank { "Sem categoria" }, color = muted, fontSize = 11.sp, maxLines = 1,
            overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp, top = 3.dp))
    }
}

@Composable
private fun AddressField(value: String, onChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onChange, modifier = Modifier.fillMaxWidth(), singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = white, unfocusedTextColor = white,
            focusedBorderColor = red, unfocusedBorderColor = muted))
}

@Composable
private fun EpgRow(program: EpgEntry) {
    val fmt = remember { SimpleDateFormat("dd/MM HH:mm", Locale("pt", "BR")) }
    val now = System.currentTimeMillis()
    Column(Modifier.fillMaxWidth().padding(vertical = 5.dp).background(panel, RoundedCornerShape(7.dp)).padding(12.dp)) {
        Text("${fmt.format(Date(program.start))} — ${fmt.format(Date(program.stop))}" + if (now in program.start until program.stop) "  • NO AR" else "", color = red, fontSize = 12.sp)
        Text(program.title, color = white, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        if (program.subtitle.isNotBlank()) Text(program.subtitle, color = muted)
        if (program.description.isNotBlank()) Text(program.description, color = white, fontSize = 13.sp)
        val info = listOf(program.category, program.year, program.rating).filter { it.isNotBlank() }.joinToString(" • ")
        if (info.isNotBlank()) Text(info, color = muted, fontSize = 12.sp)
    }
}

@Composable
private fun GamaPlayer(item: IPTVItem, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val player = remember(item.url) { ExoPlayer.Builder(ctx).build() }
    LaunchedEffect(item.url) {
        try { player.setMediaItem(MediaItem.fromUri(item.url)); player.prepare(); player.playWhenReady = true }
        catch (_: Exception) { onBack() }
    }
    DisposableEffect(player) { onDispose { player.release() } }
    Box(Modifier.fillMaxSize().background(ink)) {
        AndroidView(factory = { PlayerView(it).apply { this.player = player; useController = true } }, modifier = Modifier.fillMaxSize())
        FocusAction("← Voltar", modifier = Modifier.align(Alignment.TopStart).padding(12.dp)) { onBack() }
    }
}

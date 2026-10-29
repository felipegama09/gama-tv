package br.gamaflix

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Guia de programação: canais à esquerda, programação completa do canal em foco à direita. */
class EpgActivity : AppCompatActivity() {

    private lateinit var rvChannels: RecyclerView
    private lateinit var rvPrograms: RecyclerView
    private lateinit var tvChannel: TextView
    private lateinit var tvHint: TextView
    private val progAdapter = ProgAdapter()
    private val channelAdapter = ChannelAdapter(
        onFocus = { p -> selectChannel(p) },
        onClick = { p -> play(p) }
    )
    private var channels: List<Item> = emptyList()
    private var selected = -1
    private var job: Job? = null
    private val handler = Handler(Looper.getMainLooper())
    private val loadRunnable = Runnable { loadPrograms() }
    private val cache = HashMap<String, Pair<Long, List<Prog>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Store.source == null) Store.source = Prefs.buildSource(this)
        if (Store.source == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_epg)
        rvChannels = findViewById(R.id.rvChannels)
        rvPrograms = findViewById(R.id.rvPrograms)
        tvChannel = findViewById(R.id.tvEpgChannel)
        tvHint = findViewById(R.id.tvEpgHint)

        rvChannels.layoutManager = LinearLayoutManager(this)
        rvChannels.adapter = channelAdapter
        rvChannels.itemAnimator = null
        rvPrograms.layoutManager = LinearLayoutManager(this)
        rvPrograms.adapter = progAdapter
        rvPrograms.itemAnimator = null

        Ui.bindTopBar(this, R.id.navEpg)
        loadChannels()
    }

    private fun loadChannels() {
        job?.cancel()
        tvHint.text = "Carregando canais..."
        job = lifecycleScope.launch {
            try {
                val c = Store.cache[Kind.LIVE] ?: Store.source!!.load(Kind.LIVE).also { Store.cache[Kind.LIVE] = it }
                channels = c.items
                channelAdapter.set(channels)
                if (channels.isEmpty()) {
                    tvHint.text = "Nenhum canal encontrado."
                } else {
                    rvChannels.post { selectChannel(0) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                tvHint.text = "Falha ao carregar: ${e.message ?: e.javaClass.simpleName}"
            }
        }
    }

    /** Ao focar um canal, espera um instante e então carrega a programação dele. */
    private fun selectChannel(p: Int) {
        selected = p
        tvChannel.text = channels.getOrNull(p)?.name.orEmpty()
        handler.removeCallbacks(loadRunnable)
        handler.postDelayed(loadRunnable, 350)
    }

    private fun loadPrograms() {
        val item = channels.getOrNull(selected) ?: return
        val cached = cache[item.id]
        if (cached != null && System.currentTimeMillis() - cached.first < 5 * 60_000) {
            show(item, cached.second)
            return
        }
        tvHint.text = "Carregando programação..."
        progAdapter.set(emptyList())
        lifecycleScope.launch {
            val progs = try {
                Store.source?.epg(item) ?: emptyList()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emptyList()
            }
            cache[item.id] = System.currentTimeMillis() to progs
            if (channels.getOrNull(selected)?.id == item.id) show(item, progs)
        }
    }

    private fun show(item: Item, progs: List<Prog>) {
        if (progs.isEmpty()) {
            tvHint.text = "Este canal não tem programação disponível."
            progAdapter.set(emptyList())
        } else {
            tvHint.text = "OK no canal para assistir"
            progAdapter.set(progs, System.currentTimeMillis() / 1000)
        }
    }

    private fun play(p: Int) {
        if (channels.isEmpty()) return
        Store.playList = channels
        Store.playIndex = p
        Store.zap = true
        Store.autoNext = false
        startActivity(Intent(this, PlayerActivity::class.java))
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}

/** Lista de programas do canal: horário, título e sinopse. O programa atual aparece marcado. */
class ProgAdapter : RecyclerView.Adapter<ProgAdapter.VH>() {

    private var data: List<Prog> = emptyList()
    private var nowSec = 0L

    fun set(d: List<Prog>, now: Long = 0L) {
        data = d
        nowSec = now
        notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val time: TextView = v.findViewById(R.id.tvTime)
        val title: TextView = v.findViewById(R.id.tvTitle)
        val desc: TextView = v.findViewById(R.id.tvDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_epg, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(h: VH, i: Int) {
        val p = data[i]
        val live = nowSec in p.start until p.stop
        h.time.text = "${hm(p.start)} – ${hm(p.stop)}" + if (live) "   •   AO VIVO" else ""
        h.title.text = p.title
        h.desc.text = p.desc
        h.desc.visibility = if (p.desc.isBlank()) View.GONE else View.VISIBLE
        h.itemView.setOnFocusChangeListener { v, f -> Ui.focusScale(v, f) }
    }

    private fun hm(sec: Long): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(sec * 1000))
}

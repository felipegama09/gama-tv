package br.gamaflix

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.dispose
import coil.load
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.widget.ImageView

/** Filmes e Séries: menu lateral de categorias, grade de capas e painel com o item focado. */
class MainActivity : AppCompatActivity() {

    private lateinit var rvCats: RecyclerView
    private lateinit var rvItems: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var progress: View
    private lateinit var ivFocus: ImageView
    private lateinit var tvFocusTitle: TextView
    private lateinit var tvFocusMeta: TextView
    private lateinit var grid: GridLayoutManager
    private val itemAdapter = ItemAdapter(
        onClick = { list, pos -> onItem(list, pos) },
        onFocus = { item -> showFocus(item) }
    )
    private val catAdapter = CatAdapter { index, byClick -> rvCats.post { onCat(index, byClick) } }

    private var kind = Kind.MOVIE
    private var catalog: Catalog? = null
    private var cats: List<Cat> = emptyList()
    private var catIndex = 0
    private var query = ""
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Store.source == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_main)

        rvCats = findViewById(R.id.rvCats)
        rvItems = findViewById(R.id.rvItems)
        tvEmpty = findViewById(R.id.tvEmpty)
        progress = findViewById(R.id.progress)
        ivFocus = findViewById(R.id.ivFocus)
        tvFocusTitle = findViewById(R.id.tvFocusTitle)
        tvFocusMeta = findViewById(R.id.tvFocusMeta)

        grid = GridLayoutManager(this, 5)
        rvItems.layoutManager = grid
        rvItems.adapter = itemAdapter
        rvItems.setHasFixedSize(true)
        rvItems.itemAnimator = null
        rvItems.setItemViewCacheSize(8)

        rvCats.layoutManager = LinearLayoutManager(this)
        rvCats.adapter = catAdapter
        rvCats.itemAnimator = null

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (query.isNotEmpty()) {
                    query = ""
                    refreshItems()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        val start = intent.getStringExtra("kind")?.let { Kind.valueOf(it) } ?: Kind.MOVIE
        kind = start
        Ui.bindTopBar(this, if (start == Kind.SERIES) R.id.navSeries else R.id.navMovies)
        select(start)
        if (intent.getBooleanExtra("search", false)) askSearch()
    }

    private fun select(k: Kind) {
        kind = k
        query = ""
        grid.spanCount = 5
        itemAdapter.kind = k

        job?.cancel()
        progress.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        catalog = null
        cats = emptyList()
        catAdapter.setData(cats, 0)
        itemAdapter.set(emptyList())

        job = lifecycleScope.launch {
            try {
                val c = Store.cache[k] ?: Store.source!!.load(k).also { Store.cache[k] = it }
                catalog = c
                cats = listOf(Cat("*", "Todos")) + c.cats.filter { c.byCat.containsKey(it.id) }
                catIndex = if (cats.size > 1) 1 else 0
                catAdapter.setData(cats, catIndex)
                refreshItems()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showEmpty("Falha ao carregar: ${e.message ?: e.javaClass.simpleName}")
            } finally {
                if (kind == k) progress.visibility = View.GONE
            }
        }
    }

    private fun onCat(index: Int, byClick: Boolean) {
        if (index == catIndex && !byClick) return
        if (!byClick && query.isNotEmpty()) return
        query = ""
        catIndex = index
        catAdapter.setSelected(index)
        refreshItems()
    }

    private fun refreshItems() {
        val c = catalog ?: return
        if (cats.isEmpty()) return
        val base = when {
            query.isNotEmpty() -> c.items
            cats[catIndex].id == "*" -> c.items
            else -> c.byCat[cats[catIndex].id].orEmpty()
        }
        val list = if (query.isEmpty()) base else base.filter { it.name.contains(query, ignoreCase = true) }
        itemAdapter.set(list)
        rvItems.scrollToPosition(0)
        if (list.isEmpty()) {
            showEmpty(if (query.isEmpty()) "Nada por aqui." else "Nada encontrado para \"$query\".")
        } else {
            tvEmpty.visibility = View.GONE
            showFocus(list[0])
        }
    }

    /** Painel do topo: mostra o título e a capa do item que está em foco. */
    private fun showFocus(item: Item) {
        tvFocusTitle.text = item.name
        tvFocusMeta.text = if (kind == Kind.SERIES) "Série" else "Filme"
        val logo = item.logo
        if (logo.isNullOrBlank()) {
            ivFocus.dispose()
            ivFocus.setImageResource(R.drawable.ph)
        } else {
            ivFocus.load(logo) {
                size(240, 320)
                placeholder(R.drawable.ph)
                error(R.drawable.ph)
            }
        }
    }

    private fun showEmpty(msg: String) {
        tvEmpty.text = msg
        tvEmpty.visibility = View.VISIBLE
    }

    private fun onItem(list: List<Item>, pos: Int) {
        Pick.item = list[pos]
        startActivity(Intent(this, DetailActivity::class.java))
    }

    fun askSearch() {
        val et = EditText(this)
        et.setSingleLine()
        et.hint = "Digite o nome"
        et.setText(query)
        AlertDialog.Builder(this, R.style.NovaDialog)
            .setTitle("Buscar em ${if (kind == Kind.SERIES) "Séries" else "Filmes"}")
            .setView(et)
            .setPositiveButton("Buscar") { _, _ ->
                query = et.text.toString().trim()
                refreshItems()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

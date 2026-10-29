package br.gamaflix

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

/** Elementos visuais compartilhados: barra superior e animação de foco. */
object Ui {

    /** Cresce levemente o item focado, com transição suave. */
    fun focusScale(v: View, focused: Boolean) {
        v.animate().cancel()
        val s = if (focused) 1.05f else 1f
        v.animate().scaleX(s).scaleY(s).setDuration(140).start()
    }

    /** Barra superior igual em todas as telas. `selected` é o id da aba atual. */
    fun bindTopBar(a: AppCompatActivity, selected: Int) {
        val pills = listOf(
            R.id.navSearch, R.id.navHome, R.id.navLive,
            R.id.navMovies, R.id.navSeries, R.id.navSettings
        )
        pills.forEach { id ->
            a.findViewById<TextView>(id).apply {
                isSelected = id == selected
                setOnFocusChangeListener { v, f -> focusScale(v, f) }
            }
        }
        a.findViewById<TextView>(R.id.navSearch).setOnClickListener {
            if (a is MainActivity) a.askSearch()
            else go(a, MainActivity::class.java, Kind.MOVIE, search = true)
        }
        a.findViewById<TextView>(R.id.navHome).setOnClickListener {
            if (a is HomeActivity) a.findViewById<RecyclerView>(R.id.rvRows).smoothScrollToPosition(0)
            else goHome(a)
        }
        a.findViewById<TextView>(R.id.navLive).setOnClickListener {
            if (selected != R.id.navLive) go(a, LiveActivity::class.java)
        }
        a.findViewById<TextView>(R.id.navMovies).setOnClickListener {
            if (selected != R.id.navMovies) go(a, MainActivity::class.java, Kind.MOVIE)
        }
        a.findViewById<TextView>(R.id.navSeries).setOnClickListener {
            if (selected != R.id.navSeries) go(a, MainActivity::class.java, Kind.SERIES)
        }
        a.findViewById<TextView>(R.id.navSettings).setOnClickListener {
            if (a is HomeActivity) a.openSettings()
            else {
                Store.openSettings = true
                goHome(a)
            }
        }
    }

    /** Abre outra seção. Quem está no Início fica por baixo, para o botão voltar funcionar. */
    private fun go(a: AppCompatActivity, cls: Class<*>, kind: Kind? = null, search: Boolean = false) {
        val i = Intent(a, cls)
        if (kind != null) i.putExtra("kind", kind.name)
        if (search) i.putExtra("search", true)
        a.startActivity(i)
        if (a !is HomeActivity) a.finish()
    }

    private fun goHome(a: AppCompatActivity) {
        a.startActivity(Intent(a, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        a.finish()
    }
}

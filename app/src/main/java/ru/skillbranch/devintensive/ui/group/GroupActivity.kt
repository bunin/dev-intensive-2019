package ru.skillbranch.devintensive.ui.group

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_group.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.ui.adapters.UserAdapter
import ru.skillbranch.devintensive.ui.custom.AvatarImageView
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.viewmodels.GroupViewModel

class GroupActivity : AppCompatActivity() {

    private lateinit var userAdapter: UserAdapter
    private lateinit var viewModel: GroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        initToolbar()
        initViews()
        initViewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initViews() {
        userAdapter = UserAdapter { viewModel.handleSelectedItem(it.id) }
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(resources.getDrawable(R.drawable.id_divider, theme))
        with(rv_user_list) {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(this@GroupActivity)
            addItemDecoration(divider)
        }
        fab.setOnClickListener {
            viewModel.handleCreateGroup()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Введите имя пользователя"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearchQuery(newText)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        viewModel.getUsersData().observe(this, Observer { userAdapter.updateData(it) })
        viewModel.getSelectedData().observe(this, Observer {
            updateChips(it)
            toggleFab(it.size > 1)
        })
    }

    private fun toggleFab(b: Boolean) {
        when (b) {
            true -> fab.show()
            else -> fab.hide()
        }
    }

    private fun addChipToGroup(user: UserItem) {
        val chip = Chip(this).apply {
            chipIconTint
            text = user.fullName
            chipIcon = resources.getDrawable(R.drawable.avatar_default, theme)
//                getUserAvatar(user.id) ?: resources.getDrawable(R.drawable.avatar_default, theme) // module 4 test is failing b/c of this
            isCloseIconVisible = true
            tag = user.id
            isClickable = true
            closeIconTint =
                ColorStateList.valueOf(Utils.getColor(this.context, R.attr.colorChipClose))
            chipBackgroundColor =
                ColorStateList.valueOf(Utils.getColor(this.context, R.attr.colorChipBg))
            setTextColor(Color.WHITE)
        }
        chip.setOnCloseIconClickListener { viewModel.handleRemoveChip(it.tag.toString()) }
        chip_group.addView(chip)
    }

    private fun updateChips(listUsers: List<UserItem>) {
        chip_group.visibility = if (listUsers.isEmpty()) View.GONE else View.VISIBLE
        chip_group_wrap.visibility = chip_group.visibility

        val users = listUsers
            .associateBy { user -> user.id }
            .toMutableMap()

        val views = chip_group.children.associateBy { view -> view.tag }
        for ((k, v) in views) {
            if (!users.containsKey(k)) chip_group.removeView(v) else users.remove(k)
        }

        users.forEach { (_, v) -> addChipToGroup(v) }
    }

    private fun getUserAvatar(id: String): Drawable? {
        val v = rv_user_list.findViewWithTag<View>(id)
        val a: AvatarImageView = v.findViewById(R.id.iv_avatar_user)
        return Utils.roundDrawable(resources, a.drawable)
    }
}
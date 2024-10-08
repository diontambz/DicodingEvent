package com.diontambz.githubuser.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.diontambz.githubuser.R
import com.diontambz.githubuser.ui.adapter.UserAdapter
import com.diontambz.githubuser.data.remote.response.SimpleUser
import com.diontambz.githubuser.databinding.ActivityFavoriteBinding
import com.diontambz.githubuser.ui.viewmodel.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private val favoriteViewModel: FavoriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            this.title = getString(R.string.favorite)
        }

        lifecycleScope.launchWhenStarted {
            launch {
                favoriteViewModel.favorite.collect {
                    if (it.isNotEmpty()) {
                        val listUsers = ArrayList<SimpleUser>()
                        it.forEach { user ->
                            val data = SimpleUser(
                                user.avatarUrl,
                                user.id
                            )

                            listUsers.add(data)
                        }

                        val listUserAdapter = UserAdapter(listUsers)

                        binding.rvFavorite.apply {
                            layoutManager = LinearLayoutManager(this@FavoriteActivity)
                            adapter = listUserAdapter
                            visibility = View.VISIBLE
                            setHasFixedSize(true)
                        }

                        binding.tvMessage.visibility = View.GONE

                        listUserAdapter.setOnItemClickCallback(object :
                            UserAdapter.OnItemClickCallback {
                            override fun onItemClicked(user: SimpleUser) {
                                Intent(this@FavoriteActivity,
                                    DetailUserActivity::class.java).apply {
                                    putExtra(DetailUserActivity.EXTRA_DETAIL, user.login)
                                }.also {
                                    startActivity(it)
                                }
                            }
                        })
                    }
                    else {
                        binding.tvMessage.visibility = View.VISIBLE
                        binding.rvFavorite.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
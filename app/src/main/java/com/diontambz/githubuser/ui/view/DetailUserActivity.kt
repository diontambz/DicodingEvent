package com.diontambz.githubuser.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.diontambz.githubuser.R
import com.diontambz.githubuser.data.Result
import com.diontambz.githubuser.data.local.UserEntity
import com.diontambz.githubuser.databinding.ActivityDetailBinding
import com.diontambz.githubuser.ui.adapter.PagerAdapter
import com.diontambz.githubuser.ui.viewmodel.DetailViewModel
import com.diontambz.githubuser.util.Helper.Companion.setAndVisible
import com.diontambz.githubuser.util.Helper.Companion.setImageGlide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailUserActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailBinding
    private var username: String? = null
    private var profileUrl: String? = null
    private var userDetail: UserEntity? = null
    private var isFavorite: Boolean? = false
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        username = intent.extras?.get(EXTRA_DETAIL) as String
        setContentView(binding.root)

        val viewPager: ViewPager2 = binding.viewPager
        val tabs: TabLayout = binding.tabs
        viewPager.adapter = PagerAdapter(this, username!!)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        setSupportActionBar(binding.toolbarDetail)
        binding.collapsingToolbar.isTitleEnabled = false
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            this.title = getString(R.string.profile)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    detailViewModel.userDetail.collect { result ->
                        with(binding) {
                            when (result) {
                                is Result.Loading -> {
                                    pbLoading.visibility = View.VISIBLE
                                    appBarLayout.visibility = View.INVISIBLE
                                    viewPager.visibility = View.INVISIBLE
                                    fabFavorite.visibility = View.GONE
                                }

                                is Result.Error -> {
                                    userDetailContainer.visibility = View.INVISIBLE
                                    tabs.visibility = View.INVISIBLE
                                    viewPager.visibility = View.INVISIBLE
                                    pbLoading.visibility = View.GONE
                                    appBarLayout.visibility = View.VISIBLE
                                    viewPager.visibility = View.VISIBLE
                                    fabFavorite.visibility = View.VISIBLE
                                    Toast.makeText(
                                        this@DetailUserActivity,
                                        result.error,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                is Result.Success -> {
                                    result.data.let { user ->
                                        tvUsername.text = user.login
                                        tvRepositories.text = user.publicRepos.toString()
                                        tvGists.text = user.publicGists.toString()
                                        tvFollowers.text = user.followers.toString()
                                        tvFollowing.text = user.following.toString()

                                        tvName.setAndVisible(user.name)
                                        tvBio.setAndVisible(user.bio)
                                        tvCompany.setAndVisible(user.company)
                                        tvLocation.setAndVisible(user.location)
                                        tvBlog.setAndVisible(user.blog)
                                        ivProfile.setImageGlide(
                                            this@DetailUserActivity,
                                            user.avatarUrl
                                        )

                                        val userEntity =
                                            UserEntity(user.login, user.avatarUrl, true)
                                        userDetail = userEntity
                                        profileUrl = user.htmlUrl
                                    }
                                    pbLoading.visibility = View.GONE
                                    appBarLayout.visibility = View.VISIBLE
                                    viewPager.visibility = View.VISIBLE
                                    fabFavorite.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
                launch {
                    detailViewModel.isFavorite(username ?: "").collect { state ->
                        if (state) {
                            binding.fabFavorite.setImageResource(R.drawable.heart_fill)
                        } else {
                            binding.fabFavorite.setImageResource(R.drawable.heart)
                        }
                        isFavorite = state
                    }
                }
                launch {
                    detailViewModel.isLoaded.collect { loaded ->
                        if (!loaded) detailViewModel.getDetail(username ?: "")
                    }
                }
            }
        }
        binding.btnOpen.setOnClickListener(this)
        binding.fabFavorite.setOnClickListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_open -> {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(profileUrl)
                }.also {
                    startActivity(it)
                }
            }

            R.id.fab_favorite -> {
                if (isFavorite == true) {
                    userDetail?.let { detailViewModel.deleteFromFavorite(it) }
                    binding.fabFavorite.setImageResource(R.drawable.heart)
                    Toast.makeText(
                        this,
                        getString(R.string.deleted_from_favorite),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    userDetail?.let { detailViewModel.addToFavorite(it) }
                    binding.fabFavorite.setImageResource(R.drawable.heart_fill)
                    Toast.makeText(this, getString(R.string.added_to_favorite), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onDestroy() {
        username = null
        profileUrl = null
        isFavorite = null
        super.onDestroy()
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
        private val TAB_TITLES = intArrayOf(R.string.followers, R.string.following)
    }
}
package com.udacity.asteroidradar.main

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.FilterAsteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.local_data.getDatabase
import com.udacity.asteroidradar.repo.Repo
import kotlinx.android.synthetic.main.item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@BindingAdapter("statusIcon")
fun ImageView.hazardstatus(hazardStatus: Boolean) {
    if (hazardStatus) {
        this.setImageResource(R.drawable.ic_status_potentially_hazardous)
        this.contentDescription = this.imageView.context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        this.setImageResource(R.drawable.ic_status_normal)
        this.contentDescription = this.imageView.context.getString(R.string.not_hazardous_asteroid_image)


    }
}

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private val listAdapter = AsteroidListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val database = getDatabase(requireContext())
        val repo = Repo(database)
        listAdapter.setOnClickListener(object : AsteroidListAdapter.AsteroidListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun listen(asteroidPos: Int) {

                viewModel.asteroidList.value?.let { viewModel.onAsteroidClicked(it.get(asteroidPos)) }
            }
        })
        binding.asteroidRecycler.adapter = listAdapter

        refresh(repo)
        binding.statusLoadingWheel.visibility = View.VISIBLE



        viewModel.navigateToDetailAsteroid.observe(viewLifecycleOwner, Observer {
            println("is changed ")
            if (it != null) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.onAsteroidNavigated()
            }
        })


        viewModel.pictureOfDay.observe(viewLifecycleOwner, Observer {
            Glide.with(this).load(it.url).placeholder(R.drawable.placeholder_picture_of_day)
                .into(binding.activityMainImageOfTheDay);
            binding.statusLoadingWheel.visibility = View.GONE

        })
        setHasOptionsMenu(true)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        viewModel.asteroidList.observe(viewLifecycleOwner, Observer<List<Asteroid>> { asteroid ->
            println("onViewCreated and add items")
            asteroid.apply {
                listAdapter.submitList(this)
            }
        })
    }


    private fun refresh(repo: Repo) {
        CoroutineScope(Dispatchers.IO).launch {
            print("test : ")
            repo.refreshAsteroids()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.onChangeFilter(
            when (item.itemId) {
                R.id.show_rent_menu -> {
                    FilterAsteroid.TODAY
                }
                R.id.show_all_menu -> {
                    FilterAsteroid.WEEK
                }
                else -> {
                    FilterAsteroid.ALL
                }
            }
        )
        return true
    }

}

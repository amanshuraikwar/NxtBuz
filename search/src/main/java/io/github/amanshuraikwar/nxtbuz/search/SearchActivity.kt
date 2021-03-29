package io.github.amanshuraikwar.nxtbuz.search

import dagger.android.support.DaggerAppCompatActivity

//class SearchActivity : DaggerAppCompatActivity() {

//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    private lateinit var viewModel: SearchViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search)
//        setupViewModel()
//        searchTiet.requestFocus()
//        searchTiet.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                viewModel.searchBusStops(searchTiet.text.toString())
//                searchTiet.clearFocus()
//                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(searchTiet.windowToken, 0)
//                return@OnEditorActionListener true
//            }
//            false
//        })
//        backFab.setOnClickListener {
//            setResult(Activity.CONTEXT_INCLUDE_CODE)
//            finish()
//        }
//    }
//
//    private fun setupViewModel() {
//        viewModel = viewModelProvider(viewModelFactory)
//
//        viewModel.error.observe(
//            this,
//            EventObserver { alert: Alert ->
//                itemsRv.visibility = View.GONE
//                loadingIv.visibility = View.VISIBLE
//                loadingTv.visibility = View.VISIBLE
//                loadingIv.setImageResource(alert.iconResId)
//                loadingTv.text = alert.msg
//            }
//        )
//
//        viewModel.busStops.observe(
//            this,
//            Observer { listItems ->
//                val adapter =
//                    MultiItemAdapter(this, RecyclerViewTypeFactoryGenerated(), listItems)
//                itemsRv.layoutManager = LinearLayoutManager(this)
//                itemsRv.adapter = adapter
//            }
//        )
//
//        viewModel.busStopClicked.observe(
//            this,
//            EventObserver { busStop ->
//                setResult(
//                    Activity.RESULT_OK,
//                    Intent().putExtra(
//                        "bus_stop",
//                        busStop
//                    )
//                )
//                finish()
//            }
//        )
//
//        viewModel.loading.observe(
//            this,
//            EventObserver { showLoading ->
//                if (showLoading) {
//                    showLoading()
//                } else {
//                    hideLoading()
//                }
//            }
//        )
//
//        viewModel.busServiceClicked.observe(
//            this,
//            EventObserver { busService ->
//                setResult(
//                    Activity.RESULT_OK,
//                    Intent().putExtra(
//                        "bus_service",
//                        busService
//                    )
//                )
//                finish()
//            }
//        )
//    }
//
//    private fun hideLoading() {
//        loadingIv.visibility = View.INVISIBLE
//        loadingTv.visibility = View.INVISIBLE
//        itemsRv.visibility = View.VISIBLE
//    }
//
//    private fun showLoading() {
//        val animated =
//            AnimatedVectorDrawableCompat.create(this, R.drawable.avd_anim_search_loading_128)
//        loadingTv.text = "Searching bus stops..."
//        loadingIv.setImageDrawable(animated)
//        animated?.start()
//        itemsRv.visibility = View.GONE
//        loadingIv.visibility = View.VISIBLE
//        loadingTv.visibility = View.VISIBLE
//    }
//}
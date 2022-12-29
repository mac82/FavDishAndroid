package com.softinsa.myapplication.views.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softinsa.myapplication.model.RandomDishModel
import com.softinsa.myapplication.network.RandomDishApiService
import com.softinsa.myapplication.network.RetrofitBuilder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class RandomDishViewModel: ViewModel() {

    private val randomRecipeApiService = RandomDishApiService(RetrofitBuilder.randomDishApiService)

    // TODO Step 3: Create an instance of CompositeDisposable class.
    // START
    /**
     * A disposable container that can hold onto multiple other Disposables and
     * offers time complexity for add(Disposable), remove(Disposable) and delete(Disposable)
     * operations.
     */
    private val compositeDisposable = CompositeDisposable()
    // END

    // TODO Step 4: Create three instance of API response observer.
    // START
    /**
     * Creates a MutableLiveData with no value assigned to it.
     */
    val loadRandomDish = MutableLiveData<Boolean>()
    val randomDishResponse = MutableLiveData<RandomDishModel.Recipes>()
    val randomDishLoadingError = MutableLiveData<Boolean>()
    // END

    // TODO Step 5: Create a function to initialize all the instance and set the observers to it.
    // START
    fun getRandomRecipeFromAPI() {
        // Define the value of the load random dish.
        loadRandomDish.value = true

        // Adds a Disposable to this container or disposes it if the container has been disposed.
        compositeDisposable.add(
            // Call the RandomDish method of RandomDishApiService class.
            randomRecipeApiService.getRandomDish()
                // Asynchronously subscribes SingleObserver to this Single on the specified Scheduler.
                /**
                 * Static factory methods for returning standard Scheduler instances.
                 *
                 * The initial and runtime values of the various scheduler types can be overridden via the
                 * {RxJavaPlugins.setInit(scheduler name)SchedulerHandler()} and
                 * {RxJavaPlugins.set(scheduler name)SchedulerHandler()} respectively.
                 */
                .subscribeOn(Schedulers.newThread())
                /**
                 * Signals the success item or the terminal signals of the current Single on the specified Scheduler,
                 * asynchronously.
                 *
                 * A Scheduler which executes actions on the Android main thread.
                 */
                .observeOn(AndroidSchedulers.mainThread())
                /**
                 * Subscribes a given SingleObserver (subclass) to this Single and returns the given
                 * SingleObserver as is.
                 */
                .subscribeWith(object : DisposableSingleObserver<RandomDishModel.Recipes>() {
                    override fun onSuccess(value: RandomDishModel.Recipes?) {
                        // Update the values with response in the success method.
                        loadRandomDish.value = false
                        randomDishResponse.value = value!!
                        randomDishLoadingError.value = false
                    }

                    override fun onError(e: Throwable?) {
                        // Update the values in the response in the error method
                        loadRandomDish.value = false
                        randomDishLoadingError.value = true
                        e!!.printStackTrace()
                    }
                })
        )
    }
    // END
}
// END
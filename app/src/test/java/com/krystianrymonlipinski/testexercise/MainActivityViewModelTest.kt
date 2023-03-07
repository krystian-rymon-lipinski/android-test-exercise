package com.krystianrymonlipinski.testexercise

import com.krystianrymonlipinski.testexercise.retrofit.HttpService
import com.krystianrymonlipinski.testexercise.retrofit.model.NumberObject
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.runners.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback

@RunWith(MockitoJUnitRunner::class)
class MainActivityViewModelTest {

    @Mock
    private lateinit var httpService: HttpService
    @Captor
    private lateinit var httpCallback: ArgumentCaptor<Callback<List<NumberObject>>>

    private var testObj: MainActivityViewModel? = null


    @Before
    fun setup() {
        testObj = MainActivityViewModel(httpService)
    }

    @After
    fun tearDown() {
        testObj = null
    }

    @Test
    fun enqueueNumbersInfo() {
        val getAllInfoRequest = Mockito.mock(Call::class.java)
        `when`(httpService.getAllNumbersInfo()).thenReturn(getAllInfoRequest as Call<List<NumberObject>>?)

        testObj?.loadAllNumbersInfo()

        verify(getAllInfoRequest).enqueue(httpCallback.capture())
    }

}
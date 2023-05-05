package com.example.acronymsmeaning.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.acronymsmeaning.data.AcronymRepository
import com.example.acronymsmeaning.data.model.Definition
import com.example.acronymsmeaning.ui.MainFragmentViewModel
import com.example.acronymsmeaning.utils.RequestState
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainFragmentViewModelTest {

    @get:Rule val rule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val mockRepository = mockk<AcronymRepository>(relaxed = true)

    private lateinit var testObject: MainFragmentViewModel

    @Before
    fun startup() {
        Dispatchers.setMain(testDispatcher)
        testObject = MainFragmentViewModel(mockRepository, testDispatcher)
    }

    @After
    fun shutdown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `handle search items when retrieving list of acronyms returns success`() {
        every { mockRepository.getAcronymDefinition("HMM") } returns flowOf(
            RequestState.SUCCESS(listOf(mockk<Definition> {
                every { lfs } returns listOf(mockk {
                    every { lf } returns "name"
                })
            }))
        )
        var loading = false
        val list = mutableListOf<RequestState>()
        testObject.acronymDefinition.observeForever {
            list.add(it)
        }
        testObject.isLoading.observeForever {
            loading = it
        }
        testObject.handleSearch("HMM")

        val result = list[0] as RequestState.SUCCESS<List<Definition>>

        assertEquals(1, list.size)
        assertFalse(loading)
        assertEquals(1, result.definitions.first().lfs.size)
        assertEquals(1, result.definitions.size)
        assertEquals("name", result.definitions.first().lfs.first().lf)
    }

    @Test
    fun `handle search items when retrieving list of acronyms returns error`() {
        every { mockRepository.getAcronymDefinition("HMM") } returns flowOf(
            RequestState.ERROR(Exception("Error"))
        )
        val list = mutableListOf<RequestState>()
        var loading = false

        testObject.acronymDefinition.observeForever {
            list.add(it)
        }

        testObject.isLoading.observeForever {
            loading = it
        }

        testObject.handleSearch("HMM")

        val result = list[0] as RequestState.ERROR

        assertEquals(1, list.size)
        assertFalse(loading)
        assertEquals("Error", result.exception.localizedMessage)
    }

    @Test
    fun `handle search items when retrieving list of acronyms returns loading`() {
        every { mockRepository.getAcronymDefinition("HMM") } returns flowOf(
            RequestState.LOADING()
        )
        val list = mutableListOf<RequestState>()
        var loading = false
        testObject.acronymDefinition.observeForever {
            list.add(it)
        }
        testObject.isLoading.observeForever {
            loading = it
        }
        testObject.handleSearch("HMM")

        val result = list[0] as RequestState.LOADING

        assertEquals(1, list.size)
        assertTrue(loading)
        assertTrue(result.loading)
    }
}
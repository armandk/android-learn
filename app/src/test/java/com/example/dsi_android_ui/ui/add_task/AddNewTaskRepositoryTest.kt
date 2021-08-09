package com.example.dsi_android_ui.ui.add_task

import com.example.dsi_android_ui.network.VolleyConsumer
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.spy
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class AddNewTaskRepositoryTest {
    @After
    fun tearDown() {
        unmockkAll()
    }

    private lateinit var payload: JSONObject
    private lateinit var listener: VolleyRequestListener
    private lateinit var target: AddNewTaskRepository
//    private lateinit var volleyConsumer: VolleyConsumer

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        payload = Mockito.mock(JSONObject::class.java)
        listener = Mockito.mock(VolleyRequestListener::class.java)
        target = spy(AddNewTaskRepository::class.java)
        mockkStatic(VolleyConsumer::class)
        every { VolleyConsumer.addRequest(any()) } answers { }
    }

    /**
     * Unit test for **[AddNewTaskRepository.postTaskRequest]**
     */
    @Test
    fun `given payload and listener when postTaskRequest then send post request`() {
        //actual call
        target.createTask(payload, listener)
        //verify
        verify { VolleyConsumer.addRequest(any()) }
    }

    /**
     * Unit test for **[AddNewTaskRepository.putTaskRequest]**
     */
    @Test
    fun `given payload and listener when putTaskRequest then send put request`() {
        //actual call
        target.updateTask(payload, listener)
        //verify
        verify { VolleyConsumer.addRequest(any()) }
    }
}
package br.com.bexs.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals


@SpringBootTest
internal class TravelRouteServiceHealthTest{

    @Mock
    lateinit var serviceProperties: ServiceProperties

    @InjectMocks
    lateinit var travelServiceHealth: TravelServiceHealth

    @BeforeEach
    fun setProperties(){
        `when`(serviceProperties.name).thenReturn("default profile:")
    }

    @Test
    fun `When getting service healthy should return the default information`(){
        assertEquals("OK {details={ 'internals' : 'working well', 'profile' : 'default profile:' }}"
            , travelServiceHealth.health().toString())
    }
}

package com.catan.datasource; // Or com.catan.domain if BoardDataInputs is there

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;


public class BoardDataInputsTest {

    // Mock FileInputStream instances to be used in tests
    private FileInputStream mockCityCoordsStream;
    private FileInputStream mockCityTerrainsStream;
    private FileInputStream mockCityValuesStream;
    private FileInputStream mockHarborsStream;
    private FileInputStream mockRoadCoordsStream;
    private FileInputStream mockCityNeighborsStream;
    private FileInputStream mockRoadNeighborsStream;
    private FileInputStream mockRobberCoordsStream;
    private FileInputStream mockRobberResourceStream;
    private FileInputStream mockRobberNumberStream;

    private BoardDataInputs dataInputs;
    private BoardDataInputs dataInputsWithNulls;


    @BeforeEach
    void setUp() {
        // Create mock FileInputStream instances for all parameters.
        mockCityCoordsStream = EasyMock.mock(FileInputStream.class);
        mockCityTerrainsStream = EasyMock.mock(FileInputStream.class);
        mockCityValuesStream = EasyMock.mock(FileInputStream.class);
        mockHarborsStream = EasyMock.mock(FileInputStream.class);
        mockRoadCoordsStream = EasyMock.mock(FileInputStream.class);
        mockCityNeighborsStream = EasyMock.mock(FileInputStream.class);
        mockRoadNeighborsStream = EasyMock.mock(FileInputStream.class);
        mockRobberCoordsStream = EasyMock.mock(FileInputStream.class);
        mockRobberResourceStream = EasyMock.mock(FileInputStream.class);
        mockRobberNumberStream = EasyMock.mock(FileInputStream.class);


        dataInputs = new BoardDataInputs(
                mockCityCoordsStream, mockCityTerrainsStream, mockCityValuesStream,
                mockHarborsStream, mockRoadCoordsStream, mockCityNeighborsStream,
                mockRoadNeighborsStream, mockRobberCoordsStream, mockRobberResourceStream,
                mockRobberNumberStream
        );


        dataInputsWithNulls = new BoardDataInputs(
                mockCityCoordsStream, null, null, null, null,
                null, null, null, null, null
        );
    }



    @Test
    void testGetCityCoordsStreamReturnsCorrectInstance() {
        assertSame(mockCityCoordsStream, dataInputs.getCityCoordsStream());
    }

    @Test
    void testGetCityTerrainsStreamReturnsCorrectInstance() {
        assertSame(mockCityTerrainsStream, dataInputs.getCityTerrainsStream());
    }

    @Test
    void testGetCityValuesStreamReturnsCorrectInstance() {
        assertSame(mockCityValuesStream, dataInputs.getCityValuesStream());
    }

    @Test
    void testGetHarborsStreamReturnsCorrectInstance() {
        assertSame(mockHarborsStream, dataInputs.getHarborsStream());
    }

    @Test
    void testGetRoadCoordsStreamReturnsCorrectInstance() {
        assertSame(mockRoadCoordsStream, dataInputs.getRoadCoordsStream());
    }

    @Test
    void testGetCityNeighborsStreamReturnsCorrectInstance() {
        assertSame(mockCityNeighborsStream, dataInputs.getCityNeighborsStream());
    }

    @Test
    void testGetRoadNeighborsStreamReturnsCorrectInstance() {
        assertSame(mockRoadNeighborsStream, dataInputs.getRoadNeighborsStream());
    }

    @Test
    void testGetRobberCoordsStreamReturnsCorrectInstance() {
        assertSame(mockRobberCoordsStream, dataInputs.getRobberCoordsStream());
    }

    @Test
    void testGetRobberResourceStreamReturnsCorrectInstance() {
        assertSame(mockRobberResourceStream, dataInputs.getRobberResourceStream());
    }

    @Test
    void testGetRobberNumberStreamReturnsCorrectInstance() {
        assertSame(mockRobberNumberStream, dataInputs.getRobberNumberStream());
    }


    @Test
    void testGetCityCoordsStreamWithNullInputsReturnsNonNull() {
        assertSame(mockCityCoordsStream, dataInputsWithNulls.getCityCoordsStream());
    }

    @Test
    void testGetCityTerrainsStreamWithNullInputReturnsNull() {
        assertNull(dataInputsWithNulls.getCityTerrainsStream());
    }

    @Test
    void testGetCityValuesStreamWithNullInputReturnsNull() {
        assertNull(dataInputsWithNulls.getCityValuesStream());
    }

    @Test
    void testGetHarborsStreamWithNullInputReturnsNull() {
        assertNull(dataInputsWithNulls.getHarborsStream());
    }

    @Test
    void testGetRoadCoordsStreamWithNullInputReturnsNull() {
        assertNull(dataInputsWithNulls.getRoadCoordsStream());
    }

    @Test
    void testGetCityNeighborsStreamWithNullInputReturnsNull() {
        assertNull(dataInputsWithNulls.getCityNeighborsStream());
    }

    @Test
    void testGetRoadNeighborsStreamWithNullInputReturnsNull() {
        assertNull(dataInputsWithNulls.getRoadNeighborsStream());
    }

    @Test
    void testGetRobberCoordsStreamWithNullInputReturnsNull() {
        assertNull(dataInputsWithNulls.getRobberCoordsStream());
    }

    @Test
    void testGetRobberResourceStreamWithNullInputReturnsNull() {
        assertNull(dataInputsWithNulls.getRobberResourceStream());
    }

    @Test
    void testGetRobberNumberStreamWithNullInputReturnsNull() {
        assertNull(dataInputsWithNulls.getRobberNumberStream());
    }
}
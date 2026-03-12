package com.catan.datasource; // Or com.catan.domain

import java.io.FileInputStream;

public class BoardDataInputs {
    private final FileInputStream cityCoordsStream;
    private final FileInputStream cityTerrainsStream;
    private final FileInputStream cityValuesStream;
    private final FileInputStream harborsStream;
    private final FileInputStream roadCoordsStream;
    private final FileInputStream cityNeighborsStream;
    private final FileInputStream roadNeighborsStream;
    private final FileInputStream robberCoordsStream;
    private final FileInputStream robberResourceStream;
    private final FileInputStream robberNumberStream;

    public BoardDataInputs(
            FileInputStream cityCoordsStream,
            FileInputStream cityTerrainsStream,
            FileInputStream cityValuesStream,
            FileInputStream harborsStream,
            FileInputStream roadCoordsStream,
            FileInputStream cityNeighborsStream,
            FileInputStream roadNeighborsStream,
            FileInputStream robberCoordsStream,
            FileInputStream robberResourceStream,
            FileInputStream robberNumberStream) {
        this.cityCoordsStream = cityCoordsStream;
        this.cityTerrainsStream = cityTerrainsStream;
        this.cityValuesStream = cityValuesStream;
        this.harborsStream = harborsStream;
        this.roadCoordsStream = roadCoordsStream;
        this.cityNeighborsStream = cityNeighborsStream;
        this.roadNeighborsStream = roadNeighborsStream;
        this.robberCoordsStream = robberCoordsStream;
        this.robberResourceStream = robberResourceStream;
        this.robberNumberStream = robberNumberStream;
    }


    public FileInputStream getCityCoordsStream() { return cityCoordsStream; }
    public FileInputStream getCityTerrainsStream() { return cityTerrainsStream; }
    public FileInputStream getCityValuesStream() { return cityValuesStream; }
    public FileInputStream getHarborsStream() { return harborsStream; }
    public FileInputStream getRoadCoordsStream() { return roadCoordsStream; }
    public FileInputStream getCityNeighborsStream() { return cityNeighborsStream; }
    public FileInputStream getRoadNeighborsStream() { return roadNeighborsStream; }
    public FileInputStream getRobberCoordsStream() { return robberCoordsStream; }
    public FileInputStream getRobberResourceStream() { return robberResourceStream; }
    public FileInputStream getRobberNumberStream() { return robberNumberStream; }
}
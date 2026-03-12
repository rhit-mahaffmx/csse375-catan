package com.catan.datasource;

import com.catan.domain.Board;
import com.catan.domain.Dice;
import com.catan.domain.GameWindowController;
import com.catan.domain.TurnStateMachine;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CatanFileReaderTest {
    @Test
    public void testReadCoordinatesFromFileIOException() throws IOException {
        GameWindowController gameWindowController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        BoardDataInputs boardDataInput = EasyMock.niceMock(BoardDataInputs.class);
        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);

        BufferedReader bufferedReader = EasyMock.createMock(BufferedReader.class);
        EasyMock.expect(bufferedReader.readLine()).andThrow(new IOException("This is an IO Exception"));

        EasyMock.replay(bufferedReader);

        try {
            CatanFileReader.readCoordinatesFromFile(bufferedReader);
        } catch (RuntimeException e) {
            assertEquals("I/O exeption reading line", e.getMessage());
        }

        EasyMock.verify(bufferedReader);
    }

    @Test
    public void testReadResourceTypesFromFileIOException() throws IOException {
        GameWindowController gameWindowController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        BoardDataInputs boardDataInput = EasyMock.niceMock(BoardDataInputs.class);
        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);

        BufferedReader bufferedReader = EasyMock.createMock(BufferedReader.class);
        EasyMock.expect(bufferedReader.readLine()).andThrow(new IOException("This is an IO Exception"));

        EasyMock.replay(bufferedReader);

        try {
            CatanFileReader.readResourceTypesFromFile(bufferedReader);
        } catch (RuntimeException e) {
            assertEquals("I/O exception reading line", e.getMessage());
        }

        EasyMock.verify(bufferedReader);

    }

    @Test
    public void testReadRobberNumbersFromFileIOException() throws IOException {
        GameWindowController gameWindowController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        BoardDataInputs boardDataInput = EasyMock.niceMock(BoardDataInputs.class);
        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);

        BufferedReader bufferedReader = EasyMock.createMock(BufferedReader.class);
        EasyMock.expect(bufferedReader.readLine()).andThrow(new IOException("This is an IO Exception"));

        EasyMock.replay(bufferedReader);

        try {
            CatanFileReader.readRobberNumbersFromFile(bufferedReader);
        } catch (RuntimeException e) {
            assertEquals("I/O exception reading line", e.getMessage());
        }

        EasyMock.verify(bufferedReader);

    }


    @Test
    public void testReadTerrainsFromFileIOException() throws IOException {
        GameWindowController gameWindowController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        BoardDataInputs boardDataInput = EasyMock.niceMock(BoardDataInputs.class);
        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);

        BufferedReader bufferedReader = EasyMock.createMock(BufferedReader.class);
        EasyMock.expect(bufferedReader.readLine()).andThrow(new IOException("This is an IO Exception"));

        EasyMock.replay(bufferedReader);

        try {
            CatanFileReader.readTerrainsFromFile(bufferedReader);
        } catch (RuntimeException e) {
            assertEquals("I/O exeption reading line", e.getMessage());
        }

        EasyMock.verify(bufferedReader);
    }

    @Test
    public void testReadNeighborsFromFileIOException() throws IOException {
        GameWindowController gameWindowController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        BoardDataInputs boardDataInput = EasyMock.niceMock(BoardDataInputs.class);
        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);

        BufferedReader bufferedReader = EasyMock.createMock(BufferedReader.class);
        EasyMock.expect(bufferedReader.readLine()).andThrow(new IOException("This is an IO Exception"));

        EasyMock.replay(bufferedReader);

        try {
            CatanFileReader.readNeighborsFromFile(bufferedReader);
        } catch (RuntimeException e) {
            assertEquals("I/O exeption reading line", e.getMessage());
        }

        EasyMock.verify(bufferedReader);
    }

    @Test
    public void testReadNeighborsFromFileTwoNeighbors() throws IOException {
        GameWindowController gameWindowController = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        BoardDataInputs boardDataInput = EasyMock.niceMock(BoardDataInputs.class);
        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);
        BufferedReader bufferedReader = EasyMock.createMock(BufferedReader.class);

        EasyMock.expect(bufferedReader.readLine()).andReturn("0,1");
        EasyMock.expect(bufferedReader.readLine()).andReturn(null);

        EasyMock.replay(bufferedReader);

        try {
            ArrayList<Set<Integer>> neighborsSet = CatanFileReader.readNeighborsFromFile(bufferedReader);

            assertTrue(neighborsSet.getFirst().contains(0));
            assertTrue(neighborsSet.getFirst().contains(1));
        } catch (RuntimeException e) {
            assertEquals("I/O exeption reading line", e.getMessage());
        }

        EasyMock.verify(bufferedReader);
    }

    @Test
    public void testReadNeighborsFromFileThreeNeighbors() throws IOException {
        BufferedReader bufferedReader = EasyMock.createMock(BufferedReader.class);

        EasyMock.expect(bufferedReader.readLine()).andReturn("0,1,2");
        EasyMock.expect(bufferedReader.readLine()).andReturn(null);

        EasyMock.replay(bufferedReader);

        try {
            ArrayList<Set<Integer>> neighborsSet = CatanFileReader.readNeighborsFromFile(bufferedReader);

            assertTrue(neighborsSet.getFirst().contains(0));
            assertTrue(neighborsSet.getFirst().contains(1));
            assertTrue(neighborsSet.getFirst().contains(2));
        } catch (RuntimeException e) {
            assertEquals("I/O exeption reading line", e.getMessage());
        }

        EasyMock.verify(bufferedReader);
    }


    @Test
    public void testReadTileValuesFileIOException() throws IOException {

        BufferedReader bufferedReader = EasyMock.createMock(BufferedReader.class);
        EasyMock.expect(bufferedReader.readLine()).andThrow(new IOException("This is an IO Exception"));

        EasyMock.replay(bufferedReader);

        try {
            CatanFileReader.readTileValues(bufferedReader);
        } catch (RuntimeException e) {
            assertEquals("I/O exeption reading line", e.getMessage());
        }

        EasyMock.verify(bufferedReader);
    }

    @Test
    void readHarbors_IOExceptionRethrown() throws IOException {
        BufferedReader bufferedReader = EasyMock.createMock(BufferedReader.class);

        EasyMock.expect(bufferedReader.readLine()).andThrow(new IOException("Simulated IO Exception"));

        EasyMock.replay(bufferedReader);

        try {
            CatanFileReader.readHarborsFromFile(bufferedReader);

        } catch (RuntimeException e) {
            assertEquals("I/O exception reading harbor line", e.getMessage());
            assertTrue(e.getCause() instanceof IOException);
            assertEquals("Simulated IO Exception", e.getCause().getMessage());
        }

        EasyMock.verify(bufferedReader);
    }



}

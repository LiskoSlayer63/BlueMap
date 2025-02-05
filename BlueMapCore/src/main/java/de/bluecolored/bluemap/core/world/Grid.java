/*
 * This file is part of BlueMap, licensed under the MIT License (MIT).
 *
 * Copyright (c) Blue (Lukas Rieger) <https://bluecolored.de>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.bluecolored.bluemap.core.world;

import com.flowpowered.math.vector.Vector2i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class Grid {

    public static final Grid UNIT = new Grid(Vector2i.ONE);

    private final Vector2i gridSize;
    private final Vector2i offset;

    public Grid(int gridSize) {
        this(gridSize, 0);
    }

    public Grid(int gridSize, int offset) {
        this(new Vector2i(gridSize, gridSize), new Vector2i(offset, offset));
    }

    public Grid(Vector2i gridSize) {
        this(gridSize, Vector2i.ZERO);
    }

    public Grid(Vector2i gridSize, Vector2i offset) {
        Objects.requireNonNull(gridSize);
        Objects.requireNonNull(offset);

        gridSize = gridSize.max(1,1);

        this.gridSize = gridSize;
        this.offset = offset;
    }

    public Vector2i getGridSize() {
        return gridSize;
    }

    public Vector2i getOffset() {
        return offset;
    }

    public Vector2i getCell(Vector2i pos) {
        return new Vector2i(
                Math.floorDiv(pos.getX() - offset.getX(), gridSize.getX()),
                Math.floorDiv(pos.getY() - offset.getY(), gridSize.getY())
        );
    }

    public Vector2i getCellMin(Vector2i cell) {
        return new Vector2i(
                cell.getX() * gridSize.getX() + offset.getX(),
                cell.getY() * gridSize.getY() + offset.getY()
        );
    }

    public Vector2i getCellMax(Vector2i cell) {
        return new Vector2i(
                (cell.getX() + 1) * gridSize.getX() + offset.getX() - 1,
                (cell.getY() + 1) * gridSize.getY() + offset.getY() - 1
        );
    }

    public Vector2i getCellMin(Vector2i cell, Grid targetGrid) {
        return targetGrid.getCell(getCellMin(cell));
    }

    public Vector2i getCellMax(Vector2i cell, Grid targetGrid) {
        return targetGrid.getCell(getCellMax(cell));
    }

    public Collection<Vector2i> getIntersecting(Vector2i cell, Grid targetGrid) {
        Vector2i min = getCellMin(cell, targetGrid);
        Vector2i max = getCellMin(cell, targetGrid);

        if (min.equals(max)) return Collections.singleton(min);

        Collection<Vector2i> intersects = new ArrayList<>();
        for (int x = min.getX(); x <= max.getX(); x++){
            for (int y = min.getY(); y <= max.getY(); y++){
                intersects.add(new Vector2i(x, y));
            }
        }

        return intersects;
    }

    public Grid multiply(Grid other) {
        return new Grid(
                this.gridSize.mul(other.gridSize),
                this.offset.mul(other.gridSize).add(other.offset)
        );
    }

    public Grid divide(Grid other) {
        return new Grid(
                this.gridSize.div(other.gridSize),
                this.offset.sub(other.offset).div(other.gridSize)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grid grid = (Grid) o;
        return gridSize.equals(grid.gridSize) && offset.equals(grid.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gridSize, offset);
    }

    @Override
    public String toString() {
        return "Grid{" +
               "gridSize=" + gridSize +
               ", offset=" + offset +
               '}';
    }

}

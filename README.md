# PathfindingVisualizer

This project uses Java and JavaFX to visualize two pathfinding algorythms: A* and Dijkstra.

## Comparision on empty grid

| A* algorythm | Dijkstra algorythm |
| :---:      | :---:       |
| ![aalgorytm czysty](gify/aalgorytm%20czysty.gif)     | ![djikstra czysty](gify/djikstra%20czysty2.gif) |

## Maze creation(Kruskal's method and random)

| Kruskal's method | Random low/high density |
| :---: | :---: |
| ![kruskal](gify/kruskal.gif) | ![Random low density ](gify/random%20density.gif)|

## RTX ON/OFF - Real Time Experience :) - pick algorithm
You can move start and end and in the meantime it will calculate path, it may lag on bigger grids. Turn on and pick algorythm with radio button.

| A* algorythm | Dijkstra algorythm |
| :---:      | :---:       |
| ![aalgorytm rtx](gify/aalgorytm%20rtx.gif)     | ![djikstra rtx](gify/djikstra%20rtx.gif) |

## Draw your own mazes
Just hold ctrl and draw walls, alt and shift to change location of start and end.

![draw](gify/draw.gif)

## Visited and path count
Counts how many squares were visited and path length.

## Clear
Just clear grid.

## Change options
You can change few options: random density, speed, grid size( bigger = slower :) ).

## Generate Data
Logs map(x, y, isWall, isStart, isEnd, Colour) to file.


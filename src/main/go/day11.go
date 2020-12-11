package main

import (
	"fmt"
	"strings"
)

func day11() {
	input := strings.Split(MustReadFile("../inputs/2020/11.txt"), "\n")
	boardA := splitInput(input)
	boardB := splitInput(input)

	var prevBoardA [][]string

	for len(prevBoardA) == 0 || !boardsEqual(prevBoardA, boardA) {
		prevBoardA = boardA
		boardA = simulate1(prevBoardA)
	}

	var prevBoardB [][]string

	for len(prevBoardB) == 0 || !boardsEqual(prevBoardB, boardB) {
		prevBoardB = boardB
		boardB = simulate2(prevBoardB)
	}

	fmt.Println(countOccupied(boardA)) //	2251
	fmt.Println(countOccupied(boardB)) //	2019
}

func countOccupied(board [][]string) int {
	occupied := 0
	for _, r := range board {
		for _, v := range r {
			if v == "#" {
				occupied++
			}
		}
	}
	return occupied
}

func boardsEqual(board1 [][]string, board2 [][]string) bool {
	for lIdx, l := range board1 {
		for cIdx := range l {
			if board1[lIdx][cIdx] != board2[lIdx][cIdx] {
				return false
			}
		}
	}
	return true
}

func splitInput(input []string) [][]string {
	var ret [][]string
	for _, line := range input {
		ret = append(ret, strings.Split(line, ""))
	}
	return ret
}

func simulate1(input [][]string) [][]string {
	ret := make([][]string, len(input))
	for rowIdx, row := range input {
		var lineRet []string
		for colIdx, c := range row {
			if c == "L" && countOccupiedNeighbors(input, rowIdx, colIdx) == 0 {
				lineRet = append(lineRet, "#")
			} else if c == "#" && countOccupiedNeighbors(input, rowIdx, colIdx) >= 4 {
				lineRet = append(lineRet, "L")
			} else {
				lineRet = append(lineRet, c)
			}
			ret[rowIdx] = lineRet
		}
	}
	return ret
}

func simulate2(input [][]string) [][]string {
	ret := make([][]string, len(input))
	for rowIdx, row := range input {
		var lineRet []string
		for colIdx, c := range row {
			if c == "L" && countOccupiedAtDirection(input, rowIdx, colIdx) == 0 {
				lineRet = append(lineRet, "#")
			} else if c == "#" && countOccupiedAtDirection(input, rowIdx, colIdx) >= 5 {
				lineRet = append(lineRet, "L")
			} else {
				lineRet = append(lineRet, c)
			}
			ret[rowIdx] = lineRet
		}
	}
	return ret
}

var directions = [][]int{
	{1, 1},
	{1, 0},
	{1, -1},
	{0, 1},
	{0, -1},
	{-1, 1},
	{-1, 0},
	{-1, -1},
}

func countOccupiedAtDirection(input [][]string, x int, y int) int {
	count := 0

	for _, dir := range directions {
		vector := []int{x + dir[0], y + dir[1]}
		for getSafe(input, vector[0], vector[1]) == "." {
			vector = []int{vector[0] + dir[0], vector[1] + dir[1]}
		}
		if getSafe(input, vector[0], vector[1]) == "#" {
			count++
		}
	}
	return count
}

func countOccupiedNeighbors(input [][]string, x int, y int) int {
	count := 0
	for _, dir := range directions {
		if getSafe(input, x+dir[0], y+dir[1]) == "#" {
			count++
		}
	}
	return count
}

func getSafe(input [][]string, x int, y int) string {
	if x >= 0 && x < len(input) && y >= 0 && y < len(input[x]) {
		return input[x][y]
	} else {
		return ""
	}
}

package main

import (
	"fmt"
	"strings"
)

func day17() {
	input := strings.Split(MustReadFile("../inputs/2020/17.txt"), "\n")
	board3d := splitInput3d(input)
	board4d := splitInput4d(input)

	fmt.Println(board3d)

	iteration := 0

	for iteration < 6 {
		board3d = expand3dDay17(board3d)
		board4d = expand4dDay17(board4d)
		iteration++
	}

	//printBoard3d(board3d)
	fmt.Println(countNonEmpty3d(board3d))
	fmt.Println(countNonEmpty4d(board4d))
}

func printBoard3d(boardA [][][]string) {
	for _, x := range boardA {
		if nonEmptyBoard(x, ".") {
			for _, y := range x {
				fmt.Println(y)
			}
			fmt.Println("")
		}
	}
}

func countNonEmpty3d(board [][][]string) int {
	counter := 0
	for _, x := range board {
		for _, y := range x {
			for _, z := range y {
				if z != "." {
					counter++
				}
			}
		}
	}
	return counter
}

func countNonEmpty4d(board [][][][]string) int {
	counter := 0
	for _, x := range board {
		counter += countNonEmpty3d(x)
	}
	return counter
}

func nonEmptyBoard(x [][]string, empty string) bool {
	for idx1 := range x {
		for idx2 := range x[idx1] {
			if x[idx1][idx2] != empty {
				return true
			}
		}
	}
	return false
}

func expand3dDay17(board [][][]string) [][][]string {
	//expand
	board = expand3d(board)

	ret := copy3d(board)

	for zIdx, z := range board {
		for yIdx, y := range z {
			for xIdx, v := range y {
				n := countActiveNeighbors3d(board, zIdx, yIdx, xIdx)
				if v == "#" {
					if n == 2 || n == 3 {
						ret[zIdx][yIdx][xIdx] = "#"
					} else {
						ret[zIdx][yIdx][xIdx] = "."
					}
				} else {
					if n == 3 {
						ret[zIdx][yIdx][xIdx] = "#"
					} else {
						ret[zIdx][yIdx][xIdx] = "."
					}
				}
			}
		}
	}
	return ret
}

func expand4dDay17(board [][][][]string) [][][][]string {
	board = expand4d(board)

	ret := copy4d(board)

	for wIdx, w := range board {
		for zIdx, z := range w {
			for yIdx, y := range z {
				for xIdx, v := range y {
					n := countActiveNeighbors4d(board, wIdx, zIdx, yIdx, xIdx)
					if v == "#" {
						if n == 2 || n == 3 {
							ret[wIdx][zIdx][yIdx][xIdx] = "#"
						} else {
							ret[wIdx][zIdx][yIdx][xIdx] = "."
						}
					} else {
						if n == 3 {
							ret[wIdx][zIdx][yIdx][xIdx] = "#"
						} else {
							ret[wIdx][zIdx][yIdx][xIdx] = "."
						}
					}
				}
			}
		}
	}
	return ret
}

func expand2d(board [][]string) [][]string {
	for idx := range board {
		board[idx] = append(board[idx], ".")
		board[idx] = append([]string{"."}, board[idx]...)
	}
	board = append(board, createEmptyBoard1d(board[0]))
	board = append([][]string{createEmptyBoard1d(board[0])}, board...)
	return board
}

func expand3d(board [][][]string) [][][]string {
	for idx := range board {
		board[idx] = expand2d(board[idx])
	}
	board = append(board, createEmptyBoard2d(board[0]))
	board = append([][][]string{createEmptyBoard2d(board[0])}, board...)
	return board
}

func expand4d(board [][][][]string) [][][][]string {
	for idx := range board {
		board[idx] = expand3d(board[idx])
	}
	board = append(board, createEmptyBoard3d(board[0]))
	board = append([][][][]string{createEmptyBoard3d(board[0])}, board...)
	return board
}

func copy3d(board [][][]string) [][][]string {
	ret := make([][][]string, len(board))
	for i, b2 := range board {
		ret[i] = make([][]string, len(b2))
		for j, b3 := range b2 {
			ret[i][j] = make([]string, len(b3))
			for k, v := range b3 {
				ret[i][j][k] = v
			}
		}
	}
	return ret
}

func copy4d(board [][][][]string) [][][][]string {
	ret := make([][][][]string, len(board))
	for i, b2 := range board {
		ret[i] = copy3d(b2)
	}
	return ret
}

func countActiveNeighbors3d(board [][][]string, z int, y int, x int) int {
	count := 0
	for _, offsetX := range []int{-1, 0, 1} {
		for _, offsetY := range []int{-1, 0, 1} {
			for _, offsetZ := range []int{-1, 0, 1} {
				if offsetX != 0 || offsetY != 0 || offsetZ != 0 {
					if z+offsetZ >= 0 && z+offsetZ < len(board) {
						if y+offsetY >= 0 && y+offsetY < len(board[z+offsetZ]) {
							if x+offsetX >= 0 && x+offsetX < len(board[z+offsetZ][y+offsetY]) {
								if board[z+offsetZ][y+offsetY][x+offsetX] == "#" {
									count++
								}
							}
						}
					}
				}
			}
		}
	}
	return count
}

func countActiveNeighbors4d(board [][][][]string, w int, z int, y int, x int) int {
	count := 0
	for _, offsetX := range []int{-1, 0, 1} {
		for _, offsetY := range []int{-1, 0, 1} {
			for _, offsetZ := range []int{-1, 0, 1} {
				for _, offsetW := range []int{-1, 0, 1} {
					if offsetX != 0 || offsetY != 0 || offsetZ != 0 || offsetW != 0 {
						if w+offsetW >= 0 && w+offsetW < len(board) {
							if z+offsetZ >= 0 && z+offsetZ < len(board[w+offsetW]) {
								if y+offsetY >= 0 && y+offsetY < len(board[w+offsetW][z+offsetZ]) {
									if x+offsetX >= 0 && x+offsetX < len(board[w+offsetW][z+offsetZ][y+offsetY]) {
										if board[w+offsetW][z+offsetZ][y+offsetY][x+offsetX] == "#" {
											count++
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	return count
}

func createEmptyBoard1d(i []string) []string {
	ret := make([]string, len(i))
	for idx := range i {
		ret[idx] = "."
	}
	return ret
}

func createEmptyBoard2d(i [][]string) [][]string {
	ret := make([][]string, len(i))
	for idx1, v1 := range i {
		ret[idx1] = createEmptyBoard1d(v1)
	}
	return ret
}

func createEmptyBoard3d(i [][][]string) [][][]string {
	ret := make([][][]string, len(i))
	for idx1, v1 := range i {
		ret[idx1] = createEmptyBoard2d(v1)
	}
	return ret
}

func splitInput3d(input []string) [][][]string {
	var ret [][]string
	for _, line := range input {
		ret = append(ret, strings.Split(line, ""))
	}
	return [][][]string{
		ret,
	}
}

func splitInput4d(input []string) [][][][]string {
	var ret [][]string
	for _, line := range input {
		ret = append(ret, strings.Split(line, ""))
	}
	return [][][][]string{
		{
			ret,
		},
	}
}

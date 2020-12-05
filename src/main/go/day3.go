package main

import (
	"fmt"
	"strings"
)

func day3() {
	content, err := ReadFile("../inputs/2020/3.txt")

	if err != nil {
		fmt.Println(err)
		return
	}
	input := *content

	rows := strings.Split(input, "\n")

	fmt.Println(calc(rows, 1, 3)) //	176

	cellsToMultiply := []Cell{
		{x: 1, y: 1},
		{x: 1, y: 3},
		{x: 1, y: 5},
		{x: 1, y: 7},
		{x: 2, y: 1},
	}

	mult := 1

	for _, cell := range cellsToMultiply {
		mult *= calc(rows, cell.x, cell.y)
	}

	fmt.Println(mult) // 5872458240
}

type Cell struct {
	x int
	y int
}

func calc(input []string, s int, t int) int {
	a := 0

	for i := 0; s*i < len(input); i++ {
		if input[s*i][(t*i%31)] == '#' {
			a++
		}
	}

	return a
}

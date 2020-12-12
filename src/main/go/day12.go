package main

import (
	"fmt"
	"math"
	"strings"
)

func day12() {
	input := strings.Split(MustReadFile("../inputs/2020/12.txt"), "\n")

	location := NewVector(0, 0)
	waypoint := NewVector(10, 1)

	for _, line := range input {
		cmd := line[0]
		num := mustParseInt(line[1:])

		if cmd == 'N' {
			waypoint.y += num
		} else if cmd == 'S' {
			waypoint.y -= num
		} else if cmd == 'W' {
			waypoint.x -= num
		} else if cmd == 'E' {
			waypoint.x += num
		} else if cmd == 'F' {
			location.AddInPlace(waypoint.Multiply(num))
		} else if cmd == 'L' {
			waypoint.RotateInPlace(num)
		} else if cmd == 'R' {
			waypoint.RotateInPlace(360 - num)
		} else {
			panic(cmd)
		}
	}

	fmt.Println(math.Abs(float64(location.x)) + math.Abs(float64(location.y))) //	52203
}

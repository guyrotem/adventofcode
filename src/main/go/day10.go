package main

import (
	"fmt"
	"math"
	"sort"
	"strings"
)

func day10() {
	input := strings.Split(MustReadFile("../inputs/2020/10.txt"), "\n")
	inputNums := parseArrayToInt(input)
	sort.Ints(inputNums)
	inputNums = append([]int{0}, inputNums...)
	inputNums = append(inputNums, inputNums[len(inputNums)-1]+3)

	var diffs []int
	idx := 1
	for idx < len(inputNums) {
		diffs = append(diffs, inputNums[idx]-inputNums[idx-1])
		idx++
	}

	ones := 0
	threes := 0
	var others []int
	for _, diff := range diffs {
		if diff == 3 {
			threes++
		} else if diff == 1 {
			ones++
		} else {
			others = append(others, diff)
		}
	}
	fmt.Println(ones * threes) //	2210

	gaps := make(map[int]int)

	idx = 0
	oneStrike := 0
	for idx < len(diffs) {
		if diffs[idx] == 1 {
			oneStrike++
		} else if oneStrike > 0 {
			gaps[oneStrike] = gaps[oneStrike] + 1
			oneStrike = 0
		}
		idx++
	}

	options := int64(1)
	for k, v := range gaps {
		subOptions := 1
		if k == 2 {
			subOptions = 2
		} else if k == 3 {
			subOptions = 4
		} else if k == 4 {
			subOptions = 7
		} else if k > 4 {
			panic("Unsupported k")
		}

		options *= int64(math.Pow(float64(subOptions), float64(v)))
	}

	fmt.Println(options) //	7086739046912

}

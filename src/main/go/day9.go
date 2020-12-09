package main

import (
	"fmt"
	"strconv"
	"strings"
)

func day9() {
	input := strings.Split(MustReadFile("../inputs/2020/9.txt"), "\n")
	var inputNums []int

	for _, line := range input {
		v, _ := strconv.ParseInt(line, 10, 32)
		inputNums = append(inputNums, int(v))
	}

	noSumIdx := -1

	for idx := range inputNums {
		if idx > 25 && !sumOfLastN(inputNums, idx, 25) {
			noSumIdx = idx
			break
		}
	}

	if noSumIdx < 0 {
		panic("NOT FOUND")
	}
	fmt.Println(inputNums[noSumIdx])

	n := 2
	for n < noSumIdx {
		res := sumN(inputNums, noSumIdx, n)
		if res >= 0 {
			fmt.Println(n)
			fmt.Println(res)

			min := findMin(inputNums, res, res+n-1)
			max := findMax(inputNums, res, res+n-1)

			fmt.Println(min + max) //	77730285
			break
		}
		n++
	}
}

func findMin(nums []int, start int, stop int) int {
	min := int(^uint(0) >> 1) //	max int
	ptr := start
	for ptr <= stop {
		if nums[ptr] < min {
			min = nums[ptr]
		}
		ptr++
	}
	return min
}

func findMax(nums []int, start int, stop int) int {
	max := -1
	ptr := start
	for ptr <= stop {
		if nums[ptr] > max {
			max = nums[ptr]
		}
		ptr++
	}
	return max
}

func sumN(input []int, idx int, n int) int {
	x := 0
	for x <= idx-n {
		sum := 0
		y := 0
		for y < n {
			sum += input[x+y]
			y++
		}
		if sum == input[idx] {
			return x
		}
		x++
	}
	return -1
}

func sumOfLastN(input []int, idx int, n int) bool {
	for n > 0 {
		y := n - 1
		for y > 0 {
			if input[idx-n]+input[idx-y] == input[idx] {
				return true
			}
			y--
		}
		n--
	}
	return false
}

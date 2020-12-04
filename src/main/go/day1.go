package main

import (
	"fmt"
	"strconv"
	"strings"
)

func day1() {
	content, err := ReadFile("./inputs/2020/1.txt")

	if err != nil {
		fmt.Println(err)
		return
	}
	input := *content
	parsed, cantParse := parse1(input)

	if len(cantParse) > 0 {
		fmt.Println(cantParse)
		return
	}

	sum2 := findSumN(parsed, 2020, 2)
	if sum2 != nil {
		fmt.Println(sum2[0] * sum2[1])
	} else {
		fmt.Println("found nothing 2!")
	}
	sum3 := findSumN(parsed, 2020, 3)
	if sum3 != nil {
		fmt.Println(sum3[0] * sum3[1] * sum3[2])
	} else {
		fmt.Println("found nothing 3!")
	}
}

func parse1(input string) ([]int, []string) {
	inputRows := strings.Split(input, "\n")
	var parsed []int
	var failed []string
	for _, row := range inputRows {
		num, err := strconv.Atoi(row)
		if err == nil {
			parsed = append(parsed, num)
		} else {
			failed = append(failed, row)
		}
	}
	return parsed, failed
}

func findSumN(input []int, sum int, levels int) []int {
	if levels == 0 && sum == 0 {
		return []int{}
	} else if levels <= 0 {
		return nil
	}

	for _, v1 := range input {
		var nextLevel = findSumN(input[1:], sum-v1, levels-1)
		if nextLevel != nil {
			return append(nextLevel, v1)
		}
	}

	return nil
}

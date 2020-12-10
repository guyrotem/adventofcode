package main

import "strconv"

func filter(ss []string, test func(string) bool) (ret []string) {
	for _, s := range ss {
		if test(s) {
			ret = append(ret, s)
		}
	}
	return
}

func parseArrayToInt(input []string) []int {
	var inputNums []int

	for _, line := range input {
		v, _ := strconv.ParseInt(line, 10, 32)
		inputNums = append(inputNums, int(v))
	}
	return inputNums
}
